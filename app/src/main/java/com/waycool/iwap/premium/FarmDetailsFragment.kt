package com.waycool.iwap.premium

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.addcrop.AddCropActivity
import com.example.cropinformation.adapter.MyCropsAdapter
import com.waycool.data.Network.NetworkModels.ViewDeviceData
import com.waycool.data.utils.Resource
import com.waycool.iwap.MainViewModel
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentFarmDetails2Binding
import com.waycool.iwap.utils.Constant.TAG


class FarmDetailsFragment : Fragment() ,ViewDeviceFlexListener {
    private var _binding: FragmentFarmDetails2Binding? = null
    private val binding get() = _binding!!

    private val viewDevice by lazy { ViewModelProvider(requireActivity())[ViewDeviceViewModel::class.java] }
    private val viewModel by lazy { ViewModelProvider(requireActivity())[MainViewModel::class.java] }
    private lateinit var myCropAdapter: MyCropsAdapter
    private var myFarmPremiumAdapter = MyFarmPremiumAdapter()
    private var  deltaAdapter=DeltaAdapter()
    var viewDeviceListAdapter = ViewDeviceListAdapter( this)
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
            viewModel.getUserDetails().observe(viewLifecycleOwner) { it ->
                var accountId = it.data?.accountId
                Log.d("TAG", "getFarmsAccount: $accountId ")
                if (accountId !=null){
                    farmDetailsObserve(accountId)
                }

            }

//        val progressbar: ProgressBar = findViewById(R.id.progressbar) as ProgressBar
//        val color = -0xff0100
//        progressbar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN)
//        progressbar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN)



    }

    private fun farmDetailsObserve(account:Int) {
        viewModel.getMyFarms(account).observe(viewLifecycleOwner){it->
            when (it) {
                is Resource.Success -> {
                    val response=it.data
                    if (it.data?.isNullOrEmpty() == true){
                        Log.d(TAG, "initiFarmDeltT:")
                        binding.tvPempDate.text=response!![0].farmPumpHp
                        binding.tvRiver.text= response[0].farmWaterSource
//                        binding.totalFormDate.text=response!![0].
                        binding.totalFormDate.text= response[0].farmPumpType
                        binding.totalHeightInches.text= response[0].farmPumpPipeSize
                        binding.tvPumpFlowRateNUmber.text= response[0].farmPumpFlowRate

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

    private fun initiFarmDeltT() {
        binding.recyclerviewDelta.adapter = deltaAdapter
        viewDevice.getFarmDetails().observe(viewLifecycleOwner){
            when (it) {
                is Resource.Success -> {
                    if (it.data?.data.isNullOrEmpty()){
                        deltaAdapter.setMovieList(it.data?.data)
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