#!/bin/bash
set -e

# Usage information
usage() {
  echo "Usage: $0 [docker|helm]"
  echo "  docker: Build image and run Docker Compose"
  echo "  helm  : Deploy using Helm charts and values files"
  exit 1
}

if [ "$#" -ne 1 ]; then
  usage
fi

MODE=$1

# Build the Docker image using your Dockerfile.
build_image() {
  echo "Building Docker image 'repo-score-image'..."
  docker build -t repo-score-image .
}

if [ "$MODE" == "docker" ]; then
  # Build the image and start all services with docker-compose.
  build_image
  echo "Starting Docker Compose services..."
  docker-compose up -d
  echo "Application started using Docker Compose."
elif [ "$MODE" == "helm" ]; then
  # Optionally build the image if your Helm charts rely on it.
  build_image
  # Deploy the Helm chart.
  # Adjust the helm chart directory (here assumed as './helm') and values file path as needed.
  echo "Deploying application using Helm..."
  helm upgrade --install reposcore-app ./helm -f ./helm/values.yaml
  echo "Application deployed using Helm."
else
  usage
fi
