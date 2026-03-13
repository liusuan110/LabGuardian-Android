package com.labguardian.core.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 风险等级标签
 */
@Composable
fun RiskBadge(riskLevel: String, modifier: Modifier = Modifier) {
    val (bg, fg, label) = when (riskLevel.lowercase()) {
        "danger" -> Triple(Color(0xFFD32F2F), Color.White, "危险")
        "warning" -> Triple(Color(0xFFFFA000), Color.White, "警告")
        else -> Triple(Color(0xFF388E3C), Color.White, "安全")
    }
    Surface(
        color = bg,
        shape = MaterialTheme.shapes.small,
        modifier = modifier,
    ) {
        Text(
            text = label,
            color = fg,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

/**
 * 带标签的圆形进度指示器
 */
@Composable
fun ProgressRing(
    progress: Float,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.size(64.dp),
                strokeWidth = 6.dp,
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

/**
 * 诊断信息列表
 */
@Composable
fun DiagnosticsList(
    diagnostics: List<String>,
    modifier: Modifier = Modifier,
) {
    if (diagnostics.isEmpty()) return
    Column(modifier = modifier) {
        Text("诊断信息", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(4.dp))
        diagnostics.forEach { msg ->
            Text(
                text = "• $msg",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 2.dp),
            )
        }
    }
}
