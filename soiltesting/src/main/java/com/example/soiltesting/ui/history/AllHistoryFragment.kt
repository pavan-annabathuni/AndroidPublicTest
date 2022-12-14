package com.example.soiltesting.ui.history

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.soiltesting.R
import com.example.soiltesting.databinding.FragmentAllHistoryBinding
import com.example.soiltesting.ui.checksoil.CheckSoilRTestViewModel
import com.example.soiltesting.ui.checksoil.CustomeDialogFragment
import com.example.soiltesting.utils.Constant
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.waycool.data.repository.domainModels.SoilTestHistoryDomain
import com.waycool.data.utils.Resource
import java.util.*
import kotlin.collections.ArrayList


class AllHistoryFragment : Fragment(), StatusTrackerListener {
    private var _binding: FragmentAllHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var accountID: Int? = null
    private val REQUEST_CODE_SPEECH_INPUT = 1
    //    private lateinit var soilHistoryAdapter: SoilHistoryAdapter
    private var soilHistoryAdapter = HistoryDataAdapter(this)
    var objectListNew = java.util.ArrayList<SoilTestHistoryDomain>()

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
        speechToText()
        initViewBackClick()
//        initSearchView()
//        clickSearch()
        viewModel.getUserDetails().observe(viewLifecycleOwner) {
//                    itemClicked(it.data?.data?.id!!, lat!!, long!!, onp_id!!)
//                    account=it.data.account
                 accountID = it.data?.accountId
                    if (accountID !=null) {
                        bindObserversSoilTestHistory(accountID!!)
                    }


        }
//        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                findNavController().navigateUp()
//            }
//
//        })


//        clickSearch()
    }

