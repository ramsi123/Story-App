package com.example.submissionintermediateandroid.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.submissionintermediateandroid.data.local.room.StoriesDatabase
import com.example.submissionintermediateandroid.data.remote.response.AddStoryResponse
import com.example.submissionintermediateandroid.data.remote.response.ListStoryItem
import com.example.submissionintermediateandroid.data.remote.response.LoginResponse
import com.example.submissionintermediateandroid.data.remote.response.RegisterResponse
import com.example.submissionintermediateandroid.data.remote.response.StoryResponse
import com.example.submissionintermediateandroid.data.remote.retrofit.ApiConfig
import com.example.submissionintermediateandroid.data.remote.retrofit.ApiService
import com.example.submissionintermediateandroid.util.SettingPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody

class DicodingStoryRepository(
    private val storiesDatabase: StoriesDatabase,
    private val apiService: ApiService,
    private val pref: SettingPreferences
) {

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun login(
        email: String,
        password: String
    ): LoginResponse {
        return apiService.login(email, password)
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        val token = runBlocking { pref.getToken().first() }
        val apiService = ApiConfig.getApiService(token)
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator = StoriesRemoteMediator(storiesDatabase, apiService),
            pagingSourceFactory = {
                //StoriesPagingSource(apiService)
                storiesDatabase.storiesDao().getAllStories()
            }
        ).liveData
    }

    suspend fun getStoriesWithLocation(): StoryResponse {
        val token = runBlocking { pref.getToken().first() }
        val apiService = ApiConfig.getApiService(token)
        return apiService.getStoriesWithLocation()
    }

    suspend fun addStory(
        file: MultipartBody.Part,
        description: RequestBody
    ): AddStoryResponse {
        val token = runBlocking { pref.getToken().first() }
        val apiService = ApiConfig.getApiService(token)
        return apiService.addStory(file, description)
    }

    suspend fun addStoryWithLocation(
        file: MultipartBody.Part,
        description: RequestBody,
        latitude: RequestBody,
        longitude: RequestBody
    ): AddStoryResponse {
        val token = runBlocking { pref.getToken().first() }
        val apiService = ApiConfig.getApiService(token)
        return apiService.addStoryWithLocation(file, description, latitude, longitude)
    }

    fun getToken(): Flow<String> {
        return pref.getToken()
    }

    suspend fun saveToken(token: String) {
        pref.saveToken(token)
    }

    companion object {
        @Volatile
        private var instance: DicodingStoryRepository? = null
        fun getInstance(
            storiesDatabase: StoriesDatabase,
            apiService: ApiService,
            pref: SettingPreferences
        ): DicodingStoryRepository =
            instance ?: synchronized(this) {
                instance ?: DicodingStoryRepository(storiesDatabase, apiService, pref)
            }.also { instance = it }
    }

}