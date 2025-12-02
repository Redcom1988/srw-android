package com.redcom1988.srw.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.redcom1988.domain.submission.model.Submission
import com.redcom1988.srw.util.formatLastUpdated
import com.redcom1988.srw.util.isPreReviewStatus
import com.redcom1988.srw.util.toReadableStatus
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun SubmissionDetailBottomSheet(
    submission: Submission,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusChip(status = submission.status)
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${submission.imageCount} images",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider()

            // Points earned
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

            // Points disclaimer
            if (submission.status.isPreReviewStatus()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Point count is subject to change after manual review. AI processing may misidentify items.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.weight(1f)
                    )
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
            if (submission.status == "REJECTED" && !submission.rejectionReason.isNullOrBlank()) {
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

            SubmissionTimeline(submission = submission)
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val (backgroundColor, textColor) = when (status) {
        "PENDING" -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        "ML_PROCESSING" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        "AWAITING_REVIEW" -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        "APPROVED" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
        "REJECTED" -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.error
        "ASSIGNED" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        "PICKED_UP" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
        "COMPLETED" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
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
) {
    val timelineSteps = buildList {
        val hasProcessed = submission.processedAt?.toEpochMilliseconds()?.let { it > 0 } == true
        val hasReviewed = submission.reviewedAt?.toEpochMilliseconds()?.let { it > 0 } == true
        val hasAssigned = submission.assignedAt?.toEpochMilliseconds()?.let { it > 0 } == true
        val hasPickedUp = submission.pickedUpAt?.toEpochMilliseconds()?.let { it > 0 } == true

        // Determine workflow state
        val isMlProcessing = submission.status == "ML_PROCESSING"
        val isAwaitingReview = submission.status == "AWAITING_REVIEW"
        val isApproved = submission.status == "APPROVED"
        val isRejected = submission.status == "REJECTED"
        val isAssigned = submission.status == "ASSIGNED"
        val isPickedUp = submission.status == "PICKED_UP"
        val isCompleted = submission.status == "COMPLETED"

        add(
            TimelineStep(
                icon = Icons.Default.Add,
                title = "Submitted", // TODO String Resource
                timestamp = submission.createdAt,
                isCompleted = true,
                description = "Waste submission created" // TODO String Resource
            )
        )

        if (isMlProcessing || hasProcessed || isAwaitingReview || isApproved || isRejected || isAssigned || isPickedUp || isCompleted) {
            add(
                TimelineStep(
                    icon = Icons.Default.AutoAwesome,
                    title = if (isMlProcessing) "Processing" else "Processed", // TODO String Resource
                    timestamp = if (hasProcessed) submission.processedAt else null,
                    isCompleted = hasProcessed || isAwaitingReview || isApproved || isRejected || isAssigned || isPickedUp || isCompleted,
                    description = if (isMlProcessing) "AI is processing images" else "AI processed images" // TODO String Resource
                )
            )
        } else {
            // Show as pending if still in PENDING state
            add(
                TimelineStep(
                    icon = Icons.Default.AutoAwesome,
                    title = "ML Processing", // TODO String Resource
                    timestamp = null,
                    isCompleted = false,
                    description = "Waiting for AI to process images" // TODO String Resource
                )
            )
        }

        if (isAwaitingReview || isApproved || isRejected || isAssigned || isPickedUp || isCompleted) {
            add(
                TimelineStep(
                    icon = Icons.Default.Search,
                    title = if (isAwaitingReview) "Awaiting Review" else "Reviewed", // TODO String Resource
                    timestamp = if (hasReviewed) submission.reviewedAt else null,
                    isCompleted = hasReviewed || isApproved || isRejected || isAssigned || isPickedUp || isCompleted,
                    description = if (isAwaitingReview) "Waiting for admin review" else "Admin reviewed submission" // TODO String Resource
                )
            )
        } else if (hasProcessed || isMlProcessing) {
            add(
                TimelineStep(
                    icon = Icons.Default.Search,
                    title = "Awaiting Review", // TODO String Resource
                    timestamp = null,
                    isCompleted = false,
                    description = "Pending admin review" // TODO String Resource
                )
            )
        }

        if (isApproved || isAssigned || isPickedUp || isCompleted) {
            add(
                TimelineStep(
                    icon = Icons.Default.CheckCircle,
                    title = "Approved", // TODO String Resource
                    timestamp = submission.reviewedAt,
                    isCompleted = true,
                    description = "Submission approved for pickup" // TODO String Resource
                )
            )
        } else if (isRejected) {
            add(
                TimelineStep(
                    icon = Icons.Default.Close,
                    title = "Rejected", // TODO String Resource
                    timestamp = submission.reviewedAt,
                    isCompleted = true,
                    description = "Submission was rejected (terminal state)" // TODO String Resource
                )
            )
        } else if (isAwaitingReview) {
            add(
                TimelineStep(
                    icon = Icons.Default.CheckCircle,
                    title = "Approval", // TODO String Resource
                    timestamp = null,
                    isCompleted = false,
                    description = "Awaiting approval decision" // TODO String Resource
                )
            )
        }

        if (!isRejected) {
            if (isAssigned || isPickedUp || isCompleted) {
                add(
                    TimelineStep(
                        icon = Icons.Default.Person,
                        title = "Assigned", // TODO String Resource
                        timestamp = submission.assignedAt,
                        isCompleted = hasAssigned || isPickedUp || isCompleted,
                        description = "Assigned to ${submission.agentName ?: "Unknown Agent"}" // TODO String Resource
                    )
                )
            } else if (isApproved) {
                add(
                    TimelineStep(
                        icon = Icons.Default.Person,
                        title = "Assignment", // TODO String Resource
                        timestamp = null,
                        isCompleted = false,
                        description = "Waiting for agent assignment" // TODO String Resource
                    )
                )
            }

            // 6. Picked Up
            if (isPickedUp || isCompleted) {
                add(
                    TimelineStep(
                        icon = Icons.Default.LocalShipping,
                        title = "Picked Up", // TODO String Resource
                        timestamp = submission.pickedUpAt,
                        isCompleted = hasPickedUp || isCompleted,
                        description = "Waste collected by agent" // TODO String Resource
                    )
                )
            } else if (isAssigned) {
                add(
                    TimelineStep(
                        icon = Icons.Default.LocalShipping,
                        title = "Pickup", // TODO String Resource
                        timestamp = null,
                        isCompleted = false,
                        description = "Waiting for agent pickup" // TODO String Resource
                    )
                )
            }

            // 7. Completed
            if (isCompleted) {
                add(
                    TimelineStep(
                        icon = Icons.Default.CheckCircle,
                        title = "Completed", // TODO String Resource
                        timestamp = submission.updatedAt,
                        isCompleted = true,
                        description = "Submission workflow completed (terminal state)" // TODO String Resource
                    )
                )
            } else if (isPickedUp) {
                add(
                    TimelineStep(
                        icon = Icons.Default.CheckCircle,
                        title = "Completion", // TODO String Resource
                        timestamp = null,
                        isCompleted = false,
                        description = "Awaiting completion" // TODO String Resource
                    )
                )
            }
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

