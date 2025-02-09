package com.example.deber

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.deber.data.DatabaseHelper
import com.example.deber.data.Jugador

class MainActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var btnAgregar: Button
    private lateinit var jugadores: List<Jugador>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DatabaseHelper(this)
        listView = findViewById(R.id.listView)
        btnAgregar = findViewById(R.id.btnAgregar)

        cargarJugadores()

        btnAgregar.setOnClickListener {
            startActivity(Intent(this, PlayerDetailActivity::class.java))
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val jugador = jugadores[position]
            val intent = Intent(this, PlayerDetailActivity::class.java).apply {
                putExtra("id", jugador.id)
            }
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu resource file
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_map -> {
                // Launch MapActivity
                startActivity(Intent(this, MapActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarJugadores()
    }

    private fun cargarJugadores() {
        jugadores = db.obtenerJugadores()
        val nombres = jugadores.map { it.nombre }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nombres)
        listView.adapter = adapter
    }
}