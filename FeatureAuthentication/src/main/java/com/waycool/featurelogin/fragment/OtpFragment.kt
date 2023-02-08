package com.waycool.featurelogin.fragment

//import com.waycool.data.utils.SharedPreferenceUtility
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mukesh.OTP_VIEW_TYPE_BORDER
import com.mukesh.OtpView
import com.waycool.core.retrofit.OTPApiCient
import com.waycool.data.Network.ApiInterface.OTPApiInterface
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.repository.domainModels.OTPResponseDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.NetworkUtil
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
    private var bottomSheetDialog: BottomSheetDialog? = null
    lateinit var binding: FragmentOtpBinding
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

        setTranslations()
        CoroutineScope(Dispatchers.IO).launch {
            fcmToken = loginViewModel.getFcmToken()
            mobileManufacturer = loginViewModel.getDeviceManufacturer()
            mobileModel = loginViewModel.getDeviceModel()

        }


        CoroutineScope(Dispatchers.Main).launch {
            val enterCode = TranslationsManager().getString("enter_otp_code")
            if (!enterCode.isNullOrEmpty()) {
                binding.receiveMsgTv.text = buildString {
                    append(enterCode)
                    append(" +91- ")
                    append(mobileNumber)
                }
            } else {
                binding.receiveMsgTv.text = buildString {
                    append(getString(R.string.opt_text2))
                    append(" +91- ")
                    append(mobileNumber)
                }
            }
        }
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
            EventClickHandling.calculateClickEvent("OTP_VIA_Call")
        }

        binding.doneBtn.setOnClickListener {
            validateOTP()
        }

        binding.resendMsgBtn.setOnClickListener {
            retryOtp("text")
            showTimer()
            EventClickHandling.calculateClickEvent("Resend_OTP")
        }

        binding.backBtn.setOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
        showTimer()
        return binding.root
    }

    private fun setTranslations() {
        TranslationsManager().loadString(
            "otp_verification",
            binding.enterNumberTv,
            "OTP verification"
        )
        TranslationsManager().loadString(
            "enter_otp_code",
            binding.receiveMsgTv,
            "Enter the 4 digit code sent to you on"
        )
        TranslationsManager().loadString("enter_otp", binding.tvEnterOtp, "Enter OTP")
        TranslationsManager().loadString(
            "receive_otp",
            binding.didnotReceiveMsgTv,
            "Don’t receive OTP?"
        )
        TranslationsManager().loadString("resend_otp", binding.resendMsgBtn, "Resend OTP")
        TranslationsManager().loadString("txt_or", binding.otpResendOr, "Or")
        TranslationsManager().loadString("get_otp_call", binding.otpViaCall, "Get OTP via Call")
    }

    private fun retryOtp(type: String) {
        loginViewModel.retryOtp(mobileNumber, type).observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    val otpResponse: OTPResponseDomain? = it.data
                    if (otpResponse?.type == "success") {
                    }
                }
                is Resource.Loading -> {}
                is Resource.Error -> {
                    ToastStateHandling.toastError(
                        requireContext(),
                        "Error Occurred",
                        Toast.LENGTH_SHORT
                    )

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
                if (it.length == 4)
                    validateOTP()
            },
            type = OTP_VIEW_TYPE_BORDER,
            password = false,
            containerSize = 48.dp,
            passwordChar = "•",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            charColor = Color(resources.getColor(com.waycool.uicomponents.R.color.primaryColor))
        )


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
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                }
            }
        }
    }

    private val isSmsPermissionGranted: Boolean
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
                                if (otpResponse.message == "Max limit reached for this otp verification") {
                                    ToastStateHandling.toastError(
                                        requireContext(),
                                        "You have reached the maximum limit for the otp verification.Get OTP again",
                                        Toast.LENGTH_LONG
                                    )
                                    //go to login fragment
                                    findNavController().popBackStack()
                                } else {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        val toastServerError = TranslationsManager().getString("wrong_otp")
                                        if(!toastServerError.isNullOrEmpty()){
                                            context?.let { it1 -> ToastStateHandling.toastError(it1,toastServerError,
                                                Toast.LENGTH_SHORT
                                            ) }}
                                        else {context?.let { it1 -> ToastStateHandling.toastError(it1,"Wrong Otp",
                                            Toast.LENGTH_SHORT
                                        ) }}}
                                }
                            }

                        }
                        is Resource.Loading -> {}
                        is Resource.Error -> {
                            CoroutineScope(Dispatchers.Main).launch {
                                val toastServerError = TranslationsManager().getString("server_error")
                                if(!toastServerError.isNullOrEmpty()){
                                    context?.let { it1 -> ToastStateHandling.toastError(it1,toastServerError,
                                        Toast.LENGTH_SHORT
                                    ) }}
                                else {context?.let { it1 -> ToastStateHandling.toastError(it1,"Server Error Occurred",
                                    Toast.LENGTH_SHORT
                                ) }}}

                        }
                    }
                }
        } else {
            context?.let {
                ToastStateHandling.toastError(
                    it,
                    "Please enter the OTP",
                    Toast.LENGTH_LONG
                )
            }
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
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                //binding.doneBtn.isEnabled = false
                getCancelResult()
            }
        }
    }

    private fun getCancelResult() {
        countDownTimer!!.cancel()
        binding.progressCircular.visibility = View.GONE
        binding.otpResendLayout.visibility = View.VISIBLE
        binding.otpCallLayout.visibility = View.VISIBLE
    }

    private fun getOtpFromMessage(message: String?) {
        // This will match any 6 digit number in the message
        val pattern = Pattern.compile("(|^)\\d{4}")
        val matcher = pattern.matcher(message)
        if (matcher.find()) {
            countDownTimer!!.cancel()
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
        registerBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(smsBroadcastReceiver)
    }

    private fun showTimer() {
        countDownTimer = null
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(30 * 1 * 1000, 1000) {
            // adjust the milli seconds here
            @SuppressLint("DefaultLocale", "SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                binding.progressCircular.visibility = View.VISIBLE
                binding.otpResendLayout.visibility = View.GONE
                binding.otpCallLayout.visibility = View.GONE
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
                binding.progressCircular.visibility = View.GONE
                binding.otpResendLayout.visibility = View.VISIBLE
                binding.otpCallLayout.visibility = View.VISIBLE
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
            context?.let { ToastStateHandling.toastError(it, "No internet", Toast.LENGTH_SHORT) }
        } else {
            apiOTP(mobileNumber)
        }
    }


    fun verifyUser() {
        if (NetworkUtil.getConnectivityStatusString(context) == 0) {
            context?.let { ToastStateHandling.toastError(it, "No internet", Toast.LENGTH_SHORT) }
        } else {

            loginViewModel.login(mobileNumber, fcmToken!!, mobileModel!!, mobileManufacturer!!)
                .observeOnce(
                    requireActivity()
                ) {

                    Log.d("Login_OTP","${it.data}")

                    when (it) {
                        is Resource.Success -> {
                            val loginMaster = it.data
                            if (loginMaster?.status == true) {

                                if (!(loginMaster.data?.isEmpty())!!) {
                                    loginViewModel.setIsLoggedIn(true)
                                    loginViewModel.setMobileNumber(mobileNumber)
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
                                ToastStateHandling.toastSuccess(
                                    requireContext(),
                                    "Logged in successfully",
                                    Toast.LENGTH_SHORT
                                )

                            } else {
                                if (loginMaster?.data == "406") {
                                    bottomSheetDialog = BottomSheetDialog(requireContext())
                                    bottomSheetDialog?.setContentView(R.layout.bottom_dialog_logged_in_details)
                                    val logginTv =
                                        bottomSheetDialog?.findViewById<TextView>(R.id.loggin_text_dialog)
                                    val continueBtn =
                                        bottomSheetDialog?.findViewById<Button>(R.id.continue_loggin_dialog)
                                    val goBackBtn =
                                        bottomSheetDialog?.findViewById<Button>(R.id.goback_login_dialog)
                                    bottomSheetDialog?.setCancelable(false)
                                    bottomSheetDialog?.setCanceledOnTouchOutside(false)
                                    bottomSheetDialog?.show()
                                    goBackBtn!!.setOnClickListener { view: View? -> bottomSheetDialog?.dismiss() }
                                    logginTv!!.text =
                                        Html.fromHtml("You have already logged in  <b>" + "another Devices" + "</b>. Click on <b>Continue</b> to login.")
                                    continueBtn!!.setOnClickListener { view: View? ->
                                        bottomSheetDialog?.dismiss()
                                        loginViewModel.logout(mobileNumber)
                                            .observeOnce(requireActivity()) { logout->
                                                Log.d("Login_OTP","${logout.data}")
                                                verifyUser()
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


    private fun moveToRegistration() {
        val args = Bundle()
        args.putString(
            getString(R.string.mobile_stringextra),
            mobileNumber
        )
        binding.pb.visibility = View.VISIBLE
        Handler().postDelayed({
            binding.pb.visibility = View.GONE
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_otpFragment_to_registrationFragment, args)
        }, 500)


    }

    fun apiOTP(mobileNumber: String) {
        loginViewModel.getOtp(mobileNumber).observe(requireActivity()) {
            if (it is Resource.Success) {
                context?.let { it1 ->
                    ToastStateHandling.toastSuccess(
                        it1,
                        "OTP Sent",
                        Toast.LENGTH_SHORT
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bottomSheetDialog?.dismiss()
    }

    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("OtpFragment")
    }

    fun <T> LiveData<T>.observeOnce(
        owner: LifecycleOwner,
        reactToChange: (T) -> Unit
    ): Observer<T> {
        val wrappedObserver = object : Observer<T> {
            override fun onChanged(data: T) {
                reactToChange(data)
                removeObserver(this)
            }
        }

        observe(owner, wrappedObserver)
        return wrappedObserver
    }
}


