package com.redcom1988.srw.screens.loginscreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.redcom1988.core.util.inject
import com.redcom1988.domain.auth.interactor.Login
import com.redcom1988.domain.client.interactor.GetClientProfile
import com.redcom1988.domain.preference.ApplicationPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginScreenModel(
    private val login: Login = inject(),
    private val getClientProfile: GetClientProfile = inject(),
    private val applicationPreference: ApplicationPreference = inject()
) : ScreenModel {

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun handleNfcTag(nfcNumber: String) {
        screenModelScope.launch(Dispatchers.IO) {
            _state.value = LoginState.Loading

            when (val result = login.await(nfcNumber)) {
                is Login.Result.Success -> {
                    checkClientProfile()
                }
                is Login.Result.Error -> {
                    _state.value = LoginState.Error(result.message)
                }
            }
        }
    }

    private suspend fun checkClientProfile() {
        try {
            when (val result = getClientProfile.await()) {
                is GetClientProfile.Result.Success -> {
                    val client = result.client
                    val hasAddress = client.address.isEmpty() &&
                        client.latitude != null && client.latitude != 0f &&
                        client.longitude != null && client.longitude != 0f
                    applicationPreference.onboardingComplete().set(hasAddress)
                    
                    if (hasAddress) {
                        _state.value = LoginState.Success
                    } else {
                        _state.value = LoginState.NeedsOnboarding
                    }
                }
                is GetClientProfile.Result.Error -> {
                    _state.value = LoginState.Success
                }
            }
        } catch (e: Exception) {
            _state.value = LoginState.Success
        }
    }

    fun resetState() {
        _state.value = LoginState.Idle
    }

    sealed interface LoginState {
        data object Idle : LoginState
        data object Loading : LoginState
        data object Success : LoginState
        data object NeedsOnboarding : LoginState
        data class Error(val message: String) : LoginState
    }
}
