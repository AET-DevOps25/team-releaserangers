import type { NextConfig } from "next"

const nextConfig: NextConfig = {
  output: "standalone", // needed for docker
}

export default nextConfig
