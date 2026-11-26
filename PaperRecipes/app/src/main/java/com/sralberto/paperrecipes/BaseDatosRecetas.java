package com.sralberto.paperrecipes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Base de datos local de la app. Guarda cada receta en tres tablas: la receta, sus ingredientes
 * y sus pasos. La primera vez que se crea, carga las recetas de ejemplo de Recetas.EJEMPLOS.
 */
public class BaseDatosRecetas extends SQLiteOpenHelper {

    private static final String NOMBRE_BASE = "paperrecipes.db";
    private static final int VERSION = 1;

    private static final String TABLA_RECETA = "receta";
    private static final String TABLA_INGREDIENTE = "ingrediente";
    private static final String TABLA_PASO = "paso";

    private static final String COLUMNA_ID = "id";
    private static final String COLUMNA_NOMBRE = "nombre";
    private static final String COLUMNA_DESCRIPCION = "descripcion";
    private static final String COLUMNA_PORCIONES = "porciones";
    private static final String COLUMNA_FOTO = "foto";
    private static final String COLUMNA_RECETA_ID = "receta_id";
    private static final String COLUMNA_ORDEN = "orden";
    private static final String COLUMNA_CANTIDAD = "cantidad";
    private static final String COLUMNA_UNIDAD = "unidad";
    private static final String COLUMNA_TEXTO = "texto";

    private static BaseDatosRecetas instancia;

    /** Una sola conexión para toda la app. */
    public static synchronized BaseDatosRecetas obtener(Context contexto) {
        if (instancia == null) {
            instancia = new BaseDatosRecetas(contexto.getApplicationContext());
        }
        return instancia;
    }

    private BaseDatosRecetas(Context contexto) {
        super(contexto, NOMBRE_BASE, null, VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Necesario para que ON DELETE CASCADE borre ingredientes y pasos junto con la receta
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLA_RECETA + " ("
                + COLUMNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMNA_NOMBRE + " TEXT NOT NULL, "
                + COLUMNA_DESCRIPCION + " TEXT NOT NULL, "
                + COLUMNA_PORCIONES + " INTEGER NOT NULL, "
                + COLUMNA_FOTO + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLA_INGREDIENTE + " ("
                + COLUMNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMNA_RECETA_ID + " INTEGER NOT NULL REFERENCES " + TABLA_RECETA
                + "(" + COLUMNA_ID + ") ON DELETE CASCADE, "
                + COLUMNA_ORDEN + " INTEGER NOT NULL, "
                + COLUMNA_NOMBRE + " TEXT NOT NULL, "
                + COLUMNA_CANTIDAD + " REAL NOT NULL, "
                + COLUMNA_UNIDAD + " TEXT NOT NULL)");
        db.execSQL("CREATE TABLE " + TABLA_PASO + " ("
                + COLUMNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMNA_RECETA_ID + " INTEGER NOT NULL REFERENCES " + TABLA_RECETA
                + "(" + COLUMNA_ID + ") ON DELETE CASCADE, "
                + COLUMNA_ORDEN + " INTEGER NOT NULL, "
                + COLUMNA_TEXTO + " TEXT NOT NULL)");

        // Datos de ejemplo. Se insertan al revés para que la primera de la lista quede arriba,
        // porque la lista muestra primero las recetas más nuevas.
        for (int i = Recetas.EJEMPLOS.size() - 1; i >= 0; i--) {
            insertar(db, Recetas.EJEMPLOS.get(i));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_PASO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_INGREDIENTE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_RECETA);
        onCreate(db);
    }

    /** Todas las recetas, de la más nueva a la más antigua. */
    public List<Receta> obtenerRecetas() {
        SQLiteDatabase db = getReadableDatabase();
        List<Long> ids = new ArrayList<>();
        try (Cursor cursor = db.query(TABLA_RECETA, new String[]{COLUMNA_ID},
                null, null, null, null, COLUMNA_ID + " DESC")) {
            while (cursor.moveToNext()) {
                ids.add(cursor.getLong(0));
            }
        }
        List<Receta> recetas = new ArrayList<>();
        for (long id : ids) {
            Receta receta = leer(db, id);
            if (receta != null) {
                recetas.add(receta);
            }
        }
        return recetas;
    }

    /** La receta con ese id, o null si ya no existe. */
    public Receta obtenerReceta(long id) {
        return leer(getReadableDatabase(), id);
    }

