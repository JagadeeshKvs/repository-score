# repository-score

The **Repository Score Application** is a Java-based service that fetches repositories from the GitHub public API endpoint and calculates scores for these repositories. The application is fully containerized with Docker and can be deployed to Kubernetes using Helm charts. It also integrates with Prometheus and Grafana for monitoring.

## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Docker Setup](#docker-setup)
- [Helm Deployment](#helm-deployment)
- [API Documentation](#api-documentation)
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

- **API Documentation:**  
  Automatically generated using Springdoc OpenAPI (Swagger). Provides interactive API documentation accessible via your browser.

## Prerequisites

Before running or deploying this application, ensure you have the following installed:

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)
- [Helm](https://helm.sh/docs/intro/install/)
- Java (if running locally without Docker)
- Gradle (if building locally without Docker)

## Docker Setup

Build the Docker image using the provided Dockerfile. The multi-stage Docker build ensures that your final image is lightweight and production-ready.

## Helm Deployment

Deploy the application to your Kubernetes cluster using the provided Helm charts. Note that monitoring tools like Prometheus and Grafana are not deployed by Helm; use Docker Compose if you need the full monitoring stack.

## API Documentation

The API documentation is automatically generated with [springdoc-openapi](https://springdoc.org/) and integrated with Swagger UI. Once the application is running, you can access the API docs:

- **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

If you need to customize the documentation, you can modify via the `application.properties` file.

## Monitoring Setup
### Prometheus:
Scrapes application metrics (configured in `prometheus.yml` under `src/main/resources`).

### Grafana:
Connects to Prometheus on port 9090 to visualize the metrics. Access Grafana via port 3000 using the default credentials (admin for both username and password, unless otherwise configured).

## Start Script Usage
The `start.sh` script simplifies the process of building and running the application in different modes.

**Usage:**
`./start.sh [docker|helm]`


### docker:
Builds the Docker image and starts the services using Docker Compose.

### helm:
Builds the Docker image and deploys the application using Helm charts.

**NOTE:** Currently helm does not support Prometheus and Grafana. Therefore, they are not deployed along with the application. Use docker to start if you need the monitoring tools.
