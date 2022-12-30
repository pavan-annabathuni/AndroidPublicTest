package com.waycool.iwap

import android.app.Application
import com.facebook.stetho.Stetho
import com.google.firebase.FirebaseApp
import com.waycool.data.Local.DataStorePref.DataStoreManager
import com.waycool.data.Sync.SyncManager
import com.waycool.data.Local.db.OutgrowDB
import com.waycool.data.Sync.syncer.DashboardSyncer
import com.waycool.data.Sync.syncer.UserDetailsSyncer
import com.waycool.data.translations.TranslationsManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class OutgrowApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //        SendOTP.initializeApp(this, "339466As1e7SnWEJ5f3e52efP1", "1007161217962634901");
        Stetho.initializeWithDefaults(this)

        DataStoreManager.init(applicationContext)
        SyncManager.init(applicationContext)
        OutgrowDB.init(applicationContext)
        TranslationsManager().init()
        FirebaseApp.initializeApp(this)

        //Refresh UserDetails If Required
        GlobalScope.launch {
            UserDetailsSyncer().invalidateSync()
            UserDetailsSyncer().getData()
            DashboardSyncer().invalidateSync()
            DashboardSyncer().getData()
        }
    }
}