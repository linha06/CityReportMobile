package com.linha.myreportcity.model.report

import com.google.gson.annotations.SerializedName

data class UpdateStatusResponse(

	@field:SerializedName("report")
	val report: GetReport? = null,

	@field:SerializedName("message")
	val message: String? = null
)
