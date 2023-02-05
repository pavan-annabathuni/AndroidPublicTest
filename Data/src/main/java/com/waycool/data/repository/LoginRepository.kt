package com.waycool.data.repository

import android.util.Log
import com.waycool.data.Local.DataStorePref.DataStoreManager
import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.NetworkModels.LoginDTO
import com.waycool.data.Network.NetworkModels.LogoutDTO
import com.waycool.data.Network.NetworkModels.OTPResponseDTO
import com.waycool.data.Network.NetworkModels.RegisterData
import com.waycool.data.Network.NetworkSource
import com.waycool.data.Sync.SyncManager
import com.waycool.data.Sync.syncer.LanguageSyncer
import com.waycool.data.Sync.syncer.ModuleMasterSyncer
import com.waycool.data.Sync.syncer.UserDetailsSyncer
import com.waycool.data.repository.DomainMapper.*
import com.waycool.data.repository.domainModels.*
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


object LoginRepository {


    fun getLanguageMaster(): Flow<Resource<List<LanguageMasterDomain>>> {
        return LanguageSyncer.getData()
            .map {
                when (it) {
                    is Resource.Success -> {
                        Log.d("LoginRepo", "Language Data: ${it.data?.size?:"EMPTY List"}")
                        Resource.Success(
                            LanguageMasterDomainMapper().toDomainList(it.data ?: emptyList())
                        )

                    }
                    is Resource.Loading -> {
                        Log.d("LoginRepo", "Language Data: Loading")

                        Resource.Loading()
                    }
                    is Resource.Error -> {
                        Log.d("LoginRepo", "Language Data: Loading")

                        Resource.Error(it.message)
                    }
                }
            }
    }


    fun setSelectedLanguageCode(langCode: String?, langId: Int?, langNative:String?) {
        GlobalScope.launch(Dispatchers.IO) {
            LocalSource.saveSelectedLanguage(langCode, langId,langNative)
            SyncManager.invalidateAll()
            TranslationsManager().init()
        }
    }

    fun login(
        mobile_no: String,
        fcmToken: String,
        deviceModel: String,
        deviceMan: String
    ): Flow<Resource<LoginDomain>> {
        return NetworkSource.login(
            mobile_no,
//            password = "outgrow_$mobile_no",
            fcmToken,
            deviceModel,
            deviceMan
        )
            .map {
                when (it) {
                    is Resource.Success -> {
                        Resource.Success(
                            LoginDomainMapper().mapToDomain(it.data ?: LoginDTO())
                        )
                    }
                    is Resource.Loading -> {
                        Resource.Loading()
                    }
                    is Resource.Error -> {
                        Resource.Error(it.message)
                    }
                }
            }

    }

//    private var _loginLiveData = MutableLiveData<LoginDTO>()
//    val loginLiveData get() = _loginLiveData

//    fun login(
//        mobile_no: String,
//        fcmToken: String,
//        deviceModel: String,
//        deviceMan: String
//    ) {
//
//        apiInterface.getLoginMaster(
//            AppSecrets.getHeaderPublic(),
//            mobile_no,
//            "outgrow_$mobile_no",
//            fcmToken,
//            deviceModel,
//            deviceMan
//        ).enqueue(object : Callback<LoginDTO?> {
//            override
//            fun onResponse(call: Call<LoginDTO?>, response: Response<LoginDTO?>) {
//                Log.d("logindata", "response" + response.code())
//                Log.d("logindata", "response" + response.message())
//                if (response.isSuccessful) {
//                    _loginLiveData.setValue(response.body())
//                } else if (response.code() == 406) {
//                    _loginLiveData.setValue(
//                        LoginDTO(
//                            false,
//                            "User Already Logged-in",
//                            response.code().toString()
//                        )
//                    )
//                } else if (response.code() == 422) {
//                    _loginLiveData.setValue(LoginDTO(false, "", "422"))
//                }
//            }
//
//            override fun onFailure(call: Call<LoginDTO?>, t: Throwable) {
//                Log.d("logindata", "response" + t.message)
//            }
//        })
//    }


    fun logout(mobile_no: String): Flow<Resource<LogoutDomain>> {
        return NetworkSource.logout(mobile_no).map {
            when (it) {
                is Resource.Success -> {
                    Resource.Success(
                        LogoutDomainMapper().mapToDomain(it.data ?: LogoutDTO())
                    )
                }
                is Resource.Loading -> {
                    Resource.Loading()
                }
                is Resource.Error -> {
                    Resource.Error(it.message)
                }
            }
        }
    }

//    private var _logoutLiveData = MutableLiveData<LoginDTO>()
//    val logoutLiveData get() = _loginLiveData
//    fun logout(mobile_no: String): LiveData<LoginDTO?> {
//
//
//        apiInterface.getLoginMaster(AppSecrets.getHeaderPublic(), mobile_no)
//            .enqueue(object : Callback<LoginDTO?> {
//                override fun onResponse(
//                    call: Call<LoginDTO?>,
//                    response: Response<LoginDTO?>
//                ) {
//                    if (response.isSuccessful) {
//                        _logoutLiveData.setValue(response.body())
//                    }
//                }
//
//                override fun onFailure(call: Call<LoginDTO?>, t: Throwable) {
//                }
//            })
//        return _logoutLiveData
//    }

