# LabGuardian Android

基于 [NowInAndroid](https://github.com/android/nowinandroid) 架构的学生端 Android 应用，连接 LabGuardian-Server 进行电路实验辅助教学。

## 架构

```
:app                      ← Application 入口 + Navigation
:core:model               ← Moshi 数据模型 (对应 Server Schema)
:core:network             ← Retrofit API + OkHttp WebSocket
:core:data                ← Repository 层 (Classroom / Pipeline / Guidance / UserPrefs)
:core:common              ← 通用工具 (Result 封装)
:core:ui                  ← Material 3 主题 + 共享 Composable
:feature:dashboard        ← 工位状态 / 班级排行 / 初始化设置
:feature:camera           ← CameraX 拍照 → Pipeline 提交 → 结果展示
:feature:guidance          ← WebSocket 实时接收教师指导消息
```

## 技术栈

| 类别 | 库 |
|------|-----|
| UI | Jetpack Compose + Material 3 |
| DI | Hilt (Dagger) + KSP |
| 网络 | Retrofit 2 + OkHttp 4 + Moshi |
| 相机 | CameraX |
| 异步 | Kotlin Coroutines + Flow |
| 导航 | Navigation Compose |
| 持久化 | DataStore Preferences |

## 快速开始

### 1. 环境要求

- Android Studio Ladybug (2024.2+)
- JDK 17
- Android SDK 35
- Gradle 8.9

### 2. 配置服务器地址

默认连接 `http://10.0.2.2:8000`（模拟器回环到宿主机）。修改方式：

**开发环境：** 编辑 `app/build.gradle.kts`:
```kotlin
buildConfigField("String", "SERVER_URL", "\"http://your-server:8000\"")
```

### 3. 构建运行

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

### 4. OpenAPI 客户端生成（可选）

当 Server API 变更时，可用 OpenAPI Generator 重新生成 Kotlin 客户端：

```bash
# 确保 LabGuardian-Server 正在运行
./scripts/generate-api.sh http://localhost:8000
```

生成的代码位于 `core/network/src/main/java/com/labguardian/core/network/generated/`（已 gitignore）。

> 当前版本已手写 Retrofit 接口 (`LabGuardianApi.kt`)，与 Server API 保持一致。OpenAPI 生成可作为
> 接口变更时的自动化补充手段。

## 模块依赖关系

```
feature:dashboard ─┐
feature:camera    ─┤→ core:data → core:network → core:model
feature:guidance  ─┘     ↓
                    core:common
                    core:ui → core:model
```

## 主要功能

1. **Dashboard** — 输入工位号/姓名 → 查看自身电路进度、风险等级、班级排行
2. **Camera** — CameraX 拍照面包板 → 提交 Pipeline 4 阶段分析 → 实时显示进度和结果
3. **Guidance** — WebSocket 长连接接收教师发送的提示/广播消息
