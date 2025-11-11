package com.example.data.remote

import com.example.core.network.GET
import com.example.core.network.NetworkHelper
import com.example.core.network.await
import com.example.domain.preference.ApplicationPreference
import okhttp3.Response

class SRWApi(
    private val networkHelper: NetworkHelper,
    private val preference: ApplicationPreference
) {
    // Example API call to get balance
    suspend fun getBalance(): Response {
        return networkHelper.client.newCall(
            GET(preference.baseUrl().get() + "/api/v1/balance")
        ).await()
    }
}