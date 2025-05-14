"use client"

import * as React from "react"
import { AudioWaveform, Blocks, Calendar, Command, Home, Inbox, MessageCircleQuestion, Plus, Search, Settings2, Sparkles, Trash2 } from "lucide-react"

import { NavFavorites } from "@/components/dashboard/nav-favorites"
import { NavMain } from "@/components/dashboard/nav-main"
import { NavSecondary } from "@/components/dashboard/nav-secondary"
import { NavCourses } from "@/components/dashboard/nav-courses"
import { TeamSwitcher } from "@/components/dashboard/team-switcher"
import { Sidebar, SidebarContent, SidebarHeader, SidebarRail } from "@/components/ui/sidebar"

import coursesData from "../../../mock/coursesData"
import { CourseCreationDialog } from "./course-creation-dialog"

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
      url: "#",
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
      title: "Settings",
      url: "#",
      icon: Settings2,
    },
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
  const favorites = coursesData.flatMap((course) => course.chapters).filter((chapter) => chapter.isFavorite)
  const [courses, setCourses] = React.useState(coursesData)
  return (
    <Sidebar className="border-r-0" {...props}>
      <SidebarHeader>
        <TeamSwitcher teams={data.teams} />
        <NavMain items={data.navMain} />
        <CourseCreationDialog />
      </SidebarHeader>
      <SidebarContent>
        <NavFavorites favorites={favorites} />
        <NavCourses courses={courses} />
        <NavSecondary items={data.navSecondary} className="mt-auto" />
      </SidebarContent>
      <SidebarRail />
    </Sidebar>
  )
}
