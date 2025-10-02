package com.linha.myreportcity.model.report

import com.google.gson.annotations.SerializedName

data class CountStatusResponse(

	@field:SerializedName("count_status")
	val countStatus: Int? = null,

	@field:SerializedName("message")
	val message: String? = null
)
