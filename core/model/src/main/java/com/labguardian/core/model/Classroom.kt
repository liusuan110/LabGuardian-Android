package com.labguardian.core.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** 排行榜条目 */
@JsonClass(generateAdapter = true)
data class RankingEntry(
    val rank: Int = 0,
    @Json(name = "station_id") val stationId: String = "",
    @Json(name = "student_name") val studentName: String = "",
    val progress: Double = 0.0,
    val similarity: Double = 0.0,
    @Json(name = "elapsed_s") val elapsedS: Double = 0.0,
    @Json(name = "risk_event_count") val riskEventCount: Int = 0,
    @Json(name = "component_count") val componentCount: Int = 0,
    @Json(name = "risk_level") val riskLevel: String = "safe",
    val online: Boolean = false,
)

/** 班级聚合统计 */
@JsonClass(generateAdapter = true)
data class ClassroomStats(
    @Json(name = "total_stations") val totalStations: Int = 0,
    @Json(name = "online_count") val onlineCount: Int = 0,
    @Json(name = "completed_count") val completedCount: Int = 0,
    @Json(name = "avg_progress") val avgProgress: Double = 0.0,
    @Json(name = "total_risk_events") val totalRiskEvents: Int = 0,
    @Json(name = "danger_count") val dangerCount: Int = 0,
    @Json(name = "error_histogram") val errorHistogram: Map<String, Int> = emptyMap(),
    @Json(name = "session_duration_s") val sessionDurationS: Double = 0.0,
)

/** 警报条目 */
@JsonClass(generateAdapter = true)
data class AlertEntry(
    @Json(name = "station_id") val stationId: String = "",
    @Json(name = "student_name") val studentName: String = "",
    @Json(name = "risk_level") val riskLevel: String = "warning",
    @Json(name = "risk_reasons") val riskReasons: List<String> = emptyList(),
    val diagnostics: List<String> = emptyList(),
    val progress: Double = 0.0,
)
