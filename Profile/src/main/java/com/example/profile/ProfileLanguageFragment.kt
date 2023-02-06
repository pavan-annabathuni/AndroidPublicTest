package com.example.profile

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.profile.adapter.LanguageAdapter
import com.example.profile.databinding.FragmentProfileLanguageBinding
import com.example.profile.viewModel.EditProfileViewModel
import com.waycool.data.Local.LocalSource
import com.waycool.data.Sync.syncer.*
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.repository.domainModels.LanguageMasterDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.featurelogin.loginViewModel.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ProfileLanguageFragment : Fragment() {


    private lateinit var languageAdapter: LanguageAdapter
    lateinit var binding: FragmentProfileLanguageBinding
    lateinit var field: java.util.HashMap<String, String>
    private val languageViewModel: LoginViewModel by lazy {
        ViewModelProvider(this).get(LoginViewModel::class.java)
    }
    private val viewModel: EditProfileViewModel by lazy {
        ViewModelProviders.of(this).get(EditProfileViewModel::class.java)
    }
    var selectedLanguage: LanguageMasterDomain? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileLanguageBinding.inflate(layoutInflater)
        binding.languageRecyclerview.layoutManager = GridLayoutManager(context, 3)
        setTranslation()

        languageViewModel.viewModelScope.launch {
            val langCode = LocalSource.getLanguageCode()
            languageAdapter = LanguageAdapter(langCode)
            binding.languageRecyclerview.adapter = languageAdapter
            languageAdapter.onItemClick = {
                setTranslationOnItemSelect(it.langCode)
                selectedLanguage = it
            }
        }
        if (NetworkUtil.getConnectivityStatusString(context) == 0) {
            context?.let { ToastStateHandling.toastError(it, "No internet", Toast.LENGTH_LONG) }
        } else {
            languageViewModel.getLanguageList().observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> {
                        languageAdapter.setData(it.data ?: emptyList())
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Error -> {

                    }
                }
            }
        }


        binding.doneBtn.setOnClickListener {

             binding.progressBar.visibility = View.VISIBLE
            binding.doneBtn.visibility = View.GONE
            GlobalScope.launch {
                LocalSource.deleteAllMyCrops()
                LocalSource.deleteTags()
                LocalSource.deleteCropMaster()
                LocalSource.deleteCropInformation()
                LocalSource.deletePestDisease()
                MyCropSyncer().invalidateSync()
                CropMasterSyncer.invalidateSync()
                CropInformationSyncer().invalidateSync()
                TagsSyncer().invalidateSync()
                PestDiseaseSyncer().invalidateSync()

            }
            if (selectedLanguage == null)

                ToastStateHandling.toastError(requireContext(), "Select Language", Toast.LENGTH_SHORT)
            else {
                languageViewModel.setSelectedLanguage(
                    selectedLanguage!!.langCode,
                    selectedLanguage!!.id,
                    selectedLanguage!!.langNative,
                )

            TranslationsManager().refreshTranslations()
            viewModel.viewModelScope.launch {
                val langCode = selectedLanguage!!.id
                EventClickHandling.calculateClickEvent("Profile_language_name$langCode")
                field = HashMap()
                langCode?.let { it1 -> field.put("lang_id", it1.toString())
                    viewModel.getProfileRepository(field).observe(viewLifecycleOwner){
            }}
                Handler().postDelayed({
                    binding.progressBar.visibility = View.GONE
                    Navigation.findNavController(binding.root)
                        .navigateUp()
                },2000)

            }

            }
        }

        binding.imgBack.setOnClickListener() {
            this.findNavController().navigateUp()
        }
        return binding.root
    }

    private fun setTranslationOnItemSelect(langCode: String?) {
        when (langCode) {
            "en" -> {
                binding.helloTv.text="Language"
                binding.selectLanguageTv.text="Choose your language"
                binding.doneBtn.text="Update"

            }
            "hi" -> {
                binding.helloTv.text="भाषा"
                binding.selectLanguageTv.text="अपनी भाषा चुनें"
                binding.doneBtn.text="अद्यतन"
            }
            "kn" -> {
                binding.helloTv.text="ಭಾಷೆ"
                binding.selectLanguageTv.text="ನಿಮ್ಮ ಭಾಷೆಯನ್ನು ಆರಿಸಿ"
                binding.doneBtn.text="ನವೀಕರಿಸಿ"
            }
            "te" -> {
                binding.helloTv.text="భాష"
                binding.selectLanguageTv.text="మీ భాషను ఎంచుకోండి"
                binding.doneBtn.text="నవీకరించు"

            }
            "ta" -> {
                binding.helloTv.text="மொழி"
                binding.selectLanguageTv.text= "உங்கள் மொழியைத் தேர்ந்தெடுக்கவும்"
                binding.doneBtn.text="புதுப்பிக்கவும்"


            }
            "mr" -> {
                binding.helloTv.text="इंग्रजी"
                binding.selectLanguageTv.text="तुमची भाषा निवडा"
                binding.doneBtn.text="अपडेट करा"
            }
        }
    }
    private fun setTranslation() {
        CoroutineScope(Dispatchers.Main).launch {
            val title = TranslationsManager().getString("str_language")
            binding.helloTv.text = title
        }
        TranslationsManager().loadString("str_choose_lang", binding.selectLanguageTv, "Choose your Language")
        TranslationsManager().loadString("str_update", binding.doneBtn, "Update")
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("ProfileLanguageFragment")
    }
}