package com.example.submissionintermediateandroid.ui.viewmodels

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.submissionintermediateandroid.data.DicodingStoryRepository
import com.example.submissionintermediateandroid.di.Injection

class ViewModelFactory(private val dicodingStoryRepository: DicodingStoryRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(dicodingStoryRepository) as T
        } else if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(dicodingStoryRepository) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(dicodingStoryRepository) as T
        } else if (modelClass.isAssignableFrom(UploadStoryViewModel::class.java)) {
            return UploadStoryViewModel(dicodingStoryRepository) as T
        } else if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(dicodingStoryRepository) as T
        }
        throw IllegalArgumentException("ViewModel class not found.")
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context, preferences: DataStore<Preferences>): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context, preferences))
            }.also { instance = it }
    }

}