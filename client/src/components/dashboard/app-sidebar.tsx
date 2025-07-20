"use client"

import * as React from "react"
import { Home, Inbox, MessageCircleQuestion, Search, Sparkles } from "lucide-react"

import { NavFavorites } from "@/components/dashboard/nav-favorites"
import { NavMain } from "@/components/dashboard/nav-main"
import { NavSecondary } from "@/components/dashboard/nav-secondary"
import { NavCourses } from "@/components/dashboard/nav-courses"
import { AppActions } from "@/components/dashboard/app-actions"
import { Sidebar, SidebarContent, SidebarHeader, SidebarRail } from "@/components/ui/sidebar"

const data = {
  navMain: [
    {
      title: "Search",
      url: "#",
      icon: Search,
      disabled: true,
    },
    {
      title: "Ask AI",
      url: "#",
      icon: Sparkles,
      disabled: true,
    },
    {
      title: "Home",
      url: "/dashboard",
      icon: Home,
      isActive: true,
      disabled: false,
    },
    {
      title: "Inbox",
      url: "#",
      icon: Inbox,
      badge: "10",
      disabled: true,
    },
  ],
  navSecondary: [
    {
      title: "Help",
      url: "https://github.com/AET-DevOps25/team-releaserangers/blob/main/README.md",
      icon: MessageCircleQuestion,
    },
  ],
}

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
  return (
    <Sidebar className="border-r-0" {...props}>
      <SidebarHeader>
        <AppActions />
        <NavMain items={data.navMain} />
        {/* <CourseCreationDialog /> */}
      </SidebarHeader>
      <SidebarContent>
        <NavFavorites />
        <NavCourses />
        <NavSecondary items={data.navSecondary} className="mt-auto" />
      </SidebarContent>
      <SidebarRail />
    </Sidebar>
  )
}
