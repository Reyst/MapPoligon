package gsihome.reyst.mappoligon.map

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import gsihome.reyst.mappoligon.R
import gsihome.reyst.mappoligon.map.data.toDto
import gsihome.reyst.mappoligon.map.data.toLatLng
import gsihome.reyst.mappoligon.map.data.toLatLngs
import gsihome.reyst.mappoligon.map.data.toPosition
import gsihome.reyst.mappoligon.map.listeners.MarkerDragListener
import gsihome.reyst.mappoligon.report.ReportActivity

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fab: FloatingActionButton
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val markers: MutableList<Marker> = ArrayList()

    private var polygon: Polygon? = null

    private lateinit var viewModel: MapScreenViewModel

    private var fillPolygonColor: Int = 0
    private var strokePolygonColor: Int = 0

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        fillPolygonColor = resources.getColor(R.color.fill_polygon)
        strokePolygonColor = resources.getColor(R.color.stroke_polygon)

        viewModel = ViewModelProviders.of(this).get(MapScreenViewModel::class.java)

        fab = findViewById(R.id.fab)
        fab.isEnabled = false
        fab.setOnClickListener { askAboutNewPolygon() }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setObservers() {
        viewModel.getObservablePositions().observe(this, Observer { positions ->

            polygon?.remove()
            if (positions != null) {

                if (positions.size != markers.size) {
                    map.clear()
                    markers.clear()
                    positions.forEach { placeMarker(map, it.toLatLng()) }
                }

                drawPolygon(positions.toLatLngs())
            }
        })
    }

    private fun placeMarker(googleMap: GoogleMap, latLng: LatLng): Marker {
        val markerOptions = MarkerOptions().position(latLng).draggable(true)
        val marker = googleMap.addMarker(markerOptions)
        markers.add(marker)
        return marker
    }

    private fun askAboutNewPolygon() {
        AlertDialog.Builder(this)
                .setMessage(R.string.q_create_polygon)
                .setPositiveButton(android.R.string.yes) { _, _ -> createNewPolygon() }
                .setNegativeButton(android.R.string.no, null)
                .show()
    }

    private fun createNewPolygon() {
        polygon?.remove()
        markers.clear()
        viewModel.clear()
        map.clear()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        fab.isEnabled = true

        setObservers()
        initMapEventListeners(googleMap)
        checkPermissionAndInitResolvingCurrentLocation()
    }

    private fun initMapEventListeners(googleMap: GoogleMap) {
        val markerDragListener = MarkerDragListener(
                endDragAction = { viewModel.updatePosition(markers.indexOf(it), it.position.toPosition()) }
        )
        googleMap.setOnMarkerDragListener(markerDragListener)

        googleMap.setOnMapClickListener { latLng ->

            placeMarker(googleMap, latLng)
                    .also {
                        viewModel.updatePosition(markers.lastIndex, latLng.toPosition())
                    }
        }

        googleMap.setOnPolygonClickListener { _ ->
            val currentPosition = viewModel.getObservableCurrentPosition().value
            val vertexes = viewModel.getObservablePositions().value?.toLatLngs()?.toMutableList()
            if (currentPosition != null && vertexes != null) {
                val location = currentPosition.toLatLng()
                if (PolyUtil.containsLocation(location, vertexes, true)) {
                    openReportScreen()
                }
            }
        }
    }

    private fun openReportScreen() {

        val report = viewModel.getObservableReport().value

        if (report == null)
            AlertDialog.Builder(this)
                    .setMessage(R.string.q_create_report)
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes) { _, _ -> ReportActivity.startForResult(this, null, REQ_REPORT, RESULT_KEY) }
                    .setCancelable(false)
                    .show()
        else
            ReportActivity.startForResult(this, report.toDto(), REQ_REPORT, RESULT_KEY)

    }

    private fun checkPermissionAndInitResolvingCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                                              arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                                              LOCATION_PERMISSIONS_REQUEST)
        } else {
            initResolvingCurrentLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when (requestCode) {
            LOCATION_PERMISSIONS_REQUEST -> {
                permissions.any {
                    val index = permissions.indexOf(it)
                    grantResults.size > index && grantResults[index] == PackageManager.PERMISSION_GRANTED
                }.also {
                    if (it) initResolvingCurrentLocation()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    @SuppressLint("MissingPermission") // permissions are checked for the method
    private fun initResolvingCurrentLocation() {
        map.isMyLocationEnabled = true
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val latLng = LatLng(it.latitude, it.longitude)
                        viewModel.updateCurrentPosition(latLng.toPosition())
                        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10f)
                        map.animateCamera(cameraUpdate)
                    }
                }
    }

    private fun drawPolygon(vertexes: Collection<LatLng>) {
        if (vertexes.size > 2) {
            polygon = map.addPolygon(
                    PolygonOptions()
                            .addAll(vertexes)
                            .fillColor(fillPolygonColor)
                            .strokeColor(strokePolygonColor)
                            .clickable(true)
            )
        }

    }

    companion object {
        private const val LOCATION_PERMISSIONS_REQUEST = 1001
        private const val REQ_REPORT = 2001

        private const val RESULT_KEY = "result"

    }
}
