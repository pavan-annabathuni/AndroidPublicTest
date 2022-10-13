package com.waycool.featurecrophealth.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.waycool.data.Local.Entity.UserDetailsEntity
import com.waycool.data.Local.LocalSource
import com.waycool.featurecrophealth.api.RetrofitService
import com.waycool.featurecrophealth.network.NetworkBuilder
import com.waycool.featurecrophealth.repository.HistoryRepo
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class HistoryViewModel constructor(application: Application) : AndroidViewModel(application) {
    private val apiClient: RetrofitService =
        NetworkBuilder.getInstance().create(RetrofitService::class.java)
    private val repository = HistoryRepo(apiClient)
    val historyLiveData get() = repository.historyLiveData
    val statusLiveData get() = repository.statusLiveData

    val aiResponse get()=repository.postLiveData


    fun getAllHistory(userId: Int,context: Context) {
        viewModelScope.launch {
            repository.getAllHistory(userId,context)
        }
    }


    fun createNote(context: Context, crop_id: Int, crop_name: String, image: MultipartBody.Part) {
        viewModelScope.launch {
            val userDetailsEntity=LocalSource.getUserDetailsEntity()?: UserDetailsEntity()
            userDetailsEntity.id?.let {
                repository.aiPassData(context, it,crop_id,crop_name,image) }
        }
    }
}