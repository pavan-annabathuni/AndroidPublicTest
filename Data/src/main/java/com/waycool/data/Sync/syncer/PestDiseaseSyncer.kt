package com.waycool.data.Sync.syncer

import androidx.datastore.preferences.core.Preferences
import com.waycool.data.Local.Entity.PestDiseaseEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Local.mappers.PestDiseaseEntityMapper
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

class PestDiseaseSyncer : SyncInterface {
    override fun getSyncKey(): Preferences.Key<String> = SyncKey.PEST_DISEASE_MASTER

    override fun getRefreshRate(): Int = SyncRate.getRefreshRate(getSyncKey())

    fun getDiseasesForCrop(cropId: Int): Flow<Resource<List<PestDiseaseEntity>?>> {
        GlobalScope.launch(Dispatchers.IO) {
            if (isSyncRequired()) {
                makeNetworkCall()
            }
        }
        return getDiseasesForCropLocal(cropId)
    }

    fun getSelectedDisease(diseaseId: Int): Flow<Resource<PestDiseaseEntity>> {
        GlobalScope.launch(Dispatchers.IO) {
            if (isSyncRequired()) {
                makeNetworkCall()
            }
        }
        return getSelectedDiseaseLocal(diseaseId)
    }

    private fun getSelectedDiseaseLocal(diseaseId: Int): Flow<Resource<PestDiseaseEntity>> {
        return LocalSource.getSelectedDiseasesForCrop(diseaseId).map {
            if (it != null) {
                Resource.Success(it)
            } else {
                Resource.Error("")
            }
        }
    }


    private fun getDiseasesForCropLocal(cropId: Int): Flow<Resource<List<PestDiseaseEntity>?>> {
        return LocalSource.getDiseasesForCrop(cropId).map {
            if (it != null) {
                Resource.Success(it)
            } else {
                Resource.Error("")
            }
        }
    }


    private fun makeNetworkCall() {

        GlobalScope.launch(Dispatchers.IO) {
            setSyncStatus(true)

            val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
            if (headerMap != null)
                NetworkSource.getPestDisease()
                    .collect {
                        when (it) {
                            is Resource.Success -> {
                                LocalSource.insertPestDiseases(
                                    PestDiseaseEntityMapper().toEntityList(it.data?.data!!)
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

    fun downloadData() {
        GlobalScope.launch(Dispatchers.IO) {
            if (isSyncRequired()) {
                makeNetworkCall()
            }
        }
    }

}