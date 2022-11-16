package com.waycool.data.Local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.waycool.data.Local.Entity.*
import com.waycool.data.Local.utils.TypeConverter
import java.lang.Exception


// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = [TagsEntity::class, CropMasterEntity::class,PestDiseaseEntity::class,CropInformationEntityData::class,MyCropDataEntity::class], version = 2, exportSchema = false)
@TypeConverters(TypeConverter::class)

abstract class OutgrowDB : RoomDatabase() {

    abstract fun outgrowDao(): OutgrowDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: OutgrowDB? = null

        fun init(context: Context) {
            INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OutgrowDB::class.java,
                    "outgrow_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
            }
        }

        fun getDatabase(): OutgrowDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: throw Exception("DataBase Not initialized")
        }
    }
}