// Manda notificaciones con el API HTTP v1 de Firebase Cloud Messaging.
// Firebase pide un token de acceso de Google. Para obtenerlo, el servidor firma un JWT
// con la llave privada de la cuenta de servicio del proyecto.
// El JSON de la cuenta de servicio es un secreto de Cloudflare y nunca entra al repo:
//   npx wrangler secret put FCM_CUENTA_SERVICIO

const URL_TOKEN = "https://oauth2.googleapis.com/token";
const ALCANCE = "https://www.googleapis.com/auth/firebase.messaging";

// El token de acceso dura una hora. Se reutiliza mientras el worker siga en memoria.
let accesoGuardado = null;

// Regresa un resultado por token: "enviada", "token_vencido" o "error"
export async function enviarNotificaciones(env, tokens, mensaje) {
  if (tokens.length === 0) {
    return [];
  }

  let cuenta;
  let acceso;
  try {
    cuenta = JSON.parse(env.FCM_CUENTA_SERVICIO);
    acceso = await obtenerAcceso(cuenta);
  } catch (error) {
    console.error("No se pudo autenticar con Firebase:", error.message);
    return tokens.map(() => "error");
  }

  return Promise.all(tokens.map((token) => enviar(cuenta.project_id, acceso, token, mensaje)));
}

async function enviar(proyecto, acceso, token, mensaje) {
  try {
    const respuesta = await fetch(`https://fcm.googleapis.com/v1/projects/${proyecto}/messages:send`, {
      method: "POST",
      headers: { Authorization: `Bearer ${acceso}`, "Content-Type": "application/json" },
      body: JSON.stringify({ message: { token, ...mensaje } }),
    });
    if (respuesta.ok) {
      return "enviada";
    }
    // 404 UNREGISTERED: el token ya no existe, por ejemplo porque desinstalaron la app
    if (respuesta.status === 404) {
      return "token_vencido";
    }
    console.error("FCM respondió", respuesta.status, await respuesta.text());
    return "error";
  } catch (error) {
    console.error("No se pudo conectar con FCM:", error.message);
    return "error";
  }
}

async function obtenerAcceso(cuenta) {
  const ahora = Math.floor(Date.now() / 1000);
  if (accesoGuardado && accesoGuardado.cuenta === cuenta.client_email && accesoGuardado.venceEn - 60 > ahora) {
    return accesoGuardado.token;
  }

  const respuesta = await fetch(URL_TOKEN, {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: new URLSearchParams({
      grant_type: "urn:ietf:params:oauth:grant-type:jwt-bearer",
      assertion: await firmarJwt(cuenta, ahora),
    }),
  });
  if (!respuesta.ok) {
    throw new Error(`Google rechazó la cuenta de servicio con ${respuesta.status}: ${await respuesta.text()}`);
  }

  const { access_token: token, expires_in: duracion } = await respuesta.json();
  accesoGuardado = { cuenta: cuenta.client_email, token, venceEn: ahora + duracion };
  return token;
}

async function firmarJwt(cuenta, ahora) {
  const encabezado = base64url(JSON.stringify({ alg: "RS256", typ: "JWT" }));
  const datos = base64url(
    JSON.stringify({ iss: cuenta.client_email, scope: ALCANCE, aud: URL_TOKEN, iat: ahora, exp: ahora + 3600 })
  );
  const llave = await crypto.subtle.importKey(
    "pkcs8",
    pemABytes(cuenta.private_key),
    { name: "RSASSA-PKCS1-v1_5", hash: "SHA-256" },
    false,
    ["sign"]
  );
  const firma = await crypto.subtle.sign("RSASSA-PKCS1-v1_5", llave, new TextEncoder().encode(`${encabezado}.${datos}`));
  return `${encabezado}.${datos}.${base64url(firma)}`;
}

function pemABytes(pem) {
  const base64 = pem.replace(/-----[^-]+-----/g, "").replace(/\s+/g, "");
  return Uint8Array.from(atob(base64), (caracter) => caracter.charCodeAt(0));
}

function base64url(valor) {
  const bytes = typeof valor === "string" ? new TextEncoder().encode(valor) : new Uint8Array(valor);
  let binario = "";
  for (const byte of bytes) {
    binario += String.fromCharCode(byte);
  }
  return btoa(binario).replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, "");
}
