package com.linha.myreportcity.model.user

import com.google.gson.annotations.SerializedName

data class Users(
    val id: Int,
    val name: String,
    val email: String,
    @SerializedName("admin_role")
    val adminRole: Boolean,
)