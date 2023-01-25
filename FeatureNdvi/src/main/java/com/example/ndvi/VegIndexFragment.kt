package com.example.ndvi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ndvi.databinding.FragmentVegIndexBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import java.net.MalformedURLException
import java.net.URL


class VegIndexFragment : Fragment(),OnMapReadyCallback{
    private lateinit var binding: FragmentVegIndexBinding
    private lateinit var googleMap: GoogleMap
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
        binding = FragmentVegIndexBinding.inflate(inflater)


        return binding.root

    }


    override fun onMapReady(map: GoogleMap?) {
        googleMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
        googleMap.let {
         googleMap = it
            val mapFragment:SupportMapFragment = childFragmentManager.findFragmentById(R.id.map_ndvi) as SupportMapFragment
            mapFragment.getMapAsync(this)
            var tileProvider: TileProvider = object : UrlTileProvider(256, 256) {
                override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {

                    /* Define the URL pattern for the tile images */
                    val url = "http://my.image.server/images/$zoom/$x/$y.png"
                    return if (!checkTileExists(x, y, zoom)) {
                        null
                    } else try {
                        URL(url)
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
            var tileOverlay = googleMap.addTileOverlay(
                TileOverlayOptions()
                    .tileProvider(tileProvider)
            )
            tileOverlay.remove()
}
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("VegIndexFragment")
    }
}

