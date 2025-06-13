"use client"

import type React from "react"

import { useRouter } from "next/navigation"
import { Calendar, ChevronRight, Clock, Play } from "lucide-react"
import { formatDistanceToNow } from "date-fns"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"

const recentCourses = [
  {
    id: "1",
    title: "Introduction to Web Development",
    description: "Learn the fundamentals of web development including HTML, CSS, and JavaScript.",
    emoji: "💻",
    progress: 75,
    lastAccessed: "2023-12-10T14:30:00Z",
    totalChapters: 12,
    completedChapters: 9,
    estimatedTime: "2h 30m remaining",
  },
  {
    id: "2",
    title: "Advanced React Patterns",
    description: "Master advanced React patterns and techniques for building scalable applications.",
    emoji: "⚛️",
    progress: 30,
    lastAccessed: "2023-12-09T16:45:00Z",
    totalChapters: 8,
    completedChapters: 2,
    estimatedTime: "5h 45m remaining",
  },
  {
    id: "3",
    title: "UI/UX Design Principles",
    description: "Learn the core principles of user interface and user experience design.",
    emoji: "🎨",
    progress: 90,
    lastAccessed: "2023-12-08T10:15:00Z",
    totalChapters: 10,
    completedChapters: 9,
    estimatedTime: "45m remaining",
  },
  {
    id: "4",
    title: "Data Science Fundamentals",
    description: "Introduction to data science, statistics, and machine learning concepts.",
    emoji: "📊",
    progress: 15,
    lastAccessed: "2023-12-07T09:20:00Z",
    totalChapters: 15,
    completedChapters: 2,
    estimatedTime: "12h 30m remaining",
  },
]

export function RecentCourses() {
  const router = useRouter()

  const handleCourseClick = (courseId: string) => {
    router.push(`/${courseId}`)
  }

  const handleContinueClick = (courseId: string, e: React.MouseEvent) => {
    e.stopPropagation()
    router.push(`/${courseId}/chapter/current`)
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-semibold">Continue Learning</h2>
        <Button variant="outline" size="sm">
          View All Courses
        </Button>
      </div>

      <div className="grid gap-4 md:grid-cols-2">
        {recentCourses.map((course) => (
          <Card key={course.id} className="cursor-pointer transition-all hover:border-primary" onClick={() => handleCourseClick(course.id)}>
            <CardHeader className="pb-2">
              <CardTitle className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <span className="text-lg">{course.emoji}</span>
                  <span className="line-clamp-1">{course.title}</span>
                </div>
                <ChevronRight className="h-5 w-5 text-muted-foreground" />
              </CardTitle>
              <CardDescription className="line-clamp-2">{course.description}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-3">
              <div className="space-y-1">
                <div className="flex justify-between text-sm">
                  <span>Progress</span>
                  <span>{course.progress}%</span>
                </div>
              </div>

              <div className="flex items-center justify-between text-sm text-muted-foreground">
                <div className="flex items-center gap-1">
                  <span>
                    {course.completedChapters}/{course.totalChapters} chapters
                  </span>
                </div>
                <div className="flex items-center gap-1">
                  <Clock className="h-3 w-3" />
                  <span>{course.estimatedTime}</span>
                </div>
              </div>
            </CardContent>
            <CardFooter className="pt-0 flex justify-between items-center">
              <div className="flex items-center text-xs text-muted-foreground">
                <Calendar className="mr-1 h-3 w-3" />
                <span>Last accessed {formatDistanceToNow(new Date(course.lastAccessed))} ago</span>
              </div>
              <Button size="sm" className="gap-1" onClick={(e) => handleContinueClick(course.id, e)}>
                <Play className="h-3 w-3" />
                Continue
              </Button>
            </CardFooter>
          </Card>
        ))}
      </div>
    </div>
  )
}
