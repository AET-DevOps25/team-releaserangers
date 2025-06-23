"use client"

import * as React from "react"
import { ArrowDown, ArrowUp, Link, MoreHorizontal, Settings2, Star, Trash2 } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Sidebar, SidebarContent, SidebarGroup, SidebarGroupContent, SidebarMenu, SidebarMenuButton, SidebarMenuItem } from "@/components/ui/sidebar"
import { formatDistanceToNow } from "date-fns"
import useCourseStore from "@/hooks/course-store"
import { useParams } from "next/navigation"
import { useFavorites } from "@/hooks/useFavorites"

export function NavActionsChapter({ chapter }: { chapter: Chapter }) {
  const params = useParams<{ courseId: string; chapterId: string }>()
  const courseId = params ? (typeof params.courseId === "string" ? params.courseId : "") : ""
  const [isOpen, setIsOpen] = React.useState(false)
  const [isFavorite, setIsFavorite] = React.useState(chapter.isFavorite)
  const { updateChapter } = useCourseStore()
  const { refetch } = useFavorites()

  const data = [
    [
      {
        label: "Customize Chapter",
        icon: Settings2,
        action: () => handleCustomizeChapter(),
      },
    ],
    [
      {
        label: "Copy Link",
        icon: Link,
        action: () => handleCopyLink(),
      },
      {
        label: "Delete Chapter",
        icon: Trash2,
        action: () => handleDeleteChapter(),
      },
    ],
    [
      {
        label: "Import",
        icon: ArrowUp,
        action: () => handleImport(),
      },
      {
        label: "Export",
        icon: ArrowDown,
        action: () => handleExport(),
      },
    ],
  ]

  const handleFavorite = async () => {
    try {
      const value = !isFavorite
      setIsFavorite(value)
      await updateChapter(courseId, chapter.id, {
        isFavorite: value,
      })
      if (refetch) refetch()
    } catch (error) {
      console.error("Failed to update favorite status:", error)
    }
  }

  const handleCustomizeChapter = () => {
    // Logic to customize the chapter
    console.log("Customize Chapter clicked")
  }
  const handleCopyLink = () => {
    navigator.clipboard.writeText(window.location.href)
    setIsOpen(false)
  }
  const handleDeleteChapter = () => {
    // Logic to move the chapter to trash
    console.log("Move to Trash clicked")
  }
  const handleImport = () => {
    // Logic to import chapter data
    console.log("Import clicked")
  }
  const handleExport = () => {
    // Logic to export chapter data
    console.log("Export clicked")
  }

  return (
    <div className="flex items-center gap-2 text-sm">
      <div className="text-muted-foreground hidden font-medium md:inline-block">
        <span>Edited {formatDistanceToNow(new Date(chapter.updatedAt.endsWith("Z") ? chapter.updatedAt : chapter.updatedAt + "Z"), { addSuffix: true })}</span>
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
                          <SidebarMenuButton onClick={item.action}>
                            <item.icon /> <span>{item.label}</span>
                          </SidebarMenuButton>
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
