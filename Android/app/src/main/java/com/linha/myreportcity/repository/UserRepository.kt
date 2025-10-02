package com.linha.myreportcity.repository

import com.linha.myreportcity.model.report.CountStatusResponse
import com.linha.myreportcity.model.user.LoginResponse
import com.linha.myreportcity.model.user.LoginUser
import com.linha.myreportcity.model.user.LogoutResponse
import com.linha.myreportcity.model.user.RegisterUser
import com.linha.myreportcity.model.user.Users
import com.linha.myreportcity.network.UsersApiService
import retrofit2.Call
import retrofit2.Response

class UserRepository(val apiService: UsersApiService) {

    suspend fun register(user: RegisterUser): Response<Users> {
        return apiService.register(user)
    }

    suspend fun login(user: LoginUser): Response<LoginResponse> {
        return apiService.login(user)
    }

    suspend fun logout(token: String): Call<LogoutResponse> {
        return apiService.logout(token)
    }

    suspend fun getUserProfiles(token: String): Response<Users> {
        return apiService.getUserProfiles(token)
    }

    suspend fun searchUsername(token: String, searchParam: String): Response<List<Users>> {
        return apiService.searchUsername(token, searchParam)
    }
}