package com.waycool.featurecrophealth.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.waycool.featurecrophealth.model.cropdetails.Data

@Database(entities = [Data::class], version = 2)
abstract class DetailsDatabase :RoomDatabase() {
 abstract fun cropDetailsDao(): CropDetailsDao


 companion object{
  @Volatile
  private var INSTANCE: DetailsDatabase? = null

  fun getDatabase(context: Context): DetailsDatabase {
   if (INSTANCE == null) {
    synchronized(this){
     INSTANCE = Room.databaseBuilder(context,
      DetailsDatabase::class.java,
      "quoteDB")
      .build()
    }
   }
   return INSTANCE!!
  }
 }
}