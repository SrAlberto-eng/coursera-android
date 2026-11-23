// Endpoints propios de Petagram.

import { enviarNotificaciones } from "./fcm.js";
import { buscarUsuarioSandbox } from "./instagram.js";
import { leerParametros, responder, texto } from "./util.js";

// Guarda el token de FCM de un dispositivo junto con su cuenta de Instagram
export async function registrarUsuario(request, env) {
  let parametros;
  try {
    parametros = await leerParametros(request);
  } catch {
    return responder({ error: "El cuerpo de la petición no se pudo leer" }, 400);
  }

  const idDispositivo = texto(parametros.id_dispositivo);
  const idUsuarioInstagram = texto(parametros.id_usuario_instagram);

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

// Guarda un like y avisa a los dispositivos registrados con la cuenta dueña de la foto.
// id_usuario_instagram es el dueño de la foto. id_dispositivo es el celular que dio el like.
export async function registrarLike(request, env) {
  let parametros;
  try {
    parametros = await leerParametros(request);
  } catch {
    return responder({ error: "El cuerpo de la petición no se pudo leer" }, 400);
  }

  const idFoto = texto(parametros.id_foto_instagram);
  const idUsuarioInstagram = texto(parametros.id_usuario_instagram);
  const idDispositivo = texto(parametros.id_dispositivo);

  if (!idFoto || !idUsuarioInstagram || !idDispositivo) {
    return responder({ error: "Faltan id_foto_instagram, id_usuario_instagram o id_dispositivo" }, 400);
  }
  if (idFoto.length > 64 || idUsuarioInstagram.length > 64 || idDispositivo.length > 4096) {
    return responder({ error: "Algún parámetro es demasiado largo" }, 400);
  }
  // El id de una foto de Instagram termina con el id de su dueño, por ejemplo 1795277700000_1002
  if (idFoto.match(/^\d+_(\d+)$/)?.[1] !== idUsuarioInstagram) {
    return responder({ error: "La foto no pertenece a id_usuario_instagram" }, 400);
  }
  if (!env.FCM_CUENTA_SERVICIO) {
    return responder({ error: "Falta configurar el secreto FCM_CUENTA_SERVICIO" }, 500);
  }

  const like = await env.DB.prepare(
    `INSERT INTO like_foto (id_foto_instagram, id_usuario_instagram, id_dispositivo)
     VALUES (?1, ?2, ?3)
     RETURNING id, id_foto_instagram, id_usuario_instagram, id_dispositivo`
  )
    .bind(idFoto, idUsuarioInstagram, idDispositivo)
    .first();

  const { results } = await env.DB.prepare(
    "SELECT id_dispositivo FROM usuario_instagram WHERE id_usuario_instagram = ?1"
  )
    .bind(idUsuarioInstagram)
    .all();
  const destinos = results.map((fila) => fila.id_dispositivo);

  // La cuenta que dio el like es la que se registró con el celular que lo mandó
  const registro = await env.DB.prepare(
    "SELECT id_usuario_instagram FROM usuario_instagram WHERE id_dispositivo = ?1"
  )
    .bind(idDispositivo)
    .first();
  const quienDioLike = registro ? await buscarUsuarioSandbox(registro.id_usuario_instagram) : null;

  // Mensaje solo de datos. Así la app siempre arma la notificación, aunque esté en segundo plano,
  // y le puede poner las acciones de Ver mi perfil, Follow y Ver usuario.
  const datos = { tipo: "like", pantalla: "perfil", id_foto_instagram: idFoto, id_usuario_instagram: idUsuarioInstagram };
  if (quienDioLike) {
    datos.id_usuario_like = quienDioLike.id;
    datos.username_like = quienDioLike.username;
  }
  const resultados = await enviarNotificaciones(env, destinos, { data: datos, android: { priority: "high" } });

  // Los tokens que Firebase ya no reconoce se borran para no volver a intentarlo
  const vencidos = destinos.filter((_, i) => resultados[i] === "token_vencido");
  if (vencidos.length > 0) {
    const borrar = env.DB.prepare("DELETE FROM usuario_instagram WHERE id_dispositivo = ?1");
    await env.DB.batch(vencidos.map((token) => borrar.bind(token)));
  }

  return responder({
    ...like,
    id_usuario_like: quienDioLike?.id ?? null,
    dispositivos: destinos.length,
    notificaciones_enviadas: resultados.filter((resultado) => resultado === "enviada").length,
  });
}
