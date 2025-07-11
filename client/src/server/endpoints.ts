// Type declaration for runtime config
declare global {
  interface Window {
    __RUNTIME_CONFIG__?: {
      API_URL?: string
    }
  }
}

// Get API URL at runtime - fallback to build-time variable for backwards compatibility
const getApiUrl = () => {
  // Check if we're in browser environment
  if (typeof window !== "undefined") {
    // In browser, try to get from window object (set by runtime config)
    return window.__RUNTIME_CONFIG__?.API_URL || process.env.NEXT_PUBLIC_API_URL
  }
  // On server side, use environment variable
  return process.env.API_URL || process.env.NEXT_PUBLIC_API_URL
}

const apiUrl = getApiUrl()

export const COURSES_ENDPOINT = `${apiUrl}/courses`
export const COURSE_ENDPOINT = (courseId: string) => `${apiUrl}/courses/${courseId}`
export const CHAPTER_ENDPOINT = (chapterId: string) => `${apiUrl}/chapters/${chapterId}`
export const FAVORITES_ENDPOINT = `${apiUrl}/favorites`
export const USER_ENDPOINT = `${apiUrl}/auth/user`
export const SIGNIN_ENDPOINT = `${apiUrl}/auth/signin`
export const SIGNUP_ENDPOINT = `${apiUrl}/auth/signup`
export const SIGNOUT_ENDPOINT = `${apiUrl}/auth/signout`
export const UPLOAD_ENDPOINT = `${apiUrl}/upload`
