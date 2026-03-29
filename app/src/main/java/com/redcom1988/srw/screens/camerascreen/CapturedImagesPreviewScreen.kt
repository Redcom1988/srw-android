package com.redcom1988.srw.screens.camerascreen

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.redcom1988.srw.components.ImagePagerViewer

data class CapturedImagesPreviewScreen(
    val capturedImages: List<Uri>,
    val onImagesUpdated: (List<Uri>) -> Unit
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        BackHandler(onBack = { navigator.pop() })

        ImagePagerViewer(
            images = capturedImages.map { it.toString() },
            title = "Captured Images",
            onNavigateUp = { navigator.pop() },
            canDelete = true,
            onImagesUpdated = { updatedImages ->
                onImagesUpdated(updatedImages.map { Uri.parse(it) })
            }
        )
    }
}

