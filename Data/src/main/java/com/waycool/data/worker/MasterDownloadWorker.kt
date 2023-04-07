package com.waycool.data.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.waycool.data.Sync.syncer.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MasterDownloadWorker(val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {

            CoroutineScope(Dispatchers.IO).launch {

                //My Crops Master
                MyCropSyncer().invalidateSync()
                MyCropSyncer().getMyCrop()

                //CropMaster
                CropMasterSyncer.invalidateSync()
                CropMasterSyncer.getCropsMaster()

                //Crop Category Master
                CropCategorySyncer.invalidateSync()
                CropCategorySyncer.getData()

                //CropInformation Master
                CropInformationSyncer().invalidateSync()
                CropInformationSyncer().downloadData()

                //CropProtection Master
//                PestDiseaseSyncer().invalidateSync()
//                PestDiseaseSyncer().downloadData()

                //Module Master
                ModuleMasterSyncer().invalidateSync()
                ModuleMasterSyncer().getData()

                //VansCategory Master
//                VansCategorySyncer().invalidateSync()
//                VansCategorySyncer().getData()

            }


        return Result.success()
    }
}