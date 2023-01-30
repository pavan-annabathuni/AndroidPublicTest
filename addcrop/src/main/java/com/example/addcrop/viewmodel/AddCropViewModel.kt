package com.example.addcrop.viewmodel

import android.util.Log
import android.view.View
import androidx.databinding.Bindable
import androidx.lifecycle.*

import com.waycool.data.Network.NetworkModels.AddCropResponseDTO
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.FarmsRepository
import com.waycool.data.repository.LoginRepository
import com.waycool.data.repository.domainModels.SoilTypeDomain
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.repository.domainModels.UserDetailsDomain

import com.waycool.data.utils.Resource

class AddCropViewModel : ViewModel() {
    private val TAG = "AddCropViewModel"
    /*
  MutableLiveData is a class in the Android architecture components library that is used to store and manage UI-related data in a lifecycle-aware fashion.
   It can be observed and updated by multiple observers, including the Android UI components.
   When the data changes, the observers are notified and can update their views accordingly.
   This helps to ensure that the UI stays in sync with the underlying data, even if the data changes dynamically.
     */

    private val _navigation = MutableLiveData<String>()
    val navigation: LiveData<String> get() = _navigation

    companion object {
        const val SUBMIT_BUTTON = "SUBMIT_BUTTON"
        const val CALENDER_SHOW = "CALENDER_SHOW"
        const val BACK_BUTTON = "BACK_BUTTON"
        const val CHECK_INTERNET = "CHECK_INTERNET"
    }

    val eventHandler = EventHandler(this)

//    private val _saveButtonPassData = MutableLiveData<String>()
//    val saveButtonPassData: LiveData<String> get() = _saveButtonPassData
//
//    private val _calenderShow = MutableLiveData<String>()
//    val calenderShow: LiveData<String> get() = _calenderShow
//
//   private val _navigateBack = MutableLiveData<Boolean>()
//   val navigateBack : LiveData<Boolean> get()=_navigateBack


//    fun saveButtonClicked() {
//        _saveButtonPassData.value = SUBMIT_BUTTON
//    }
//    fun calenderShow() {
//        _calenderShow.value = CALENDER_SHOW
//    }
//    fun backButton() {
//        _navigateBack.value =false
//    }


    fun getAddCropType(): LiveData<Resource<List<SoilTypeDomain>?>> {
        return CropsRepository.getSoilType().asLiveData()
    }

    fun addCropDataPass(map: MutableMap<String, Any> = mutableMapOf<String, Any>()): LiveData<Resource<AddCropResponseDTO?>> =
        CropsRepository.addCropDataPass(map).asLiveData()

    fun getUserDetails(): LiveData<Resource<UserDetailsDomain>> =
        LoginRepository.getUserDetails().asLiveData()

    fun getMyCrop2(): LiveData<Resource<List<MyCropDataDomain>>> =
        CropsRepository.getMyCrop2().asLiveData()

    fun getEditMyCrop(id: Int): LiveData<Resource<Unit?>> {
        return CropsRepository.getEditCrop(id).asLiveData()
    }

    fun getMyFarms(): LiveData<Resource<List<MyFarmsDomain>>> =
        FarmsRepository.getMyFarms().asLiveData()


    fun checkInputFields(nickName: String, area: String, date: String): Boolean {
        if (nickName.isEmpty()) {
            return false
        } else if (area.isEmpty()) {
            return false
        } else if (date.isEmpty()) {
            return false
        }
        return true
    }

    class EventHandler(private val viewModel: AddCropViewModel) {
        /*
         value property of a LiveData object to set its initial value.
         */

        fun saveButtonClicked(view: View?) {
            viewModel._navigation.value = SUBMIT_BUTTON
        }

        fun calenderShow(view: View?) {
            Log.d(viewModel.TAG, "CalenderClicked:")
            viewModel._navigation.value = CALENDER_SHOW
        }

        fun backButton(view: View?) {
            Log.i(viewModel.TAG, "backButtonClicked:")
            viewModel._navigation.value = BACK_BUTTON
        }

        fun checkInternet(view: View?) {
            Log.i(viewModel.TAG, "Internet:")
            viewModel._navigation.value = CHECK_INTERNET
        }


    }

}