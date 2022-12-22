package com.example.soiltesting.ui.request

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.waycool.data.Network.NetworkModels.ActivateDeviceDTO
import com.waycool.data.Network.NetworkModels.SoilTestReportMaster
import com.waycool.data.Network.NetworkModels.SoilTestResponseDTO
import com.waycool.data.repository.CropsRepository
import com.waycool.data.repository.GeocodeRepository
import com.waycool.data.repository.LoginRepository
import com.waycool.data.repository.ProfileRepository
import com.waycool.data.repository.domainModels.*
import com.waycool.data.utils.Resource
import okhttp3.ResponseBody

class SoilTestRequestViewModel : ViewModel() {
    fun getCropMaster(searchQuery: String? = ""): LiveData<Resource<List<CropMasterDomain>?>> {
        return CropsRepository.getAiCrops(searchQuery).asLiveData()
    }

    fun getCropCategory(): LiveData<Resource<List<CropCategoryMasterDomain>?>> {
        return CropsRepository.getCropCategory().asLiveData()
    }

    fun getMyCrop2(account_id: Int): LiveData<Resource<List<MyCropDataDomain>>> =
        CropsRepository.getMyCrop2(account_id).asLiveData()

    fun postNewSoil(
        account_id: Int,
        lat: Double,
        long: Double,
        org_id: Int,
        plot_no: String,
        pincode: String,
        address: String,
        state: String,
        district: String,
        number: String,
        plot_id:Int
    ): LiveData<Resource<SoilTestResponseDTO?>> =
        CropsRepository.postNewSoil(
            account_id,
            lat,
            long,
            org_id,
            plot_no,
            pincode,
            address,
            state,
            district,
            number,
            plot_id
        ).asLiveData()

    //    suspend fun getUserProfileDetails():LiveData<Resource<UserDetailsDTO?>> =
//        ProfileRepository.getUserProfileDet().asLiveData()
    fun getUserDetails(): LiveData<Resource<UserDetailsDomain>> =
        LoginRepository.getUserDetails().asLiveData()


    fun getReverseGeocode(latlon: String): LiveData<GeocodeDomain> =
        GeocodeRepository.getReverseGeocode(latlon).asLiveData()

    //view report
    fun viewReport(id: Int): LiveData<Resource<SoilTestReportMaster?>> =
        CropsRepository.viewReport(id).asLiveData()

    fun pdfDownload(id: Int): LiveData<Resource<ResponseBody?>> =
        CropsRepository.pdfDownload(id).asLiveData()

}


//}