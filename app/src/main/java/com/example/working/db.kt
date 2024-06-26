package com.example.working
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database (entities = [Items::class, Users::class, ItemsIzbr::class], version = 1)
abstract class MainDb : RoomDatabase() {
    abstract fun getDao(): com.example.working.Dao

    companion object{
        fun getDb(context: Context): MainDb{
            return Room.databaseBuilder(
                context.applicationContext,
                MainDb::class.java,
                "test.db"
            ).build()
        }
    }
}

