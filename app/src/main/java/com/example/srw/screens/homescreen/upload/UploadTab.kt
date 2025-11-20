package com.example.srw.screens.homescreen.upload

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object UploadTab : Tab {
    private fun readResolve(): Any = UploadTab

    override val options: TabOptions
        @Composable get() {
            return TabOptions(
                index = 2u,
                title = "Upload",
                icon = rememberVectorPainter(Icons.Filled.Add)
            )
        }

    @Composable
    override fun Content() {
        UploadScreen()
    }
}