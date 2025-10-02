package com.linha.myreportcity.network

import com.linha.myreportcity.model.cloudinary.CloudinaryUploadResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CloudinaryApiService {
    @Multipart
    @POST("api/upload-file")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Response<CloudinaryUploadResponse>
}