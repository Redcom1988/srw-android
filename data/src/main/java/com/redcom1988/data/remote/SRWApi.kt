package com.redcom1988.data.remote

import com.redcom1988.core.network.GET
import com.redcom1988.core.network.NetworkHelper
import com.redcom1988.core.network.NetworkPreference
import com.redcom1988.core.network.POST
import com.redcom1988.core.network.await
import com.redcom1988.core.network.json
import com.redcom1988.data.remote.model.auth.AuthRequest
import com.redcom1988.data.remote.model.client.AddressRequest
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File

class SRWApi(
    private val networkHelper: NetworkHelper,
    private val preference: NetworkPreference
) {
    suspend fun login(nfcNumber: String): Response {
        val requestBody = json.encodeToString(AuthRequest(nfc = nfcNumber))
            .toRequestBody("application/json".toMediaType())

        return networkHelper.client.newCall(
            POST(
                url = preference.baseUrl().get() + "/auth/login/client",
                body = requestBody
            )
        ).await()
    }

    suspend fun refreshToken(refreshToken: String): Response {
        val requestBody = json.encodeToString(AuthRequest(refreshToken = refreshToken))
            .toRequestBody("application/json".toMediaType())

        return networkHelper.client.newCall(
            POST(
                url = preference.baseUrl().get() + "/auth/refresh",
                body = requestBody
            )
        ).await()
    }

    suspend fun logout(refreshToken: String): Response {
        val requestBody = json.encodeToString(AuthRequest(refreshToken = refreshToken))
            .toRequestBody("application/json".toMediaType())

        return networkHelper.client.newCall(
            POST(
                url = preference.baseUrl().get() + "/auth/logout",
                body = requestBody
            )
        ).await()
    }

    suspend fun getClientProfile(): Response {
        return networkHelper.client.newCall(
            GET(
                url = preference.baseUrl().get() + "/clients/profile",
            )
        ).await()
    }

    suspend fun getClientPoints(
        page: Int = 1,
        pageSize: Int = 20
    ): Response {
        val url = preference.baseUrl().get() + "/clients/profile/points?page=$page&pageSize=$pageSize"

        return networkHelper.client.newCall(
            GET(
                url = url,

            )
        ).await()
    }

    suspend fun getSubmissions(
        page: Int = 1,
        pageSize: Int = 20
    ): Response {
        val url = preference.baseUrl().get() + "/clients/submissions?page=$page&pageSize=$pageSize"

        return networkHelper.client.newCall(
            GET(
                url = url,
            )
        ).await()
    }

    suspend fun getSubmissionById(id: Int): Response {
        return networkHelper.client.newCall(
            GET(
                url = preference.baseUrl().get() + "/clients/submissions/$id",
            )
        ).await()
    }

    suspend fun uploadSubmission(
        imageFiles: List<File>
    ): Response {
        val multipartBodyBuilder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)

        imageFiles.forEach { file ->
            // Detect actual file type by reading magic bytes
            val fileBytes = file.readBytes()
            val isPng = fileBytes.size >= 8 &&
                fileBytes[0] == 0x89.toByte() &&
                fileBytes[1] == 0x50.toByte() &&
                fileBytes[2] == 0x4E.toByte() &&
                fileBytes[3] == 0x47.toByte()
            val isJpeg = fileBytes.size >= 2 &&
                fileBytes[0] == 0xFF.toByte() &&
                fileBytes[1] == 0xD8.toByte()

            val detectedType = when {
                isPng -> "image/png"
                isJpeg -> "image/jpeg"
                else -> "application/octet-stream"
            }

            val requestBody = file.asRequestBody(detectedType.toMediaType())
            multipartBodyBuilder.addFormDataPart(
                name = "File",
                filename = file.name,
                body = requestBody
            )
        }

        val requestBody = multipartBodyBuilder.build()
        val url = preference.baseUrl().get() + "/clients/submissions/new"

        return networkHelper.client.newCall(
            POST(
                url = url,
                body = requestBody
            )
        ).await()
    }

    suspend fun updateAddress(
        address: String,
        latitude: Float,
        longitude: Float
    ): Response {
        val requestBody = json.encodeToString(
    AddressRequest(
                address = address,
                latitude = latitude,
                longitude = longitude
            )
        ).toRequestBody("application/json".toMediaType())

        return networkHelper.client.newCall(
            POST(
                url = preference.baseUrl().get() + "/clients/profile/address",
                body = requestBody
            )
        ).await()
    }

}