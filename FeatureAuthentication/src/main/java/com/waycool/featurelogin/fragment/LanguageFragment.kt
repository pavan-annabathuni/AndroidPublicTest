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
import com.waycool.data.eventscreentime.EventClickHandling
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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
        TranslationsManager().loadString("txt_internet_problem",apiErrorHandlingBinding.tvInternetProblem,"There is a problem with Internet.")
        TranslationsManager().loadString("txt_check_net",apiErrorHandlingBinding.tvCheckInternetConnection,"Please check your Internet connection")
        TranslationsManager().loadString("txt_tryagain",apiErrorHandlingBinding.tvTryAgainInternet,"TRY AGAIN")
        binding.languageRecyclerview.layoutManager = GridLayoutManager(context, 3)
        languageSelectionAdapter = LanguageSelectionAdapter()
        binding.languageRecyclerview.adapter = languageSelectionAdapter

        //making an api call to get Language list
        apiCall(apiErrorHandlingBinding, languageSelectionAdapter!!)

        //Click on Item in Language Adapter
        languageSelectionAdapter?.onItemClick = {
            //setting translations according to selected language
            setTranslation(it.langCode)
            //making an api call to get Language list
            apiCall(apiErrorHandlingBinding, languageSelectionAdapter!!)
            //storing the clicked Language data(LanguageMasterDomain) to selectedLanguage variable
            selectedLanguage = it
            languageViewModel.setSelectedLanguage(
                selectedLanguage!!.langCode,
                selectedLanguage!!.id,
                selectedLanguage!!.langNative
            )
        }

        languageViewModel.language.observe(viewLifecycleOwner) {
            it.langCode?.let { langCode ->
                setTranslation(langCode)
                selectedLanguage?.langCode = langCode
                languageSelectionAdapter?.updatedSelectedLanguage(langCode)
                selectedLanguage = it
            }
        }

        //Click on the Continue button present in UI
        binding.doneBtn.setOnClickListener {
            //checking if the selectedLanguage is null or not
            //if selected is null go to the "IF" condition
            if (selectedLanguage == null) {
                //Checking the internet connectivity
                //If Internet is not available go to "IF" condition
                if (NetworkUtil.getConnectivityStatusString(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
                    //Keep making api call to get language list
                    apiCall(apiErrorHandlingBinding, languageSelectionAdapter!!)
                }
                //If Internet is is available go to "ELSE" condition
                else {
                    //Since the selectedLanguage is null we are showing a toast with a message-"Please select Language"
                    context?.let {
                        CoroutineScope(Dispatchers.Main).launch {
                            val toastSelectLanguage = TranslationsManager().getString("select_language")
                            if(!toastSelectLanguage.isNullOrEmpty()){
                                context?.let { it1 -> ToastStateHandling.toastError(it1,toastSelectLanguage,
                                    LENGTH_SHORT
                                ) }}
                            else {context?.let { it1 -> ToastStateHandling.toastError(it1,"Please select Language",
                                LENGTH_SHORT
                            ) }}}
                    }
                }
            }
            //if selected is not null go to the "ELSE" condition
            else {
                //Checking the internet connectivity
                //If Internet is not available go to "IF" condition
                if (NetworkUtil.getConnectivityStatusString(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
                    //Keep making api call to get language list
                    apiCall(apiErrorHandlingBinding, languageSelectionAdapter!!)
                }
                //If Internet is is available go to "ELSE" condition
                else {
                    //Storing data to the DataStoreManager

                    binding.progressBar.visibility = View.VISIBLE
                    binding.doneBtn.visibility = View.GONE
                    val args = Bundle()
                    args.putString("langCode", selectedLanguage!!.langCode)

                    //On selection of language the translation was not reflecting quickly in the Login Fragment, So we have added a post delay
                    Handler().postDelayed({
                        binding.progressBar.visibility = View.GONE
                        binding.doneBtn.visibility = View.VISIBLE

                        //navigation from language fragment to login Nav
                        Navigation.findNavController(binding.root)
                            .navigate(R.id.action_languageFragment_to_login_nav,args)
                    }, 1500)
                }

            }
            //Click event on selection of any language
            EventClickHandling.calculateClickEvent("language_selection$selectedLanguage")
        }

        //If internet is not available user get the screen Network Error
        //This is the click on the "TRY AGAIN" button provided in UI
        apiErrorHandlingBinding.clBtnTryAgainInternet.setOnClickListener {
            //After clicking on "Try again" button we will make the api call to load the language list
            apiCall(apiErrorHandlingBinding, languageSelectionAdapter!!)
        }

        return binding.root
    }

    private fun setTranslation(langCode: String?) {
        when (langCode) {
            "en" -> {
                binding.helloTv.text="Welcome to Outgrow"
                binding.selectLanguageTv.text="Select your language"
                binding.doneBtn.text="Continue"

            }
            "hi" -> {
                binding.helloTv.text="आउटग्रो में आपका स्वागत है"
                binding.selectLanguageTv.text="अपनी भाषा का चयन करें"
                binding.doneBtn.text="जारी रखें"
            }
            "kn" -> {
                binding.helloTv.text="ಔಟ್\u200Cಗ್ರೋಗೆ ಸುಸ್ವಾಗತ"
                binding.selectLanguageTv.text="ನಿಮ್ಮ ಭಾಷೆಯನ್ನು ಆಯ್ಕೆಮಾಡಿ"
                binding.doneBtn.text="ಮುಂದುವರಿಸಿ"
            }
            "te" -> {
                binding.helloTv.text="ఔట్ గ్రో కి స్వాగతం"
                binding.selectLanguageTv.text="మీ భాషను ఎంచుకోండి"
                binding.doneBtn.text="కొనసాగించు"

            }
            "ta" -> {
                binding.helloTv.text="அவுட்குரோவிற்கு வரவேற்கிறோம்"
                binding.selectLanguageTv.text= "உங்கள் மொழியைத் தேர்ந்தெடுக்கவும்"
                binding.doneBtn.text="தொடரவும்"


            }
            "mr" -> {
                binding.helloTv.text="आऊटग्रो मध्ये आपले स्वागत आहे"
                binding.selectLanguageTv.text="तुमची भाषा निवडा"
                binding.doneBtn.text="सुरू"

            }

        }

    }

    private fun apiCall(
        apiErrorHandlingBinding: ApiErrorHandlingBinding,
        languageSelectionAdapter: LanguageSelectionAdapter
    ) {
        //Check Internet Availability
        //if Internet is not available go to "IF" condition
        if (NetworkUtil.getConnectivityStatusString(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
            //If Network is not available we will make the UI of No internet as VISIBLE
            binding.clInclude.visibility = View.VISIBLE
            //If Network is not available we will make the UI components of No internet as VISIBLE
            apiErrorHandlingBinding.clInternetError.visibility = View.VISIBLE

            //Here We are showing the toast as "Please check your internet connection"
            CoroutineScope(Dispatchers.Main).launch {
                val toastCheckInternet = TranslationsManager().getString("check_your_interent")
                if(!toastCheckInternet.isNullOrEmpty()){
                    context?.let { it1 -> ToastStateHandling.toastSuccess(it1,toastCheckInternet,
                        LENGTH_SHORT
                    ) }}
                else {context?.let { it1 -> ToastStateHandling.toastSuccess(it1,"Please check your internet connection",
                    LENGTH_SHORT
                ) }}}
            //Setting visibility of some views as "GONE
            binding.doneBtn.visibility = View.GONE
            binding.helloTv.visibility = View.GONE
            binding.selectLanguageTv.visibility = View.GONE
        }
        //if Internet is  available go to "ELSE" condition
        else {

            //Api Call to get the Language List
            languageViewModel.getLanguageList().observe(viewLifecycleOwner) {
                when (it) {
                    //Success State
                    is Resource.Success -> {
                        //If the data is null or empty show loader
                        if(!it.data.isNullOrEmpty()){
                            binding.progressBar.visibility=View.GONE
                        }
                        //Set visibility of views
                        binding.clInclude.visibility = View.GONE
                        binding.doneBtn.visibility = View.VISIBLE
                        binding.helloTv.visibility = View.VISIBLE
                        binding.selectLanguageTv.visibility = View.VISIBLE
                        apiErrorHandlingBinding.clInternetError.visibility = View.GONE
                        //Send data to the LanguageSelectionAdapter
                        it.data?.let { it1 -> languageSelectionAdapter.setData(it1) }
                    }
                    //Loading State
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    //Error State
                    is Resource.Error -> {
                        CoroutineScope(Dispatchers.Main).launch {
                            val toastServerError = TranslationsManager().getString("server_error")
                            if(!toastServerError.isNullOrEmpty()){
                                context?.let { it1 -> ToastStateHandling.toastError(it1,toastServerError,
                                    LENGTH_SHORT
                                ) }}
                            else {context?.let { it1 -> ToastStateHandling.toastError(it1,"Server Error Occurred",
                                LENGTH_SHORT
                            ) }}}
                    }
                }
            }
        }
    }

    //Time Spent on the Language Screen
    override fun onResume() {
        super.onResume()
        languageViewModel.getSelectedLangCode()
        EventScreenTimeHandling.calculateScreenTime("Language Screen")
    }
}