//    private fun initSearchView() {
//        binding.searchView.addTextChangedListener(object :TextWatcher{
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                objectListNew.clear()
//                Log.d("TAG", "::::str " + charSequence.toString())
//                for (j in filteredList.indices) {
//                    if (filteredList[0].soil_test_number .lowercase().contains(
//                            charSequence.toString().lowercase(
//                            )
//                        )
//                    ) {
//                        objectListNew.add(filteredList.)
//
//                    }
//                }
////                areaAdapter.upDateList(objectListNew)
//                soilHistoryAdapter.upDateList(filteredList)
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//                TODO("Not yet implemented")
//            }
//
//        })
//    }

    private fun speechToText() {
        binding.textToSpeach.setOnClickListener() {
            binding.searchView.text.clear()
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

            // on below line we are passing language model
            // and model free form in our intent
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
            )

            // on below line we are passing our
            // language as a default language.
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE, "en-IN"
            )

            // on below line we are specifying a prompt
            // message as speak to text on below line.
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

            // on below line we are specifying a try catch block.
            // in this block we are calling a start activity
            // for result method and passing our result code.
            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            } catch (e: Exception) {
                // on below line we are displaying error message in toast
//                Toast
//                    .makeText(
//                        context, " " + e.message,
//                        Toast.LENGTH_SHORT
//                    )
//                    .show()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // in this method we are checking request
        // code with our result code.
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            // on below line we are checking if result code is ok
            if (resultCode == AppCompatActivity.RESULT_OK && data != null) {

                // in that case we are extracting the
                // data from our array list
                val res: java.util.ArrayList<String> =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as java.util.ArrayList<String>

                // on below line we are setting data
                // to our output text view.
                binding.searchView.setText(
                    Objects.requireNonNull(res)[0]
                )
            }
        }
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
                 accountID = it.data?.accountId
                        if (accountID !=null) {
                            binding.progressBar.isVisible = true
                            binding.clProgressBar.visibility = View.VISIBLE
                            binding.cardCheckHealth.isClickable=false
//                            temp()
                            isLocationPermissionGranted(accountID!!)
                        }


            }
        }
    }

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

    override fun statusTracker(data: SoilTestHistoryDomain) {
        val bundle = Bundle()
        bundle.putInt("id", data.id!!)
        bundle.putString("soil_test_number", data.soil_test_number)
        findNavController().navigate(
            R.id.action_allHistoryFragment_to_statusTrackerFragment,
            bundle
        )

    }
    fun temp(){
        viewModel.getCheckSoilTestLab(
            11,"18.3603","77.6028").observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    binding.clProgressBar.visibility = View.GONE
                    Log.d(
                        "TAG",
                        "bindObserversDataCheckSoilData:" + it.data.toString()
                    )
                    if (it.data!!.isNullOrEmpty()) {
                        CustomeDialogFragment.newInstance().show(requireActivity().supportFragmentManager, CustomeDialogFragment.TAG)
                        binding.cardCheckHealth.isClickable=true
//                                        binding.clProgressBar.visibility = View.VISIBLE
//                        binding.constraintLayout.setBackgroundColor(R.color.background_dialog)
                        //                      findNavController().navigate(R.id.action_soilTestingHomeFragment_to_customeDialogFragment)
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
                    binding.progressBar.isVisible = true
                    binding.clProgressBar.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT)
                        .show()

                }
            }

        }

    }
    private fun isLocationPermissionGranted(account_id: Int?): Boolean {
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
                    if (location != null && account_id !=null) {
                        // use your location object
                        // get latitude , longitude and other info from this
                        Log.d("checkLocation", "isLocationPermissionGranted: $location")
//                        getAddress(location.latitude, location.longitude)
                        Log.d(Constant.TAG, "isLocationPermissionGrantedLotudetude: ${location.latitude}")
                        Log.d(Constant.TAG, "isLocationPermissionGrantedLotudetude: ${location.longitude}")

//                        checkSoilTestViewModel.getSoilTest(1, location.latitude, location.longitude)
//                        bindObserversCheckSoilTest()

                        val  latitude = String.format(Locale.ENGLISH, "%.2f", location.latitude)
                        val longitutde = String.format(Locale.ENGLISH, "%.2f", location.longitude)

                        viewModel.getCheckSoilTestLab(
                            account_id,
                            latitude,
                            longitutde
                        ).observe(requireActivity()) {
                            when (it) {
                                is Resource.Success -> {
                                    binding.clProgressBar.visibility = View.GONE
                                    binding.progressBar.isVisible = false
                                    Log.d(
                                        "TAG",
                                        "bindObserversDataCheckSoilData:" + it.data.toString()
                                    )
                                    if (it.data!!.isNullOrEmpty()) {

                                        CustomeDialogFragment.newInstance().show(
                                            requireActivity().supportFragmentManager,
                                            CustomeDialogFragment.TAG
                                        )
                                        binding.cardCheckHealth.isClickable=true
//                                        binding.clProgressBar.visibility = View.VISIBLE
//                        binding.constraintLayout.setBackgroundColor(R.color.background_dialog)
                                        //                           findNavController().navigate(R.id.action_soilTestingHomeFragment_to_customeDialogFragment)
                                    } else if (it.data!!.isNotEmpty()) {
                                        val response = it.data
                                        Log.d(
                                            Constant.TAG,
                                            "bindObserversCheckSoilTestModelFJndsj: $response"
                                        )
                                        var bundle = Bundle().apply {
                                            putParcelableArrayList(
                                                "list",
                                                ArrayList<Parcelable>(response)
                                            )
                                        }

                                        bundle.putString("lat",latitude)
                                        bundle.putString("lon",longitutde)

                                        findNavController().navigate(
                                            R.id.action_allHistoryFragment_to_checkSoilTestFragment,
                                            bundle
                                        )
                                    }

                                }
                                is Resource.Error -> {
                                    Toast.makeText(
                                        requireContext(),
                                        it.message.toString(),
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()

                                }
                                is Resource.Loading -> {
                                    binding.progressBar.isVisible = true
                                    binding.clProgressBar.visibility = View.VISIBLE
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

//    private fun isLocationPermissionGranted(account_id:Int): Boolean {
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
//                        Log.d(Constant.TAG, "isLocationPermissionGrantedLotudetude: ${location.latitude}")
//                        Log.d(Constant.TAG, "isLocationPermissionGrantedLotudetude: ${location.longitude}")
//
//
//
////                        checkSoilTestViewModel.getSoilTest(1, location.latitude, location.longitude)
////                        bindObserversCheckSoilTest()
//                        val  latitude = String.format(Locale.ENGLISH, "%.2f", location.latitude)
//                        val longitutde = String.format(Locale.ENGLISH, "%.2f", location.longitude)
//
//                        viewModel.getCheckSoilTestLab(
//                            account_id,
//                            latitude,
//                            longitutde
//                        ).observe(requireActivity()) {
//                            when (it) {
//                                is Resource.Success -> {
//                                    binding.progressBar.isVisible = false
//                                    binding.clProgressBar.visibility = View.GONE
//                                    Log.d(
//                                        "TAG",
//                                        "bindObserversDataCheckSoilData:" + it.data.toString()
//                                    )
//                                    if (it.data!!.isNullOrEmpty()) {
//                                        binding.cardCheckHealth.isClickable=true
//                                        CustomeDialogFragment.newInstance().show(requireActivity().supportFragmentManager, CustomeDialogFragment.TAG)
////                                        binding.clProgressBar.visibility = View.VISIBLE
////                        binding.constraintLayout.setBackgroundColor(R.color.background_dialog)
//                  //                      findNavController().navigate(R.id.action_soilTestingHomeFragment_to_customeDialogFragment)
//                                    } else if (it.data!!.isNotEmpty()) {
//                                        val response = it.data
//                                        Log.d(
//                                            Constant.TAG,
//                                            "bindObserversCheckSoilTestModelFJndsj: $response")
//                                        var bundle = Bundle().apply {
//                                            putParcelableArrayList("list", ArrayList<Parcelable>(response))
//                                        }
//
//                                        findNavController().navigate(
//                                            R.id.action_allHistoryFragment_to_checkSoilTestFragment,
//                                            bundle
//                                        )
//                                    }
//
//                                }
//                                is Resource.Error -> {
//                                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT)
//                                        .show()
//
//                                }
//                                is Resource.Loading -> {
//                                    binding.progressBar.isVisible = true
//                                    binding.clProgressBar.visibility = View.VISIBLE
//                                    Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT)
//                                        .show()
//
//                                }
//                            }
//
//                        }
//
//
//                    }
//                }
//            true
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}