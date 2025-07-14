"use client"

import { ArrowUpRight, Link as LinkIcon, MoreHorizontal, StarOff } from "lucide-react"
import Link from "next/link"

import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuSeparator, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"
import { SidebarGroup, SidebarGroupLabel, SidebarMenu, SidebarMenuAction, SidebarMenuButton, SidebarMenuItem, useSidebar } from "@/components/ui/sidebar"
import { useFavorites } from "@/hooks/useFavorites"
import { useUpdateCourse } from "@/hooks/courseAPI"
import { useUpdateChapter } from "@/hooks/chapterAPI"
import { mutate } from "swr"
import { CHAPTER_ENDPOINT, COURSE_ENDPOINT } from "@/server/endpoints"

export function NavFavorites() {
  const { isMobile } = useSidebar()
  const { favorites, error, isLoading, refetch } = useFavorites()
  const { updateCourse } = useUpdateCourse()
  const { updateChapter } = useUpdateChapter()

  const handleFavorite = async (item: Favorite) => {
    try {
      if (item.type === "course") {
        await updateCourse(item.id, {
          isFavorite: false,
        })
        mutate(COURSE_ENDPOINT(item.id))
      } else {
        await updateChapter(item.id, {
          isFavorite: false,
        })
        mutate(CHAPTER_ENDPOINT(item.id))
      }
      if (refetch) refetch()
    } catch (error) {
      console.error("Failed to update favorite status:", error)
    }
  }

  const handleCopyLink = (item: Favorite) => {
    const domain = window.location.origin
    const itemUrl = `${domain}${item.type === "course" ? `/${item.id}` : `/${item.courseId}/chapter/${item.id}`}`
    navigator.clipboard.writeText(itemUrl)
  }

  const handleOpenInNewTab = (item: Favorite) => {
    const domain = window.location.origin
    const itemUrl = `${domain}${item.type === "course" ? `/${item.id}` : `/${item.courseId}/chapter/${item.id}`}`
    window.open(itemUrl, "_blank")
  }

  if (error) {
    return (
      <SidebarGroup className="group-data-[collapsible=icon]:hidden">
        <SidebarGroupLabel>Favorites</SidebarGroupLabel>
        <SidebarMenu>
          <SidebarMenuItem>
            <SidebarMenuButton asChild className="pointer-events-none">
              <span className="text-red-500">Error loading favorites</span>
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarGroup>
    )
  }

  return (
    <>
      <SidebarGroup className="group-data-[collapsible=icon]:hidden">
        <SidebarGroupLabel>Favorites</SidebarGroupLabel>
        <SidebarMenu>
          {isLoading &&
            favorites.length === 0 &&
            Array.from({ length: 2 }).map((_, index) => (
              <SidebarMenuItem key={`loading-${index}`}>
                <SidebarMenuButton className="text-sidebar-foreground/70">
                  <div className="flex items-center w-full gap-2">
                    <div className="w-5 h-5 rounded-full bg-sidebar-foreground/20 animate-pulse"></div>
                    <div className="h-4 w-full max-w-[120px] rounded-md bg-sidebar-foreground/20 animate-pulse"></div>
                  </div>
                </SidebarMenuButton>
              </SidebarMenuItem>
            ))}
          {!isLoading && favorites.length === 0 && (
            <SidebarMenuItem>
              <SidebarMenuButton asChild className="pointer-events-none">
                <span className="text-sidebar-foreground/70">No favorites yet.</span>
              </SidebarMenuButton>
            </SidebarMenuItem>
          )}
          {favorites.map((item) => (
            <SidebarMenuItem key={item.title}>
              <SidebarMenuButton asChild>
                <Link href={item.type === "course" ? `/${item.id}` : `/${item.courseId}/chapter/${item.id}`} title={item.title}>
                  <span>{item.emoji}</span>
                  <span>{item.title}</span>
                </Link>
              </SidebarMenuButton>
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <SidebarMenuAction showOnHover>
                    <MoreHorizontal />
                    <span className="sr-only">More</span>
                  </SidebarMenuAction>
                </DropdownMenuTrigger>
                <DropdownMenuContent className="w-56 rounded-lg" side={isMobile ? "bottom" : "right"} align={isMobile ? "end" : "start"}>
                  <DropdownMenuItem onClick={() => handleFavorite(item)}>
                    <StarOff className="text-muted-foreground" />
                    <span>Remove from Favorites</span>
                  </DropdownMenuItem>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem onClick={() => handleCopyLink(item)}>
                    <LinkIcon className="text-muted-foreground" />
                    <span>Copy Link</span>
                  </DropdownMenuItem>
                  <DropdownMenuItem onClick={() => handleOpenInNewTab(item)}>
                    <ArrowUpRight className="text-muted-foreground" />
                    <span>Open in New Tab</span>
                  </DropdownMenuItem>
                  <DropdownMenuSeparator />
                </DropdownMenuContent>
              </DropdownMenu>
            </SidebarMenuItem>
          ))}
        </SidebarMenu>
      </SidebarGroup>
    </>
  )
}
