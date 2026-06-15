package com.dimitriskatsikas.signing.ui.signing.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.dimitriskatsikas.common.previews.Previews
import com.dimitriskatsikas.signing.ui.signing.SigningView
import com.dimitriskatsikas.transactionsigning.feature.signing.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SigningContent(
    state: SigningView.State,
    onUiAction: (SigningView.UiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.signing_title)) },
                navigationIcon = {
                    IconButton(onClick = { onUiAction(SigningView.UiAction.Back) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.signing_back_content_description)
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            MainContent(
                innerPadding = innerPadding,
                state = state,
                onUiAction = onUiAction
            )
        }
    )
}

@Composable
private fun MainContent(
    innerPadding: PaddingValues,
    state: SigningView.State,
    onUiAction: (SigningView.UiAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //TODO here add the content
    }
}

@Previews
@Composable
private fun SigningContentPreview(
    @PreviewParameter(SigningPreviewStateProvider::class) state: SigningView.State
) {
    SigningContent(
        state = state,
        onUiAction = {}
    )
}
