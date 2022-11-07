package com.example.soiltesting.ui.history

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.soiltesting.R
import com.example.soiltesting.databinding.FragmentAllHistoryBinding
import com.example.soiltesting.ui.checksoil.CheckSoilRTestViewModel
import com.example.soiltesting.utils.Constant
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.waycool.data.repository.domainModels.SoilTestHistoryDomain
import com.waycool.data.utils.Resource


class AllHistoryFragment : Fragment(), StatusTrackerListener {
    private var _binding: FragmentAllHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var accountID: Int? = null

    //    private lateinit var soilHistoryAdapter: SoilHistoryAdapter
    private var soilHistoryAdapter = HistoryDataAdapter(this)

    private val viewModel by lazy { ViewModelProvider(this)[HistoryViewModel::class.java] }

    private val checkSoilTestViewModel by lazy { ViewModelProvider(this)[CheckSoilRTestViewModel::class.java] }
    val filteredList = java.util.ArrayList<SoilTestHistoryDomain>()
//    lateinit var cropDetailsList: SoilHistory


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAllHistoryBinding.inflate(inflater, container, false)
        soilHistoryAdapter = HistoryDataAdapter(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerviewStatusTracker.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerviewStatusTracker.adapter = soilHistoryAdapter
        initViewBackClick()
        viewModel.getUserDetails().observe(viewLifecycleOwner) {
//                    itemClicked(it.data?.data?.id!!, lat!!, long!!, onp_id!!)
//                    account=it.data.account
            for (i in it.data!!.account) {
                if (i.accountType == "outgrow") {
                    Log.d(ContentValues.TAG, "onCreateViewAccountID:${i.id}")
                    accountID = i.id
                    if (accountID !=null) {
                        bindObserversSoilTestHistory(accountID!!)
                    }

                }
            }
        }

        locationClick()
//        clickSearch()
    }

    private fun initViewBackClick() {
        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }
        binding.cardCheckHealth.setOnClickListener {
            viewModel.getUserDetails().observe(viewLifecycleOwner) {
//                    itemClicked(it.data?.data?.id!!, lat!!, long!!, onp_id!!)
//                    account=it.data.account
                for (i in it.data!!.account) {
                    if (i.accountType == "outgrow") {
                        Log.d(ContentValues.TAG, "onCreateViewAccountIDAA:${i.id}")
                        accountID = i.id
                        if (accountID !=null) {
                            isLocationPermissionGranted(accountID!!)
                        }

                    }
                }
            }
        }
    }

    private fun locationClick() {
//        checkSoilTestViewModel.getSoilTest(1, 13.078100, 77.636580)
//        checkSoilTestViewModel.getSoilTest(4,13.22,3.33)

//        binding.cardCheckHealth.setOnClickListener {
//            isLocationPermissionGranted()
////            bindObserversCheckSoilTest()
////            findNavController().navigate(R.id.action_soilTestingHomeFragment_to_newSoilTestFormFragment)
//        }


    }


























//    private fun isLocationPermissionGranted(): Boolean {
//        return if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                android.Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                requireContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                requireActivity(),
//                arrayOf(
//                    android.Manifest.permission.ACCESS_FINE_LOCATION,
//                    android.Manifest.permission.ACCESS_COARSE_LOCATION
//                ),
//                100
//            )
//            Log.d("checkLocation", "isLocationPermissionGranted:1 ")
//            false
//        } else {
//            Log.d("checkLocation", "isLocationPermissionGranted:2 ")
//            fusedLocationClient.lastLocation
//                .addOnSuccessListener { location ->
//                    if (location != null) {
//                        // use your location object
//                        // get latitude , longitude and other info from this
//                        Log.d("checkLocation", "isLocationPermissionGranted: $location")
////                        getAddress(location.latitude, location.longitude)
//                        Log.d(
//                            Constant.TAG,
//                            "isLocationPermissionGrantedLotudetude: ${location.latitude}"
//                        )
//                        Log.d(
//                            Constant.TAG,
//                            "isLocationPermissionGrantedLotudetude: ${location.longitude}"
//                        )
//                        checkSoilTestViewModel.getSoilTest(1, location.latitude, location.longitude)
//                        bindObserversCheckSoilTest()
//
//                    }
//                }
//            true
//        }
//    }

