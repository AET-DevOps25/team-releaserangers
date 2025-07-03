# Database Schema Documentation

This document describes the database schema for the DevOps 2025 Team ReleaseRangers learning management system.

## Schema Overview

The database consists of 4 main tables across 3 microservices:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│     users       │    │     courses     │    │    chapters     │    │ uploaded_files  │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ id (PK)         │◄───┤ user_id         │◄───┤ course_id (FK)  │    │ course_id       │
│ email (UNIQUE)  │    │ course_id (PK)  │    │ chapter_id (PK) │    │ id (PK)         │
│ name            │    │ course_name     │    │ chapter_title   │    │ filename        │
│ password        │    │ course_desc     │    │ chapter_content │    │ content_type    │
│ created_at      │    │ course_emoji    │    │ chapter_emoji   │    │ data (BLOB)     │
│ updated_at      │    │ is_favorite     │    │ is_favorite     │    │ created_at      │
└─────────────────┘    │ created_at      │    │ created_at      │    │ updated_at      │
                       │ updated_at      │    │ updated_at      │    └─────────────────┘
                       └─────────────────┘    └─────────────────┘            │
                                │                       │                     │
                                └───────────────────────┼─────────────────────┘
                                                        │
                                                   (Soft Reference)
```

## Table Details

### users (Authentication Service)
- **Purpose**: Store user accounts for authentication
- **Primary Key**: `id` (auto-increment)
- **Unique Constraints**: `email`

### courses (Course Management Service)  
- **Purpose**: Store course information created by users
- **Primary Key**: `course_id` (UUID)
- **Relationships**: 
  - Belongs to user (soft reference via `user_id`)
  - Has many chapters (1:N with cascade delete)
  - Has many uploaded files (1:N)

### chapters (Course Management Service)
- **Purpose**: Store chapter content within courses
- **Primary Key**: `chapter_id` (UUID)  
- **Relationships**: 
  - Belongs to course (foreign key with cascade delete)

### uploaded_files (Upload Service)
- **Purpose**: Store file uploads associated with courses
- **Primary Key**: `id` (UUID)
- **Relationships**: 
  - Associated with course (soft reference via `course_id`)

## Key Features

1. **Microservices Architecture**: Tables are distributed across 3 services
2. **Soft References**: Cross-service relationships use IDs without foreign key constraints
3. **Audit Trail**: All tables have `created_at` and `updated_at` timestamps
4. **Favorites**: Both courses and chapters can be marked as favorites
5. **Cascade Deletion**: Deleting a course removes all its chapters
6. **File Storage**: Binary file data stored directly in database

## Data Types

- **IDs**: UUIDs for most entities, auto-increment for users
- **Timestamps**: `LocalDateTime` managed by Hibernate annotations
- **Text Content**: `TEXT` type for chapter content
- **Binary Data**: `BLOB` type for file storage
- **Booleans**: For favorite flags with default false values

## DBML Schema

The complete database schema is also available in DBML format: [database_schema.dbml](./database_schema.dbml)

This DBML file can be imported into [dbdiagram.io](https://dbdiagram.io) to generate visual diagrams.