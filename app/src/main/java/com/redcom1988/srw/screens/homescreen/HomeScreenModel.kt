package com.redcom1988.srw.screens.homescreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.redcom1988.core.util.inject
import com.redcom1988.domain.auth.interactor.Logout
import com.redcom1988.domain.client.interactor.GetClientProfile
import com.redcom1988.domain.client.model.Client
import com.redcom1988.domain.submission.interactor.GetRecentSubmissions
import com.redcom1988.domain.submission.model.Submission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeScreenModel(
    private val logout: Logout = inject(),
    private val getRecentSubmissions: GetRecentSubmissions = inject(),
    private val getClientProfile: GetClientProfile = inject()
) : ScreenModel {

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> = _logoutState.asStateFlow()

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _submissionsState = MutableStateFlow<SubmissionsState>(SubmissionsState.Loading)
    val submissionsState: StateFlow<SubmissionsState> = _submissionsState.asStateFlow()

    init {
        loadProfile()
        loadRecentSubmissions()
    }

    fun loadProfile() {
        screenModelScope.launch {
            _profileState.value = ProfileState.Loading

            when (val result = getClientProfile.await()) {
                is GetClientProfile.Result.Success -> {
                    _profileState.value = ProfileState.Success(result.client)
                }
                is GetClientProfile.Result.Error -> {
                    _profileState.value = ProfileState.Error(
                        result.error.message ?: "Failed to load profile"
                    )
                }
            }
        }
    }

    private fun loadRecentSubmissions() {
        screenModelScope.launch {
            _submissionsState.value = SubmissionsState.Loading

            when (val result = getRecentSubmissions.await(limit = 5)) {
                is GetRecentSubmissions.Result.Success -> {
                    _submissionsState.value = SubmissionsState.Success(result.submissions)
                }
                is GetRecentSubmissions.Result.Error -> {
                    _submissionsState.value = SubmissionsState.Error(result.error.message ?: "Failed to load submissions")
                }
            }
        }
    }

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

    sealed interface SubmissionsState {
        data object Loading : SubmissionsState
        data class Success(val submissions: List<Submission>) : SubmissionsState
        data class Error(val message: String) : SubmissionsState
    }

    sealed interface ProfileState {
        data object Idle : ProfileState
        data object Loading : ProfileState
        data class Success(val client: Client) : ProfileState
        data class Error(val message: String) : ProfileState
    }
}