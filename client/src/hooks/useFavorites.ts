import { FAVORITES_ENDPOINT } from "@/server/endpoints"
import useSWR, { mutate } from "swr"
import { authenticatedFetcher } from "./authenticated-fetcher"
import useCourseDataStore from "./course-store"
import { useEffect } from "react"

export function useFavorites() {
  const { setFavorites } = useCourseDataStore()
  const { data, error, isLoading } = useSWR<Favorite[], Error>(FAVORITES_ENDPOINT, authenticatedFetcher)

  useEffect(() => {
    if (data) {
      setFavorites(data)
    }
  }, [data, setFavorites])

  return {
    favorites: data || [],
    isLoading,
    error: error,
    refetch: () => {
      mutate(FAVORITES_ENDPOINT)
    },
  }
}
