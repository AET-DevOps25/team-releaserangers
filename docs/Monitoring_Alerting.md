[← Back to Main README](../README.md).

---

# Monitoring & Alerting Setup

This document describes the monitoring and alerting setup for the ReleaseRangers project.

## Overview

We use Prometheus and Grafana for monitoring, with Loki and Promtail for log aggregation. One alert rule is configured in Grafana, can be monitored using our alert dashboard and trigger when certain conditions are met (upload errors).

## Components

- **Prometheus**: Collects metrics from all microservices (Spring Boot actuator endpoints) and other services.
- **Grafana**: Visualizes metrics and dashboards, provisions alerting rules, and sends notifications.
- **Loki & Promtail**: Aggregate and visualize logs from containers.

## Configuration

- **Prometheus** is configured via `prometheus/prometheus.yml` to scrape metrics from all services.
- **Grafana** is configured via `grafana/grafana.ini` and provisioning files in `grafana/provisioning/`:
  - **Dashboards**: JSON files in `grafana/provisioning/dashboards/`.
  - **Datasources**: Prometheus and Loki defined in `grafana/provisioning/datasources/`.
  - **Alerting**: Alert rules in `grafana/provisioning/alerting/alerts.yaml`.

## Example: Upload Error Alert

An alert is configured to monitor the `upload_service_errors_gauge` metric. If errors are detected, Grafana triggers the alert rule and the status is first set to pending and if the error persists to alerting.
This alert can be monitored in the Grafana alert dashboard. The alert can be triggered e.g. by creating an empty pdf using:
```bash
touch empty.pdf
```
And then uploading it using the client application. This will cause the upload service to throw an error and trigger the alert.

## How to Use in local setup

- Start all services using Docker Compose: `docker compose up --build`
- Access Grafana at [http://localhost:3001](http://localhost:3001)
- Dashboards and alerts are automatically provisioned.
- You can login with the default credentials:
  - Username: `admin`
  - Password: `admin`

## Files

- `prometheus/prometheus.yml`: Prometheus configuration
- `grafana/grafana.ini`: Grafana main configuration
- `grafana/provisioning/dashboards/`: Dashboard definitions
- `grafana/provisioning/datasources/`: Datasource definitions
- `grafana/provisioning/alerting/alerts.yaml`: Alerting rules


---
