# repository-score

The **Repository Score Application** is a Java-based service that fetches repositories from Gihub public api endpoint and calculates scores for these repositories  The application is fully containerized with Docker and can be deployed to Kubernetes using Helm charts. It also integrates with Prometheus and Grafana for monitoring.

## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Docker Setup](#docker-setup)
- [Monitoring Setup](#monitoring-setup)
- [Start Script Usage](#start-script-usage)

## Features
- **Caching:**  
  Uses Caffeine caching to optimize expensive computations.

- **Containerization:**  
  Built with a multi-stage Dockerfile.

- **Kubernetes Deployment:**  
  Deploy the application with Helm charts for a production-ready Kubernetes setup.

- **Monitoring:**  
  Integrated Prometheus and Grafana for real-time performance monitoring and visualization.

## Prerequisites

Before running or deploying this application, ensure you have the following installed:

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)
- [Helm](https://helm.sh/docs/intro/install/)
- Java (if running locally without Docker)
- Gradle (if building locally without Docker)

## Monitoring Setup
### Prometheus:
Scrapes application metrics (configured in prometheus.yml under src/main/resources).

### Grafana:
Connects to Prometheus on port 9090 to visualize the metrics. Access Grafana via port 3000 using the default credentials (admin for both username and password, unless otherwise configured).

## Start Script Usage
The `start.sh` script simplifies the process of building and running the application in different modes.

Usage
`./start.sh [docker|helm]`

### docker:
Builds the Docker image and starts the services using Docker Compose.

### helm:
Builds the Docker image and deploys the application using Helm charts.

**NOTE**: Currently helm does not support prometheus and grafana. Therefore, they are not deployed along with the application. Use docker to start if you need the monitoring tools.
