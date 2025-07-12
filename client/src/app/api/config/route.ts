import { NextResponse } from "next/server"

export async function GET() {
  return NextResponse.json({
    jwtSecret: process.env.NEXT_JWT_SECRET || process.env.JWT_SECRET || "",
  })
}