//    private fun bindObserversCheckSoilTest() {
//        checkSoilTestViewModel.checkSoilTestLiveData.observe(viewLifecycleOwner, Observer { model ->
//            when (model) {
//                is NetworkResult.Success -> {
//
//                    Log.d("TAG", "bindObserversDataCheckSoilData:" + model.data.toString())
//                    if (model.data?.data!!.isEmpty()) {
//                        findNavController().navigate(R.id.action_allHistoryFragment_to_customeDialogFragment)
//                    } else if (model.data.data.isNotEmpty()) {
//                        val response = model.data.data
//                        var bundle = Bundle().apply {
//                            putParcelableArrayList("list", ArrayList<Parcelable>(response))
//                        }
//                        Log.d(TAG, "bindObserversCheckSoilTestModel: ${model.data.data.toString()}")
//                        findNavController().navigate(
//                            R.id.action_allHistoryFragment_to_checkSoilTestFragment,
//                            bundle
//                        )
//                    }
//                }
//                is NetworkResult.Error -> {
//                    Toast.makeText(requireContext(), model.message.toString(), Toast.LENGTH_SHORT)
//                        .show()
//                    binding.progressBar.isVisible = false
//                    binding.clProgressBar.visibility = View.GONE
//                }
//                is NetworkResult.Loading -> {
//                    binding.clProgressBar.visibility = View.VISIBLE
//                    binding.progressBar.isVisible = true
//
//                }
//            }
//
//        })
//
//    }

    private fun bindObserversSoilTestHistory(account_id: Int) {
        viewModel.getSoilTestHistory(account_id).observe(requireActivity()) {

            when (it) {
                is Resource.Success -> {
                    val response = it.data as ArrayList<SoilTestHistoryDomain>
                    soilHistoryAdapter.setMovieList(response)
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
                is Resource.Loading -> {

                }
            }
        }
    }

    private fun clickSearch() {

        binding.searchView.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int

            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
                filteredList.clear()
//                Log.d("TAG", "::::str $charSequence")
                for (item in filteredList[0].soil_test_number!!.indices) {
                    Log.d("TAG", "::::stderr $charSequence")
                    if (filteredList[0].soil_test_number!!.lowercase()
                            .startsWith(charSequence.toString().lowercase())
                    ) {
//                        filteredList.add(filteredList)
                        Log.d(TAG, "onTextChangedList:$filteredList")
                        Log.d("TAG", "::::::::stderr $charSequence")
                    }

                }
                soilHistoryAdapter.upDateList(filteredList)
//                binding.etSearchItem.getText().clear();
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun statusTracker(data: SoilTestHistoryDomain) {
        val bundle = Bundle()
        bundle.putInt("id", data.id!!)
        bundle.putString("soil_test_number", data.soil_test_number)
        findNavController().navigate(
            R.id.action_allHistoryFragment_to_statusTrackerFragment,
            bundle
        )

    }
    private fun isLocationPermissionGranted(account_id:Int): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                100
            )
            Log.d("checkLocation", "isLocationPermissionGranted:1 ")
            false
        } else {
            Log.d("checkLocation", "isLocationPermissionGranted:2 ")
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // use your location object
                        // get latitude , longitude and other info from this
                        Log.d("checkLocation", "isLocationPermissionGranted: $location")
//                        getAddress(location.latitude, location.longitude)
                        Log.d(Constant.TAG, "isLocationPermissionGrantedLotudetude: ${location.latitude}")
                        Log.d(Constant.TAG, "isLocationPermissionGrantedLotudetude: ${location.longitude}")



//                        checkSoilTestViewModel.getSoilTest(1, location.latitude, location.longitude)
//                        bindObserversCheckSoilTest()
                        viewModel.getCheckSoilTestLab(
                            account_id,
                            location.latitude,
                            location.longitude
                        ).observe(requireActivity()) {
                            when (it) {
                                is Resource.Success -> {
                                    binding.progressBar.isVisible = false
                                    binding.clProgressBar.visibility = View.GONE
                                    Log.d(
                                        "TAG",
                                        "bindObserversDataCheckSoilData:" + it.data.toString()
                                    )
                                    if (it.data!!.isNullOrEmpty()) {
//                        binding.clProgressBar.visibility = View.VISIBLE
//                        binding.constraintLayout.setBackgroundColor(R.color.background_dialog)
                                        findNavController().navigate(R.id.action_soilTestingHomeFragment_to_customeDialogFragment)
                                    } else if (it.data!!.isNotEmpty()) {
                                        val response = it.data
                                        Log.d(
                                            Constant.TAG,
                                            "bindObserversCheckSoilTestModelFJndsj: $response")
                                        var bundle = Bundle().apply {
                                            putParcelableArrayList("list", ArrayList<Parcelable>(response))
                                        }

                                        findNavController().navigate(
                                            R.id.action_allHistoryFragment_to_checkSoilTestFragment,
                                            bundle
                                        )
                                    }

                                }
                                is Resource.Error -> {
                                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT)
                                        .show()

                                }
                                is Resource.Loading -> {
                                    Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT)
                                        .show()

                                }
                            }

                        }


                    }
                }
            true
        }
    }


}