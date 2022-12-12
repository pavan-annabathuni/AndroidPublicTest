package com.waycool.data.repository

import android.text.Editable
import android.widget.EditText
import com.waycool.data.Local.Entity.PestDiseaseEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Local.mappers.MyCropEntityMapper
import com.waycool.data.Network.NetworkModels.AiCropDetectionData
import com.waycool.data.Network.NetworkModels.*
import com.waycool.data.Network.NetworkSource
import com.waycool.data.Sync.SyncManager.invalidateSync
import com.waycool.data.Sync.syncer.AiCropHistorySyncer
import com.waycool.data.repository.DomainMapper.*
import com.waycool.data.repository.domainModels.*
import com.waycool.data.Sync.syncer.CropCategorySyncer
import com.waycool.data.Sync.syncer.CropInformationSyncer
import com.waycool.data.Sync.syncer.CropMasterSyncer
import com.waycool.data.Sync.syncer.PestDiseaseSyncer
import com.waycool.data.Sync.syncer.*
import com.waycool.data.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call

object CropsRepository {

    fun getCropCategory(): Flow<Resource<List<CropCategoryMasterDomain>?>> {
        return CropCategorySyncer().getData().map {
            when (it) {
                is Resource.Success -> {
                    Resource.Success(
                        CropCategoryMasterDomainMapper().toDomainList(it.data ?: emptyList())
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

    fun getAddCropType(): Flow<Resource<List<AddCropTypeDomain>?>> {
        return AddCropTypeSyncer().getData().map {
            when (it) {
                is Resource.Success -> {
                    Resource.Success(
                        AddCropTypeDomainMapper().toDomainList(it.data ?: emptyList())
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


    fun getAllCrops(): Flow<Resource<List<CropMasterDomain>?>> {
        return CropMasterSyncer().getCropsMaster().map {
            when (it) {
                is Resource.Success -> {
                    Resource.Success(
                        CropMasterDomainMapper().toDomainList(it.data ?: emptyList())
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

    fun getPestDiseaseCrops(searchQuery: String? = ""): Flow<Resource<List<CropMasterDomain>?>> {
        return CropMasterSyncer().getCropsPestDiseases(searchQuery).map {
            when (it) {
                is Resource.Success -> {
                    Resource.Success(
                        CropMasterDomainMapper().toDomainList(it.data ?: emptyList())
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

    fun getCropInfoCrops(searchQuery: String? = ""): Flow<Resource<List<CropMasterDomain>?>> {
        return CropMasterSyncer().getCropsCropInfo(searchQuery).map {
            when (it) {
                is Resource.Success -> {
                    Resource.Success(
                        CropMasterDomainMapper().toDomainList(it.data ?: emptyList())
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

    fun getAiCrops(searchQuery: String? = ""): Flow<Resource<List<CropMasterDomain>?>> {
        return CropMasterSyncer().getCropsAiCropHealth(searchQuery).map {
            when (it) {
                is Resource.Success -> {
                    Resource.Success(
                        CropMasterDomainMapper().toDomainList(it.data ?: emptyList())
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

    fun getSoilTestHistory(account_id: Int): Flow<Resource<List<SoilTestHistoryDomain>?>> {
        return SoilTestHistorySyncer().getData(account_id).map {
            when (it) {
                is Resource.Success -> {
                    SoilTestHistorySyncer().invalidateSync()
                    Resource.Success(
                        SoilTestHistoryDomainMapper().toDomainList(it.data ?: emptyList())
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
    //check soil test lab


    fun getSoilTestLab(
        account: Int,
        lat: String,
        long: String
    ): Flow<Resource<List<CheckSoilTestDomain>?>> {
        return NetworkSource.getSoilTestLab(account, lat, long).map {
            when (it) {
                is Resource.Success -> {
                    SoilTestHistorySyncer().invalidateSync()
                    Resource.Success(
                        CheckSoilTestLabMapper().toDomainList(it.data?.data ?: emptyList())

//                        SoilTestHistoryDomainMapper().toDomainList(it.data?: emptyList())
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

//    fun getIotDevice(
//        account: Int,device_model_id:Int
//    ): Flow<Resource<List<ViewDeviceDTO>?>> {
//        return NetworkSource.getIotDevice().map {
//            when (it) {
//                is Resource.Success -> {
////                    SoilTestHistorySyncer().invalidateSync()
//                    Resource.Success(
//
//
////                        CheckSoilTestLabMapper().toDomainList(it.data?.data ?: emptyList())
//
////                        SoilTestHistoryDomainMapper().toDomainList(it.data?: emptyList())
//                    )
//                }
//                is Resource.Loading -> {
//                    Resource.Loading()
//                }
//                is Resource.Error -> {
//                    Resource.Error(it.message)
//                }
//            }
//        }
//    }
    fun getIotDevice(): Flow<Resource<ViewDeviceDTO?>> {
        GlobalScope.launch {
            MyCropSyncer().invalidateSync()
        }
        return NetworkSource.getIotDevice()
    }
    fun getGraphsViewDevice(serial_no_id:Int?,device_model_id:Int?,value:String?): Flow<Resource<GraphsViewDataDTO?>> {
        GlobalScope.launch {
            MyCropSyncer().invalidateSync()
        }
        return NetworkSource.getGraphsViewDevice(serial_no_id,device_model_id,value)
    }
    fun getDashBoard(): Flow<Resource<DashBoardModel?>> {
        GlobalScope.launch {
            MyCropSyncer().invalidateSync()
        }
        return NetworkSource.dashBoard()
    }
    fun getFarmDetails(): Flow<Resource<FarmDetailsDTO?>> {
        GlobalScope.launch {
            MyCropSyncer().invalidateSync()
        }
        return NetworkSource.farmDetails()
    }



    fun getTracker(soil_test_request_id: Int): Flow<Resource<List<TrackerDemain>?>> {
        return NetworkSource.getTracker(soil_test_request_id).map {
            when (it) {
                is Resource.Success -> {
                    Resource.Success(
                        TrackerDomainMapper().toDomainList(it.data?.data ?: emptyList())
//                        SoilTestHistoryDomainMapper().toDomainList(it.data?: emptyList())
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
    fun pdfDownload(soil_test_request_id: Int): Flow<Resource<ResponseBody?>> {
        return NetworkSource.pdfDownload(soil_test_request_id)
    }

    //check soil test lab

//    fun getTracker(lat:Double,long:Double): Flow<Resource<List<CheckSoilTestDomain>?>> {
//        return NetworkSource.getSoilTestLab(1,lat,long).map {
//            when (it) {
//                is Resource.Success -> {
//                    Resource.Success(
//                        CheckSoilTestLabMapper().toDomainList(it.data?.data?: emptyList())
////                        SoilTestHistoryDomainMapper().toDomainList(it.data?: emptyList())
//                    )
//                }
//                is Resource.Loading -> {
//                    Resource.Loading()
//                }
//                is Resource.Error -> {
//                    Resource.Error(it.message)
//                }
//            }
//        }
//    }


    fun getPestAndDiseasesForCrop(cropId: Int): Flow<Resource<List<PestDiseaseDomain>>> {
        return PestDiseaseSyncer().getDiseasesForCrop(cropId).map {
            when (it) {
                is Resource.Success -> {
                    Resource.Success(
                        PestDiseaseDomainMapper().toDomainList(it.data ?: emptyList())
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


    fun getPestDiseaseForSelectedDisease(diseaseId: Int): Flow<Resource<PestDiseaseDomain>> {
        return PestDiseaseSyncer().getSelectedDisease(diseaseId).map {
            when (it) {
                is Resource.Success -> {
                    Resource.Success(
                        PestDiseaseDomainMapper().mapToDomain(it.data ?: PestDiseaseEntity())
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

//    fun addCropPassData(
//        crop_id: Int?,
//        account_id: Int?,
//        plot_nickname: String?,
//        is_active: Int?,
//        sowing_date: String?,
//        area: Editable?
//    ): Flow<Resource<AddCropResponseDTO?>> {
//        return NetworkSource.addCropPassData(
//            crop_id,
//            account_id,
//            plot_nickname,
//            is_active,
//            sowing_date,
//            area
//        )
//    }
    fun addCropDataPass(
        map: MutableMap<String, Any> = mutableMapOf<String,Any>()
    ): Flow<Resource<AddCropResponseDTO?>> {
    GlobalScope.launch {
        MyCropSyncer().invalidateSync()
    }
        return NetworkSource.addCropDataPass(map)
    }
    fun activateDevice(
        map: MutableMap<String, Any> = mutableMapOf<String,Any>()
    ): Flow<Resource<ActivateDeviceDTO?>> {
        GlobalScope.launch {
            MyCropSyncer().invalidateSync()
        }
        return NetworkSource.activateDevice(map)
    }
    fun viewReport(id:Int): Flow<Resource<SoilTestReportMaster?>> {
        GlobalScope.launch {
            MyCropSyncer().invalidateSync()
        }
        return NetworkSource.viewReport(id)
    }

    fun postNewSoil(
        account_id: Int,
        lat: Double,
        long: Double,
        org_id: Int,
        plot_no: String,
        pincode: String,
        address: String,
        state: String,
        district: String,
        number: String
    ): Flow<Resource<SoilTestResponseDTO?>> {

        return NetworkSource.postNewSoil(
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
    }
    fun checkToken(
        user_id: Int,
        token: String
    ): Flow<Resource<CheckTokenResponseDTO?>> {

        return NetworkSource.checkToken(
          user_id,token
        )
    }


    fun postAiCropImage(
        cropId: Int,
        cropName: String,
        image: MultipartBody.Part
    ): Flow<Resource<AiCropDetectionDomain>> {


        return NetworkSource.detectAiCrop(
            cropId, cropName, image
        )
            .map {
                when (it) {
                    is Resource.Success -> {
                        Resource.Success(
                            AiCropDetectionDomainMapper().mapToDomain(
                                it.data?: AiCropDetectionDTO()
                            )
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


    fun getAiCropHistory(): Flow<Resource<List<AiCropHistoryDomain>>> {
        return AiCropHistorySyncer().getData().map {
            when (it) {
                is Resource.Success -> {
                    AiCropHistorySyncer().invalidateSync()
                    Resource.Success(
                        AiCropHistoryDomainMapper().toDomainList(it.data ?: emptyList())
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

    fun getCropInformation(crop_id: Int): Flow<Resource<List<CropInformationDomainData>>> {
        return CropInformationSyncer().getCropInformation(crop_id).map {
            when (it) {
                is Resource.Success -> {
                    Resource.Success(
                        CropInformationDomainMapper().toDomainList(it.data ?: emptyList())
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

    fun getUpdateProfile(crop_id: Int): Flow<Resource<List<CropInformationDomainData>>> {
        return CropInformationSyncer().getCropInformation(crop_id).map {
            when (it) {
                is Resource.Success -> {
                    Resource.Success(
                        CropInformationDomainMapper().toDomainList(it.data ?: emptyList())
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

    suspend fun getState(): Flow<Resource<StateModel?>> {
        val map=LocalSource.getHeaderMapSanctum()?: emptyMap()
        return NetworkSource.getStateList(map)
    }


    fun getMyCrop2(crop_id: Int): Flow<Resource<List<MyCropDataDomain>>> {
        return MyCropSyncer().getMyCrop(crop_id).map {
            when (it) {
                is Resource.Success -> {
                    Resource.Success(
                        MyCropDomainMapper().toDomainList(it.data ?: emptyList())
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
     fun getEditCrop(id:Int):Flow<Resource<Unit?>> {


         GlobalScope.launch(Dispatchers.IO){
             LocalSource.deleteMyCrop()
             MyCropSyncer().invalidateSync()
         }

        return NetworkSource.editMyCrop(id)

    }
}