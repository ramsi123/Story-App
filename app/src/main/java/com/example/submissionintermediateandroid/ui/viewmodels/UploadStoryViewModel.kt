package com.example.submissionintermediateandroid.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submissionintermediateandroid.data.DicodingStoryRepository
import com.example.submissionintermediateandroid.data.Result
import com.example.submissionintermediateandroid.data.remote.response.AddStoryResponse
import com.example.submissionintermediateandroid.data.remote.response.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class UploadStoryViewModel(private val dicodingStoryRepository: DicodingStoryRepository) : ViewModel() {

    private val _story = MutableLiveData<Result<AddStoryResponse>>()
    val story: LiveData<Result<AddStoryResponse>> = _story

    fun addStory(
        file: MultipartBody.Part,
        description: RequestBody
    ) {
        viewModelScope.launch {
            _story.postValue(Result.Loading)
            try {
                val response = dicodingStoryRepository.addStory(file, description)
                _story.postValue(Result.Success(response))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                errorResponse.message?.let { Result.Error(it) }?.let { _story.postValue(it) }
            }
        }
    }

    fun addStoryWithLocation(
        file: MultipartBody.Part,
        description: RequestBody,
        latitude: RequestBody,
        longitude: RequestBody
    ) {
        viewModelScope.launch {
            _story.postValue(Result.Loading)
            try {
                val response = dicodingStoryRepository.addStoryWithLocation(
                    file,
                    description,
                    latitude,
                    longitude
                )
                _story.postValue(Result.Success(response))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                errorResponse.message?.let { Result.Error(it) }?.let { _story.postValue(it) }
            }
        }
    }

}