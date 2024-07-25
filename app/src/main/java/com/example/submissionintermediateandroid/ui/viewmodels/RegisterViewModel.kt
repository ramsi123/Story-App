package com.example.submissionintermediateandroid.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submissionintermediateandroid.data.DicodingStoryRepository
import com.example.submissionintermediateandroid.data.Result
import com.example.submissionintermediateandroid.data.remote.response.ErrorResponse
import com.example.submissionintermediateandroid.data.remote.response.RegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterViewModel(private val dicodingStoryRepository: DicodingStoryRepository) : ViewModel() {

    private val _userRegister = MutableLiveData<Result<RegisterResponse>>()
    val userRegister: LiveData<Result<RegisterResponse>> = _userRegister

    fun register(
        name: String,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            _userRegister.postValue(Result.Loading)
            try {
                val response = dicodingStoryRepository.register(name, email, password)
                _userRegister.postValue(Result.Success(response))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                errorResponse.message?.let { Result.Error(it) }?.let { _userRegister.postValue(it) }
            }
        }
    }

}