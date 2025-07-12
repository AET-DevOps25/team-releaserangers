import { NextRequest, NextResponse } from "next/server"
import { JWTPayload, jwtVerify } from "jose"

export const dynamic = "force-dynamic"

// 1. Specify protected and public routes
const protectedRoutes = ["/dashboard"]
const publicRoutes = ["/login", "/signup", "/"]

// Function to get JWT secret at runtime
const getJwtSecret = () => {
  // Try different environment variable names that might be available at runtime
  return process.env.NEXT_PUBLIC_JWT_SECRET || process.env.JWT_SECRET || ""
}

export default async function middleware(req: NextRequest) {
  // 2. Check if the current route is protected or public
  const path = req.nextUrl.pathname
  const isProtectedRoute = protectedRoutes.includes(path)
  const isPublicRoute = publicRoutes.includes(path)

  // 3. Extract JWT from cookie
  const token = req.cookies.get("token")?.value || null
  let session: JWTPayload | null = null
  if (token) {
    try {
      const jwtSecret = getJwtSecret()
      if (!jwtSecret) {
        console.error("JWT secret not available")
        session = null
      } else {
        const secret = new TextEncoder().encode(jwtSecret)
        const { payload } = await jwtVerify(token, secret)
        session = payload
      }
    } catch (e) {
      console.error("JWT verification failed:", e)
      session = null
    }
  }

  // 4. Redirect to /login if the user is not authenticated
  if (isProtectedRoute && !session) {
    return NextResponse.redirect(new URL("/login", req.nextUrl))
  }

  // 5. Redirect to /dashboard if the user is authenticated
  if (isPublicRoute && session && !req.nextUrl.pathname.startsWith("/dashboard")) {
    return NextResponse.redirect(new URL("/dashboard", req.nextUrl))
  }

  return NextResponse.next()
}

// Routes Middleware should not run on
export const config = {
  matcher: ["/((?!api|_next/static|_next/image|.*\\.png$).*)", "/runtime-config.js"],
}
