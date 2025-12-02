package com.redcom1988.srw.screens.camerascreen

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.rememberAsyncImagePainter
import com.redcom1988.srw.components.AppBar
import kotlinx.coroutines.launch

data class CapturedImagesPreviewScreen(
    val capturedImages: List<Uri>,
    val onImagesUpdated: (List<Uri>) -> Unit
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var images by remember { mutableStateOf(capturedImages) }
        var showDeleteDialog by remember { mutableStateOf(false) }
        var currentPage by remember { mutableStateOf(0) }

        ImagePagerView(
            images = images,
            currentPage = currentPage,
            onPageChanged = { currentPage = it },
            onNavigateUp = {
                onImagesUpdated(images)
                navigator.pop()
            },
            onDeleteCurrent = {
                showDeleteDialog = true
            }
        )

        if (showDeleteDialog) {
            DeleteConfirmationDialog(
                onConfirm = {
                    if (images.isNotEmpty()) {
                        images = images.filterIndexed { index, _ -> index != currentPage }
                        if (currentPage >= images.size && images.isNotEmpty()) {
                            currentPage = images.size - 1
                        }
                        if (images.isEmpty()) {
                            onImagesUpdated(images)
                            navigator.pop()
                        }
                    }
                    showDeleteDialog = false
                },
                onDismiss = {
                    showDeleteDialog = false
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImagePagerView(
    images: List<Uri>,
    currentPage: Int,
    onPageChanged: (Int) -> Unit,
    onNavigateUp: () -> Unit,
    onDeleteCurrent: () -> Unit
) {
    BackHandler(onBack = onNavigateUp)

    val pagerState = rememberPagerState(
        initialPage = currentPage,
        pageCount = { images.size }
    )
    val thumbnailListState = rememberLazyListState()

    LaunchedEffect(pagerState.currentPage) {
        onPageChanged(pagerState.currentPage)
    }

    Scaffold(
        topBar = {
            AppBar(
                title = "Captured Images", // TODO String Resource
                navigateUp = onNavigateUp,
                actions = {
                    IconButton(onClick = onDeleteCurrent) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        if (images.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No images",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { page ->
                    val imageUri = images[page]
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = "Image ${page + 1}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LazyRow(
                        state = thumbnailListState,
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        itemsIndexed(images) { index, imageUri ->
                            val coroutineScope = rememberCoroutineScope()
                            ThumbnailItem(
                                imageUri = imageUri,
                                isCurrentPage = pagerState.currentPage == index,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThumbnailItem(
    imageUri: Uri,
    isCurrentPage: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(if (!isCurrentPage) 56.dp else 64.dp),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrentPage) 8.dp else 2.dp
        )
    ) {
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text(text = "Delete image?") }, // TODO String Resource
        text = { Text(text = "Are you sure you want to delete this image? This action cannot be undone." ) }, // TODO String Resource
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                content = {
                    Text(
                        text = "Delete", // TODO String Resource
                        color = MaterialTheme.colorScheme.error
                    )
                }
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                content = { Text(text = "Cancel") }// TODO String Resource
            )
        }
    )
}

