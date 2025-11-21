#!/bin/bash

set -e

# --------------------------
# COLORS
# --------------------------
GREEN="\033[1;32m"
BLUE="\033[1;34m"
YELLOW="\033[1;33m"
RED="\033[1;31m"
NC="\033[0m"

info() { echo -e "${BLUE}[INFO] $*${NC}"; }
success() { echo -e "${GREEN}[SUCCESS] $*${NC}"; }
warn() { echo -e "${YELLOW}[WARN] $*${NC}"; }
error() { echo -e "${RED}[ERROR] $*${NC}"; }

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

pull_image_if_exists() {
    local img="$1"
    if docker pull "$img" &>/dev/null; then
        info "Pulled latest image: $img"
    else
        warn "No remote image for $img, will build locally"
    fi
}

run_backend_tests() {
    info "Running backend tests..."
    ./gradlew test
}

run_frontend_tests() {
    info "Running frontend tests..."
    cd admin-ui
    npm install
    npm test -- --watchAll=false
    cd ..
}

build_backend() {
    info "Building Java AuthService..."
    ./gradlew clean build
    docker build -t "$AUTH_IMG" .
}

build_frontend() {
    info "Building Admin UI React..."
    cd admin-ui
    npm install
    npm run build
    docker build -t "$ADMIN_IMG" .
    cd ..
}

docker_compose_up() {
    info "Starting full stack via Docker Compose..."
    docker-compose -f "$DOCKER_COMPOSE_FILE" up --build -d
}

helm_deploy() {
    if [ "$1" == "--helm" ]; then
        info "Deploying Helm chart to Kubernetes..."
        helm upgrade --install authservice "$HELM_CHART_PATH" -f "$HELM_VALUES"
    fi
}

wait_for_container() {
    local name="$1"
    local timeout=${2:-60}
    info "Waiting for container $name to be ready (max $timeout s)..."
    for i in $(seq 1 $timeout); do
        if [ "$(docker inspect -f '{{.State.Health.Status}}' "$name" 2>/dev/null)" == "healthy" ]; then
            success "$name is healthy!"
            return 0
        fi
        sleep 1
    done
    warn "$name not healthy after $timeout seconds"
}

status_check() {
    info "Checking service status..."
    docker ps --filter "name=authservice" --filter "name=admin-ui"
    info "Waiting for containers to become healthy..."
    wait_for_container "authservice"
    wait_for_container "admin-ui"
    success "You can access AuthService: http://localhost:9000"
    success "You can access Admin UI: http://localhost:3000"
}

# --------------------------
# MAIN
# --------------------------

echo -e "${BLUE}=== ADVANCED ONE-SHOT FULLSTACK LAUNCH SCRIPT ===${NC}"

pull_image_if_exists "$AUTH_IMG"
pull_image_if_exists "$ADMIN_IMG"

run_backend_tests
run_frontend_tests

build_backend
build_frontend

docker_compose_up

helm_deploy "$1"

status_check

echo -e "${GREEN}✅ Full stack launch complete!${NC}"