package com.labguardian.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labguardian.core.data.ClassroomRepository
import com.labguardian.core.data.UserPreferencesRepository
import com.labguardian.core.model.ClassroomStats
import com.labguardian.core.model.RankingEntry
import com.labguardian.core.model.StationHeartbeat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val stationId: String = "",
    val studentName: String = "",
    val myStation: StationHeartbeat? = null,
    val stats: ClassroomStats? = null,
    val ranking: List<RankingEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val classroomRepo: ClassroomRepository,
    private val prefs: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            prefs.stationId.collect { id ->
                _uiState.update { it.copy(stationId = id) }
                if (id.isNotBlank()) refresh()
            }
        }
        viewModelScope.launch {
            prefs.studentName.collect { name ->
                _uiState.update { it.copy(studentName = name) }
            }
        }
    }

    fun setStationId(id: String) {
        viewModelScope.launch { prefs.setStationId(id) }
    }

    fun setStudentName(name: String) {
        viewModelScope.launch { prefs.setStudentName(name) }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val stationId = _uiState.value.stationId
                val stats = classroomRepo.getStats()
                val ranking = classroomRepo.getRanking()
                val myStation = if (stationId.isNotBlank()) {
                    runCatching { classroomRepo.getStation(stationId) }.getOrNull()
                } else null
                _uiState.update {
                    it.copy(
                        stats = stats,
                        ranking = ranking,
                        myStation = myStation,
                        isLoading = false,
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
