package com.waycool.featurelogin.fragment

import android.app.Activity.RESULT_OK
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
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.featurelogin.R
import com.waycool.featurelogin.activity.PrivacyPolicyActivity
import com.waycool.featurelogin.databinding.FragmentLoginBinding
import com.waycool.featurelogin.loginViewModel.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.interfaces.RSAKey
import java.util.*

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
            .privacyPolicyUrl("http://one.waycool.in/Outgrow_PrivacyPolicy.html")
            .termsOfServiceUrl("http://one.waycool.in/Terms_and_Conditions.html")
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
            } else {

                loginViewModel.setMobileNumber(binding.mobilenoEt.text.toString())
//                moveToRegistration()

                AuthorizeMobileNumber(binding.mobilenoEt.text.toString())
            }
        }


        CoroutineScope(Dispatchers.IO).launch {
            fcmToken = loginViewModel.getFcmToken()
            deviceModel = loginViewModel.getDeviceModel()
            deviceManufacturer = loginViewModel.getDeviceManufacturer()
        }

        setTermsText()


    }

    private fun setTermsText() {
        val text = "By continuing you agree to Outgrow's privacy policy and terms of service"
        val spannableString = SpannableString(text)
        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent: Intent = Intent(context, PrivacyPolicyActivity::class.java)
                intent.putExtra("url", "http://one.waycool.in/Terms_and_Conditions.html")
                intent.putExtra("tittle", "Terms and Conditions")
                requireActivity().startActivity(intent)
            }
        }
        val clickableSpan2: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent: Intent = Intent(context, PrivacyPolicyActivity::class.java)
                intent.putExtra("url", "http://one.waycool.in/Outgrow_PrivacyPolicy.html")
                intent.putExtra("tittle", "Privacy Policy")
                requireActivity().startActivity(intent)
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
            Toast.makeText(context, "No internet", Toast.LENGTH_LONG).show()
        } else {
            loginViewModel.setMobileNumber(mobileNo)

            if (!isTruecallerVerified) {
                moveToOtp(mobileNo)
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
                                    gotoMainActivity()
//                                    requireActivity().setResult(RESULT_OK)
//                                    requireActivity().finish()

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
                                        continueBtn!!.setOnClickListener { view: View? ->
                                            loginViewModel.logout(mobileNo)
                                                .observe(requireActivity()) {
                                                    when (it) {
                                                        is Resource.Success -> {
                                                            AuthorizeMobileNumber(mobileNo)
                                                            bottomSheetDialog.dismiss()
                                                        }
                                                        is Resource.Loading -> {}
                                                        is Resource.Error -> {
                                                            Toast.makeText(
                                                                requireContext(),
                                                                "Error occurred.",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
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

                                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
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