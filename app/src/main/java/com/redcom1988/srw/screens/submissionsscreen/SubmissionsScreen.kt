package com.redcom1988.srw.screens.submissionsscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
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
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.redcom1988.domain.submission.model.Submission
import com.redcom1988.srw.components.AppBar
import com.redcom1988.srw.components.SubmissionCard
import com.redcom1988.srw.screens.submissiondetail.SubmissionDetailScreen

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
            onNavigateUp = { navigator.pop() },
            navigator = navigator
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SubmissionsScreenContent(
    lazyPagingItems: LazyPagingItems<Submission>,
    onNavigateUp: () -> Unit,
    navigator: Navigator
) {
    val isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading

    Scaffold(
        topBar = {
            AppBar(
                title = "Submissions", // TODO String Resource
                navigateUp = onNavigateUp,
                shadowElevation = 4.dp
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { lazyPagingItems.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when {
                    // Error state (only show when not refreshing)
                    lazyPagingItems.loadState.refresh is LoadState.Error && !isRefreshing -> {
                        item {
                            val error = (lazyPagingItems.loadState.refresh as LoadState.Error).error
                            Column(
                                modifier = Modifier.fillParentMaxSize(),
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
                    }

                    // Empty state
                    lazyPagingItems.loadState.refresh is LoadState.NotLoading && lazyPagingItems.itemCount == 0 -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillParentMaxSize()
                                    .wrapContentHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Receipt,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No submissions yet",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "Your submissions will appear here",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }

                    else -> {
                        items(
                            count = lazyPagingItems.itemCount,
                            key = lazyPagingItems.itemKey { it.id }
                        ) { index ->
                            val submission = lazyPagingItems[index]
                            if (submission != null) {
                                SubmissionCard(
                                    submission = submission,
                                    onClick = { navigator.push(SubmissionDetailScreen(submission)) }
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
