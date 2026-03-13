package com.labguardian.core.data

import com.labguardian.core.model.*
import com.labguardian.core.network.LabGuardianApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClassroomRepository @Inject constructor(
    private val api: LabGuardianApi,
) {
    suspend fun postHeartbeat(heartbeat: StationHeartbeat): Map<String, Any> =
        api.postHeartbeat(heartbeat)

    suspend fun getRanking(): List<RankingEntry> = api.getRanking()

    suspend fun getAlerts(): List<AlertEntry> = api.getAlerts()

    suspend fun getStats(): ClassroomStats = api.getStats()

    suspend fun getStation(stationId: String): StationHeartbeat = api.getStation(stationId)

    suspend fun getThumbnail(stationId: String): String? =
        api.getThumbnail(stationId)["thumbnail_b64"]

    suspend fun sendGuidance(stationId: String, msg: GuidanceMessage) =
        api.sendGuidance(stationId, msg)

    suspend fun broadcast(msg: BroadcastMessage) = api.broadcast(msg)
}
