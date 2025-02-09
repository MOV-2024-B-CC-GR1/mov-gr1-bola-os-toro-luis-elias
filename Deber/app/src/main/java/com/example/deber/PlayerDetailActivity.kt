package com.example.deber

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.deber.data.DatabaseHelper
import com.example.deber.data.Jugador

class PlayerDetailActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var editNombre: EditText
    private lateinit var editPosicion: EditText
    private lateinit var editNumero: EditText
    private lateinit var btnUbicacion: Button
    private lateinit var btnGuardar: Button
    private lateinit var btnEliminar: Button
    private var jugadorId: Int? = null
    private var latitud: Double? = null
    private var longitud: Double? = null

    private val locationPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                latitud = data.getDoubleExtra("latitude", 0.0)
                longitud = data.getDoubleExtra("longitude", 0.0)
                btnUbicacion.text = "Ubicación seleccionada ✓"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_detail)

        db = DatabaseHelper(this)
        editNombre = findViewById(R.id.editNombre)
        editPosicion = findViewById(R.id.editPosicion)
        editNumero = findViewById(R.id.editNumero)
        btnUbicacion = findViewById(R.id.btnUbicacion)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnEliminar = findViewById(R.id.btnEliminar)

        jugadorId = intent.getIntExtra("id", -1).takeIf { it != -1 }

        jugadorId?.let {
            val jugador = db.obtenerJugadorPorId(it)
            jugador?.let {
                editNombre.setText(jugador.nombre)
                editPosicion.setText(jugador.posicion)
                editNumero.setText(jugador.numero.toString())
                latitud = jugador.latitud
                longitud = jugador.longitud
                if (latitud != null && longitud != null) {
                    btnUbicacion.text = "Ubicación seleccionada ✓"
                }
            }
        }

        btnUbicacion.setOnClickListener {
            val intent = Intent(this, LocationPickerActivity::class.java).apply {
                putExtra("latitude", latitud ?: 0.0)
                putExtra("longitude", longitud ?: 0.0)
            }
            locationPickerLauncher.launch(intent)
        }

        btnGuardar.setOnClickListener {
            val nombre = editNombre.text.toString()
            val posicion = editPosicion.text.toString()
            val numero = editNumero.text.toString().toIntOrNull() ?: 0

            if (nombre.isEmpty() || posicion.isEmpty() || numero == 0) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (jugadorId == null) {
                db.agregarJugador(nombre, posicion, numero, latitud, longitud)
            } else {
                db.actualizarJugador(jugadorId!!, nombre, posicion, numero, latitud, longitud)
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