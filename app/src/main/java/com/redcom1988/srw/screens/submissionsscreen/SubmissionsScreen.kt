package com.redcom1988.srw.screens.submissionsscreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

object SubmissionsScreen : Screen {
    @Suppress("unused")
    private fun readResolve() = SubmissionsScreen

    @Composable
    override fun Content() {
        SubmissionsScreenContent()
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SubmissionsScreenContent(
    onClickSubmission: () -> Unit = {},
    onClickFilter: () -> Unit = {},
//    submissions: List<Submissions>
) {

}