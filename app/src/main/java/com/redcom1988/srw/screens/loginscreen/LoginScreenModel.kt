package com.redcom1988.srw.screens.loginscreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.redcom1988.core.util.inject
import com.redcom1988.core.util.injectLazy
import com.redcom1988.domain.auth.interactor.Login
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginScreenModel(
    private val login: Login = inject()
) : ScreenModel {

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun handleNfcTag(nfcNumber: String) {
        screenModelScope.launch {
            _state.value = LoginState.Loading

            when (val result = login.await(nfcNumber)) {
                is Login.Result.Success -> {
                    _state.value = LoginState.Success
                }
                is Login.Result.Error -> {
                    _state.value = LoginState.Error(
                        result.error.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }

    fun resetState() {
        _state.value = LoginState.Idle
    }

    sealed interface LoginState {
        data object Idle : LoginState
        data object Loading : LoginState
        data object Success : LoginState
        data class Error(val message: String) : LoginState
    }
}
