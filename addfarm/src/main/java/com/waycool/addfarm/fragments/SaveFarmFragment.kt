package com.waycool.addfarm.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.SphericalUtil
import com.waycool.addfarm.FarmsViewModel
import com.waycool.addfarm.R
import com.waycool.addfarm.adapter.SelectCropAdapter
import com.waycool.addfarm.databinding.FragmentSaveFarmBinding
import com.waycool.data.Local.LocalSource
import com.waycool.data.repository.domainModels.MyCropDataDomain
import kotlinx.coroutines.launch

class SaveFarmFragment : Fragment(), OnMapReadyCallback {

    private var waterSourcesSelected: MutableList<String> = mutableListOf()
    private var farmjson: String? = null
    private var farmCentroid: String? = null
    private val binding by lazy { FragmentSaveFarmBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this)[FarmsViewModel::class.java] }
    private var accountId: Int? = null
    private var checkedCropList: MutableList<MyCropDataDomain>? = null

    private val myCropsAdapter: SelectCropAdapter by lazy { SelectCropAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            callback
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarTitle.text = "Farm Details"
        binding.toolbar.setNavigationOnClickListener { activity?.finish() }

        (childFragmentManager.findFragmentById(R.id.map_save_fragment) as SupportMapFragment?)?.getMapAsync(
            this
        )

        if (arguments != null) {
            farmjson = arguments?.getString("farm_json")
            farmCentroid = arguments?.getString("farm_center")
        }

        binding.mycropsRv.adapter = myCropsAdapter

        viewModel.viewModelScope.launch {
            accountId = LocalSource.getUserDetailsEntity()?.accountId

        }



        binding.riverSource.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            if (b && !waterSourcesSelected?.contains("River")!!) {
                waterSourcesSelected?.add("River")
            } else if (!b) waterSourcesSelected?.remove("River")
        }

        binding.canalSource.setOnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->
            if (b && !waterSourcesSelected?.contains("Canal")!!) {
                waterSourcesSelected?.add("Canal")
            } else if (!b) waterSourcesSelected?.remove("Canal")
        }
        binding.rainSource.setOnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->
            if (b && !waterSourcesSelected?.contains("Rain")!!) {
                waterSourcesSelected?.add("Rain")
            } else if (!b) waterSourcesSelected?.remove("Rain")
        }
        binding.lakeSource.setOnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->
            if (b && !waterSourcesSelected?.contains("Lake")!!) {
                waterSourcesSelected?.add("Lake")
            } else if (!b) waterSourcesSelected?.remove("Lake")
        }
        binding.borewellSource.setOnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->
            if (b && !waterSourcesSelected?.contains("Borewell")!!) {
                waterSourcesSelected?.add("Borewell")
            } else if (!b) waterSourcesSelected?.remove("Borewell")
        }


        binding.saveFarmBtn.setOnClickListener {
            if (accountId != null) {
                binding.saveProgressBar.visibility = View.VISIBLE
                binding.saveFarmBtn.visibility = View.INVISIBLE

                var checkedcrops: String? = null
                if (checkedCropList != null && checkedCropList!!.isNotEmpty()) {
                    val plotIdsList:MutableList<String> = mutableListOf()
                    for(crop in checkedCropList!!){
                        plotIdsList.add(crop.id.toString())
                    }
                    checkedcrops = Gson().toJson(plotIdsList)
                } else checkedcrops = null

                var watersources:String?=null
                if(waterSourcesSelected.isNotEmpty()){
                    watersources=Gson().toJson(waterSourcesSelected)
                }else watersources=null

                viewModel.addFarm(
                    accountId!!,
                    binding.farmnameEtAddfarm.text.toString(),
                    farmCentroid!!,
                    binding.farmareaEtAddfarm.text.toString(),
                    farmjson!!,
                   checkedcrops,
                    binding.setPrimaryFarm.isChecked,
                    watersources
                ).observe(viewLifecycleOwner) {
                    binding.saveProgressBar.visibility = View.INVISIBLE
                    binding.saveFarmBtn.visibility = View.VISIBLE

                    accountId?.let { it1 ->
                        viewModel.getMyCrop2(it1).observe(viewLifecycleOwner) {}
                    }

                    Toast.makeText(requireContext(), "Farm Saved", Toast.LENGTH_SHORT).show()
                    activity?.finish()
                }
            }
        }

        if (accountId != null)
            viewModel.getMyCrop2(accountId!!).observe(viewLifecycleOwner) {
                val croplist:MutableList<MyCropDataDomain> = mutableListOf()
                for(crop in it.data!!){
                    if(crop.farmId==null){
                        croplist.add(crop)
                    }
                }
                if(croplist.isEmpty()){
                    binding.mycropsRv.visibility=View.GONE
                    binding.textView2.visibility=View.GONE
                }else{
                    binding.mycropsRv.visibility=View.VISIBLE
                    binding.textView2.visibility=View.VISIBLE
                    myCropsAdapter.submitList(croplist)
                }
            }
        myCropsAdapter.onItemClick = {
            if (it != null) {
                checkedCropList = it
            }
        }
    }

    override fun onMapReady(mMap: GoogleMap?) {
        mMap?.mapType = GoogleMap.MAP_TYPE_HYBRID

        if (farmjson != null) {
            val points = convertStringToLatLnList(farmjson)
            if (points != null) {
                if (points.size >= 3) {
                    mMap?.addPolygon(
                        PolygonOptions().addAll(points).fillColor(Color.argb(100, 58, 146, 17))
                            .strokeColor(
                                Color.argb(255, 255, 255, 255)
                            )
                    )
                }
                for (latLng in points) {
                    val marker = mMap!!.addMarker(
                        MarkerOptions().position(
                            latLng!!
                        )
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_green))
                            .anchor(0.5f, .5f)
                            .draggable(false)
                            .flat(true)
                    )
                }
                mMap?.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        getLatLnBounds(points), 50
                    )
                )
                val area: Double =
                    getArea(points) / 4046.86
                binding.farmareaEtAddfarm.setText((String.format("%.2f", area)).trim { it <= ' ' })
            }
        }
    }

    private fun getArea(latLngs: List<LatLng?>?): Double {
        return SphericalUtil.computeArea(latLngs)
    }

    fun getLatLnBounds(points: List<LatLng?>): LatLngBounds? {
        val builder = LatLngBounds.builder()
        for (ll in points) {
            builder.include(ll)
        }
        return builder.build()
    }

    fun convertStringToLatLnList(s: String?): List<LatLng?>? {
        val listType = object : TypeToken<List<LatLng?>?>() {}.type
        return Gson().fromJson(s, listType)
    }
}