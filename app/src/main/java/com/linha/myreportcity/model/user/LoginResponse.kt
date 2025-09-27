package com.linha.myreportcity.model.user

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("access_token")
    val accessToken: String?,

    @SerializedName("token_type")
    val tokenType: String?,

    @SerializedName("expires_in")
    val expiresIn: Int?
)

// serialization name, untuk mencocokan nama key di json dengan variable