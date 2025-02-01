package com.example.deber

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.deber.data.DatabaseHelper
import com.example.deber.data.Jugador
import com.example.deber.R

class PlayerDetailActivity : Activity() {

    private lateinit var db: DatabaseHelper
    private lateinit var editNombre: EditText
    private lateinit var editPosicion: EditText
    private lateinit var editNumero: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnEliminar: Button
    private var jugadorId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_detail)

        db = DatabaseHelper(this)
        editNombre = findViewById(R.id.editNombre)
        editPosicion = findViewById(R.id.editPosicion)
        editNumero = findViewById(R.id.editNumero)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnEliminar = findViewById(R.id.btnEliminar)

        jugadorId = intent.getIntExtra("id", -1).takeIf { it != -1 }

        jugadorId?.let {
            val jugador = db.obtenerJugadorPorId(it)
            jugador?.let {
                editNombre.setText(jugador.nombre)
                editPosicion.setText(jugador.posicion)
                editNumero.setText(jugador.numero.toString())
            }
        }

        btnGuardar.setOnClickListener {
            val nombre = editNombre.text.toString()
            val posicion = editPosicion.text.toString()
            val numero = editNumero.text.toString().toIntOrNull() ?: 0

            if (jugadorId == null) {
                db.agregarJugador(nombre, posicion, numero)
            } else {
                db.actualizarJugador(jugadorId!!, nombre, posicion, numero)
            }
            finish()
        }

        btnEliminar.setOnClickListener {
            jugadorId?.let {
                db.eliminarJugador(it)
                finish()
            }
        }
    }
}
