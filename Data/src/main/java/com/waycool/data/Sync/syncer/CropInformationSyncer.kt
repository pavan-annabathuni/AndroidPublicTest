package com.waycool.data.Sync.syncer

import androidx.datastore.preferences.core.Preferences
import com.waycool.data.Local.Entity.CropInformationEntityData
import com.waycool.data.Local.Entity.PestDiseaseEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Local.mappers.CropInformationEntityMapper
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

class CropInformationSyncer : SyncInterface {

    override fun getSyncKey(): Preferences.Key<String> = SyncKey.CROP_INFORMATION_MASTER


    override fun getRefreshRate(): Int = SyncRate.getRefreshRate(getSyncKey())
    fun getCropInformation(crop_id: Int): Flow<Resource<List<CropInformationEntityData>>> {
        GlobalScope.launch(Dispatchers.IO) {
            if (isSyncRequired()) {
                makeNetworkCall()
            }
        }
        return getSelectedCropInformation(crop_id)
    }

    fun getSelectedCropInformation(crop_id: Int): Flow<Resource<List<CropInformationEntityData>>> {
        return LocalSource.getCropInformation(crop_id).map {
            Resource.Success(it)
        }
    }

    private fun makeNetworkCall() {

        GlobalScope.launch(Dispatchers.IO) {


            val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
            if (headerMap != null) {
                setSyncStatus(true)
                NetworkSource.getCropInformation(headerMap)
                    .collect {
                        when (it) {
                            is Resource.Success -> {
                                LocalSource.insertCropInformation(
                                    CropInformationEntityMapper().toEntityList(it.data?.data!!)
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

    fun downloadData() {
        GlobalScope.launch(Dispatchers.IO) {
            if (isSyncRequired()) {
                makeNetworkCall()
            }
        }
    }
}