package com.waycool.data.Network

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.waycool.core.retrofit.OTPApiCient
import com.waycool.core.retrofit.OutgrowClient
import com.waycool.core.retrofit.WeatherClient
import com.waycool.core.utils.AppSecrets
import com.waycool.data.Local.Entity.UserDetailsEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.ApiInterface.ApiInterface
import com.waycool.data.Network.ApiInterface.OTPApiInterface
import com.waycool.data.Network.ApiInterface.WeatherApiInterface
import com.waycool.data.Network.NetworkModels.*
import com.waycool.data.Network.PagingSource.MandiPagingSource
import com.waycool.data.Network.PagingSource.VansPagingSource
import com.waycool.data.repository.DomainMapper.MandiDomainMapper
import com.waycool.data.repository.domainModels.MandiDomain
import com.waycool.data.repository.domainModels.MandiDomainRecord
import com.waycool.data.repository.domainModels.MandiHistoryDomain
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.awaitResponse
import kotlin.Exception

object NetworkSource {

     val apiInterface: ApiInterface
    private val weatherInterface: WeatherApiInterface
    private val headerMapPublic: Map<String, String>
    private val otpInterface: OTPApiInterface


    init {
        val internalRetrofit: Retrofit = OutgrowClient.retrofit
        apiInterface = internalRetrofit.create(ApiInterface::class.java)
        headerMapPublic = AppSecrets.getHeaderPublic()
        val otpRetrofit: Retrofit = OTPApiCient.apiClient
        otpInterface = otpRetrofit.create(OTPApiInterface::class.java)
        val weatherClient = WeatherClient.apiClient
        weatherInterface = weatherClient.create(WeatherApiInterface::class.java)
    }

