import { SIGNIN_ENDPOINT, SIGNOUT_ENDPOINT, SIGNUP_ENDPOINT, USER_ENDPOINT } from "@/server/endpoints"
import { authenticatedFetcher } from "./authenticated-fetcher"
import useSWR, { mutate } from "swr"
import { useState } from "react"

export function useUser() {
  const { data, error, isLoading } = useSWR<User>(USER_ENDPOINT, authenticatedFetcher)

  return {
    user: data || null,
    isLoading,
    error: error,
    refetch: () => {
      mutate(USER_ENDPOINT)
    },
  }
}

export function useUpdateUser() {
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<Error | null>(null)

  const updateUser = async (userUpdate: Partial<User>) => {
    setIsLoading(true)
    setError(null)

    try {
      const response = await fetch(USER_ENDPOINT, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(userUpdate),
        credentials: "include",
      })
      if (!response.ok) {
        throw new Error(response.statusText)
      }
      const updatedUser = await response.json()
      mutate(USER_ENDPOINT, updatedUser, false)
      return updatedUser
    } catch (err) {
      console.error("Error updating user:", err)
      setError(err as Error)
      throw err
    } finally {
      setIsLoading(false)
    }
  }

  return { updateUser, isLoading, error }
}

export function useDeleteUser() {
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<Error | null>(null)

  const deleteUser = async () => {
    setIsLoading(true)
    setError(null)

    try {
      const response = await fetch(USER_ENDPOINT, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
      })
      if (!response.ok) {
        throw new Error(response.statusText)
      }
      mutate(USER_ENDPOINT, null, false)
    } catch (err) {
      console.error("Error deleting user:", err)
      setError(err as Error)
      throw err
    } finally {
      setIsLoading(false)
    }
  }

  return { deleteUser, isLoading, error }
}

export function useSignUp() {
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<Error | null>(null)

  const signUp = async (name: string, email: string, password: string) => {
    setIsLoading(true)
    setError(null)

    try {
      const response = await fetch(SIGNUP_ENDPOINT, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ name, email, password }),
        credentials: "include",
      })
      if (!response.ok) {
        throw new Error(response.statusText)
      }
      const user = await response.json()
      mutate(USER_ENDPOINT, user, false)
      return user
    } catch (err) {
      console.error("Error signing up:", err)
      setError(err as Error)
      throw err
    } finally {
      setIsLoading(false)
    }
  }

  return { signUp, isLoading, error }
}

export function useSignIn() {
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<Error | null>(null)

  const signIn = async (email: string, password: string) => {
    setIsLoading(true)
    setError(null)

    try {
      const response = await fetch(SIGNIN_ENDPOINT, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
        credentials: "include",
      })
      if (!response.ok) {
        throw new Error(response.statusText)
      }
      const user = await response.json()
      mutate(USER_ENDPOINT, user, false)
      return user
    } catch (err) {
      console.error("Error signing in:", err)
      setError(err as Error)
      throw err
    } finally {
      setIsLoading(false)
    }
  }

  return { signIn, isLoading, error }
}

export function useSignOut() {
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<Error | null>(null)

  const signOut = async () => {
    setIsLoading(true)
    setError(null)

    try {
      const response = await fetch(SIGNOUT_ENDPOINT, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
      })
      if (!response.ok) {
        throw new Error(response.statusText)
      }
      mutate(USER_ENDPOINT, null, false)
    } catch (err) {
      console.error("Error signing out:", err)
      setError(err as Error)
      throw err
    } finally {
      setIsLoading(false)
    }
  }

  return { signOut, isLoading, error }
}
