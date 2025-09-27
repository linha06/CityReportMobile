package com.linha.myreportcity.repository

import com.linha.myreportcity.model.cloudinary.CloudinaryUploadResponse
import com.linha.myreportcity.network.CloudinaryApiService
import okhttp3.MultipartBody
import retrofit2.Response

class CloudinaryRepository(val apiService: CloudinaryApiService) {
    suspend fun uploadImage(token: String, image: MultipartBody.Part): Response<CloudinaryUploadResponse> {
        return apiService.uploadImage(token = token, file = image)
    }
}