package com.example.submissionintermediateandroid.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.submissionintermediateandroid.data.local.entity.RemoteKeys
import com.example.submissionintermediateandroid.data.remote.response.ListStoryItem

@Database(
    entities = [ListStoryItem::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class StoriesDatabase : RoomDatabase() {

    abstract fun storiesDao(): StoriesDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: StoriesDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoriesDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoriesDatabase::class.java, "stories_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}