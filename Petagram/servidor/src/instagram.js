// API simulada de Instagram v1. El API real ya no existe.
// Las cuentas y las fotos salen de los JSON de la carpeta Petagram/api del repo.
// Los likes que se dan desde la app se guardan en D1 y se suman al conteo de cada foto.

import { leerParametros, responder, texto } from "./util.js";

const URL_DATOS = "https://raw.githubusercontent.com/SrAlberto-eng/coursera-android/main/Petagram/api/v1/";

const RUTAS_FIJAS = new Set(["users/self/follows", "users/search"]);
const RUTA_MEDIA_RECIENTE = /^users\/(\d+)\/media\/recent$/;
const RUTA_RELACION = /^users\/(\d+)\/relationship$/;
// Instagram arma el id de una foto con un número y el id de su dueño, por ejemplo 1795277700000_1002
const RUTA_LIKES = /^media\/(\d+_(\d+))\/likes$/;
// En el API real cada access_token pertenece a una cuenta. Follow y unfollow necesitan saber cuál,
// así que en la copia simulada el token termina con el id de la cuenta, por ejemplo petagram.sandbox.1001
const TOKEN_DE_CUENTA = /^petagram\.sandbox\.(\d+)$/;

export async function atenderInstagram(request, env, ruta) {
  let parametros;
  try {
    parametros = request.method === "POST" ? await leerParametros(request) : {};
  } catch {
    return errorInstagram("APIInvalidParametersError", "invalid request body", 400);
  }

  // Instagram rechazaba cualquier petición sin access_token
  const accessToken = texto(parametros.access_token ?? new URL(request.url).searchParams.get("access_token"));
  if (!accessToken) {
    return errorInstagram("OAuthParameterException", "Missing client_id or access_token URL parameter.", 400);
  }

  const relacion = ruta.match(RUTA_RELACION);
  if (relacion && (request.method === "GET" || request.method === "POST")) {
    return atenderRelacion(env, request.method, relacion[1], accessToken, texto(parametros.action));
  }

  if (request.method === "GET") {
    if (RUTAS_FIJAS.has(ruta)) {
      const datos = await leerDatos(ruta);
      return datos ? responder(datos) : errorDatos();
    }
    const media = ruta.match(RUTA_MEDIA_RECIENTE);
    if (media) {
      return mediaReciente(env, ruta);
    }
  }

  if (request.method === "POST") {
    const like = ruta.match(RUTA_LIKES);
    if (like) {
      return darLike(env, like[1], like[2]);
    }
  }

  return errorInstagram("APINotFoundError", "this endpoint does not exist", 404);
}

// GET users/{user-id}/media/recent con los likes de D1 sumados
async function mediaReciente(env, ruta) {
  const datos = await leerDatos(ruta);
  if (!datos) {
    return errorInstagram("APINotFoundError", "this user does not exist", 400);
  }

  const ids = datos.data.map((foto) => foto.id);
  if (ids.length > 0) {
    const marcadores = ids.map((_, i) => `?${i + 1}`).join(", ");
    const { results } = await env.DB.prepare(
      `SELECT id_foto, COUNT(*) AS total FROM instagram_like
       WHERE id_foto IN (${marcadores}) GROUP BY id_foto`
    )
      .bind(...ids)
      .all();
    const nuevos = new Map(results.map((fila) => [fila.id_foto, fila.total]));
    for (const foto of datos.data) {
      foto.likes.count += nuevos.get(foto.id) ?? 0;
    }
  }
  return responder(datos);
}

// POST media/{media-id}/likes. Instagram respondía con data en null.
async function darLike(env, idFoto, idDueno) {
  const datos = await leerDatos(`users/${idDueno}/media/recent`);
  if (!datos || !datos.data.some((foto) => foto.id === idFoto)) {
    return errorInstagram("APINotFoundError", "invalid media id", 400);
  }
  await env.DB.prepare("INSERT INTO instagram_like (id_foto) VALUES (?1)").bind(idFoto).run();
  return responder({ meta: { code: 200 }, data: null });
}

// GET y POST users/{user-id}/relationship. POST recibe action=follow o action=unfollow.
async function atenderRelacion(env, metodo, idOtro, accessToken, accion) {
  const idPropio = accessToken.match(TOKEN_DE_CUENTA)?.[1];
  if (!idPropio) {
    return errorInstagram("OAuthAccessTokenException", "The access_token provided is invalid.", 400);
  }
  const usuarios = await leerDatos("users/self/follows");
  if (!usuarios) {
    return errorDatos();
  }
  const ids = new Set(usuarios.data.map((usuario) => usuario.id));
  if (!ids.has(idPropio) || !ids.has(idOtro)) {
    return errorInstagram("APINotFoundError", "this user does not exist", 400);
  }

  if (metodo === "POST") {
    if (idPropio === idOtro) {
      return errorInstagram("APIInvalidParametersError", "you cannot follow yourself", 400);
    }
    if (accion === "follow") {
      await env.DB.prepare("INSERT OR IGNORE INTO instagram_relacion (id_usuario, id_seguido) VALUES (?1, ?2)")
        .bind(idPropio, idOtro)
        .run();
    } else if (accion === "unfollow") {
      await env.DB.prepare("DELETE FROM instagram_relacion WHERE id_usuario = ?1 AND id_seguido = ?2")
        .bind(idPropio, idOtro)
        .run();
    } else {
      return errorInstagram("APIInvalidParametersError", "action must be follow or unfollow", 400);
    }
  }

  const { results } = await env.DB.prepare(
    `SELECT id_usuario FROM instagram_relacion
     WHERE (id_usuario = ?1 AND id_seguido = ?2) OR (id_usuario = ?2 AND id_seguido = ?1)`
  )
    .bind(idPropio, idOtro)
    .all();
  const loSigue = results.some((fila) => fila.id_usuario === idPropio);
  const meSigue = results.some((fila) => fila.id_usuario === idOtro);

  const data = { outgoing_status: loSigue ? "follows" : "none", target_user_is_private: false };
  if (metodo === "GET") {
    data.incoming_status = meSigue ? "followed_by" : "none";
  }
  return responder({ meta: { code: 200 }, data });
}

// Cuenta del Sandbox con ese id. Regresa null si no existe o si no se pudieron leer los datos.
export async function buscarUsuarioSandbox(id) {
  const usuarios = await leerDatos("users/self/follows");
  return usuarios?.data.find((usuario) => usuario.id === id) ?? null;
}

// GitHub manda los JSON como texto. Cloudflare los guarda 5 minutos para no pedirlos en cada visita.
async function leerDatos(ruta) {
  const respuesta = await fetch(URL_DATOS + ruta, { cf: { cacheTtl: 300, cacheEverything: true } });
  if (!respuesta.ok) {
    return null;
  }
  return await respuesta.json();
}

function errorDatos() {
  return errorInstagram("APIError", "the sandbox data could not be read", 502);
}

// Mismo formato de error que usaba Instagram
function errorInstagram(tipo, mensaje, estado) {
  return responder({ meta: { error_type: tipo, code: estado, error_message: mensaje } }, estado);
}
