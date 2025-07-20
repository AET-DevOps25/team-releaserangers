[← Back to Main README](../README.md)

# Kubernetes Deployment

## Overview

This document outlines the Kubernetes deployment architecture for the ReleaseRangers application platform. The setup consists of both application services and a monitoring stack, deployed across two dedicated namespaces for clean separation of concerns.

## Architecture

### Namespaces

- **Application Namespace (`releaserangers`)**: Contains all application services and database
- **Monitoring Namespace (`ranger-observatory`)**: Contains Prometheus and Grafana

### Application Components

1. **Frontend**
   - React application serving the user interface
   
2. **Backend Services**
   - Authentication Service: JWT-based authentication
   - Course Management Service: Course-related functionality
   - Upload Service: File upload with monitoring
   - GenAI Service: AI features using Google's Gemini model
   
3. **Data Layer**
   - PostgreSQL Database: Persistent storage for application data

### Monitoring Stack

1. **Prometheus**: Metrics collection and storage
2. **Grafana**: Visualization with pre-configured dashboards:
   - Main Dashboard: Application metrics
   - Alert Dashboard: Error conditions monitoring

## Deployment Structure

```
helm/releaserangersapp/
├── Chart.yaml              # Helm chart metadata
├── values.yaml             # Default configuration values
├── secrets.yaml            # Kubernetes secrets (stored separately)
├── grafana/                # Grafana configuration
│   └── dashboards/         # Pre-configured dashboards       
└── templates/              # Kubernetes resource templates
    ├── application components
    └── monitoring components
```

## Secrets Management

The application uses Kubernetes Secrets for sensitive information:

- `authentication-env`: JWT secret for authentication
- `postgres-secret`: Database credentials
- `genai-env`: API key for the Gemini AI model
- `grafana-credentials`: Grafana admin credentials

These secrets should be stored securely and not in the Git repository.

## Manual Deployment

### Prerequisites

- Kubectl installed and configured
- Helm v3 installed
- Access to the Kubernetes cluster

### Deployment Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/team-releaserangers.git
   cd team-releaserangers
   ```

2. **Create namespaces**
   ```bash
   kubectl create namespace releaserangers
   kubectl create namespace ranger-observatory
   ```

3. **Apply secrets**
   ```bash
   # Create a secrets.yaml file based on the template (not included in repo)
   # Apply the secrets to the cluster
   kubectl apply -f path/to/your/secrets.yaml
   ```

4. **Update Helm dependencies**
   ```bash
   helm dependency update ./helm/releaserangersapp
   ```

5. **Deploy the application**
   ```bash
   helm upgrade --install releaserangersapp ./helm/releaserangersapp \
     -n releaserangers \
     -f ./helm/releaserangersapp/values.yaml
   ```

6. **Verify deployment**
   ```bash
   # Check application pods
   kubectl get pods -n releaserangers
   
   # Check monitoring pods
   kubectl get pods -n ranger-observatory
   
   # View services
   kubectl get svc -n releaserangers
   kubectl get svc -n ranger-observatory
   ```

## Automated Deployment

The project includes a GitHub Actions workflow that automatically deploys the application to the Kubernetes cluster when changes are merged to the main branch. The workflow:

1. Sets up Kubernetes configuration
2. Applies secrets
3. Deploys the Helm chart
4. Restarts deployments as needed
5. Shows deployment status

No manual intervention is required for the CI/CD pipeline once it's configured.

## Accessing the Application

- **Main Application**: https://releaserangers.student.k8s.aet.cit.tum.de
- **Grafana Dashboard**: https://releaserangers-grafana.student.k8s.aet.cit.tum.de
  - login: user: admin, password: admin (or see in secrets.yaml)
- **Prometheus UI**: https://releaserangers-prometheus.student.k8s.aet.cit.tum.de

## Monitoring and Observability

### Metrics Collection

Prometheus is configured to scrape metrics from all backend services. The Spring Boot services expose metrics via the `/actuator/prometheus` endpoint.

### Dashboard Access

To access the Grafana dashboards:
1. Navigate to https://releaserangers-grafana.student.k8s.aet.cit.tum.de
2. Log in with the credentials from the `grafana-credentials` secret
3. Access the pre-configured dashboards from the dashboard list

## Troubleshooting

### Common Issues

1. **Pods not starting**
   ```bash
   kubectl describe pod <pod-name> -n releaserangers
   kubectl logs <pod-name> -n releaserangers
   ```

2. **Database connection issues**
   ```bash
   # Check if PostgreSQL is running
   kubectl get pod -l app=postgres -n releaserangers
   # Check if secret exists
   kubectl get secret postgres-secret -n releaserangers
   ```

3. **Monitoring issues**
   ```bash
   # Check Prometheus targets
   kubectl port-forward svc/prometheus -n ranger-observatory 9090:9090
   # Then access http://localhost:9090/targets in browser
   
   # Check Grafana dashboards
   kubectl exec -it <grafana-pod-name> -n ranger-observatory -- \
     ls -la /etc/grafana/provisioning/dashboards
   ```

## Security Considerations

- TLS encryption is enabled for all public endpoints
- Sensitive data is stored in Kubernetes Secrets
- Resource limits are defined to prevent resource exhaustion
- Access controls are implemented through Kubernetes RBAC

## Conclusion

This Kubernetes setup provides a robust, scalable, and observable environment for the ReleaseRangers application with minimal manual intervention required. The automated CI/CD pipeline ensures consistent deployments, while the monitoring stack provides detailed insights into application performance and health.
