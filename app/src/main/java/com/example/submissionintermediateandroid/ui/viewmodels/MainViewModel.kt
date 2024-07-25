package com.example.submissionintermediateandroid.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.submissionintermediateandroid.data.DicodingStoryRepository
import com.example.submissionintermediateandroid.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val dicodingStoryRepository: DicodingStoryRepository) : ViewModel() {

    val token: LiveData<String> = dicodingStoryRepository.getToken().asLiveData()

    fun saveToken(token: String) {
        viewModelScope.launch {
            dicodingStoryRepository.saveToken(token)
        }
    }

    fun getStories(): LiveData<PagingData<ListStoryItem>> =
        dicodingStoryRepository.getStories().cachedIn(viewModelScope)

}