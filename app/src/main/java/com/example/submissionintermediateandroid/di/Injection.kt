package com.example.submissionintermediateandroid.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.submissionintermediateandroid.data.DicodingStoryRepository
import com.example.submissionintermediateandroid.data.local.room.StoriesDatabase
import com.example.submissionintermediateandroid.data.remote.retrofit.ApiConfig
import com.example.submissionintermediateandroid.util.SettingPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {

    fun provideRepository(context: Context, preferences: DataStore<Preferences>): DicodingStoryRepository {
        val database = StoriesDatabase.getDatabase(context)
        val pref = SettingPreferences.getInstance(preferences)
        val user = runBlocking { pref.getToken().first() }
        val apiService = ApiConfig.getApiService(user)
        return DicodingStoryRepository.getInstance(database, apiService, pref)
    }

}
