"use client"

import * as React from "react"
import { Home, Inbox, MessageCircleQuestion, Search, Sparkles } from "lucide-react"

import { NavFavorites } from "@/components/dashboard/nav-favorites"
import { NavMain } from "@/components/dashboard/nav-main"
import { NavSecondary } from "@/components/dashboard/nav-secondary"
import { NavCourses } from "@/components/dashboard/nav-courses"
import { AppActions } from "@/components/dashboard/app-actions"
import { Sidebar, SidebarContent, SidebarHeader, SidebarRail } from "@/components/ui/sidebar"

import useCourseStore from "@/hooks/course-store"

const data = {
  navMain: [
    {
      title: "Search",
      url: "#",
      icon: Search,
    },
    {
      title: "Ask AI",
      url: "#",
      icon: Sparkles,
    },
    {
      title: "Home",
      url: "/dashboard",
      icon: Home,
      isActive: true,
    },
    {
      title: "Inbox",
      url: "#",
      icon: Inbox,
      badge: "10",
    },
  ],
  navSecondary: [
    {
      title: "Help",
      url: "#",
      icon: MessageCircleQuestion,
    },
  ],
}

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
  const { fetchCourses } = useCourseStore()
  const [isLoading, setIsLoading] = React.useState(true)

  React.useEffect(() => {
    const loadData = async () => {
      setIsLoading(true)
      try {
        await fetchCourses()
      } catch (error) {
        console.error("Failed to load data:", error)
      } finally {
        setIsLoading(false)
      }
    }
    loadData()
  }, [fetchCourses])

  return (
    <Sidebar className="border-r-0" {...props}>
      <SidebarHeader>
        <AppActions />
        <NavMain items={data.navMain} />
        {/* <CourseCreationDialog /> */}
      </SidebarHeader>
      <SidebarContent>
        <NavFavorites />
        <NavCourses isLoading={isLoading} />
        <NavSecondary items={data.navSecondary} className="mt-auto" />
      </SidebarContent>
      <SidebarRail />
    </Sidebar>
  )
}
