package com.labguardian.feature.guidance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labguardian.core.data.GuidanceRepository
import com.labguardian.core.data.UserPreferencesRepository
import com.labguardian.core.network.WsEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GuidanceMessage(
    val type: String,
    val message: String,
    val sender: String,
    val timestamp: Long = System.currentTimeMillis(),
)

data class GuidanceUiState(
    val connected: Boolean = false,
    val messages: List<GuidanceMessage> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class GuidanceViewModel @Inject constructor(
    private val guidanceRepo: GuidanceRepository,
    private val prefs: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GuidanceUiState())
    val uiState: StateFlow<GuidanceUiState> = _uiState.asStateFlow()

    private var wsJob: Job? = null

    init {
        viewModelScope.launch {
            prefs.stationId.collect { id ->
                if (id.isNotBlank()) connectWs(id)
            }
        }
    }

    private fun connectWs(stationId: String) {
        wsJob?.cancel()
        wsJob = viewModelScope.launch {
            guidanceRepo.observeGuidance(stationId).collect { event ->
                when (event) {
                    is WsEvent.Connected -> _uiState.update { it.copy(connected = true, error = null) }
                    is WsEvent.Disconnected -> _uiState.update { it.copy(connected = false) }
                    is WsEvent.Error -> _uiState.update { it.copy(connected = false, error = event.throwable.message) }
                    is WsEvent.GuidanceReceived -> {
                        val msg = GuidanceMessage(
                            type = event.type,
                            message = event.message,
                            sender = event.sender,
                        )
                        _uiState.update { it.copy(messages = it.messages + msg) }
                    }
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(messages = emptyList()) }
    }

    override fun onCleared() {
        super.onCleared()
        guidanceRepo.disconnect()
    }
}
