"""Genera la API simulada de Petagram con el formato del API v1 de Instagram.

Uso: python3 generar_api.py <carpeta de drawables> <carpeta de salida>
Necesita rsvg-convert para pasar las ilustraciones a PNG.

Salida:
  v1/users/self/follows          usuarios del sandbox
  v1/users/search                búsqueda de usuarios
  v1/users/<id>/media/recent     fotos recientes de cada usuario
  media/*.png                    fotos y fotos de perfil
"""
import json
import re
import subprocess
import sys
import xml.etree.ElementTree as ET
from datetime import datetime, timedelta, timezone
from pathlib import Path

DRAWABLES = Path(sys.argv[1])
OUT = Path(sys.argv[2])
URL_MEDIA = "https://raw.githubusercontent.com/SrAlberto-eng/coursera-android/main/Petagram/api/media/"
NS = "{http://schemas.android.com/apk/res/android}"


def leer_vector(nombre):
    """Convierte un VectorDrawable sencillo en una lista de elementos <path> de SVG."""
    raiz = ET.parse(DRAWABLES / f"{nombre}.xml").getroot()
    caminos = []
    for p in raiz.iter("path"):
        a = {k.replace(NS, ""): v for k, v in p.attrib.items()}
        attrs = [f'd="{a["pathData"]}"']
        attrs.append(f'fill="{a["fillColor"]}"' if "fillColor" in a else 'fill="none"')
        if "strokeColor" in a:
            attrs.append(f'stroke="{a["strokeColor"]}" stroke-width="{a.get("strokeWidth", "1")}"')
            attrs.append(f'stroke-linecap="{a.get("strokeLineCap", "butt")}"')
            attrs.append(f'stroke-linejoin="{a.get("strokeLineJoin", "miter")}"')
        if a.get("fillType") == "evenOdd":
            attrs.append('fill-rule="evenodd"')
        caminos.append(f"<path {' '.join(attrs)}/>")
    return caminos


HUELLA = leer_vector("ic_huella")
HUESO = leer_vector("ic_hueso_dorado")


def decoracion_huellas(color):
    piezas = []
    for i, (x, y, r) in enumerate([(14, 18, -20), (150, 12, 15), (170, 150, -10), (8, 140, 25), (120, 172, 5), (70, 6, -5)]):
        huella = "".join(HUELLA).replace("#3C2F33", color)
        piezas.append(f'<g transform="translate({x} {y}) rotate({r} 12 12) scale(1.1)" opacity="0.35">{huella}</g>')
    return "".join(piezas)


def decoracion_rayas(color):
    return "".join(
        f'<rect x="{x}" y="-60" width="14" height="320" fill="{color}" opacity="0.18" transform="rotate(35 100 100)"/>'
        for x in range(-80, 300, 36))


def decoracion_confeti():
    colores = ["#E83B35", "#DCBE68", "#1B8D93", "#3C2F33", "#FFFFFF"]
    puntos = [(18, 22), (176, 30), (34, 170), (164, 176), (100, 14), (186, 100), (12, 96), (60, 186), (140, 8), (150, 190)]
    return "".join(
        f'<circle cx="{x}" cy="{y}" r="{3 + (i % 3)}" fill="{colores[i % len(colores)]}" opacity="0.8"/>'
        for i, (x, y) in enumerate(puntos))


def decoracion_hueso():
    return f'<g transform="translate(138 136) rotate(-15 24 24) scale(2.4)">{"".join(HUESO)}</g>'


# Cada foto: color de fondo, decoración, rotación, escala y desplazamiento de la mascota
VARIANTES = [
    (None, "", 0, 1.0, (0, 0)),
    ("#F6D8AE", "huellas", 0, 0.88, (0, 6)),
    ("#BDE3C3", "", 0, 1.4, (0, -14)),
    ("#F7C6C7", "hueso", -10, 0.9, (-8, -6)),
    ("#C9D7F8", "rayas", 8, 0.86, (0, 4)),
    ("#FFF1B8", "confeti", 0, 1.0, (0, 0)),
]


