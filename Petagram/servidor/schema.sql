-- Tabla del modelo de la práctica: usuario_instagram(id_dispositivo, id_usuario_instagram)
CREATE TABLE IF NOT EXISTS usuario_instagram (
    id INTEGER PRIMARY KEY,
    id_dispositivo TEXT NOT NULL UNIQUE,
    id_usuario_instagram TEXT NOT NULL,
    creado TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);
