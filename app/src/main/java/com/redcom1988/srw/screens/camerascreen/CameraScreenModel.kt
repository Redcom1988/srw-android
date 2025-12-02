package com.redcom1988.srw.screens.camerascreen

import android.content.Context
import android.net.Uri
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.redcom1988.core.util.inject
import com.redcom1988.domain.submission.interactor.UploadSubmission
import com.redcom1988.domain.submission.model.Submission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File


class CameraScreenModel(
    private val uploadSubmission: UploadSubmission = inject()
) : ScreenModel {

    private val _capturedImages = MutableStateFlow<List<Uri>>(emptyList())
    val capturedImages: StateFlow<List<Uri>> = _capturedImages.asStateFlow()

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()

    fun addImage(uri: Uri) {
        _capturedImages.update { currentImages ->
            currentImages + uri
        }
    }

    fun removeImage(uri: Uri) {
        _capturedImages.update { currentImages ->
            currentImages.filter { it != uri }
        }
    }

    fun updateImages(images: List<Uri>) {
        _capturedImages.value = images
    }

    fun clearImages() {
        _capturedImages.value = emptyList()
    }

    fun submitImages(context: Context) {
        screenModelScope.launch {
            _uploadState.value = UploadState.Loading

            try {
                val files = _capturedImages.value.map { uri ->
                    uriToFile(context, uri)
                }

                val pngFiles = files.map { file ->
                    com.redcom1988.srw.util.ImageConversionUtil.ensurePng(file)
                }

                when (val result = uploadSubmission.await(pngFiles)) {
                    is UploadSubmission.Result.Success -> {
                        _uploadState.value = UploadState.Success(result.submissions)
                        clearImages()

                        pngFiles.filter { it.name.contains("_converted.png") }.forEach { it.delete() }
                    }
                    is UploadSubmission.Result.Error -> {
                        _uploadState.value = UploadState.Error(result.error.message ?: "Upload failed")

                        pngFiles.filter { it.name.contains("_converted.png") }.forEach { it.delete() }
                    }
                }
            } catch (e: Exception) {
                _uploadState.value = UploadState.Error(e.message ?: "Upload failed")
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        return File(uri.path!!)
    }

    fun resetUploadState() {
        _uploadState.value = UploadState.Idle
    }

    sealed class UploadState {
        object Idle : UploadState()
        object Loading : UploadState()
        data class Success(val submission: Submission) : UploadState()
        data class Error(val message: String) : UploadState()
    }
}

