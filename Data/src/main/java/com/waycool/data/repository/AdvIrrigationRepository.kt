package com.waycool.data.repository

import androidx.lifecycle.asLiveData
import com.waycool.data.Local.LocalSource
import com.waycool.data.Network.NetworkModels.*
import com.waycool.data.Network.NetworkSource
import com.waycool.data.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import kotlin.collections.ArrayList

object AdvIrrigationRepository {
    fun getAdvIrrigation(account_id: Int, plot_id: Int): Flow<Resource<AdvIrrigationModel?>> {
        return NetworkSource.getAdvIrrigation(account_id, plot_id)
    }

    fun updateHarvest(plot_id: Int, account_id: Int, cropId: Int, harvest_date: String, actual_yield: Int): Flow<Resource<HarvestDateModel?>> {
        return NetworkSource.updateHarvest(plot_id, account_id, cropId, harvest_date, actual_yield)
    }

    fun updateIrrigation(irrigation_id: Int, irrigation: Int): Flow<Resource<IrrigationPerDay?>> {
        return NetworkSource.updateIrrigation(irrigation_id, irrigation)
    }

    //    fun updateCropStage(
//        id:Int,farmId:Int,plotId:Int,value1:String?,value2:String?,value3:String?,value4:String?,value5:String?,value6:String?,
//        value7:String?,value8:String?,value9:String?,value10:String?,value11:String?,value12:String?,value13:String?,value14:String?,
//        value15:String?):Flow<Resource<CropStageModel?>>{
//        return  NetworkSource.updateCropStage(id,farmId,plotId,value1,value2,value3,value4,value5,value7,value7,value8,value9,
//            value10,value11,value12,value13,value14,value15)
//    }
    fun getCropStage(account_id: Int, plot_id: Int): Flow<Resource<CropStageModel?>> {
        return NetworkSource.getCropStage(account_id, plot_id)
    }

    fun updateCropStage(account_id: Int, cropStageId: Int, plot_id: Int, date: String): Flow<Resource<UpdateCropStage?>> {
        return NetworkSource.updateCropStage(account_id, cropStageId, plot_id, date)
    }

    fun getDisease(account_id: Int, plot_id: Int): Flow<Resource<PestAndDiseaseModel?>> {
        return NetworkSource.getDisease(account_id, plot_id).map {
            val currentData = it?.data?.data?.currentData
                ?.map { it1 ->
                    it1.disease?.diseaseNameTranslated = it1.diseaseId?.let { it1 ->
                        LocalSource.getSelectedDiseaseEntity(it1)?.diseaseName ?: "--"
                    }
                    it1
                }

            val histoyData = it?.data?.data?.historicData
                ?.map { it1 ->
                    it1.disease?.diseaseNameTranslated = it1.diseaseId?.let { it1 ->
                        LocalSource.getSelectedDiseaseEntity(it1)?.diseaseName ?: "--"
                    }
                    it1
                }
            it.data?.data?.currentData= currentData as ArrayList<DiseaseCurrentData>
            it.data?.data?.historicData= histoyData as ArrayList<DiseaseHistoricData>

            it
        }
    }
}