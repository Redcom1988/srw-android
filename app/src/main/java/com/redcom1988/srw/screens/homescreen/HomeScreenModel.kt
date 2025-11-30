package com.redcom1988.srw.screens.homescreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.redcom1988.core.util.inject
import com.redcom1988.domain.auth.interactor.Logout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeScreenModel(
    private val logout: Logout = inject(),
) : ScreenModel {

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> = _logoutState.asStateFlow()

    fun handleLogout() {
        screenModelScope.launch {
            _logoutState.value = LogoutState.Loading

            when (val result = logout.await()) {
                is Logout.Result.Success -> {
                    _logoutState.value = LogoutState.Success
                }
                is Logout.Result.Error -> {
                    _logoutState.value = LogoutState.Error(result.error.message ?: "Logout failed")
                }
            }
        }
    }

    fun resetState() {
        _logoutState.value = LogoutState.Idle
    }

    sealed interface LogoutState {
        data object Idle : LogoutState
        data object Loading : LogoutState
        data object Success : LogoutState
        data class Error(val message: String) : LogoutState
    }
}