package com.labguardian.core.network

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import javax.inject.Inject

class OkHttpStationWebSocket @Inject constructor(
    private val client: OkHttpClient,
    private val baseWsUrl: String,
) : StationWebSocket {

    private var ws: WebSocket? = null

    override fun connect(stationId: String): Flow<WsEvent> = callbackFlow {
        val url = "$baseWsUrl/ws/station/$stationId"
        val request = Request.Builder().url(url).build()

        ws = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                trySend(WsEvent.Connected)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                runCatching {
                    val json = JSONObject(text)
                    WsEvent.GuidanceReceived(
                        type = json.optString("type", "hint"),
                        message = json.optString("message", ""),
                        sender = json.optString("sender", "Teacher"),
                    )
                }.onSuccess { trySend(it) }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
                trySend(WsEvent.Disconnected)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                trySend(WsEvent.Error(t))
            }
        })

        awaitClose { disconnect() }
    }

    override fun disconnect() {
        ws?.close(1000, "bye")
        ws = null
    }
}
