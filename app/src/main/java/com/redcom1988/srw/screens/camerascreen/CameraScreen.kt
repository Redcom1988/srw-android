package com.redcom1988.srw.screens.camerascreen

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.rememberAsyncImagePainter
import com.redcom1988.srw.util.rememberPermissionState
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object CameraScreen : Screen {
    @Suppress("unused")
    private fun readResolve(): Any = CameraScreen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val screenModel = rememberScreenModel { CameraScreenModel() }
        val capturedImages by screenModel.capturedImages.collectAsState()
        val uploadState by screenModel.uploadState.collectAsState()

        LaunchedEffect(uploadState) {
            when (uploadState) {
                is CameraScreenModel.UploadState.Success -> {
                    screenModel.resetUploadState()
                    navigator.pop()
                    // TODO: Show success message or navigate to submission detail
                }
                is CameraScreenModel.UploadState.Error -> {
                    // TODO: Show error message
                    screenModel.resetUploadState()
                }
                else -> {}
            }
        }

        CameraScreenContent(
            capturedImages = capturedImages,
            uploadState = uploadState,
            onAddImage = screenModel::addImage,
            onUpdateImages = screenModel::updateImages,
            onNavigateUp = { navigator.pop() },
            onSubmit = { screenModel.submitImages(context) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraScreenContent(
    capturedImages: List<Uri>,
    uploadState: CameraScreenModel.UploadState,
    onAddImage: (Uri) -> Unit,
    onUpdateImages: (List<Uri>) -> Unit,
    onNavigateUp: () -> Unit,
    onSubmit: () -> Unit
) {
    val context = LocalContext.current
    val navigator = LocalNavigator.currentOrThrow
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showBackDialog by remember { mutableStateOf(false) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.isGranted.value ) {
            cameraPermissionState.requestPermission()
        }
    }

    BackHandler(
        onBack = {
            if (capturedImages.isEmpty()) onNavigateUp() else showBackDialog = true
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (cameraPermissionState.isGranted.value) {
            CameraPreview(
                onImageCaptureReady = { capture ->
                    imageCapture = capture
                }
            )

            // Loading overlay during upload
            if (uploadState is CameraScreenModel.UploadState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            CameraBottomBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding(),
                capturedImages = capturedImages,
                onViewImages = {
                    navigator.push(
                        CapturedImagesPreviewScreen(
                            capturedImages = capturedImages,
                            onImagesUpdated = onUpdateImages
                        )
                    )
                },
                onCaptureImage = {
                    imageCapture?.let { capture ->
                        captureImage(
                            context = context,
                            imageCapture = capture,
                            onImageCaptured = onAddImage,
                            onError = { exception ->
                                Log.e("CameraScreen", "Camera error", exception)
                            }
                        )
                    }
                },
                onSubmit = {
                    showConfirmDialog = true
                }
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.3f))
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                IconButton(
                    onClick = {
                        if (capturedImages.isEmpty()) onNavigateUp() else showBackDialog = true
                    },
                    content = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        }
    }

    if (showBackDialog) {
        AlertDialog(
            onDismissRequest = { showBackDialog = false },
            title = { Text(text = "Cancel Submission?") }, // TODO String Resource
            text = { Text(text = "Are you sure you want to cancel your submission?") }, // TODO String Resource
            confirmButton = {
                TextButton(
                    onClick = {
                        showBackDialog = false
                        onNavigateUp()
                    },
                    content = { Text("Yes") } // TODO String Resource
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { showBackDialog = false },
                    content = { Text("Cancel") } // TODO String Resource
                )
            }
        )
    }

    // Confirmation dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text(text = "Finish Submission?") }, // TODO String Resource
            text = { Text(text = "Are you sure you want to finish and submit ${capturedImages.size} image(s)?") }, // TODO String Resource
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        onSubmit()
                    },
                    content = { Text("Yes") } // TODO String Resource
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = false },
                    content = { Text("Cancel") } // TODO String Resource
                )
            }
        )
    }
}

@Composable
private fun CameraPreview(
    onImageCaptureReady: (ImageCapture) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        try {
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
            onImageCaptureReady(imageCapture)
        } catch (e: Exception) {
            Log.e("CameraPreview", "Failed to bind camera use cases", e)
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun CameraBottomBar(
    modifier: Modifier = Modifier,
    capturedImages: List<Uri>,
    onViewImages: () -> Unit,
    onCaptureImage: () -> Unit,
    onSubmit: () -> Unit
) {
    val hasImages = capturedImages.isNotEmpty()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(32.dp)
    ) {
        if (hasImages) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
            ) {
                FloatingActionButton(
                    onClick = onViewImages,
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(capturedImages.last()),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 2.dp),
                    content =  {
                        Text(
                            text = capturedImages.size.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }

            FloatingActionButton(
                onClick = onSubmit,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(48.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                content = {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = null,
                    )
                }
            )
        }

        FloatingActionButton(
            onClick = onCaptureImage,
            modifier = Modifier
                .align(Alignment.Center)
                .size(72.dp),
            shape = CircleShape,
            containerColor = Color.White,
            content = {}
        )
    }
}

private fun captureImage(
    context: Context,
    imageCapture: ImageCapture,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val photoFile = File(
        context.getExternalFilesDir(null),
        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                onImageCaptured(savedUri)
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

