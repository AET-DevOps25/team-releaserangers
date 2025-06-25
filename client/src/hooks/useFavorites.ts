import { FAVORITES_ENDPOINT } from "@/server/endpoints"
import useSWR, { mutate } from "swr"
import { authenticatedFetcher } from "./authenticated-fetcher"

export function useFavorites() {
  const { data, error, isLoading } = useSWR<Favorite[], Error>(FAVORITES_ENDPOINT, authenticatedFetcher)

  return {
    favorites: data || [],
    isLoading,
    error: error,
    refetch: () => {
      mutate(FAVORITES_ENDPOINT)
    },
  }
}
