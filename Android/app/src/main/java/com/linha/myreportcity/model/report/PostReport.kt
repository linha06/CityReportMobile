package com.linha.myreportcity.model.report

data class PostReport(
    val userId: Int,
    val urlFoto: String,
    val deskripsi: String,
    val status: Int? = null,
    val lat: Double?,
    val dat: Double?,
    val upvote: Int? = null
)
