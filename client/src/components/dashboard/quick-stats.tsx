"use client"

import { BookOpen, Clock, Target, TrendingUp } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import useCourseDataStore from "@/hooks/course-store"
import { useState, useEffect } from "react"

// use state to manage course length

const stats = [
  {
    title: "Study Time",
    value: "24h",
    description: "This week",
    icon: Clock,
    trend: "+4h from last week",
  },
  {
    title: "Completed",
    value: "8",
    description: "Courses finished",
    icon: Target,
    trend: "67% completion rate",
  },
  {
    title: "Streak",
    value: "7",
    description: "Days in a row",
    icon: TrendingUp,
    trend: "Personal best!",
  },
]

export function QuickStats() {
  const { courses } = useCourseDataStore()
  const [courseLength, setCourseLength] = useState(courses.length)

  useEffect(() => {
    setCourseLength(courses.length)
  }, [courses])

  return (
    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
      <Card key={"test"}>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium">{"Total Courses"}</CardTitle>
          <BookOpen className="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          <div className="text-2xl font-bold">{courseLength}</div>
          <p className="text-xs text-muted-foreground">{"3 in progress"}</p>
          <p className="text-xs text-green-600 mt-1">{"+2 this month"}</p>
        </CardContent>
      </Card>
      {stats.map((stat) => (
        <Card key={stat.title}>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">{stat.title}</CardTitle>
            <stat.icon className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{stat.value}</div>
            <p className="text-xs text-muted-foreground">{stat.description}</p>
            <p className="text-xs text-green-600 mt-1">{stat.trend}</p>
          </CardContent>
        </Card>
      ))}
    </div>
  )
}
