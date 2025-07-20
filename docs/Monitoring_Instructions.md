[← Back to Main README](../README.md).

## Grafana Dashboards

Our monitoring setup includes several Grafana dashboards, each visualizing key metrics for different services. Below are descriptions of each dashboard and instructions for accessing them.

### How to Access Dashboards

1. **Login to Grafana:**
    - Open Grafana in your browser.
    - Use the default credentials:
        - Username: `admin`
        - Password: `admin`
2. **Navigate to Dashboards:**
    - After logging in, go to the left sidebar and click on the "Dashboards" section.
    - Select the desired dashboard to view its metrics and visualizations.

### Dashboard Descriptions


#### 1. Authentication Service Dashboard
- **File:** `grafana/provisioning/dashboards/authentiaction-service-dashboard.json`
- **Metrics:**
    - Total number of authentication requests
    - Current latency of authentication service requests
    - Total requests to the authentication service
    - Total errors in the authentication service
- **Visualization:**
    - Stat panels for totals
    - Timeseries panels for latency and error tracking

#### 2. Course Management Dashboard
- **File:** `grafana/provisioning/dashboards/coursemgmt-dashboard.json`
- **Metrics:**
    - Total number of course management requests
    - Latency of last request to course management service
    - Total requests to course management service
    - Total errors in course management service
- **Visualization:**
    - Stat panels for totals
    - Timeseries panels for latency and error tracking

#### 3. Summary Service Dashboard
- **File:** `grafana/provisioning/dashboards/summary-service-dashboard.json`
- **Metrics:**
    - Total number of summary requests
    - Duration of last summary request
    - Total time spent summarizing
    - Maximum time needed for summary
    - Total requests to summary service
- **Visualization:**
    - Stat panels for totals
    - Timeseries panels for durations and maximums

#### 4. Upload Service Dashboard
- **File:** `grafana/provisioning/dashboards/upload-service-dashboard.json`
- **Metrics:**
    - Total number of upload requests
    - Latency of last upload request
    - Total requests to upload service
    - Total errors in upload service
- **Visualization:**
    - Stat panels for totals
    - Timeseries panels for latency and error tracking

#### 5. Alert Dashboard
- **File:** `grafana/provisioning/dashboards/alert-dashboard.json`
- **Metrics:**
    - Upload service error gauge (shows errors over the last 10 minutes)
- **Visualization:**
    - Timeseries panel displaying error counts and thresholds

#### 6. All Services Ready Time and Requests Active Seconds
- **File:** `grafana/provisioning/dashboards/all-services.json`
- **Metrics:**
    - Application ready time for all services
    - Active HTTP server requests (max seconds)
    - Virtual memory usage
- **Visualization:**
    - Timeseries panel showing service readiness and request activity over time.