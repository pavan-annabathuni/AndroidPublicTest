package com.example.adddevice.fragments

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.adddevice.R
import com.example.adddevice.databinding.FragmentAddDeviceBinding
import com.example.adddevice.viewmodel.AddDeviceViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.gms.maps.model.TileProvider
import com.google.android.gms.maps.model.UrlTileProvider
import com.google.zxing.integration.android.IntentIntegrator
import com.waycool.data.utils.Resource
import java.net.MalformedURLException
import java.net.URL


class AddDeviceFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentAddDeviceBinding? = null
    private val binding get() = _binding!!
//    private lateinit var binding: FragmentAddDeviceBinding
    private lateinit var googleMap: GoogleMap
    private var tileOverlayTransparent: TileOverlay? = null
    var spinner1 = arrayOf("Outgrow GWX")
    var spinner2 = arrayOf("GWX 007","GWX 008")
    private var accountID: Int? = null
    var contactNumber:String=""
    private val viewModel by lazy { ViewModelProvider(this)[AddDeviceViewModel::class.java] }
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
//        binding = FragmentAddDeviceBinding.inflate(inflater)
        _binding = FragmentAddDeviceBinding.inflate(inflater, container, false)

        spinners()
        binding.topAppBar.setNavigationOnClickListener(){
            this.findNavController().navigateUp()
        }
        scanner()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val mapFragment: SupportMapFragment = childFragmentManager.findFragmentById(R.id.Map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
        viewModel.getUserDetails().observe(viewLifecycleOwner) {
//                    itemClicked(it.data?.data?.id!!, lat!!, long!!, onp_id!!)
//                    account=it.data.account
            contactNumber= it.data?.phone.toString()
            binding. mobileNo.text="+91 $contactNumber"
            accountID=it.data?.accountId

        }
        binding.submit.setOnClickListener {
            activityDevice(11,"867542059649031")
        }
    }

    private fun activityDevice(account:Int,serial_no:String) {
        //867542059649031
        val map = mutableMapOf<String, Any>()
        map.put("account_no",1)
        map.put("device_lat",12.33)
        map.put("device_long",77.55)
        map.put("serial_no",serial_no)
        map.put("device_name","praveen")
        map.put("device_elevation",68)
        map.put("device_version","praaf")
        viewModel.activateDevice(map).observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    activity?.finish()
                }
                is Resource.Error -> {
                    Toast.makeText(
                        requireContext(),
                        it.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(
                        ContentValues.TAG,
                        "postAddCropExption: ${it.message.toString()}"
                    )
                }
                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT)
                        .show()

                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.mapType = GoogleMap.MAP_TYPE_TERRAIN
        map.let {
            googleMap = it!!

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

            tileOverlayTransparent = googleMap.addTileOverlay(
                TileOverlayOptions()
                    .tileProvider(tileProvider)
            )
        }

    }
    private fun spinners(){
        val arrayAdapter = ArrayAdapter<String>(requireContext(),R.layout.item_spinner,spinner1)
        binding.spinner1.adapter =arrayAdapter
        val arrayAdapter2 = ArrayAdapter<String>(requireContext(),R.layout.item_spinner,spinner2)
        binding.spinner3.adapter =arrayAdapter2
    }
    private fun scanner(){
        binding.btScanner.setOnClickListener(){
            val intentIntegrator = IntentIntegrator.forSupportFragment(this)
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            intentIntegrator.setPrompt("Scan")
            intentIntegrator.setBeepEnabled(true)
            intentIntegrator.captureActivity

//            intentIntegrator.setOrientationLocked(false)
            intentIntegrator.initiateScan()
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?
    ) {

        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        Log.d("scanner", "onActivityResult: $intentResult ")
        if (intentResult != null) {
            if (intentResult.contents == null) {
                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
            } else {
                // if the intentResult is not null we'll set
                // the content and format of scan message
                Toast.makeText(context, "Scanned ${intentResult.contents}", Toast.LENGTH_SHORT).show()
//                messageText.setText(intentResult.contents)
//                messageFormat.setText(intentResult.formatName)
            }
        } else {
            // the content and format of scan message
            Toast.makeText(context, "Scanned", Toast.LENGTH_SHORT).show()
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}