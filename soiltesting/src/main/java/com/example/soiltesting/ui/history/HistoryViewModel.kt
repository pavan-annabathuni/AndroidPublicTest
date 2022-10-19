package com.example.soiltesting.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.soiltesting.apiclient.ApiService
import com.example.soiltesting.network.RetrofitBuilder
import com.example.soiltesting.repository.SoilTestingRepository
import kotlinx.coroutines.launch

class HistoryViewModel constructor(application: Application):AndroidViewModel(application) {
    private val apiService:ApiService =
        RetrofitBuilder.getInstance().create(ApiService::class.java)
    private val repository = SoilTestingRepository(apiService)
    val historyLiveData get() = repository.historyLiveData
    val statusLiveData get() = repository.statusLiveData
    fun getAllHistory(userAccount: Int) {
        viewModelScope.launch {
            repository.getAllHistory(userAccount)
        }
    }
}