package com.example.ndvi

import android.animation.LayoutTransition
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.ndvi.adapter.DateAdapter
import com.example.ndvi.databinding.FragmentNdviBinding
import com.example.ndvi.viewModel.NdviViewModel
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.*
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.Slider
import com.google.android.material.tabs.TabLayout
import com.google.maps.android.PolyUtil
import com.waycool.data.Network.NetworkModels.NdviData
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.translations.TranslationsManager
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

    lateinit var ndviTile: String
    lateinit var trueColor: String
    private var accountId: Int? = null

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
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNdviBinding.inflate(inflater)
        binding.farmName.text = myFarm?.farmName

        val mapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.map_ndvi) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            if (accountId == null) {
                accountId = it.data?.accountId
                getNdviFromAPI()
            }
        }

        binding.roolLlNdvi.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

//        viewModel.getNdvi(myFarm?.id, 2).observe(viewLifecycleOwner) {
//            Log.d("MapUrl", "onMapReady: ${it.data?.data?.get(0)?.ndviTile}")
//             ndviTile = it.data?.data?.get(0)?.ndviTile+"&paletteid=4"
//             trueColor = it.data?.data?.get(0)?.truecolorTile.toString()
//
//
//            //cloud data
//            val cloud = it.data?.data?.get(0)?.cloudCoverage?.toInt()
//            if(cloud!=null)
//            if(cloud < 30){
//            val dialog = BottomSheetDialog(this.requireContext(), R.style.BottomSheetDialog)
//            dialog.setContentView(R.layout.item_cloud)
//            val close = dialog.findViewById<ImageView>(R.id.img_close)
//            close?.setOnClickListener(){
//                dialog.dismiss()
//            }
//        }}

        onClicks()
        tabs()
//        observer()
//        spinner()
        opacity()
        translation()


