package com.linha.myreportcity.network

import com.linha.myreportcity.model.report.CountStatusResponse
import com.linha.myreportcity.model.user.LoginResponse
import com.linha.myreportcity.model.user.LoginUser
import com.linha.myreportcity.model.user.LogoutResponse
import com.linha.myreportcity.model.user.RegisterUser
import com.linha.myreportcity.model.user.Users
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface UsersApiService {

    // pakai @Body karena content-type nya application/json
    @POST("api/register")
    suspend fun register(
        @Body itemData: RegisterUser
    ): Response<Users>

    @POST("api/login")
    suspend fun login(
        @Body itemData: LoginUser
    ): Response<LoginResponse>

    // butuh token header token, tipe bearer
    @POST("api/logout")
    suspend fun logout(
        @Header("Authorization") token: String
        // ini nanti jadi nya Authorization: Bearer <token> || dimana Bearer dan token dari code yg diisi
    ): Call<LogoutResponse>

    @GET("api/me")
    suspend fun getUserProfiles(
        @Header("Authorization") token: String
    ): Response<Users>

    @GET("api/search-username")
    suspend fun searchUsername(
        @Header("Authorization") token: String,
        @Query("username") username: String
    ): Response<List<Users>>
}

// ini kalau misal content-type nya formurlendcoded
//@FormUrlEncoded
//@POST("api/login")
//suspend fun login(
//    @Field("email") email: String,
//    @Field("password") password: String
//): LoginResponse