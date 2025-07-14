"use client"

import { AppSidebar } from "@/components/dashboard/app-sidebar"
import { MarkdownViewer } from "@/components/dashboard/markdown-viewer"
import { Breadcrumb, BreadcrumbItem, BreadcrumbLink, BreadcrumbList, BreadcrumbPage, BreadcrumbSeparator } from "@/components/ui/breadcrumb"
import { Separator } from "@/components/ui/separator"
import { SidebarInset, SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar"
import { NavActionsChapter } from "@/components/dashboard/nav-actions-chapter"
import { useParams } from "next/navigation"
import Loading from "./loading"
import { useChapter } from "@/hooks/chapterAPI"
import { useCourse } from "@/hooks/courseAPI"

// TODO: Table, Code, links and other components

export default function ChapterPage() {
  const params = useParams<{ courseId: string; chapterId: string }>()
  const courseId = params ? (typeof params.courseId === "string" ? params.courseId : "") : ""
  const chapterId = params ? (typeof params.chapterId === "string" ? params.chapterId : "") : ""
  const { chapter, isLoading, error } = useChapter(chapterId)
  const { course, isLoading: isCourseLoading, error: courseError } = useCourse(courseId)

  if (error || courseError) {
    return (
      <SidebarProvider>
        <AppSidebar />
        <SidebarInset>
          <div className="flex items-center justify-center h-full">
            <div className="text-center">
              <p className="text-lg font-semibold">Error loading chapter</p>
              <p className="text-muted-foreground">Please try again later</p>
            </div>
          </div>
        </SidebarInset>
      </SidebarProvider>
    )
  }

  if (isLoading || isCourseLoading) {
    return <Loading />
  }

  if (!chapter || !course) {
    return (
      <SidebarProvider>
        <AppSidebar />
        <SidebarInset>
          <div className="flex items-center justify-center h-full">
            <div className="text-center">
              <p className="text-lg font-semibold">Chapter not found</p>
              <p className="text-muted-foreground">The requested chapter could not be loaded</p>
            </div>
          </div>
        </SidebarInset>
      </SidebarProvider>
    )
  }

  return (
    <SidebarProvider>
      <AppSidebar />
      <SidebarInset>
        <header className="flex h-14 shrink-0 items-center gap-2 sticky top-0 z-40 bg-background">
          <div className="flex flex-1 items-center gap-2 px-3">
            <SidebarTrigger />
            <Separator orientation="vertical" className="mr-2 h-4" />
            <Breadcrumb>
              <BreadcrumbList>
                <BreadcrumbItem>
                  <BreadcrumbLink href={`/${courseId}`}>{course?.name || "Course"}</BreadcrumbLink>
                </BreadcrumbItem>
                <BreadcrumbSeparator />
                <BreadcrumbItem>
                  <BreadcrumbPage className="line-clamp-1">{chapter?.title}</BreadcrumbPage>
                </BreadcrumbItem>
              </BreadcrumbList>
            </Breadcrumb>
          </div>
          <div className="ml-auto px-3">{chapter && <NavActionsChapter chapter={chapter} />}</div>
        </header>
        <div className="flex flex-1 flex-col gap-4 px-4 py-10">
          <MarkdownViewer title={chapter?.emoji + " " + chapter?.title || ""} content={chapter?.content || ""} />
        </div>
      </SidebarInset>
    </SidebarProvider>
  )
}
