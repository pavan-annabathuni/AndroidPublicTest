package com.waycool.addfarm.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableMap.OnMapChangedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.gson.Gson
import com.google.maps.android.SphericalUtil
import com.waycool.addfarm.FarmsViewModel
import com.waycool.addfarm.R
import com.waycool.addfarm.adapter.PlacesListAdapter
import com.waycool.addfarm.databinding.FragmentDrawFarmBinding
import com.waycool.addfarm.utils.DrawingOption
import com.waycool.core.utils.AppSecrets
import com.waycool.data.utils.PlacesSearchEventError
import com.waycool.data.utils.PlacesSearchEventFound
import com.waycool.data.utils.PlacesSearchEventLoading
import kotlinx.coroutines.flow.merge
import java.util.*


class DrawFarmFragment : Fragment(), OnMapReadyCallback {


    private var isLocationFabPressed: Boolean = false
    private var currentMarker: Marker? = null
    private var searchLocationMarker: Marker? = null
    private var mMap: GoogleMap? = null
    private var points: MutableList<LatLng> = mutableListOf()

    private var markerList: MutableList<Marker> = mutableListOf()
    private var polygon: Polygon? = null
    private var polyline: Polyline? = null
    private lateinit var drawingOption: DrawingOption
    private var selectedMarkerIndex: Int? = null
    private var isPolygonDraw: Boolean = false
    private var isMarkerSelected: Boolean = false
    private var centerMarkerList: MutableList<Marker>? = mutableListOf()
    private val previousStateStack = Stack<MapState>()
    private val viewModel by lazy { ViewModelProvider(this)[FarmsViewModel::class.java] }
    private lateinit var placesClient: PlacesClient
    private var handler: Handler? = null
    private var searchCharSequence: CharSequence? = ""

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->


        Log.d("permission", "test$result")
        var allAreGranted = true
        for (b in result.values) {
            allAreGranted = allAreGranted && b
        }

