import { create } from "zustand"

interface CourseStore {
  courses: Course[]
  isLoading: boolean
  set: (courses: Course[]) => void
  add: (course: CourseCreationForm) => Promise<string>
  fetchCourses: () => Promise<void>
  fetchCourse: (courseId: string) => Promise<Course>
  updateCourse: (courseId: string, course: Partial<Course>) => Promise<Course>
}

const useCourseStore = create<CourseStore>()((set, get) => ({
  courses: [] as Course[],
  isLoading: true,
  set: (courses: Course[]) => set({ courses }),
  add: async (course: CourseCreationForm) => {
    course.userId = "1" // TODO: Assuming a static user ID for now
    set(() => ({ isLoading: true }))
    const response = await fetch("http://localhost:8080/courses", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(course),
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
      const response = await fetch("http://localhost:8080/courses")
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
      const response = await fetch(`http://localhost:8080/courses/${courseId}`)
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
      const response = await fetch(`http://localhost:8080/courses/${courseId}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(courseUpdate),
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
}))

export default useCourseStore