    fun getUserDetails(): Flow<Resource<UserDetailsDomain>> {
        return UserDetailsSyncer.getData().map {
            when (it) {
                is Resource.Success -> {
                    Log.d("TAG", "getUserDetailsAccountID:${it.data} ")
                    Resource.Success(UserDetailsDomainMapper().mapToDomain(it.data!!))
                }
                is Resource.Loading -> {
                    Resource.Loading()
                }
                is Resource.Error -> {
                    Resource.Error(it.message)
                }
            }
        }
    }


//    fun getUserDetails(context: Context?, accessToken: String): LiveData<UserDetailsDTO?> {
//        val userDetailsMutableLiveData = MutableLiveData<UserDetailsDTO?>()
//        val header: MutableMap<String, String> = HashMap()
//        header["Authorization"] = "Bearer $accessToken"
//        header["Accept"] = "application/json, text/plain, */*"
//        //        headerMap.put("x-api-key", "a9c1dc00-1761-478c-8fa5-f216d1e8d82b");
//        val apiService = OutgrowClient.retrofit.create(ApiInterface::class.java)
//        val call = apiService.getUserDetails(header)
//        call.enqueue(object : Callback<UserDetailsDTO?> {
//            override fun onResponse(
//                call: Call<UserDetailsDTO?>,
//                response: Response<UserDetailsDTO?>
//            ) {
//                if (response.isSuccessful) {
//                    val userDetails = response.body()
//                    userDetailsMutableLiveData.setValue(userDetails)
//                    SharedPreferenceUtility.setUserDetais(context, userDetails)
//                    Log.d("UserDetails", "Success$userDetails")
//                } else {
//                    Log.d("UserDetails", "Failed" + response.errorBody())
//                }
//            }
//
//            override fun onFailure(call: Call<UserDetailsDTO?>, t: Throwable) {
//                // feedbackMaster.setValue(null);
//                Log.d("UserDetails", "Error" + t.message)
//            }
//        })
//        return userDetailsMutableLiveData
//    }

    fun setIsFirst(first: Boolean) {

        CoroutineScope(Dispatchers.IO).launch {
            DataStoreManager.saveIsFirstTime(first)
        }
    }

    suspend fun getIsFirst(): Boolean = DataStoreManager.isFirstTime()

    fun saveFcmToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            DataStoreManager.saveFcmToken(token)
        }
    }

    suspend fun getFcmToken() = DataStoreManager.getFcmToken()

    fun saveDeviceDetails(deviceManufacturer: String, deviceName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            DataStoreManager.saveDeviceDetails(deviceManufacturer, deviceName)
        }
    }

    suspend fun getDeviceManufacturer() = DataStoreManager.getDeviceManufacturer()
    suspend fun getDeviceModel() = DataStoreManager.getDeviceModel()

    fun setIsLoggedIn(isLoggedIn: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            DataStoreManager.saveIsLoggedIn(isLoggedIn)
        }
    }

    fun setUserToken(userToken: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            DataStoreManager.saveUserToken(userToken)
        }
    }
//    fun getSelectedLanguageCode(){
//        CoroutineScope(Dispatchers.IO).launch {
//            DataStoreManager.getUserTokenFlow()
//        }
//    }
//    fun getSelectedLanguageCode() :Flow<Resource<String>>{
//        return DataStoreManager.getUserTokenFlow().map {
//            it
//        }
//    }

    suspend fun getSelectedLangCode()=LocalSource.getLanguageCode()
    suspend fun getSelectedLangId()=LocalSource.getLanguageId()
    suspend fun getSelectedLanguage()=LocalSource.getLanguage()


    suspend fun getIsLoggedIn() = DataStoreManager.isLoggedIn()

    suspend fun getUserToken() = DataStoreManager.getUserToken()

    fun getOTP(mobile_no: String): Flow<Resource<OTPResponseDomain>> {
        return NetworkSource.requestOTP(mobile_no).map {
            when (it) {
                is Resource.Success -> {
                    Resource.Success(
                        OTPResponseDomainMapper().mapToDomain(it.data ?: OTPResponseDTO())
                    )
                }
                is Resource.Loading -> {
                    Resource.Loading()
                }
                is Resource.Error -> {
                    Resource.Error(it.message)
                }
            }
        }
    }

    fun retryOTP(mobile_no: String, type: String): Flow<Resource<OTPResponseDomain>> {
        return NetworkSource.retryOTP(mobile_no, type).map {
            when (it) {
                is Resource.Success -> {
                    Resource.Success(
                        OTPResponseDomainMapper().mapToDomain(it.data ?: OTPResponseDTO())
                    )
                }
                is Resource.Loading -> {
                    Resource.Loading()
                }
                is Resource.Error -> {
                    Resource.Error(it.message)
                }
            }
        }
    }


    fun verifyOTP(mobile_no: String, otp: String): Flow<Resource<OTPResponseDomain>> {
        return NetworkSource.verifyOTP(mobile_no, otp).map {
            when (it) {
                is Resource.Success -> {
                    Resource.Success(
                        OTPResponseDomainMapper().mapToDomain(it.data ?: OTPResponseDTO())
                    )
                }
                is Resource.Loading -> {
                    Resource.Loading()
                }
                is Resource.Error -> {
                    Resource.Error(it.message)
                }
            }
        }
    }

