package com.labguardian.feature.guidance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuidanceScreen(viewModel: GuidanceViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // 新消息自动滚动到底部
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部状态栏
        TopAppBar(
            title = { Text("教师指导") },
            actions = {
                // 连接状态指示器
                Icon(
                    Icons.Default.Circle,
                    contentDescription = if (state.connected) "已连接" else "未连接",
                    tint = if (state.connected) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
                    modifier = Modifier.size(12.dp),
                )
                Spacer(Modifier.width(8.dp))
                if (state.messages.isNotEmpty()) {
                    IconButton(onClick = { viewModel.clearMessages() }) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "清除消息")
                    }
                }
            },
        )

        // 错误提示
        state.error?.let { error ->
            Text(
                text = "连接异常: $error",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }

        if (state.messages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (state.connected) "等待教师消息…" else "未连接到服务器",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                items(state.messages) { msg ->
                    MessageBubble(msg)
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(msg: GuidanceMessage) {
    val isBroadcast = msg.type == "broadcast"
    val containerColor = if (isBroadcast) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = if (isBroadcast) "📢 全班广播" else "💬 ${msg.sender}",
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    text = formatTime(msg.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(text = msg.message, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private fun formatTime(ts: Long): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(ts))
}
