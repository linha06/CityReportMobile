package com.linha.myreportcity.model.report

import com.google.gson.annotations.SerializedName

data class CountMonthStatusResponse(

	@field:SerializedName("count_month_status")
	val countMonthStatus: List<Int?>? = null,

	@field:SerializedName("message")
	val message: String? = null
)
