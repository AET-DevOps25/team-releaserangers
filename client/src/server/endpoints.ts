// Runtime API URL determination - works with pre-built Docker images
const getApiUrl = () => {
  // Server-side: use environment variable
  if (typeof window === "undefined") {
    return process.env.NEXT_PUBLIC_API_URL || ""
  }

  // Client-side: construct from current hostname
  if (typeof window !== "undefined") {
    const hostname = window.location.hostname
    // Extract base domain (remove 'client.' prefix if present)
    const baseDomain = hostname.replace(/^client\./, "")
    return `https://api.${baseDomain}`
  }

  return ""
}

export const COURSES_ENDPOINT = () => `${getApiUrl()}/courses`
export const COURSE_ENDPOINT = (courseId: string) => `${getApiUrl()}/courses/${courseId}`
export const CHAPTER_ENDPOINT = (chapterId: string) => `${getApiUrl()}/chapters/${chapterId}`
export const FAVORITES_ENDPOINT = () => `${getApiUrl()}/favorites`
export const USER_ENDPOINT = () => `${getApiUrl()}/auth/user`
export const SIGNIN_ENDPOINT = () => `${getApiUrl()}/auth/signin`
export const SIGNUP_ENDPOINT = () => `${getApiUrl()}/auth/signup`
export const SIGNOUT_ENDPOINT = () => `${getApiUrl()}/auth/signout`
export const UPLOAD_ENDPOINT = () => `${getApiUrl()}/upload`
