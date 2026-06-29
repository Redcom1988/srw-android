package com.redcom1988.srw.screens.homescreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.redcom1988.core.util.inject
import com.redcom1988.domain.auth.interactor.Logout
import com.redcom1988.domain.client.interactor.GetClientProfile
import com.redcom1988.domain.client.model.Client
import com.redcom1988.domain.submission.interactor.GetRecentSubmissions
import com.redcom1988.domain.submission.model.Submission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeScreenModel(
    private val logout: Logout = inject(),
    private val getRecentSubmissions: GetRecentSubmissions = inject(),
    private val getClientProfile: GetClientProfile = inject()
) : ScreenModel {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadAll()
    }

    fun loadAll() {
        screenModelScope.launch(Dispatchers.IO) {
            _uiState.value = UiState.Loading

            var profileError: String? = null
            var submissionsError: String? = null

            when (val result = getClientProfile.await()) {
                is GetClientProfile.Result.Success -> {
                    // Profile loaded
                }
                is GetClientProfile.Result.Error -> {
                    profileError = result.error.message ?: "Failed to load profile"
                }
            }

            when (val result = getRecentSubmissions.await(limit = 5)) {
                is GetRecentSubmissions.Result.Success -> {
                    // Submissions loaded
                }
                is GetRecentSubmissions.Result.Error -> {
                    submissionsError = result.error.message ?: "Failed to load submissions"
                }
            }

            val generalError = profileError ?: submissionsError

            when (val profileResult = getClientProfile.await()) {
                is GetClientProfile.Result.Success -> {
                    when (val submissionsResult = getRecentSubmissions.await(limit = 5)) {
                        is GetRecentSubmissions.Result.Success -> {
                            _uiState.value = UiState.Success(
                                client = profileResult.client,
                                submissions = submissionsResult.submissions
                            )
                        }
                        is GetRecentSubmissions.Result.Error -> {
                            _uiState.value = UiState.Success(
                                client = profileResult.client,
                                submissions = emptyList()
                            )
                        }
                    }
                }
                is GetClientProfile.Result.Error -> {
                    _uiState.value = UiState.Error(
                        generalError = generalError,
                        profileError = profileError ?: "Failed to load profile",
                        submissionsError = submissionsError
                    )
                }
            }
        }
    }

    fun loadProfile() {
        loadAll()
    }

    fun loadRecentSubmissions() {
        loadAll()
    }

    fun handleLogout(onLoggedOut: () -> Unit) {
        screenModelScope.launch(Dispatchers.IO) {
            logout.await()
            onLoggedOut()
        }
    }

    sealed interface UiState {
        data object Loading : UiState
        data class Success(
            val client: Client,
            val submissions: List<Submission>
        ) : UiState
        data class Error(
            val generalError: String? = null,
            val profileError: String? = null,
            val submissionsError: String? = null
        ) : UiState
    }
}