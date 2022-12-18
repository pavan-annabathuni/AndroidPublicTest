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
import com.waycool.core.utils.AppSecrets
import com.waycool.data.Local.LocalSource
import com.waycool.data.Sync.syncer.*
import com.waycool.featurechat.FeatureChat
import com.waycool.featurelogin.activity.LoginMainActivity
import com.waycool.featurelogin.activity.PrivacyPolicyActivity
import com.waycool.featurelogin.loginViewModel.LoginViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MyProfileFragment : Fragment() {
    private lateinit var binding: FragmentMyProfileBinding
    private val loginViewModel: LoginViewModel by lazy { ViewModelProvider(this)[LoginViewModel::class.java] }
    private val viewModel: EditProfileViewModel by lazy {
        ViewModelProviders.of(this).get(EditProfileViewModel::class.java)
    }
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
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        //viewModel.getUsers()
        // viewModel.getUserDetails()


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
        observer()
        return binding.root
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


//        viewModel.getUserDetails().observe(viewLifecycleOwner){
//            if(it.data?.profile?.profilePic!=null) {
//               Glide.with(this).load(it.data?.profile?.profilePic).into(binding.proPic)
//            Log.d("ProfilePic", "observer: $it")
//
//        }}
         return true
    }

    private fun onClick() {
        binding.ll1.setOnClickListener() {
            this.findNavController()
                .navigate(MyProfileFragmentDirections.actionMyProfileFragmentToEditProfileFragment())
        }
        binding.ll3.setOnClickListener() {
            this.findNavController()
                .navigate(MyProfileFragmentDirections.actionMyProfileFragmentToFarmSupportFragment())
        }
        binding.ll4.setOnClickListener() {
            ShareCompat.IntentBuilder.from(requireActivity())
                .setType("text/plain")
                .setChooserTitle("Chooser title")
                .setText("http://play.google.com/store/apps/details?id=" + requireActivity().getPackageName())
                .startChooser();
        }
        binding.rateUs.setOnClickListener(){
            val intent = Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=com.waycool.iwap"))
            startActivity(intent)
        }
        binding.back.setOnClickListener(){
            this.findNavController().navigateUp()
        }
        binding.textView.setOnClickListener(){
            val intent: Intent = Intent(context, PrivacyPolicyActivity::class.java)
            intent.putExtra("url", "http://one.waycool.in/Outgrow_PrivacyPolicy.html")
            intent.putExtra("tittle", "Privacy Policy")
            requireActivity().startActivity(intent)
        }
        binding.textView2.setOnClickListener(){
            val intent: Intent = Intent(context, PrivacyPolicyActivity::class.java)
            intent.putExtra("url", "http://one.waycool.in/Terms_and_Conditions.html")
            intent.putExtra("tittle", "Terms and Conditions")
            requireActivity().startActivity(intent)

        }
        binding.ll5.setOnClickListener(){
            this.findNavController().navigate(MyProfileFragmentDirections.actionMyProfileFragmentToAboutOutgrowFragment())
        }




        binding.cvChat.setOnClickListener {
            FeatureChat.zenDeskInit(requireContext())
//            Chat.INSTANCE.init(requireContext(),AppSecrets.getAccountKey(),
//            AppSecrets.getChatAppId())
//            val chatConfiguration = ChatConfiguration.builder()
//                .withAgentAvailabilityEnabled(false)
//                .withTranscriptEnabled(false)
//                .build()
//            val visitorInfo: VisitorInfo = VisitorInfo.builder()
//                .withName("Bob")
//                .withEmail("bob@example.com")
//                .withPhoneNumber("123456") // numeric string
//                .build();


//            var jwtAuthenticator =  JwtAuthenticator {
//                it.onTokenLoaded("eyJpdiI6IjBGN0lWQ1d3N0tQS0lreHRMNWVKV0E9PSIsInZhbHVlIjoib2VsYU5OVjJqdVBNRWZyMkpJcWVyQT09IiwibWFjIjoiYTJmODA4Y2ExOTg1NWRkNjNhNGUwYWJjZTcyYWJmNTNiNjJiN2I2Y2NiZWRkMWEwZjE2ZGY3ODAyZDViYzlkZiIsInRhZyI6IiJ9")
//                it.onError()
//                Log.d("JWT", "onClick: $jwtToken")
//            }

//           val jwtAuthenticator =
//                JwtAuthenticator { jwtCompletion -> //Fetch or generate the JWT token at this point
//                    //OnSuccess
//                    jwtCompletion.onTokenLoaded("")
//                    //OnError
//                    jwtCompletion.onError()
  //              }

//                Chat.INSTANCE.init(requireContext(),AppSecrets.getAccountKey(),AppSecrets.getChatAppId())
//                val jwtAuth = JwtAuth()
//                Chat.INSTANCE.setIdentity(jwtAuth)
//
//         //   Chat.INSTANCE.setIdentity(jwtAuthenticator)
//
//            val chatProvidersConfiguration: ChatProvidersConfiguration = ChatProvidersConfiguration.builder()
////                .withVisitorInfo(visitorInfo)
//                .withDepartment("English Language Group")
//                .build()
//
//            Chat.INSTANCE.setChatProvidersConfiguration(chatProvidersConfiguration)
//
//
//
//            MessagingActivity.builder()
//                .withEngines(ChatEngine.engine())
//                .show(requireContext(), chatConfiguration);

        }
//        binding.rateUs.setOnClickListener(){
//            val reviewManager = ReviewManagerFactory.create(requireContext())
//            val requestReviewFlow = reviewManager.requestReviewFlow()
//            requestReviewFlow.addOnCompleteListener { request ->
//                if (request.isSuccessful) {
//                    // We got the ReviewInfo object
//                    val reviewInfo = request.result
//                    val flow = reviewManager.launchReviewFlow(requireActivity(), reviewInfo)
//                    flow.addOnCompleteListener {
//                        // The flow has finished. The API does not indicate whether the user
//                        // reviewed or not, or even whether the review dialog was shown. Thus, no
//                        // matter the result, we continue our app flow.
//                    }
//                } else {
//                    Log.d("Error: ", request.exception.toString())
//                    // There was some problem, continue regardless of the result.
//                }
//            }
//        }

        binding.ll2.setOnClickListener() {
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

                            Toast.makeText(
                                context,
                                "Successfully Logout",
                                Toast.LENGTH_LONG
                            ).show()

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

   }
//internal class JwtAuth : JwtAuthenticator {
//
//    private fun retrieveToken(callback: JwtCallback) {
//        // request to backend service that returns a JWT Token or an Error
//
//        callback.onSuccess("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6NTAsIm5hbWUiOiJTZCIsImNvbnRhY3QiOiI3Njc5OTUyMDUwIiwiZW1haWwiOiI3Njc5OTUyMDUwNjM3MjFmMGNkYmY1YmR1bW15QHdheWNvb2wuaW4iLCJlbWFpbF92ZXJpZmllZF9hdCI6bnVsbCwiYXBwcm92ZWQiOjEsInNldHRpbmdzIjpudWxsLCJpYXQiOjE2Njg0MjM0MzYsImp0aSI6IjYzNzIxZjBjZGJmODIiLCJwcm9maWxlIjp7ImlkIjozMSwiZ2VuZGVyIjpudWxsLCJhZGRyZXNzIjpudWxsLCJ2aWxsYWdlIjoiQmVsbGFuZHVyIiwicGluY29kZSI6IjU2MDEwMyIsInN0YXRlIjpudWxsLCJkaXN0cmljdCI6bnVsbCwic3ViX2Rpc3RyaWN0IjpudWxsLCJjaXR5IjpudWxsLCJjb3VudHJ5IjpudWxsLCJsYXQiOiIxMi45MzAxNDAwMCIsImxvbmciOiI3Ny42ODYyOTAwMCIsInByb2ZpbGVfcGljIjpudWxsLCJ1c2VyX2lkIjo1MCwibGFuZ19pZCI6MX0sImFjY291bnQiOlt7ImlkIjozMCwiYWNjb3VudF9ubyI6IkFITFg3VlNZTVIiLCJhY2NvdW50X3R5cGUiOiJvdXRncm93IiwiaXNfYWN0aXZlIjoxLCJkZWZhdWx0X21vZHVsZXMiOjEsImlzX3ByZW1pdW0iOjB9XX0.y3pY-4UPvfmPjPFIwRbNmABQ8nclvtdjF_Vx91yJQno")
//    }
//
//    override fun getToken(jwtCompletion: JwtCompletion) {
//        retrieveToken(object : JwtCallback {
//            override fun onSuccess(token: String?) {
//                jwtCompletion.onTokenLoaded(token)
//            }
//
//            override fun onError() {
//                jwtCompletion.onError()
//            }
//        })
//    }
//
//    internal interface JwtCallback {
//        fun onSuccess(token: String?)
//        fun onError()
//    }
//}
