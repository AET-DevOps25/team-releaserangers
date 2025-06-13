"use client"

import { BookOpen, Search, Upload } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { ButtonType, CourseCreationDialog } from "./course-creation-dialog"

export function QuickActions() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">Quick Actions</CardTitle>
        <CardDescription>Get started with common tasks</CardDescription>
      </CardHeader>
      <CardContent className="space-y-3">
        <CourseCreationDialog buttonType={ButtonType.QuickStats} />

        <Button variant="outline" className="w-full justify-start gap-2">
          <Upload className="h-4 w-4" />
          Upload Materials
        </Button>

        <Button variant="outline" className="w-full justify-start gap-2">
          <Search className="h-4 w-4" />
          Browse Courses
        </Button>

        <Button variant="outline" className="w-full justify-start gap-2">
          <BookOpen className="h-4 w-4" />
          View Templates
        </Button>
      </CardContent>
    </Card>
  )
}
