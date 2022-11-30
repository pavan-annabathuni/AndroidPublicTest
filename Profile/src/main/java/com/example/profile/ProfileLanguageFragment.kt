package com.example.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.profile.adapter.LanguageAdapter
import com.example.profile.databinding.FragmentProfileLanguageBinding
import com.waycool.data.Local.LocalSource
import com.waycool.data.repository.domainModels.LanguageMasterDomain
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.featurelogin.loginViewModel.LoginViewModel
import kotlinx.coroutines.launch


class ProfileLanguageFragment : Fragment() {


    private lateinit var languageAdapter: LanguageAdapter
    lateinit var binding: FragmentProfileLanguageBinding
    private val languageViewModel: LoginViewModel by lazy {
        ViewModelProvider(this).get(LoginViewModel::class.java)
    }
    var selectedLanguage: LanguageMasterDomain? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileLanguageBinding.inflate(layoutInflater)
        binding.languageRecyclerview.layoutManager = GridLayoutManager(context, 3)
        // binding.languageRecyclerview.setHasFixedSize(true)

        languageViewModel.viewModelScope.launch {
            val langCode = LocalSource.getLanguageCode()
            languageAdapter = LanguageAdapter(langCode)
            binding.languageRecyclerview.adapter = languageAdapter
            languageAdapter.onItemClick = {
                selectedLanguage = it
            }
        }
        if (NetworkUtil.getConnectivityStatusString(context) == 0) {
            Toast.makeText(context, "No internet", Toast.LENGTH_LONG).show()
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
            if (selectedLanguage == null)
                Toast.makeText(requireContext(), "Select Language", Toast.LENGTH_SHORT).show()
            else {
                languageViewModel.setSelectedLanguage(
                    selectedLanguage!!.langCode,
                    selectedLanguage!!.id,
                    selectedLanguage!!.langNative
                )
                Navigation.findNavController(binding.root)
                    .navigateUp()
            }
        }
        binding.imgBack.setOnClickListener() {
            this.findNavController().navigateUp()
            // this.findNavController().navigate(LanguageFragmentDirections.actionLanguageFragment3ToMyProfileFragment())
        }
//        binding.privacyCheckBox.setOnCheckedChangeListener { compoundButton, b ->
//            binding.doneBtn.isEnabled = b
//        }
//        termsConditionClick()
        return binding.root
    }
}