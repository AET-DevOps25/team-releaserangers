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

  return response.json()
}
