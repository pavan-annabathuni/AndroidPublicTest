package com.example.soiltesting.ui.request

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.soiltesting.R
import com.example.soiltesting.databinding.FragmentNewSoilTestFormBinding
import com.example.soiltesting.utils.Constant.TAG
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventItemClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.AppUtils
import com.waycool.data.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewSoilTestFormFragment : Fragment() {
    private var _binding: FragmentNewSoilTestFormBinding? = null
    private val binding get() = _binding!!
    private val soilViewModel by lazy { ViewModelProvider(this)[SoilTestRequestViewModel::class.java] }
    var ploteNumber: String = ""
    var pincode: String = ""
    var address: String = ""
    var city: String = ""
    var state: String = ""
    private var accountID: Int? = null
    var mobileNumber: String = ""
    var contactNumber: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewSoilTestFormBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val onp_id = arguments?.getInt("soil_test_number")
            val lat = arguments?.getString("lat")
            val long = arguments?.getString("long")
            val crop_id = arguments?.getInt("plot_id")
            val onpName = arguments?.getString("onpName")
            val cropName = arguments?.getString("cropName")


            translationSoilTesting()
            soilViewModel.getReverseGeocode("${lat},${long}")
                .observe(viewLifecycleOwner) {
                    if (it.results.isNotEmpty()) {
                        val result = it.results[0]
                        if (result.subLocality != null)
                            binding.etCity.append("${result.subLocality}")
                        else
                            binding.etCity.append("${result.locality}")
                        binding.etState.append("${result.state}")

                        binding.etAddress.setText("${result.formattedAddress ?: ""}")
                        binding.etAddress.setSelection(0)

                        binding.etPincodeNumber.setText(result.pincode ?: "")

                    }
                }


            soilViewModel.getUserDetails().observe(viewLifecycleOwner) {
                contactNumber = it.data?.phone.toString()
                binding.tvContact.text = contactNumber
                var accountId = it.data?.accountId
                if (it.data?.accountId == null) {
                   CoroutineScope(Dispatchers.IO).launch{
                        val toastSelectAccount = TranslationsManager().getString("select_account")
                        if(!toastSelectAccount.isNullOrEmpty()){
                            context?.let { it1 -> ToastStateHandling.toastError(it1,toastSelectAccount,
                                Toast.LENGTH_SHORT
                            ) }}
                        else {context?.let { it1 -> ToastStateHandling.toastError(it1,"Please Select Account",
                            Toast.LENGTH_SHORT
                        ) }}}
                } else if (it.data?.accountId != null) {
                    Log.d(TAG, "onCreateViewAccountID:$accountID")
                    itemClicked(accountId!!, lat!!, long!!, onp_id!!, contactNumber,crop_id.toString().toInt(),cropName,onpName)
                }
            }


        }
        initViewBackClick()
        mvvm()

    }





    private fun mvvm() {


    }
    fun translationSoilTesting() {
        TranslationsManager().loadString("plot_number_and_sample_collection_address", binding.plot,"Plot Number and Sample Collection Address")
        TranslationsManager().loadString("plot_number", binding.plotNumber,"Plot Number ")
        TranslationsManager().loadString("pincode", binding.pincodeNumber,"Pincode ")
        TranslationsManager().loadString("Address", binding.Address,"Address ")
        TranslationsManager().loadString("city_village", binding.City,"City / Village ")
        TranslationsManager().loadString("state", binding.State,"State ")
        TranslationsManager().loadString("mobile", binding.Mobile,"Mobile Number")
        TranslationsManager().loadString("submit", binding.tvCheckCrop,"Submit")


    }

    private fun initViewBackClick() {
        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }


    }

    private fun itemClicked(
        account_id: Int,
        lat: String,
        long: String,
        onp_number: Int,
        phoneNumber: String,
        crop_id: Int,
        cropName: String?,
        onpName: String?
    ) {
        binding.cardCheckHealth.setOnClickListener {
            ploteNumber = binding.etPlotNumber.text.toString().trim()
            pincode = binding.etPincodeNumber.text.toString().trim()
            address = binding.etAddress.text.toString().trim()
            city = binding.etCity.text.toString().trim()
            state = binding.etState.text.toString().trim()
            mobileNumber = binding.tvContact.text.toString().trim()
            if (ploteNumber.isEmpty()) {
                binding.etPlotNumber.error = "Plot Number should not be empty"
                return@setOnClickListener
            } else if (pincode.isEmpty()) {
                binding.etPincodeNumber.error = "Enter Pincode "
                return@setOnClickListener
            } else if (pincode.length != 6) {
                CoroutineScope(Dispatchers.Main).launch {
                    val toastPinCode = TranslationsManager().getString("txt_error_pincode")
                    if(!toastPinCode.isNullOrEmpty()){
                        context?.let { it1 -> ToastStateHandling.toastError(it1,toastPinCode,
                            Toast.LENGTH_SHORT
                        ) }}
                    else {context?.let { it1 -> ToastStateHandling.toastError(it1,"Please give a correct Pincode",
                        Toast.LENGTH_SHORT
                    ) }}}
//                ToastStateHandling.toastError(requireContext(), "PLease Enter 6 Digit Pincode", Toast.LENGTH_SHORT)
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
                binding.tvContact.error = "Enter Mobile Number"
                return@setOnClickListener
            } else if (ploteNumber.isNotEmpty() && pincode.isNotEmpty() && address.isNotEmpty() && city.isNotEmpty() && state.isNotEmpty() && mobileNumber.isNotEmpty()) {
                binding.cardCheckHealth.visibility = View.GONE
                binding.progressBar2.visibility = View.VISIBLE

                val eventBundle=Bundle()
                eventBundle.putString("cropName", cropName)
                eventBundle.putString("onpName",onpName)
                eventBundle.putString("plotNumber",binding.etPlotNumber.text.toString())
                eventBundle.putString("plotNumber",binding.etAddress.text.toString())
                EventItemClickHandling.calculateItemClickEvent("Soiltesting_checksoilhealth",eventBundle)

                soilViewModel.postNewSoil(
                    account_id, lat.toDouble(), long.toDouble(),
                    onp_number,
                    binding.etPlotNumber.text.toString(),
                    binding.etPincodeNumber.text.toString(),
                    binding.etAddress.text.toString(),
                    binding.etState.text.toString(),
                    binding.etCity.text.toString(),
                    phoneNumber,
                    crop_id
                ).observe(requireActivity()) {
                    when (it) {
                        is Resource.Success -> {
                            binding.cardCheckHealth.visibility = View.VISIBLE
                            binding.progressBar2.visibility = View.GONE
                            val bundle = Bundle()
                            bundle.putString(
                                "soil_test_number",
                                it.data?.data?.soilTestNumber.toString()
                            )
                            Log.d(
                                TAG,
                                "initViewsendingId: " + it.data!!.data.soilTestNumber.toString()
                            )
                            findNavController().navigate(
                                R.id.action_newSoilTestFormFragment_to_sucessFullFragment,
                                bundle
                            )
                        }
                        is Resource.Loading -> {
                            CoroutineScope(Dispatchers.Main).launch {
                                val toastLoading = TranslationsManager().getString("loading")
                                if(!toastLoading.isNullOrEmpty()){
                                    context?.let { it1 -> ToastStateHandling.toastError(it1,toastLoading,
                                        Toast.LENGTH_SHORT
                                    ) }}
                                else {context?.let { it1 -> ToastStateHandling.toastError(it1,"Loading",
                                    Toast.LENGTH_SHORT
                                ) }}}
                            binding.cardCheckHealth.visibility = View.GONE
                            binding.progressBar2.visibility = View.VISIBLE

                        }
                        is Resource.Error -> {
                      AppUtils.translatedToastServerErrorOccurred(context)

                            binding.cardCheckHealth.visibility = View.VISIBLE
                            binding.progressBar2.visibility = View.GONE
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
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("NewSoilTestingFormFragment")
    }

}


