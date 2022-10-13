package com.wayone.farmerregistration.registrationViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.waycool.data.Network.NetworkModels.ModuleMasterDTO;
import com.waycool.data.Network.NetworkModels.RegisterDTO;

import java.util.HashMap;

public class RegistrationViewModel extends AndroidViewModel {
    private final RegistrationRespository registrationRespository;
    public RegistrationViewModel(@NonNull Application application) {
        super(application);
        registrationRespository= new RegistrationRespository();
    }
    public LiveData<ModuleMasterDTO> getModuleMaster(){
        return registrationRespository.moduleMaster();
    }
   /* public LiveData<RegisterMaster> getUserData(String name, String mobile_no, String lat, String lon, String languageId, String email, String pincode, String village, String address, String state, String district, String subDistrict){
        Log.d("loginrespository", "Responseview: " );
        return registrationRespository.userCreater(name,mobile_no,lat,lon,languageId,email,pincode,village,address,state,district,subDistrict);
    }*/
   public LiveData<RegisterDTO> getUserData(HashMap<String,String> query){
       Log.d("loginrespository", "Responseview: " );
       return registrationRespository.userCreater(query);
   }
}
