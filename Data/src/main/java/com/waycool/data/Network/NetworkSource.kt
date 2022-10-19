package com.waycool.data.Network

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.waycool.core.retrofit.OTPApiCient
import com.waycool.core.retrofit.OutgrowClient
import com.waycool.core.utils.AppSecrets
import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.ApiInterface.ApiInterface
import com.waycool.data.Network.ApiInterface.OTPApiInterface
import com.waycool.data.Network.NetworkModels.*
import com.waycool.data.Network.PagingSource.VansPagingSource
import com.waycool.data.Repository.DomainModels.AddCropRequestDomain
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.awaitResponse
import kotlin.Exception

object NetworkSource {

    private val apiInterface: ApiInterface
    private val headerMapPublic: Map<String, String>
    private val otpInterface: OTPApiInterface


    init {
        val internalRetrofit: Retrofit = OutgrowClient.retrofit
        apiInterface = internalRetrofit.create(ApiInterface::class.java)
        headerMapPublic = AppSecrets.getHeaderPublic()
        val otpRetrofit: Retrofit = OTPApiCient.apiClient
        otpInterface = otpRetrofit.create(OTPApiInterface::class.java)

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
    fun addCropPassData(addCropRequest: AddCropRequestDomain) = flow<Resource<AddCropResponseDTO?>> {
        emit(Resource.Loading())
        try {
            val response = apiInterface.addCropPassData(headerMapPublic,addCropRequest)
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


    fun getCropCategoryMaster(headerMap: Map<String, String>) = flow<Resource<CropCategoryMasterDTO?>> {

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
        emit(Resource.Loading())
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
            emit(Resource.Loading())
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
        headerMap: Map<String, String>,
        userId: Int,
        cropId: Int,
        cropName: String,
        image: MultipartBody.Part
    ) = flow<Resource<AiCropDetectionDTO?>> {

        emit(Resource.Loading())
        try {
            val response = apiInterface.postAiCrop(headerMap, userId, cropId, cropName, image)

            if (response.isSuccessful)
                emit(Resource.Success(response.body()))
            else {
                emit(Resource.Error(response.errorBody()?.charStream()?.readText()))
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
}

