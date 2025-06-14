# API Endpoints Documentation

This document lists all API endpoints exposed by the microservices in this project, including those routed via the nginx reverse proxy.

---

## Nginx Reverse Proxy Endpoints

These endpoints are accessible externally and are routed to the respective microservices. The base URL for all endpoints is:

**http://localhost**

| Path Prefix      | Proxied To                                 |
|------------------|---------------------------------------------|
| `/auth`         | `http://authentication-service:8080`       |
| `/courses`      | `http://coursemgmt-service:8080/courses`   |
| `/chapters`     | `http://coursemgmt-service:8080/chapters`  |
| `/upload`       | `http://upload-service:8080`               |

---

## Microservices API Endpoints

### Authentication Service
- Base URL: `http://localhost/auth/`
- **Endpoints:**
  - *(To be filled: List all upload-related endpoints here)*

### Course Management Service
- Base URLs: `http://localhost/courses/`, `http://localhost/chapters/`
- **Endpoints:**

  **Course Endpoints (`/courses`):**
  - `GET /courses` — Get all courses
  - `GET /courses/user/{userId}` — Get all courses for a user
  - `GET /courses/{courseId}` — Get course by ID
  - `GET /courses/{courseId}/chapters` — Get all chapters for a course
  - `POST /courses` — Create a new course
  - `POST /courses/{courseId}/chapters` — Create a new chapter in a course
  - `PUT /courses/{courseId}` — Update a course
  - `PATCH /courses/{courseId}` — Patch (partially update) a course
  - `DELETE /courses/{courseId}` — Delete a course

  **Chapter Endpoints (`/chapters`):**
  - `GET /chapters` — Get all chapters
  - `GET /chapters/{chapter_id}` — Get chapter by ID
  - `POST /chapters` — Create a new chapter
  - `PUT /chapters/{chapter_id}` — Update a chapter
  - `PATCH /chapters/{chapter_id}` — Patch (partially update) a chapter
  - `DELETE /chapters/{chapter_id}` — Delete a chapter

### Upload Service
- Base URL: `http://localhost/upload/`
- **Endpoints:**
  - *(To be filled: List all upload-related endpoints here)*

### Summary Service
- Base URL: `http://127.0.0.1:8082/summarize`
- **Endpoints:**
  - `POST /summarize` — Summarize provided content

---

> **Note:**
> All endpoints are accessible via `http://localhost` as the base URL, except the summary service which runs on `http://127.0.0.1:8000`.
