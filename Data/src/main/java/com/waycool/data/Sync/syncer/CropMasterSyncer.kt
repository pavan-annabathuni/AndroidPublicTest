package com.waycool.data.Sync.syncer

import androidx.datastore.preferences.core.Preferences
import com.waycool.data.Local.Entity.CropMasterEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Local.mappers.CropMasterEntityMapper
import com.waycool.data.Network.NetworkSource
import com.waycool.data.Sync.SyncInterface
import com.waycool.data.Sync.SyncKey
import com.waycool.data.Sync.SyncRate
import com.waycool.data.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

object CropMasterSyncer : SyncInterface {


    override fun getSyncKey(): Preferences.Key<String> = SyncKey.CROPS_MASTER

    override fun getRefreshRate(): Int = SyncRate.getRefreshRate(getSyncKey())

    fun getCropsMaster(searchQuery: String? = ""): Flow<Resource<List<CropMasterEntity>>> {

        GlobalScope.launch(Dispatchers.IO) {
            if (isSyncRequired()) {
                makeNetworkCall()
            }
        }
        return getCropMasterFromLocal(searchQuery)
    }

    fun getCropsPestDiseases(searchQuery: String? = ""): Flow<Resource<List<CropMasterEntity>>> {
        GlobalScope.launch(Dispatchers.IO) {
            if (isSyncRequired()) {
                makeNetworkCall()
            }
        }
        return getCropsPestDiseasesFromLocal(searchQuery)
    }

    fun getCropsAiCropHealth(searchQuery: String? = ""): Flow<Resource<List<CropMasterEntity>>> {
        GlobalScope.launch(Dispatchers.IO) {
            if (isSyncRequired()) {
                makeNetworkCall()
            }
        }
        return getCropsAiCropHealthFromLocal(searchQuery)
    }

    fun getCropsCropInfo(searchQuery: String? = ""): Flow<Resource<List<CropMasterEntity>>> {
        GlobalScope.launch(Dispatchers.IO) {
            if (isSyncRequired()) {
                makeNetworkCall()
            }
        }
        return getCropsInfoFromLocal(searchQuery)
    }

    private fun getCropsInfoFromLocal(searchQuery: String? = ""): Flow<Resource<List<CropMasterEntity>>> {

        return LocalSource.getCropsInfo(searchQuery).map {
            if (it != null) {
                (Resource.Success(it))
            } else {
                (Resource.Error(""))
            }
        }
    }

    fun getIrrigationCrops(searchQuery: String? = ""): Flow<Resource<List<CropMasterEntity>>> {
        GlobalScope.launch(Dispatchers.IO) {
            if (isSyncRequired()) {
                makeNetworkCall()
            }
        }
        return getIrrigationCropsFromLocal(searchQuery)
    }

    private fun getIrrigationCropsFromLocal(searchQuery: String? = ""): Flow<Resource<List<CropMasterEntity>>> {

        return LocalSource.getIrrigationCrops(searchQuery).map {
            if (it != null) {
                (Resource.Success(it))
            } else {
                (Resource.Error(""))
            }
        }
    }

    private fun getCropsAiCropHealthFromLocal(searchQuery: String? = ""): Flow<Resource<List<CropMasterEntity>>> {

        return LocalSource.getCropsAiCropHealth(searchQuery).map {
            if (it != null) {
                (Resource.Success(it))
            } else {
                (Resource.Error(""))
            }
        }
    }


    private fun getCropsPestDiseasesFromLocal(searchQuery: String? = ""): Flow<Resource<List<CropMasterEntity>>> {
        return LocalSource.getCropsPestDiseases(searchQuery).map {
            if (it != null) {
                Resource.Success(it)
            } else {
                Resource.Error("")
            }
        }
    }

    private fun getCropMasterFromLocal(searchQuery: String? = ""): Flow<Resource<List<CropMasterEntity>>> {
        return LocalSource.getCropMaster(searchQuery).map {
            if (it != null) {
                Resource.Success(it)
            } else {
                (Resource.Error(""))
            }
        }
    }

    private fun makeNetworkCall() {
        GlobalScope.launch(Dispatchers.IO) {
            val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
            if (headerMap != null) {
                setSyncStatus(true)
                NetworkSource.getCropMaster(headerMap)
                    .collect {
                        when (it) {
                            is Resource.Success -> {
                                LocalSource.insertCropMaster(
                                    CropMasterEntityMapper().toEntityList(
                                        it.data?.data!!
                                    )
                                )
                                if (it.data.data.isNotEmpty())
                                    setSyncStatus(true)
                                else setSyncStatus(false)
                            }

                            is Resource.Loading -> {

                            }
                            is Resource.Error -> {
                                setSyncStatus(false)
                            }
                        }
                    }
            }
        }
    }
}