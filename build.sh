set -e

docker_build() {
  echo "Starting docker build..."
  docker build -f k8s/Dockerfile -t movie-info:latest .
}

docker_publish() {
  echo "Publishing docker image..."
  docker tag movie-info:latest mikereem/movie-info:latest
  docker push mikereem/movie-info:latest
}

helm_deps() {
  echo "Updating helm dependencies..."
  helm dependency update k8s/helm
}

compose_docker_up() {
    echo "Starting up with docker-compose..."
    docker-compose -f k8s/docker-compose.yml --env-file .env up --build -d
}

compose_docker_down() {
    echo "Shutting down with docker-compose..."
    docker-compose -f k8s/docker-compose.yml down
}

compose_docker_dev_up() {
    echo "Starting up with docker-compose in dev mode..."
    docker-compose -f k8s/docker-compose.dev.yml --env-file .env up -d
}

compose_docker_dev_down() {
    echo "Shutting down with docker-compose in dev mode..."
    docker-compose -f k8s/docker-compose.dev.yml down
}

if [ "$1" = "up" ]; then
    compose_docker_up
elif [ "$1" = "down" ]; then
    compose_docker_down
elif [ "$1" = "up-dev" ]; then
    compose_docker_dev_up
elif [ "$1" = "down-dev" ]; then
    compose_docker_dev_down
elif [ "$1" = "build" ]; then
    docker_build
elif [ "$1" = "publish" ]; then
    docker_publish
    elif [ "$1" = "helm-deps" ]; then
        docker_publish
else
    echo "Usage: $0 {up|down|up-dev|down-dev|build|publish|helm-deps}"
    exit 1
fi
