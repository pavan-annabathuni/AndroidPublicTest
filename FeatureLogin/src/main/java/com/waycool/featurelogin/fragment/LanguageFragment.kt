package com.waycool.featurelogin.fragment

import android.os.Bundle
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
import com.waycool.data.repository.domainModels.LanguageMasterDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.featurelogin.R
import com.waycool.featurelogin.adapter.LanguageSelectionAdapter
import com.waycool.featurelogin.databinding.FragmentLanguageBinding
import com.waycool.featurelogin.loginViewModel.LoginViewModel
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding


class LanguageFragment : Fragment() {

    private var languageSelectionAdapter: LanguageSelectionAdapter?=null
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
        TranslationsManager().loadString("welcome_to_outgrow",binding.helloTv,"Welcome to Outgrow")

        val apiErrorHandlingBinding=binding.errorState
        binding.languageRecyclerview.layoutManager = GridLayoutManager(context, 3)
        languageSelectionAdapter = LanguageSelectionAdapter()
        binding.languageRecyclerview.adapter = languageSelectionAdapter

        apiCall(apiErrorHandlingBinding,languageSelectionAdapter!!)
          languageSelectionAdapter!!.onItemClick = {
            selectedLanguage = it
        }

        binding.doneBtn.setOnClickListener {
            if (selectedLanguage == null) {
                if(NetworkUtil.getConnectivityStatusString(context)==NetworkUtil.TYPE_NOT_CONNECTED){
                    apiCall(apiErrorHandlingBinding, languageSelectionAdapter!!)

                }
                else{
                    context?.let { it1 ->
                        ToastStateHandling.toastError(
                            it1,
                            "Please select Language",
                            LENGTH_SHORT
                        )
                    }
                }
            }
            else {
                if(NetworkUtil.getConnectivityStatusString(context)==NetworkUtil.TYPE_NOT_CONNECTED){
                    apiCall(apiErrorHandlingBinding, languageSelectionAdapter!!)
                }
                else{
                    languageViewModel.setSelectedLanguage(
                        selectedLanguage!!.langCode,
                        selectedLanguage!!.id,
                        selectedLanguage!!.langNative
                    )
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.action_languageFragment_to_login_nav)
                }

            }
        }

        apiErrorHandlingBinding.clBtnTryAgainInternet.setOnClickListener {
            apiCall(apiErrorHandlingBinding, languageSelectionAdapter!!)
            languageSelectionAdapter!!.onItemClick = {
                selectedLanguage = it
            }
        }


        return binding.root
    }

    private fun apiCall(
        apiErrorHandlingBinding: ApiErrorHandlingBinding,
        languageSelectionAdapter: LanguageSelectionAdapter
    ) {
        if (NetworkUtil.getConnectivityStatusString(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
            binding.clInclude.visibility=View.VISIBLE
            binding.progressBar.visibility=View.GONE
            apiErrorHandlingBinding.clInternetError.visibility=View.VISIBLE
            context?.let { ToastStateHandling.toastError(it,"Please check your internet connection",
                LENGTH_SHORT
            ) }
            binding.doneBtn.visibility=View.GONE
            binding.helloTv.visibility=View.GONE
            binding.selectLanguageTv.visibility=View.GONE
        }
        else {
            languageViewModel.getLanguageList().observe(viewLifecycleOwner) {
                binding.progressBar.visibility=View.VISIBLE
                when (it) {
                    is Resource.Success -> {
                        binding.clInclude.visibility=View.GONE
                        binding.progressBar.visibility=View.GONE
                        binding.doneBtn.visibility=View.VISIBLE
                        binding.helloTv.visibility=View.VISIBLE
                        binding.selectLanguageTv.visibility=View.VISIBLE
                        apiErrorHandlingBinding.clInternetError.visibility=View.GONE
                        it.data?.let { it1 -> languageSelectionAdapter.setData(it1) }
                    }
                    is Resource.Loading -> {
                        binding.progressBar.visibility=View.VISIBLE
                    }
                    is Resource.Error -> {
                        ToastStateHandling.toastError(requireContext(),"Server Error", LENGTH_SHORT)
                        binding.progressBar.visibility=View.GONE
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