    /**
     * Inserta la receta si es nueva o la reemplaza si ya tiene id.
     * Los ingredientes y pasos se vuelven a escribir completos. Devuelve el id de la receta.
     */
    public long guardarReceta(Receta receta) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            long id;
            if (receta.getId() == Receta.SIN_ID) {
                id = insertar(db, receta);
            } else {
                id = receta.getId();
                db.update(TABLA_RECETA, valoresReceta(receta), COLUMNA_ID + " = ?", argumento(id));
                db.delete(TABLA_INGREDIENTE, COLUMNA_RECETA_ID + " = ?", argumento(id));
                db.delete(TABLA_PASO, COLUMNA_RECETA_ID + " = ?", argumento(id));
                insertarDetalle(db, id, receta);
            }
            db.setTransactionSuccessful();
            return id;
        } finally {
            db.endTransaction();
        }
    }

    public void borrarReceta(long id) {
        getWritableDatabase().delete(TABLA_RECETA, COLUMNA_ID + " = ?", argumento(id));
    }

    private static long insertar(SQLiteDatabase db, Receta receta) {
        long id = db.insertOrThrow(TABLA_RECETA, null, valoresReceta(receta));
        insertarDetalle(db, id, receta);
        return id;
    }

    private static void insertarDetalle(SQLiteDatabase db, long id, Receta receta) {
        List<Ingrediente> ingredientes = receta.getIngredientes();
        for (int i = 0; i < ingredientes.size(); i++) {
            Ingrediente ingrediente = ingredientes.get(i);
            ContentValues valores = new ContentValues();
            valores.put(COLUMNA_RECETA_ID, id);
            valores.put(COLUMNA_ORDEN, i);
            valores.put(COLUMNA_NOMBRE, ingrediente.getNombre());
            valores.put(COLUMNA_CANTIDAD, ingrediente.getCantidad());
            valores.put(COLUMNA_UNIDAD, ingrediente.getUnidad());
            db.insertOrThrow(TABLA_INGREDIENTE, null, valores);
        }
        List<String> pasos = receta.getPasos();
        for (int i = 0; i < pasos.size(); i++) {
            ContentValues valores = new ContentValues();
            valores.put(COLUMNA_RECETA_ID, id);
            valores.put(COLUMNA_ORDEN, i);
            valores.put(COLUMNA_TEXTO, pasos.get(i));
            db.insertOrThrow(TABLA_PASO, null, valores);
        }
    }

    private static ContentValues valoresReceta(Receta receta) {
        ContentValues valores = new ContentValues();
        valores.put(COLUMNA_NOMBRE, receta.getNombre());
        valores.put(COLUMNA_DESCRIPCION, receta.getDescripcion());
        valores.put(COLUMNA_PORCIONES, receta.getPorciones());
        valores.put(COLUMNA_FOTO, receta.getFoto());
        return valores;
    }

    private static Receta leer(SQLiteDatabase db, long id) {
        String nombre;
        String descripcion;
        int porciones;
        String foto;
        try (Cursor cursor = db.query(TABLA_RECETA, null, COLUMNA_ID + " = ?", argumento(id),
                null, null, null)) {
            if (!cursor.moveToFirst()) {
                return null;
            }
            nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMNA_NOMBRE));
            descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMNA_DESCRIPCION));
            porciones = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMNA_PORCIONES));
            int columnaFoto = cursor.getColumnIndexOrThrow(COLUMNA_FOTO);
            foto = cursor.isNull(columnaFoto) ? null : cursor.getString(columnaFoto);
        }

        List<Ingrediente> ingredientes = new ArrayList<>();
        try (Cursor cursor = db.query(TABLA_INGREDIENTE, null, COLUMNA_RECETA_ID + " = ?",
                argumento(id), null, null, COLUMNA_ORDEN)) {
            while (cursor.moveToNext()) {
                ingredientes.add(new Ingrediente(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMNA_NOMBRE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMNA_CANTIDAD)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMNA_UNIDAD))));
            }
        }

        List<String> pasos = new ArrayList<>();
        try (Cursor cursor = db.query(TABLA_PASO, new String[]{COLUMNA_TEXTO},
                COLUMNA_RECETA_ID + " = ?", argumento(id), null, null, COLUMNA_ORDEN)) {
            while (cursor.moveToNext()) {
                pasos.add(cursor.getString(0));
            }
        }
        return new Receta(id, nombre, descripcion, porciones, foto, ingredientes, pasos);
    }

    private static String[] argumento(long id) {
        return new String[]{String.valueOf(id)};
    }
}
