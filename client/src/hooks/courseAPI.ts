import { COURSE_ENDPOINT, COURSES_ENDPOINT } from "@/server/endpoints"
import useSWR, { mutate } from "swr"
import { authenticatedFetcher } from "./authenticated-fetcher"
import { useState, useEffect } from "react"
import useCourseDataStore from "./course-store"
import { handleUnauthorized } from "./handle-unauthorized"

export function useCourses() {
  const { setCourses } = useCourseDataStore()
  const { data, error, isLoading } = useSWR<Course[], Error>(COURSES_ENDPOINT, authenticatedFetcher)

  useEffect(() => {
    if (data) {
      setCourses(data)
    }
  }, [data, setCourses])

  return {
    courses: data || [],
    isLoading,
    error: error,
    refetch: () => {
      mutate(COURSES_ENDPOINT)
    },
  }
}

export function useCourse(courseId: string) {
  const { setCourse } = useCourseDataStore()
  const { data, error, isLoading } = useSWR<Course>(COURSE_ENDPOINT(courseId), authenticatedFetcher)

  useEffect(() => {
    if (data) {
      setCourse(data)
    }
  }, [data, setCourse])

  return {
    course: data || null,
    isLoading,
    error: error,
    refetch: () => {
      mutate(COURSE_ENDPOINT(courseId))
    },
  }
}

export function useUpdateCourse() {
  const { setCourse } = useCourseDataStore()
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<Error | null>(null)

  const updateCourse = async (courseId: string, courseUpdate: Partial<Course>) => {
    setIsLoading(true)
    try {
      const response = await fetch(COURSE_ENDPOINT(courseId), {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(courseUpdate),
        credentials: "include",
      })
      if (response.status === 401) {
        handleUnauthorized()
        setIsLoading(false)
        return Promise.reject("Unauthorized access")
      }
      if (!response.ok) {
        throw new Error(response.statusText)
      }
      const updatedCourse: Course = await response.json()
      // mutate
      mutate(COURSE_ENDPOINT(courseId), updatedCourse, false) // Optimistically update the cache
      setCourse(updatedCourse) // Update the course in the store
      return updatedCourse
    } catch (err) {
      const error = err as Error
      console.error("Error updating course:", error)
      setError(error)
      throw error
    } finally {
      setIsLoading(false)
    }
  }

  return {
    updateCourse,
    isLoading,
    error,
  }
}

export function useDeleteCourse() {
  const { deleteCourse: deleteCourseFromStore } = useCourseDataStore()
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<Error | null>(null)

  const deleteCourse = async (courseId: string) => {
    setIsLoading(true)
    try {
      const response = await fetch(COURSE_ENDPOINT(courseId), {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
      })
      if (response.status === 401) {
        handleUnauthorized()
        setIsLoading(false)
        return Promise.reject("Unauthorized access")
      }
      if (!response.ok) {
        throw new Error(response.statusText)
      }
      mutate(COURSES_ENDPOINT) // Revalidate the courses list
      deleteCourseFromStore(courseId) // Remove the course from the store
    } catch (error) {
      console.error("Error deleting course:", error)
      setError(error as Error)
      throw error
    } finally {
      setIsLoading(false)
    }
  }

  return {
    deleteCourse,
    isLoading,
    error,
  }
}

export function useCreateCourse() {
  const { setCourses } = useCourseDataStore()
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<Error | null>(null)

  const createCourse = async (courseData: CourseCreationForm) => {
    setIsLoading(true)
    try {
      const response = await fetch(COURSES_ENDPOINT, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(courseData),
        credentials: "include",
      })
      if (response.status === 401) {
        handleUnauthorized()
        setIsLoading(false)
        return Promise.reject("Unauthorized access")
      }
      if (!response.ok) {
        throw new Error(response.statusText)
      }
      const newCourse: Course = await response.json()

      // Update the courses list in the cache
      mutate(COURSES_ENDPOINT)
      const { courses } = useCourseDataStore.getState()
      setCourses([...courses, newCourse])
      return newCourse
    } catch (err) {
      const error = err as Error
      console.error("Error creating course:", error)
      setError(error)
      throw error
    } finally {
      setIsLoading(false)
    }
  }

  return {
    createCourse,
    isLoading,
    error,
  }
}