//    fun getOTP(context: Context, mobile_no: String) {
//
//        otpInterface.sendOTP(
//            AppSecrets.getOTPKey(), AppSecrets.getOTPTemplateId(),
//            "91$mobile_no"
//        )
//            .enqueue(object : Callback<OTPResponseDTO?> {
//                override fun onResponse(
//                    call: Call<OTPResponseDTO?>,
//                    response: Response<OTPResponseDTO?>
//                ) {
//                    // you can get otp form message filled
//                    Toast.makeText(context, "OTP Sent", Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onFailure(call: Call<OTPResponseDTO?>, t: Throwable) {
//                    Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show()
//                    // you can get otp form message filled
//                }
//            })
//    }
//
//    fun retryOtp(context: Context, mobileNumber: String, type: String) {
//        otpInterface.retryOTP(AppSecrets.getOTPKey(), "91$mobileNumber", type)
//            .enqueue(object : Callback<OTPResponseDTO?> {
//                override fun onResponse(
//                    call: Call<OTPResponseDTO?>,
//                    response: Response<OTPResponseDTO?>
//                ) {
//                    Toast.makeText(context, "Retry Requested", Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onFailure(
//                    call: Call<OTPResponseDTO?>,
//                    t: Throwable
//                ) {
//                    Toast.makeText(context, "Retry Request Failed", Toast.LENGTH_SHORT)
//                        .show()
//                }
//            })
//    }
//
//    fun verifyOtp(context: Context, otp: String, mobileNumber: String): MutableLiveData<Boolean> {
//        var verifyLivedata = MutableLiveData<Boolean>()
//        verifyLivedata.postValue(false)
//        otpInterface.verifyOTP(AppSecrets.getOTPKey(), otp, mobileNumber)
//            .enqueue(object : Callback<OTPResponseDTO?> {
//                override fun onResponse(
//                    call: Call<OTPResponseDTO?>,
//                    response: Response<OTPResponseDTO?>
//                ) {
//                    if (response.?isSuccessful) {
//                        // you can get otp form message filled
//                        Toast.makeText(context, "OTP Verified", Toast.LENGTH_SHORT).show()
//                        verifyLivedata.postValue(true)
//
//                    }
//                }
//
//                override fun onFailure(call: Call<OTPResponseDTO?>, t: Throwable) {
//                    Toast.makeText(context, "OTP Verification Failed", Toast.LENGTH_SHORT).show()
//                    // you can get otp form message filled
//                }
//            })
//        return verifyLivedata
//    }

    fun setMobileNumber(mobileNo: String) {
        CoroutineScope(Dispatchers.IO).launch {
            DataStoreManager.setMobileNumber(mobileNo)
        }
    }

    suspend fun getMobileNumber(): String? = DataStoreManager.getMobileNumber()

    fun register(fields: HashMap<String, String>): Flow<Resource<RegisterDomain>> {
        return NetworkSource.register(fields).map {
            when (it) {
                is Resource.Success -> {
                    Resource.Success(
                        RegistrationDomainMapper().mapToDomain(it.data?.data ?: RegisterData())
                    )
                }
                is Resource.Loading -> {
                    Resource.Loading()
                }
                is Resource.Error -> {
                    Resource.Error(it.message)
                }
            }
        }
    }


    fun getModuleMaster(): Flow<Resource<List<ModuleMasterDomain>>> {
        return ModuleMasterSyncer().getData().map {
            when (it) {
                is Resource.Success -> {
                    Resource.Success(
                        ModuleMasterDomainMapper().toDomainList(it.data ?: emptyList())
                    )
                }
                is Resource.Loading -> {
                    Resource.Loading()
                }
                is Resource.Error -> {
                    Resource.Error(it.message)
                }
            }
        }
    }
}

