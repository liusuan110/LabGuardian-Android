#!/usr/bin/env bash
# ─── OpenAPI Kotlin 客户端生成脚本 ───
# 用法:
#   1. 确保 LabGuardian-Server 正在运行
#   2. ./scripts/generate-api.sh [server_url]
#
# 依赖: docker 或 npx @openapitools/openapi-generator-cli
set -euo pipefail

SERVER_URL="${1:-http://localhost:8000}"
SPEC_URL="$SERVER_URL/openapi.json"
OUTPUT_DIR="$(cd "$(dirname "$0")/.." && pwd)/core/network/src/main/java/com/labguardian/core/network/generated"

echo "📥 Fetching OpenAPI spec from $SPEC_URL ..."
SPEC_FILE=$(mktemp /tmp/labguardian-openapi.XXXXXX.json)
curl -sS "$SPEC_URL" -o "$SPEC_FILE"

echo "🧹 Cleaning previous generated code..."
rm -rf "$OUTPUT_DIR"

echo "🔧 Generating Kotlin client..."
if command -v docker &>/dev/null; then
    docker run --rm \
        -v "$SPEC_FILE:/spec.json:ro" \
        -v "$OUTPUT_DIR:/out" \
        openapitools/openapi-generator-cli generate \
        -i /spec.json \
        -g kotlin \
        -o /out \
        --package-name com.labguardian.core.network.generated \
        --additional-properties=library=jvm-retrofit2,serializationLibrary=moshi,useCoroutines=true \
        --global-property models,apis,supportingFiles=false
else
    npx @openapitools/openapi-generator-cli generate \
        -i "$SPEC_FILE" \
        -g kotlin \
        -o "$OUTPUT_DIR" \
        --package-name com.labguardian.core.network.generated \
        --additional-properties=library=jvm-retrofit2,serializationLibrary=moshi,useCoroutines=true \
        --global-property models,apis,supportingFiles=false
fi

rm -f "$SPEC_FILE"
echo "✅ Generated code → $OUTPUT_DIR"
