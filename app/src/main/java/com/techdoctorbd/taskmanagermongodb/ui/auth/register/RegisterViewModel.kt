package com.techdoctorbd.taskmanagermongodb.ui.auth.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techdoctorbd.taskmanagermongodb.data.api.TaskManagerApi
import com.techdoctorbd.taskmanagermongodb.data.models.AuthResponse
import com.techdoctorbd.taskmanagermongodb.data.models.User
import com.techdoctorbd.taskmanagermongodb.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val taskManagerApi: TaskManagerApi
) : ViewModel() {

    var registerResponse: MutableLiveData<NetworkResult<AuthResponse>> = MutableLiveData()

    fun registerUser(user: User) {
        viewModelScope.launch {
            //registerResponse.value = NetworkResult.Loading()
            try {
                val response = taskManagerApi.registerUser(user)
                registerResponse.value = handleRegisterResponse(response)
            } catch (e: Exception) {
                registerResponse.value = NetworkResult.Error(e.message)
            }
        }
    }

    private fun handleRegisterResponse(response: Response<AuthResponse>): NetworkResult<AuthResponse> {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Request timeout")
            }

            response.code() == 400 -> {
                return NetworkResult.Error("User already exists")
            }

            response.isSuccessful -> {
                val result = response.body()
                return NetworkResult.Success(result!!)
            }

            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }
}