package com.example.ndvi

import android.animation.LayoutTransition
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.ndvi.adapter.DateAdapter
import com.example.ndvi.databinding.FragmentNdviBinding
import com.example.ndvi.viewModel.NdviViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.Slider
import com.google.android.material.tabs.TabLayout
import com.waycool.data.Network.NetworkModels.NdviData
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import kotlinx.coroutines.launch
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class NdviFragment : Fragment(), OnMapReadyCallback {
    private var polygon: Polygon? = null
    private lateinit var binding: FragmentNdviBinding
    private val viewModel: NdviViewModel by lazy {
        ViewModelProvider(this)[NdviViewModel::class.java]
    }
    private var googleMap: GoogleMap? = null
    private var myFarm: MyFarmsDomain? = null

    private enum class TileType {
        NDVI, TRUE_COLOR
    }
    private var selectedTileType = TileType.NDVI
    private var tileOverlay: TileOverlay? = null
    private var selectedNdvi: NdviData? = null
    private var ndviDataList: List<NdviData>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (arguments != null) {
                myFarm = arguments?.getParcelable("farm")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNdviBinding.inflate(inflater)
        binding.farmName.text = myFarm?.farmName

        val mapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.map_ndvi) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            binding.farmLocation.text = it.data?.profile?.district
        }

        binding.roolLlNdvi.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        onClicks()
        tabs()
        opacity()
        translation()

        binding.recycleView.adapter = DateAdapter()
        EventClickHandling.calculateClickEvent("NDVI_Landing")
        return binding.root
    }

    private fun onClicks() {
        binding.floatingActionButton1.setOnClickListener {
            this.findNavController().navigate(R.id.action_ndviFragment_to_infoSheetFragment)
        }
        binding.topAppBar.setOnClickListener {
            this.findNavController().navigateUp()
        }

        binding.floatingActionButton2.setOnClickListener {
            if (myFarm != null && googleMap != null) {
                val points = myFarm?.farmJson as ArrayList
                getLatLnBounds(points)?.let { it1 ->
                    CameraUpdateFactory.newLatLngBounds(
                        it1,
                        50
                    )
                }?.let { it2 ->
                    googleMap?.animateCamera(
                        it2
                    )
                }
            }
        }

    }

    private fun tabs() {
        viewModel.viewModelScope.launch {
            val vegIndex = TranslationsManager().getString("vegetation_index")
            binding.tabLayout.addTab(
                binding.tabLayout.newTab().setText(vegIndex)
                    .setCustomView(R.layout.item_tab)
            )
            val TranTureColor = TranslationsManager().getString("true_colour")
            binding.tabLayout.addTab(
                binding.tabLayout.newTab().setText(TranTureColor).setCustomView(R.layout.item_tab)
            )
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (binding.tabLayout.selectedTabPosition) {
                    0 -> {
                        EventClickHandling.calculateClickEvent("Vegitation_Index")
                        selectedTileType = TileType.NDVI
                        showTileNDVI(selectedNdvi)
                    }
                    1 -> {
                        EventClickHandling.calculateClickEvent("True_colour")
                        selectedTileType = TileType.TRUE_COLOR
                        showTileNDVI(selectedNdvi)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
//                onMapReady(googleMap)
//                googleMap?.clear()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    override fun onMapReady(map: GoogleMap) {
        // val url = it.data?.data?.get(0)?.truecolorTile

        googleMap = map
        map.mapType = GoogleMap.MAP_TYPE_SATELLITE
        drawFarmPolygon(map)

        getNdviFromAPI()


    }

    private fun getNdviFromAPI() {
        myFarm?.id?.let {
            viewModel.getNdvi(it).observe(viewLifecycleOwner) {

                when(it){
                    is Resource.Success ->{
                        ndviDataList = it.data?.data
                            ?.filter { ndviData -> ndviData.tile?.ndvi != null }

                        val datesList: List<String?> = ndviDataList
                            ?.map { data -> changeDateFormat(data.dt) } ?: mutableListOf()

                        val arrayAdapter =
                            ArrayAdapter(requireContext(), R.layout.item_spinner, datesList)
                        binding.dateSpinner.adapter = arrayAdapter
                        binding.dateSpinner.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    p0: AdapterView<*>?,
                                    p1: View?,
                                    position: Int,
                                    p3: Long
                                ) {
                                    binding.dateSpinner.isEnabled = false
                                    Handler(Looper.myLooper()!!).postDelayed({
                                      binding.dateSpinner.isEnabled = true
                                    },1000)
                                    selectedNdvi = ndviDataList?.get(position)
                                    showTileNDVI(selectedNdvi)

                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {

                                }
                            }
                    }
                    is Resource.Error ->{
                        ToastStateHandling.toastWarning(requireContext(),"No NDVI available. Please try after sometime.",Toast.LENGTH_SHORT)
                    }
                    is Resource.Loading ->{}
                }


            }
        }


    }

    private fun changeDateFormat(dt: Long?): String {
        val simpleDateFormat = SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
        val date = Date(dt?.times(1000) ?: 0)
        Log.d("NDVI", "dateFormat: $date")
        return simpleDateFormat.format(date)
    }


    private fun showTileNDVI(selectedNdvi:NdviData?) {
        if (tileOverlay != null) {
            tileOverlay?.remove()
        }
        if (selectedNdvi != null) {
//            binding.ndviMean.text = String.format("%.2f", selectedNdvi?.meanNdvi)
            binding.cardView2.visibility = View.GONE
            if (selectedNdvi.cl != null)
                if (selectedNdvi.cl!! > 85) {
                    viewModel.viewModelScope.launch {
                        binding.tvTextAlert.text = TranslationsManager().getString("str_cloud_coverage")
                            binding.cardView2.visibility = View.VISIBLE
                    }
                }

            selectedNdvi.stats?.ndvi?.let {
                viewModel.getNdviMean(it).observe(viewLifecycleOwner) { it1 ->
                    binding.ndviMean.text = if(it1?.data?.mean==null) "--" else String.format("%.2f",if(it1?.data?.mean!! > 1.0) 1.0 else it1?.data?.mean)
                }
            }

            val tileUrl: String? = if (selectedTileType == TileType.NDVI) {
                selectedNdvi.tile?.ndvi
            } else {
                selectedNdvi.tile?.truecolor
            }
            if (tileUrl.isNullOrEmpty() || !URLUtil.isValidUrl(tileUrl)) {
                Log.d("g56", "NDVI Url: $tileUrl")
//                ToastStateHandling.toastError(
//                    requireContext(),
//                    "Image Not Available",
//                    Toast.LENGTH_LONG
                //)
                binding.ndviMean.visibility = View.GONE
                binding.cardView2.visibility = View.VISIBLE
                binding.tvTextAlert.text = "Image Not Available"
                binding.tabLayout.visibility = View.GONE
                binding.cardView3.visibility = View.GONE
            } else {
                val tileProvider: TileProvider = object : UrlTileProvider(256, 256) {
                    override fun getTileUrl(x: Int, y: Int, zoom: Int): URL {
                        var url: String = if (selectedTileType == TileType.NDVI) {
                            "${selectedNdvi.tile?.ndvi}&paletteid=4"
                        } else {
                            "${selectedNdvi.tile?.truecolor}"
                        }
                        url = url.replace("{x}", x.toString() + "")
                        url = url.replace("{y}", y.toString() + "")
                        url = url.replace("{z}", zoom.toString() + "")
                        Log.d("g56", "NDVI Url: $url")
                        return try {
                            URL(url)
                        } catch (e: MalformedURLException) {
                            URL("")
//                            throw java.lang.AssertionError(e)
                        }
                    }
                }
                if (googleMap != null) tileOverlay = googleMap?.addTileOverlay(
                    TileOverlayOptions()
                        .tileProvider(tileProvider)
                )
                binding.ndviMean.visibility = View.VISIBLE
                binding.tabLayout.visibility = View.VISIBLE
                binding.cardView3.visibility = View.VISIBLE
            }
        }
    }


    private fun drawFarmPolygon(mMap: GoogleMap?) {
        if (myFarm != null) {
            val points = myFarm?.farmJson
            if (points != null) {
                if (points.size >= 3) {
                    if (polygon != null)
                        polygon!!.remove()
                    polygon = mMap?.addPolygon(
                        PolygonOptions().addAll(points).fillColor(Color.argb(0, 58, 146, 17))
                            .strokeColor(
                                Color.argb(255, 255, 255, 255)
                            )
                    )
                }
                getLatLnBounds(points)?.let {
                    CameraUpdateFactory.newLatLngBounds(
                        it, 50
                    )
                }?.let {
                    mMap?.animateCamera(
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

//    private fun getArea(latLngs: List<LatLng?>?): Double {
//        return SphericalUtil.computeArea(latLngs)
//    }

    fun opacity() {

        binding.slider.setLabelFormatter(LabelFormatter { value ->
            Math.round(value).toString() + "%"
        })
        binding.slider.addOnChangeListener(Slider.OnChangeListener { slider: Slider?, value: Float, fromUser: Boolean ->
            if (tileOverlay != null) {
                Log.d("g56", "slider:$value")
                tileOverlay?.transparency = if (value == 100f) 0f else 1 - value / 100f
            }
        })
    }

    private fun translation() {
        TranslationsManager().loadString("str_date", binding.textView8, "Date")
        TranslationsManager().loadString("str_low", binding.textView3, "Low")
        TranslationsManager().loadString("str_high", binding.textView4, "High")
        TranslationsManager().loadString("str_unhealthy", binding.unhealthy, "Unhealthy")
        TranslationsManager().loadString("moderately_healthy", binding.moderate, "Moderately Healthy")
        TranslationsManager().loadString("healthy", binding.healthy, "Healthy")
        TranslationsManager().loadString("mean_ndvi", binding.textView5, "Mean NDVI")
        TranslationsManager().loadString("opacity", binding.textView6, "Opacity")
        TranslationsManager().loadString(
            "str_cloud_coverage",
            binding.tvTextAlert,
            "This Satellite Image has high Cloud Cover. This Imagery may not be an accurate representation of Crop Health."
        )

        viewModel.viewModelScope.launch {
            val title = TranslationsManager().getString("str_ndvi")
            binding.topAppBar.title = title
        }


    }

    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("NdviFragment")
    }
}
