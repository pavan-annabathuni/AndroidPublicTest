package com.example.ndvi

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ndvi.adapter.DateAdapter
import com.example.ndvi.databinding.FragmentNdviBinding
import com.example.ndvi.viewModel.NdviViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.gms.maps.model.TileProvider
import com.google.android.gms.maps.model.UrlTileProvider
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.tabs.TabLayout
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class NdviFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentNdviBinding
    private val viewModel: NdviViewModel by lazy {
        ViewModelProvider(this)[NdviViewModel::class.java]
    }
    private lateinit var googleMap: GoogleMap
    var languages = arrayOf("23/8/22", "24/8/22", "25/8/22", "26/8/22", "27/8/22", "28/8/22")
    val NEW_SPINNER_ID = 1
    var cal = Calendar.getInstance()
    private var tileOverlayTransparent: TileOverlay? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNdviBinding.inflate(inflater)
        val mapFragment: SupportMapFragment = childFragmentManager.findFragmentById(R.id.Map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val arrayAdapter = ArrayAdapter<String>(requireContext(),R.layout.item_spinner,languages)
        binding.dateSpinner.adapter =arrayAdapter
        binding.dateSpinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        onClicks()
        tabs()
        observer()
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()

            }
        }
//        binding.date.setOnClickListener(){
//            DatePickerDialog(requireContext(),
//                dateSetListener,
//                // set DatePickerDialog to point to today's date when it loads up
//                cal.get(Calendar.YEAR),
//                cal.get(Calendar.MONTH),
//                cal.get(Calendar.DAY_OF_MONTH)).show()
//        }

        binding.slider.setLabelFormatter(LabelFormatter { value ->
            Math.round(value).toString() + "%"
        })
        binding.slider.addOnChangeListener { slider, value, fromUser ->
            tileOverlayTransparent?.setTransparency(
                (if (value == 100f) 0 else 1 - value / 100).toFloat().toInt().toFloat()
            )

        }

        binding.recycleView.adapter = DateAdapter()
        return binding.root
    }

    private fun onClicks() {
    }

    private fun tabs() {

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Vegetation Index").setCustomView(R.layout.item_tab))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("True Color").setCustomView(R.layout.item_tab))
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
               when(binding.tabLayout.selectedTabPosition){
                   0-> {
                      // Toast.makeText(context, "WORKED", Toast.LENGTH_SHORT).show()
                       onMapReady(googleMap)
                   }
                   1-> {
                      // Toast.makeText(context, "WORKED2", Toast.LENGTH_SHORT).show()
                       onMapReady(googleMap)
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
        viewModel.getNdvi(1, 1).observe(viewLifecycleOwner) {
           // val url = it.data?.data?.get(0)?.truecolorTile
            var url = "http://api.agromonitoring.com/tile/1.0/{z}/{x}/{y}/120637eb400/634b7093176fe62ecc43f143?appid=248c44ddf25114728e9aceff0f59b219"
            url = (it.data?.data?.get(0)?.ndviTile+"&paletteid=4")
            map?.mapType = GoogleMap.MAP_TYPE_TERRAIN
            map.let {
                googleMap = it!!

                var tileProvider: TileProvider = object : UrlTileProvider(256, 256) {
                    override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {

                        /* Define the URL pattern for the tile images */
//                        http://api.agromonitoring.com/tile/1.0/17/93851/60792/120638be300/634b7030e8d9ac678e1722d4?appid=248c44ddf25114728e9aceff0f59b219&paletteid=40

                        url?.replace("{z}", "${zoom}")
                        url?.replace("{x}", "${x}")
                        url?.replace("{y}", "${y}")
                        Log.d("g56", "NDVI Url: $url")
                        try {
                            return URL(url)
                        } catch (e: MalformedURLException) {
                            throw AssertionError(e)
                        }
                    }

                    /*
                 * Check that the tile server supports the requested x, y and zoom.
                 * Complete this stub according to the tile range you support.
                 * If you support a limited range of tiles at different zoom levels, then you
                 * need to define the supported x, y range at each zoom level.
                 */
                    private fun checkTileExists(x: Int, y: Int, zoom: Int): Boolean {
                        val minZoom = 12
                        val maxZoom = 16
                        return zoom in minZoom..maxZoom
                    }

                }

                tileOverlayTransparent = googleMap.addTileOverlay(
                    TileOverlayOptions()
                        .tileProvider(tileProvider)
                )

                // tileOverlayTransparent.remove()
            }
        }
    }

        private fun updateDateInView() {
            val myFormat = "dd/MM/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.ENGLISH)
            // binding.date.text = sdf.format(cal.getTime())
        }

        fun observer() {
            viewModel.getNdvi(1, 1).observe(viewLifecycleOwner) {
                binding.slider.value = it.data?.data?.get(0)?.meanNdvi?.toFloat() ?: 0.0.toFloat()
                Log.d("Ndvi", "observer: ${it.data.toString()}")
                binding.ndviMean.text = it.data?.data?.get(0)?.meanNdvi?.toString()
            }
        }
    }
