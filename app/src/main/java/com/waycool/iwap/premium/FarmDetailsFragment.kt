package com.waycool.iwap.premium

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ekn.gruzer.gaugelibrary.Range
import com.example.addcrop.AddCropActivity
import com.example.adddevice.AddDeviceActivity
import com.example.cropinformation.adapter.MyCropsAdapter
import com.example.ndvi.MainActivityNdvi
import com.waycool.data.Network.NetworkModels.ViewDeviceData
import com.waycool.data.utils.Resource
import com.waycool.iwap.MainViewModel
import com.waycool.iwap.databinding.FragmentFarmDetails2Binding
import com.waycool.iwap.utils.Constant.TAG


class FarmDetailsFragment : Fragment(), ViewDeviceFlexListener {
    private var _binding: FragmentFarmDetails2Binding? = null
    private val binding get() = _binding!!

    private val viewDevice by lazy { ViewModelProvider(requireActivity())[ViewDeviceViewModel::class.java] }
    private val viewModel by lazy { ViewModelProvider(requireActivity())[MainViewModel::class.java] }
    private lateinit var myCropAdapter: MyCropsAdapter

    //    private var myFarmPremiumAdapter = MyFarmPremiumAdapter(this)
    var viewDeviceListAdapter = ViewDeviceListAdapter(this)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFarmDetails2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewClick()
        initMyObserve()
        initObserveDevice()
        myCrop()
        initiFarmDeltT()
        initDeltaSpeedGauges()
        viewModel.getUserDetails().observe(viewLifecycleOwner) { it ->
            if (arguments != null) {
                val farm_id = arguments?.getInt("farm_id")
                var accountId = it.data?.accountId
                Log.d("TAG", "getFarmsAccount: $accountId ")
                if (accountId != null) {
                    farmDetailsObserve(accountId, farm_id)
                }

            }
        }

//        val progressbar: ProgressBar = findViewById(R.id.progressbar) as ProgressBar
//        val color = -0xff0100
//        progressbar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN)
//        progressbar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN)


    }

    private fun initDeltaSpeedGauges() {
        val range = Range()
        range.color = Color.parseColor("#EC4544")
        range.from = 0.0
        range.to = 10.0

        val range2 = Range()
        range2.color = Color.parseColor("#1FB04B")
        range2.from = 10.0
        range2.to = 40.0

        val range3 = Range()
        range3.color = Color.parseColor("#00b20b")
        range3.from = 40.0
        range3.to = 80.0
        val range4 = Range()
        range3.color = Color.parseColor("#EC4544")
        range3.from = 80.0
        range3.to = 100.0
        //add color ranges to gauge
        binding.soilMoistureOne.addRange(range)
        binding.soilMoistureOne.addRange(range2)
        binding.soilMoistureOne.addRange(range3)
        binding.soilMoistureOne.addRange(range4)
        //set min max and current value
        binding.soilMoistureOne.minValue = 0.0
        binding.soilMoistureOne.maxValue = 100.0
        binding.soilMoistureOne.value = 0.0
    }

    private fun farmDetailsObserve(account: Int, farm_id: Int?) {
        viewModel.getMyFarms(account, farm_id).observe(viewLifecycleOwner) { it ->
            when (it) {
                is Resource.Success -> {
                    val response = it.data
                    if (it.data?.isNullOrEmpty() == true) {
                        response?.forEach {
                            binding.tvPempDate.text = it.farmPumpHp
                            binding.tvRiver.text = it.farmWaterSource
//                        binding.totalFormDate.text=response!![0].
                            binding.totalFormDate.text = it.farmPumpType
                            binding.totalHeightInches.text = it.farmPumpPipeSize
                            binding.tvPumpFlowRateNUmber.text = it.farmPumpFlowRate
                        }
//                        Log.d(TAG, "initiFarmDeltT:")
//                        binding.tvPempDate.text=it.farmPumpHp
//                        binding.tvRiver.text= response[0].farmWaterSource
////                        binding.totalFormDate.text=response!![0].
//                        binding.totalFormDate.text= response[0].farmPumpType
//                        binding.totalHeightInches.text= response[0].farmPumpPipeSize
//                        binding.tvPumpFlowRateNUmber.text= response[0].farmPumpFlowRate

                    }

                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    private fun initiFarmDeltT() {
        var deltaAdapter = DeltaAdapter(requireContext())
        var deltaTomAdapter = DeltaTomAdapter(requireContext())
        binding.sparayingRv.adapter = deltaAdapter
        binding.sparayingRv2.adapter = deltaTomAdapter
        viewDevice.farmDetailsDelta().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    if (it.data?.data!!.isNotEmpty()) {
                        deltaAdapter.setMovieList(it.data?.data)
//                        binding.soilMoistureOne.progress=60
//                        Log.d(TAG, "initiFarmDeltT: ${it.data!!.data}")
//                        deltaAdapter.notifyDataSetChanged()
                        deltaTomAdapter.setMovieList(it.data?.data)
//                        deltaAdapter.update(getSprayingItems(sprayingTime.getToday()))
//                        Log.d(TAG, "initiFarmDeltT: ${it.data!!.data[0].Today} ")

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
    }

    private fun myCrop() {
        myCropAdapter = MyCropsAdapter(MyCropsAdapter.DiffCallback.OnClickListener {
//            val intent = Intent(activity, IrrigationPlannerActivity::class.java)
//            startActivity(intent)
        })
        binding.rvMyCrops.adapter = myCropAdapter
        viewModel.getUserDetails().observe(viewLifecycleOwner) { it ->
            if (it.data != null) {
                var accountId: Int? = null
                accountId = it.data?.accountId

//                var accountId: Int = it.data!!.account[0].id!!
                if (accountId != null)
                    viewModel.getMyCrop2(accountId).observe(viewLifecycleOwner) {
                        Log.d("MyCrops", "myCrop: ${it.data}")
                        myCropAdapter.submitList(it.data)
                        if ((it.data != null)) {
                            binding.tvCount.text = it.data!!.size.toString()
                        } else {
                            binding.tvCount.text = "0"
                        }
                        if (it.data!!.isNotEmpty()) {
                            binding.cvEditCrop.visibility = View.VISIBLE
                            binding.cardAddForm.visibility = View.GONE
                        } else {
                            binding.cvEditCrop.visibility = View.GONE
                            binding.cardAddForm.visibility = View.VISIBLE
                        }
//                        if (it.data?.size!! < 8) {
//                            binding.addLl.visibility = View.VISIBLE
//                        } else binding.addLl.visibility = View.GONE
                    }
            }
        }
    }

    private fun initObserveDevice() {
        viewDevice.getIotDevice().observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    if (it.data?.data != null) {
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

    private fun initMyObserve() {

    }

    private fun initViewClick() {
        binding.tvMyCrops.setOnClickListener {
            val intent = Intent(activity, AddCropActivity::class.java)
            startActivity(intent)
        }
        binding.MyDevice.setOnClickListener {
            val intent = Intent(activity, AddDeviceActivity::class.java)
            startActivity(intent)
        }
        binding.tvNdviBanner.setOnClickListener {
            val intent = Intent(activity, MainActivityNdvi::class.java)
            startActivity(intent)
        }

    }

    override fun viewDevice(data: ViewDeviceData) {
        binding.let {
            it.totalAreea.text = data.iotDevicesData?.battery.toString()
            it.tvAddDeviceStart.text = data.model?.modelName.toString()
            it.tvLastUpdate.text = data.dataTimestamp.toString()
//            binding.soilMoistureOne .progress=60
//            binding.soilMoistureTwo .progress=70
            it.tvLastUpdateRefresh.setOnClickListener {
            }
        }

    }


}