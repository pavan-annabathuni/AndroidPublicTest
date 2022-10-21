package com.example.soiltesting.ui.checksoil

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.soiltesting.apiclient.ApiService
import com.example.soiltesting.model.feedback.FeedbackRequest
import com.example.soiltesting.model.postsoil.NewSoilTestPost
import com.example.soiltesting.network.RetrofitBuilder
import com.example.soiltesting.repository.CheckSoilTestingRepository
import com.example.soiltesting.repository.SoilTestingRepository
import kotlinx.coroutines.launch

class CheckSoilRTestViewModel constructor(application: Application) :
    AndroidViewModel(application) {
    private val apiService: ApiService =
        RetrofitBuilder.getInstance().create(ApiService::class.java)

    private val repository = CheckSoilTestingRepository(apiService)

    val checkSoilTestLiveData get() = repository.checkSoilTestLiveData
    val statusLiveData get() = repository.statusLiveData

    val newSoilTestLiveData get() = repository.newSoilTestLiveData

    fun getSoilTest(userAccount: Int, lat: Double, long: Double) {
        viewModelScope.launch {
            repository.getSoilTest(userAccount, lat, long)
        }
    }

    fun postNewSoil(newSoilTestPost: NewSoilTestPost) {
        viewModelScope.launch {
            repository.postNewSoil(newSoilTestPost)
        }

    }
    fun postFeedback(feedbackRequest: FeedbackRequest) {
        viewModelScope.launch {
            repository.postFeedback(feedbackRequest)
        }

    }

}