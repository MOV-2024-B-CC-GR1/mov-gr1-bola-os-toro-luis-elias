package com.example.deber

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.deber.data.DatabaseHelper
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {
    private lateinit var map: MapView
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar OSMdroid
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE))

        setContentView(R.layout.activity_map)

        db = DatabaseHelper(this)

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val mapController = map.controller
        mapController.setZoom(15.0)

        // Obtener todos los jugadores y mostrar sus marcadores
        val jugadores = db.obtenerJugadores()
        var lastPoint: GeoPoint? = null

        for (jugador in jugadores) {
            if (jugador.latitud != null && jugador.longitud != null) {
                val point = GeoPoint(jugador.latitud, jugador.longitud)
                lastPoint = point

                Marker(map).apply {
                    position = point
                    title = jugador.nombre
                    snippet = "Posición: ${jugador.posicion}, Número: ${jugador.numero}"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    map.overlays.add(this)
                }
            }
        }

        // Centrar el mapa en el último punto o en Quito si no hay puntos
        lastPoint?.let { mapController.setCenter(it) } ?:
        mapController.setCenter(GeoPoint(-0.1807, -78.4678))
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}