package com.example.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.profile.adapter.LanguageAdapter
import com.example.profile.databinding.FragmentLanguageBinding
import com.waycool.data.Repository.DomainModels.LanguageMasterDomain
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.featurelogin.R
import com.waycool.featurelogin.adapter.LanguageSelectionAdapter
import com.waycool.featurelogin.loginViewModel.LoginViewModel


class LanguageFragment : Fragment() {


    lateinit var binding: FragmentLanguageBinding
    private val languageViewModel: LoginViewModel by lazy {
        ViewModelProvider(this).get(LoginViewModel::class.java)
    }
    var selectedLanguage: LanguageMasterDomain? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLanguageBinding.inflate(layoutInflater)
        binding.languageRecyclerview.layoutManager = GridLayoutManager(context, 3)
        // binding.languageRecyclerview.setHasFixedSize(true)
        var languageAdapter = LanguageAdapter()
        binding.languageRecyclerview.adapter = languageAdapter
        if (NetworkUtil.getConnectivityStatusString(context) == 0) {
            Toast.makeText(context, "No internet", Toast.LENGTH_LONG).show()
        } else {
            languageViewModel.getLanguageList().observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> {
                        languageAdapter.setData(it.data?: emptyList())
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Error -> {

                    }
                }
            }
        }

        languageAdapter.onItemClick = {
            selectedLanguage = it
        }
        binding.doneBtn.setOnClickListener {
            if (selectedLanguage == null)
                Toast.makeText(requireContext(), "Select Language", Toast.LENGTH_SHORT).show()
            else {
                languageViewModel.setSelectedLanguage(
                    selectedLanguage!!.langCode,
                    selectedLanguage!!.id
                )
                Navigation.findNavController(binding.root)
                    .navigate(LanguageFragmentDirections.actionLanguageFragment3ToMyProfileFragment())
            }
        }
        binding.imgBack.setOnClickListener(){
            this.findNavController().navigate(LanguageFragmentDirections.actionLanguageFragment3ToMyProfileFragment())
        }
//        binding.privacyCheckBox.setOnCheckedChangeListener { compoundButton, b ->
//            binding.doneBtn.isEnabled = b
//        }
//        termsConditionClick()
        return binding.root
    }
}