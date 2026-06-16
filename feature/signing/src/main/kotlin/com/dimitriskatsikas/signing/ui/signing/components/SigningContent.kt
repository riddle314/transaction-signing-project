package com.dimitriskatsikas.signing.ui.signing.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.dimitriskatsikas.common.previews.Previews
import com.dimitriskatsikas.signing.ui.signing.SigningView
import com.dimitriskatsikas.signing.ui.signing.SigningView.SigningMechanism
import com.dimitriskatsikas.transactionsigning.core.designsystem.theme.TransactionSigningTheme
import com.dimitriskatsikas.transactionsigning.feature.signing.R

@Composable
internal fun SigningContent(
    state: SigningView.State,
    onUiAction: (SigningView.UiAction) -> Unit
) {
    BackHandler {
        onUiAction(SigningView.UiAction.BackPress)
    }
    Surface(modifier = Modifier.fillMaxSize()) {
        when (state) {
            is SigningView.State.Loading -> Loading()
            is SigningView.State.SigningLoading -> SigningLoading(signingMechanism = state.signingMechanism)
            SigningView.State.Error -> ErrorContent(onUiAction = onUiAction)
            is SigningView.State.Content -> MainContent(
                state = state,
                onUiAction = onUiAction
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    state: SigningView.State.Content,
    onUiAction: (SigningView.UiAction) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { onUiAction(SigningView.UiAction.BackPress) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.signing_back_content_description)
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            SigningOptions(
                innerPadding = innerPadding,
                state = state,
                onUiAction = onUiAction
            )
        }
    )
}

@Composable
private fun SigningOptions(
    innerPadding: PaddingValues,
    state: SigningView.State.Content,
    onUiAction: (SigningView.UiAction) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(
                R.string.signing_authorize_title,
                stringResource(getOperationTypeRes(state.operationType))
            ),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = stringResource(R.string.signing_options_description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        state.signingMechanisms.forEach { mechanism ->
            Button(
                onClick = { onUiAction(SigningView.UiAction.SignTransaction(mechanism)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = stringResource(
                        R.string.signing_button_text,
                        stringResource(getSigningMethodRes(mechanism.type))
                    ),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun SigningLoading(signingMechanism: SigningMechanism) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(
                R.string.signing_loading_text,
                stringResource(getSigningMethodRes(signingMechanism.type))
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun Loading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
    }
}

@Composable
private fun ErrorContent(onUiAction: (SigningView.UiAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.signing_error),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onUiAction(SigningView.UiAction.RetryLoading) }) {
            Text(
                text = stringResource(R.string.signing_retry),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

private fun getOperationTypeRes(operationType: SigningView.OperationType): Int = when (operationType) {
    SigningView.OperationType.WITHDRAWAL -> R.string.signing_withdrawal
    SigningView.OperationType.TRANSFER -> R.string.signing_transfer
    SigningView.OperationType.SWAP -> R.string.signing_swap
}

private fun getSigningMethodRes(type: SigningView.SigningMethodType): Int = when (type) {
    SigningView.SigningMethodType.PASSKEY -> R.string.signing_method_passkey
    SigningView.SigningMethodType.OTP -> R.string.signing_method_otp
    SigningView.SigningMethodType.EOA -> R.string.signing_method_eoa
}

@Previews
@Composable
private fun SigningContentPreview(
    @PreviewParameter(SigningPreviewStateProvider::class) state: SigningView.State
) {
    TransactionSigningTheme {
        SigningContent(
            state = state,
            onUiAction = {}
        )
    }
}