        if (allAreGranted) {
            getLocation()
        }
    }

    private val binding by lazy { FragmentDrawFarmBinding.inflate(layoutInflater) }
    private val adapter by lazy { PlacesListAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        retainInstance = true
        if (!Places.isInitialized()) {
            Places.initialize(requireContext().applicationContext, AppSecrets.getMapsKey())
        }
        placesClient = Places.createClient(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarTitle.text = "Add Farm"
        binding.toolbar.setNavigationOnClickListener {
            activity?.finish()
        }


        binding.placesRv.adapter = adapter

        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)?.getMapAsync(this)


        binding.gpsFab.setOnClickListener {
            getLocation()
            isLocationFabPressed = true
        }

        binding.markerImageview.setOnTouchListener(CircleDotImageTouchListener())

        drawingOption = DrawingOption(
            0.0,
            0.0,
            18f,
            Color.argb(100, 58, 146, 17),
            Color.argb(255, 255, 255, 255),
            4,
            true,
            false,
            true,
            DrawingOption.DrawingType.POLYGON
        )

        binding.resetFab.setOnClickListener {
            previousStateStack.clear()
            isPolygonDraw = false
            //binding.savemapBtn.setVisibility(View.GONE)
            binding.areaCard.setVisibility(View.GONE)
            if (isMarkerSelected) {
                binding.markerImageview.setVisibility(View.INVISIBLE)
                isMarkerSelected = false
            }
            if (markerList != null && !markerList.isEmpty()) {
                for (m in markerList) {
                    m.remove()
                }
                markerList.clear()
            }
            if (centerMarkerList != null && !centerMarkerList!!.isEmpty()) {
                for (m in centerMarkerList!!) m.remove()
                centerMarkerList!!.clear()
            }
            points.clear()
            markerList.clear()
            if (polygon != null) polygon!!.remove()
            if (polyline != null) polyline!!.remove()
            polyline = null
            polygon = null
        }

        binding.undoFab.setOnClickListener {
            if (isMarkerSelected) {
                isMarkerSelected = false
                binding.markerImageview.setVisibility(View.INVISIBLE)
                val ll = mMap!!.projection
                    .fromScreenLocation(
                        Point(
                            binding.markerImageview.getX()
                                .toInt() + binding.markerImageview.getWidth() / 2,
                            binding.markerImageview.getY()
                                .toInt() + binding.markerImageview.getHeight() / 2
                        )
                    )
                markerList[selectedMarkerIndex!!].setPosition(ll)
                points[selectedMarkerIndex!!] = ll
                markerList[selectedMarkerIndex!!].isVisible = true
                if (polyline != null) {
                    polyline!!.points = points
                    addCenterMarkersToPolyline(polyline)
                    binding.areaCard.setVisibility(View.GONE)
                    //binding.savemapBtn.setVisibility(View.GONE)
                }

                if (polygon != null) {
                    polygon!!.points = points
                    addCenterMarkersToPolygon(polygon)
                    showAreaCard()
                }
            }
            /*else if(getIntent().hasExtra("edit_map")){
                }*/if (previousStateStack.size == 1) {
            previousStateStack.pop()
            binding.areaCard.setVisibility(View.GONE)
            //binding.savemapBtn.setVisibility(View.GONE)
            for (m in markerList) {
                m.remove()
            }
            for (m in centerMarkerList!!) m.remove()
            points.clear()
            markerList.clear()
            if (polygon != null) polygon!!.remove()
            if (polyline != null) polyline!!.remove()
            polyline = null
            polygon = null
        } else if (!previousStateStack.isEmpty()) {
            previousStateStack.pop()
            val state = previousStateStack.peek()
            points.clear()
            points.addAll(state.getLatLngList())
            isPolygonDraw = state.isPolygonDrawn
            for (m in markerList) {
                m.remove()
            }
            markerList.clear()
            for (p in points) {
                markerList.add(
                    mMap!!.addMarker(
                        MarkerOptions().position(
                            p
                        ).draggable(false)
                            .icon(
                                BitmapDescriptorFactory.fromResource(
                                    R.drawable.circle_green
                                )
                            ).flat(true).anchor(.5f, .5f)
                    )
                )
            }
            if (state.isPolygonDrawn) {
                polygon!!.points = points
                addCenterMarkersToPolygon(polygon)
                showAreaCard()
            } else {
                if (polyline == null) {
                    val dashedPattern =
                        Arrays.asList(
                            Dash(60F),
                            Gap(60F)
                        )
                    polyline = mMap!!.addPolyline(
                        PolylineOptions().addAll(points)
                            .color(-0x44000001).geodesic(true).pattern(dashedPattern)
                    )
                    binding.areaCard.setVisibility(View.GONE)
                    //binding.savemapBtn.setVisibility(View.GONE)
                }
                if (polygon != null) {
                    polygon!!.remove()
                    polygon = null
                }
                polyline!!.points = points
                addCenterMarkersToPolyline(polyline)
                binding.areaCard.setVisibility(View.GONE)
                //binding.savemapBtn.setVisibility(View.GONE)
            }
        }
            if (points.size == 0) isPolygonDraw = false
        }

        handler = Handler(Looper.myLooper()!!)
        val searchRunnable =
            Runnable {
                viewModel.onSearchQueryChanged(searchCharSequence.toString(), placesClient)
            }

        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                searchCharSequence = charSequence
                if (charSequence.isNotEmpty()) {
                    binding.searchCloseBtn.visibility = View.VISIBLE
                    binding.micBtn.visibility = View.GONE
                    handler!!.removeCallbacks(searchRunnable)
                    handler!!.postDelayed(searchRunnable, 500)
                } else {
                    binding.searchCloseBtn.visibility = View.GONE
                    binding.micBtn.visibility = View.VISIBLE
                    binding.placesResultsCv.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                }

            }

            override fun afterTextChanged(editable: Editable) {}
        })

        binding.searchCloseBtn.setOnClickListener {
            binding.search.text.clear()
        }
        binding.micBtn.setOnClickListener {
            speechToText()
        }

        viewModel.events.observe(requireActivity()) { event ->
            when (event) {
                is PlacesSearchEventLoading -> {
                    binding.progressBar.isIndeterminate = true
                    binding.progressBar.visibility = View.VISIBLE
                    binding.placesResultsCv.visibility = View.GONE
                }
                is PlacesSearchEventError -> {
                    binding.progressBar.isIndeterminate = false
                    binding.progressBar.visibility = View.GONE
                    binding.placesResultsCv.visibility = View.GONE
                }
                is PlacesSearchEventFound -> {
                    binding.progressBar.isIndeterminate = false
                    binding.progressBar.visibility = View.GONE
                    binding.placesResultsCv.visibility = View.VISIBLE
                    adapter.submitList(event.places)
                }
                else -> {}
            }
        }

        adapter.onItemClick = {
//            binding.search.setText(it.getPrimaryText(null))
            binding.placesResultsCv.visibility = View.GONE
            binding.progressBar.visibility = View.GONE

            val placeFields = listOf(Place.Field.LAT_LNG)

            val request = FetchPlaceRequest.builder(it.placeId, placeFields).build()

            placesClient.fetchPlace(request)
                .addOnSuccessListener { response ->
                    val place = response?.place
                    val latitude = place?.latLng?.latitude
                    val longitude = place?.latLng?.longitude
                    if (latitude != null && longitude != null) {
                        val latLng = LatLng(latitude, longitude)
                        moveMapCameraSearch(latLng)
                    }
                }
                .addOnFailureListener { exception ->
                    if (exception is ApiException) {
                        Toast.makeText(requireContext(), exception.message + "", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }


        binding.savemapBtn.setOnClickListener {
            if (points.isEmpty() && searchLocationMarker == null) {
                Toast.makeText(
                    requireContext(),
                    "Please Mark your Farm or nearest location of your farm.",
                    Toast.LENGTH_LONG
                ).show()
            } else if(points.size<3){
                Toast.makeText(
                    requireContext(),
                    "Please Mark more than 2 points to plot your Farm or nearest location of your farm.",
                    Toast.LENGTH_LONG
                ).show()

            }else {
                val bundle = Bundle()
                if (points.isNotEmpty()) {
                    if (points[points.size - 1].latitude != points[0].latitude || points[points.size - 1].longitude != points[0].longitude)
                        points.add(points[0])
                    bundle.putString("farm_json", Gson().toJson(points))
                    bundle.putString("farm_center", Gson().toJson(listOf(computeCentroid(points))))
                } else if (searchLocationMarker != null) {
                    bundle.putString(
                        "farm_json",
                        Gson().toJson(listOf(searchLocationMarker?.position))
                    )
                    bundle.putString(
                        "farm_center",
                        Gson().toJson(listOf(searchLocationMarker?.position))
                    )
                }
                findNavController().navigate(
                    R.id.action_drawFarmFragment_to_saveFarmFragment,
                    bundle
                )
            }
        }
    }


    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
        mMap!!.mapType = GoogleMap.MAP_TYPE_HYBRID

        getLocation()
        mMap!!.setOnMapClickListener { latLng ->
            if (!isMarkerSelected && !isPolygonDraw) {
                if (markerList == null) {
                    markerList =
                        ArrayList()
                    points = ArrayList()
                }
                markerList.add(
                    mMap!!.addMarker(
                        MarkerOptions().position(latLng)
                            .draggable(false)
                            .icon(
                                BitmapDescriptorFactory.fromResource(
                                    R.drawable.circle_green
                                )
                            ).flat(true).anchor(.5f, .5f)
                    )
                )

                points.add(latLng)
                if (points != null) {
                    if (points.size>=3) {
                        binding.savemapBtn.background.setTint(ContextCompat.getColor(requireContext(),(com.waycool.uicomponents.R.color.primaryColor)))
                        binding.savemapBtn.setTextColor(ContextCompat.getColor(requireContext(),(com.waycool.uicomponents.R.color.white)))

                  /*      mMap?.addPolygon(
                            PolygonOptions().addAll(points).fillColor(Color.argb(100, 58, 146, 17))
                                .strokeColor(
                                    Color.argb(255, 255, 255, 255)
                                )
                        )*/
                        showAreaCard()

                    }
                }
                val state = MapState()
                state.isPolygonDrawn = isPolygonDraw
                state.copyArrayList(points)
                previousStateStack.push(state)
                if (polyline != null) polyline!!.remove()
                val dashedPattern =
                    Arrays.asList(
                        Dash(60f),
                        Gap(60f)
                    )
                polyline = mMap!!.addPolyline(
                    PolylineOptions().addAll(points)
                        .color(-0x44000001).geodesic(true).pattern(dashedPattern)
                )
                addCenterMarkersToPolyline(polyline)
//                binding.areaCard.setVisibility(View.GONE)
                //binding.savemapBtn.setVisibility(View.GONE)
            } else if (isMarkerSelected) {
                isMarkerSelected = false
                binding.markerImageview.setVisibility(View.INVISIBLE)
                val ll = mMap!!.projection.fromScreenLocation(
                    Point(
                        binding.markerImageview.getX()
                            .toInt() + binding.markerImageview.getWidth() / 2,
                        binding.markerImageview.getY()
                            .toInt() + binding.markerImageview.getHeight() / 2
                    )
                )
                markerList[selectedMarkerIndex!!].setPosition(ll)
                points[selectedMarkerIndex!!] = ll
                markerList[selectedMarkerIndex!!].isVisible = true
                if (polyline != null) {
                    polyline!!.points = points
                    addCenterMarkersToPolyline(polyline)
//                    binding.areaCard.setVisibility(View.GONE)
                    //binding.savemapBtn.setVisibility(View.GONE)
                }
                if (polygon != null) {
                    polygon!!.points = points
                    addCenterMarkersToPolygon(polygon)
                    showAreaCard()
                }
            }
        }

        mMap!!.setOnMarkerClickListener(GoogleMap.OnMarkerClickListener { marker1 ->
            if (marker1 == currentMarker) return@OnMarkerClickListener true
            if (isMarkerSelected) {
                isMarkerSelected = false
                binding.markerImageview.setVisibility(View.INVISIBLE)
                val ll = mMap!!.projection.fromScreenLocation(
                    Point(
                        binding.markerImageview.getX()
                            .toInt() + binding.markerImageview.getWidth() / 2,
                        binding.markerImageview.getY()
                            .toInt() + binding.markerImageview.getHeight() / 2
                    )
                )
                markerList[selectedMarkerIndex!!].setPosition(ll)
                points[selectedMarkerIndex!!] = ll
                markerList[selectedMarkerIndex!!].isVisible = true
                if (polyline != null) {
                    polyline!!.points = points
                    addCenterMarkersToPolyline(polyline)
                    binding.areaCard.setVisibility(View.GONE)
                    //binding.savemapBtn.setVisibility(View.GONE)
                }

                if (polygon != null) {
                    polygon!!.points = points
                    addCenterMarkersToPolygon(polygon)
                    showAreaCard()
                }
            }
            if (centerMarkerList != null && centerMarkerList!!.contains(marker1)) {
                val centerMarkerIndex = centerMarkerList!!.indexOf(marker1)
                marker1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.circle_green))
                markerList.add(centerMarkerIndex + 1, marker1)
                points.add(centerMarkerIndex + 1, marker1.position)
                centerMarkerList!!.removeAt(centerMarkerIndex)
                val state = MapState()
                state.isPolygonDrawn = (isPolygonDraw)
                state.copyArrayList(points)
                previousStateStack.push(state)
                if (polyline != null) {
                    polyline!!.points = points
                    addCenterMarkersToPolyline(polyline)
//                    binding.areaCard.setVisibility(View.GONE)
                    //binding.savemapBtn.setVisibility(View.GONE)
                }
                if (polygon != null) {
                    polygon!!.points = points
                    addCenterMarkersToPolygon(polygon)
                    showAreaCard()
                }
            }
            isMarkerSelected = true
            selectedMarkerIndex = markerList.indexOf(marker1)
            if (marker1 == markerList[0] && polygon == null && markerList.size > 2) {
                if (polyline != null) polyline!!.remove()
                polyline = null
                isPolygonDraw = true
                polygon = mMap!!.addPolygon(
                    PolygonOptions().addAll(points).fillColor(drawingOption!!.fillColor)
                        .strokeColor(drawingOption.strokeColor)
                )
                addCenterMarkersToPolygon(polygon)
                showAreaCard()
                drawingOption.drawingType = (DrawingOption.DrawingType.POLYGON)
                val state = MapState()
                state.isPolygonDrawn = (isPolygonDraw)
                state.copyArrayList(points)
                previousStateStack.push(state)
            }
            marker1.isVisible = false
            val ll = marker1.position
            val p = mMap!!.projection.toScreenLocation(ll)
            binding.markerImageview.setX((p.x - binding.markerImageview.getWidth() / 2).toFloat())
            binding.markerImageview.setY((p.y - binding.markerImageview.getHeight() / 2).toFloat())
            binding.markerImageview.setVisibility(View.VISIBLE)
            true
        })

        mMap!!.setOnCameraMoveListener {
            if (isMarkerSelected) {
                val markerPointOnScreen = mMap!!.projection.toScreenLocation(
                    markerList[selectedMarkerIndex!!].position
                )
                binding.markerImageview.setX((markerPointOnScreen.x - binding.markerImageview.getWidth() / 2).toFloat())
                binding.markerImageview.setY((markerPointOnScreen.y - binding.markerImageview.getHeight() / 2).toFloat())
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                val mFusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(requireActivity().applicationContext)
                mFusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            moveMapToCenter(location)
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        Log.d("Registration", "" + it.message)
                    }
                    .addOnCanceledListener {
                        Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()

                    }
            } else {
                Toast.makeText(context, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermission()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermission() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )

//        requestPermissions(
//            arrayOf(
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ),
//            permissionId
//        )
        //requestPermissions(String[] {android.Manifest.permission.READ_CONTACTS}, REQUEST_CONTACT);
    }

    private fun moveMapToCenter(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        if (mMap != null) {
            placeMyLocationMarker(latLng)
            mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        }
    }

    private fun moveMapCameraSearch(latLng: LatLng?) {
        if (mMap != null && latLng != null) {
            placeSearchLocationMarker(latLng)
            mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }

//    fun moveMarkerCurrentPosition(location: Location) {
//        val latLng = LatLng(location.latitude, location.longitude)
//        if (mMap != null) {
//            myLocationMarker(latLng)
//        }
//    }

    private fun placeSearchLocationMarker(latLng: LatLng?) {
        if (searchLocationMarker != null) {
            searchLocationMarker?.position = latLng
        } else {
            searchLocationMarker = mMap!!.addMarker(
                latLng?.let {
                    MarkerOptions()
                        .position(it)
                        .draggable(false)
                }
            )
        }
    }

    private fun placeMyLocationMarker(latLng: LatLng) {
        if (currentMarker != null) {
            currentMarker?.position = latLng
        } else {
            currentMarker = mMap!!.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location))
                    .draggable(false)
            )
        }
    }


    fun addCenterMarkersToPolyline(polyline: Polyline?) {
        if (polyline == null) return
        val centerPOintsList = java.util.ArrayList<LatLng>()
        var builder: LatLngBounds.Builder
        for (i in 0 until polyline.points.size - 1) {
            builder = LatLngBounds.builder()
            builder.include(polyline.points[i])
            builder.include(polyline.points[i + 1])
            centerPOintsList.add(builder.build().center)
        }
        if (centerMarkerList == null) {
            centerMarkerList = java.util.ArrayList<Marker>()
            for (i in centerPOintsList.indices) {
                centerMarkerList?.add(
                    mMap!!.addMarker(
                        MarkerOptions().position(
                            centerPOintsList[i]
                        ).anchor(.5f, .5f).draggable(false)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.white_circle_small))
                            .flat(true)
                    )
                )
            }
        }
        if (centerMarkerList?.size == centerPOintsList.size) {
            for (i in centerPOintsList.indices) {
                centerMarkerList?.get(i)?.setPosition(centerPOintsList[i])
            }
        }
        if (centerMarkerList?.size!! < centerPOintsList.size) {
            for (i in centerPOintsList.indices) {
                if (i < centerMarkerList!!.size) centerMarkerList!!.get(i)
                    .setPosition(centerPOintsList[i]) else {
                    centerMarkerList!!.add(
                        mMap!!.addMarker(
                            MarkerOptions().position(
                                centerPOintsList[i]
                            ).anchor(.5f, .5f).draggable(false)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.white_circle_small))
                                .flat(true)
                        )
                    )
                }
            }
        }
        if (centerMarkerList!!.size > centerPOintsList.size) {
            for (m in centerMarkerList!!) {
                m.remove()
            }
            centerMarkerList!!.clear()
            for (i in centerPOintsList.indices) {
                centerMarkerList!!.add(
                    mMap!!.addMarker(
                        MarkerOptions().position(
                            centerPOintsList[i]
                        ).anchor(.5f, .5f).draggable(false)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.white_circle_small))
                            .flat(true)
                    )
                )
            }
        }
    }

    fun addCenterMarkersToPolygon(polygon: Polygon?) {
        if (polygon == null) return
        val centerPOintsList = java.util.ArrayList<LatLng>()
        var builder: LatLngBounds.Builder
        for (i in 0 until polygon.points.size - 1) {
            builder = LatLngBounds.Builder()
            builder.include(polygon.points[i])
            builder.include(polygon.points[i + 1])
            centerPOintsList.add(builder.build().center)
        }
        if (centerMarkerList == null) {
            centerMarkerList = java.util.ArrayList<Marker>()
            for (i in centerPOintsList.indices) {
                centerMarkerList!!.add(
                    mMap!!.addMarker(
                        MarkerOptions().position(
                            centerPOintsList[i]
                        ).anchor(.5f, .5f).draggable(false)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.white_circle_small))
                            .flat(true)
                    )
                )
            }
        }
        if (centerMarkerList!!.size == centerPOintsList.size) {
            for (i in centerPOintsList.indices) {
                centerMarkerList!!.get(i).setPosition(centerPOintsList[i])
            }
        }
        if (centerMarkerList!!.size < centerPOintsList.size) {
            for (i in centerPOintsList.indices) {
                if (i < centerMarkerList!!.size) centerMarkerList!!.get(i)
                    .setPosition(centerPOintsList[i]) else {
                    centerMarkerList!!.add(
                        mMap!!.addMarker(
                            MarkerOptions().position(
                                centerPOintsList[i]
                            ).anchor(.5f, .5f).draggable(false)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.white_circle_small))
                                .flat(true)
                        )
                    )
                }
            }
        }
        if (centerMarkerList!!.size > centerPOintsList.size) {
            for (m in centerMarkerList!!) {
                m.remove()
            }
            centerMarkerList!!.clear()
            for (i in centerPOintsList.indices) {
                centerMarkerList!!.add(
                    mMap!!.addMarker(
                        MarkerOptions().position(
                            centerPOintsList[i]
                        ).anchor(.5f, .5f).draggable(false)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.white_circle_small))
                            .flat(true)
                    )
                )
            }
        }
    }

    fun getLatLnBounds(points: List<LatLng?>): LatLngBounds? {
        val builder = LatLngBounds.builder()
        for (ll in points) {
            builder.include(ll)
        }
        return builder.build()
    }

    private fun showAreaCard() {
        binding.areaCard.visibility = View.VISIBLE
        //binding.savemapBtn.visibility = View.VISIBLE
        val area: Double =
            getArea(points) / 4046.86
        val perimeter: Double = getLength(points)
        binding.areaDisplayTv.text = (String.format("%.2f", area) + " Acre").trim { it <= ' ' }
        binding.perimeterTv.text = (String.format("%.2f", perimeter) + " Mtrs").trim { it <= ' ' }
//        if (area > 200) {
//            Toast.makeText(requireContext(), "Select smaller Area.", Toast.LENGTH_SHORT).show()
//            //binding.savemapBtn.setEnabled(false)
//        } else {
//            //binding.savemapBtn.setEnabled(true)
//        }
    }

    private fun getArea(latLngs: List<LatLng?>?): Double {
        return SphericalUtil.computeArea(latLngs)
    }

    private fun getLength(latLngs: List<LatLng?>?): Double {
        return SphericalUtil.computeLength(latLngs)
    }


    inner class CircleDotImageTouchListener : View.OnTouchListener {
        var dx = 0
        var dy = 0
        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            val x = motionEvent.rawX.toInt()
            val y = motionEvent.rawY.toInt()
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    mMap?.uiSettings?.setAllGesturesEnabled(false)
                    val layoutParams = view.layoutParams as RelativeLayout.LayoutParams
                    dx = x - layoutParams.leftMargin
                    dy = y - layoutParams.topMargin
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    val lParams = view.layoutParams as RelativeLayout.LayoutParams
                    lParams.leftMargin = x - dx
                    lParams.topMargin = y - dy
                    lParams.rightMargin = -250
                    lParams.bottomMargin = -250
                    view.layoutParams = lParams
                    markerList.get(selectedMarkerIndex!!).setPosition(
                        mMap?.projection!!.fromScreenLocation(
                            Point(
                                binding.markerImageview.x
                                    .toInt() + binding.markerImageview.width / 2,
                                binding.markerImageview.y
                                    .toInt() + binding.markerImageview.height / 2
                            )
                        )
                    )
                    points.set(
                        selectedMarkerIndex!!,
                        markerList.get(selectedMarkerIndex!!).position
                    )
                    if (polyline != null) {
                        polyline?.points = points
                        addCenterMarkersToPolyline(polyline)
                        binding.areaCard.setVisibility(View.GONE)
                        //binding.savemapBtn.setVisibility(View.GONE)
                    }
                    if (polygon != null) {
                        polygon!!.setPoints(points)
                        addCenterMarkersToPolygon(polygon)
                        showAreaCard()
                    }
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    mMap?.uiSettings?.setAllGesturesEnabled(true)
                    mMap?.projection?.let {
                        markerList[selectedMarkerIndex!!].setPosition(
                            it.fromScreenLocation(
                                Point(
                                    binding.markerImageview.x
                                        .toInt() + binding.markerImageview.width / 2,
                                    binding.markerImageview.y
                                        .toInt() + binding.markerImageview.height / 2
                                )
                            )
                        )
                    }
                    points.set(
                        selectedMarkerIndex!!,
                        markerList.get(selectedMarkerIndex!!).getPosition()
                    )
                    val state = MapState()
                    state.isPolygonDrawn = isPolygonDraw
                    state.copyArrayList(points)
                    previousStateStack.push(state)
                    return true
                }
            }
            return false
        }
    }

    class MapState {
        var isPolygonDrawn = false
        private var latLngList: MutableList<LatLng> = ArrayList()

        constructor() {}
        constructor(isPolygonDrawn: Boolean, latLngList: MutableList<LatLng>) {
            this.isPolygonDrawn = isPolygonDrawn
            this.latLngList = latLngList
        }

        fun getLatLngList(): List<LatLng> {
            return latLngList
        }

        fun setLatLngList(latLngList: MutableList<LatLng>) {
            this.latLngList = latLngList
        }

        fun copyArrayList(list: List<LatLng>?) {
            latLngList.addAll(list!!)
        }
    }

    private fun speechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast
                .makeText(requireActivity(), " " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )
                searchCharSequence = result?.get(0).toString()
                binding.search.setText(searchCharSequence)

            }
        }
    }

    companion object {
        private const val REQUEST_CODE_SPEECH_INPUT = 10110
    }

    private fun computeCentroid(points: List<LatLng>): LatLng? {
        var latitude = 0.0
        var longitude = 0.0
        val n = points.size
        for (point in points) {
            latitude += point.latitude
            longitude += point.longitude
        }
        return LatLng(latitude / n, longitude / n)
    }

    private fun getLatLong(points: List<LatLng>): LatLng? {
        var latitude = 0.0
        var longitude = 0.0
        val n = points.size
        for (point in points) {
            if (n <= 1) {
                latitude += point.latitude
                longitude += point.longitude
            }
        }
        return LatLng(latitude, longitude)
    }

}


