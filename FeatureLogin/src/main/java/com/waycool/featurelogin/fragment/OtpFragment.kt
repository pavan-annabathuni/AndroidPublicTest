package com.waycool.featurelogin.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mukesh.OTP_VIEW_TYPE_BORDER
import com.mukesh.OtpView
import com.waycool.core.retrofit.OTPApiCient
import com.waycool.data.utils.NetworkUtil
//import com.waycool.data.utils.SharedPreferenceUtility
import com.waycool.data.Network.ApiInterface.OTPApiInterface
import com.waycool.data.repository.domainModels.OTPResponseDomain
import com.waycool.data.utils.Resource
import com.waycool.featurelogin.R
import com.waycool.featurelogin.databinding.FragmentOtpBinding
import com.waycool.featurelogin.loginViewModel.LoginViewModel
import com.waycool.featurelogin.support.SmsBroadcastReceiver
import com.waycool.featurelogin.test.Validator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.regex.Pattern


class OtpFragment : Fragment() {
    lateinit var binding: FragmentOtpBinding
    private var mToast: Toast? = null
    var mobileNumber: String = ""
    var OTP = ""
    var smsBroadcastReceiver: SmsBroadcastReceiver? = null
    var validator: Validator? = null

    val loginViewModel: LoginViewModel by lazy { ViewModelProvider(this)[LoginViewModel::class.java] }

    var countDownTimer: CountDownTimer? = null

    var otpRetrofit: Retrofit = OTPApiCient.apiClient
    var otpApiInterface: OTPApiInterface = otpRetrofit.create(
        OTPApiInterface::class.java
    )

    var fcmToken: String? = ""
    var mobileManufacturer: String? = null
    var mobileModel: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOtpBinding.inflate(layoutInflater)

        if (arguments != null)
            mobileNumber = arguments?.getString("mobilenumber").toString()

