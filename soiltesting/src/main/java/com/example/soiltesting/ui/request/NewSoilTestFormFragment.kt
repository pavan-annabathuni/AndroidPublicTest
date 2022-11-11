package com.example.soiltesting.ui.request

import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.soiltesting.R
import com.example.soiltesting.databinding.FragmentNewSoilTestFormBinding
import com.example.soiltesting.model.postsoil.NewSoilTestPost
import com.example.soiltesting.ui.checksoil.CheckSoilRTestViewModel
import com.example.soiltesting.utils.Constant.TAG
import com.example.soiltesting.utils.NetworkResult
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.waycool.data.utils.Resource
import kotlinx.coroutines.launch

class NewSoilTestFormFragment : Fragment() {
    private var _binding: FragmentNewSoilTestFormBinding? = null
    private val binding get() = _binding!!
    private val viewModel by lazy { ViewModelProvider(this)[CheckSoilRTestViewModel::class.java] }
    private val soilViewModel by lazy { ViewModelProvider(this)[SoilTestRequestViewModel::class.java] }
    var ploteNumber: String = ""
    var pincode: String = ""
    var address: String = ""
    var city: String = ""
    var state: String = ""
    private var accountID: Int? = null
    var mobileNumber: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewSoilTestFormBinding.inflate(inflater, container, false)
        if (arguments != null) {
            val onp_id = arguments?.getInt("soil_test_number")
            val lat = arguments?.getDouble("lat")
            val long = arguments?.getDouble("long")

            Log.d(TAG, "onCreateViewONPID:$onp_id ")
            Log.d(TAG, "onCreateViewONPID:$lat ")
            Log.d(TAG, "onCreateViewONPID:$long ")


            soilViewModel.getReverseGeocode("${lat},${long}")
                .observe(viewLifecycleOwner) {
                    if (it.results.isNotEmpty()) {
                        val result = it.results[0]
                        if (result.subLocality != null)
                            binding.etCity.append("${result.subLocality},")
                        else
                            binding.etCity.append("${result.locality}")
                        binding.etState.append("${result.state}")

                        binding.etAddress.setText("${result.formattedAddress ?: ""}")
                        binding.etAddress.setSelection(0)

                        binding.etPincodeNumber.setText(result.pincode ?: "")

                    }
                }


//            soilViewModel.viewModelScope.launch {
            soilViewModel.getUserDetails().observe(viewLifecycleOwner) {
//                    itemClicked(it.data?.data?.id!!, lat!!, long!!, onp_id!!)
//                    account=it.data.account
                for (i in it.data!!.account) {
                    if (i.accountType == "outgrow") {
                        Log.d(TAG, "onCreateViewAccountID:${i.id}")
                        accountID = i.id
                        if (accountID != null) {
                            Log.d(TAG, "onCreateViewAccountID:$accountID")
                            itemClicked(accountID!!, lat!!, long!!, onp_id!!)
                        }

                    }


//                            if (account!=null){
//                                itemClicked(account!!, lat!!, long!!, onp_id!!)
//                            }
                }
            }


        }

//            if (account!=null){
//                itemClicked(account!!, lat!!, long!!, onp_id!!)
//            }


//        binding.cardCheckHealth.setOnClickListener {


//        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewBackClick()
//        itemClicked()
//        initView()
        mvvm()
//            initView()
//            val newSoilTestPost = NewSoilTestPost(
//                "123456",
//                ploteNumber.toString(),
//                pincode.toString(),
//                "12.9344",
//                "72.000",
//                address.toString(),
//                city.toString(),
//                state.toString(),
//                mobileNumber,
//                "1"
//            )
//            viewModel.postNewSoil(newSoilTestPost)
        Log.d(TAG, "onViewCreated: ButtonClicked")
//            soilViewModel.postNewSoil(
//                binding.etPlotNumber.toString(),
//                binding.etPincodeNumber.toString(),
//                binding.etAddress.toString(),
//                "123456782345"
//            ).observe(requireActivity()) {
//                when (it) {
//                    is Resource.Success -> {
//                        val bundle=Bundle()
//                        bundle.putString("soil_test_number",it.data?.data.toString())
//                        Log.d(TAG, "initViewsendingId: "+it.data!!.data.soilTestNumber.toString())
//                        findNavController().navigate(R.id.action_newSoilTestFormFragment_to_sucessFullFragment)
//                    }
//                    is Resource.Loading -> {
//
//
//                    }
//                    is Resource.Error -> {
//                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
//                            .show()
////                        .show()
//                    }
//                }
//
//
//            }


    }

    private fun onBottomButtonBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }

        })

    }

