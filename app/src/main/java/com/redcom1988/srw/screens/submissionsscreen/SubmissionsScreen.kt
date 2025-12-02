package com.redcom1988.srw.screens.submissionsscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.redcom1988.domain.submission.model.Submission
import com.redcom1988.srw.components.AppBar
import com.redcom1988.srw.components.SubmissionCard
import com.redcom1988.srw.components.SubmissionDetailBottomSheet

object SubmissionsScreen : Screen {
    @Suppress("unused")
    private fun readResolve(): Any = SubmissionsScreen

    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { SubmissionsScreenModel() }
        val navigator = LocalNavigator.currentOrThrow
        val lazyPagingItems = screenModel.submissionsPagingData.collectAsLazyPagingItems()

        SubmissionsScreenContent(
            lazyPagingItems = lazyPagingItems,
            onNavigateUp = { navigator.pop() }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SubmissionsScreenContent(
    lazyPagingItems: LazyPagingItems<Submission>,
    onNavigateUp: () -> Unit
) {
    var selectedSubmission by remember { mutableStateOf<Submission?>(null) }

    selectedSubmission?.let { submission ->
        SubmissionDetailBottomSheet(
            submission = submission,
            onDismiss = { selectedSubmission = null }
        )
    }

    val isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading

    Scaffold(
        topBar = {
            AppBar(
                title = "Submissions", // TODO String Resource
                navigateUp = onNavigateUp,
                actions = {
                    IconButton(
                        onClick = { lazyPagingItems.refresh() },
                        enabled = !isRefreshing
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isRefreshing -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = "Loading...", // TODO String Resource
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Error state
                lazyPagingItems.loadState.refresh is LoadState.Error -> {
                    val error = (lazyPagingItems.loadState.refresh as LoadState.Error).error
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error: ${error.message}", // TODO String Resource
                            color = MaterialTheme.colorScheme.error
                        )
                        TextButton(onClick = { lazyPagingItems.retry() }) {
                            Text("Retry") // TODO String Resource
                        }
                    }
                }

                // Empty state
                lazyPagingItems.loadState.refresh is LoadState.NotLoading && lazyPagingItems.itemCount == 0 -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No submissions found") // TODO String Resource
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            count = lazyPagingItems.itemCount,
                            key = lazyPagingItems.itemKey { it.id }
                        ) { index ->
                            val submission = lazyPagingItems[index]
                            if (submission != null) {
                                SubmissionCard(
                                    submission = submission,
                                    onClick = { selectedSubmission = submission }
                                )
                            }
                        }

                        if (lazyPagingItems.loadState.append is LoadState.Loading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        // Error indicator when loading more fails
                        if (lazyPagingItems.loadState.append is LoadState.Error) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    TextButton(onClick = { lazyPagingItems.retry() }) {
                                        Text("Load more failed. Retry") // TODO String Resource
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



