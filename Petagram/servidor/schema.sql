-- Se puede volver a correr sin borrar datos, porque solo crea lo que falta.

-- Dispositivo registrado con su cuenta de Instagram: usuario_instagram(id_dispositivo, id_usuario_instagram)
CREATE TABLE IF NOT EXISTS usuario_instagram (
    id INTEGER PRIMARY KEY,
    id_dispositivo TEXT NOT NULL UNIQUE,
    id_usuario_instagram TEXT NOT NULL,
    creado TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS usuario_instagram_por_usuario ON usuario_instagram (id_usuario_instagram);

-- Registro propio de likes. id_usuario_instagram es el dueño de la foto
-- e id_dispositivo es el celular que dio el like.
CREATE TABLE IF NOT EXISTS like_foto (
    id INTEGER PRIMARY KEY,
    id_foto_instagram TEXT NOT NULL,
    id_usuario_instagram TEXT NOT NULL,
    id_dispositivo TEXT NOT NULL,
    creado TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Likes de la API simulada de Instagram. Se suman al conteo que traen los JSON del repo.
CREATE TABLE IF NOT EXISTS instagram_like (
    id INTEGER PRIMARY KEY,
    id_foto TEXT NOT NULL,
    creado TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS instagram_like_por_foto ON instagram_like (id_foto);

-- Quién sigue a quién en la API simulada de Instagram
CREATE TABLE IF NOT EXISTS instagram_relacion (
    id_usuario TEXT NOT NULL,
    id_seguido TEXT NOT NULL,
    creado TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_usuario, id_seguido)
);
