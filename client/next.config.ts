import type { NextConfig } from "next"

const nextConfig: NextConfig = {
  output: "standalone", // needed for docker
  env: {
    NEXT_PUBLIC_API_URL: process.env.NEXT_PUBLIC_API_URL || "",
  },
}

export default nextConfig
