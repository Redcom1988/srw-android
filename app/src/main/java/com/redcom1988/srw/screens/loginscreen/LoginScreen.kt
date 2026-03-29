package com.redcom1988.srw.screens.loginscreen

import android.app.Activity
import android.nfc.NfcAdapter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.redcom1988.core.util.extractNfcNumber
import com.redcom1988.srw.screens.homescreen.HomeScreen
import com.redcom1988.srw.screens.locationpicker.LocationPickerScreen
import kotlinx.coroutines.launch

object LoginScreen : Screen {
    @Suppress("unused")
    private fun readResolve(): Any = LoginScreen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { LoginScreenModel() }
        val state by screenModel.state.collectAsState()

        LoginScreenContent(
            state = state,
            onHandleNfcTag = screenModel::handleNfcTag,
            onResetState = screenModel::resetState,
            onLoginSuccess = {
                navigator.replaceAll(HomeScreen)
            },
            onNavigateToOnboarding = {
                navigator.replaceAll(LocationPickerScreen)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginScreenContent(
    state: LoginScreenModel.LoginState,
    onHandleNfcTag: (String) -> Unit,
    onResetState: () -> Unit,
    onLoginSuccess: () -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val nfcAdapter = remember { NfcAdapter.getDefaultAdapter(context) }

    DisposableEffect(activity) {
        val nfcListener = NfcAdapter.ReaderCallback { tag ->
            val nfcNumber = extractNfcNumber(tag)
            if (nfcNumber.isNotEmpty()) {
                onHandleNfcTag(nfcNumber)
            } else {
                onResetState()
            }
        }

        activity?.let {
            nfcAdapter?.enableReaderMode(
                it,
                nfcListener,
                NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_NFC_B or
                        NfcAdapter.FLAG_READER_NFC_F or
                        NfcAdapter.FLAG_READER_NFC_V or
                        NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                null
            )
        }

        onDispose {
            activity?.let {
                nfcAdapter?.disableReaderMode(it)
            }
        }
    }

    LaunchedEffect(state) {
        when (state) {
            is LoginScreenModel.LoginState.Success -> {
                onLoginSuccess()
            }
            is LoginScreenModel.LoginState.NeedsOnboarding -> {
                onNavigateToOnboarding()
            }
            is LoginScreenModel.LoginState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(state.message)
                }
                onResetState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // App logo/title
                Text(
                    text = "SMART RECYCLE WASTE", // TODO String Resource
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(48.dp))

                // NFC Card animation/icon
                Card(
                    modifier = Modifier.size(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Nfc,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Instructions
                when (state) {
                    is LoginScreenModel.LoginState.Idle -> {
                        Text(
                            text = "Tap Your NFC Card", // TODO String Resource
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Hold your NFC card near the back\nof your device to login", // TODO String Resource
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    is LoginScreenModel.LoginState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Authenticating...", // TODO String Resource
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }

                    is LoginScreenModel.LoginState.Error,
                    is LoginScreenModel.LoginState.Success,
                    is LoginScreenModel.LoginState.NeedsOnboarding -> {
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // NFC status info
                AnimatedVisibility(
                    visible = state is LoginScreenModel.LoginState.Idle,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (nfcAdapter == null) {
                                        "⚠️ NFC Not Available" // TODO String Resource
                                    } else if (!nfcAdapter.isEnabled) {
                                        "⚠️ Please Enable NFC in Settings" // TODO String Resource
                                    } else {
                                        "✓ NFC Ready" // TODO String Resource
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Debug mock login button
                        Button(
                            onClick = { onHandleNfcTag("client") }
                        ) {
                            Text("Mock Login (Debug)")
                        }
                    }
                }
            }
        }
    }
}