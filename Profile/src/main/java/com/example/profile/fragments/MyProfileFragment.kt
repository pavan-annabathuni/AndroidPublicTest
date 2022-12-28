package com.example.profile.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.profile.databinding.FragmentMyProfileBinding
import com.example.profile.viewModel.EditProfileViewModel
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.waycool.core.utils.AppSecrets
import com.waycool.data.Local.LocalSource
import com.waycool.data.Sync.syncer.*
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.utils.NetworkUtil
import com.waycool.featurechat.FeatureChat
import com.waycool.featurelogin.activity.LoginMainActivity
import com.waycool.featurelogin.activity.PrivacyPolicyActivity
import com.waycool.featurelogin.loginViewModel.LoginViewModel
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MyProfileFragment : Fragment() {
    private lateinit var binding: FragmentMyProfileBinding
    private lateinit var apiErrorHandlingBinding: ApiErrorHandlingBinding

    private val loginViewModel: LoginViewModel by lazy { ViewModelProvider(this)[LoginViewModel::class.java] }
    private val viewModel: EditProfileViewModel by lazy {
        ViewModelProviders.of(this).get(EditProfileViewModel::class.java)
    }
    private lateinit var appVer:String
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

        apiErrorHandlingBinding=binding.errorState
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        //viewModel.getUsers()
        // viewModel.getUserDetails()

        binding.llInviteFarmer.setOnClickListener {
            shareInviteLink()
        }

        viewModel.viewModelScope.launch {
            appVer = TranslationsManager().getString("str_app_ver")
            binding.version.text = "$appVer ${com.example.profile.BuildConfig.VERSION_NAME}"
        }
        networkCall()
        apiErrorHandlingBinding.clBtnTryAgainInternet.setOnClickListener {
            networkCall()
        }


        binding.version.text = "App Ver ${com.example.profile.BuildConfig.VERSION_NAME}"

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
        return binding.root
    }

    private fun shareInviteLink() {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("https://adminuat.outgrowdigital.com/invite"))
            .setDomainUriPrefix("https://outgrowdev.page.link")
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder()
                    .setFallbackUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.waycool.iwap"))
                    .build()
            )
            .setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder()
                    .setImageUrl(Uri.parse("https://gramworkx.com/PromotionalImages/gramworkx_roundlogo_white_outline.png"))
                    .setTitle("Outgrow sends an invitation for you to join us and grow with us")
                    .setDescription("Outgrow app-Let's grow together")
                    .build()
            )
            .buildShortDynamicLink().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val shortLink: Uri? = task.result.shortLink
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_TEXT, shortLink.toString())
                    sendIntent.type = "text/plain"
                    startActivity(Intent.createChooser(sendIntent, "choose one"))

                }
            }
    }

    private fun networkCall() {
        if (NetworkUtil.getConnectivityStatusString(context) == 0) {
            binding.clInclude.visibility = View.VISIBLE
            apiErrorHandlingBinding.clInternetError.visibility = View.VISIBLE
            context?.let {
                ToastStateHandling.toastError(
                    it,
                    "Please check your internet connectivity",
                    Toast.LENGTH_SHORT
                )
            }
        } else {
            binding.clInclude.visibility = View.GONE
            apiErrorHandlingBinding.clInternetError.visibility = View.GONE
            observer()


        }

    }


    fun observer(): Boolean {
        viewModel.viewModelScope.launch {
              viewModel.getUserProfileDetails().observe(viewLifecycleOwner){
                  binding.username.text = it.data?.data?.name
                  binding.phoneNo.text = "+91 ${it.data?.data?.contact}"
                  if(it.data?.data?.profile?.remotePhotoUrl!=null) {
                      Glide.with(requireContext()).load(it.data?.data?.profile?.remotePhotoUrl).into(binding.proPic)
                      Log.d("ProfilePic", "observer: $it")

                  }
              }
          }

         return true
    }

    private fun onClick() {
        binding.llMyProfile.setOnClickListener() {
            this.findNavController()
                .navigate(MyProfileFragmentDirections.actionMyProfileFragmentToEditProfileFragment())
        }
        binding.llFarmSupport.setOnClickListener() {
            this.findNavController()
                .navigate(MyProfileFragmentDirections.actionMyProfileFragmentToFarmSupportFragment())
        }
   /*     binding.ll4.setOnClickListener() {
            ShareCompat.IntentBuilder.from(requireActivity())
                .setType("text/plain")
                .setChooserTitle("Chooser title")
                .setText("http://play.google.com/store/apps/details?id=" + requireActivity().getPackageName())
                .startChooser();
        }*/
        binding.rateUs.setOnClickListener(){
            val intent = Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=com.waycool.iwap"))
            startActivity(intent)
        }
        binding.back.setOnClickListener(){
            this.findNavController().navigateUp()
        }
        binding.textView.setOnClickListener(){
            val intent: Intent = Intent(context, PrivacyPolicyActivity::class.java)
            intent.putExtra("url", "https://admindev.outgrowdigital.com/privacy-policy")
            intent.putExtra("tittle", "Privacy Policy")
            requireActivity().startActivity(intent)
        }
        binding.textView2.setOnClickListener(){
            val intent: Intent = Intent(context, PrivacyPolicyActivity::class.java)
            intent.putExtra("url", "https://admindev.outgrowdigital.com/terms-and-conditions")
            intent.putExtra("tittle", "Terms and Conditions")
            requireActivity().startActivity(intent)

        }
        binding.llAboutOutgrow.setOnClickListener(){
            this.findNavController().navigate(MyProfileFragmentDirections.actionMyProfileFragmentToAboutOutgrowFragment())
        }




        binding.cvChat.setOnClickListener {
            FeatureChat.zenDeskInit(requireContext())
        }

        binding.llLanguage.setOnClickListener() {
            this.findNavController()
                .navigate(MyProfileFragmentDirections.actionMyProfileFragmentToLanguageFragment3())
        }
            val mobileno = loginViewModel.getMobileNumber()
            if (mobileno != null)
                binding.logout.setOnClickListener {

                    loginViewModel.logout(mobileno)
                        .observe(viewLifecycleOwner) {

                           // loginViewModel.setUserToken(null)
                            loginViewModel.setIsLoggedIn(false)
                            FeatureChat.zendeskLogout()

                            context?.let { it1 ->
                                ToastStateHandling.toastSuccess(
                                    it1,
                                    "Successfully Logout",
                                    Toast.LENGTH_LONG
                                )
                            }

                        }
                    moveToLogin()
                }

        }
    private fun moveToLogin() {
        val intent:Intent = Intent(context, LoginMainActivity::class.java)
        startActivity(intent)
        activity?.finish()
        GlobalScope.launch {
            LocalSource.deleteAllMyCrops()
            LocalSource.deleteTags()
            LocalSource.deleteCropMaster()
            LocalSource.deleteCropInformation()
            LocalSource.deletePestDisease()
            MyCropSyncer().invalidateSync()
            CropMasterSyncer().invalidateSync()
            CropInformationSyncer().invalidateSync()
            TagsSyncer().invalidateSync()
            PestDiseaseSyncer().invalidateSync()

        }
    }
      private fun translation(){
          TranslationsManager().loadString("str_farmer_profile",binding.tvFarmer)
          TranslationsManager().loadString("str_myProfile",binding.tvMyProfile)
          TranslationsManager().loadString("str_language",binding.tvLang)
          TranslationsManager().loadString("str_about",binding.tvAbout)
          TranslationsManager().loadString("str_invite_farmer",binding.tvInvite)
          TranslationsManager().loadString("str_ask_chat",binding.tvAsk)
          TranslationsManager().loadString("str_rate_us",binding.tvRate)
          TranslationsManager().loadString("str_logout",binding.tvLogout)
          TranslationsManager().loadString("str_privacy_policy",binding.textView)
          TranslationsManager().loadString("str_terms",binding.textView2)
          TranslationsManager().loadString("str_farm_support",binding.tvSupport)
      }

   }
