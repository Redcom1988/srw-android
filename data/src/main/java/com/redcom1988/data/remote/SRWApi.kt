package com.redcom1988.data.remote

import com.redcom1988.core.network.GET
import com.redcom1988.core.network.NetworkHelper
import com.redcom1988.core.network.POST
import com.redcom1988.core.network.await
import com.redcom1988.data.remote.model.BaseResponse
import com.redcom1988.data.remote.model.PaginatedResponse
import com.redcom1988.data.remote.model.auth.AuthRequest
import com.redcom1988.data.remote.model.auth.AuthResponse
import com.redcom1988.data.remote.model.client.ClientResponse
import com.redcom1988.data.remote.model.point.PointResponse
import com.redcom1988.data.remote.model.submission.SubmissionResponse
import com.redcom1988.domain.preference.ApplicationPreference
import kotlinx.serialization.json.Json
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class SRWApi(
    private val networkHelper: NetworkHelper,
    private val preference: ApplicationPreference
) {
    suspend fun login(nfcNumber: String): BaseResponse<AuthResponse> {
        val requestBody = Json.encodeToString(AuthRequest(nfc = nfcNumber))
            .toRequestBody("application/json".toMediaType())

        val response = networkHelper.client.newCall(
            POST(
                url = preference.baseUrl().get() + "/auth/login/client",
                body = requestBody
            )
        ).await()

        return Json.decodeFromString(response.body.string())
    }

    suspend fun refreshToken(refreshToken: String): BaseResponse<AuthResponse> {
        val requestBody = Json.encodeToString(AuthRequest(refreshToken = refreshToken))
            .toRequestBody("application/json".toMediaType())

        val response = networkHelper.client.newCall(
            POST(
                url = preference.baseUrl().get() + "/auth/refresh",
                body = requestBody
            )
        ).await()

        return Json.decodeFromString(response.body.string())

    }

    suspend fun logout(refreshToken: String): BaseResponse<String?> {
        val requestBody = Json.encodeToString(AuthRequest(refreshToken = refreshToken))
            .toRequestBody("application/json".toMediaType())

        val response = networkHelper.client.newCall(
            POST(
                url = preference.baseUrl().get() + "/auth/logout",
                body = requestBody
            )
        ).await()

        return Json.decodeFromString(response.body.string())

    }

    suspend fun getClientProfile(): BaseResponse<ClientResponse> {
        val accessToken = preference.accessToken().get()
        val headers = Headers.Builder()
            .add("Authorization", "Bearer $accessToken")
            .build()

        val response = networkHelper.client.newCall(
            GET(
                url = preference.baseUrl().get() + "/clients/profile",
                headers = headers
            )
        ).await()

        return Json.decodeFromString(response.body.string())
    }

    suspend fun getClientPoints(
        page: Int = 1,
        pageSize: Int = 20
    ): BaseResponse<PaginatedResponse<PointResponse>> {
        val accessToken = preference.accessToken().get()
        val headers = Headers.Builder()
            .add("Authorization", "Bearer $accessToken")
            .build()
        val url = preference.baseUrl().get() + "/clients/profile/points?page=$page&pageSize=$pageSize"

        val response = networkHelper.client.newCall(
            GET(
                url = url,
                headers = headers

            )
        ).await()

        return Json.decodeFromString(response.body.string())
    }

    suspend fun getSubmissions(
        page: Int = 1,
        pageSize: Int = 20
    ): BaseResponse<PaginatedResponse<SubmissionResponse>> {
        val accessToken = preference.accessToken().get()
        val headers = Headers.Builder()
            .add("Authorization", "Bearer $accessToken")
            .build()
        val url = preference.baseUrl().get() + "/clients/submissions?page=$page&pageSize=$pageSize"

        val response = networkHelper.client.newCall(
            GET(
                url = url,
                headers = headers
            )
        ).await()

        return Json.decodeFromString(response.body.string())
    }

    suspend fun uploadSubmission(
        imageFiles: List<File>
    ): BaseResponse<SubmissionResponse> {
        val accessToken = preference.accessToken().get()
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

        val headers = Headers.Builder()
            .add("Authorization", "Bearer $accessToken")
            .build()

        val url = preference.baseUrl().get() + "/clients/submissions/new"

        val response = networkHelper.client.newCall(
            POST(
                url = url,
                headers = headers,
                body = requestBody
            )
        ).await()

        return Json.decodeFromString(response.body.string())
    }

}