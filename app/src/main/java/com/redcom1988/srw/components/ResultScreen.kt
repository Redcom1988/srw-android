package com.redcom1988.srw.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import kotlin.also

abstract class ResultScreen: Screen {
    var arguments: HashMap<String, Any?> = kotlin.collections.HashMap()

    @Composable
    final override fun Content() {
        val currentArguments = remember(arguments) {
            kotlin.collections.HashMap(arguments).also {
                arguments.clear() // Clear after capturing
            }
        }
        Content(currentArguments)
    }

    @Composable
    abstract fun Content(arguments: Map<String, Any?>)
}