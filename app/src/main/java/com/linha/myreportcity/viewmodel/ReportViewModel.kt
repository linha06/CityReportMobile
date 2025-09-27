package com.linha.myreportcity.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.linha.myreportcity.model.report.GetReport
import com.linha.myreportcity.model.report.PostReport
import com.linha.myreportcity.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ReportViewModel(val repo: ReportRepository) : ViewModel() {
    private val _getReports = MutableLiveData<List<GetReport>>(emptyList())
    val getReports: MutableLiveData<List<GetReport>> = _getReports

    private val _getReportsById = MutableLiveData<List<GetReport>>(emptyList())
    val getReportsById: MutableLiveData<List<GetReport>> = _getReportsById

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _statusCounts = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val statusCounts: StateFlow<Map<Int, Int>> = _statusCounts.asStateFlow()

    private val _monthStatusCount = MutableStateFlow<Map<Int, List<Int?>?>>(emptyMap())
    val monthStatusCount: StateFlow<Map<Int, List<Float?>?>> = _monthStatusCount
        .map { mapOfInt ->
            // Iterasi melalui setiap entri (statusId -> List<Int?>)
            mapOfInt.mapValues { (_, listOfInts) ->
                // Jika List<Ints> tidak null, konversi setiap Int menjadi Float
                listOfInts?.map { intValue ->
                    intValue?.toFloat() // Konversi Int? menjadi Float?
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // menunggu 5 detik sebelum memulai ulang
            initialValue = emptyMap()
        )

    // State untuk Loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // State untuk Pagination
    private val _canPaginate = MutableStateFlow(true)
    val canPaginate: StateFlow<Boolean> = _canPaginate.asStateFlow()

    fun loadReports(
        token: String,
        isInitialLoad: Boolean = true,
        isSorted: Boolean = false,
        orderByStatus: String? = null
    ) {
        viewModelScope.launch {
            // pemuatan awal
            if (isInitialLoad) {
                _currentPage.value = 1
                _canPaginate.value = true
                if (_isLoading.value) return@launch
            }

            // reset nilai awal karna sorted diulang dr awal
            if (isSorted) {
                _getReports.value = emptyList()
                _currentPage.value = 1
                _canPaginate.value = true
                if (isLoading.value) return@launch
            }

            // Jika sedang loading atau tidak bisa paginate, cancel kode dibawah
            if (_isLoading.value || !canPaginate.value) return@launch

            _isLoading.value = true
            val pageToLoad = _currentPage.value

            try {
                // logika pemanggilan beda REPO / API
                val result = if (isSorted) repo.getSortedReports(
                    token = token,
                    currentPage = pageToLoad,
                    orderByStatus = orderByStatus
                ) else repo.loadReports(
                    token = token,
                    page = pageToLoad
                )

                if (result.isSuccessful && result.body() != null) {
                    val newReports = result.body()?.data.orEmpty()

                    // Logika penggabungan data
                    if (isInitialLoad) {
                        _getReports.value = newReports // Ganti total data lama
                    } else {
                        val oldList = _getReports.value.orEmpty()
                        _getReports.value = oldList + newReports // Tambahkan data baru
                    }

                    // Logika Pagination
                    _canPaginate.value = result.body()?.nextPageUrl != null
                    if (_canPaginate.value) {
                        _currentPage.value++
                    }

                    Log.d("ReportViewModel", "Success loading reports (Page: $pageToLoad)")
                } else {
                    if (isInitialLoad) _getReports.value = emptyList()
                    _canPaginate.value = false
                    Log.e(
                        "ReportViewModel",
                        "API call failed: ${result.code()} ${result.message()}"
                    )
                }
            } catch (e: Exception) {
                if (isInitialLoad) _getReports.value = emptyList()
                _canPaginate.value = false
                Log.e("ReportViewModel", "Error loading reports: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getAllReports(token: String) {
        loadReports(token, isInitialLoad = true)
    }

    fun getNextReportPage(token: String) {
        loadReports(token, isInitialLoad = false)
    }

    fun getSortedReportByStatusAsc(token: String) {
        loadReports(token, isInitialLoad = true, isSorted = true, orderByStatus = "asc")
    }

    fun getSortedReportByStatusDesc(token: String) {
        loadReports(token, isInitialLoad = true, isSorted = true, orderByStatus = "desc")
    }

    fun getReportByStatus(token: String, status: Int) {
        loadReportByStatus(
            token = token,
            isInitialLoad = true,
            status = status
        )
    }

    fun getNextPageReportByStatus(token: String, status: Int) {
        loadReportByStatus(
            token = token,
            isInitialLoad = false,
            status = status
        )
    }

    fun loadReportByStatus(token: String, isInitialLoad: Boolean = true, status: Int){
        viewModelScope.launch {
            // pemuatan awal
            if (isInitialLoad) {
                _currentPage.value = 1
                _canPaginate.value = true
                if (_isLoading.value) return@launch
            }

            // Jika sedang loading atau tidak bisa paginate, cancel kode dibawah
            if (_isLoading.value || !canPaginate.value) return@launch

            _isLoading.value = true
            val pageToLoad = _currentPage.value

            try {
                val result = repo.getReportByStatus(
                    token = token,
                    currentPage = pageToLoad,
                    status = status,
                )

                if (result.isSuccessful && result.body() != null) {
                    val newReports = result.body()?.data.orEmpty()

                    // Logika penggabungan data
                    if (isInitialLoad) {
                        _getReports.value = newReports // Ganti total data lama
                    } else {
                        val oldList = _getReports.value.orEmpty()
                        _getReports.value = oldList + newReports // Tambahkan data baru
                    }

                    // Logika Pagination
                    _canPaginate.value = result.body()?.nextPageUrl != null
                    if (_canPaginate.value) {
                        _currentPage.value++
                    }

                    Log.d("ReportViewModel", "Success loading reports (Page: $pageToLoad)")
                } else {
                    if (isInitialLoad) _getReports.value = emptyList()
                    _canPaginate.value = false
                    Log.e(
                        "ReportViewModel",
                        "API call failed: ${result.code()} ${result.message()}"
                    )
                }
            } catch (e: Exception) {
                if (isInitialLoad) _getReports.value = emptyList()
                _canPaginate.value = false
                Log.e("ReportViewModel", "Error loading reports: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getCountStatus(token: String, status: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repo.getCountStatus(token, status)
                if (result.isSuccessful && result.body() != null) {
                    val count = result.body()?.countStatus ?: 0

                    _statusCounts.update { currentMap ->
                        currentMap.toMutableMap().apply {
                            this[status] = count
                        }
                    }
                    Log.d("ReportViewModel", "Success getting count status")
                } else {
                    Log.e(
                        "ReportViewModel",
                        "getting count status API call failed or returned null body: ${result.code()} ${result.message()} with token : $token"
                    )
                }
            } catch (e: Exception) {
                Log.e("ReportViewModel", "Error getting count status: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getCountMonthStatus(token: String, status: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repo.getCountMonthStatus(token, status)
                if (result.isSuccessful && result.body() != null) {
                    val count = result.body()?.countMonthStatus

                    _monthStatusCount.update { currentMap ->
                        currentMap.toMutableMap().apply {
                            this[status] = count
                        }
                    }
                    Log.d("ReportViewModel", "Success getting count status")
                } else {
                    Log.e(
                        "ReportViewModel",
                        "getting count status API call failed or returned null body: ${result.code()} ${result.message()} with token : $token"
                    )
                }
            } catch (e: Exception) {
                Log.e("ReportViewModel", "Error getting count status: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getReportById(userId: Int, token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repo.getReportByUserId(userId, token)
                if (result.isSuccessful && result.body() != null) {
                    _getReportsById.value = result.body()
                    Log.d("ReportViewModel", "Success getting Reports by id")
                } else {
                    _getReportsById.value = emptyList()
                    Log.e(
                        "ReportViewModel",
                        "getting Reports by id API call failed or returned null body: ${result.code()} ${result.message()} with token : $token"
                    )
                }
            } catch (e: Exception) {
                _getReportsById.value = emptyList()
                Log.e("ReportViewModel", "Error getting All Reports: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadReport(token: String, report: PostReport, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repo.postReport(token, report)
                if (result.isSuccessful) {
                    Log.d("ReportViewModel", "Success uploading report")
                    Toast.makeText(context, "Success uploading report", Toast.LENGTH_SHORT).show()
                    getAllReports(token)
                } else {
                    Log.e(
                        "ReportViewModel",
                        "uploading report API call failed or returned null body: ${result.code()} & message : ${result.message()}"
                    )
                    Toast.makeText(
                        context,
                        "Failed uploading report, isi semua field",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("ReportViewModel", "Error uploading report: ${e.message}")
                Toast.makeText(
                    context,
                    "Failed uploading report, periksa internet kamu",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateReportStatus(token: String, id: Int, status: Int, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repo.updateStatus(token, id, status)

                if (result.isSuccessful) {
                    Log.d("ReportViewModel", "Status updated successfully for ID: $id")
                    Toast.makeText(context, "Status laporan berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    getReportByStatus(token, status)
                } else {
                    val errorMessage = result.errorBody()?.string() ?: "Unknown error"
                    Log.e("ReportViewModel", "Update status failed: ${result.code()} - $errorMessage")
                    Toast.makeText(context, "Gagal update status: ${result.code()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("ReportViewModel", "Error updating status: ${e.message}")
                Toast.makeText(context, "Koneksi gagal atau error lain: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                _isLoading.value = false
            }
        }
    }



    class ReportViewModelFactory(private val repository: ReportRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ReportViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
