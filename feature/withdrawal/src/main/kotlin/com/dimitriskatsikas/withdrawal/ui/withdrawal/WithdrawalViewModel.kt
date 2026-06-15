package com.dimitriskatsikas.withdrawal.ui.withdrawal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimitriskatsikas.common.dispatchers.AppDispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WithdrawalViewModel @Inject constructor(
    private val appDispatchers: AppDispatchers
) : ViewModel() {

    private val _state = MutableStateFlow(WithdrawalView.State.Content())
    val state = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WithdrawalView.State.Content()
    )

    private val _effect: Channel<WithdrawalView.Effect> = Channel(Channel.CONFLATED)
    val effect: Flow<WithdrawalView.Effect> = _effect.receiveAsFlow()

    fun onUiAction(action: WithdrawalView.UiAction) {

    }
}
