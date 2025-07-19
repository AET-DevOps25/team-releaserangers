"use client"

import { AppSidebar } from "@/components/dashboard/app-sidebar"
import { NavActionsCourse } from "@/components/dashboard/nav-actions-course"
import { Breadcrumb, BreadcrumbItem, BreadcrumbList, BreadcrumbPage } from "@/components/ui/breadcrumb"
import { Separator } from "@/components/ui/separator"
import { SidebarInset, SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar"
import { ChapterList } from "@/components/dashboard/chapter-list"
import { UploadDropzone } from "@/components/dashboard/upload-dropzone"
import { AddContentButton } from "@/components/dashboard/add-content-button"
import { useParams } from "next/navigation"
import Loading from "./loading"
import { Button } from "@/components/ui/button"
import { Plus } from "lucide-react"
import { useCourse } from "@/hooks/courseAPI"
import { useEffect } from "react"

export default function CoursePage() {
  const params = useParams<{ courseId: string }>()
  const courseId = params ? (typeof params.courseId === "string" ? params.courseId : "") : ""
  const { course, isLoading, error } = useCourse(courseId)

  // Update document title and favicon when course loads
  useEffect(() => {
    if (course) {
      const emoji = course.emoji || "📚"
      const title = `${course.name} | ReleaseRangers`
      document.title = title

      // Update favicon with course emoji
      const favicon = document.querySelector("link[rel*='icon']") as HTMLLinkElement | null
      const apple_icon = document.querySelector("link[rel='apple-touch-icon']") as HTMLLinkElement | null
      if (favicon) {
        favicon.href = `data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'><text y='.9em' font-size='90'>${emoji}</text></svg>`
      }
      if (apple_icon) {
        apple_icon.href = `data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'><text y='.9em' font-size='90'>${emoji}</text></svg>`
      }
    }
  }, [course])

  if (isLoading) {
    return <Loading />
  }

  if (error) {
    return (
      <SidebarProvider>
        <AppSidebar />
        <SidebarInset>
          <div className="flex items-center justify-center h-full">
            <div className="text-center">
              <p className="text-lg font-semibold">Error loading course</p>
              <p className="text-muted-foreground">Please try again later</p>
            </div>
          </div>
        </SidebarInset>
      </SidebarProvider>
    )
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
              <AddContentButton courseId={courseId}>
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
                <ChapterList chapters={course.chapters} courseId={courseId} />
              </div>
            ) : (
              <div className="space-y-6">
                <UploadDropzone isInDialog={false} courseId={courseId} />
              </div>
            )}
          </div>
        </div>
      </SidebarInset>
    </SidebarProvider>
  )
}
