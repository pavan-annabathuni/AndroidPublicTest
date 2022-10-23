package com.waycool.iwap

import android.app.Application
import com.facebook.stetho.Stetho
import com.waycool.data.Local.DataStorePref.DataStoreManager
import com.waycool.data.Sync.SyncManager
import com.waycool.data.Local.db.OutgrowDB

class OutgrowApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //        SendOTP.initializeApp(this, "339466As1e7SnWEJ5f3e52efP1", "1007161217962634901");
        Stetho.initializeWithDefaults(this)

        DataStoreManager.init(applicationContext)
        SyncManager.init(applicationContext)
        OutgrowDB.init(applicationContext)

    }
}