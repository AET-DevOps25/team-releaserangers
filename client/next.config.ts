import type { NextConfig } from "next"

const nextConfig: NextConfig = {
  output: "standalone", // needed for docker
  env: {
    NEXT_PUBLIC_API_URL: process.env.NEXT_PUBLIC_API_URL || "",
    NEXT_JWT_SECRET: process.env.NEXT_JWT_SECRET || "",
  },
}

export default nextConfig
