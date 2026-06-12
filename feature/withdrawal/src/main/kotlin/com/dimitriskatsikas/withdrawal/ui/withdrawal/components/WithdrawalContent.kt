package com.dimitriskatsikas.withdrawal.ui.withdrawal.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.dimitriskatsikas.common.previews.Previews
import com.dimitriskatsikas.transactionsigning.feature.withdrawal.R
import com.dimitriskatsikas.withdrawal.ui.withdrawal.WithdrawalView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WithdrawalContent(
    state: WithdrawalView.State,
    onUiAction: (WithdrawalView.UiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.withdrawal_title)) },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AmountInputField(
                state = state,
                onUiAction = onUiAction,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            ContinueButton(
                ctaState = state.ctaState,
                onUiAction = onUiAction,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AmountInputField(
    state: WithdrawalView.State,
    onUiAction: (WithdrawalView.UiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = state.amount,
        onValueChange = { onUiAction(WithdrawalView.UiAction.AmountChanged(it)) },
        label = { Text(stringResource(R.string.withdrawal_amount_label)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                onUiAction(WithdrawalView.UiAction.SubmitWithdrawal)
            }
        ),
        modifier = modifier
    )
}

@Composable
private fun ContinueButton(
    ctaState: WithdrawalView.CtaState,
    onUiAction: (WithdrawalView.UiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonHeight = 50.dp
    val keyboardController = LocalSoftwareKeyboardController.current
    when (ctaState) {
        WithdrawalView.CtaState.Disabled -> Button(
            modifier = modifier
                .height(buttonHeight),
            onClick = { },
            enabled = false
        ) {
            Text(text = stringResource(R.string.withdrawal_continue_button))
        }

        WithdrawalView.CtaState.Enabled -> Button(
            modifier = modifier
                .height(buttonHeight),
            onClick = {
                keyboardController?.hide()
                onUiAction(WithdrawalView.UiAction.SubmitWithdrawal)
            }
        ) {
            Text(text = stringResource(R.string.withdrawal_continue_button))
        }

        WithdrawalView.CtaState.Loading -> Button(
            modifier = modifier
                .height(buttonHeight),
            onClick = { }
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 3.dp
            )
        }
    }
}

@Previews
@Composable
private fun WithdrawalContentPreview(
    @PreviewParameter(WithdrawalPreviewStateProvider::class) state: WithdrawalView.State
) {
    WithdrawalContent(
        state = state,
        onUiAction = {}
    )
}
