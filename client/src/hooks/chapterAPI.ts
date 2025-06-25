import { CHAPTER_ENDPOINT } from "@/server/endpoints"
import useSWR, { mutate } from "swr"
import { authenticatedFetcher } from "./authenticated-fetcher"
import useCourseDataStore from "./course-store"
import { useEffect, useState } from "react"
import { handleUnauthorized } from "./handle-unauthorized"

export function useChapter(courseId: string, chapterId: string) {
  const { setChapter } = useCourseDataStore()
  const { data, error, isLoading } = useSWR<Chapter>(CHAPTER_ENDPOINT(chapterId), authenticatedFetcher)

  // if (!data.chapters) {
  //   data.chapters = []
  // }

  useEffect(() => {
    if (data) {
      setChapter(courseId, data)
    }
  }, [data, setChapter, courseId])

  return {
    chapter: data || null,
    isLoading,
    error: error,
    refetch: () => {
      mutate(CHAPTER_ENDPOINT(chapterId))
    },
  }
}

export function useUpdateChapter() {
  const { setChapter } = useCourseDataStore()
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<Error | null>(null)

  const updateChapter = async (courseId: string, chapterId: string, chapterUpdate: Partial<Chapter>) => {
    setIsLoading(true)
    setError(null)

    try {
      const response = await fetch(CHAPTER_ENDPOINT(chapterId), {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(chapterUpdate),
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
      const updatedChapter = await response.json()
      mutate(CHAPTER_ENDPOINT(chapterId), updatedChapter, false)
      setChapter(courseId, updatedChapter)
    } catch (err) {
      console.error("Error updating chapter:", err)
      setError(err as Error)
    } finally {
      setIsLoading(false)
    }
  }

  return {
    updateChapter,
    isLoading,
    error,
  }
}

export function useDeleteChapter() {
  const { deleteChapter: deleteChapterFromStore } = useCourseDataStore()
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<Error | null>(null)

  const deleteChapter = async (courseId: string, chapterId: string) => {
    setIsLoading(true)
    setError(null)

    try {
      const response = await fetch(CHAPTER_ENDPOINT(chapterId), {
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
      mutate(CHAPTER_ENDPOINT(chapterId), null, false)
      deleteChapterFromStore(courseId, chapterId)
    } catch (err) {
      console.error("Error deleting chapter:", err)
      setError(err as Error)
    } finally {
      setIsLoading(false)
    }
  }

  return {
    deleteChapter,
    isLoading,
    error,
  }
}
