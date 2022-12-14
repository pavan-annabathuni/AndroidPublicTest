package com.example.ndvi

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.MalformedURLException
import java.net.URL


class NdviFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentNdviBinding
    private val viewModel: NdviViewModel by lazy {
        ViewModelProvider(this)[NdviViewModel::class.java]
    }
    private lateinit var googleMap: GoogleMap
    private var farmjson: String? = null
    private enum class TileType {
        NDVI, TRUE_COLOR
    }
    lateinit var ndviTile:String
    lateinit var trueColor:String

    private var selectedTileType = TileType.NDVI
    private var tileOverlayTransparent: TileOverlay? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (arguments != null) {
                farmjson = arguments?.getString("farm_json")
              //  farmCentroid = arguments?.getString("farm_center")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNdviBinding.inflate(inflater)
        val mapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.map_ndvi) as SupportMapFragment
        mapFragment.getMapAsync(this)
        viewModel.getNdvi(3, 3).observe(viewLifecycleOwner) {
            Log.d("MapUrl", "onMapReady: ${it.data?.data?.get(0)?.ndviTile}")
             ndviTile = it.data?.data?.get(0)?.ndviTile+"&paletteid=4"
             trueColor = it.data?.data?.get(0)?.truecolorTile.toString()
        }
        onClicks()
        tabs()
        observer()
        spinner()
        opacity()



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
        binding.floatingActionButton2.setOnClickListener() {
        }

    }

    private fun tabs() {

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Vegetation Index").setCustomView(R.layout.item_tab))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("True Color").setCustomView(R.layout.item_tab))
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
               when(binding.tabLayout.selectedTabPosition){
                   0-> {

                       selectedTileType = TileType.NDVI
                       Log.d("select", "onTabSelected: $selectedTileType")
                   }
                   1-> {
                      // Toast.makeText(context, "WORKED2", Toast.LENGTH_SHORT).show()
                       selectedTileType = TileType.TRUE_COLOR
                       Log.d("select", "onTabSelected: $selectedTileType")
                       Toast.makeText(context, "WORKED", Toast.LENGTH_SHORT).show()
                   }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                onMapReady(googleMap)
                googleMap.clear()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }
    override fun onMapReady(map: GoogleMap?) {
           // val url = it.data?.data?.get(0)?.truecolorTile

            map?.mapType = GoogleMap.MAP_TYPE_SATELLITE
            polyGone(map)

            map.let {itt->
                googleMap = itt!!
                var tileProvider: TileProvider = object : UrlTileProvider(256, 256) {
                    override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {
                        var url: String?
                        //   url = "http://api.agromonitoring.com/tile/1.0/{z}/{x}/{y}/1206393cc00/634b70b6176fe6885343f144?appid=248c44ddf25114728e9aceff0f59b219&paletteid=4"
                        if(selectedTileType == TileType.NDVI) {
                           // url = "http://api.agromonitoring.com/tile/1.0/{z}/{x}/{y}/120636aed80/6391d44c50d9ff43ef5568a1?appid=071e58db72985a51f8f5da4ab1969561&paletteid=4"
                            url = "http://api.agromonitoring.com/tile/1.0/{z}/{x}/{y}/1206393cc00/634b70b6176fe6885343f144?appid=248c44ddf25114728e9aceff0f59b219&paletteid=4"
                        }else{
                           url = trueColor
                       }
                       url= url?.replace("{z}", "${zoom}")
                        url=url?.replace("{x}", "${x}")
                        url=url?.replace("{y}", "${y}")
                        Log.d("g56", "NDVI Url: $url")
                        try {
                            return URL(url)
                        } catch (e: MalformedURLException) {
                            throw AssertionError(e)
                        }
                    }

                    private fun checkTileExists(x: Int, y: Int, zoom: Int): Boolean {
                        val minZoom = 12
                        val maxZoom = 16
                        return zoom in minZoom..maxZoom
                    }

                }

                if(tileOverlayTransparent!=null)
                    tileOverlayTransparent?.remove()

                tileOverlayTransparent = googleMap.addTileOverlay(
                    TileOverlayOptions()
                        .tileProvider(tileProvider)
                )

                // tileOverlayTransparent.remove()
            }


    }


        fun observer() {
            viewModel.getNdvi(3, 3).observe(viewLifecycleOwner) {
               // binding.slider.value = it.data?.data?.get(0)?.meanNdvi?.toFloat() ?: 0.0.toFloat()
                Log.d("Ndvi", "observer: ${it.data.toString()}")
                binding.ndviMean.text = it.data?.data?.get(0)?.meanNdvi?.toString()
            }
        }
            private fun spinner(){
                viewModel.getNdvi(3, 3).observe(viewLifecycleOwner) {
                val list: MutableList<String?> = (it?.data?.data
                    ?.filter { ndviData -> ndviData.tileDate!=null }
                    ?.map { data -> data.tileDate }?: mutableListOf()) as MutableList<String?>

                val arrayAdapter = ArrayAdapter(requireContext(),R.layout.item_spinner, list)
                binding.dateSpinner.adapter = arrayAdapter
                binding.dateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            p2: Int,
                            p3: Long
                        ) {

                           // onMapReady(googleMap)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                    }
            }}

    fun polyGone(mMap: GoogleMap?){
        farmjson = "[{\"latitude\":20.13548870253035,\"longitude\":74.05694760382175},{\"latitude\":20.136933556503628,\"longitude\":74.05710987746716},{\"latitude\":20.137414856745288,\"longitude\":74.05718564987183},{\"latitude\":20.137414856745288,\"longitude\":74.05754171311855},{\"latitude\":20.137073949009807,\"longitude\":74.05756786465645},{\"latitude\":20.136801977762257,\"longitude\":74.05752763152122},{\"latitude\":20.136450366084365,\"longitude\":74.05747465789318},{\"latitude\":20.135871165176347,\"longitude\":74.05748907476664},{\"latitude\":20.13552112532596,\"longitude\":74.05743677169085},{\"latitude\":20.134875186421276,\"longitude\":74.05727550387383},{\"latitude\":20.134367121488765,\"longitude\":74.05725672841072},{\"latitude\":20.133880775256966,\"longitude\":74.05717693269253},{\"latitude\":20.133956639073013,\"longitude\":74.0565100684762},{\"latitude\":20.13527874115715,\"longitude\":74.05658517032862},{\"latitude\":20.13548870253035,\"longitude\":74.05694760382175}]"
        if (farmjson != null) {
            val points = convertStringToLatLnList(farmjson)
            if (points != null) {
                if (points.size >= 3) {
                    mMap?.addPolygon(
                        PolygonOptions().addAll(points).fillColor(Color.argb(0, 58, 146, 17))
                            .strokeColor(
                                Color.argb(255, 255, 255, 255)
                            )
                    )
                }
//                for (latLng in points) {
//                    val marker = mMap!!.addMarker(
//                        MarkerOptions().position(
//                            latLng!!
//                        )
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_green))
//                            .anchor(0.5f, .5f)
//                            .draggable(false)
//                            .flat(true)
//                    )
//                }
                mMap?.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        getLatLnBounds(points), 50
                    )
                )
//                val area: Double =
//                    getArea(points) / 4046.86
               // binding.farmareaEtAddfarm.setText((String.format("%.2f", area)).trim { it <= ' ' })
            }
        }
    }

    fun convertStringToLatLnList(s: String?): List<LatLng?>? {
        val listType = object : TypeToken<List<LatLng?>?>() {}.type
        return Gson().fromJson(s, listType)
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

    fun opacity(){

        binding.slider.setLabelFormatter(LabelFormatter { value ->
            Math.round(value).toString() + "%"
        })
        binding.slider.addOnChangeListener(Slider.OnChangeListener { slider: Slider?, value: Float, fromUser: Boolean ->
            if (tileOverlayTransparent != null) {
                Log.d("g56", "slider:$value")
                tileOverlayTransparent?.transparency = if (value == 100f) 0f else 1 - value / 100f
            }
        })
    }

    }
