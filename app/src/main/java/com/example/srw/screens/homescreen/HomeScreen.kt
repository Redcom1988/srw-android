package com.example.srw.screens.homescreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.example.srw.screens.homescreen.history.HistoryTab
import com.example.srw.screens.homescreen.home.HomeTab
import com.example.srw.screens.homescreen.profile.ProfileTab
import com.example.srw.screens.homescreen.statistics.StatisticsTab
import com.example.srw.screens.homescreen.upload.UploadTab
import soup.compose.material.motion.animation.materialFadeThroughIn
import soup.compose.material.motion.animation.materialFadeThroughOut

object HomeScreen : Screen {
    @Suppress("unused")
    private fun readResolve(): Any = HomeScreen

    @Suppress("ConstPropertyName")
    private const val TabFadeDuration = 200

    @Suppress("ConstPropertyName")
    private const val TabNavigatorKey = "HomeTabs"

    private val TABS = listOf(
        HomeTab,
        ProfileTab,
        UploadTab,
        StatisticsTab,
        HistoryTab
    )

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        TabNavigator(
            tab = HomeTab,
            key = TabNavigatorKey
        ) { tabNavigator ->

            BackHandler(tabNavigator.current != HomeTab) {
                tabNavigator.current = HomeTab
            }

            CompositionLocalProvider(LocalNavigator provides navigator) {
                Scaffold(
                    bottomBar = {
                        HomeNavigationBar(tabNavigator)
                    }
                ) { contentPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding)
                            .consumeWindowInsets(contentPadding)
                    ) {
                        AnimatedContent(
                            targetState = tabNavigator.current,
                            transitionSpec = {
                                materialFadeThroughIn(
                                    initialScale = 1f,
                                    durationMillis = TabFadeDuration
                                ) togetherWith materialFadeThroughOut(TabFadeDuration)
                            },
                            label = "tabContent"
                        ) {
                            tabNavigator.saveableState(key = "currentTab", it) {
                                it.Content()
                            }
                        }
                    }
                }
            }

        }
    }

    @Composable
    private fun HomeNavigationBar(tabNavigator: TabNavigator) {
        Box {
            NavigationBar {
                TABS.forEach { tab ->
                    if (tab == UploadTab) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        val selected = tabNavigator.current::class == tab::class
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    tabNavigator.current = tab
                                }
                            },
                            icon = {
                                tab.options.icon?.let { icon ->
                                    Icon(
                                        painter = icon,
                                        contentDescription = null
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = tab.options.title,
                                    style = MaterialTheme.typography.labelLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    tabNavigator.current = UploadTab
                },
                modifier = Modifier
                    .align(androidx.compose.ui.Alignment.TopCenter)
                    .offset(y = (-28).dp)
                    .size(56.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp
                )
            ) {
                Icon(
                    painter = rememberVectorPainter(Icons.Filled.Add),
                    contentDescription = "Upload",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}