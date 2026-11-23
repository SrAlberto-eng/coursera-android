// Funciones que usan las dos partes del servidor.

export function responder(cuerpo, estado = 200, encabezados = {}) {
  return new Response(JSON.stringify(cuerpo), {
    status: estado,
    headers: { "Content-Type": "application/json; charset=utf-8", ...encabezados },
  });
}

// Acepta formulario, que es lo que manda Retrofit con @FormUrlEncoded, o JSON
export async function leerParametros(request) {
  const tipo = request.headers.get("Content-Type") ?? "";
  if (tipo.includes("application/json")) {
    return await request.json();
  }
  return Object.fromEntries(new URLSearchParams(await request.text()));
}

export function texto(valor) {
  return String(valor ?? "").trim();
}
