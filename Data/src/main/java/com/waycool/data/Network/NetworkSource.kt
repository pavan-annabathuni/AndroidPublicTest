package com.waycool.data.Network

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.waycool.core.retrofit.MapsClient
import com.waycool.core.retrofit.OTPApiCient
import com.waycool.core.retrofit.OutgrowClient
import com.waycool.core.retrofit.WeatherClient
import com.waycool.core.utils.AppSecrets
import com.waycool.data.Local.Entity.UserDetailsEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Local.utils.TypeConverter
import com.waycool.data.Network.ApiInterface.ApiInterface
import com.waycool.data.Network.ApiInterface.MapsApiInterface
import com.waycool.data.Network.ApiInterface.OTPApiInterface
import com.waycool.data.Network.ApiInterface.WeatherApiInterface
import com.waycool.data.Network.NetworkModels.*
import com.waycool.data.Network.PagingSource.MandiPagingSource
import com.waycool.data.Network.PagingSource.VansPagingSource
import com.waycool.data.repository.domainModels.MandiDomainRecord
import com.waycool.data.repository.domainModels.MandiHistoryDomain
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.awaitResponse
import kotlin.Exception

object NetworkSource {

    private val apiInterface: ApiInterface
    private val weatherInterface: WeatherApiInterface
    private val headerMapPublic: Map<String, String>
    private val otpInterface: OTPApiInterface

    private val geocodeInterface:MapsApiInterface


    init {
        val internalRetrofit: Retrofit = OutgrowClient.retrofit
        apiInterface = internalRetrofit.create(ApiInterface::class.java)
        headerMapPublic = AppSecrets.getHeaderPublic()
        val otpRetrofit: Retrofit = OTPApiCient.apiClient
        otpInterface = otpRetrofit.create(OTPApiInterface::class.java)
        val weatherClient = WeatherClient.apiClient
        weatherInterface = weatherClient.create(WeatherApiInterface::class.java)
        val geocodeCLient=MapsClient.apiClient
        geocodeInterface=geocodeCLient.create(MapsApiInterface::class.java)
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
            val langCode=LocalSource.getLanguageCode()?:"en"
            val response = apiInterface.getCropMaster(headerMap, lang = langCode)
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
            val langCode=LocalSource.getLanguageCode()?:"en"

            val response = apiInterface.getVansCategory(headerMap, lang = langCode)
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
            config = PagingConfig(pageSize = 15, prefetchDistance = 2),
            pagingSourceFactory = { VansPagingSource(apiInterface, queryMap) }
        ).flow
    }


    fun getCropCategoryMaster(headerMap: Map<String, String>) =
        flow<Resource<CropCategoryMasterDTO?>> {

            try {
                val langCode=LocalSource.getLanguageCode()?:"en"
                val response = apiInterface.getCropCategoryMaster(headerMap, lang = langCode)
                if (response.isSuccessful) {
                    emit(Resource.Success(response.body()))
                } else {
                    emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message))
            }
        }

