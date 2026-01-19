package com.redcom1988.srw.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import kotlinx.coroutines.launch

private const val THUMBNAIL_WIDTH_DP = 64
private const val THUMBNAIL_SPACING_DP = 8

/**
 * Reusable image pager viewer component that supports both local URIs and remote URLs
 *
 * @param images List of image sources (can be Uri.toString() or remote URLs)
 * @param initialPage The initial page to display (0-indexed, defaults to 0)
 * @param title Title to display in the app bar
 * @param onNavigateUp Callback when user navigates back
 * @param canDelete Whether delete functionality should be enabled (default: false)
 * @param onImagesUpdated Callback when images are updated (only called if canDelete is true)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePagerViewer(
    images: List<String>,
    initialPage: Int = 0,
    title: String = "Images",
    onNavigateUp: () -> Unit,
    canDelete: Boolean = false,
    onImagesUpdated: ((List<String>) -> Unit)? = null
) {
    var imageList by remember { mutableStateOf(images) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(initialPage) }
    val configuration = LocalConfiguration.current
    val containerWidth = configuration.screenWidthDp
    val thumbnailWidth = THUMBNAIL_WIDTH_DP
    val spacing = THUMBNAIL_SPACING_DP
    val centerOffset = (containerWidth / 2 - thumbnailWidth / 2)

    val pagerState = rememberPagerState(
        initialPage = initialPage.coerceIn(0, (imageList.size - 1).coerceAtLeast(0)),
        pageCount = { imageList.size }
    )
    val thumbnailListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        currentPage = pagerState.currentPage
    }

    LaunchedEffect(currentPage) {
        if (imageList.isNotEmpty()) {
            coroutineScope.launch {
                thumbnailListState.animateScrollToItem(
                    index = currentPage,
                    scrollOffset = centerOffset
                )
            }
        }
    }

    Scaffold(
        topBar = {
            AppBar(
                title = title,
                navigateUp = {
                    if (canDelete && onImagesUpdated != null) {
                        onImagesUpdated(imageList)
                    }
                    onNavigateUp()
                },
                actions = {
                    if (canDelete && imageList.isNotEmpty()) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete image"
                            )
                        }
                    }
                }
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        if (imageList.isEmpty()) {
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
                    val imageSource = imageList[page]
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        SubcomposeAsyncImage(
                            model = imageSource,
                            contentDescription = "Image ${page + 1}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit,
                            loading = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                }
                            },
                            error = {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BrokenImage,
                                        contentDescription = "Failed to load",
                                        tint = Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        text = "Failed to load image",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                }
                            }
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
                        contentPadding = PaddingValues(horizontal = centerOffset.dp),
                        horizontalArrangement = Arrangement.spacedBy(spacing.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        itemsIndexed(imageList) { index, imageSource ->
                            ThumbnailItem(
                                imageSource = imageSource,
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

    if (showDeleteDialog && canDelete) {
        UniversalDialog(
            title = "Delete image?",
            message = "Are you sure you want to delete this image? This action cannot be undone.",
            icon = Icons.Default.Delete,
            iconTint = MaterialTheme.colorScheme.error,
            confirmText = "Delete",
            confirmColor = MaterialTheme.colorScheme.error,
            dismissText = "Cancel",
            onConfirm = {
                if (imageList.isNotEmpty()) {
                    imageList = imageList.filterIndexed { index, _ -> index != currentPage }
                    if (currentPage >= imageList.size && imageList.isNotEmpty()) {
                        currentPage = imageList.size - 1
                    }
                    if (imageList.isEmpty()) {
                        onImagesUpdated?.invoke(imageList)
                        onNavigateUp()
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

@Composable
fun ThumbnailItem(
    imageSource: String,
    isCurrentPage: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(if (!isCurrentPage) 56.dp else 64.dp),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = if (isCurrentPage) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrentPage) 4.dp else 2.dp
        )
    ) {
        SubcomposeAsyncImage(
            model = imageSource,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                }
            },
            error = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BrokenImage,
                        contentDescription = "Failed to load",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        )
    }
}
