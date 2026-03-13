package com.labguardian.feature.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.labguardian.core.ui.component.DiagnosticsList
import com.labguardian.core.ui.component.ProgressRing
import com.labguardian.core.ui.component.RiskBadge

@Composable
fun CameraScreen(viewModel: CameraViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    // 相册选图
    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val stream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(stream)
                stream?.close()
                if (bitmap != null) {
                    viewModel.addCapture(bitmap)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "读取图片失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 相机预览
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).also { previewView ->
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.surfaceProvider = previewView.surfaceProvider
                            }
                            val capture = ImageCapture.Builder()
                                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                .build()
                            imageCapture = capture

                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                capture,
                            )
                        }, ContextCompat.getMainExecutor(ctx))
                    }
                },
                modifier = Modifier.fillMaxSize(),
            )

            // 底部按钮: 相册 + 拍照
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 从相册选择
                FloatingActionButton(
                    onClick = { pickImageLauncher.launch("image/*") },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "相册")
                }
                // 拍照
                FloatingActionButton(
                    onClick = {
                        imageCapture?.takePicture(
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageCapturedCallback() {
                                override fun onCaptureSuccess(image: ImageProxy) {
                                    val buffer = image.planes[0].buffer
                                    val bytes = ByteArray(buffer.remaining())
                                    buffer.get(bytes)
                                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                    val rotated = bitmap.rotate(image.imageInfo.rotationDegrees.toFloat())
                                    viewModel.addCapture(rotated)
                                    image.close()
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    Toast.makeText(context, "拍照失败", Toast.LENGTH_SHORT).show()
                                }
                            },
                        )
                    },
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "拍照")
                }
            }
        }

        // 已拍照片预览 + 操作区
        Column(modifier = Modifier.padding(8.dp)) {
            if (state.capturedImages.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(state.capturedImages) { index, b64 ->
                        val bytes = android.util.Base64.decode(b64, android.util.Base64.DEFAULT)
                        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        Box {
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "照片 $index",
                                modifier = Modifier.size(80.dp),
                            )
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "删除",
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .clickable { viewModel.removeCapture(index) },
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { viewModel.clearCaptures() }) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("清除")
                    }
                    Button(
                        onClick = { viewModel.submitPipeline() },
                        enabled = !state.isSubmitting,
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text(if (state.isSubmitting) "分析中…" else "提交分析")
                    }
                }
            }

            // Pipeline 进度
            if (state.isSubmitting && state.currentStage != null) {
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text("阶段: ${state.currentStage}", style = MaterialTheme.typography.bodySmall)
            }

            // 结果展示
            state.result?.let { result ->
                Spacer(Modifier.height(8.dp))
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    // ── 摘要卡片 ──
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text("分析结果", style = MaterialTheme.typography.titleSmall)
                                RiskBadge(result.riskLevel)
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                                ProgressRing(progress = result.progress.toFloat(), label = "进度")
                                ProgressRing(progress = result.similarity.toFloat(), label = "相似度")
                            }
                            Text("元器件: ${result.componentCount}  网络: ${result.netCount}")
                            Text("耗时: ${result.totalDurationMs.toInt()} ms")
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // ── 元件详情卡片 ──
                    val mappingStage = result.stages.firstOrNull { it.stage == "mapping" }
                    val components = (mappingStage?.data?.get("components") as? List<*>)
                        ?.filterIsInstance<Map<*, *>>()
                    if (!components.isNullOrEmpty()) {
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("元件识别详情", style = MaterialTheme.typography.titleSmall)
                                Spacer(Modifier.height(4.dp))
                                components.forEachIndexed { idx, comp ->
                                    val className = comp["class_name"] as? String ?: "?"
                                    val conf = (comp["confidence"] as? Number)?.toFloat() ?: 0f
                                    val pin1 = (comp["pin1_logic"] as? List<*>)?.joinToString(",")
                                    val pin2 = (comp["pin2_logic"] as? List<*>)?.joinToString(",")
                                    if (idx > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Text(
                                            text = className,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                        Text(
                                            text = "置信度 ${"%.0f%%".format(conf * 100)}",
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                    }
                                    if (pin1 != null && pin2 != null) {
                                        Text(
                                            text = "引脚: ($pin1) → ($pin2)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // ── 拓扑描述卡片 ──
                    val topoStage = result.stages.firstOrNull { it.stage == "topology" }
                    val circuitDesc = topoStage?.data?.get("circuit_description") as? String
                    if (!circuitDesc.isNullOrBlank()) {
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("电路拓扑", style = MaterialTheme.typography.titleSmall)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = circuitDesc,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // ── 诊断信息卡片 ──
                    if (result.diagnostics.isNotEmpty()) {
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                DiagnosticsList(result.diagnostics)
                            }
                        }
                    }
                }
            }

            state.error?.let { err ->
                Text(err, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

private fun Bitmap.rotate(degrees: Float): Bitmap {
    if (degrees == 0f) return this
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}
