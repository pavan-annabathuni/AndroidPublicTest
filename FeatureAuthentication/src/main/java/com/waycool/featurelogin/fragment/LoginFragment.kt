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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.truecaller.android.sdk.*
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
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
    var deviceManufacturer: String? = null
    var deviceModel: String? = null
    var fcmToken: String? = null
    var langCode : String? =null
    private var trueCallerVerified: Boolean = false
    private lateinit var trueCallerSDK: TruecallerSDK
    lateinit var fragmentLoginBinding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by lazy { ViewModelProvider(requireActivity())[LoginViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false)
        return fragmentLoginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentLoginBinding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        loginViewModel.getSelectedLangCode()
        translation()

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


        loginViewModel.language.observe(viewLifecycleOwner) {
            langCode = it.langCode
            Handler(Looper.myLooper()!!).postDelayed({
                if (trueCallerSDK.isUsable) {
                    trueCallerSDK.setLocale(Locale(langCode ?: "en"))
                    trueCallerSDK.getUserProfile(requireActivity())
                }
            }, 700)
        }

        fragmentLoginBinding.getotpBtn.setOnClickListener {
            if (fragmentLoginBinding.mobilenoEt.text.toString()
                    .isEmpty() || fragmentLoginBinding.mobilenoEt.text.toString().length != 10
            ) {
                fragmentLoginBinding.mobileNoTextlayout.error = "Please enter valid mobile number"
            } else if (!checkForValidMobileNumber(fragmentLoginBinding.mobilenoEt.text.toString())) {
                fragmentLoginBinding.mobileNoTextlayout.error = "Please enter valid mobile number"
            } else {
                loginViewModel.setMobileNumber(fragmentLoginBinding.mobilenoEt.text.toString())
                AuthorizeMobileNumber(fragmentLoginBinding.mobilenoEt.text.toString())
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


    private fun translation() {
        TranslationsManager().loadString(
            "enter_mobile_no",
            fragmentLoginBinding.enterNumberTv,
            "Enter your mobile number"
        )
        TranslationsManager().loadString(
            "recieve_otp",
            fragmentLoginBinding.receiveMsgTv,
            "You’ll receive a 4 digit code to verify"
        )
        TranslationsManager().loadString(
            "enter_number",
            fragmentLoginBinding.tvEnterMobileNumber,
            "Enter mobile number"
        )
        TranslationsManager().loadString("get_otp", fragmentLoginBinding.getotpBtn, "Get OTP")

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
                val intent = Intent(context, PrivacyPolicyActivity::class.java)
                intent.putExtra("url", "https://admindev.outgrowdigital.com/terms-and-conditions")
                intent.putExtra("tittle", "Terms and Conditions")
                requireActivity().startActivity(intent)
                EventClickHandling.calculateClickEvent("Terms_of_use_landing")
            }
        }
        val clickableSpan2: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(context, PrivacyPolicyActivity::class.java)
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
        fragmentLoginBinding.termsTv.text = spannableString
        fragmentLoginBinding.termsTv.movementMethod = LinkMovementMethod.getInstance()
        fragmentLoginBinding.termsTv.highlightColor =
            ContextCompat.getColor(requireContext(), com.waycool.uicomponents.R.color.white)

    }

    val sdkCallback: ITrueCallback = object : ITrueCallback {
        override fun onSuccessProfileShared(trueProfile: TrueProfile) {
            trueCallerVerified = true
            val number = trueProfile.phoneNumber.substring(3)
            trueCallerFullName = trueProfile.firstName + " " + trueProfile.lastName
            AuthorizeMobileNumber(number)
        }

        override fun onFailureProfileShared(trueError: TrueError) {
            trueCallerVerified = false
//            ToastStateHandling.toastError(
//                requireContext(),
//                trueError.toString(),
//                Toast.LENGTH_SHORT
//            )
        }

        override fun onVerificationRequired(trueError: TrueError?) {
            trueCallerVerified = false
//            ToastStateHandling.toastError(
//                requireContext(),
//                trueError.toString(),
//                Toast.LENGTH_SHORT
//            )
        }
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
            fragmentLoginBinding.getotpBtn.isEnabled = false

            if (!trueCallerVerified) {
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
                                Log.d("Login", "${loginMaster}")
                                if (loginMaster?.status == true) {

                                    if (!(loginMaster.data?.isEmpty())!!) {
                                        loginViewModel.setIsLoggedIn(true)
                                        loginViewModel.setUserToken(loginMaster.data)
                                    }
                                    Handler(Looper.myLooper()!!).postDelayed({
                                        loginViewModel.getUserDetails()
                                            .observe(viewLifecycleOwner) { user ->
                                                if (user.data != null && user.data?.userId != null) {
                                                    gotoMainActivity()
                                                }
                                            }
                                    }, 200)

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
        fragmentLoginBinding.pb.visibility = View.VISIBLE
        fragmentLoginBinding.getotpBtn.visibility = View.GONE
        Handler().postDelayed({
            fragmentLoginBinding.pb.visibility = View.GONE
            fragmentLoginBinding.getotpBtn.visibility = View.VISIBLE
            findNavController(fragmentLoginBinding.root)
                .navigate(R.id.action_loginFragment_to_otpFragment, args)
        }, 500)
    }

    private fun moveToRegistration(mobileNo: String) {
        loginViewModel.setIsFirst(false)

        val args = Bundle()
        args.putString("mobile_number", mobileNo)

        args.putString("name", trueCallerFullName?.trim() ?: "")
        findNavController(fragmentLoginBinding.root)
            .navigate(R.id.action_loginFragment_to_registrationFragment, args)
    }

    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("LoginFragment")
    }
}