def svg_foto(mascota, variante):
    caminos = leer_vector(mascota)
    fondo_original = re.search(r'fill="(#[0-9A-Fa-f]+)"', caminos[0]).group(1)
    figura = "".join(caminos[1:])
    color, deco, rot, esc, (dx, dy) = variante
    fondo = color or fondo_original
    capas = [f'<rect width="200" height="200" fill="{fondo}"/>']
    if deco == "huellas":
        capas.append(decoracion_huellas("#3C2F33"))
    elif deco == "rayas":
        capas.append(decoracion_rayas("#FFFFFF"))
    elif deco == "confeti":
        capas.append(decoracion_confeti())
    capas.append(
        f'<g transform="translate({100 + dx} {100 + dy}) rotate({rot}) scale({esc}) translate(-100 -100)">{figura}</g>')
    if deco == "hueso":
        capas.append(decoracion_hueso())
    return ('<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 200 200" width="200" height="200">'
            + "".join(capas) + "</svg>")


def svg_perfil(mascota):
    return ('<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 200 200" width="200" height="200">'
            + "".join(leer_vector(mascota)) + "</svg>")


def renderizar(svg, destino, tamano):
    destino.parent.mkdir(parents=True, exist_ok=True)
    subprocess.run(["rsvg-convert", "-w", str(tamano), "-h", str(tamano), "-o", str(destino)],
                   input=svg.encode(), check=True)


TIJUANA = timezone(timedelta(hours=-8))


def segundos(fecha):
    """Fecha en hora de Tijuana a segundos Unix, como el created_time de Instagram."""
    return str(int(datetime.strptime(fecha, "%Y-%m-%d %H:%M").replace(tzinfo=TIJUANA).timestamp()))


MASCOTAS = [
    {"id": "1001", "username": "catty", "full_name": "Catty", "drawable": "mascota_catty",
     "bio": "Perrita de departamento. Duermo 16 horas al día.",
     "fotos": [("2026-11-20 18:10", 12, "Domingo de no hacer nada"),
               ("2026-11-17 09:25", 7, "Encontré la cobija perfecta"),
               ("2026-11-13 21:40", 3, "Vigilando la ventana"),
               ("2026-11-09 15:05", 9, "Siesta número tres del día"),
               ("2026-11-05 11:30", 4, "Día de baño, no me gustó"),
               ("2026-11-01 19:50", 6, "Primer post en Petagram")]},
    {"id": "1002", "username": "ronny", "full_name": "Ronny", "drawable": "mascota_ronny",
     "bio": "Perro feliz de Tijuana. Me gustan los huesos y el parque.",
     "fotos": [("2026-11-21 08:15", 5, "Listo para el paseo de la mañana"),
               ("2026-11-18 17:45", 0, "Esperando a que lleguen de la escuela"),
               ("2026-11-15 12:20", 3, "Día de parque en la Morelos"),
               ("2026-11-11 10:05", 10, "Me gané un hueso nuevo"),
               ("2026-11-07 19:30", 2, "Cansado después de correr"),
               ("2026-11-03 16:40", 3, "Foto de perfil nueva")]},
    {"id": "1003", "username": "luna", "full_name": "Luna", "drawable": "mascota_luna",
     "bio": "Gata curiosa. Paso el día viendo por la ventana.",
     "fotos": [("2026-11-21 13:05", 8, "Hora de la comida"),
               ("2026-11-16 08:50", 5, "Explorando el jardín"),
               ("2026-11-12 18:15", 11, "Orejas en modo antena"),
               ("2026-11-08 14:00", 2, "Escondida detrás del sillón"),
               ("2026-11-04 09:35", 6, "Desayuno con vista"),
               ("2026-11-02 20:10", 1, "Hola, Petagram")]},
    {"id": "1004", "username": "max", "full_name": "Max", "drawable": "mascota_max",
     "bio": "Perro guardián en entrenamiento.",
     "fotos": [("2026-11-20 07:30", 4, "Cuidando la casa desde temprano"),
               ("2026-11-17 20:20", 9, "Mi pelota favorita"),
               ("2026-11-14 16:55", 1, "No fui yo, fue el gato"),
               ("2026-11-10 11:10", 13, "Baño de sol"),
               ("2026-11-06 18:45", 5, "Aprendí a dar la pata"),
               ("2026-11-02 08:05", 2, "Primer día en Petagram")]},
    {"id": "1005", "username": "kira", "full_name": "Kira", "drawable": "mascota_kira",
     "bio": "Gatita juguetona. Persigo todo lo que se mueve.",
     "fotos": [("2026-11-19 22:00", 15, "Cazadora nocturna"),
               ("2026-11-16 15:30", 6, "El estambre perdió"),
               ("2026-11-13 10:45", 2, "Lunes de pereza"),
               ("2026-11-09 19:15", 8, "Pose de modelo"),
               ("2026-11-05 13:25", 3, "Probando mi cama nueva"),
               ("2026-11-01 10:00", 7, "Aquí empieza mi cuenta")]},
    {"id": "1006", "username": "toby", "full_name": "Toby", "drawable": "mascota_toby",
     "bio": "Perro atleta. Salgo a correr todas las tardes.",
     "fotos": [("2026-11-21 19:40", 3, "Carrera en la playa"),
               ("2026-11-18 11:55", 10, "Me robé un calcetín"),
               ("2026-11-14 08:20", 4, "Nuevo récord en el parque"),
               ("2026-11-10 21:35", 1, "Acomodando mi cama"),
               ("2026-11-06 07:45", 7, "Buenos días desde el patio"),
               ("2026-11-03 12:30", 2, "Mi primera foto")]},
]


