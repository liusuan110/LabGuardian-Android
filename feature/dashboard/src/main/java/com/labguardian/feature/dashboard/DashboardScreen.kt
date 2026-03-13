package com.labguardian.feature.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.labguardian.core.ui.component.DiagnosticsList
import com.labguardian.core.ui.component.ProgressRing
import com.labguardian.core.ui.component.RiskBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // 首次配置: 输入工位和姓名
    if (state.stationId.isBlank()) {
        SetupCard(
            onConfirm = { id, name ->
                viewModel.setStationId(id)
                viewModel.setStudentName(name)
            },
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // 刷新按钮
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("工位 ${state.stationId}", style = MaterialTheme.typography.headlineSmall)
                IconButton(onClick = { viewModel.refresh() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "刷新")
                }
            }
        }

        // 我的状态卡片
        state.myStation?.let { station ->
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("我的状态", style = MaterialTheme.typography.titleMedium)
                            RiskBadge(station.riskLevel)
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            ProgressRing(progress = station.progress.toFloat(), label = "进度")
                            ProgressRing(progress = station.similarity.toFloat(), label = "相似度")
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("元器件: ${station.componentCount}  网络: ${station.netCount}")
                        DiagnosticsList(station.diagnostics, Modifier.padding(top = 8.dp))
                    }
                }
            }
        }

        // 班级统计
        state.stats?.let { stats ->
            item {
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("班级概况", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Text("在线: ${stats.onlineCount} / ${stats.totalStations}")
                        Text("已完成: ${stats.completedCount}")
                        Text("平均进度: ${(stats.avgProgress * 100).toInt()}%")
                        if (stats.dangerCount > 0) {
                            Text(
                                "⚠ 危险工位: ${stats.dangerCount}",
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }
        }

        // 排行榜
        if (state.ranking.isNotEmpty()) {
            item {
                Text("排行榜", style = MaterialTheme.typography.titleMedium)
            }
            itemsIndexed(state.ranking) { _, entry ->
                ListItem(
                    headlineContent = { Text("${entry.rank}. ${entry.studentName}") },
                    supportingContent = {
                        Text("进度 ${(entry.progress * 100).toInt()}% · 元件 ${entry.componentCount}")
                    },
                    trailingContent = { RiskBadge(entry.riskLevel) },
                )
            }
        }

        // 错误提示
        state.error?.let { error ->
            item {
                Text(error, color = MaterialTheme.colorScheme.error)
            }
        }

        // 加载中
        if (state.isLoading) {
            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun SetupCard(onConfirm: (stationId: String, studentName: String) -> Unit) {
    var stationId by remember { mutableStateOf("") }
    var studentName by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        ElevatedCard(modifier = Modifier.padding(32.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("初始化工位", style = MaterialTheme.typography.headlineSmall)
                OutlinedTextField(
                    value = stationId,
                    onValueChange = { stationId = it },
                    label = { Text("工位编号") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = studentName,
                    onValueChange = { studentName = it },
                    label = { Text("姓名") },
                    singleLine = true,
                )
                Button(
                    onClick = { onConfirm(stationId.trim(), studentName.trim()) },
                    enabled = stationId.isNotBlank(),
                ) {
                    Text("确认")
                }
            }
        }
    }
}
