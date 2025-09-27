package com.linha.myreportcity.network

import com.linha.myreportcity.model.report.CountMonthStatusResponse
import com.linha.myreportcity.model.report.CountStatusResponse
import com.linha.myreportcity.model.report.GetReport
import com.linha.myreportcity.model.report.PostReport
import com.linha.myreportcity.model.report.ReportPaginationResponse
import com.linha.myreportcity.model.report.UpdateStatusResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReportsApiService {

    //---------------------------- GET ------------------------------//
    @GET("api/reports")
    suspend fun getAllReports(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int? = null
    ): Response<ReportPaginationResponse>

    @GET("api/reports-by-user/{userId}")
    suspend fun getReportsByUserId(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<List<GetReport>>

    @GET("api/reports-orderby-status")
    suspend fun getSortedReports(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int? = null,
        @Query("orderByStatus") orderByStatus: String? = "asc",
    ): Response<ReportPaginationResponse>

    @GET("api/reports-by-status")
    suspend fun getReportByStatus(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int? = null,
        @Query("status") status: Int? = 1,
    ): Response<ReportPaginationResponse>

    @GET("api/show-count-status/{status}")
    suspend fun getCountStatus(
        @Header("Authorization") token: String,
        @Path("status") status: Int
    ) : Response<CountStatusResponse>

    @GET("api/count-month-status/{status}")
    suspend fun getCountMonthStatus(
        @Header("Authorization") token: String,
        @Path("status") status: Int
    ) : Response<CountMonthStatusResponse>


    //---------------------------- POST ------------------------------//
    @POST("api/reports")
    suspend fun createReport(
        @Header("Authorization") token: String,
        @Body itemData: PostReport
    ): Response<GetReport>

    @POST("api/update-status/{id}")
    suspend fun updateStatus(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Query("status") status: Int
    ) : Response<UpdateStatusResponse>
}