    fun getTagsAndKeywords(headerMap: Map<String, String>) = flow<Resource<TagsAndKeywordsDTO>> {


        emit(Resource.Loading())

        try {
            val response = apiInterface.getTagsAndKeywords(headerMap).awaitResponse()

            if (response.isSuccessful) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(
                    Resource.Error(
                        response.errorBody()?.charStream()?.readText()
                    )
                )
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.message))

        }

    }


    fun getLanguageMaster() = flow<Resource<LanguageMasterDTO?>> {
        emit(Resource.Loading())
        try {
            val response = apiInterface.getLanguageMaster(headerMapPublic)

            if (response.isSuccessful) {
                emit(Resource.Success(response.body()))
            } else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }


    fun getCropMaster(headerMap: Map<String, String>) = flow<Resource<CropMasterDTO?>> {
        try {
            val response = apiInterface.getCropMaster(headerMap)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()))
            } else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }


    fun getVansCategory(headerMap: Map<String, String>) = flow<Resource<VansCategoryDTO?>> {
        try {
            val response = apiInterface.getVansCategory(headerMap)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()))
            } else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun getUserDetails(headerMap: Map<String, String>) = flow<Resource<UserDetailsDTO?>> {
        try {
            val response = apiInterface.getUserDetails(headerMap)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()))
            } else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun register(map: Map<String, String>) = flow<Resource<RegisterDTO?>> {
        emit(Resource.Loading())
        try {
            val response = apiInterface.register(headerMapPublic, map)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()))
            } else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun getModuleMaster() = flow<Resource<ModuleMasterDTO?>> {
        try {
            val response = apiInterface.getModuleMaster(headerMapPublic)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()))
            } else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun logout(contact: String) = flow<Resource<LogoutDTO?>> {
        emit(Resource.Loading())
        try {
            val response = apiInterface.logout(headerMapPublic, contact)

            if (response.isSuccessful) {
                emit(Resource.Success(response.body()))
            } else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun getVansFeeder(queryMap: MutableMap<String, String>): Flow<PagingData<VansFeederListNetwork>> {
        return Pager(
            config = PagingConfig(pageSize = 15, maxSize = 50),
            pagingSourceFactory = { VansPagingSource(apiInterface, queryMap) }
        ).flow
    }


    fun getCropCategoryMaster(headerMap: Map<String, String>) =
        flow<Resource<CropCategoryMasterDTO?>> {

            try {
                val response = apiInterface.getCropCategoryMaster(headerMap)
                if (response.isSuccessful) {
                    emit(Resource.Success(response.body()))
                } else {
                    emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message))
            }
        }

    fun addCropPassData(
        crop_id: Int,
        account_id: Int,
        plot_nickname: String,
        is_active: Int,
        sowing_date: String
    ) = flow<Resource<AddCropResponseDTO?>> {
        try {
            val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
            val response = apiInterface.addCropPassData(
                headerMap!!,
                crop_id,
                account_id,
                plot_nickname,
                is_active,
                sowing_date
            )
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()))
            } else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun postNewSoil(org_id:Int,plot_no: String, pincode: String, address: String, number: String) =
        flow<Resource<SoilTestResponseDTO?>> {
            try {
                val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
                val response =
                    apiInterface.postNewSoil(headerMap!!, org_id,plot_no, pincode, address, number)
                if (response.isSuccessful) {
                    emit(Resource.Success(response.body()))
                } else {
                    Log.d("Soil", "postNewSoil: " + response.errorBody()?.charStream()?.readText())
                    emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
                }
            } catch (e: Exception) {
                Log.d("Soil", "postNewSoil: " + e.message)
                emit(Resource.Error(e.message))
            }
        }


    fun login(
        contact: String,
        password: String,
        fcm_token: String,
        mobile_model: String,
        mobile_manufacturer: String
    ) = flow<Resource<LoginDTO?>> {
        emit(Resource.Loading())
        try {
            val response = apiInterface.login(
                headerMapPublic,
                contact,
                password,
                fcm_token,
                mobile_model,
                mobile_manufacturer
            )

            if (response.isSuccessful) {
                emit(Resource.Success(response.body()))
            } else if (response.code() == 406) {
                emit(
                    Resource.Success(
                        LoginDTO(
                            false,
                            "User Already Logged-in", response.code().toString()
                        )
                    )
                )

            } else if (response.code() == 422) {
                emit(Resource.Success(LoginDTO(false, "", "422")))
            } else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            Log.d("Network", "LoginAPI : ${e.message}")
            emit(Resource.Error(e.message))
        }
    }


    fun requestOTP(contact: String) = flow<Resource<OTPResponseDTO?>> {
        emit(Resource.Loading())

        try {


            val response = otpInterface.sendOTP(
                AppSecrets.getOTPKey(),
                AppSecrets.getOTPTemplateId(), "91$contact"
            )

            if (response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }


    fun retryOTP(contact: String, type: String) = flow<Resource<OTPResponseDTO?>> {
//        emit(Resource.Loading())

        try {
            val response = otpInterface.retryOTP(
                AppSecrets.getOTPKey(),
                "91$contact", type
            )

            if (response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun verifyOTP(contact: String, otp: String) = flow<Resource<OTPResponseDTO?>> {
        try {
            val response = otpInterface.verifyOTP(
                AppSecrets.getOTPKey(), otp,
                "91$contact"
            )

            if (response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun getAiCropHistory(headerMap: Map<String, String>) =
        flow<Resource<AiCropHistoryDTO?>> {
            try {
                val response = apiInterface.getAiCropHistory(headerMap)
                if (response.isSuccessful)
                    emit(Resource.Success(response.body()))
                else {
                    emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message))
            }
        }

    fun detectAiCrop(
        cropId: Int,
        cropName: String,
        image: MultipartBody.Part
    ) = flow<Resource<AiCropDetectionDTO?>> {

            val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
            val userDetailsEntity = LocalSource.getUserDetailsEntity() ?: UserDetailsEntity()

        try {
            val response =
                headerMap?.let { userDetailsEntity.id?.let { it1 ->
                    apiInterface.postAiCrop(it,
                        it1, cropId, cropName, image)
                } }

            if (response != null) {
                if (response.isSuccessful)
                    emit(Resource.Success(response.body()))
                else {
                    emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun getPestDisease(cropId: Int? = null) =
        flow<Resource<PestDiseaseDTO?>> {
            emit(Resource.Loading())
            try {
                val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
                if (headerMap != null) {
                    val response = apiInterface.getPestDisease(headerMap, cropId)

                    if (response.isSuccessful)
                        emit(Resource.Success(response.body()))
                    else {
                        emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
                    }
                } else {
                    throw RuntimeException("Header Map is null")
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message))
            }
        }

    fun getWeatherForecast(lat:String,lon:String,lang:String="en") = flow<Resource<WeatherDTO?>> {
        try {
            val response = weatherInterface.getWeather(lat,lon,AppSecrets.getWeatherApiKey(),lang)
            if (response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }

        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }


    //add crop
    fun getAddCropType(headerMap: Map<String, String>) = flow<Resource<AddCropTypeDTO?>> {
        try {
            val response = apiInterface.getAddCropType(headerMap)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()))
            } else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun getSoilTesAllHistory(headerMap: Map<String, String>, account_id: Int) =
        flow<Resource<SoilTestHistoryDTO?>> {
            try {
                val response = apiInterface.getSoilTestAllHistory(headerMap, account_id)
                if (response.isSuccessful) {
                    emit(Resource.Success(response.body()))
                } else {
                    emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message))
            }
        }

    fun getSoilTestLab(account_id: Int, lat: Double, long: Double) =
        flow<Resource<CheckSoilTestLabDTO?>> {
            try {
//            val header =
//                LocalSource.getUserDetailsEntity()?.account
//                    ?.firstOrNull { it.accountType == "outgrow" }
                val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()

                val response = apiInterface.getSoilTestLab(headerMap!!, account_id, lat, long)
                if (response.isSuccessful) {
                    emit(Resource.Success(response.body()))
                } else {
                    emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message))
            }
        }

    fun getTracker(soil_test_request_id: Int) = flow<Resource<TrackerDTO?>> {
        try {
//            val header =
//                LocalSource.getUserDetailsEntity()?.account
//                    ?.firstOrNull { it.accountType == "outgrow" }
            val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()

            val response = apiInterface.getTracker(headerMap!!, soil_test_request_id)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()))
            } else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }


    fun getCropInformation(
        headerMap: Map<String, String>,
    ) = flow<Resource<CropInfo?>> {

        emit(Resource.Loading())
        try {
            val response = apiInterface.getCropInformation(headerMap)

            if (response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun updateProfile(
        name: String,
        address: String, village: String, pincode: String, state: String, district: String,
        headerMap: Map<String, String>,
    ) = flow<Resource<profile?>> {

        emit(Resource.Loading())
        try {
            val response = apiInterface.updateProfile(
                headerMap,
                name,
                address,
                village,
                pincode,
                state,
                district
            )

            if (response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun getUserProfile(
        headerMap: Map<String, String>,
    ) = flow<Resource<UserDetailsDTO?>> {

        emit(Resource.Loading())
        try {
            val response = apiInterface.getUserDetails(headerMap)

            if (response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun getUserProfilePic(
        headerMap: Map<String, String>, file: MultipartBody.Part
    ) = flow<Resource<profilePicModel?>> {

        emit(Resource.Loading())
        try {
            val response = apiInterface.getProfilePic(headerMap, file)

            if (response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }
//    fun getFullMandiList(
//        headerMap: Map<String, String>?,crop_category:String?,state:String?,crop:String?,
//         sortBy: String, orderBy: String?
//    ) = flow<Resource<MandiDomain?>> {
//
//        emit(Resource.Loading())
//        try {
//            val response = apiInterface.getMandiList(headerMap,"77.22","18.22",crop_category,
//                state,crop,1,sortBy,orderBy)
//
//            if (response.isSuccessful)
//                emit(Resource.Success(response.body()))
//            else {
//                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
//            }
//        } catch (e: Exception) {
//            emit(Resource.Error(e.message))
//        }
//    }
    fun getMandiList(lat: String?,lon: String?,crop_category:String?,state:String?,crop:String?,
                     sortBy: String, orderBy: String?,search:String?): Flow<PagingData<MandiDomainRecord>> {
        return Pager(
            config = PagingConfig(pageSize = 50, maxSize = 150),
            pagingSourceFactory = { MandiPagingSource(
                apiInterface,lat,lon,crop_category,
                state,crop,sortBy,orderBy,search) }
        ).flow
    }
    fun getMandiHistory(
        headerMap: Map<String, String>,crop_master_id:Int?,mandi_master_id:Int?
    ) = flow<Resource<MandiHistoryDomain?>> {

        emit(Resource.Loading())
        try {
            val response = apiInterface.getMandiHistory(headerMap,crop_master_id,mandi_master_id)

            if (response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }
}

