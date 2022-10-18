package com.waycool.data.Repository

import com.waycool.data.Local.Entity.PestDiseaseEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.NetworkModels.AiCropDetectionData
import com.waycool.data.Network.NetworkSource
import com.waycool.data.Repository.DomainMapper.*
import com.waycool.data.Repository.DomainModels.*
import com.waycool.data.Sync.syncer.AddCropTypeSyncer
import com.waycool.data.Sync.syncer.CropCategorySyncer
import com.waycool.data.Sync.syncer.CropMasterSyncer
import com.waycool.data.Sync.syncer.PestDiseaseSyncer
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    fun getAiCrops(): Flow<Resource<List<CropMasterDomain>?>> {
        return CropMasterSyncer().getCropsAiCropHealth().map {
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

    suspend fun postAiCropImage(
        userId: Int,
        cropId: Int,
        cropName: String,
        image: MultipartBody.Part
    ): Flow<Resource<AiCropDetectionDomain>> {
        val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
        if (headerMap != null) {
            return NetworkSource.detectAiCrop(headerMap, userId, cropId, cropName, image)
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
        } else {
            throw RuntimeException("Header Map is null")
        }
    }

}