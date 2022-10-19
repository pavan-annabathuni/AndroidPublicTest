package com.example.soiltesting.ui.request

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.soiltesting.R
import com.example.soiltesting.databinding.FragmentNewSoilTestFormBinding
import com.example.soiltesting.model.postsoil.NewSoilTestPost
import com.example.soiltesting.ui.checksoil.CheckSoilRTestViewModel
import com.example.soiltesting.utils.Constant.TAG
import com.example.soiltesting.utils.NetworkResult

class NewSoilTestFormFragment : Fragment()  {
    private var _binding: FragmentNewSoilTestFormBinding? = null
    private val binding get() = _binding!!
    private val viewModel by lazy { ViewModelProvider(this)[CheckSoilRTestViewModel::class.java] }
    var ploteNumber: String = ""
    var pincode: String = ""
    var address: String = ""
    var city: String = ""
    var state: String = ""
    var mobileNumber: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewSoilTestFormBinding.inflate(inflater, container, false)
        var onp_id = arguments?.getString("soil_test_number")
        Log.d(TAG, "onCreateViewONPIDPrinted: $onp_id")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewBackClick()
//        itemClicked()
        initView()
        mvvm()
        binding.cardCheckHealth.setOnClickListener {
            val newSoilTestPost = NewSoilTestPost(
                "123456",
                ploteNumber.toString(),
                pincode.toString(),
                "12.9344",
                "72.000",
                address.toString(),
                city.toString(),
                state.toString(),
                mobileNumber,
                "1"
            )
            viewModel.postNewSoil(newSoilTestPost)

        }

    }
    private fun onBottomButtonBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

            }

        })

    }

    private fun initView() {
        viewModel.newSoilTestLiveData.observe(viewLifecycleOwner , Observer {
            when (it) {
                is NetworkResult.Success -> {
//                    val response = it.data?.data as ArrayList<com.example.soiltesting.model.postsoil.Data>
//                    val arrayList = ArrayList<Data>()
//                    arrayList.add(response[0].)
                    val bundle=Bundle()
                    bundle.putString("soil_test_number",it.data?.data?.id.toString())
                    Log.d(TAG, "initViewsendingId: "+it.data?.data?.id.toString())
                    findNavController().navigate(R.id.action_newSoilTestFormFragment_to_sucessFullFragment,bundle)

                }
                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
                is NetworkResult.Loading -> {
                }
            }
        })


    }

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


    private fun itemClicked() {
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
                val newSoilTestPost =
                    NewSoilTestPost(
                        "6333F77F01A6C",
                        ploteNumber.toString(),
                        pincode.toString(),
                        "12.9344",
                        "72.000",
                        address.toString(),
                        city.toString(),
                        state.toString(),
                        mobileNumber,
                        "1"
                    )
                viewModel.postNewSoil(newSoilTestPost)
                Toast.makeText(requireContext(), "Success API Call", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_newSoilTestFormFragment_to_sucessFullFragment)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}