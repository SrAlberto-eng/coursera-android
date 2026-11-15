package com.sralberto.petagram;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class BaseDatosMascotas extends SQLiteOpenHelper {

    public static final int MAXIMO_FAVORITAS = 5;

    private static final String NOMBRE_BASE = "petagram.db";
    private static final int VERSION = 1;

    private static final String TABLA = "mascota";
    private static final String COLUMNA_ID = "id";
    private static final String COLUMNA_NOMBRE = "nombre";
    private static final String COLUMNA_FOTO = "foto";
    private static final String COLUMNA_RAITING = "raiting";

    private final Context contexto;

    public BaseDatosMascotas(Context contexto) {
        super(contexto, NOMBRE_BASE, null, VERSION);
        this.contexto = contexto.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLA + " ("
                + COLUMNA_ID + " INTEGER NOT NULL UNIQUE, "
                + COLUMNA_NOMBRE + " TEXT NOT NULL, "
                + COLUMNA_FOTO + " TEXT NOT NULL, "
                + COLUMNA_RAITING + " INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLA);
        onCreate(db);
    }

    public void guardarHueso(Mascota mascota) {
        SQLiteDatabase db = getWritableDatabase();

        // SQLite le da a cada fila un rowid que crece con cada insert.
        // Al borrar y volver a insertar la mascota, queda como la más reciente.
        db.delete(TABLA, COLUMNA_ID + " = ?", new String[]{String.valueOf(mascota.getId())});
        db.insert(TABLA, null, crearValores(mascota));

        db.execSQL("DELETE FROM " + TABLA + " WHERE rowid NOT IN ("
                + "SELECT rowid FROM " + TABLA + " ORDER BY rowid DESC LIMIT " + MAXIMO_FAVORITAS + ")");
    }

    public ArrayList<Mascota> obtenerFavoritas() {
        ArrayList<Mascota> favoritas = new ArrayList<>();
        String consulta = "SELECT * FROM " + TABLA + " ORDER BY rowid DESC";

        try (Cursor cursor = getReadableDatabase().rawQuery(consulta, null)) {
            while (cursor.moveToNext()) {
                favoritas.add(leerMascota(cursor));
            }
        }
        return favoritas;
    }

    // La foto se guarda por nombre, como "mascota_catty", porque el número de R.drawable cambia al compilar
    private ContentValues crearValores(Mascota mascota) {
        ContentValues valores = new ContentValues();
        valores.put(COLUMNA_ID, mascota.getId());
        valores.put(COLUMNA_NOMBRE, mascota.getNombre());
        valores.put(COLUMNA_FOTO, contexto.getResources().getResourceEntryName(mascota.getFoto()));
        valores.put(COLUMNA_RAITING, mascota.getRaiting());
        return valores;
    }

    private Mascota leerMascota(Cursor cursor) {
        String nombreFoto = cursor.getString(cursor.getColumnIndexOrThrow(COLUMNA_FOTO));
        int foto = contexto.getResources().getIdentifier(nombreFoto, "drawable", contexto.getPackageName());

        return new Mascota(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMNA_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMNA_NOMBRE)),
                foto,
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMNA_RAITING)));
    }
}
