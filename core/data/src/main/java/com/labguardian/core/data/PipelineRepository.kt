package com.labguardian.core.data

import com.labguardian.core.model.JobStatusResponse
import com.labguardian.core.model.PipelineRequest
import com.labguardian.core.model.PipelineResult
import com.labguardian.core.network.LabGuardianApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PipelineRepository @Inject constructor(
    private val api: LabGuardianApi,
) {
    /** 同步执行 Pipeline（演示用，无需 Redis/Celery） */
    suspend fun runSync(request: PipelineRequest): PipelineResult =
        api.runPipeline(request)

    suspend fun submit(request: PipelineRequest): JobStatusResponse =
        api.submitPipeline(request)

    suspend fun getStatus(jobId: String): JobStatusResponse =
        api.getPipelineStatus(jobId)

    /**
     * 提交任务后轮询状态直到完成或失败
     */
    fun submitAndPoll(
        request: PipelineRequest,
        intervalMs: Long = 2000L,
    ): Flow<JobStatusResponse> = flow {
        val initial = submit(request)
        emit(initial)
        var jobId = initial.jobId
        while (true) {
            delay(intervalMs)
            val status = getStatus(jobId)
            emit(status)
            if (status.status in listOf("completed", "failed")) break
        }
    }
}
