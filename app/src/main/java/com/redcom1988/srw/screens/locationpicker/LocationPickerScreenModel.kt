package com.redcom1988.srw.screens.locationpicker

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.redcom1988.core.network.NetworkPreference
import com.redcom1988.core.util.inject
import com.redcom1988.domain.client.interactor.UpdateAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationPickerScreenModel(
    private val updateAddress: UpdateAddress = inject(),
    private val networkPreference: NetworkPreference = inject()
) : ScreenModel {

    private val _state = MutableStateFlow(LocationState())
    val state: StateFlow<LocationState> = _state.asStateFlow()

    fun updateSelectedLocation(
        address: String,
        latitude: Double,
        longitude: Double
    ) {
        _state.value = _state.value.copy(
            selectedAddress = address,
            latitude = latitude,
            longitude = longitude
        )
    }

    fun confirmLocation() {
        val currentState = _state.value
        if (currentState.selectedAddress.isBlank()) return

        screenModelScope.launch(Dispatchers.IO) {
            _state.value = currentState.copy(isLoading = true)

            when (val result = updateAddress.await(
                address = currentState.selectedAddress,
                latitude = currentState.latitude.toFloat(),
                longitude = currentState.longitude.toFloat()
            )) {
                is UpdateAddress.Result.Success -> {
                    networkPreference.onboardingComplete().set(true)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }
                is UpdateAddress.Result.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.error.message ?: "Failed to update address"
                    )
                }
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    data class LocationState(
        val selectedAddress: String = "",
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val error: String? = null
    )
}
