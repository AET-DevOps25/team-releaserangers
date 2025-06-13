"use client"

import { create } from "zustand"

interface UserStore {
  user: User | null
  setUser: (user: User | null) => void
  fetchUser: () => Promise<void>
  updateUser: (user: Partial<User>) => Promise<User>
  deleteUser: () => Promise<void>
  signUp: (name: string, email: string, password: string) => Promise<void>
  signIn: (email: string, password: string) => Promise<void>
  signOut: () => Promise<void>
}

const useUserStore = create<UserStore>()((set) => ({
  user: null,
  setUser: (user: User | null) => set({ user }),
  fetchUser: async () => {
    try {
      const response = await fetch("http://localhost/auth/user", {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
      })
      if (!response.ok) {
        throw new Error("Failed to fetch user")
      }
      const data: User = await response.json()
      set({ user: data })
    } catch (error) {
      console.error("Error fetching user:", error)
      throw error
    }
  },
  updateUser: async (userUpdates: Partial<User>) => {
    try {
      const response = await fetch("http://localhost/auth/user", {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(userUpdates),
        credentials: "include",
      })
      if (!response.ok) {
        throw new Error("Failed to update user")
      }
      const updatedUser: User = await response.json()
      set({ user: updatedUser })
      return updatedUser
    } catch (error) {
      console.error("Error updating user:", error)
      throw error
    }
  },
  deleteUser: async () => {
    try {
      const response = await fetch("http://localhost/auth/user", {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
      })
      if (!response.ok) {
        throw new Error("Failed to delete user")
      }
      set({ user: null })
    } catch (error) {
      console.error("Error deleting user:", error)
      throw error
    }
  },
  signUp: async (name: string, email: string, password: string) => {
    try {
      const response = await fetch("http://localhost/auth/signup", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ name, email, password }),
        credentials: "include",
      })
      if (!response.ok) {
        throw new Error("Failed to sign up")
      }
      const user: User = await response.json()
      set({ user })
    } catch (error) {
      console.error("Error signing up:", error)
      throw error
    }
  },
  signIn: async (email: string, password: string) => {
    try {
      const response = await fetch("http://localhost/auth/signin", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
        credentials: "include",
      })
      if (!response.ok) {
        throw new Error("Failed to sign in")
      }
      const user: User = await response.json()
      set({ user })
    } catch (error) {
      console.error("Error signing in:", error)
      throw error
    }
  },
  signOut: async () => {
    try {
      const response = await fetch("http://localhost/auth/signout", {
        method: "POST",
        credentials: "include",
      })
      if (!response.ok) {
        throw new Error("Failed to sign out")
      }
      set({ user: null })
    } catch (error) {
      console.error("Error signing out:", error)
      throw error
    }
  },
}))
export default useUserStore
