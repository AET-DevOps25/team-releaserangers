import { handleUnauthorized } from "./handle-unauthorized"

export const authenticatedFetcher = async (url: string) => {
  const response = await fetch(url, {
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include",
  })

  if (response.status === 204) {
    return null
  }

  if (response.status === 401) {
    handleUnauthorized()
  }

  return response.json()
}
