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
  const handleExport = () => {
    // Logic to export course data
    console.log("Export clicked")
  }

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
          <Button variant="ghost" size="icon" className="data-[state=open]:bg-accent h-7 w-7">
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
                            <AddContentButton>
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
