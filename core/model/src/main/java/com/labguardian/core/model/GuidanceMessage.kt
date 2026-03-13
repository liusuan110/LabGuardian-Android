package com.labguardian.core.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** 教师 → 单个学生 的指导消息 */
@JsonClass(generateAdapter = true)
data class GuidanceMessage(
    @Json(name = "station_id") val stationId: String = "",
    val type: String = "hint",
    val message: String = "",
    val sender: String = "Teacher",
    val timestamp: Double = System.currentTimeMillis() / 1000.0,
)

/** 教师 → 全班广播消息 */
@JsonClass(generateAdapter = true)
data class BroadcastMessage(
    val type: String = "broadcast",
    val message: String = "",
    val sender: String = "Teacher",
    val timestamp: Double = System.currentTimeMillis() / 1000.0,
)
