"use client"

import { AppSidebar } from "@/components/dashboard/app-sidebar"
import { NavActionsCourse } from "@/components/dashboard/nav-actions-course"
import { Breadcrumb, BreadcrumbItem, BreadcrumbList, BreadcrumbPage } from "@/components/ui/breadcrumb"
import { Separator } from "@/components/ui/separator"
import { SidebarInset, SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar"
import { ChapterList } from "@/components/dashboard/chapter-list"
import { UploadDropzone } from "@/components/dashboard/upload-dropzone"
import { AddContentButton } from "@/components/dashboard/add-content-button"
import useCourseStore from "@/hooks/course-store"
import { useEffect, useState } from "react"
import { useParams } from "next/navigation"
import Loading from "./loading"
import { Button } from "@/components/ui/button"
import { Plus } from "lucide-react"

export default function CoursePage() {
  const params = useParams<{ courseId: string }>()
  const courseId = params ? (typeof params.courseId === "string" ? params.courseId : "") : ""
  const { fetchCourse, courses } = useCourseStore()
  const [course, setCourse] = useState<Course | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const existingCourse = courses.find((c) => c.id === courseId)
    if (existingCourse) {
      setCourse(existingCourse)
      setLoading(false)
      return
    }

    async function loadCourse() {
      try {
        const courseData = await fetchCourse(courseId)
        setCourse(courseData)
      } catch (error) {
        console.error("Failed to load course:", error)
      } finally {
        setLoading(false)
      }
    }

    loadCourse()
  }, [courseId, fetchCourse, courses])

  if (loading) {
    return <Loading />
  }

  if (!course) {
    return (
      <SidebarProvider>
        <AppSidebar />
        <SidebarInset>
          <div className="flex items-center justify-center h-full">
            <div className="text-center">
              <p className="text-lg font-semibold">Course not found</p>
              <p className="text-muted-foreground">The requested course could not be loaded</p>
            </div>
          </div>
        </SidebarInset>
      </SidebarProvider>
    )
  }

  const hasChapters = course.chapters && course.chapters.length > 0

  return (
    <SidebarProvider>
      <AppSidebar />
      <SidebarInset>
        <header className="flex h-14 shrink-0 items-center gap-2">
          <div className="flex flex-1 items-center gap-2 px-3">
            <SidebarTrigger />
            <Separator orientation="vertical" className="mr-2 h-4" />
            <Breadcrumb>
              <BreadcrumbList>
                <BreadcrumbItem>
                  <BreadcrumbPage className="line-clamp-1">{course.name}</BreadcrumbPage>
                </BreadcrumbItem>
              </BreadcrumbList>
            </Breadcrumb>
          </div>
          <div className="ml-auto flex items-center gap-4 px-3">
            {hasChapters && (
              <AddContentButton>
                <Button size="sm" className="gap-2">
                  <Plus className="h-4 w-4" />
                  <span>Add Content</span>
                </Button>
              </AddContentButton>
            )}
            <NavActionsCourse course={course} />
          </div>
        </header>
        <div className="flex flex-1 flex-col gap-6 p-6">
          <div className="max-w-4xl mx-auto w-full">
            <div className="mb-8">
              <h1 className="text-3xl font-bold mb-2 flex items-center gap-2">
                <span>{course.emoji || "📚"}</span>
                <span>{course.name}</span>
              </h1>
              {course.description && <p className="text-muted-foreground">{course.description}</p>}
            </div>

            {hasChapters ? (
              <div className="space-y-8">
                <ChapterList chapters={course.chapters} courseId={course.id} />
              </div>
            ) : (
              <div className="space-y-6">
                <UploadDropzone isInDialog={false} />
              </div>
            )}
          </div>
        </div>
      </SidebarInset>
    </SidebarProvider>
  )
}
