package com.waycool.featurelogin.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.waycool.data.repository.domainModels.LanguageMasterDomain
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.featurelogin.R
import com.waycool.featurelogin.adapter.LanguageSelectionAdapter
import com.waycool.featurelogin.databinding.FragmentLanguageBinding
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
        var languageSelectionAdapter = LanguageSelectionAdapter()
        binding.languageRecyclerview.adapter = languageSelectionAdapter
        if (NetworkUtil.getConnectivityStatusString(context) == 0) {
            Toast.makeText(context, "No internet", Toast.LENGTH_LONG).show()
        } else {
            languageViewModel.getLanguageList().observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> {
                        languageSelectionAdapter.setData(it.data ?: emptyList())
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Error -> {

                    }
                }
            }
        }

//        if (selectedLanguage == null) {
//            binding.doneBtn.isEnabled = false
//        }
        languageSelectionAdapter.onItemClick = {
            selectedLanguage = it
//            binding.doneBtn.isEnabled = selectedLanguage != null
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
                    .navigate(R.id.action_languageFragment_to_login_nav)
            }
        }
//        binding.privacyCheckBox.setOnCheckedChangeListener { compoundButton, b ->
//            binding.doneBtn.isEnabled = b
//        }
//        termsConditionClick()
        return binding.root
    }

//    fun termsConditionClick() {
//        val text = "I agree to Terms of Use ,Privacy Policy and to share my location"
//        val spannableString = SpannableString(text)
//        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
//            override fun onClick(widget: View) {
//                val intent: Intent = Intent(context, ExoPlayerActivity::class.java)
//                intent.putExtra("url", "http://one.waycool.in/Terms_and_Conditions.html")
//                intent.putExtra("tittle", "Terms and Conditions")
//                requireActivity().startActivity(intent)
//            }
//
//            override fun updateDrawState(ds: TextPaint) {
//                super.updateDrawState(ds)
//                ds.isUnderlineText = false
//                ds.color = context?.let {
//                    ContextCompat.getColor(
//                        it,
//                        com.waycool.uicomponents.R.color.green
//                    )
//                }!!
//
//            }
//        }
//        val clickableSpan2: ClickableSpan = object : ClickableSpan() {
//            override fun onClick(widget: View) {
//                val intent: Intent = Intent(context, ExoPlayerActivity::class.java)
//                intent.putExtra("url", "http://one.waycool.in/Outgrow_PrivacyPolicy.html")
//                intent.putExtra("tittle", "Privacy Policy")
//                requireActivity().startActivity(intent)
//            }
//
//            override fun updateDrawState(ds: TextPaint) {
//                super.updateDrawState(ds)
//                ds.isUnderlineText = false
//                ds.color = context?.let {
//                    ContextCompat.getColor(
//                        it,
//                        com.waycool.uicomponents.R.color.green
//                    )
//                }!!
//
//            }
//        }
//        val boldSpan = StyleSpan(Typeface.BOLD)
//        spannableString.setSpan(boldSpan, 11, 39, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        spannableString.setSpan(clickableSpan1, 11, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        spannableString.setSpan(clickableSpan2, 25, 39, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        binding.privacyPolicyTv.text = spannableString
//        binding.privacyPolicyTv.movementMethod = LinkMovementMethod.getInstance()
//        binding.privacyPolicyTv.highlightColor =
//            context?.let { ContextCompat.getColor(it, com.waycool.uicomponents.R.color.white) }!!
//
//    }


//    override fun onClick(p0: View?) {
//        if (p0 != null) {
//            when (p0.getId()) {
//                R.id.done_btn -> if (binding.privacyCheckBox.isChecked) {
//                    if (languageList.size > 0) {
//
//                        SharedPreferenceUtility.setUserFirst(context, "1")
//                        Navigation.findNavController(binding.root)
//                            .navigate(R.id.action_languageFragment_to_login_nav)
////                        requireActivity().supportFragmentManager.beginTransaction()
////                            .replace(
////                                R.id.login_fragment_frame,
////                                LoginFragment(),
////                                LoginFragment().javaClass.simpleName
////                            )
////                            .addToBackStack(null).commit()
//
//                    } else {
//                        SharedPreferenceUtility.setUserFirst(context, "1")
//                        Navigation.findNavController(binding.root)
//                            .navigate(R.id.action_languageFragment_to_login_nav)
////                        requireActivity().supportFragmentManager.beginTransaction()
////                            .replace(
////                                R.id.login_fragment_frame,
////                                LoginFragment(),
////                                LoginFragment().javaClass.simpleName
////                            )
////                            .addToBackStack(null).commit()
//
//                    }
//                } else {
//                    //AppUtil.Toast(applicationContext, "Please Accept privacy and policy")
//                }
//            }
//        }
//    }

}