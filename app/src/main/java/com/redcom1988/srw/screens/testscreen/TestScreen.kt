package com.redcom1988.srw.screens.testscreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

object TestScreen: Screen {
    private fun readResolve(): Any = TestScreen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { TestScreenModel() }

        TestScreenContent()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TestScreenContent() {

}