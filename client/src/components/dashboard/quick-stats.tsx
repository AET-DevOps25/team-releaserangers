"use client"

import { Book, Clock, Library, Star } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { useCourses } from "@/hooks/courseAPI"
import { useFavorites } from "@/hooks/useFavorites"
import { formatDistanceToNow } from "date-fns"

export function QuickStats() {
  const { courses, isLoading } = useCourses()
  const totalChapters = courses.reduce((acc, course) => acc + course.chapters.length, 0)
  const lastInteraction =
    courses.length > 0
      ? (() => {
          const latest = courses.reduce((latest, course) => {
            const courseUpdated = new Date(course.updatedAt.endsWith("Z") ? course.updatedAt : course.updatedAt + "Z")
            let latestDate = courseUpdated > latest ? courseUpdated : latest

            // Check chapters for more recent updates
            course.chapters.forEach((chapter) => {
              const chapterUpdated = new Date(chapter.updatedAt.endsWith("Z") ? chapter.updatedAt : chapter.updatedAt + "Z")
              if (chapterUpdated > latestDate) {
                latestDate = chapterUpdated
              }
            })

            return latestDate
          }, new Date(0))
          return latest.getTime() === 0 ? null : latest
        })()
      : null
  const { favorites, isLoading: isFavoritesLoading } = useFavorites()

  return (
    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
      <Card key={"total-courses"}>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium">Courses</CardTitle>
          <Book className="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="animate-pulse">
              <div className="h-6 w-full max-w-[30px] rounded-md bg-muted mb-2"></div>
            </div>
          ) : (
            <div className="text-2xl font-bold">{courses.length}</div>
          )}
        </CardContent>
      </Card>
      <Card key={"total-chapters"}>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium">Chapters</CardTitle>
          <Library className="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="animate-pulse">
              <div className="h-6 w-full max-w-[30px] rounded-md bg-muted mb-2"></div>
            </div>
          ) : (
            <div className="text-2xl font-bold">{totalChapters}</div>
          )}
        </CardContent>
      </Card>
      <Card key={"total-favorites"}>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium">Favorites</CardTitle>
          <Star className="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          {isFavoritesLoading ? (
            <div className="animate-pulse">
              <div className="h-6 w-full max-w-[30px] rounded-md bg-muted mb-2"></div>
            </div>
          ) : (
            <div className="text-2xl font-bold">{favorites.length}</div>
          )}
        </CardContent>
      </Card>
      <Card key={"last-interaction"}>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium">Last Interaction</CardTitle>
          <Clock className="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="animate-pulse">
              <div className="h-6 w-full max-w-[100px] rounded-md bg-muted mb-2"></div>
            </div>
          ) : lastInteraction ? (
            <div className="text-2xl font-bold">{formatDistanceToNow(lastInteraction, { addSuffix: true })}</div>
          ) : (
            <div className="text-2xl font-bold">No activity</div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
