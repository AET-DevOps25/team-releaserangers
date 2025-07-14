import { CHAPTER_ENDPOINT, COURSE_ENDPOINT } from "@/server/endpoints"
import useSWR, { mutate } from "swr"
import { authenticatedFetcher } from "./authenticated-fetcher"
import { useState } from "react"
import { handleUnauthorized } from "./handle-unauthorized"

export function useChapter(chapterId: string) {
  const { data, error, isLoading } = useSWR<Chapter>(CHAPTER_ENDPOINT(chapterId), authenticatedFetcher)

  // if (!data.chapters) {
  //   data.chapters = []
  // }

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
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<Error | null>(null)

  const updateChapter = async (chapterId: string, courseId: string, chapterUpdate: Partial<Chapter>) => {
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
      const updatedChapter: Chapter = await response.json()
      mutate(CHAPTER_ENDPOINT(chapterId), updatedChapter, false)
      mutate(COURSE_ENDPOINT(courseId)) // Update the course cache after chapter update
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
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<Error | null>(null)

  const deleteChapter = async (chapterId: string) => {
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
