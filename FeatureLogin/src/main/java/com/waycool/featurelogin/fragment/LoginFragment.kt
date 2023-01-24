package com.waycool.featurelogin.fragment

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import com.truecaller.android.sdk.TruecallerSDK
import com.truecaller.android.sdk.TruecallerSdkScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.truecaller.android.sdk.ITrueCallback
import com.truecaller.android.sdk.TrueProfile
import com.truecaller.android.sdk.TrueError
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.featurelogin.R
import com.waycool.featurelogin.activity.PrivacyPolicyActivity
import com.waycool.featurelogin.databinding.FragmentLoginBinding
import com.waycool.featurelogin.loginViewModel.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.regex.Pattern

class LoginFragment : Fragment() {
    private var trueCallerFullName: String? = null

    private var isTruecallerVerified: Boolean = false
    lateinit var binding: FragmentLoginBinding
    val loginViewModel: LoginViewModel by lazy {
        ViewModelProvider(requireActivity())[LoginViewModel::class.java]
    }

    private lateinit var trueCallerSDK: TruecallerSDK

    var deviceManufacturer: String? = null
    var deviceModel: String? = null
    var fcmToken: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backBtn.setOnClickListener {
            activity?.finish()
        }
        val trueScope = TruecallerSdkScope.Builder(requireContext(), sdkCallback)
            .consentMode(TruecallerSdkScope.CONSENT_MODE_BOTTOMSHEET)
            .buttonColor(
                ContextCompat.getColor(
                    requireContext(),
                    com.waycool.uicomponents.R.color.primaryColor
                )
            )
            .buttonTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    com.waycool.uicomponents.R.color.white
                )
            )
            .loginTextPrefix(TruecallerSdkScope.LOGIN_TEXT_PREFIX_TO_GET_STARTED)
            .loginTextSuffix(TruecallerSdkScope.LOGIN_TEXT_SUFFIX_PLEASE_VERIFY_MOBILE_NO)
            .ctaTextPrefix(TruecallerSdkScope.CTA_TEXT_PREFIX_USE)
            .buttonShapeOptions(TruecallerSdkScope.BUTTON_SHAPE_ROUNDED)
            .privacyPolicyUrl("https://admindev.outgrowdigital.com/privacy-policy")
            .termsOfServiceUrl("https://admindev.outgrowdigital.com/terms-and-conditions")
            .footerType(TruecallerSdkScope.FOOTER_TYPE_MANUALLY)
            .consentTitleOption(TruecallerSdkScope.SDK_CONSENT_TITLE_LOG_IN)
            .sdkOptions(TruecallerSdkScope.SDK_OPTION_WITHOUT_OTP)
            .build()
        TruecallerSDK.init(trueScope)
        trueCallerSDK = TruecallerSDK.getInstance()


        Handler(Looper.myLooper()!!).postDelayed({
            if (trueCallerSDK.isUsable) {
                trueCallerSDK.setLocale(Locale("en"))
                trueCallerSDK.getUserProfile(requireActivity())
            }
        }, 700)


        binding.getotpBtn.setOnClickListener {

            if (binding.mobilenoEt.text.toString()
                    .isEmpty() || binding.mobilenoEt.text.toString().length != 10
            ) {
                binding.mobileNoTextlayout.error = "Please enter valid mobile number"
            } else if (!checkForValidMobileNumber(binding.mobilenoEt.text.toString())) {
                binding.mobileNoTextlayout.error = "Please enter valid mobile number"
            } else {

                loginViewModel.setMobileNumber(binding.mobilenoEt.text.toString())

                AuthorizeMobileNumber(binding.mobilenoEt.text.toString())
                EventClickHandling.calculateClickEvent("Login_OTP")
            }
        }


        CoroutineScope(Dispatchers.IO).launch {
            fcmToken = loginViewModel.getFcmToken()
            deviceModel = loginViewModel.getDeviceModel()
            deviceManufacturer = loginViewModel.getDeviceManufacturer()
        }

        setTermsText()

    }

    private fun checkForValidMobileNumber(mobileno: String): Boolean {
        val pattern = Pattern.compile("^[6-9]\\d{9}\$")

        val matcher = pattern.matcher(mobileno)
        return matcher.find()
    }

    private fun setTermsText() {
        val text = "By continuing you agree to Outgrow's privacy policy and terms of service"
        val spannableString = SpannableString(text)
        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent: Intent = Intent(context, PrivacyPolicyActivity::class.java)
                intent.putExtra("url", "https://admindev.outgrowdigital.com/terms-and-conditions")
                intent.putExtra("tittle", "Terms and Conditions")
                requireActivity().startActivity(intent)
                EventClickHandling.calculateClickEvent("Terms_of_use_landing")
            }
        }
        val clickableSpan2: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent: Intent = Intent(context, PrivacyPolicyActivity::class.java)
                intent.putExtra("url", "https://admindev.outgrowdigital.com/privacy-policy")
                intent.putExtra("tittle", "Privacy Policy")
                requireActivity().startActivity(intent)
                EventClickHandling.calculateClickEvent("Privacy_policy_landing")
            }
        }
        val boldSpan = StyleSpan(Typeface.BOLD)
        spannableString.setSpan(boldSpan, 27, 36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(clickableSpan2, 37, 51, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(clickableSpan1, 55, 72, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.termsTv.text = spannableString
        binding.termsTv.movementMethod = LinkMovementMethod.getInstance()
        binding.termsTv.highlightColor =
            ContextCompat.getColor(requireContext(), com.waycool.uicomponents.R.color.white)

    }

    val sdkCallback: ITrueCallback = object : ITrueCallback {
        override fun onSuccessProfileShared(trueProfile: TrueProfile) {
            isTruecallerVerified = true
            val number = trueProfile.phoneNumber.substring(3)
            trueCallerFullName = trueProfile.firstName + " " + trueProfile.lastName
            AuthorizeMobileNumber(number)
        }

        override fun onFailureProfileShared(trueError: TrueError) {
            isTruecallerVerified = false
        }

        override fun onVerificationRequired(trueError: TrueError?) {}
    }

    /*
     * This method is used to check mobile number from API
     */
    fun AuthorizeMobileNumber(mobileNo: String) {
        if (NetworkUtil.getConnectivityStatusString(context) == 0) {
            context?.let {
                ToastStateHandling.toastError(
                    it,
                    "Please check your Internet connection",
                    Toast.LENGTH_SHORT
                )
            }
        } else {
            loginViewModel.setMobileNumber(mobileNo)

            if (!isTruecallerVerified) {
                moveToOtp(mobileNo)
                EventClickHandling.calculateClickEvent("Login_Truecaller")
            } else {
                loginViewModel.login(mobileNo, fcmToken!!, deviceModel!!, deviceManufacturer!!)
                    .observe(
                        requireActivity()
                    ) { it ->
                        when (it) {
                            is Resource.Success -> {
                                val loginMaster = it.data
                                if (loginMaster?.status == true) {

                                    if (!(loginMaster.data?.isEmpty())!!) {
                                        loginViewModel.setIsLoggedIn(true)

                                        loginViewModel.setUserToken(loginMaster.data)

                                    }
                                    loginViewModel.getUserDetails().observe(viewLifecycleOwner) {user->
                                        if (user.data != null && user.data?.userId != null) {
                                            gotoMainActivity()
                                        }
                                    }
//

                                } else {
                                    if (loginMaster?.data == "406") {
                                        val bottomSheetDialog = BottomSheetDialog(requireContext())
                                        bottomSheetDialog.setContentView(R.layout.bottom_dialog_logged_in_details)
                                        val logginTv =
                                            bottomSheetDialog.findViewById<TextView>(R.id.loggin_text_dialog)
                                        val continueBtn =
                                            bottomSheetDialog.findViewById<Button>(R.id.continue_loggin_dialog)
                                        val goBackBtn =
                                            bottomSheetDialog.findViewById<Button>(R.id.goback_login_dialog)
                                        bottomSheetDialog.setCancelable(false)
                                        bottomSheetDialog.setCanceledOnTouchOutside(false)
                                        bottomSheetDialog.show()
                                        goBackBtn!!.setOnClickListener { view: View? -> bottomSheetDialog.dismiss() }
                                        logginTv!!.text =
                                            Html.fromHtml("You have already logged in  <b>" + "another Devices" + "</b>. Click on <b>Continue</b> to login.")
                                        continueBtn!!.setOnClickListener {
                                            loginViewModel.logout(mobileNo)
                                                .observe(requireActivity()) { it1 ->
                                                    when (it1) {
                                                        is Resource.Success -> {
                                                            AuthorizeMobileNumber(mobileNo)
                                                            bottomSheetDialog.dismiss()
                                                        }
                                                        is Resource.Loading -> {}
                                                        is Resource.Error -> {
/*
                                                            context?.let { ToastStateHandling.toastError(it,"Error occurred",Toast.LENGTH_SHORT) }
*/

                                                        }
                                                    }

                                                }

                                        }
                                    } else if (loginMaster?.data == "422") {
                                        moveToRegistration(mobileNo)

                                    }
                                }
                            }
                            is Resource.Loading -> {

                            }
                            is Resource.Error -> {
/*
                                context?.let { ToastStateHandling.toastError(it,"Error occurred",Toast.LENGTH_LONG) }
*/

                            }
                        }

                    }
            }
        }
    }

    private fun gotoMainActivity() {
        val intent = Intent()
        intent.setClassName(requireContext(), "com.waycool.iwap.MainActivity")
        startActivity(intent)
        requireActivity().finish()
    }


    override fun onDestroy() {
        super.onDestroy()
        TruecallerSDK.clear()
    }

    private fun moveToOtp(mobileNo: String) {
        loginViewModel.setIsFirst(false)

//        moveToRegistration(mobileNo)

        val args = Bundle()
        args.putString("mobilenumber", mobileNo)
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_loginFragment_to_otpFragment, args)

    }

    private fun moveToRegistration(mobileNo: String) {
        loginViewModel.setIsFirst(false)

        val args = Bundle()
        args.putString("mobile_number", mobileNo)

        args.putString("name", trueCallerFullName?.trim() ?: "")
        findNavController(binding.root)
            .navigate(R.id.action_loginFragment_to_registrationFragment, args)
    }

}