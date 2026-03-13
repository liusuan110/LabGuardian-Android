package com.labguardian.feature.camera

import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.labguardian.core.data.PipelineRepository
import com.labguardian.core.data.UserPreferencesRepository
import com.labguardian.core.model.PipelineRequest
import com.labguardian.core.model.PipelineResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

data class CameraUiState(
    val capturedImages: List<String> = emptyList(), // base64 jpeg
    val isSubmitting: Boolean = false,
    val currentStage: String? = null,
    val result: PipelineResult? = null,
    val error: String? = null,
)

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val pipelineRepo: PipelineRepository,
    private val prefs: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    private var stationId: String = ""

    init {
        viewModelScope.launch {
            prefs.stationId.collect { stationId = it }
        }
    }

    fun addCapture(bitmap: Bitmap) {
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, bos)
        val b64 = Base64.encodeToString(bos.toByteArray(), Base64.NO_WRAP)
        _uiState.update { it.copy(capturedImages = it.capturedImages + b64) }
    }

    fun removeCapture(index: Int) {
        _uiState.update {
            it.copy(capturedImages = it.capturedImages.toMutableList().apply { removeAt(index) })
        }
    }

    fun clearCaptures() {
        _uiState.update { it.copy(capturedImages = emptyList(), result = null, error = null) }
    }

    fun submitPipeline() {
        val images = _uiState.value.capturedImages
        if (images.isEmpty()) return
        val sid = stationId.ifBlank { "S01" }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null, result = null, currentStage = "detect") }
            try {
                val result = pipelineRepo.runSync(
                    PipelineRequest(stationId = sid, imagesB64 = images),
                )
                _uiState.update {
                    it.copy(
                        result = result,
                        isSubmitting = false,
                        currentStage = null,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        currentStage = null,
                        error = e.message ?: "Pipeline 执行失败",
                    )
                }
            }
        }
    }
}
