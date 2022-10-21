package com.waycool.data.repository

import com.waycool.data.Local.Entity.PestDiseaseEntity
import com.waycool.data.Local.Entity.UserDetailsEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.NetworkModels.AiCropDetectionData
import com.waycool.data.Network.NetworkSource
import com.waycool.data.Sync.syncer.AiCropHistorySyncer
import com.waycool.data.Sync.syncer.CropCategorySyncer
import com.waycool.data.Sync.syncer.CropMasterSyncer
import com.waycool.data.Sync.syncer.PestDiseaseSyncer
import com.waycool.data.repository.DomainMapper.*
import com.waycool.data.repository.domainModels.*
import com.waycool.data.utils.Resource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

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

    fun getCropInfoCrops(): Flow<Resource<List<CropMasterDomain>?>> {
        return CropMasterSyncer().getCropsCropInfo().map {
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

     fun postAiCropImage(
        cropId: Int,
        cropName: String,
        image: MultipartBody.Part
    ): Flow<Resource<AiCropDetectionDomain>> {



            return NetworkSource.detectAiCrop(
                 cropId, cropName, image)
                .map {
                    when (it) {
                        is Resource.Success -> {
                            Resource.Success(
                                AiCropDetectionDomainMapper().mapToDomain(
                                    it.data?.data ?: AiCropDetectionData()
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


    fun getAiCropHistory():Flow<Resource<List<AiCropHistoryDomain>>>{
        return AiCropHistorySyncer().getData().map {
            when (it) {
                is Resource.Success -> {
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
}