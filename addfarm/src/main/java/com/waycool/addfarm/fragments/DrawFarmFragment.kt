package com.waycool.addfarm.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
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
import com.waycool.addfarm.utils.ShowCaseViewModel
import com.waycool.core.utils.AppSecrets
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventItemClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.*
import com.waycool.data.utils.AppUtils.networkErrorStateTranslations
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import java.util.*


class DrawFarmFragment : Fragment(), OnMapReadyCallback {

    private var fusedLocationProviderClient: FusedLocationProviderClient?=null
    private var viewCase: GuideView? = null
    private var pos: Int = 0
    private var isLocationFabPressed: Boolean = false
    private var currentMarker: Marker? = null
    private var searchLocationMarker: Marker? = null
    private var mMap: GoogleMap? = null
    private var points: MutableList<LatLng> = mutableListOf()
    private lateinit var apiErrorHandlingBinding: ApiErrorHandlingBinding

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
    private val showCaseDataList: ArrayList<ShowCaseViewModel> = ArrayList<ShowCaseViewModel>()
    private var myFarmEdit: MyFarmsDomain? = null
    private var _binding: FragmentDrawFarmBinding? = null
    private val binding get() = _binding!!

    private val locationRequest = LocationRequest
        .Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
        .build()

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult.lastLocation?.let {
                moveMapToCenter(it)
                removeLocationCallback()
            }
        }
    }
    private fun removeLocationCallback() {
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->

        Log.d("permission", "test$result")
        var allAreGranted = true
        for (b in result.values) {
            allAreGranted = allAreGranted && b
        }

        if (checkPermissions()) {

            getLocation()
        }
    }

    private val adapter by lazy { PlacesListAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDrawFarmBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (!Places.isInitialized()) {
            Places.initialize(requireContext().applicationContext, AppSecrets.getMapsKey())
        }
        placesClient = Places.createClient(requireContext())

        CoroutineScope(Dispatchers.IO).launch {
            binding.toolbarTitle.text = TranslationsManager().getString("str_add_farm")
            binding.search.hint = TranslationsManager().getString("search")

        }
        TranslationsManager().loadString("next",binding.savemapBtn,"NEXT")
        binding.toolbar.setNavigationOnClickListener {
            activity?.finish()
        }

        if (requireActivity().intent.extras != null) {
            val bundle = requireActivity().intent.extras
            if (bundle?.getBoolean("isedit") == true) {
                myFarmEdit = bundle.getParcelable("farm")
            }
        }

        apiErrorHandlingBinding = binding.errorState
        networkErrorStateTranslations(apiErrorHandlingBinding)

        networkCall()
        apiErrorHandlingBinding.clBtnTryAgainInternet.setOnClickListener {
            networkCall()
        }

        binding.placesRv.adapter = adapter

        showCaseDataList.add(
            ShowCaseViewModel(
                binding.gpsFab,
                "Get Current Location",
                "Click on the Location Button \nto view current location on google maps."
            )
        )
        showCaseDataList.add(ShowCaseViewModel(binding.pointA, "Mark Point 1", "NEXT"))
        showCaseDataList.add(ShowCaseViewModel(binding.pointB, "Mark Point 2", "NEXT"))
        showCaseDataList.add(
            ShowCaseViewModel(
                binding.pointC,
                "Mark alteast 3 Points to plot a farm",
                "NEXT"
            )
        )
        showCaseDataList.add(ShowCaseViewModel(binding.undoFab, "Click this to Undo point", "NEXT"))
        showCaseDataList.add(
            ShowCaseViewModel(
                binding.resetFab,
                "Click this to Reset all points",
                "NEXT"
            )
        )




        binding.tutorial.setOnClickListener {
            EventClickHandling.calculateClickEvent("farm_tutorial")
            binding.pointA.visibility = View.VISIBLE
            binding.pointB.visibility = View.VISIBLE
            binding.pointC.visibility = View.VISIBLE
            pos = 0

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                guideView(showCaseDataList[pos])

            }
        }
        binding.gpsFab.setOnClickListener {
            EventClickHandling.calculateClickEvent("location_icon")
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
            EventClickHandling.calculateClickEvent("farm_reset")
            previousStateStack.clear()
            isPolygonDraw = false
            binding.areaCard.visibility = View.GONE
            if (isMarkerSelected) {
                binding.markerImageview.visibility = View.INVISIBLE
                isMarkerSelected = false
            }
            if (!markerList.isNullOrEmpty()) {
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
            EventClickHandling.calculateClickEvent("farm_undo")
            if (isMarkerSelected) {
                isMarkerSelected = false
                binding.markerImageview.visibility = View.INVISIBLE
                val ll = mMap!!.projection
                    .fromScreenLocation(
                        Point(
                            binding.markerImageview.x
                                .toInt() + binding.markerImageview.width / 2,
                            binding.markerImageview.y
                                .toInt() + binding.markerImageview.height / 2
                        )
                    )
                markerList[selectedMarkerIndex!!].position = ll
                points[selectedMarkerIndex!!] = ll
                markerList[selectedMarkerIndex!!].isVisible = true
                if (polyline != null) {
                    polyline!!.points = points
                    addCenterMarkersToPolyline(polyline)
                    binding.areaCard.visibility = View.GONE
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
            binding.areaCard.visibility = View.GONE
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
                mMap!!.addMarker(
                    MarkerOptions().position(
                        p
                    ).draggable(false)
                        .icon(
                            BitmapDescriptorFactory.fromResource(
                                R.drawable.circle_green
                            )
                        ).flat(true).anchor(.5f, .5f)
                )?.let { it1 ->
                    markerList.add(
                        it1
                    )
                }
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
                    binding.areaCard.visibility = View.GONE
                    //binding.savemapBtn.setVisibility(View.GONE)
                }
                if (polygon != null) {
                    polygon!!.remove()
                    polygon = null
                }
                polyline!!.points = points
                addCenterMarkersToPolyline(polyline)
                binding.areaCard.visibility = View.GONE
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

                EventClickHandling.calculateClickEvent("search_location")
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
                        ToastStateHandling.toastError(
                            requireContext(),
                            exception.message + "",
                            Toast.LENGTH_SHORT
                        )
                    }
                }
        }


        binding.savemapBtn.setOnClickListener {
            if (points.isEmpty() && searchLocationMarker == null) {

                CoroutineScope(Dispatchers.Main).launch {
                    val toastServerError = TranslationsManager().getString("please_mark")
                    if(!toastServerError.isNullOrEmpty()){
                        context?.let { it1 -> ToastStateHandling.toastError(it1,toastServerError,
                            Toast.LENGTH_SHORT
                        ) }}
                    else {context?.let { it1 -> ToastStateHandling.toastError(it1,"Please Mark your Farm or nearest location of your farm.",
                        Toast.LENGTH_SHORT
                    ) }}}
            } else if (points.size < 3) {
                CoroutineScope(Dispatchers.Main).launch {
                    val toastServerError = TranslationsManager().getString("please_mark_points")
                    if(!toastServerError.isNullOrEmpty()){
                        context?.let { it1 -> ToastStateHandling.toastError(it1,toastServerError,
                            Toast.LENGTH_SHORT
                        ) }}
                    else {context?.let { it1 -> ToastStateHandling.toastError(it1,"Please Mark more than 2 points to plot your Farm or nearest location of your farm.",

                        Toast.LENGTH_SHORT
                    ) }}}


            } else if (getAreaInAcre(points) > 100) {
                CoroutineScope(Dispatchers.Main).launch {
                    val toastServerError = TranslationsManager().getString("farm_area_draw")
                    if(!toastServerError.isNullOrEmpty()){
                        context?.let { it1 -> ToastStateHandling.toastError(it1,toastServerError,
                            Toast.LENGTH_SHORT
                        ) }}
                    else {context?.let { it1 -> ToastStateHandling.toastError(it1,"Farm Area is large. Draw smaller Farm.",
                        Toast.LENGTH_SHORT
                    ) }}}

            } else {
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
                if (myFarmEdit != null)
                    bundle.putParcelable("farm", myFarmEdit)

                findNavController().navigate(
                    R.id.action_drawFarmFragment_to_saveFarmFragment,
                    bundle
                )
            }
        }
    }

    private fun editFarm() {
        if (myFarmEdit != null) {
            val pnts: ArrayList<LatLng>? = myFarmEdit?.farmJson
            mMap?.setOnMapLoadedCallback {
                polygon = pnts?.let {
                    PolygonOptions().addAll(it).fillColor(Color.argb(100, 58, 146, 17))
                        .strokeColor(
                            Color.argb(180, 58, 146, 17)
                        )
                }?.let {
                    mMap?.addPolygon(
                        it
                    )
                }


                pnts?.let { getLatLnBounds(it) }?.let {
                    CameraUpdateFactory.newLatLngBounds(it, 250)
                }?.let {
                    mMap?.animateCamera(it)
                }

                val state = MapState()
                isPolygonDraw = true
                if (pnts != null) {
                    for (latLng in pnts) {
                        val marker = mMap!!.addMarker(
                            MarkerOptions().position(
                                latLng
                            )
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_green))
                                .anchor(0.5f, .5f)
                                .draggable(false)
                                .flat(true)
                        )
                        marker?.tag = latLng
                        if (marker != null) {
                            markerList.add(marker)
                        }
                        points.add(latLng)
                    }
                }

                if (points != null) {
                    if (points.size >= 3) {
                        binding.savemapBtn.background.setTint(
                            ContextCompat.getColor(
                                requireContext(),
                                (com.waycool.uicomponents.R.color.primaryColor)
                            )
                        )
                        binding.savemapBtn.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                (com.waycool.uicomponents.R.color.white)
                            )
                        )
                        showAreaCard()

                    }
                }

                addCenterMarkersToPolygon(polygon)
                showAreaCard()
                state.isPolygonDrawn = (isPolygonDraw)
                state.copyArrayList(pnts)
                previousStateStack.push(state)
            }
        }
    }

    private fun networkCall() {
        if (NetworkUtil.getConnectivityStatusString(context) == 0) {
            binding.tutorial.isClickable = false
            binding.clInclude.visibility = View.VISIBLE
            apiErrorHandlingBinding.clInternetError.visibility = View.VISIBLE
            AppUtils.translatedToastServerErrorOccurred(context)
        } else {
            binding.tutorial.isClickable = true
            binding.clInclude.visibility = View.GONE
            apiErrorHandlingBinding.clInternetError.visibility = View.GONE
            if (myFarmEdit == null)
                getLocation()
        }
    }

    private fun guideView(showCaseData: ShowCaseViewModel) {
        viewCase = GuideView.Builder(requireContext())
            .setTitle(showCaseData.title)
            .setContentText(showCaseData.content)
            .setTargetView(showCaseData.view)
            .setContentTextSize(12) //optional
            .setTitleTextSize(14)
            .setDismissType(DismissType.anywhere)//optional
            .setGuideListener {
                if (pos + 1 <= (showCaseDataList.size - 1)) {
                    pos += 1
                    guideView(showCaseDataList[pos])
                }
                if (pos + 1 == showCaseDataList.size - 1) {
                    binding.pointA.visibility = View.GONE
                    binding.pointB.visibility = View.GONE
                    binding.pointC.visibility = View.GONE
                }

            }
            .build()
        viewCase!!.show()

    }


    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap?.mapType = GoogleMap.MAP_TYPE_HYBRID


        mMap?.setOnMapClickListener { latLng ->
            if (!isMarkerSelected && !isPolygonDraw) {
                if (markerList == null) {
                    markerList =
                        ArrayList()
                    points = ArrayList()
                }
                mMap!!.addMarker(
                    MarkerOptions().position(latLng)
                        .draggable(false)
                        .icon(
                            BitmapDescriptorFactory.fromResource(
                                R.drawable.circle_green
                            )
                        ).flat(true).anchor(.5f, .5f)
                )?.let {
                    markerList.add(
                        it
                    )
                }

                points.add(latLng)
                if (points != null) {
                    if (points.size >= 3) {
                        binding.savemapBtn.background.setTint(
                            ContextCompat.getColor(
                                requireContext(),
                                (com.waycool.uicomponents.R.color.primaryColor)
                            )
                        )
                        binding.savemapBtn.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                (com.waycool.uicomponents.R.color.white)
                            )
                        )

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
                binding.markerImageview.visibility = View.INVISIBLE
                val ll = mMap!!.projection.fromScreenLocation(
                    Point(
                        binding.markerImageview.x
                            .toInt() + binding.markerImageview.width / 2,
                        binding.markerImageview.y
                            .toInt() + binding.markerImageview.height / 2
                    )
                )
                markerList[selectedMarkerIndex!!].position = ll
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

        mMap?.setOnMarkerClickListener(GoogleMap.OnMarkerClickListener { marker1 ->
            if (marker1 == currentMarker) return@OnMarkerClickListener true
            if (marker1 == searchLocationMarker) return@OnMarkerClickListener true
            if (isMarkerSelected) {
                isMarkerSelected = false
                binding.markerImageview.visibility = View.INVISIBLE
                val ll = mMap!!.projection.fromScreenLocation(
                    Point(
                        binding.markerImageview.x
                            .toInt() + binding.markerImageview.width / 2,
                        binding.markerImageview.y
                            .toInt() + binding.markerImageview.height / 2
                    )
                )
                markerList[selectedMarkerIndex!!].position = ll
                points[selectedMarkerIndex!!] = ll
                markerList[selectedMarkerIndex!!].isVisible = true
                if (polyline != null) {
                    polyline!!.points = points
                    addCenterMarkersToPolyline(polyline)
                    binding.areaCard.visibility = View.GONE
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
                    PolygonOptions().addAll(points).fillColor(drawingOption.fillColor)
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
            val p = mMap?.projection?.toScreenLocation(ll)
            (p?.x?.minus(binding.markerImageview.width / 2))?.toFloat()
                ?.let { binding.markerImageview.x = it }
            (p?.y?.minus(binding.markerImageview.height / 2))?.toFloat()
                ?.let { binding.markerImageview.y = it }
            binding.markerImageview.visibility = View.VISIBLE
            true
        })

        mMap!!.setOnCameraMoveListener {
            if (isMarkerSelected) {
                val markerPointOnScreen = mMap!!.projection.toScreenLocation(
                    markerList[selectedMarkerIndex!!].position
                )
                binding.markerImageview.x = (markerPointOnScreen.x - binding.markerImageview.width / 2).toFloat()
                binding.markerImageview.y = (markerPointOnScreen.y - binding.markerImageview.height / 2).toFloat()
            }
        }

        if (myFarmEdit != null) {
            editFarm()
        } else {
            getLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(requireContext().applicationContext)
                fusedLocationProviderClient?.lastLocation!!
                    .addOnSuccessListener {
                        if (it != null){
                            val bundle=Bundle()
                            bundle.putString("latitude",it.latitude.toString())
                            bundle.putString("longitude",it.longitude.toString())
                            EventItemClickHandling.calculateItemClickEvent("location_icon",bundle)

                            moveMapToCenter(it)
                        }
                    }
                fusedLocationProviderClient?.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )

            } else {

                val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                    .setAlwaysShow(true)

                val locationResponseTask: Task<LocationSettingsResponse> =
                    LocationServices.getSettingsClient(requireContext().applicationContext)
                        .checkLocationSettings(builder.build())
                locationResponseTask.addOnCompleteListener {
                    try {
                        val response: LocationSettingsResponse = it.getResult(ApiException::class.java)
                    } catch (e: ApiException) {
                        if (e.statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                            val apiException: ResolvableApiException = e as ResolvableApiException
                            try {
                                apiException.startResolutionForResult(requireActivity(),REQUEST_CODE_GPS )
                            } catch (sendIntent: IntentSender.SendIntentException) {
                                sendIntent.printStackTrace()
                            }
                        }
                    }

                }
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
            if (latLng != null) {
                searchLocationMarker?.position = latLng
            }
        } else {
            searchLocationMarker = latLng?.let {
                MarkerOptions()
                    .position(it)
                    .draggable(false)
            }?.let {
                mMap!!.addMarker(
                    it
                )
            }
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
                mMap!!.addMarker(
                    MarkerOptions().position(
                        centerPOintsList[i]
                    ).anchor(.5f, .5f).draggable(false)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.white_circle_small))
                        .flat(true)
                )?.let {
                    centerMarkerList?.add(
                        it
                    )
                }
            }
        }
        if (centerMarkerList?.size == centerPOintsList.size) {
            for (i in centerPOintsList.indices) {
                centerMarkerList?.get(i)?.position = centerPOintsList[i]
            }
        }
        if (centerMarkerList?.size!! < centerPOintsList.size) {
            for (i in centerPOintsList.indices) {
                if (i < centerMarkerList!!.size) centerMarkerList!!.get(i)
                    .setPosition(centerPOintsList[i]) else {
                    mMap!!.addMarker(
                        MarkerOptions().position(
                            centerPOintsList[i]
                        ).anchor(.5f, .5f).draggable(false)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.white_circle_small))
                            .flat(true)
                    )?.let {
                        centerMarkerList!!.add(
                            it
                        )
                    }
                }
            }
        }
        if (centerMarkerList!!.size > centerPOintsList.size) {
            for (m in centerMarkerList!!) {
                m.remove()
            }
            centerMarkerList!!.clear()
            for (i in centerPOintsList.indices) {
                mMap!!.addMarker(
                    MarkerOptions().position(
                        centerPOintsList[i]
                    ).anchor(.5f, .5f).draggable(false)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.white_circle_small))
                        .flat(true)
                )?.let {
                    centerMarkerList!!.add(
                        it
                    )
                }
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
                mMap!!.addMarker(
                    MarkerOptions().position(
                        centerPOintsList[i]
                    ).anchor(.5f, .5f).draggable(false)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.white_circle_small))
                        .flat(true)
                )?.let {
                    centerMarkerList!!.add(
                        it
                    )
                }
            }
        }
        if (centerMarkerList!!.size == centerPOintsList.size) {
            for (i in centerPOintsList.indices) {
                centerMarkerList!!.get(i).position = centerPOintsList[i]
            }
        }
        if (centerMarkerList!!.size < centerPOintsList.size) {
            for (i in centerPOintsList.indices) {
                if (i < centerMarkerList!!.size) centerMarkerList!!.get(i)
                    .setPosition(centerPOintsList[i]) else {
                    mMap!!.addMarker(
                        MarkerOptions().position(
                            centerPOintsList[i]
                        ).anchor(.5f, .5f).draggable(false)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.white_circle_small))
                            .flat(true)
                    )?.let {
                        centerMarkerList!!.add(
                            it
                        )
                    }
                }
            }
        }
        if (centerMarkerList!!.size > centerPOintsList.size) {
            for (m in centerMarkerList!!) {
                m.remove()
            }
            centerMarkerList!!.clear()
            for (i in centerPOintsList.indices) {
                mMap!!.addMarker(
                    MarkerOptions().position(
                        centerPOintsList[i]
                    ).anchor(.5f, .5f).draggable(false)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.white_circle_small))
                        .flat(true)
                )?.let {
                    centerMarkerList!!.add(
                        it
                    )
                }
            }
        }
    }

    fun getLatLnBounds(points: List<LatLng?>): LatLngBounds? {
        val builder = LatLngBounds.builder()
        for (ll in points) {
            if (ll != null) {
                builder.include(ll)
            }
        }
        return builder.build()
    }

    private fun showAreaCard() {
        binding.areaCard.visibility = View.VISIBLE
        //binding.savemapBtn.visibility = View.VISIBLE
        val area: Double =
            getAreaInAcre(points)
        val perimeter: Double = getLength(points)
        binding.areaDisplayTv.text = (String.format("%.2f", area) + " Acre").trim { it <= ' ' }
        binding.perimeterTv.text = (String.format("%.2f", perimeter) + " Mtrs").trim { it <= ' ' }
        TranslationsManager().loadString("str_area",binding.areaTitleTv,"Area")
        TranslationsManager().loadString("preimeter",binding.textView,"Perimeter")

    }

    private fun getAreaInAcre(latLngs: List<LatLng?>?): Double {
        return SphericalUtil.computeArea(latLngs) / 4046.86
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
                    markerList.get(selectedMarkerIndex!!).position = mMap?.projection!!.fromScreenLocation(
                        Point(
                            binding.markerImageview.x
                                .toInt() + binding.markerImageview.width / 2,
                            binding.markerImageview.y
                                .toInt() + binding.markerImageview.height / 2
                        )
                    )
                    points.set(
                        selectedMarkerIndex!!,
                        markerList.get(selectedMarkerIndex!!).position
                    )
                    if (polyline != null) {
                        polyline?.points = points
                        addCenterMarkersToPolyline(polyline)
                        binding.areaCard.visibility = View.GONE
                        //binding.savemapBtn.setVisibility(View.GONE)
                    }
                    if (polygon != null) {
                        polygon!!.points = points
                        addCenterMarkersToPolygon(polygon)
                        showAreaCard()
                    }
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    mMap?.uiSettings?.setAllGesturesEnabled(true)
                    mMap?.projection?.let {
                        markerList[selectedMarkerIndex!!].position = it.fromScreenLocation(
                            Point(
                                binding.markerImageview.x
                                    .toInt() + binding.markerImageview.width / 2,
                                binding.markerImageview.y
                                    .toInt() + binding.markerImageview.height / 2
                            )
                        )
                    }
                    points.set(
                        selectedMarkerIndex!!,
                        markerList.get(selectedMarkerIndex!!).position
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

        constructor()
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
        EventClickHandling.calculateClickEvent("Add_farm_STT")
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
            ToastStateHandling.toastError(requireActivity(), " " + e.message, Toast.LENGTH_SHORT)
        }
    }

    @Deprecated("Deprecated in Java")
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
        if(resultCode==REQUEST_CODE_GPS && resultCode==AppCompatActivity.RESULT_OK){
            getLocation()
        }
    }

    companion object {
        private const val REQUEST_CODE_SPEECH_INPUT = 10110
        private const val REQUEST_CODE_GPS = 1011
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

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)

    }

    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("DrawFarmFragment")
    }

}


