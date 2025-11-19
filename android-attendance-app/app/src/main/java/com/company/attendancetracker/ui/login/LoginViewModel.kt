package com.company.attendancetracker.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.attendancetracker.data.local.entity.Employee
import com.company.attendancetracker.data.repository.AuthRepository
import com.company.attendancetracker.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Login screen.
 *
 * Handles user authentication and session management.
 */
class LoginViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Form fields
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    /**
     * Update username field.
     */
    fun onUsernameChange(newUsername: String) {
        _username.value = newUsername
    }

    /**
     * Update password field.
     */
    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    /**
     * Perform login.
     *
     * Steps:
     * 1. Validate input
     * 2. Authenticate with repository
     * 3. Save session if successful
     * 4. Update UI state
     */
    fun login() {
        viewModelScope.launch {
            // Validate input
            if (_username.value.isBlank()) {
                _uiState.value = LoginUiState.Error("Username cannot be empty")
                return@launch
            }
            if (_password.value.isBlank()) {
                _uiState.value = LoginUiState.Error("Password cannot be empty")
                return@launch
            }

            // Show loading
            _uiState.value = LoginUiState.Loading

            try {
                // Authenticate
                val employee = authRepository.login(_username.value, _password.value)

                if (employee != null) {
                    // Save session
                    sessionManager.saveLoginSession(employee.id)

                    // Update UI state with success
                    _uiState.value = LoginUiState.Success(employee)
                } else {
                    _uiState.value = LoginUiState.Error("Invalid username or password")
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("Login failed: ${e.message}")
            }
        }
    }

    /**
     * Check if user is already logged in.
     * Called when app starts.
     */
    fun checkLoginStatus() {
        viewModelScope.launch {
            try {
                if (sessionManager.isLoggedIn()) {
                    val employeeId = sessionManager.getEmployeeId()
                    if (employeeId != null) {
                        val employee = authRepository.getEmployeeById(employeeId)
                        if (employee != null) {
                            _uiState.value = LoginUiState.Success(employee)
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore errors, user will stay on login screen
            }
        }
    }

    /**
     * Reset UI state (after showing error).
     */
    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}

/**
 * UI State for Login screen.
 */
sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val employee: Employee) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
