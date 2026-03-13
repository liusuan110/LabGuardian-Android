package com.labguardian.core.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 学生工位心跳包 — 对应 Server schemas.classroom.StationHeartbeat
 *
 * 每 2 秒由 Android 端 POST 到 LabGuardian-Server
 */
@JsonClass(generateAdapter = true)
data class StationHeartbeat(
    @Json(name = "station_id") val stationId: String,
    @Json(name = "student_name") val studentName: String = "",
    val timestamp: Double = System.currentTimeMillis() / 1000.0,

    // 元器件检测
    @Json(name = "component_count") val componentCount: Int = 0,
    @Json(name = "net_count") val netCount: Int = 0,
    val components: List<ComponentInfo> = emptyList(),

    // 电路验证
    val progress: Double = 0.0,
    val similarity: Double = 0.0,
    @Json(name = "match_level") val matchLevel: String = "",
    @Json(name = "missing_components") val missingComponents: List<String> = emptyList(),

    // 诊断
    val diagnostics: List<String> = emptyList(),

    // 风险分级
    @Json(name = "risk_level") val riskLevel: String = "safe",
    @Json(name = "risk_reasons") val riskReasons: List<String> = emptyList(),

    // 电路快照
    @Json(name = "circuit_snapshot") val circuitSnapshot: String = "",

    // 系统状态
    val fps: Double = 0.0,
    @Json(name = "detector_ok") val detectorOk: String = "ok",
    @Json(name = "llm_backend") val llmBackend: String = "",
    @Json(name = "ocr_backend") val ocrBackend: String = "",

    // 缩略图
    @Json(name = "thumbnail_b64") val thumbnailB64: String = "",
)
