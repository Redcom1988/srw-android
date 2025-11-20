package com.example.srw.screens.homescreen.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.srw.components.AppBar

data class TabContent(
    val title: String,
    val badgeNumber: Int? = null,
    val searchEnabled: Boolean = false,
    val actions: List<AppBar.AppBarAction> = listOf(),
    val content: @Composable (contentPadding: PaddingValues, snackbarHostState: SnackbarHostState) -> Unit,
)

object HomeTab : Tab {
    private fun readResolve(): Any = HomeTab

    override val options: TabOptions
        @Composable get() {
            val isSelected = LocalTabNavigator.current.current.key == key
            return TabOptions(
                index = 1u,
                title = "Home", // TODO String Resource
                icon = rememberVectorPainter(
                    when {
                        isSelected -> Icons.Filled.Home
                        else -> Icons.Outlined.Home
                    }
                )
            )
        }

    @Composable
    override fun Content() {

    }
}