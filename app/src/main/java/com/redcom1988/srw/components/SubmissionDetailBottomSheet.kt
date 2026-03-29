package com.redcom1988.srw.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.redcom1988.domain.submission.model.Submission
import com.redcom1988.domain.submission.model.SubmissionStatus
import com.redcom1988.srw.util.formatLastUpdated
import com.redcom1988.srw.util.isPreReviewStatus
import com.redcom1988.srw.util.toReadableStatus
import kotlin.time.ExperimentalTime

/**
 * @deprecated Use SubmissionDetailScreen instead. This bottom sheet approach causes navigation issues
 * when viewing images, as the bottom sheet is dismissed when navigating to the image viewer.
 * SubmissionDetailScreen preserves state when navigating to images and provides a better UX.
 */
@Deprecated(
    message = "Use SubmissionDetailScreen instead for better navigation handling",
    replaceWith = ReplaceWith(
        "SubmissionDetailScreen(submission)",
        "com.redcom1988.srw.screens.submissiondetail.SubmissionDetailScreen"
    )
)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun SubmissionDetailBottomSheet(
    submission: Submission,
    onDismiss: () -> Unit,
    onViewImages: ((Submission) -> Unit)? = null
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
//            Column(
//                modifier = Modifier.fillMaxWidth(),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(8.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    StatusChip(status = submission.status)
//                    Text(
//                        text = "•",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                    Text(
//                        text = "${submission.images?.size} images",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//            }
//
//            HorizontalDivider()

            // Points earned - only show when COMPLETED
            if (submission.status == SubmissionStatus.COMPLETED) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
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

            // Points disclaimer
//            if (submission.status.isPreReviewStatus()) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 4.dp),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Info,
//                        contentDescription = null,
//                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
//                        modifier = Modifier.size(16.dp)
//                    )
//                    Text(
//                        text = "Point count is subject to change after manual review. AI processing may misidentify items.",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
//                        modifier = Modifier.weight(1f)
//                    )
//                }
//            }

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

            HorizontalDivider()

            // Journey Timeline
            Text(
                text = "Submission Journey",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            SubmissionTimeline(
                submission = submission,
                onViewImages = onViewImages
            )
        }
    }
}

@Composable
private fun StatusChip(status: SubmissionStatus) {
    val (backgroundColor, textColor) = when (status) {
        SubmissionStatus.PENDING -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        SubmissionStatus.ML_PROCESSING -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        SubmissionStatus.AWAITING_REVIEW -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        SubmissionStatus.APPROVED -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
        SubmissionStatus.REJECTED -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.error
        SubmissionStatus.ASSIGNED -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        SubmissionStatus.PICKED_UP -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
        SubmissionStatus.COMPLETED -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor
    ) {
        Text(
            text = status.toReadableStatus(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
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
    submittedText: String = "Submitted",
    onViewImages: ((Submission) -> Unit)? = null
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
                }
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
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        timelineSteps.forEachIndexed { index, step ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Timeline indicator
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(40.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (step.isCompleted)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            ),
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

                    // Connector line
                    if (index < timelineSteps.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(60.dp)
                                .background(
                                    if (step.isCompleted)
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                )
                        )
                    }
                }

                // Timeline content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = if (index < timelineSteps.size - 1) 24.dp else 0.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (step.isCompleted)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = step.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    step.timestamp?.let { timestamp ->
                        Text(
                            text = formatLastUpdated(timestamp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                    
                    // Show images for the "Submitted" step
                    if (index == 0 && !submission.images.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val imageList = submission.images!!.map { it.url }
                        ImageThumbnailGrid(
                            images = imageList,
                            onClick = if (onViewImages != null) {
                                { onViewImages(submission) }
                            } else null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageThumbnailGrid(
    images: List<String>,
    onClick: (() -> Unit)?
) {
    val displayImages = images.take(4)
    val remainingCount = (images.size - 4).coerceAtLeast(0)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        displayImages.forEachIndexed { index, imageUrl ->
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Image ${index + 1}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Show "+X" overlay on the last thumbnail if there are more images
                if (index == 3 && remainingCount > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+$remainingCount",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
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
    val description: String
)
