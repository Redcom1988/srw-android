package com.redcom1988.srw.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Universal dialog component for consistent dialogs across the app
 *
 * @param title Dialog title
 * @param message Dialog message/content
 * @param icon Optional icon to display at the top
 * @param iconTint Optional tint color for the icon
 * @param confirmText Text for the confirm button
 * @param confirmColor Color for the confirm button text
 * @param dismissText Text for the dismiss button (null to hide)
 * @param onConfirm Callback when confirm button is clicked
 * @param onDismiss Callback when dismiss button is clicked or dialog is dismissed
 */
@Composable
fun UniversalDialog(
    title: String,
    message: String,
    icon: ImageVector? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    confirmText: String = "OK",
    confirmColor: Color = MaterialTheme.colorScheme.primary,
    dismissText: String? = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = icon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = iconTint
                )
            }
        },
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                content = {
                    Text(
                        text = confirmText,
                        color = confirmColor
                    )
                }
            )
        },
        dismissButton = dismissText?.let {
            {
                TextButton(
                    onClick = onDismiss,
                    content = { Text(text = it) }
                )
            }
        }
    )
}
