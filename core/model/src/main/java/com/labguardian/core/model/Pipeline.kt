package com.labguardian.core.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** Pipeline 阶段 */
enum class PipelineStage { DETECT, MAPPING, TOPOLOGY, VALIDATE }

/** 任务状态 */
enum class JobStatus { PENDING, RUNNING, COMPLETED, FAILED }

/** Pipeline 提交请求 */
@JsonClass(generateAdapter = true)
data class PipelineRequest(
    @Json(name = "station_id") val stationId: String,
    @Json(name = "images_b64") val imagesB64: List<String>,
    val conf: Double = 0.25,
    val imgsz: Int = 1280,
    @Json(name = "reference_circuit") val referenceCircuit: Map<String, Any>? = null,
)

/** 单阶段执行结果 */
@JsonClass(generateAdapter = true)
data class StageResult(
    val stage: String = "",
    val status: String = "completed",
    @Json(name = "duration_ms") val durationMs: Double = 0.0,
    val data: Map<String, Any> = emptyMap(),
    val errors: List<String> = emptyList(),
)

/** 完整 Pipeline 结果 */
@JsonClass(generateAdapter = true)
data class PipelineResult(
    @Json(name = "job_id") val jobId: String = "",
    @Json(name = "station_id") val stationId: String = "",
    val status: String = "completed",
    val stages: List<StageResult> = emptyList(),
    @Json(name = "total_duration_ms") val totalDurationMs: Double = 0.0,
    @Json(name = "component_count") val componentCount: Int = 0,
    @Json(name = "net_count") val netCount: Int = 0,
    val progress: Double = 0.0,
    val similarity: Double = 0.0,
    val diagnostics: List<String> = emptyList(),
    @Json(name = "risk_level") val riskLevel: String = "safe",
    @Json(name = "risk_reasons") val riskReasons: List<String> = emptyList(),
    val report: String = "",
)

/** 任务状态查询响应 */
@JsonClass(generateAdapter = true)
data class JobStatusResponse(
    @Json(name = "job_id") val jobId: String = "",
    val status: String = "pending",
    @Json(name = "current_stage") val currentStage: String? = null,
    val result: PipelineResult? = null,
)

/** Pipeline 提交响应 */
@JsonClass(generateAdapter = true)
data class PipelineSubmitResponse(
    @Json(name = "job_id") val jobId: String,
    val status: String = "accepted",
)