def usuario(m):
    return {"id": m["id"], "username": m["username"], "full_name": m["full_name"],
            "profile_picture": f"{URL_MEDIA}{m['username']}_perfil.png", "bio": m["bio"],
            "website": "", "is_business": False}


def usuario_corto(m):
    return {"id": m["id"], "full_name": m["full_name"],
            "profile_picture": f"{URL_MEDIA}{m['username']}_perfil.png", "username": m["username"]}


def media(m, indice, fecha, likes, texto):
    creada = segundos(fecha)
    nombre = f"{m['username']}_{indice + 1}"
    return {
        "id": f"{creada}{indice:03d}_{m['id']}",
        "user": usuario_corto(m),
        "images": {
            "thumbnail": {"width": 150, "height": 150, "url": f"{URL_MEDIA}{nombre}_150.png"},
            "low_resolution": {"width": 320, "height": 320, "url": f"{URL_MEDIA}{nombre}_320.png"},
            "standard_resolution": {"width": 640, "height": 640, "url": f"{URL_MEDIA}{nombre}.png"},
        },
        "created_time": creada,
        "caption": {"id": f"{creada}{indice:03d}9", "text": texto, "created_time": creada,
                    "from": usuario_corto(m)},
        "user_has_liked": False,
        "likes": {"count": likes},
        "tags": [],
        "filter": "Normal",
        "comments": {"count": 0},
        "type": "image",
        "location": None,
        "users_in_photo": [],
    }


def escribir_json(ruta, datos):
    ruta.parent.mkdir(parents=True, exist_ok=True)
    ruta.write_text(json.dumps(datos, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def main():
    lista_usuarios = {"pagination": {}, "meta": {"code": 200}, "data": [usuario(m) for m in MASCOTAS]}
    escribir_json(OUT / "v1/users/self/follows", lista_usuarios)
    escribir_json(OUT / "v1/users/search", {"meta": {"code": 200}, "data": [usuario(m) for m in MASCOTAS]})

    for m in MASCOTAS:
        datos = [media(m, i, *foto) for i, foto in enumerate(m["fotos"])]
        escribir_json(OUT / f"v1/users/{m['id']}/media/recent", {"pagination": {}, "meta": {"code": 200}, "data": datos})

        renderizar(svg_perfil(m["drawable"]), OUT / f"media/{m['username']}_perfil.png", 150)
        for i, variante in enumerate(VARIANTES):
            svg = svg_foto(m["drawable"], variante)
            base = OUT / f"media/{m['username']}_{i + 1}"
            renderizar(svg, base.with_suffix(".png"), 640)
            renderizar(svg, Path(f"{base}_320.png"), 320)
            renderizar(svg, Path(f"{base}_150.png"), 150)


if __name__ == "__main__":
    main()
