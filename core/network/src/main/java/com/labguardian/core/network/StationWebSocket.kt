package com.labguardian.core.network

import kotlinx.coroutines.flow.Flow

/**
 * WebSocket 接收到的消息
 */
sealed interface WsEvent {
    data class GuidanceReceived(val type: String, val message: String, val sender: String) : WsEvent
    data class Error(val throwable: Throwable) : WsEvent
    data object Connected : WsEvent
    data object Disconnected : WsEvent
}

/**
 * 站点 WebSocket 客户端抽象
 */
interface StationWebSocket {
    /** 建立连接并持续接收消息 */
    fun connect(stationId: String): Flow<WsEvent>
    /** 关闭连接 */
    fun disconnect()
}
