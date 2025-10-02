package com.linha.myreportcity.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.linha.myreportcity.model.user.LoginUser
import com.linha.myreportcity.model.user.LogoutResponse
import com.linha.myreportcity.model.user.RegisterUser
import com.linha.myreportcity.model.user.Users
import com.linha.myreportcity.navigation.Screen
import com.linha.myreportcity.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserViewModel(val repo: UserRepository) : ViewModel() {
    private val _user = MutableLiveData<Users?>()
    val user: MutableLiveData<Users?> = _user
    private val _searchedUser = MutableLiveData<List<Users>?>(emptyList())
    val searchedUser: MutableLiveData<List<Users>?> = _searchedUser
    private val _tempToken = MutableStateFlow<String?>(null)
    val tempToken: StateFlow<String?> = _tempToken.asStateFlow()
    // State untuk Loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

//    init {
//        getAllReports()
//    }

    fun register(
        user: RegisterUser,
        navController: NavController,
        context: Context
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repo.register(user)
                if (result.isSuccessful) {
                    _user.value = result.body()
                    Log.d("UsersViewModel", "Success Register User: ${result.code()}")
                    Toast.makeText(
                        context,
                        "Berhasil membuat akun, silahkan login",
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.navigate(Screen.Login.route)
                } else {
                    Log.e("UsersViewModel", "Error Register User: ${result.message()}")
                    Toast.makeText(
                        context,
                        "Gagal membuat akun",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("UsersViewModel", "Error Register User exception: ${e.message}")
                Toast.makeText(
                    context,
                    "Gagal membuat akun, periksa koneksi anda",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(
        user: LoginUser,
        rememberMe: Boolean = false,
        navController: NavController,
        context: Context
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repo.login(user)
                if (result.isSuccessful && result.body() != null) {
                    _tempToken.value = "Bearer ${result.body()?.accessToken}"
                    Log.d("UsersViewModel", "Success Login User with ${result.body()?.accessToken}")
                    if (rememberMe) {
                        TODO()
//                        Log.d("UsersViewModel", "Success remember me")
                    }
                    navController.navigate("main_graph") {
                        popUpTo("welcome_graph") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                    Toast.makeText(
                        context,
                        "Berhasil login dengan token ${tempToken.value}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.e("UsersViewModel", "Error Login User: ${result.message()}")
                    Toast.makeText(
                        context,
                        "Gagal login, coba lagi periksa email dan password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("UsersViewModel", "Error Login User exception: ${e.message}")
                Toast.makeText(
                    context,
                    "Gagal login, coba lagi periksa koneksi internet",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getUserProfiles(token: String, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repo.getUserProfiles(token)
                if (result.isSuccessful) {
                    _user.value = result.body()
                    Log.d("UsersViewModel", "Success getUserProfiles User: ${result.code()}")
                } else {
                    Log.e("UsersViewModel", "Error getUserProfiles User: ${result.message()}")
                }
            } catch (e: Exception) {
                Log.e("UsersViewModel", "Error getUserProfiles exception: ${e.message}")
                Toast.makeText(
                    context,
                    "Gagal memuat, periksa koneksi anda",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchUsername(token: String, searchParam: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repo.searchUsername(token, searchParam)
                if (result.isSuccessful && searchParam != ""){
                    _searchedUser.value = result.body()
                    Log.d("UsersViewModel", "Sucess search user : ${result.code()}")
                } else {
                    _searchedUser.value = emptyList()
                    Log.e("UsersViewModel", "Error searchUsername: ${result.message()}")
                }
            } catch (e: Exception) {
                Log.e("UsersViewModel", "Error searchUsername: ${e.message}")
                _searchedUser.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getCountStatus(token: String, status: Int) {

    }

    fun logout() {
        viewModelScope.launch {
            val token = tempToken.value

            _isLoading.value = true
            try {
                if (token != null) {
                    repo.logout(token).enqueue(object : Callback<LogoutResponse> {
                        override fun onResponse(
                            call: Call<LogoutResponse>,
                            response: Response<LogoutResponse>
                        ) {
                            if (response.isSuccessful) {
                                val message = response.body()?.message ?: "null message"
                                Log.d("UserViewModel", "Logout success: $message")
                            } else {
                                Log.e(
                                    "UserViewModel",
                                    "Logout failed: ${response.message()} & status code: ${response.code()}"
                                )
                            }
                            _isLoading.value = false
                        }

                        override fun onFailure(
                            call: Call<LogoutResponse>,
                            t: Throwable
                        ) {
                            Log.e("UserViewModel", "Logout failed: ${t.message}")
                            _isLoading.value = false
                        }
                    })
                } else {
                    Log.e("UserViewModel", "Logout failed: Token is null.")
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Logout failed: ${e.message}")
                _isLoading.value = false
            }
        }
    }

    class UserViewModelFactory(private val repository: UserRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
