package com.waycool.data.Sync.syncer

import androidx.datastore.preferences.core.Preferences
import com.waycool.data.Local.Entity.TagsEntity
import com.waycool.data.Local.LocalSource
import com.waycool.data.Local.mappers.TagsEntityMapper
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

class TagsSyncer : SyncInterface {

    private val key = SyncKey.TAGS_MASTER

    fun getData(): Flow<Resource<List<TagsEntity>?>> {
        GlobalScope.launch(Dispatchers.IO) {
            if (isSyncRequired()) {
                makeNetworkCall()
            }
        }
        return getDataFromLocal()
    }

    private fun getDataFromLocal(): Flow<Resource<List<TagsEntity>?>> {
        return LocalSource.getTags().map {
            Resource.Success(it)
        }
    }

    private fun makeNetworkCall() {

        GlobalScope.launch(Dispatchers.IO) {
            val headerMap: Map<String, String>? = LocalSource.getHeaderMapSanctum()
            if (headerMap != null)
                NetworkSource.getTagsAndKeywords(headerMap)
                    .collect {
                        when (it) {
                            is Resource.Success -> {
                                LocalSource.insertTags(TagsEntityMapper().toEntityList(it.data?.data!!))
                                if (it.data.data!!.isNotEmpty())
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

    override fun getSyncKey(): Preferences.Key<String> {
        return key
    }


    override fun getRefreshRate(): Int = SyncRate.getRefreshRate(getSyncKey())


}