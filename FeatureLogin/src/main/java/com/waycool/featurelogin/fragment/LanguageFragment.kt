package com.waycool.featurelogin.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.repository.domainModels.LanguageMasterDomain
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.featurelogin.R
import com.waycool.featurelogin.adapter.LanguageSelectionAdapter
import com.waycool.featurelogin.databinding.FragmentLanguageBinding
import com.waycool.featurelogin.loginViewModel.LoginViewModel
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding


class LanguageFragment : Fragment() {

    private var languageSelectionAdapter: LanguageSelectionAdapter? = null
    lateinit var binding: FragmentLanguageBinding
    private val languageViewModel: LoginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }
    var selectedLanguage: LanguageMasterDomain? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLanguageBinding.inflate(layoutInflater)

        val apiErrorHandlingBinding = binding.errorState
        binding.languageRecyclerview.layoutManager = GridLayoutManager(context, 3)
        languageSelectionAdapter = LanguageSelectionAdapter()
        binding.languageRecyclerview.adapter = languageSelectionAdapter
        apiCall(apiErrorHandlingBinding, languageSelectionAdapter!!)
        languageSelectionAdapter!!.onItemClick = {
            setTranslation(it)
            apiCall(apiErrorHandlingBinding, languageSelectionAdapter!!)
            selectedLanguage = it
        }
        binding.doneBtn.setOnClickListener {
            if (selectedLanguage == null) {
                if (NetworkUtil.getConnectivityStatusString(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
                    apiCall(apiErrorHandlingBinding, languageSelectionAdapter!!)
                } else {
                    context?.let { it1 ->
                        ToastStateHandling.toastError(it1, "Please select Language", LENGTH_SHORT)
                    }
                }
            } else {
                if (NetworkUtil.getConnectivityStatusString(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
                    apiCall(apiErrorHandlingBinding, languageSelectionAdapter!!)
                } else {
                    languageViewModel.setSelectedLanguage(
                        selectedLanguage!!.langCode,
                        selectedLanguage!!.id,
                        selectedLanguage!!.langNative
                    )
                    binding.pb.visibility = View.VISIBLE
                    binding.doneBtn.visibility = View.GONE
                    val args = Bundle()
                    args.putString("langCode", selectedLanguage!!.langCode)

                    Handler().postDelayed({
                        binding.pb.visibility = View.GONE
                        binding.doneBtn.visibility = View.VISIBLE

                        Navigation.findNavController(binding.root)
                            .navigate(R.id.action_languageFragment_to_login_nav,args)
                    }, 1500)
                }

            }
            EventClickHandling.calculateClickEvent("language_selection$selectedLanguage")
        }

        apiErrorHandlingBinding.clBtnTryAgainInternet.setOnClickListener {
            apiCall(apiErrorHandlingBinding, languageSelectionAdapter!!)
            languageSelectionAdapter!!.onItemClick = {
                setTranslation(it)
                selectedLanguage = it
            }
        }


        return binding.root
    }

    private fun setTranslation(languageMasterDomain: LanguageMasterDomain) {
        when (languageMasterDomain.langCode) {
            "en" -> {
                binding.helloTv.text="Welcome to Outgrow"
                binding.selectLanguageTv.text="Select your language"
                binding.doneBtn.text="Continue"
            }
            "hi" -> {
                binding.helloTv.text="आउटग्रो में आपका स्वागत है"
                binding.selectLanguageTv.text="Select your language"
                binding.doneBtn.text="जारी रखें"
            }
            "kn" -> {
                binding.helloTv.text="ಔಟ್\u200Cಗ್ರೋಗೆ ಸುಸ್ವಾಗತ"
                binding.selectLanguageTv.text="Select your language"
                binding.doneBtn.text="ಮುಂದುವರಿಸಿ"


            }
            "te" -> {
                binding.helloTv.text="ఔట్ గ్రో కి స్వాగతం"
                binding.selectLanguageTv.text="Select your language"
                binding.doneBtn.text="కొనసాగించు"

            }
            "ta" -> {
                binding.helloTv.text="அவுட்குரோவிற்கு வரவேற்கிறோம்"
                binding.selectLanguageTv.text="Select your language"
                binding.doneBtn.text="தொடரவும்"


            }
            "mr" -> {
                binding.helloTv.text="आऊटग्रो मध्ये आपले स्वागत आहे"
                binding.selectLanguageTv.text="Select your language"
                binding.doneBtn.text="सुरू"


            }

        }

    }

    private fun apiCall(
        apiErrorHandlingBinding: ApiErrorHandlingBinding,
        languageSelectionAdapter: LanguageSelectionAdapter
    ) {
        if (NetworkUtil.getConnectivityStatusString(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
            binding.clInclude.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            apiErrorHandlingBinding.clInternetError.visibility = View.VISIBLE
            context?.let {
                ToastStateHandling.toastError(
                    it, "Please check your internet connection",
                    LENGTH_SHORT
                )
            }
            binding.doneBtn.visibility = View.GONE
            binding.helloTv.visibility = View.GONE
            binding.selectLanguageTv.visibility = View.GONE
        } else {
            languageViewModel.getLanguageList().observe(viewLifecycleOwner) {
                binding.progressBar.visibility = View.VISIBLE
                when (it) {
                    is Resource.Success -> {
                        binding.clInclude.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                        binding.doneBtn.visibility = View.VISIBLE
                        binding.helloTv.visibility = View.VISIBLE
                        binding.selectLanguageTv.visibility = View.VISIBLE
                        apiErrorHandlingBinding.clInternetError.visibility = View.GONE
                        it.data?.let { it1 -> languageSelectionAdapter.setData(it1) }
                    }
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Error -> {
                        ToastStateHandling.toastError(
                            requireContext(),
                            "Server Error",
                            LENGTH_SHORT
                        )
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("LanguageFragment")
    }
}