//    private fun initView() {
//        soilViewModel.postNewSoil(
//            binding.etPlotNumber.toString(),
//            binding.etPincodeNumber.toString(),
//            binding.etAddress.toString(),
//            binding.etMobile.toString()
//        ).observe(viewLifecycleOwner, Observer {
//            when (it) {
//                is Resource.Success -> {
//                    val bundle = Bundle()
//                    bundle.putString("soil_test_number", it.data?.data.toString())
//                    Log.d(TAG, "initViewsendingId: " + it.data!!.data.soilTestNumber.toString())
//                    findNavController().navigate(
//                        R.id.action_newSoilTestFormFragment_to_sucessFullFragment,
//                        bundle
//                    )
//                }
//                is Resource.Loading -> {
//
//
//                }
//                is Resource.Error -> {
//                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
//                        .show()
////                        .show()
//                }
//            }
//
//        })
//
//
//    }

    private fun mvvm() {
//        binding.cardCheckHealth.setOnClickListener {
//            val newSoilTestPost=NewSoilTestPost("praveen","503305","fgdbjsnakdvbhi",6304423001,"Hyderabad","telangana")
//            viewModel.postNewSoil(newSoilTestPost)
//
//        }


    }

    private fun initViewBackClick() {
        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }
    }


    private fun itemClicked(account_id: Int, lat: Double, long: Double, onp_number: Int) {
        binding.cardCheckHealth.setOnClickListener {
            ploteNumber = binding.etPlotNumber.text.toString().trim()
            pincode = binding.etPincodeNumber.text.toString().trim()
            address = binding.etAddress.text.toString().trim()
            city = binding.etCity.text.toString().trim()
            state = binding.etState.text.toString().trim()
            mobileNumber = binding.etMobile.text.toString().trim()
            if (ploteNumber.isEmpty()) {
                binding.etPlotNumber.error = "Plot Number should not be empty"
                return@setOnClickListener
            } else if (pincode.isEmpty()) {
                binding.etPincodeNumber.error = "Enter Pincode "
                return@setOnClickListener
            } else if (address.isEmpty()) {
                binding.etState.error = "Enter Address"
                return@setOnClickListener
            } else if (city.isEmpty()) {
                binding.etCity.error = "Enter City"
                return@setOnClickListener
            } else if (state.isEmpty()) {
                binding.etState.error = "Enter State"
                return@setOnClickListener
            } else if (mobileNumber.isEmpty()) {
                binding.etMobile.error = "Enter Mobile Number"
                return@setOnClickListener
            } else if (ploteNumber.isNotEmpty() && pincode.isNotEmpty() && address.isNotEmpty() && city.isNotEmpty() && state.isNotEmpty() && mobileNumber.isNotEmpty()) {
                soilViewModel.postNewSoil(
                    account_id, lat, long,
                    onp_number,
                    binding.etPlotNumber.text.toString(),
                    binding.etPincodeNumber.text.toString(),
                    binding.etAddress.text.toString(),
                    binding.etState.text.toString(),
                    binding.etCity.text.toString(),
                    binding.etMobile.text.toString()
                ).observe(requireActivity()) {
                    when (it) {
                        is Resource.Success -> {
                            val bundle = Bundle()
                            bundle.putString("soil_test_number", it.data?.data.toString())
                            Log.d(
                                TAG,
                                "initViewsendingId: " + it.data!!.data.soilTestNumber.toString()
                            )
                            findNavController().navigate(R.id.action_newSoilTestFormFragment_to_sucessFullFragment)
                        }
                        is Resource.Loading -> {


                        }
                        is Resource.Error -> {
                            Toast.makeText(
                                requireContext(),
                                it.message.toString(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
//                        .show()
                        }
                    }


                }

            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


