package com.redcom1988.srw.screens.camerascreen

import android.net.Uri
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CameraScreenModel : ScreenModel {

    private val _capturedImages = MutableStateFlow<List<Uri>>(emptyList())
    val capturedImages: StateFlow<List<Uri>> = _capturedImages.asStateFlow()

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
}

