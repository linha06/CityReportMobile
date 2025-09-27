package com.linha.myreportcity.repository

import com.linha.myreportcity.model.report.CountMonthStatusResponse
import com.linha.myreportcity.model.report.CountStatusResponse
import com.linha.myreportcity.model.report.GetReport
import com.linha.myreportcity.model.report.PostReport
import com.linha.myreportcity.model.report.ReportPaginationResponse
import com.linha.myreportcity.model.report.UpdateStatusResponse
import com.linha.myreportcity.network.ReportsApiService
import retrofit2.Response

class ReportRepository(val apiService: ReportsApiService) {

    suspend fun getAllReports(token: String): Response<ReportPaginationResponse> {
        return apiService.getAllReports(token)
    }

    suspend fun loadReports(token: String, page: Int, limit: Int? = null): Response<ReportPaginationResponse> {
        return apiService.getAllReports(token, page, limit)
    }

    suspend fun getReportByUserId(userId: Int, token: String): Response<List<GetReport>> {
        return apiService.getReportsByUserId(token, userId)
    }

    suspend fun postReport(token: String,report: PostReport): Response<GetReport> {
        return apiService.createReport(token, report)
    }

    suspend fun getSortedReports(token: String, currentPage: Int, orderByStatus: String?, limit: Int? = null): Response<ReportPaginationResponse> {
        return apiService.getSortedReports(token, currentPage, limit, orderByStatus)
    }

    suspend fun getReportByStatus(token: String, currentPage: Int, status: Int?, limit: Int? = null): Response<ReportPaginationResponse> {
        return apiService.getReportByStatus(token, currentPage, limit, status)
    }

    suspend fun getCountStatus(token: String, status: Int): Response<CountStatusResponse> {
        return apiService.getCountStatus(token, status)
    }

    suspend fun getCountMonthStatus(token: String, status: Int): Response<CountMonthStatusResponse> {
        return apiService.getCountMonthStatus(token, status)
    }

    suspend fun updateStatus(token: String, id: Int, status: Int): Response<UpdateStatusResponse> {
        return apiService.updateStatus(token, id, status)
    }
}