        CoroutineScope(Dispatchers.IO).launch {
            fcmToken = loginViewModel.getFcmToken()
            mobileManufacturer = loginViewModel.getDeviceManufacturer()
            mobileModel = loginViewModel.getDeviceModel()

        }

//        if (arguments?.getString("mobile_number") != null)˳˳
//            mobileNumber = arguments?.getString("mobile_number")!!
//        if (arguments?.getString("existing_user") != null)
//            existingUser = arguments?.getString("existing_user")
//        if (arguments?.getString("fcmToken") != null)
//            fcmToken = arguments?.getString("fcmToken")
//        validator = Validator(2)
        binding.receiveMsgTv.setText(getString(R.string.opt_text2) + " +91- " + mobileNumber)
        //binding.doneBtn.isEnabled=false
        /*  binding.otpPassword.isEnabled  = true
          binding.otpPassword.isClickable = true
          binding.otpPassword.requestFocus()*/
        startSmsUserConsent()
        if (!isSmsPermissionGranted) {
            requestReadAndSendSmsPermission()
        }
        binding.otpPassword.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    otpView()
                }
            }
        }
        requestForOTP()

        binding.otpViaCall.setOnClickListener {
            retryOtp("voice")
            showTimer()
        }

        binding.doneBtn.setOnClickListener {
            validateOTP()
        }

        binding.resendMsgBtn.setOnClickListener {
            retryOtp("text")
            showTimer()
        }

        binding.backBtn.setOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
        showTimer()
        return binding.root
    }

    private fun retryOtp(type: String) {
        loginViewModel.retryOtp(mobileNumber, type).observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    val otpResponse: OTPResponseDomain? = it.data
                    if (otpResponse?.type == "success") {
//                        Toast.makeText(requireContext(),"Retry with $type requested",Toast.LENGTH_SHORT).show()
                    } else if (otpResponse?.type == "error") {
                        Toast.makeText(
                            requireContext(),
                            "${it.data?.message}",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
                is Resource.Loading -> {}
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Error Occurred", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    @Composable
    fun otpView(otpVal: String = "") {
        var otpValue by remember { mutableStateOf(otpVal) }
        OtpView(
            otpText = otpValue,
            onOtpTextChange = {
                otpValue = it
                OTP = it
                Log.d("Actual Value", otpValue)
                if (it.length == 4)
                    validateOTP()
            },
            type = OTP_VIEW_TYPE_BORDER,
            password = false,
            containerSize = 48.dp,
            passwordChar = "•",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            charColor = Color(resources.getColor(com.waycool.uicomponents.R.color.primaryColor)),
        )

    }

    private fun showToastAtCentre(message: String, duration: Int) {
        if (mToast != null) {
            mToast!!.cancel()
            mToast = null
        }
        Toast.makeText(context, message, duration).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            SMS_PERMISSION_CODE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                }
            }
        }
    }

    val isSmsPermissionGranted: Boolean
        get() = activity?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.READ_SMS
            )
        } == PackageManager.PERMISSION_GRANTED

    /**
     * Request runtime SMS permission
     */
    private fun requestReadAndSendSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_SMS
            )
        ) {
        }
        activity?.let {
            ActivityCompat.requestPermissions(
                it,
                arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS),
                SMS_PERMISSION_CODE
            )
        }
    }


    private fun validateOTP() {

        if (OTP.length == 4) {
            loginViewModel.verifyOtp(requireContext(), OTP, mobileNumber)
                .observe(viewLifecycleOwner) {
                    when (it) {
                        is Resource.Success -> {
                            val otpResponse: OTPResponseDomain? = it.data
                            if (otpResponse?.type == "success") {
                                verifyUser()
                            } else if (otpResponse?.type == "error") {
                                Toast.makeText(
                                    requireContext(),
                                    "${it.data?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        }
                        is Resource.Loading -> {}
                        is Resource.Error -> {
                            Toast.makeText(
                                requireContext(),
                                "Error Occurred. ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
                }
        } else {
            showToastAtCentre("Please enter the OTP", Toast.LENGTH_LONG)
        }
    }

    /*
     * This method is used to auto fetch otp code from sms
     */
    private fun startSmsUserConsent() {
        val client = SmsRetriever.getClient(requireActivity())
        //We can add sender phone number or leave it blank
        // I'm adding null here
        client.startSmsUserConsent(null).addOnSuccessListener {
            // Toast.makeText(getApplicationContext(), "On Success", Toast.LENGTH_LONG).show();
        }.addOnFailureListener {
            //Toast.makeText(getApplicationContext(), "On OnFailure", Toast.LENGTH_LONG).show();
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_USER_CONSENT) {
            if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
                //That gives all message to us.
                // We need to get the code from inside with regex
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                //  Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                // System.out.println(String.format("%s - %s", "Received Message", message)+"otptestedmsg");
                getOtpFromMessage(message)
                binding.doneBtn.isEnabled = true
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                //binding.doneBtn.isEnabled = false
                getCancelResult()
            }
        }
    }

    private fun getCancelResult() {
        countDownTimer!!.cancel()
        binding.progressCircular.setVisibility(View.GONE)
        binding.otpResendLayout.setVisibility(View.VISIBLE)
        binding.otpCallLayout.setVisibility(View.VISIBLE)
    }

    private fun getOtpFromMessage(message: String?) {
        // This will match any 6 digit number in the message
        val pattern = Pattern.compile("(|^)\\d{4}")
        val matcher = pattern.matcher(message)
        if (matcher.find()) {
            countDownTimer!!.cancel()
            binding.doneBtn.isEnabled
            binding.progressCircular.visibility = View.GONE
            binding.otpResendLayout.visibility = View.GONE
            binding.otpCallLayout.visibility = View.GONE
            binding.otpPassword.setContent {
                otpView(matcher.group(0))
            }
            OTP = matcher.group(0)
            validateOTP()
        }
    }

    fun registerBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver!!.smsBroadcastReceiverListener = object :
            SmsBroadcastReceiver.SmsBroadcastReceiverListener {
            override fun onSuccess(intent: Intent) {
                startActivityForResult(intent, REQ_USER_CONSENT)
            }

            override fun onFailure() {}
        }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        requireActivity().registerReceiver(smsBroadcastReceiver, intentFilter)
    }

    override fun onStart() {
        super.onStart()
        registerBroadcastReceiver();
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(smsBroadcastReceiver);
    }

    private fun showTimer() {
        countDownTimer = null
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(30 * 1 * 1000, 1000) {
            // adjust the milli seconds here
            @SuppressLint("DefaultLocale", "SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                binding.progressCircular.setVisibility(View.VISIBLE)
                binding.otpResendLayout.setVisibility(View.GONE)
                binding.otpCallLayout.setVisibility(View.GONE)
                val f: NumberFormat = DecimalFormat("00")
                val sec = millisUntilFinished / 1000 % 60
                binding.progressCircular.setText(f.format(sec))
                binding.progressCircular.progress = sec.toFloat()
                //binding.doneBtn.isEnabled = false
                /*binding.otpMinCount.setText("Please wait for " + String.format("%02d : %02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));*/
            }

            override fun onFinish() {
                binding.progressCircular.setVisibility(View.GONE)
                binding.otpResendLayout.setVisibility(View.VISIBLE)
                binding.otpCallLayout.setVisibility(View.VISIBLE)
                binding.doneBtn.isEnabled = true
            }
        }
        (countDownTimer as CountDownTimer).start()
    }

    companion object {
        private const val SMS_PERMISSION_CODE = 1001
        private const val REQ_USER_CONSENT = 200
    }

    private fun requestForOTP() {
        if (NetworkUtil.getConnectivityStatusString(context) == 0) {
            Toast.makeText(context, "No internet", Toast.LENGTH_LONG).show()
        } else {
            apiOTP(mobileNumber)
            /*SendOTPConfigBuilder()
                .setCountryCode(91)
                .setMobileNumber(mobileNumber)
                .setVerifyWithoutOtp(false) //direct verification while connect with mobile network
                .setAutoVerification(activity) //Auto read otp from Sms And Verify
                .setMessage("Welcome to GramworkX, Your Verification Code for Signup is ##OTP##")
                .setSenderId("GWXIND")
                .setOtpLength(OTP_LENGTH)
                .setVerificationCallBack(VerificationListener { sendOTPResponseCode, s ->
                    requireActivity().runOnUiThread {
                        if (sendOTPResponseCode == SendOTPResponseCode.DIRECT_VERIFICATION_SUCCESSFUL_FOR_NUMBER || sendOTPResponseCode == SendOTPResponseCode.OTP_VERIFIED) {
                            //otp verified OR direct verified by send otp 2.O
                            //otpVerified()
                            Toast.makeText(
                                context,
                                " Verified",
                                Toast.LENGTH_SHORT
                            ).show()
                            verifyUser()
                        } else if (sendOTPResponseCode == SendOTPResponseCode.READ_OTP_SUCCESS) {
                            //Auto read otp from sms successfully
                            // you can get otp form message filled
                            Toast.makeText(
                                context,
                                "OTP auto",
                                Toast.LENGTH_SHORT
                            ).show()
                           // binding.otpPassword.setText(s)

                        } else if (sendOTPResponseCode == SendOTPResponseCode.SMS_SUCCESSFUL_SEND_TO_NUMBER || sendOTPResponseCode == SendOTPResponseCode.DIRECT_VERIFICATION_FAILED_SMS_SUCCESSFUL_SEND_TO_NUMBER) {
                            // Otp send to number successfully

                            Toast.makeText(
                                activity,
                                "OTP Sent",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
                }).build()


            SendOTP.getInstance().trigger.initiate()*/
        }
    }

    /* fun onSendOtpResponse(responseCode: SendOTPResponseCode, s: String?) {
        runOnUiThread {
            if (responseCode == SendOTPResponseCode.DIRECT_VERIFICATION_SUCCESSFUL_FOR_NUMBER || responseCode == SendOTPResponseCode.OTP_VERIFIED) {
                //otp verified OR direct verified by send otp 2.O
                //otpVerified()
                Toast.makeText(
                    context,
                    " verified",
                    Toast.LENGTH_SHORT
                ).show()
                verifyUser()
            } else if (responseCode == SendOTPResponseCode.READ_OTP_SUCCESS) {
                //Auto read otp from sms successfully
                // you can get otp form message filled
                Toast.makeText(
                    context,
                    "OTP auto",
                    Toast.LENGTH_SHORT
                ).show()
                binding.otpPassword.setText(s)

            } else if (responseCode == SendOTPResponseCode.SMS_SUCCESSFUL_SEND_TO_NUMBER || responseCode == SendOTPResponseCode.DIRECT_VERIFICATION_FAILED_SMS_SUCCESSFUL_SEND_TO_NUMBER) {
                // Otp send to number successfully
                Toast.makeText(
                    context,
                    "OTP Sent",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }*/


    fun verifyUser() {
        if (NetworkUtil.getConnectivityStatusString(context) == 0) {
            Toast.makeText(context, "No internet", Toast.LENGTH_LONG).show()
        } else {

            loginViewModel.login(mobileNumber, fcmToken!!, mobileModel!!, mobileManufacturer!!)
                .observe(
                    requireActivity()
                ) {

                    when (it) {
                        is Resource.Success -> {
                            val loginMaster = it.data
                            if (loginMaster?.status == true) {

                                if (!(loginMaster.data?.isEmpty())!!) {
                                    loginViewModel.setIsLoggedIn(true)
                                    loginViewModel.setMobileNumber(mobileNumber)
                                    loginViewModel.setUserToken(loginMaster.data)

                                }
                                gotoMainActivity()
//                                requireActivity().setResult(RESULT_OK)
//                                requireActivity().finish()

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
                                        loginViewModel.logout(mobileNumber)
                                            .observe(requireActivity()) {
                                                verifyUser()
                                                bottomSheetDialog.dismiss()
                                            }

                                    }
                                } else if (loginMaster?.data == "422") {
                                    moveToRegistration()

                                }
                            }
                        }
                        is Resource.Loading -> {}
                        is Resource.Error -> {}
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


//    fun verifyUser() {
//
//
//        if (existingUser.equals("0")) {
//            moveToRegistration(RegistrationFragment())
//        } else {
//            SharedPreferenceUtility.setMobileNumber(context, mobileNumber)
//            SharedPreferenceUtility.seUserToken(context, fcmToken)
//            SharedPreferenceUtility.setLogin(context, "1")
//            requireActivity().setResult(RESULT_OK)
//            requireActivity().finish()
//        }
//    }

    private fun moveToRegistration() {
        val args = Bundle()
        args.putString(
            getString(R.string.mobile_stringextra),
            mobileNumber
        )

        Navigation.findNavController(binding.root)
            .navigate(R.id.action_otpFragment_to_registrationFragment, args)
    }

    fun apiOTP(mobileNumber: String) {
        loginViewModel.getOtp(mobileNumber).observe(requireActivity()) {
            if (it is Resource.Success) {
                Toast.makeText(requireContext(), "OTP Sent", Toast.LENGTH_LONG).show()
            }
        }
    }
}


