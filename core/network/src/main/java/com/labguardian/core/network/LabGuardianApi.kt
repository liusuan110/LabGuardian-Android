package com.labguardian.core.network

import com.labguardian.core.model.*
import retrofit2.http.*

/**
 * LabGuardian-Server REST API — Retrofit 接口
 *
 * base path: /api/v1
 */
interface LabGuardianApi {

    // ── Classroom ──────────────────────────────────────────

    @POST("classroom/heartbeat")
    suspend fun postHeartbeat(@Body heartbeat: StationHeartbeat): Map<String, Any>

    @GET("classroom/stations")
    suspend fun getStations(): Map<String, StationHeartbeat>

    @GET("classroom/ranking")
    suspend fun getRanking(): List<RankingEntry>

    @GET("classroom/alerts")
    suspend fun getAlerts(): List<AlertEntry>

    @GET("classroom/stats")
    suspend fun getStats(): ClassroomStats

    @GET("classroom/station/{stationId}")
    suspend fun getStation(@Path("stationId") stationId: String): StationHeartbeat

    @GET("classroom/station/{stationId}/thumbnail")
    suspend fun getThumbnail(@Path("stationId") stationId: String): Map<String, String>

    @POST("classroom/station/{stationId}/guidance")
    suspend fun sendGuidance(
        @Path("stationId") stationId: String,
        @Body message: GuidanceMessage,
    ): Map<String, String>

    @POST("classroom/broadcast")
    suspend fun broadcast(@Body message: BroadcastMessage): Map<String, Any>

    @POST("classroom/reference")
    suspend fun setReference(@Body circuit: Map<String, Any>): Map<String, String>

    @GET("classroom/reference")
    suspend fun getReference(): Map<String, Any>

    @POST("classroom/reset")
    suspend fun resetSession(): Map<String, String>

    // ── Pipeline ───────────────────────────────────────────

    @POST("pipeline/run")
    suspend fun runPipeline(@Body request: PipelineRequest): PipelineResult

    @POST("pipeline/submit")
    suspend fun submitPipeline(@Body request: PipelineRequest): JobStatusResponse

    @GET("pipeline/status/{jobId}")
    suspend fun getPipelineStatus(@Path("jobId") jobId: String): JobStatusResponse
}
