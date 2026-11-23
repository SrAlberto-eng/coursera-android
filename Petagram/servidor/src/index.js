// Web service de Petagram en Cloudflare Workers.
// Guarda en D1 el token de FCM de cada dispositivo junto con el id de usuario de Instagram.

export default {
  async fetch(request, env) {
    const { pathname } = new URL(request.url);

    if (pathname === "/registrar-usuario") {
      if (request.method !== "POST") {
        return responder({ error: "Este endpoint solo acepta POST" }, 405, { Allow: "POST" });
      }
      return registrarUsuario(request, env);
    }

    if (pathname === "/") {
      return responder({ servicio: "Petagram", endpoints: ["POST /registrar-usuario"] });
    }

    return responder({ error: "No existe esa ruta" }, 404);
  },
};

async function registrarUsuario(request, env) {
  let parametros;
  try {
    parametros = await leerParametros(request);
  } catch {
    return responder({ error: "El cuerpo de la petición no se pudo leer" }, 400);
  }

  const idDispositivo = String(parametros.id_dispositivo ?? "").trim();
  const idUsuarioInstagram = String(parametros.id_usuario_instagram ?? "").trim();

  if (!idDispositivo || !idUsuarioInstagram) {
    return responder({ error: "Faltan id_dispositivo o id_usuario_instagram" }, 400);
  }
  if (idDispositivo.length > 4096 || idUsuarioInstagram.length > 64) {
    return responder({ error: "Algún parámetro es demasiado largo" }, 400);
  }

  // Un dispositivo queda una sola vez. Si se registra otra vez, se actualiza su usuario.
  const registro = await env.DB.prepare(
    `INSERT INTO usuario_instagram (id_dispositivo, id_usuario_instagram)
     VALUES (?1, ?2)
     ON CONFLICT (id_dispositivo) DO UPDATE SET
       id_usuario_instagram = excluded.id_usuario_instagram,
       actualizado = CURRENT_TIMESTAMP
     RETURNING id, id_dispositivo, id_usuario_instagram`
  )
    .bind(idDispositivo, idUsuarioInstagram)
    .first();

  return responder(registro);
}

// Acepta formulario (lo que manda Retrofit con @FormUrlEncoded) o JSON
async function leerParametros(request) {
  const tipo = request.headers.get("Content-Type") ?? "";
  if (tipo.includes("application/json")) {
    return await request.json();
  }
  return Object.fromEntries(new URLSearchParams(await request.text()));
}

function responder(cuerpo, estado = 200, encabezados = {}) {
  return new Response(JSON.stringify(cuerpo), {
    status: estado,
    headers: { "Content-Type": "application/json; charset=utf-8", ...encabezados },
  });
}
