const apiUrl = process.env.NEXT_PUBLIC_API_URL
export const COURSES_ENDPOINT = `${apiUrl}/courses`
export const COURSE_ENDPOINT = (courseId: string) => `${apiUrl}/courses/${courseId}`
export const CHAPTER_ENDPOINT = (chapterId: string) => `${apiUrl}/chapters/${chapterId}`
export const FAVORITES_ENDPOINT = `${apiUrl}/favorites`
export const USER_ENDPOINT = `${apiUrl}/auth/user`
export const SIGNIN_ENDPOINT = `${apiUrl}/auth/signin`
export const SIGNUP_ENDPOINT = `${apiUrl}/auth/signup`
export const SIGNOUT_ENDPOINT = `${apiUrl}/auth/signout`
export const UPLOAD_ENDPOINT = `${apiUrl}/upload`
