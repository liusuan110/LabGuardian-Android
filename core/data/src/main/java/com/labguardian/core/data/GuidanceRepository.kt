package com.labguardian.core.data

import com.labguardian.core.network.StationWebSocket
import com.labguardian.core.network.WsEvent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GuidanceRepository @Inject constructor(
    private val ws: StationWebSocket,
) {
    fun observeGuidance(stationId: String): Flow<WsEvent> = ws.connect(stationId)

    fun disconnect() = ws.disconnect()
}
