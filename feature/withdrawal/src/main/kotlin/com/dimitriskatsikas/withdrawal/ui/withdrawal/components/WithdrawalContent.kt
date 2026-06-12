package com.dimitriskatsikas.withdrawal.ui.withdrawal.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dimitriskatsikas.common.previews.Previews
import com.dimitriskatsikas.withdrawal.ui.withdrawal.WithdrawalView

@Composable
internal fun WithdrawalContent(
    state: WithdrawalView.State,
    onUiAction: (WithdrawalView.UiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Withdrawal",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = state.amount,
                onValueChange = { onUiAction(WithdrawalView.UiAction.AmountChanged(it)) },
                label = { Text("Enter Amount") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onUiAction(WithdrawalView.UiAction.SubmitWithdrawal) },
                enabled = state.amount.isNotEmpty()
            ) {
                Text("Continue")
            }
        }
    }
}

@Previews
@Composable
private fun WithdrawalContentEmptyAmountPreview() {
    WithdrawalContent(
        state = WithdrawalView.State(amount = ""),
        onUiAction = {}
    )
}

@Previews
@Composable
private fun WithdrawalContentWithAmountPreview() {
    WithdrawalContent(
        state = WithdrawalView.State(amount = "100.00"),
        onUiAction = {}
    )
}

@Previews
@Composable
private fun WithdrawalContentLoadingPreview() {
    WithdrawalContent(
        state = WithdrawalView.State(
            amount = "100.00",
            isProcessing = true
        ),
        onUiAction = {}
    )
}
