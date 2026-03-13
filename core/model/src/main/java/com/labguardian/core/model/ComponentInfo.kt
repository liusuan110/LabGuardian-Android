package com.labguardian.core.model

import com.squareup.moshi.JsonClass

/**
 * 单个元器件信息 — 对应 Server schemas.classroom.ComponentInfo
 */
@JsonClass(generateAdapter = true)
data class ComponentInfo(
    val name: String = "",
    val type: String = "",
    val polarity: String = "none",
    val pin1: List<String> = emptyList(),
    val pin2: List<String> = emptyList(),
    val pin3: List<String> = emptyList(),
    val confidence: Double = 0.0,
)
