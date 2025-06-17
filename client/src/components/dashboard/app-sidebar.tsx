"use client"

import * as React from "react"
import { AudioWaveform, Command, Home, Inbox, MessageCircleQuestion, Search, Sparkles, Trash2 } from "lucide-react"

import { NavFavorites } from "@/components/dashboard/nav-favorites"
import { NavMain } from "@/components/dashboard/nav-main"
import { NavSecondary } from "@/components/dashboard/nav-secondary"
import { NavCourses } from "@/components/dashboard/nav-courses"
import { AppActions } from "@/components/dashboard/app-actions"
import { Sidebar, SidebarContent, SidebarHeader, SidebarRail } from "@/components/ui/sidebar"

import useCourseStore from "@/hooks/course-store"

const data = {
  teams: [
    {
      name: "Acme Inc",
      logo: Command,
      plan: "Enterprise",
    },
    {
      name: "Acme Corp.",
      logo: AudioWaveform,
      plan: "Startup",
    },
    {
      name: "Evil Corp.",
      logo: Command,
      plan: "Free",
    },
  ],
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
      title: "Trash",
      url: "#",
      icon: Trash2,
    },
    {
      title: "Help",
      url: "#",
      icon: MessageCircleQuestion,
    },
  ],
}

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
  const { fetchFavorites, favorites } = useCourseStore()

  React.useEffect(() => {
    fetchFavorites()
  }, [fetchFavorites])

  return (
    <Sidebar className="border-r-0" {...props}>
      <SidebarHeader>
        <AppActions />
        <NavMain items={data.navMain} />
        {/* <CourseCreationDialog /> */}
      </SidebarHeader>
      <SidebarContent>
        <NavFavorites favorites={favorites} />
        <NavCourses />
        <NavSecondary items={data.navSecondary} className="mt-auto" />
      </SidebarContent>
      <SidebarRail />
    </Sidebar>
  )
}