//    fun addCropPassData(
//        crop_id: Int?,
//        account_id: Int?,
//        plot_nickname: String?,
//        is_active: Int?,
//        sowing_date: String?,
//        area: Editable?
//    ) = flow<Resource<AddCropResponseDTO?>> {
//        try {
//            val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
//            val response = apiInterface.addCropPassData(
//                headerMap!!,
//                crop_id,
//                account_id,
//                plot_nickname,
//                is_active,
//                sowing_date,
//                area
//            )
//            if (response.isSuccessful) {
//                emit(Resource.Success(response.body()))
//            } else {
//                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
//            }
//        } catch (e: Exception) {
//            emit(Resource.Error(e.message))
//        }
//    }
    fun addCropDataPass(map: MutableMap<String, Any> = mutableMapOf<String,Any>()) = flow<Resource<AddCropResponseDTO?>> {
        try {
            val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
            val response = apiInterface.
            addCropDataPass(headerMap!!,map)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()))
            } else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }
    fun activateDevice(map: MutableMap<String, Any> = mutableMapOf<String,Any>()) = flow<Resource<ActivateDeviceDTO?>> {
        try {
            val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
            val response = apiInterface.
            activateDevice(headerMap!!,map)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()))
            } else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }
    fun viewReport(id:Int) = flow<Resource<SoilTestReportMaster?>> {
        try {
            val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()

            val response = apiInterface.viewReport(headerMap!!,id)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()))
            } else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun postNewSoil(
        account_id: Int, lat: Double, long: Double, org_id: Int, plot_no: String, pincode: String,
        address: String, state: String, district: String, number: String
    ) =
        flow<Resource<SoilTestResponseDTO?>> {
            try {
                val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
                val response =
                    apiInterface.postNewSoil(
                        headerMap!!,
                        account_id,
                        lat,
                        long,
                        org_id,
                        plot_no,
                        pincode,
                        address,
                        state,
                        district,
                        number
                    )
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

    fun checkToken(user_id: Int, token: String) =
        flow<Resource<CheckTokenResponseDTO?>> {
            try {
                val headerMap: Map<String, String>? = AppSecrets.getHeaderPublic()
                val response =
                    apiInterface.checkToken(headerMap!!, user_id, token)
                if (response.isSuccessful) {
                    emit(Resource.Success(response.body()))
                } else if (response.code() == 404) {
                    val error = response.errorBody()?.charStream()?.readText() ?: ""
                    emit(Resource.Success(TypeConverter.convertStringToCheckToken(error)))
                } else {
                    Log.d("Token", "check token: " + response.errorBody()?.charStream()?.readText())
                    emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
                }
            } catch (e: Exception) {
                Log.d("Token", "check token:" + e.message)
                emit(Resource.Error(e.message))
            }
        }


    fun login(
        contact: String,
//        password: String,
        fcm_token: String,
        mobile_model: String,
        mobile_manufacturer: String
    ) = flow<Resource<LoginDTO?>> {
        emit(Resource.Loading())
        try {
            val response = apiInterface.login(
                headerMapPublic,
                contact,
//                password,
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
                headerMap?.let {
                    userDetailsEntity.userId?.let { it1 ->
                        apiInterface.postAiCrop(
                            it,
                            it1, cropId, cropName, image
                        )
                    }
                }

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
                val langCode=LocalSource.getLanguageCode()?:"en"

                val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
                if (headerMap != null) {
                    val response = apiInterface.getPestDisease(headerMap, cropId, lang = langCode)

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

    fun getWeatherForecast(lat: String, lon: String, lang: String = "en") =
        flow<Resource<WeatherDTO?>> {
            try {
                val response =
                    weatherInterface.getWeather(lat, lon, AppSecrets.getWeatherApiKey(), lang)
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
    fun dashBoard() =
        flow<Resource<DashBoardModel?>> {
            try {
                val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()

                val response = apiInterface.dashBoard(headerMap)
                if (response.isSuccessful) {
                    emit(Resource.Success(response.body()))
                } else {
                    emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message))
            }
        }
    fun farmDetailsDelta() =
        flow<Resource<FarmDetailsDTO?>> {
            try {
                val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
//                val headerMap: Map<String, String>? = AppSecrets.getHeaderPublic()
                val response = apiInterface.farmDetailsDelta(headerMap)
                if (response.isSuccessful) {
                    emit(Resource.Success(response.body()))
                } else {
                    emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message))
            }
        }

    fun getSoilTestLab(account_id: Int, lat: String?, long: String?) =
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
    fun getIotDevice() =
        flow<Resource<ViewDeviceDTO?>> {
            try {
//            val header =
//                LocalSource.getUserDetailsEntity()?.account
//                    ?.firstOrNull { it.accountType == "outgrow" }
                val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()

                val response = apiInterface.getIotDevice(headerMap!!)
                if (response.isSuccessful) {
                    emit(Resource.Success(response.body()))
                } else {
                    emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message))
            }
        }
    fun getGraphsViewDevice(serial_no_id:Int?,device_model_id:Int?,value:String?) =
        flow<Resource<GraphsViewDataDTO?>> {
            try {
//            val header =
//                LocalSource.getUserDetailsEntity()?.account
//                    ?.firstOrNull { it.accountType == "outgrow" }
                val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()

                val response = apiInterface.getGraphsViewDevice(headerMap!!,serial_no_id,device_model_id,value)
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
    fun pdfDownload(soil_test_request_id: Int) = flow<Resource<ResponseBody?>> {
        try {
//            val header =
//                LocalSource.getUserDetailsEntity()?.account
//                    ?.firstOrNull { it.accountType == "outgrow" }
            val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()

            val response = apiInterface.pdfDownload(headerMap!!, soil_test_request_id)
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
            val langCode=LocalSource.getLanguageCode()?:"en"
            val response = apiInterface.getCropInformation(headerMap, lang = langCode)

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
    fun getMandiList(
        lat: String?, lon: String?, crop_category: String?, state: String?, crop: String?,
        sortBy: String?, orderBy: String?, search: String?,accountId: Int?
    ): Flow<PagingData<MandiDomainRecord>> {
        return Pager(
            config = PagingConfig(pageSize = 50, prefetchDistance = 2, initialLoadSize = 2),
            pagingSourceFactory = {
                MandiPagingSource(
                    apiInterface, lat, lon, crop_category,
                    state, crop, sortBy, orderBy, search,accountId
                )
            }
        ).flow
    }

    fun getMandiHistory(
        headerMap: Map<String, String>, crop_master_id: Int?, mandi_master_id: Int?
    ) = flow<Resource<MandiHistoryDomain?>> {

        emit(Resource.Loading())
        try {
            val response = apiInterface.getMandiHistory(headerMap, crop_master_id, mandi_master_id)

            if (response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun getStateList(
        headerMap: Map<String, String>
    ) = flow<Resource<StateModel?>> {

        emit(Resource.Loading())
        try {
            val response = apiInterface.getStateList(headerMap)

            if (response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun editMyCrop(
        id:Int)
    = flow<Resource<Unit?>> {
        val map= LocalSource.getHeaderMapSanctum()?: emptyMap()
        emit(Resource.Loading())
        try {
                val response = apiInterface.editMyCrops(map,id)
             if(response.isSuccessful)
                 emit(Resource.Success(response.body()))
             else {
                 emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
             }

        } catch (e: Exception) {
            //   emit(Resource.Error(e.message))
        }
    }
    fun getMyCrop(headerMap: Map<String, String>,account_id: Int,
    ) = flow<Resource<MyCropsModel?>> {

        try {
            val response = apiInterface.getMyCrops(headerMap,account_id)

            if (response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun getMyCrop2(headerMap: Map<String, String>,account_id: Int,
    ) = flow<Resource<MyCropsModel?>> {

        try {
            val response = apiInterface.getMyCrops(headerMap,account_id)

            if (response.isSuccessful)
                emit(Resource.Success(response.body()))

            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun getGeocode(address: String
    ) = flow<GeocodeDTO?> {

        try {
            val response = geocodeInterface.getGeocode(address,AppSecrets.getMapsKey())

            if (response.isSuccessful)
                emit(response.body())
            else {
//                emit(response.errorBody()?.charStream()?.readText())
            }
        } catch (e: Exception) {
//            emit(e.message)
        }
    }

    fun getReverseGeocode(latlon: String
    ) = flow<GeocodeDTO?> {

        try {
            val response = geocodeInterface.getReverseGeocode(latlon,AppSecrets.getMapsKey())

            if (response.isSuccessful)
                emit(response.body())
            else {
//                emit(response.errorBody()?.charStream()?.readText())
            }
        } catch (e: Exception) {
//            emit(e.message)
        }
    }

    fun getAdvIrrigation(
        headerMap: Map<String, String>, account_id: Int, plot_id: Int
    ) = flow<Resource<AdvIrrigationModel?>> {

        try {
            val response = apiInterface.advIrrigation(headerMap, account_id, plot_id)

            if (response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun addFarm(
        accountId: Int,
        farmName: String,
        farm_center: String,
        farm_area: String,
        farm_json: String,
        plot_ids: String?,
        is_primary: Int?,
        farm_water_source: String?,
        farm_pump_hp: String?,
        farm_pump_type: String?,
        farm_pump_depth: String?,
        farm_pump_pipe_size: String?,
        farm_pump_flow_rate: String?
    ) = flow<Resource<ResponseBody?>> {

        try {
            val headerMap = LocalSource.getHeaderMapSanctum()
            val response = apiInterface.addFarm(
                headerMap,
                accountId,
                farmName,
                farm_center,
                farm_area,
                farm_json,
                plot_ids,
                is_primary,
                farm_water_source,
                farm_pump_hp,
                farm_pump_type,
                farm_pump_depth,
                farm_pump_pipe_size,
                farm_pump_flow_rate
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

    fun getMyFarms(accountId: Int,farm_id:Int?) = flow<Resource<MyFarmsDTO?>> {

        val map= LocalSource.getHeaderMapSanctum()?: emptyMap()
        val accountIdLocal=LocalSource.getUserDetailsEntity()?.accountId?:accountId

        try {
            val response = apiInterface.getMyFarms(map,accountIdLocal,farm_id)
            if (response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun getAppTranslations()= flow<Resource<AppTranlationsDTO?>> {
        try {
            val langCode=LocalSource.getLanguageCode()?:"en"
            val headerMap: Map<String, String> = LocalSource.getHeaderMapSanctum()?: emptyMap()

            val response = apiInterface.getTranslations(headerMap, lang = langCode)

            if (response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        }
    }

    fun updateHarvest(
        id:Int,harvest_date:String,actual_yield:Int)
            = flow<Resource<HarvestDateModel?>> {
        val map= LocalSource.getHeaderMapSanctum()?: emptyMap()
        emit(Resource.Loading())
        try {
            val response = apiInterface.harvestDate(map,id,harvest_date, actual_yield,"PUT")
            if(response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }

        } catch (e: Exception) {
            //   emit(Resource.Error(e.message))
        }
    }
    fun updateIrrigation(
        id:Int,irrigation:Int)
            = flow<Resource<IrrigationPerDay?>> {
        val map= LocalSource.getHeaderMapSanctum()?: emptyMap()
        emit(Resource.Loading())
        try {
            val response = apiInterface.irrigationPerDay(map,id,irrigation,"PUT")
            if(response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }

        } catch (e: Exception) {
            //   emit(Resource.Error(e.message))
        }
    }
    fun updateCropStage(
        id:Int,farmId:Int,plotId:Int,value1:String?,value2:String?,value3:String?,value4:String?,value5:String?,value6:String?,
        value7:String?,value8:String?,value9:String?,value10:String?,value11:String?,value12:String?,value13:String?,value14:String?,
        value15:String?)
            = flow<Resource<CropStageModel?>> {
        val map= LocalSource.getHeaderMapSanctum()?: emptyMap()
        emit(Resource.Loading())
        try {
            val response = apiInterface.updateCropStage(map,id,farmId,plotId,value1,value2,value3,value4,value5,value7,value7,value8,value9,
                value10,value11,value12,value13,value14,value15)
            if(response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }

        } catch (e: Exception) {
            //   emit(Resource.Error(e.message))
        }
    }
    fun getCropStage()
            = flow<Resource<GetCropStage?>> {
        val map= LocalSource.getHeaderMapSanctum()?: emptyMap()
        emit(Resource.Loading())
        try {
            val response = apiInterface.getCropStage(map)
            if(response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }

        } catch (e: Exception) {
            //   emit(Resource.Error(e.message))
        }
    }

    fun getNdvi(farmId: Int,accountId: Int)
            = flow<Resource<NdviModel?>> {
        val map= LocalSource.getHeaderMapSanctum()?: emptyMap()
        emit(Resource.Loading())
        try {
            val response = apiInterface.getNdvi(map,farmId,accountId)
            if(response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }

        } catch (e: Exception) {
            //   emit(Resource.Error(e.message))
        }
    }
    fun updateFarmSupport(name: String,contact:Long,
                          lat:Double,lon:Double, roleId:Int,
                          pincode:Int,village: String,address: String,
    state: String,district: String)
            = flow<Resource<FarmSupportModel?>> {
        val map= LocalSource.getHeaderMapSanctum()?: emptyMap()
        emit(Resource.Loading())
        try {
            val response = apiInterface.updateFarmSupport(map,name,contact,lat,lon,roleId,pincode,
           village,address,state,district)
            if(response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }

        } catch (e: Exception) {
            //   emit(Resource.Error(e.message))
        }
    }
    fun getFarmSupport(accountId: Int)
            = flow<Resource<GetFarmSupport?>> {
        val map= LocalSource.getHeaderMapSanctum()?: emptyMap()
        emit(Resource.Loading())
        try {
            val response = apiInterface.getFarmSupportUser(map,accountId)
            if(response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }

        } catch (e: Exception) {
            //   emit(Resource.Error(e.message))
        }
    }

    fun deleteFarmSupport(
        id:Int)
            = flow<Resource<DeleteFarmSupport?>> {
        val map= LocalSource.getHeaderMapSanctum()?: emptyMap()
        emit(Resource.Loading())
        try {
            val response = apiInterface.deleteFarmSupport(map,id)
            if(response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }

        } catch (e: Exception) {
            //   emit(Resource.Error(e.message))
        }
    }

    fun getNotification()
            = flow<Resource<NotificationModel?>> {
        val map= LocalSource.getHeaderMapSanctum()?: emptyMap()
        emit(Resource.Loading())
        try {
            val response = apiInterface.getNotification(map)
            if(response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }

        } catch (e: Exception) {
            //   emit(Resource.Error(e.message))
        }
    }
    fun updateNotification(id: String)
            = flow<Resource<UpdateNotification?>> {
        val map= LocalSource.getHeaderMapSanctum()?: emptyMap()
        emit(Resource.Loading())
        try {
            val response = apiInterface.updateNotification(map,id)
            if(response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
            }

        } catch (e: Exception) {
            //   emit(Resource.Error(e.message))
        }
    }
}

