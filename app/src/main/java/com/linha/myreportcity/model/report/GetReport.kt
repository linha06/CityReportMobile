package com.linha.myreportcity.model.report

import com.linha.myreportcity.model.user.User

data class GetReport(
    val id: Int,
    val userId: Int,
    val urlFoto: String,
    val deskripsi: String,
    val status: Int,
    val lat: Double,
    val dat: Double,
    val upvote: Int,
    val user: User?
)