//        binding.slider.setLabelFormatter(LabelFormatter { value ->
//            Math.round(value).toString() + "%"
//        })
//        binding.slider.addOnChangeListener { slider, value, fromUser ->
//            tileOverlayTransparent?.setTransparency(
//                (if (value == 100f) 0 else 1 - value / 100).toFloat().toInt().toFloat()
//            )
//
//        }

        binding.recycleView.adapter = DateAdapter()
        return binding.root
    }

    private fun onClicks() {
        binding.floatingActionButton1.setOnClickListener() {
            this.findNavController().navigate(R.id.action_ndviFragment_to_infoSheetFragment)
        }
        binding.topAppBar.setOnClickListener() {
            this.findNavController().navigateUp()
        }

        binding.floatingActionButton2.setOnClickListener {
            if (myFarm != null && googleMap != null) {
                val points = myFarm?.farmJson as ArrayList
                googleMap?.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        getLatLnBounds(points),
                        50
                    )
                )
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
                        selectedTileType = TileType.NDVI
                        showTileNDVI()
                    }
                    1 -> {
                        selectedTileType = TileType.TRUE_COLOR
                        showTileNDVI()
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

    override fun onMapReady(map: GoogleMap?) {
        // val url = it.data?.data?.get(0)?.truecolorTile

        googleMap = map
        map?.mapType = GoogleMap.MAP_TYPE_SATELLITE
        drawFarmPolygon(map)

        getNdviFromAPI()

//        map.let { itt ->
//            googleMap = itt!!
//            var tileProvider: TileProvider = object : UrlTileProvider(256, 256) {
//                override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {
//                    var url: String?
//                    url =
//                        "http://api.agromonitoring.com/tile/1.0/{z}/{x}/{y}/020635f1000/639b20b9dfcf2290ab07ef4d?appid=b1503a7f8fcdbd96d5fd399fac9eb1a6&paletteid=4"
////                        if(selectedTileType == TileType.NDVI) {
////                           // url = "http://api.agromonitoring.com/tile/1.0/{z}/{x}/{y}/120636aed80/6391d44c50d9ff43ef5568a1?appid=071e58db72985a51f8f5da4ab1969561&paletteid=4"
////                         //   url = "http://api.agromonitoring.com/tile/1.0/{z}/{x}/{y}/020635f1000/639b20b9dfcf2290ab07ef4d?appid=b1503a7f8fcdbd96d5fd399fac9eb1a6&paletteid=4"
////                        }else{
////                           url = trueColor
////                       }
//                    url = url?.replace("{z}", "${zoom}")
//                    url = url?.replace("{x}", "${x}")
//                    url = url?.replace("{y}", "${y}")
//                    Log.d("g56", "NDVI Url: $url")
//                    try {
//                        return URL(url)
//                    } catch (e: MalformedURLException) {
//                        throw AssertionError(e)
//                    }
//                }
//
//                private fun checkTileExists(x: Int, y: Int, zoom: Int): Boolean {
//                    val minZoom = 12
//                    val maxZoom = 16
//                    return zoom in minZoom..maxZoom
//                }
//
//            }
//
//            if (tileOverlay != null)
//                tileOverlay?.remove()
//
//            tileOverlay = googleMap.addTileOverlay(
//                TileOverlayOptions()
//                    .tileProvider(tileProvider)
//            )
//
//            // tileOverlayTransparent.remove()
//        }


    }

    private fun getNdviFromAPI() {
        myFarm?.id?.let {
            accountId?.let { it1 ->
                viewModel.getNdvi(it, it1).observe(viewLifecycleOwner) {

                    ndviDataList = it?.data?.data
                        ?.filter { ndviData -> ndviData.dt != null }

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
                                selectedNdvi = ndviDataList?.get(position)
                                showTileNDVI()

                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
                        }
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


    private fun showTileNDVI() {
        if (tileOverlay != null) {
            tileOverlay?.remove()
        }
        if (selectedNdvi != null) {
//            binding.ndviMean.text = String.format("%.2f", selectedNdvi?.meanNdvi)
            binding.cardView2.visibility = View.GONE
            if (selectedNdvi?.cl != null)
                if (selectedNdvi?.cl!! > 85) {
                    binding.cardView2.visibility = View.VISIBLE
                    binding.tvTextAlert.text =
                        "The Cloud Cover for Satellite Image is ${selectedNdvi?.cl}%. This Imagery may not be an accurate representation of Crop Health."
                }

            selectedNdvi?.stats?.ndvi?.let {
                viewModel.getNdviMean(it).observe(viewLifecycleOwner) {it1->
                    binding.ndviMean.text = String.format("%.2f", it1?.data?.mean)
                }
            }

            val tileProvider: TileProvider = object : UrlTileProvider(256, 256) {
                override fun getTileUrl(x: Int, y: Int, zoom: Int): URL {
                    var url: String = if (selectedTileType == TileType.NDVI) {
                        "${selectedNdvi?.tile?.ndvi}&paletteid=4"
                    } else {
                        "${selectedNdvi?.tile?.truecolor}"
                    }
                    url = url.replace("{x}", x.toString() + "")
                    url = url.replace("{y}", y.toString() + "")
                    url = url.replace("{z}", zoom.toString() + "")
                    Log.d("g56", "NDVI Url: $url")
                    return try {
                        URL(url)
                    } catch (e: MalformedURLException) {
                        throw java.lang.AssertionError(e)
                    }
                }
            }
            if (googleMap != null) tileOverlay = googleMap?.addTileOverlay(
                TileOverlayOptions()
                    .tileProvider(tileProvider)
            )
        }
    }


//    fun observer() {
//        viewModel.getNdvi(1, 2).observe(viewLifecycleOwner) {
//            // binding.slider.value = it.data?.data?.get(0)?.meanNdvi?.toFloat() ?: 0.0.toFloat()
//            Log.d("Ndvi", "observer: ${it.data.toString()}")
//            binding.ndviMean.text = it.data?.data?.get(0)?.meanNdvi?.toString()
//        }
//    }
//
//    private fun spinner() {
//        viewModel.getNdvi(1, 2).observe(viewLifecycleOwner) {
//            val list: MutableList<String?> = (it?.data?.data
//                ?.filter { ndviData -> ndviData.tileDate != null }
//                ?.map { data -> data.tileDate } ?: mutableListOf()) as MutableList<String?>
//
//            val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_spinner, list)
//            binding.dateSpinner.adapter = arrayAdapter
//            binding.dateSpinner.onItemSelectedListener =
//                object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(
//                        p0: AdapterView<*>?,
//                        p1: View?,
//                        p2: Int,
//                        p3: Long
//                    ) {
//
//                        // onMapReady(googleMap)
//                    }
//
//                    override fun onNothingSelected(parent: AdapterView<*>?) {
//
//                    }
//                }
//        }
//    }

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
                mMap?.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        getLatLnBounds(points), 50
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
        TranslationsManager().loadString("str_date", binding.textView8)
        TranslationsManager().loadString("str_low", binding.textView3)
        TranslationsManager().loadString("str_high", binding.textView4)
        TranslationsManager().loadString("str_unhealthy", binding.unhealthy)
        TranslationsManager().loadString("moderately_healthy", binding.moderate)
        TranslationsManager().loadString("healthy", binding.healthy)
        TranslationsManager().loadString("mean_ndvi", binding.textView5)
        TranslationsManager().loadString("opacity", binding.textView6)

        viewModel.viewModelScope.launch() {
            val title = TranslationsManager().getString("str_ndvi")
            binding.topAppBar.title = title
        }

        binding.topAppBar.setOnClickListener() {
            this.findNavController().navigateUp()
        }

    }
}
