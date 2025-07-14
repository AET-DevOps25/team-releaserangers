"use client"

import type React from "react"

import { useRouter } from "next/navigation"
import { Calendar, ChevronRight } from "lucide-react"
import { formatDistanceToNow } from "date-fns"

import { Card, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { useCourses } from "@/hooks/courseAPI"
import { ButtonType, CourseCreationDialog } from "./course-creation-dialog"

export function RecentCourses() {
  const router = useRouter()
  const { courses, isLoading } = useCourses()

  const handleCourseClick = (courseId: string) => {
    router.push(`/${courseId}`)
  }

  // Sort courses by last edited time (most recent first) and limit to 4
  const sortedCourses = [...courses].sort((a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime()).slice(0, 4)

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-semibold">Continue Learning</h2>
        <CourseCreationDialog buttonType={ButtonType.Home} />
      </div>

      <div className="grid gap-4 md:grid-cols-2">
        {isLoading
          ? Array.from({ length: 4 }).map((_, index) => (
              <Card key={`loading-${index}`} className="animate-pulse">
                <CardHeader className="pb-2">
                  <CardTitle className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <div className="h-6 w-6 rounded bg-muted"></div>
                      <div className="h-4 w-32 rounded bg-muted"></div>
                    </div>
                    <div className="h-5 w-5 rounded bg-muted"></div>
                  </CardTitle>
                  <div className="h-4 w-full rounded bg-muted mt-2"></div>
                  <div className="h-4 w-3/4 rounded bg-muted mt-1"></div>
                </CardHeader>
                <CardFooter className="pt-0 flex justify-between items-center">
                  <div className="flex items-center gap-1">
                    <div className="h-3 w-3 rounded bg-muted"></div>
                    <div className="h-3 w-24 rounded bg-muted"></div>
                  </div>
                </CardFooter>
              </Card>
            ))
          : sortedCourses.map((course) => (
              <Card key={course.id} className="cursor-pointer transition-all hover:border-primary" onClick={() => handleCourseClick(course.id)}>
                <CardHeader className="pb-2">
                  <CardTitle className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <span className="text-lg">{course.emoji}</span>
                      <span className="line-clamp-1">{course.name}</span>
                    </div>
                    <ChevronRight className="h-5 w-5 text-muted-foreground" />
                  </CardTitle>
                  <CardDescription className="line-clamp-2">{course.description}</CardDescription>
                </CardHeader>
                <CardFooter className="pt-0 flex justify-between items-center">
                  <div className="flex items-center text-xs text-muted-foreground">
                    <Calendar className="mr-1 h-3 w-3" />
                    <span>Last edited {formatDistanceToNow(new Date(course.updatedAt + "Z"))} ago</span>
                  </div>
                </CardFooter>
              </Card>
            ))}
      </div>
    </div>
  )
}
