package com.linha.myreportcity.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.linha.myreportcity.repository.CloudinaryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class CloudinaryViewModel(val repo: CloudinaryRepository) : ViewModel() {

    private val _urlResult = MutableLiveData<String?>()
    val urlResult: LiveData<String?> get() = _urlResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun uploadImage(token: String, image: MultipartBody.Part) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val response = repo.uploadImage(token = token, image = image)
                Log.d("CloudinaryViewModel", "Response code: ${response.code()} & message: ${response.message()}")
                if (response.isSuccessful) {
                    val imageUrl = response.body()?.url
                    Log.d("CloudinaryViewModel", "Success get Image URL: $imageUrl")
                    _urlResult.postValue(imageUrl)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("CloudinaryViewModel", "API call failed. Error body: $errorBody")
                    Log.e("CloudinaryViewModel", "API call failed or returned null body: ${response.code()} & message : ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("CloudinaryViewModel", "Error uploading image: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetUrlResult() {
        _urlResult.value = null
    }

    class CloudinaryViewModelFactory(private val repository: CloudinaryRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CloudinaryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CloudinaryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}