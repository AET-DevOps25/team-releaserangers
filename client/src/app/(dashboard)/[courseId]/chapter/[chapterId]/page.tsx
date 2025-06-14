"use client"

import { AppSidebar } from "@/components/dashboard/app-sidebar"
import { MarkdownViewer } from "@/components/dashboard/markdown-viewer"
import { Breadcrumb, BreadcrumbItem, BreadcrumbLink, BreadcrumbList, BreadcrumbPage, BreadcrumbSeparator } from "@/components/ui/breadcrumb"
import { Separator } from "@/components/ui/separator"
import { SidebarInset, SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar"
import { NavActionsChapter } from "@/components/dashboard/nav-actions-chapter"
import { useParams } from "next/navigation"
import useCourseStore from "@/hooks/course-store"
import { useEffect, useState } from "react"
import Loading from "./loading"

// TODO: Table, Code, links and other components

export default function ChapterPage() {
  const params = useParams<{ courseId: string; chapterId: string }>()
  const courseId = params ? (typeof params.courseId === "string" ? params.courseId : "") : ""
  const chapterId = params ? (typeof params.chapterId === "string" ? params.chapterId : "") : ""
  const { fetchChapter, fetchCourse } = useCourseStore()
  const [chapter, setChapter] = useState<Chapter | null>(null)
  const [course, setCourse] = useState<Course | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    async function loadChapter() {
      try {
        const chapterData = await fetchChapter(courseId, chapterId)
        setChapter(chapterData)
        const courseData = await fetchCourse(courseId)
        setCourse(courseData)
      } catch (error) {
        console.error("Failed to load chapter:", error)
      } finally {
        setLoading(false)
      }
    }

    loadChapter()
  }, [courseId, chapterId, fetchChapter, fetchCourse])

  if (loading) {
    return <Loading />
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
          <MarkdownViewer title={chapter?.title || ""} content={chapter?.content || ""} />
        </div>
      </SidebarInset>
    </SidebarProvider>
  )
}
