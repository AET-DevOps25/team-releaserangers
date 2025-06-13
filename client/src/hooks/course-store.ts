import { create } from "zustand"

interface CourseStore {
  courses: Course[]
  isLoading: boolean
  set: (courses: Course[]) => void
  add: (course: CourseCreationForm) => Promise<string>
  fetchCourses: () => Promise<void>
  fetchCourse: (courseId: string) => Promise<Course>
  updateCourse: (courseId: string, course: Partial<Course>) => Promise<Course>
  fetchChapter: (courseId: string, chapterId: string) => Promise<Chapter>
}

const useCourseStore = create<CourseStore>()((set, get) => ({
  courses: [] as Course[],
  isLoading: true,
  set: (courses: Course[]) => set({ courses }),
  add: async (course: CourseCreationForm) => {
    set(() => ({ isLoading: true }))
    const response = await fetch("http://localhost/courses", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(course),
      credentials: "include",
    })
    if (!response.ok) {
      throw new Error("Failed to add course")
    }
    await get().fetchCourses()
    return response.json().then((data) => {
      return data.id
    })
  },
  fetchCourses: async () => {
    set({ isLoading: true })
    try {
      const response = await fetch("http://localhost/courses", {
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
      })
      if (!response.ok) {
        throw new Error("Failed to fetch courses")
      }
      if (response.status === 204) {
        set({ courses: [], isLoading: false })
        return
      }
      const data: Course[] = await response.json()
      set({ courses: data, isLoading: false })
    } catch (error) {
      console.error("Error fetching courses:", error)
      set({ isLoading: false })
      throw error
    }
  },
  fetchCourse: async (courseId: string) => {
    set({ isLoading: true })
    try {
      const response = await fetch(`http://localhost/courses/${courseId}`, {
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
      })
      if (!response.ok) {
        throw new Error("Failed to fetch course")
      }
      const data: Course = await response.json()

      if (!data.chapters) {
        data.chapters = []
      }

      set((state) => {
        const courseExists = state.courses.some((c) => c.id === data.id)
        let updatedCourses = state.courses

        if (courseExists) {
          updatedCourses = state.courses.map((course) => (course.id === data.id ? data : course))
        } else {
          updatedCourses = [...state.courses, data]
        }

        return { courses: updatedCourses, isLoading: false }
      })

      return data
    } catch (error) {
      console.error("Error fetching course:", error)
      set({ isLoading: false })
      throw error
    }
  },
  updateCourse: async (courseId: string, courseUpdate: Partial<Course>) => {
    set({ isLoading: true })
    try {
      const response = await fetch(`http://localhost/courses/${courseId}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(courseUpdate),
        credentials: "include",
      })

      if (!response.ok) {
        throw new Error("Failed to update course")
      }

      const updatedCourse: Course = await response.json()

      set((state) => ({
        courses: state.courses.map((course) => (course.id === updatedCourse.id ? updatedCourse : course)),
        isLoading: false,
      }))

      return updatedCourse
    } catch (error) {
      console.error("Error updating course:", error)
      set({ isLoading: false })
      throw error
    }
  },
  fetchChapter: async (courseId: string, chapterId: string) => {
    set({ isLoading: true })
    try {
      const response = await fetch(`http://localhost/chapters/${chapterId}`, {
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
      })
      if (!response.ok) {
        throw new Error("Failed to fetch chapter")
      }
      const data: Chapter = await response.json()

      set((state) => {
        const courseExists = state.courses.some((c) => c.id === courseId)
        let updatedCourses = state.courses
        const chapterExists = courseExists && state.courses.find((c) => c.id === courseId)?.chapters?.some((ch) => ch.id === data.id)
        if (chapterExists) {
          updatedCourses = state.courses.map((course) => {
            if (course.id === courseId) {
              return {
                ...course,
                chapters: course.chapters.map((ch) => (ch.id === data.id ? data : ch)),
              }
            }
            return course
          })
        } else if (courseExists) {
          updatedCourses = state.courses.map((course) => {
            if (course.id === courseId) {
              return {
                ...course,
                chapters: [...(course.chapters || []), data],
              }
            }
            return course
          })
        }

        return { courses: updatedCourses, isLoading: false }
      })

      return data
    } catch (error) {
      console.error("Error fetching course:", error)
      set({ isLoading: false })
      throw error
    }
  },
}))

export default useCourseStore
