package com.redcom1988.srw.screens.homescreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.redcom1988.domain.submission.model.Submission
import com.redcom1988.srw.components.AppBar
import com.redcom1988.srw.components.SubmissionCard
import com.redcom1988.srw.components.SubmissionDetailBottomSheet
import com.redcom1988.srw.screens.camerascreen.CameraScreen
import com.redcom1988.srw.screens.loginscreen.LoginScreen
import com.redcom1988.srw.screens.pointsscreen.PointsScreen
import com.redcom1988.srw.screens.submissionsscreen.SubmissionsScreen

object HomeScreen : Screen {
    @Suppress("unused")
    private fun readResolve(): Any = HomeScreen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = androidx.compose.ui.platform.LocalContext.current
        val screenModel = rememberScreenModel { HomeScreenModel() }
        val logoutState by screenModel.logoutState.collectAsState()
        val profileState by screenModel.profileState.collectAsState()
        val submissionsState by screenModel.submissionsState.collectAsState()

        LaunchedEffect(Unit) {
            screenModel.loadProfile()
            screenModel.loadRecentSubmissions()
        }

        LaunchedEffect(logoutState) {
            when (val state = logoutState) {
                is HomeScreenModel.LogoutState.Success -> {
                    screenModel.resetState()
                    navigator.replaceAll(LoginScreen)
                }
                is HomeScreenModel.LogoutState.Error -> {
                    Toast.makeText(
                        context,
                        "Logout failed: ${state.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    screenModel.resetState()
                }
                else -> {}
            }
        }

        LaunchedEffect(profileState) {
            if (profileState is HomeScreenModel.ProfileState.Error) {
                val errorMessage = (profileState as HomeScreenModel.ProfileState.Error).message
                Toast.makeText(
                    context,
                    "Failed to load profile: $errorMessage",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        LaunchedEffect(submissionsState) {
            if (submissionsState is HomeScreenModel.SubmissionsState.Error) {
                val errorMessage = (submissionsState as HomeScreenModel.SubmissionsState.Error).message
                Toast.makeText(
                    context,
                    "Failed to load submissions: $errorMessage",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        HomeScreenContent(
            profileState = profileState,
            submissionsState = submissionsState,
            onClickLogout = screenModel::handleLogout,
            onClickViewAll = { navigator.push(SubmissionsScreen) },
            onClickLedger = { navigator.push(PointsScreen) },
            onClickUpload = { navigator.push(CameraScreen) },
            onRefresh = {
                screenModel.loadProfile()
                screenModel.loadRecentSubmissions()
            }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun HomeScreenContent(
    profileState: HomeScreenModel.ProfileState = HomeScreenModel.ProfileState.Idle,
    submissionsState: HomeScreenModel.SubmissionsState = HomeScreenModel.SubmissionsState.Loading,
    onClickLogout: () -> Unit = {},
    onClickLedger: () -> Unit = {},
    onClickViewAll: () -> Unit = {},
    onClickUpload: () -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    var selectedSubmission by remember { mutableStateOf<Submission?>(null) }

    selectedSubmission?.let { submission ->
        SubmissionDetailBottomSheet(
            submission = submission,
            onDismiss = { selectedSubmission = null }
        )
    }

    val isLoading = profileState is HomeScreenModel.ProfileState.Loading ||
                    submissionsState is HomeScreenModel.SubmissionsState.Loading

    Scaffold(
        topBar = {
            AppBar(
                titleContent = {
                    Text(
                        text = "SRW", // TODO String Resource
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }, // TODO change to app icon
                actions = {
                    IconButton(
                        onClick = onRefresh,
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = onClickLogout,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null
                        )
                    }
                },
            )
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Loading...", // TODO String Resource
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (profileState) {
                    is HomeScreenModel.ProfileState.Success -> {
                        BalanceCard(
                            onClickLedger = onClickLedger,
                            name = profileState.client.name,
                            points = profileState.client.totalPoints.toString(),
                        )
                    }
                    is HomeScreenModel.ProfileState.Error -> {
                        // Show error card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Failed to load profile", // TODO String Resource
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = profileState.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                    else -> {}
                }

                    RecentSubmissionsSection(
                        submissionsState = submissionsState,
                        onClickViewAll = onClickViewAll,
                        onSubmissionClick = { submission ->
                            selectedSubmission = submission
                        }
                    )
                    }

                    ExtendedFloatingActionButton(
                        text = {
                            Text(
                                text = "Create", // TODO String Resource
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium,
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null
                            )
                        },
                        onClick = onClickUpload,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun BalanceCard(
    onClickLedger: () -> Unit,
    name: String,
    points: String,
) {
    Card(
        onClick = onClickLedger,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Account Holder", // TODO String Resource
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column {
                    Text(
                        text = "Available Balance", // TODO String Resource
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = points,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = " points", // TODO String Resource
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun RecentSubmissionsSection(
    submissionsState: HomeScreenModel.SubmissionsState,
    onClickViewAll: () -> Unit = {},
    onSubmissionClick: (Submission) -> Unit = {}
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Submissions", // TODO String Resource
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            TextButton(
                onClick = onClickViewAll,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "View All", // TODO String Resource
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (submissionsState) {
            is HomeScreenModel.SubmissionsState.Loading -> {}

            is HomeScreenModel.SubmissionsState.Success -> {
                if (submissionsState.submissions.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No submissions yet", // TODO String Resource
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Your recent submissions will appear here", // TODO String Resource
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        submissionsState.submissions.forEach { submission ->
                            SubmissionCard(
                                submission = submission,
                                onClick = { onSubmissionClick(submission) }
                            )
                        }
                    }
                }
            }

            is HomeScreenModel.SubmissionsState.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Failed to load submissions", // TODO String Resource
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = submissionsState.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}