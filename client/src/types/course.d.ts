interface Course {
  id: string
  name: string
  description?: string
  chapters: Chapter[]
  emoji: string
  isFavorite: boolean
  createdAt: string
  updatedAt: string
}

interface CourseCreationForm {
  userId?: string
  name: string
  description: string
  emoji: string
}

interface Chapter {
  id: string
  title: string
  content?: string
  emoji: string
  isFavorite: boolean
  createdAt: string
  updatedAt: string
}
