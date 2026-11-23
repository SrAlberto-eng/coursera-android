// Web service de Petagram en Cloudflare Workers.
//
// /v1/...             API simulada de Instagram, con las mismas rutas del API v1
// /registrar-usuario  guarda el token de FCM de un dispositivo con su cuenta de Instagram
// /registrar-like     guarda un like y avisa al dueño de la foto con una notificación

import { atenderInstagram } from "./instagram.js";
import { registrarLike, registrarUsuario } from "./petagram.js";
import { responder } from "./util.js";

const ENDPOINTS = {
  "/registrar-usuario": registrarUsuario,
  "/registrar-like": registrarLike,
};

export default {
  async fetch(request, env) {
    try {
      const { pathname } = new URL(request.url);

      if (pathname.startsWith("/v1/")) {
        return await atenderInstagram(request, env, pathname.slice("/v1/".length).replace(/\/$/, ""));
      }

      const endpoint = ENDPOINTS[pathname];
      if (endpoint) {
        if (request.method !== "POST") {
          return responder({ error: "Este endpoint solo acepta POST" }, 405, { Allow: "POST" });
        }
        return await endpoint(request, env);
      }

      if (pathname === "/") {
        return responder({
          servicio: "Petagram",
          endpoints: ["POST /registrar-usuario", "POST /registrar-like"],
          instagram: "/v1/",
        });
      }

      return responder({ error: "No existe esa ruta" }, 404);
    } catch (error) {
      console.error(error);
      return responder({ error: "Error interno del servidor" }, 500);
    }
  },
};
