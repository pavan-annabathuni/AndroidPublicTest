package com.waycool.featurelogin.loginViewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.waycool.data.Repository.DomainModels.*
import com.waycool.data.Repository.LoginRepository
import com.waycool.data.utils.Resource
import kotlinx.coroutines.*

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModel(application: Application) : AndroidViewModel(application) {

//    val languageLiveData get() = LoginRepository.getLanguageMaster()
//    val loginLiveData get() = LoginRepository.loginLiveData
//    val logoutLiveData get() = LoginRepository.logoutLiveData


    fun getLanguageList(): LiveData<Resource<List<LanguageMasterDomain>>> =
        LoginRepository.getLanguageMaster().asLiveData()


     fun setSelectedLanguage(langCode: String?, langId: Int?) {
        LoginRepository.setSelectedLanguageCode(langCode, langId)
    }

    fun login(
        mobile_no: String,
        fcmToken: String,
        deviceModel: String,
        deviceMan: String
    ): LiveData<Resource<LoginDomain>> =
        LoginRepository.login(
            mobile_no,
            fcmToken,
            deviceModel,
            deviceMan
        ).asLiveData()


    fun logout(mobile_no: String): LiveData<Resource<LogoutDomain>> =
        LoginRepository.logout(mobile_no).asLiveData()


    fun getUserDetails(): LiveData<Resource<UserDetailsDomain>> =
        LoginRepository.getUserDetails().asLiveData()

    /*  public LiveData<AuthorizeOTP> authorizeOTP(Context context,String mobile_no,String otp,String FcmToken,String androidId, String msg){
        return loginRepository.authorizedOTP(context,mobile_no,otp,FcmToken,androidId,msg);
    }*/

    fun setIsFirst(isFirst: Boolean) = LoginRepository.setIsFirst(isFirst)

    suspend fun getIsFirst(): Boolean = LoginRepository.getIsFirst()
    fun addFcmToken(token: String) {
        LoginRepository.saveFcmToken(token)
    }

    suspend fun getFcmToken() = LoginRepository.getFcmToken()

    fun saveDeviceDetails(deviceManufacturer: String, deviceName: String) {
        LoginRepository.saveDeviceDetails(deviceManufacturer, deviceName)
    }

    suspend fun getDeviceManufacturer() = LoginRepository.getDeviceManufacturer()

    suspend fun getDeviceModel() = LoginRepository.getDeviceModel()

    fun setIsLoggedIn(isLoggedIn: Boolean) = LoginRepository.setIsLoggedIn(isLoggedIn)

    suspend fun getIsLoggedIn() = LoginRepository.getIsLoggedIn()

    fun setUserToken(userToken: String?) = LoginRepository.setUserToken(userToken)


    suspend fun getUserToken() = LoginRepository.getUserToken()

    fun getOtp(mobileNumber: String) = LoginRepository.getOTP(mobileNumber).asLiveData()


    fun retryOtp(mobileNumber: String, type: String) =
        LoginRepository.retryOTP(mobileNumber, type).asLiveData()

    fun verifyOtp(
        context: Context,
        otp: String,
        mobileNumber: String
    ): LiveData<Resource<OTPResponseDomain>> =
        LoginRepository.verifyOTP(mobileNumber, otp).asLiveData()

    fun setMobileNumber(mobileNo: String) = LoginRepository.setMobileNumber(mobileNo)

    fun getMobileNumber(): String? {
        var mobileNumber: String? = null
        viewModelScope.launch {
            mobileNumber = LoginRepository.getMobileNumber()
        }
        return mobileNumber
    }

    fun getModuleMaster(): LiveData<Resource<List<ModuleMasterDomain>>> = LoginRepository.getModuleMaster().asLiveData()


    fun getUserData(query: HashMap<String, String>): LiveData<Resource<RegisterDomain>> = LoginRepository.register(query).asLiveData()

}