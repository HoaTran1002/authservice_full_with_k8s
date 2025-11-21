#!/bin/bash

set -e

# --------------------------
# CONFIG
# --------------------------
REPO_ROOT=$(pwd)
DOCKER_REGISTRY="your-registry"
AUTH_IMG="$DOCKER_REGISTRY/authservice:latest"
ADMIN_IMG="$DOCKER_REGISTRY/authservice-admin:latest"
DOCKER_COMPOSE_FILE="$REPO_ROOT/docker-compose.yml"
HELM_CHART_PATH="$REPO_ROOT/helm/authservice"
HELM_VALUES="$HELM_CHART_PATH/values-production.yaml"

# --------------------------
# FUNCTIONS
# --------------------------

build_backend() {
    echo "📦 Building Java AuthService..."
    ./gradlew clean build -x test
    docker build -t "$AUTH_IMG" .
}

build_frontend() {
    echo "📦 Building Admin UI React..."
    cd admin-ui
    npm install
    npm run build
    docker build -t "$ADMIN_IMG" .
    cd ..
}

docker_compose_up() {
    echo "🐳 Starting full stack via Docker Compose..."
    docker-compose -f "$DOCKER_COMPOSE_FILE" up --build -d
}

helm_deploy() {
    if [ "$1" == "--helm" ]; then
        echo "🚀 Deploying Helm chart to Kubernetes..."
        helm upgrade --install authservice "$HELM_CHART_PATH" -f "$HELM_VALUES"
    fi
}

status_check() {
    echo "🔎 Checking service status..."
    docker ps --filter "name=authservice" --filter "name=admin-ui"
    echo "You can access AuthService: http://localhost:9000"
    echo "You can access Admin UI: http://localhost:3000"
}

# --------------------------
# MAIN
# --------------------------

echo "=== ONE-SHOT FULLSTACK LAUNCH SCRIPT ==="

# Build images
build_backend
build_frontend

# Start Docker Compose stack
docker_compose_up

# Optional Helm deploy
helm_deploy "$1"

# Status check
status_check

echo "✅ Full stack launch complete!"