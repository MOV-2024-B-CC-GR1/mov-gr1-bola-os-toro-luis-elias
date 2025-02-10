package com.example.deber.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "futbol.db"
        private const val DATABASE_VERSION = 2 // Incrementamos la versión
        private const val TABLE_JUGADORES = "jugadores"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_JUGADORES (
                id INTEGER PRIMARY KEY AUTOINCREMENT, 
                nombre TEXT, 
                posicion TEXT, 
                numero INTEGER,
                latitud REAL,   
                longitud REAL   
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_JUGADORES ADD COLUMN latitud REAL")
            db.execSQL("ALTER TABLE $TABLE_JUGADORES ADD COLUMN longitud REAL")
        }
    }

    fun agregarJugador(nombre: String, posicion: String, numero: Int, latitud: Double?, longitud: Double?): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("posicion", posicion)
            put("numero", numero)
            put("latitud", latitud)
            put("longitud", longitud)
        }
        val id = db.insert(TABLE_JUGADORES, null, values)
        db.close()
        return id
    }

    fun obtenerJugadores(): List<Jugador> {
        val jugadores = mutableListOf<Jugador>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_JUGADORES", null)

        while (cursor.moveToNext()) {
            val jugador = Jugador(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getDouble(4),
                cursor.getDouble(5),
            )
            jugadores.add(jugador)
        }
        cursor.close()
        db.close()
        return jugadores
    }

    fun obtenerJugadorPorId(id: Int): Jugador? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_JUGADORES WHERE id=?", arrayOf(id.toString()))
        return if (cursor.moveToFirst()) {
            val jugador = Jugador(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getDouble(4),
                cursor.getDouble(5)
            )
            cursor.close()
            jugador
        } else {
            cursor.close()
            null
        }
    }

    fun actualizarJugador(id: Int, nombre: String, posicion: String, numero: Int, latitud: Double?, longitud: Double?) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("posicion", posicion)
            put("numero", numero)
            put("latitud", latitud)
            put("longitud", longitud)
        }
        db.update(TABLE_JUGADORES, values, "id=?", arrayOf(id.toString()))
        db.close()
    }

    fun eliminarJugador(id: Int) {
        val db = writableDatabase
        db.delete(TABLE_JUGADORES, "id=?", arrayOf(id.toString()))
        db.close()
    }
}