package com.redcom1988.data.remote

import com.redcom1988.core.network.GET
import com.redcom1988.core.network.NetworkHelper
import com.redcom1988.core.network.await
import com.redcom1988.domain.preference.ApplicationPreference
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