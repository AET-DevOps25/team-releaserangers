import { create } from "zustand"

interface CourseDataStore {
  courses: Course[]
  favorites: Favorite[]
  setCourses: (courses: Course[]) => void
  setCourse: (course: Course) => void
  deleteCourse: (courseId: string) => Promise<void>
  setChapter: (courseId: string, chapter: Chapter) => void
  deleteChapter: (courseId: string, chapterId: string) => Promise<void>
  setFavorites: (favorites: Favorite[]) => void
}

// TODO rename this to useDataStore or something more generic
// This store manages courses, chapters, and favorites
const useCourseDataStore = create<CourseDataStore>()((set) => ({
  courses: [] as Course[],
  favorites: [] as Favorite[],
  setCourses: (courses: Course[]) => set({ courses }),
  setCourse: (course: Course) => {
    set((state) => ({
      courses: state.courses.map((c) => (c.id === course.id ? course : c)),
    }))
  },
  deleteCourse: async (courseId: string) => {
    set((state) => ({
      courses: state.courses.filter((course) => course.id !== courseId),
    }))
  },
  setChapter: (courseId: string, chapter: Chapter) => {
    set((state) => ({
      courses: state.courses.map((course) => {
        if (course.id === courseId) {
          return {
            ...course,
            chapters: [...(course.chapters || []), chapter],
          }
        }
        return course
      }),
    }))
  },
  deleteChapter: async (courseId: string, chapterId: string) => {
    set((state) => ({
      courses: state.courses.map((course) => {
        if (course.id === courseId) {
          return {
            ...course,
            chapters: (course.chapters || []).filter((chapter) => chapter.id !== chapterId),
          }
        }
        return course
      }),
    }))
  },
  setFavorites: (favorites: Favorite[]) => set({ favorites }),
}))

export default useCourseDataStore
