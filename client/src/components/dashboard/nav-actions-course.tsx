"use client"

import * as React from "react"
import { ArrowDown, ArrowUp, Link, MoreHorizontal, Settings2, Star, Trash2 } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Sidebar, SidebarContent, SidebarGroup, SidebarGroupContent, SidebarMenu, SidebarMenuButton, SidebarMenuItem } from "@/components/ui/sidebar"
import { formatDistanceToNow } from "date-fns"
import { DeleteCourseDialog } from "./delete-course-dialog"
import { CustomizeCourseDialog } from "./customize-course-dialog"
import { AddContentButton } from "./add-content-button"
import { useFavorites } from "@/hooks/useFavorites"
import { useUpdateCourse } from "@/hooks/courseAPI"

export function NavActionsCourse({ course }: { course: Course }) {
  const data = [
    [
      {
        label: "Customize Course",
        icon: Settings2,
      },
    ],
    [
      {
        label: "Copy Link",
        icon: Link,
        action: () => handleCopyLink(),
      },
      {
        label: "Delete Course",
        icon: Trash2,
      },
    ],
    [
      {
        label: "Add Content",
        icon: ArrowUp,
      },
      {
        label: "Export",
        icon: ArrowDown,
        action: () => handleExport(),
      },
    ],
  ]

  const [isOpen, setIsOpen] = React.useState(false)
  const [isFavorite, setIsFavorite] = React.useState(course.isFavorite)
  const { updateCourse } = useUpdateCourse()
  const { refetch } = useFavorites()

  const handleFavorite = async () => {
    const value = !isFavorite
    setIsFavorite(value)
    await updateCourse(course.id, {
      isFavorite: value,
    })
    if (refetch) refetch()
  }

  const handleCopyLink = () => {
    navigator.clipboard.writeText(window.location.href)
    setIsOpen(false)
  }
  const handleExportChapter = (chapter: Chapter) => {
    const content = chapter.title + "\n\n" + chapter.content
    const blob = new Blob([content], { type: "text/markdown" })
    const url = URL.createObjectURL(blob)
    const a = document.createElement("a")
    a.href = url
    a.download = `${chapter.title.replace(/[^a-z0-9]/gi, "_").toLowerCase()}.md`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
  }

  const handleExport = () => {
    if (course.chapters && course.chapters.length > 0) {
      course.chapters.forEach((chapter) => {
        handleExportChapter(chapter)
      })
    }
    setIsOpen(false)
  }

  React.useEffect(() => {
    setIsFavorite(course.isFavorite)
  }, [course.isFavorite])

  return (
    <div className="flex items-center gap-2 text-sm">
      <div className="text-muted-foreground hidden font-medium md:inline-block">
        <span>Edited {formatDistanceToNow(new Date(course.updatedAt.endsWith("Z") ? course.updatedAt : course.updatedAt + "Z"), { addSuffix: true })}</span>
      </div>
      <Button variant="ghost" size="icon" className="h-7 w-7" onClick={handleFavorite}>
        {isFavorite ? <Star fill="var(--color-yellow-500)" stroke="var(--color-yellow-500)" /> : <Star className="text-muted-foreground" />}
      </Button>
      <Popover open={isOpen} onOpenChange={setIsOpen}>
        <PopoverTrigger asChild>
          <Button variant="ghost" size="icon" className="data-[state=open]:bg-accent h-7 w-7" data-testid="nav-actions-course-button">
            <MoreHorizontal />
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-56 overflow-hidden rounded-lg p-0" align="end">
          <Sidebar collapsible="none" className="bg-transparent">
            <SidebarContent>
              {data.map((group, index) => (
                <SidebarGroup key={index} className="border-b last:border-none">
                  <SidebarGroupContent className="gap-0">
                    <SidebarMenu>
                      {group.map((item, index) => (
                        <SidebarMenuItem key={index}>
                          {item.label === "Customize Course" ? (
                            <CustomizeCourseDialog course={course}>
                              <SidebarMenuButton>
                                <item.icon /> <span>{item.label}</span>
                              </SidebarMenuButton>
                            </CustomizeCourseDialog>
                          ) : item.label === "Delete Course" ? (
                            <DeleteCourseDialog course={course}>
                              <SidebarMenuButton>
                                <item.icon /> <span>{item.label}</span>
                              </SidebarMenuButton>
                            </DeleteCourseDialog>
                          ) : item.label === "Add Content" ? (
                            <AddContentButton courseId={course.id}>
                              <SidebarMenuButton>
                                <item.icon /> <span>{item.label}</span>
                              </SidebarMenuButton>
                            </AddContentButton>
                          ) : (
                            <SidebarMenuButton onClick={item.action}>
                              <item.icon /> <span>{item.label}</span>
                            </SidebarMenuButton>
                          )}
                        </SidebarMenuItem>
                      ))}
                    </SidebarMenu>
                  </SidebarGroupContent>
                </SidebarGroup>
              ))}
            </SidebarContent>
          </Sidebar>
        </PopoverContent>
      </Popover>
    </div>
  )
}
