package com.waycool.iwap.premium

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.addcrop.AddCropActivity
import com.example.adddevice.AddDeviceActivity
import com.waycool.addfarm.AddFarmActivity
import com.waycool.data.Network.NetworkModels.ViewDeviceData
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.repository.domainModels.SoilTestHistoryDomain
import com.waycool.data.utils.Resource
import com.waycool.iwap.MainViewModel
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentHomePagePremiumBinding
import kotlin.math.roundToInt


class HomePagePremiumFragment : Fragment(), ViewDeviceFlexListener {
    private var _binding: FragmentHomePagePremiumBinding? = null
    private val binding get() = _binding!!
    private val viewModel by lazy { ViewModelProvider(requireActivity())[MainViewModel::class.java] }
    private val viewDevice by lazy { ViewModelProvider(requireActivity())[ViewDeviceViewModel::class.java] }
    private var myCropPremiumAdapter = MyCropPremiumAdapter()
    private var myFarmPremiumAdapter = MyFarmPremiumAdapter()
    var viewDeviceListAdapter = ViewDeviceListAdapter( this)
    var deviceDataAdapter = DeviceDataAdapter()

    //    private var accountID: Int? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomePagePremiumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickEvents()
        initViewProfile()
        initViewAddCrop()
        initMyCropObserve()
        initObserveMYFarm()
        initObserveDevice()
        initViewPager()
        val fragment: ArrayList<Fragment> = arrayListOf(
            DeviceFragmentOne(),
            DeviceFragmentTwo()

        )

