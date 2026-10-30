package com.example.motobuddy.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.motobuddy.data.remote.dto.UserProfileDto
import com.example.motobuddy.data.repository.AuthRepository
import com.example.motobuddy.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(application)

    private val _loginState = MutableStateFlow<UiState<UserProfileDto?>>(UiState.Idle)
    val loginState: StateFlow<UiState<UserProfileDto?>> = _loginState

    private val _registerState = MutableStateFlow<UiState<UserProfileDto?>>(UiState.Idle)
    val registerState: StateFlow<UiState<UserProfileDto?>> = _registerState

    private val _logoutState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val logoutState: StateFlow<UiState<Boolean>> = _logoutState

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _loginState.value = UiState.Error("Email and password cannot be empty.")
            return
        }
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            val result = repository.login(email, pass)
            result.onSuccess {
                _loginState.value = UiState.Success(it)
            }.onFailure {
                _loginState.value = UiState.Error(it.localizedMessage ?: "Login failed")
            }
        }
    }

    fun register(name: String, email: String, pass: String) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            _registerState.value = UiState.Error("All registration fields are required.")
            return
        }
        viewModelScope.launch {
            _registerState.value = UiState.Loading
            val result = repository.register(name, email, pass)
            result.onSuccess {
                _registerState.value = UiState.Success(it)
            }.onFailure {
                _registerState.value = UiState.Error(it.localizedMessage ?: "Registration failed")
            }
        }
    }

    fun performLogout() {
        viewModelScope.launch {
            _logoutState.value = UiState.Loading
            repository.logout()
            _loginState.value = UiState.Idle
            _registerState.value = UiState.Idle
            _logoutState.value = UiState.Success(true)
        }
    }

    fun resetLogoutState() {
        _logoutState.value = UiState.Idle
    }
}
