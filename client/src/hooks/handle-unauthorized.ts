export function handleUnauthorized() {
  // Handle unauthorized access, e.g., redirect to login or show a message
  if (typeof window !== "undefined") {
    window.location.href = "/login"
  }
}
