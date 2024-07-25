package com.example.submissionintermediateandroid.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submissionintermediateandroid.data.DicodingStoryRepository
import com.example.submissionintermediateandroid.data.Result
import com.example.submissionintermediateandroid.data.remote.response.ErrorResponse
import com.example.submissionintermediateandroid.data.remote.response.LoginResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private val dicodingStoryRepository: DicodingStoryRepository) : ViewModel() {

    private val _userLogin = MutableLiveData<Result<LoginResponse>>()
    val userLogin: LiveData<Result<LoginResponse>> = _userLogin

    fun login(
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            _userLogin.postValue(Result.Loading)
            try {
                val response = dicodingStoryRepository.login(email, password)
                _userLogin.postValue(Result.Success(response))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                errorResponse.message?.let { Result.Error(it) }?.let { _userLogin.postValue(it) }
            }
        }
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            dicodingStoryRepository.saveToken(token)
        }
    }

}