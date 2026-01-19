package com.redcom1988.srw.screens.submissiondetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.SubcomposeAsyncImage
import com.redcom1988.domain.submission.model.Submission
import com.redcom1988.domain.submission.model.SubmissionStatus
import com.redcom1988.srw.components.AppBar
import com.redcom1988.srw.screens.submissionimages.SubmissionImagesScreen
import com.redcom1988.srw.util.formatLastUpdated
import kotlin.time.ExperimentalTime

/**
 * Screen for viewing submission details
 */
data class SubmissionDetailScreen(
    val submission: Submission
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        SubmissionDetailScreenContent(
            submission = submission,
            onNavigateUp = { navigator.pop() },
            onViewImages = { sub, index -> navigator.push(SubmissionImagesScreen(sub, index)) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
private fun SubmissionDetailScreenContent(
    submission: Submission,
    onNavigateUp: () -> Unit,
    onViewImages: (Submission, Int) -> Unit
) {
    Scaffold(
        topBar = {
            AppBar(
                title = "Submission #${submission.id}",
                navigateUp = onNavigateUp,
                shadowElevation = 4.dp
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // Points earned - only show when COMPLETED
            if (submission.status == SubmissionStatus.COMPLETED) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Points Earned",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = (submission.totalPoints ?: 0).toString(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Agent assigned
            if (!submission.agentName.isNullOrBlank()) {
                InfoRow(
                    icon = Icons.Default.Person,
                    label = "Assigned Agent",
                    value = submission.agentName
                )
            }

            // Rejection reason (if rejected)
            if (submission.status == SubmissionStatus.REJECTED && !submission.rejectionReason.isNullOrBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Rejection Reason",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        Text(
                            text = submission.rejectionReason!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            SubmissionTimeline(
                submission = submission,
                onViewImages = onViewImages
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value ?: "N/A",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun SubmissionTimeline(
    submission: Submission,
    onViewImages: (Submission, Int) -> Unit
) {
    val timelineSteps = buildList {
        val hasProcessed = submission.processedAt?.toEpochMilliseconds()?.let { it > 0 } == true
        val hasReviewed = submission.reviewedAt?.toEpochMilliseconds()?.let { it > 0 } == true
        val hasAssigned = submission.assignedAt?.toEpochMilliseconds()?.let { it > 0 } == true
        val hasPickedUp = submission.pickedUpAt?.toEpochMilliseconds()?.let { it > 0 } == true

        // Helper function to check if current status is at or past a given status
        fun isStatusAtOrPast(vararg statuses: SubmissionStatus): Boolean {
            return submission.status in statuses
        }

        // Define status progression order
        val statusOrder = listOf(
            SubmissionStatus.PENDING,
            SubmissionStatus.ML_PROCESSING,
            SubmissionStatus.AWAITING_REVIEW,
            SubmissionStatus.APPROVED,
            SubmissionStatus.ASSIGNED,
            SubmissionStatus.PICKED_UP,
            SubmissionStatus.COMPLETED
        )

        // Helper to check if we've passed a certain stage
        fun hasPassed(status: SubmissionStatus): Boolean {
            val currentIndex = statusOrder.indexOf(submission.status)
            val targetIndex = statusOrder.indexOf(status)
            return currentIndex >= targetIndex || submission.status == SubmissionStatus.REJECTED
        }

        val isRejected = submission.status == SubmissionStatus.REJECTED
        val isMlProcessing = submission.status == SubmissionStatus.ML_PROCESSING
        val isAwaitingReview = submission.status == SubmissionStatus.AWAITING_REVIEW

        // 1. Submitted - always completed
        add(
            TimelineStep(
                icon = Icons.Default.Add,
                title = "Submitted",
                timestamp = submission.createdAt,
                isCompleted = true,
                description = "Waste submission created"
            )
        )

        // 2. ML Processing
        add(
            TimelineStep(
                icon = Icons.Default.AutoAwesome,
                title = when {
                    hasPassed(SubmissionStatus.AWAITING_REVIEW) -> "Processed"
                    isMlProcessing -> "Processing"
                    else -> "ML Processing"
                },
                timestamp = if (hasProcessed) submission.processedAt else null,
                isCompleted = hasPassed(SubmissionStatus.AWAITING_REVIEW),
                description = when {
                    hasPassed(SubmissionStatus.AWAITING_REVIEW) -> "AI processed images"
                    isMlProcessing -> "AI is processing images"
                    else -> "Waiting for AI to process images"
                }
            )
        )

        // 3. Review
        val reviewedWithPoints = hasPassed(SubmissionStatus.APPROVED) && (submission.totalPoints ?: 0) > 0
        
        // Get detected items from metadata - only show if reviewed
        val detectedItems = if (hasReviewed) {
            submission.images?.flatMap { it.metadata ?: emptyList() }
                ?.groupBy { it.trashType }
                ?.map { (type, items) -> 
                    type to items.sumOf { it.amount }
                }
                ?.filter { it.second > 0 }
        } else {
            null
        }
        
        add(
            TimelineStep(
                icon = Icons.Default.Search,
                title = when {
                    hasPassed(SubmissionStatus.APPROVED) -> "Reviewed"
                    isAwaitingReview -> "Awaiting Review"
                    else -> "Review"
                },
                timestamp = if (hasPassed(SubmissionStatus.APPROVED)) submission.reviewedAt else null,
                isCompleted = isAwaitingReview || hasPassed(SubmissionStatus.APPROVED),
                description = when {
                    reviewedWithPoints -> "Valued at ${submission.totalPoints} points"
                    hasPassed(SubmissionStatus.APPROVED) -> "Admin reviewed submission"
                    isAwaitingReview -> "Waiting for admin review"
                    else -> "Pending admin review"
                },
                metadata = detectedItems
            )
        )

        // 4. Approval Decision
        add(
            TimelineStep(
                icon = if (isRejected) Icons.Default.Close else Icons.Default.CheckCircle,
                title = when {
                    isRejected -> "Rejected"
                    hasPassed(SubmissionStatus.APPROVED) -> "Approved"
                    else -> "Approval"
                },
                timestamp = if (isRejected || hasPassed(SubmissionStatus.APPROVED)) submission.reviewedAt else null,
                isCompleted = isRejected || hasPassed(SubmissionStatus.APPROVED),
                description = when {
                    isRejected && !submission.rejectionReason.isNullOrBlank() -> submission.rejectionReason!!
                    isRejected -> "Submission was rejected"
                    hasPassed(SubmissionStatus.APPROVED) -> "Submission approved for pickup"
                    else -> "Awaiting approval decision"
                }
            )
        )

        // Only continue with workflow if not rejected
        if (!isRejected) {
            // 5. Assigned
            add(
                TimelineStep(
                    icon = Icons.Default.Person,
                    title = if (hasPassed(SubmissionStatus.ASSIGNED)) "Assigned" else "Assignment",
                    timestamp = if (hasPassed(SubmissionStatus.ASSIGNED)) submission.assignedAt else null,
                    isCompleted = hasPassed(SubmissionStatus.ASSIGNED),
                    description = if (hasPassed(SubmissionStatus.ASSIGNED)) "Assigned to ${submission.agentName ?: "Agent"}" else "Waiting for agent assignment"
                )
            )

            // 6. Picked Up
            add(
                TimelineStep(
                    icon = Icons.Default.LocalShipping,
                    title = if (hasPassed(SubmissionStatus.PICKED_UP)) "Picked Up" else "Pickup",
                    timestamp = if (hasPassed(SubmissionStatus.PICKED_UP)) submission.pickedUpAt else null,
                    isCompleted = hasPassed(SubmissionStatus.PICKED_UP),
                    description = if (hasPassed(SubmissionStatus.PICKED_UP)) "Waste collected by agent" else "Waiting for agent pickup"
                )
            )

            // 7. Completed
            add(
                TimelineStep(
                    icon = Icons.Default.CheckCircle,
                    title = if (submission.status == SubmissionStatus.COMPLETED) "Completed" else "Completion",
                    timestamp = if (submission.status == SubmissionStatus.COMPLETED) submission.updatedAt else null,
                    isCompleted = submission.status == SubmissionStatus.COMPLETED,
                    description = if (submission.status == SubmissionStatus.COMPLETED) "Submission workflow completed" else "Awaiting completion"
                )
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        timelineSteps.forEachIndexed { index, step ->
            TimelineNode(
                step = step,
                isLast = index == timelineSteps.size - 1,
                showImages = index == 0 && !submission.images.isNullOrEmpty(),
                images = if (index == 0) submission.images?.map { it.url } ?: emptyList() else emptyList(),
                onViewImages = { imgIndex -> onViewImages(submission, imgIndex) }
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun TimelineNode(
    step: TimelineStep,
    isLast: Boolean = false,
    showImages: Boolean = false,
    images: List<String> = emptyList(),
    onViewImages: (Int) -> Unit = {}
) {
    val density = LocalDensity.current
    var contentHeight by remember { mutableStateOf(0.dp) }

    val nodeColor = if (step.isCompleted) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = if (step.isCompleted) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val strokeColor = if (step.isCompleted) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val nodeSize = 40.dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Timeline indicator
        Column(
            modifier = Modifier.width(nodeSize),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Node circle
            Box(
                modifier = Modifier
                    .size(nodeSize)
                    .clip(RoundedCornerShape(20.dp))
                    .background(nodeColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = step.icon,
                    contentDescription = null,
                    tint = if (step.isCompleted)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Connector line - show if not last, height matches content
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(contentHeight)
                        .background(strokeColor)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Timeline content
        Column(
            modifier = Modifier
                .weight(1f)
                .onSizeChanged { size ->
                    contentHeight = with(density) { size.height.toDp() * 0.75f }
                },
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = step.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            
            Text(
                text = step.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Show metadata if available
            step.metadata?.let { metadataList ->
                if (metadataList.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        metadataList.forEach { (type, amount) ->
                            Text(
                                text = "• $type: $amount",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            
            step.timestamp?.let { timestamp ->
                Text(
                    text = formatLastUpdated(timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            
            // Show images for the "Submitted" step
            if (showImages) {
                Spacer(modifier = Modifier.height(8.dp))
                ImageThumbnailGrid(
                    images = images,
                    onClick = onViewImages
                )
            }

            // Bottom spacing
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ImageThumbnailGrid(
    images: List<String>,
    onClick: (Int) -> Unit
) {
    val displayImages = images.take(4)
    val remainingCount = (images.size - 4).coerceAtLeast(0)
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        displayImages.forEachIndexed { index, imageUrl ->
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(onClick = { onClick(index) })
            ) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = "Image ${index + 1}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
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
                
                // Show "+X" overlay on the last thumbnail if there are more images
                if (index == 3 && remainingCount > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(onClick = { onClick(0) })
                            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+$remainingCount",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
private data class TimelineStep(
    val icon: ImageVector,
    val title: String,
    val timestamp: kotlin.time.Instant?,
    val isCompleted: Boolean,
    val description: String,
    val metadata: List<Pair<String, Int>>? = null
)
