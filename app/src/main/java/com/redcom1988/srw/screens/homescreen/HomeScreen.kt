package com.redcom1988.srw.screens.homescreen

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.redcom1988.core.util.inject
import com.redcom1988.domain.preference.ApplicationPreference
import com.redcom1988.domain.submission.model.Submission
import com.redcom1988.srw.BuildConfig
import com.redcom1988.srw.R
import com.redcom1988.srw.components.AppBar
import com.redcom1988.srw.components.SubmissionCard
import com.redcom1988.srw.screens.camerascreen.CameraScreen
import com.redcom1988.srw.screens.locationpicker.LocationPickerScreen
import com.redcom1988.srw.screens.loginscreen.LoginScreen
import com.redcom1988.srw.screens.pointsscreen.PointsScreen
import com.redcom1988.srw.screens.submissiondetail.SubmissionDetailScreen
import com.redcom1988.srw.screens.submissionsscreen.SubmissionsScreen
import com.redcom1988.srw.util.ErrorMessageUtil

object HomeScreen : Screen {
    @Suppress("unused")
    private fun readResolve(): Any = HomeScreen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { HomeScreenModel() }
        val applicationPreference: ApplicationPreference = inject()
        val uiState by screenModel.uiState.collectAsState()

        HomeScreenContent(
            uiState = uiState,
            onClickLogout = { screenModel.handleLogout { navigator.replaceAll(LoginScreen) } },
            onClickViewAll = { navigator.push(SubmissionsScreen) },
            onClickUpload = {
                if (!applicationPreference.onboardingComplete().get()) {
                    navigator.push(LocationPickerScreen())
                } else {
                    navigator.push(CameraScreen)
                }
            },
            onRefresh = screenModel::loadAll
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun HomeScreenContent(
    uiState: HomeScreenModel.UiState = HomeScreenModel.UiState.Loading,
    onClickLogout: () -> Unit = {},
    onClickViewAll: () -> Unit = {},
    onClickUpload: () -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showClientOptionsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val navigator = LocalNavigator.currentOrThrow
    val isRefreshing = uiState is HomeScreenModel.UiState.Loading
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        if (uiState is HomeScreenModel.UiState.Error) {
            snackbarHostState.showSnackbar(
                ErrorMessageUtil.translate(uiState.generalError ?: "Something went wrong")
            )
        }
    }

    Scaffold(
        topBar = {
            AppBar(
                titleContent = {
                    Row {
                        Icon(
                            painter = painterResource(R.drawable.app_logo_alt),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SRW", // TODO String Resource
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }, // TODO change to app icon
                actions = {
                    IconButton(
                        onClick = { showLogoutDialog = true },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null
                        )
                    }
                },
                shadowElevation = 4.dp
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    ) { contentPadding ->
        if (showClientOptionsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showClientOptionsSheet = false },
                sheetState = sheetState
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("Points History") },
                        supportingContent = { Text("View your points transaction history") },
                        leadingContent = {
                            Icon(Icons.Default.History, contentDescription = null)
                        },
                        modifier = Modifier.clickable {
                            showClientOptionsSheet = false
                            navigator.push(PointsScreen)
                        }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("Change Address") },
                        supportingContent = { Text("Update your pickup location") },
                        leadingContent = {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                        },
                        modifier = Modifier.clickable {
                            showClientOptionsSheet = false
                            navigator.push(LocationPickerScreen())
                        }
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = {
                    Text(
                        text = "Logout",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to log out?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                            onClickLogout()
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showLogoutDialog = false }
                    ) {
                        Text("No")
                    }
                }
            )
        }

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            when (uiState) {
                is HomeScreenModel.UiState.Loading -> {}
                is HomeScreenModel.UiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(
                                modifier = Modifier.padding(12.dp),
                                onClick = onRefresh
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Refresh")
                            }
                            if (BuildConfig.DEBUG) {
                                uiState.profileError?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                uiState.submissionsError?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                is HomeScreenModel.UiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BalanceCard(
                            onClick = { showClientOptionsSheet = true },
                            name = uiState.client.name,
                            points = uiState.client.totalPoints.toString(),
                            address = uiState.client.address,
                        )

                        RecentSubmissionsSection(
                            submissions = uiState.submissions,
                            onClickViewAll = onClickViewAll,
                            navigator = navigator
                        )
                    }

                    ExtendedFloatingActionButton(
                        text = {
                            Text(
                                text = "Create",
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
    onClick: () -> Unit,
    name: String,
    points: String,
    address: String,
) {
    val displayAddress = address.ifBlank { "No address set" }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Account Holder", // TODO String Resource
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = displayAddress,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column {
                    Text(
                        text = "Available Balance", // TODO String Resource
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = " points", // TODO String Resource
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
    submissions: List<Submission>,
    onClickViewAll: () -> Unit = {},
    navigator: Navigator
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

        if (submissions.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
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
                submissions.forEach { submission ->
                    SubmissionCard(
                        submission = submission,
                        onClick = { navigator.push(SubmissionDetailScreen(submission)) }
                    )
                }
            }
        }
    }
}