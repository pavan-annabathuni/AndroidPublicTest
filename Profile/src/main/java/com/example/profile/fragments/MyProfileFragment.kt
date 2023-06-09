package com.example.profile.fragments

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.profile.databinding.FragmentMyProfileBinding
import com.example.profile.viewModel.EditProfileViewModel
import com.waycool.data.Local.DataStorePref.DataStoreManager
import com.waycool.data.Local.LocalSource
import com.waycool.data.Sync.SyncManager
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.AppUtils
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.featurechat.FeatureChat
import com.waycool.featurelogin.activity.LoginActivity
import com.waycool.featurelogin.activity.PrivacyPolicyActivity
import com.waycool.featurelogin.deeplink.DeepLinkNavigator.INVITE_SHARE_LINK
import com.waycool.featurelogin.loginViewModel.LoginViewModel
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding
import com.waycool.uicomponents.utils.Constants.PLAY_STORE_LINK
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MyProfileFragment : Fragment() {
    private lateinit var binding: FragmentMyProfileBinding
    private lateinit var apiErrorHandlingBinding: ApiErrorHandlingBinding

    private val loginViewModel: LoginViewModel by lazy { ViewModelProvider(this)[LoginViewModel::class.java] }
    private val viewModel: EditProfileViewModel by lazy {
        ViewModelProviders.of(this)[EditProfileViewModel::class.java]
    }
    private lateinit var appVer: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMyProfileBinding.inflate(inflater)

        apiErrorHandlingBinding = binding.errorState
        AppUtils.networkErrorStateTranslations(apiErrorHandlingBinding)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.llInviteFarmer.setOnClickListener {
            it?.isEnabled = false
            Handler().postDelayed({
                it?.isEnabled = true
            },2000)
            EventClickHandling.calculateClickEvent("invite_farmer")
            shareInviteLink()
        }

        viewModel.viewModelScope.launch {
            appVer = TranslationsManager().getString("str_app_ver")
            binding.version.text = buildString {
                append(appVer)
                append(" ")
                try {
                    val pInfo: PackageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
                    val version: String = pInfo.versionName
                    append (version)
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
//        try {
//            val pInfo: PackageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
//            val version: String = pInfo.versionName
//            binding.version.text = (version)
//        } catch (e: PackageManager.NameNotFoundException) {
//            e.printStackTrace()
//        }
        networkCall()
        apiErrorHandlingBinding.clBtnTryAgainInternet.setOnClickListener {
            networkCall()
        }

        viewModel.viewModelScope.launch {
            binding.language.text = LocalSource.getLanguage()
        }
        onClick()
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    this@MyProfileFragment.findNavController().navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            callback
        )
        translation()
        observer()
        EventClickHandling.calculateClickEvent("Profile_landing")
        return binding.root
    }


    private fun shareInviteLink() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, INVITE_SHARE_LINK)
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, "choose one"))
    }

        private fun networkCall() {
            if (NetworkUtil.getConnectivityStatusString(context) == 0) {
                binding.clInclude.visibility = View.VISIBLE
                apiErrorHandlingBinding.clInternetError.visibility = View.VISIBLE
               AppUtils.translatedToastCheckInternet(context)
            } else {
                binding.clInclude.visibility = View.GONE
                apiErrorHandlingBinding.clInternetError.visibility = View.GONE
                observer()

            }

        }


        private fun observer(): Boolean {
            viewModel.getUserProfileDetails().observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                    }
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Error -> {}
                }
                if (it.data?.subscription == 0) {
                    binding.llFarmSupport.visibility = View.GONE
                } else {
                    binding.llFarmSupport.visibility = View.VISIBLE
                }
                binding.username.text = it.data?.name
                binding.phoneNo.text = buildString {
                    append("+91 ")
                    append(it.data?.phone)
                }
                if (it.data?.profile?.remotePhotoUrl != null) {
                    Glide.with(requireContext()).load(it.data?.profile?.remotePhotoUrl)
                        .into(binding.proPic)
                    Log.d("ProfilePic", "observer: $it")

                }
            }

            return true
        }

        private fun onClick() {
            binding.llMyProfile.setOnClickListener {
                this.findNavController()
                    .navigate(MyProfileFragmentDirections.actionMyProfileFragmentToEditProfileFragment())
                EventClickHandling.calculateClickEvent("farmer_profile")
            }
            binding.llFarmSupport.setOnClickListener {
                EventClickHandling.calculateClickEvent("profile_farm_support")
                this.findNavController()
                    .navigate(MyProfileFragmentDirections.actionMyProfileFragmentToFarmSupportFragment())
            }
            binding.rateUs.setOnClickListener {
                EventClickHandling.calculateClickEvent("rate_us")
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(PLAY_STORE_LINK)
                )
                startActivity(intent)
            }
            binding.back.setOnClickListener {
                this.findNavController().navigateUp()
            }
            binding.textView.setOnClickListener {
                EventClickHandling.calculateClickEvent("Privacy_policy_profile")
                val intent = Intent(context, PrivacyPolicyActivity::class.java)
                intent.putExtra("url", "https://admindev.outgrowdigital.com/privacy-policy")
                intent.putExtra("tittle", "Privacy Policy")
                requireActivity().startActivity(intent)
            }
            binding.textView2.setOnClickListener {

                EventClickHandling.calculateClickEvent("Terms_of_use_profile")
                val intent = Intent(context, PrivacyPolicyActivity::class.java)
                intent.putExtra("url", "https://admindev.outgrowdigital.com/terms-and-conditions")
                intent.putExtra("tittle", "Terms and Conditions")
                requireActivity().startActivity(intent)

            }
            binding.llAboutOutgrow.setOnClickListener {
                EventClickHandling.calculateClickEvent("profile_farm_support")
                this.findNavController()
                    .navigate(MyProfileFragmentDirections.actionMyProfileFragmentToAboutOutgrowFragment())
            }

            binding.cvChat.setOnClickListener {
                EventClickHandling.calculateClickEvent("chat_support_profile")
                FeatureChat.zenDeskInit(requireContext())
            }

            binding.llLanguage.setOnClickListener {
                EventClickHandling.calculateClickEvent("Profile_language")
                this.findNavController()
                    .navigate(MyProfileFragmentDirections.actionMyProfileFragmentToLanguageFragment3())
            }
            val mobileNo = loginViewModel.getMobileNumber()
            if (mobileNo != null)
                binding.logout.setOnClickListener {
                    EventClickHandling.calculateClickEvent("logout")
                    loginViewModel.logout(mobileNo)
                        .observe(viewLifecycleOwner) {

                            // loginViewModel.setUserToken(null)
                            loginViewModel.setIsLoggedIn(false)
                            FeatureChat.zendeskLogout()
                            viewModel.viewModelScope.launch {
                                val toast = TranslationsManager().getString("successfully_logout")
                                if (toast.isNullOrEmpty()) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        val toastLogout = TranslationsManager().getString("successfully_logout")
                                        if(!toastLogout.isNullOrEmpty()){
                                            context?.let { it1 -> ToastStateHandling.toastSuccess(it1,toastLogout,
                                                LENGTH_SHORT
                                            ) }}
                                        else {context?.let { it1 -> ToastStateHandling.toastSuccess(it1,"Successfully Logout",
                                            LENGTH_SHORT
                                        ) }}}
                                } else {
                                    context?.let { it1 ->
                                        ToastStateHandling.toastSuccess(
                                            it1,
                                            toast,
                                            Toast.LENGTH_SHORT
                                        )
                                    }
                                }
                            }

                        }
                    moveToLogin()
                }

        }

        /* Logout Function and Clearing all Room db and Data Store data */
        private fun moveToLogin() {
            val intent = Intent(context, LoginActivity::class.java)

            GlobalScope.launch {
                LocalSource.deleteAllMyCrops()
                LocalSource.deleteTags()
//                LocalSource.deleteCropMaster()
//                LocalSource.deleteCropInformation()
//                LocalSource.deletePestDisease()
                LocalSource.deleteMyFarms()
                SyncManager.invalidateAll()
                DataStoreManager.clearData()

            }
            startActivity(intent)
            activity?.finish()
        }

        private fun translation() {
            TranslationsManager().loadString(
                "str_farmer_profile",
                binding.tvFarmer,
                "Farmer Profile"
            )
            TranslationsManager().loadString("str_myProfile", binding.tvMyProfile, "My Profile")
            TranslationsManager().loadString("str_language", binding.tvLang, "Language")
            TranslationsManager().loadString("str_about", binding.tvAbout, "About Outgrow")
            TranslationsManager().loadString("str_invite_farmer", binding.tvInvite, "Invite Farmer")
            TranslationsManager().loadString(
                "str_ask_chat",
                binding.tvAsk,
                "Feel free to Ask, We ready to help \n" + "Contact us"
            )
            TranslationsManager().loadString("str_rate_us", binding.tvRate, "Rate us")
            TranslationsManager().loadString("str_logout", binding.tvLogout, "Logout")
            TranslationsManager().loadString(
                "str_privacy_policy",
                binding.textView,
                "Privacy Policy"
            )
            TranslationsManager().loadString("str_terms", binding.textView2, "Terms & Conditions")
            TranslationsManager().loadString("str_farm_support", binding.tvSupport, "Farm Support")
        }

        override fun onResume() {
            super.onResume()
            EventScreenTimeHandling.calculateScreenTime("MyProfileFragment")
        }

    }

