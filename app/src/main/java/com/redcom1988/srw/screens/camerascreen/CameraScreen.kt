package com.redcom1988.srw.screens.camerascreen

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.redcom1988.srw.components.UniversalDialog
import com.redcom1988.srw.util.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        var isUploading by remember { mutableStateOf(false) }


        LaunchedEffect(uploadState) {
            when (val state = uploadState) {
                is CameraScreenModel.UploadState.Success -> {
                    screenModel.resetUploadState()
                    isUploading = false
                    navigator.pop()
                }
                is CameraScreenModel.UploadState.Error -> {
                    Toast.makeText(
                        context,
                        "Upload failed: ${state.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("ASD", "Upload failed: ${state.message}")
                    screenModel.resetUploadState()
                }
                else -> {}
            }
        }

        CameraScreenContent(
            capturedImages = capturedImages,
            uploadState = uploadState,
            isUploading = isUploading,
            onAddImage = screenModel::addImage,
            onUpdateImages = screenModel::updateImages,
            onNavigateUp = { navigator.pop() },
            onSubmit = {
                screenModel.submitImages(context)
                isUploading = true
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraScreenContent(
    capturedImages: List<Uri>,
    uploadState: CameraScreenModel.UploadState,
    isUploading: Boolean,
    onAddImage: (Uri) -> Unit,
    onUpdateImages: (List<Uri>) -> Unit,
    onNavigateUp: () -> Unit,
    onSubmit: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val navigator = LocalNavigator.currentOrThrow
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showBackDialog by remember { mutableStateOf(false) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var showCameraFlash by remember { mutableStateOf(false) }
    var isCapturing by remember { mutableStateOf(false) }
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

            // Camera flash animation
            AnimatedVisibility(
                visible = showCameraFlash,
                enter = fadeIn(tween(100, easing = FastOutSlowInEasing)),
                exit = fadeOut(tween(100, easing = FastOutLinearInEasing))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                )
            }


            AltCameraBottomBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter),
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
                    if (!isCapturing) {
                        isCapturing = true
                        showCameraFlash = true
                        imageCapture?.let { capture ->
                            captureImage(
                                context = context,
                                imageCapture = capture,
                                onImageCaptured = { uri ->
                                    onAddImage(uri)
                                    showCameraFlash = false
                                    scope.launch {
                                        delay(300)
                                        isCapturing = false
                                    }
                                },
                                onError = {
                                    Toast.makeText(
                                        context,
                                        "Failed to capture image",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    showCameraFlash = false
                                    isCapturing = false
                                }
                            )
                        }
                    }
                },
                onSubmit = {
                    showConfirmDialog = true
                },
                isUploading = isUploading
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

        // Loading overlay during upload - covers entire screen including bars
        if (uploadState is CameraScreenModel.UploadState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    if (showBackDialog) {
        UniversalDialog(
            title = "Cancel Submission?",
            message = "Are you sure you want to cancel your submission?",
            confirmText = "Yes",
            dismissText = "Cancel",
            onConfirm = {
                showBackDialog = false
                onNavigateUp()
            },
            onDismiss = { showBackDialog = false }
        )
    }

    // Confirmation dialog
    if (showConfirmDialog) {
        UniversalDialog(
            title = "Finish Submission?",
            message = "Are you sure you want to finish and submit ${capturedImages.size} image(s)?",
            confirmText = "Yes",
            dismissText = "Cancel",
            onConfirm = {
                showConfirmDialog = false
                onSubmit()
            },
            onDismiss = { showConfirmDialog = false }
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
    val imageCapture = remember { ImageCapture.Builder()
        .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()
    }

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
private fun AltCameraBottomBar(
    modifier: Modifier = Modifier,
    isUploading: Boolean = true,
    capturedImages: List<Uri>,
    onViewImages: () -> Unit,
    onCaptureImage: () -> Unit,
    onSubmit: () -> Unit
) {
    val hasImages = capturedImages.isNotEmpty()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(0.3f))
            .padding(32.dp)
            .navigationBarsPadding()
    ) {
        if (hasImages) {
            // Preview thumbnail - left side
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(
                            onClick = onViewImages,
                            enabled = !isUploading,
                        ),
                ) {
                    AnimatedContent(
                        targetState = capturedImages.last(),
                        transitionSpec = {
                            fadeIn(
                                animationSpec = tween(300, easing = FastOutSlowInEasing)
                            ) togetherWith fadeOut(
                                animationSpec = tween(200, easing = FastOutLinearInEasing)
                            ) using SizeTransform { _, _ ->
                                tween(300, easing = FastOutSlowInEasing)
                            }
                        },
                    ) { targetUri ->
                        Image(
                            painter = rememberAsyncImagePainter(targetUri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                AnimatedContent(
                    targetState = capturedImages.size,
                    transitionSpec = {
                        (fadeIn(tween(200)) +
                                scaleIn(
                                    initialScale = 0.8f,
                                    animationSpec = tween(200, easing = FastOutSlowInEasing)
                                )) togetherWith
                                (fadeOut(tween(100)) +
                                        scaleOut(
                                            targetScale = 1.2f,
                                            animationSpec = tween(100, easing = FastOutLinearInEasing)
                                        ))
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 2.dp),
                ) { count ->
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        content = {
                            Text(
                                text = count.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                }
            }

            // Submit button - right side
            Surface(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(
                        onClick = onSubmit,
                        enabled = !isUploading,
                    ),
                color = Color.Black.copy(0.1f),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        tint = Color.White,
                        contentDescription = null
                    )
                }
            }
        }

        // Capture button - ALWAYS visible (outside if block)
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .size(72.dp)
                .clip(CircleShape)
                .clickable(
                    onClick = onCaptureImage,
                    enabled = !isUploading,
                ),
            color = Color.White,
        ) {}
    }
}

@Composable
private fun CameraBottomBar(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
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
            .navigationBarsPadding()
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
                    AnimatedContent(
                        targetState = capturedImages.last(),
                        transitionSpec = {
                            fadeIn(
                                animationSpec = tween(300, easing = FastOutSlowInEasing)
                            ) togetherWith fadeOut(
                                animationSpec = tween(200, easing = FastOutLinearInEasing)
                            ) using SizeTransform { _, _ ->
                                tween(300, easing = FastOutSlowInEasing)
                            }
                        },
                        label = "thumbnail_transition"
                    ) { targetUri ->
                        Image(
                            painter = rememberAsyncImagePainter(targetUri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                AnimatedContent(
                    targetState = capturedImages.size,
                    transitionSpec = {
                        (fadeIn(tween(200)) +
                         scaleIn(
                             initialScale = 0.8f,
                             animationSpec = tween(200, easing = FastOutSlowInEasing)
                         )) togetherWith
                        (fadeOut(tween(100)) +
                         scaleOut(
                             targetScale = 1.2f,
                             animationSpec = tween(100, easing = FastOutLinearInEasing)
                         ))
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 2.dp),
                    label = "badge_transition"
                ) { count ->
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        content = {
                            Text(
                                text = count.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                }
            }

            FloatingActionButton(
                onClick = onSubmit,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(48.dp),
                containerColor = Color.Transparent,
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

