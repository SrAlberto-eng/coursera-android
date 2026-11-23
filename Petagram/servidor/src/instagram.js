// API simulada de Instagram v1. El API real ya no existe.
// Las cuentas y las fotos salen de los JSON de la carpeta Petagram/api del repo.
// Los likes que se dan desde la app se guardan en D1 y se suman al conteo de cada foto.

import { leerParametros, responder, texto } from "./util.js";

const URL_DATOS = "https://raw.githubusercontent.com/SrAlberto-eng/coursera-android/main/Petagram/api/v1/";

const RUTAS_FIJAS = new Set(["users/self/follows", "users/search"]);
const RUTA_MEDIA_RECIENTE = /^users\/(\d+)\/media\/recent$/;
// Instagram arma el id de una foto con un número y el id de su dueño, por ejemplo 1795277700000_1002
const RUTA_LIKES = /^media\/(\d+_(\d+))\/likes$/;

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