        val map = mutableMapOf<String, Any>()
        map.put("serial_no_id",1)
        map.put("device_model_id",2)
        val adapter = ViewPagerAdapter(fragment, requireParentFragment())
//        binding.idViewPager.adapter=adapter
    }

    private fun initViewPager() {
        binding.tvWelcomeName.setOnClickListener {
            findNavController().navigate(R.id.action_homePagePremiumFragment2_to_deviceFragmentOne)
        }


    }

    private fun initObserveDevice() {
        viewDevice.getIotDevice().observe(requireActivity()) {
            if (it.data?.data.isNullOrEmpty()) {
                binding.cardAddDevice.visibility = View.VISIBLE
            } else
                when (it) {
                    is Resource.Success -> {
                        if (it.data?.data != null) {
                            binding.cardAddDevice.visibility = View.GONE
                        val response = it.data!!.data as ArrayList<ViewDeviceData>
                        binding.deviceFarm.adapter = viewDeviceListAdapter
                        viewDeviceListAdapter.setMovieList(response)
                        }

                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()

                    }
                }

        }


//

////        binding.rvMyDevice.adapter = deviceDataAdapter
//        viewDevice.getIotDevice().observe(viewLifecycleOwner) { it ->
////            if (it.data?.data==null){
////                Toast.makeText(requireContext(), "Server Data", Toast.LENGTH_SHORT).show()
////            }
//            if (it.data?.data?.isEmpty() == true) {
//                binding.cardAddDevice.visibility = View.VISIBLE
//            } else
//                when (it) {
//                    is Resource.Success -> {
////                        binding.cardAddDevice.visibility = View.GONE
//                        val response = it.data!!.data as ArrayList<ViewDeviceData>
//                        viewDeviceListAdapter.setMovieList(response)
//
////                        val arrayList = ArrayList<ModelFlex>()
////                        response.forEach {
////                            arrayList.add(ModelFlex(it.deviceName.toString()))
////                            Log.d("TAG", "initObserveDevicefgcg:$arrayList ")
////                            viewDeviceListAdapter.setMovieList(arrayList)
////                        }
////                        deviceDataAdapter.setMovieList(response)
////                        Log.d("TAG", "initObserveDevice: $response")
//                    }
//                    is Resource.Error -> {
//                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
//
//                    }
//                    is Resource.Loading -> {
//                        Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()
//
//                    }
//                }
//
//
//        }

    }

    private fun initClickEvents() {
        binding.clAddCropData.setOnClickListener {
            val intent = Intent(activity, AddCropActivity::class.java)
            startActivity(intent)
        }
        binding.tvMyCrops.setOnClickListener {
            val intent = Intent(activity, AddCropActivity::class.java)
            startActivity(intent)
        }
        binding.cardAddForm.setOnClickListener {
            val intent = Intent(activity, AddCropActivity::class.java)
            startActivity(intent)
        }
        binding.cardAddDevice.setOnClickListener {
            val intent = Intent(activity, AddDeviceActivity::class.java)
            startActivity(intent)
        }
        binding.clAddForm.setOnClickListener {
            val intent = Intent(activity, AddFarmActivity::class.java)
            startActivity(intent)
        }
        binding.MyFarm.setOnClickListener {
            val intent = Intent(activity, AddFarmActivity::class.java)
            startActivity(intent)
        }
        binding.MyDevice.setOnClickListener {
            val intent = Intent(activity, AddDeviceActivity::class.java)
            startActivity(intent)
        }

    }

    private fun initMyCropObserve() {
        binding.rvMyCrops.adapter = myCropPremiumAdapter
        binding.videosScroll.setCustomThumbDrawable(com.waycool.uicomponents.R.drawable.slider_custom_thumb)
        binding.rvMyCrops.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                binding.videosScroll.value =
                    calculateScrollPercentage2(binding).toFloat()
            }
        })
        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            if (it.data != null) {
                var accountId = it.data?.accountId
                Log.d("TAG", "initMyCropObserveAccountId: ")
//                var accountId: Int? = null
//                for (account in it?.data?.) {
//                    if (account.accountType?.lowercase() == "outgrow") {
//                        accountId = account.id
//                    }
//
//                }
//                var accountId: Int = it.data!!.account[0].id!!
                if (accountId != null)
                    viewModel.getMyCrop2(accountId).observe(viewLifecycleOwner) {
//                        myCropAdapter.submitList(it.data)
                        val response = it.data as ArrayList<MyCropDataDomain>
                        myCropPremiumAdapter.setMovieList(response)
                        if ((it.data != null)) {
//                            binding.tvCount.text = it.data!!.size.toString()

                        } else {

//                            binding.tvCount.text = "0"
                        }
                        if (it.data!!.isNotEmpty()) {
                            binding.cvEditCrop.visibility = View.VISIBLE
                            binding.cardAddForm.visibility = View.GONE
                        } else {
                            binding.cvEditCrop.visibility = View.GONE
                            binding.cardAddForm.visibility = View.VISIBLE
                        }
                    }
            }
        }

    }


    private fun initViewAddCrop() {


    }

    @SuppressLint("SetTextI18n")
    private fun initViewProfile() {
        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {

                    Log.d("Profile", it.data.toString())
                    it.data.let { userDetails ->
                        Log.d("Profile", userDetails.toString())
                        Log.d("Profile", userDetails?.profile?.lat + userDetails?.profile?.long)
                        binding.tvWelcome.text = userDetails?.profile?.village
                        binding.tvWelcomeName.text = "Welcome, ${it.data?.name.toString()}"

                        Log.d("TAG", "onViewCreatedProfileUser: $it.data?.name")
                        userDetails?.profile?.lat?.let { it1 ->
                            userDetails.profile?.long?.let { it2 ->
                                Log.d("Profile", it1 + it2)
//                                Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        }
    }

    private fun initObserveMYFarm() {
        binding.rvMyFarm.adapter = myFarmPremiumAdapter
        binding.videosScrollMyFarm.setCustomThumbDrawable(com.waycool.uicomponents.R.drawable.slider_custom_thumb)
        binding.rvMyFarm.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                binding.videosScrollMyFarm.value =
                    calculateScrollPercentageFarm(binding).toFloat()
            }
        })
        viewModel.getUserDetails().observe(viewLifecycleOwner) { it ->
            if (it.data != null) {
                var accountId = it.data?.accountId
                Log.d("TAG", "initObserveMYFarmAccount $accountId: ")

                if (accountId != null)
                    viewModel.getMyFarms(accountId).observe(viewLifecycleOwner) {
                        when (it) {
                            is Resource.Success -> {
                                if (it.data == null) {
                                    Log.d("TAG", "initObserveMYFarmPraveen: ")
                                } else if (it.data != null)
                                    if (it.data!!.isNotEmpty()) {
                                        binding.clAddForm.visibility = View.GONE
                                        binding.cardMYFarm.visibility = View.VISIBLE
//                                        binding.farmsDetailsCl.visibility = View.VISIBLE
//                                        binding.tvAddress.visibility=View.INVISIBLE
                                        val response = it.data as ArrayList<MyFarmsDomain>
                                        Log.d("TAG", "initObserveMYFarmData:$response ")
                                        myFarmPremiumAdapter.setMovieList(response)
                                    } else {
                                        binding.clAddForm.visibility = View.VISIBLE
                                        binding.cardMYFarm.visibility = View.GONE
//                                        binding.farmsDetailsCl.visibility = View.GONE
//                                        binding.tvAddress.visibility=View.VISIBLE

                                    }
                            }
                            is Resource.Loading -> {}
                            is Resource.Error -> {
//                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            }
        }
    }

//        viewModel.getUserDetails().observe(viewLifecycleOwner) {
//            if (it.data != null) {
//                var accountId = it.data?.accountId
//                if (accountId != null)
//                    viewModel.getMyFarms(721).observe(viewLifecycleOwner) {
//                        binding.rvMyFarm.adapter = myFarmPremiumAdapter
//                        if (it.data!!.isEmpty()) {
//                            binding.clAddForm.visibility = View.VISIBLE
//                        } else
//                            when (it) {
//                                is Resource.Success -> {
//                                    if (it.data == null) {
//                                        binding.clAddForm.visibility = View.VISIBLE
//                                    }
//                                    binding.clAddForm.visibility = View.GONE
//                                    binding.cardMYFarm.visibility = View.VISIBLE
//                                    val response = it.data as ArrayList<MyFarmsDomain>
//
//                                    myFarmPremiumAdapter.setMovieList(response)
//
//                                }
//                                is Resource.Error -> {
//                                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT)
//                                        .show()
//
//                                }
//                                is Resource.Loading -> {
//                                    Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT)
//                                        .show()
//
//                                }
//                            }
//                    }
//            }
//        }


    fun calculateScrollPercentage2(videosBinding: FragmentHomePagePremiumBinding): Int {
        val offset: Int = videosBinding.rvMyCrops.computeHorizontalScrollOffset()
        val extent: Int = videosBinding.rvMyCrops.computeHorizontalScrollExtent()
        val range: Int = videosBinding.rvMyCrops.computeHorizontalScrollRange()
        val scroll = 100.0f * offset / (range - extent).toFloat()
        if (scroll.isNaN())
            return 0
        return scroll.roundToInt()
    }

    fun calculateScrollPercentageFarm(videosBinding: FragmentHomePagePremiumBinding): Int {
        val offset: Int = videosBinding.rvMyFarm.computeHorizontalScrollOffset()
        val extent: Int = videosBinding.rvMyFarm.computeHorizontalScrollExtent()
        val range: Int = videosBinding.rvMyFarm.computeHorizontalScrollRange()
        val scroll = 100.0f * offset / (range - extent).toFloat()
        if (scroll.isNaN())
            return 0
        return scroll.roundToInt()
    }

    override fun viewDevice(data: ViewDeviceData) {
//        viewDeviceListAdapter.notifyDataSetChanged()
        binding.let {
            it.totalAreea.text = data.iotDevicesData?.battery.toString()
            it.tvAddDeviceStart.text = data.model?.modelName.toString()
            it.tvTempDegree.text = data.temperature.toString()
            it.tvWindDegree.text = data.rainfall.toString()
            it.tvHumidityDegree.text = data.humidity.toString()
            it.tvWindSpeedDegree.text = data.windspeed.toString()
            it.tvLeafWetnessDegree.text = data.leafWetness.toString()
            it.tvPressureDegree.text = data.pressure.toString()
            it.ivSoilDegree.text = data.soilTemperature1.toString() + "C"
            it.ivSoilDegreeOne.text = data.soilTemperature2.toString() + "C"
            it.tvLastUpdate.text = data.dataTimestamp.toString()
            it.tubeSpeedometer.maxSpeed = 100f
            it.tubeSpeedometer.speedTo(140f)
            it.tubeSpeedometer.speedometerBackColor = Color.GRAY
            it.tvLastUpdateRefresh.setOnClickListener {


            }
            it.clTemp.setOnClickListener {
                findNavController().navigate(R.id.action_homePagePremiumFragment2_to_graphsFragment)
            }
            it.clWindSpeed.setOnClickListener {
                findNavController().navigate(R.id.action_homePagePremiumFragment2_to_graphsFragment)
            }
            it.clHumidity.setOnClickListener {
                findNavController().navigate(R.id.action_homePagePremiumFragment2_to_graphsFragment)
            }
            it.clWindSpeed.setOnClickListener {
                findNavController().navigate(R.id.action_homePagePremiumFragment2_to_graphsFragment)
            }
            it.clLeafWetness.setOnClickListener {
                findNavController().navigate(R.id.action_homePagePremiumFragment2_to_graphsFragment)
            }
            it.clPressure.setOnClickListener {
                findNavController().navigate(R.id.action_homePagePremiumFragment2_to_graphsFragment)
            }
        }


//        deviceDataAdapter.notifyDataSetChanged()
    }


}