package com.example.submissionintermediateandroid.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.submissionintermediateandroid.data.DicodingStoryRepository
import com.example.submissionintermediateandroid.data.Result
import com.example.submissionintermediateandroid.data.remote.response.ErrorResponse
import com.example.submissionintermediateandroid.data.remote.response.StoryResponse
import com.google.gson.Gson
import retrofit2.HttpException

class MapsViewModel(private val dicodingStoryRepository: DicodingStoryRepository) : ViewModel() {

    fun getStoriesWithLocation(): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = dicodingStoryRepository.getStoriesWithLocation()
            val users: MutableLiveData<Result<StoryResponse>> = MutableLiveData()
            users.value = Result.Success(response)
            emitSource(users)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            errorResponse.message?.let { Result.Error(it) }?.let { emit(it) }
        }
    }

}