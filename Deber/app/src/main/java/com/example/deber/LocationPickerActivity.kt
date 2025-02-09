package com.example.deber

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

class LocationPickerActivity : AppCompatActivity() {
    private lateinit var map: MapView
    private var currentMarker: Marker? = null

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
        private const val STORAGE_PERMISSION_REQUEST_CODE = 124
        private const val QUITO_LAT = -0.1807
        private const val QUITO_LNG = -78.4678
        private const val DEFAULT_ZOOM = 13.0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_picker)

        // Verificar permisos al inicio
        checkPermissions()
    }

    private fun checkPermissions() {
        if (hasLocationAndStoragePermissions()) {
            initializeMap()
        } else {
            requestPermissions()
        }
    }

    private fun hasLocationAndStoragePermissions(): Boolean {
        val locationPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val storagePermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

        return locationPermissionGranted && storagePermissionGranted
    }

    private fun requestPermissions() {
        // Solicitar permisos de ubicación primero
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_REQUEST_CODE
            )
        } else {
            // Si ya tenemos permisos de ubicación, solicitar almacenamiento
            requestStoragePermission()
        }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                } catch (e: Exception) {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    startActivity(intent)
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun initializeMap() {
        try {
            Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE))

            map = findViewById(R.id.map)
            setupMap()
            setupMapEvents()
            setupConfirmButton()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al inicializar el mapa: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun setupMap() {
        map.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.ALWAYS)
            setBuiltInZoomControls(true)
        }

        val mapController = map.controller
        mapController.setZoom(DEFAULT_ZOOM)

        val quitoPoint = GeoPoint(QUITO_LAT, QUITO_LNG)
        mapController.setCenter(quitoPoint)
        addMarker(quitoPoint)
    }

    private fun setupMapEvents() {
        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                addMarker(p)
                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean = false
        })
        map.overlays.add(mapEventsOverlay)
    }

    private fun setupConfirmButton() {
        findViewById<Button>(R.id.btnConfirmar).setOnClickListener {
            currentMarker?.let { marker ->
                val intent = Intent().apply {
                    putExtra("latitude", marker.position.latitude)
                    putExtra("longitude", marker.position.longitude)
                }
                setResult(Activity.RESULT_OK, intent)
                finish()
            } ?: run {
                Toast.makeText(this, "Por favor selecciona una ubicación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addMarker(point: GeoPoint) {
        currentMarker?.let { map.overlays.remove(it) }

        currentMarker = Marker(map).apply {
            position = point
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Ubicación seleccionada"
            snippet = "Lat: ${point.latitude}\nLon: ${point.longitude}"
        }

        map.overlays.add(currentMarker)
        map.invalidate()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    requestStoragePermission()
                } else {
                    showPermissionExplanationDialog()
                }
            }
            STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeMap()
                } else {
                    showStoragePermissionDialog()
                }
            }
        }
    }

    private fun showPermissionExplanationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permisos Necesarios")
            .setMessage("Esta aplicación necesita permisos de ubicación y almacenamiento para funcionar correctamente.")
            .setPositiveButton("Configuración") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancelar") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun showStoragePermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permiso de Almacenamiento")
            .setMessage("Se necesita acceso al almacenamiento para el funcionamiento del mapa.")
            .setPositiveButton("Configuración") { _, _ ->
                requestStoragePermission()
            }
            .setNegativeButton("Cancelar") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    override fun onResume() {
        super.onResume()
        if (!::map.isInitialized && hasLocationAndStoragePermissions()) {
            initializeMap()
        }
        if (::map.isInitialized) {
            map.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::map.isInitialized) {
            map.onPause()
        }
    }
}