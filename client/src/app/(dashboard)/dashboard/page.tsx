"use client"

import { AppSidebar } from "@/components/dashboard/app-sidebar"
import { Breadcrumb, BreadcrumbItem, BreadcrumbList, BreadcrumbPage } from "@/components/ui/breadcrumb"
import { Separator } from "@/components/ui/separator"
import { SidebarInset, SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar"
import { WelcomeSection } from "@/components/dashboard/welcome-section"
import { RecentCourses } from "@/components/dashboard/recent-courses"
import { QuickStats } from "@/components/dashboard/quick-stats"
import { RecentActivity } from "@/components/dashboard/recent-activity"
import { QuickActions } from "@/components/dashboard/quick-actions"
import useUserStore from "@/hooks/user-store"
import { useEffect } from "react"

export default function DashboardPage() {
  const { fetchUser } = useUserStore()

  useEffect(() => {
    async function loadUser() {
      try {
        await fetchUser()
      } catch (error) {
        console.error("Failed to load user data:", error)
      }
    }
    loadUser()
  }, [fetchUser])

  return (
    <SidebarProvider>
      <AppSidebar />
      <SidebarInset>
        <header className="flex h-14 shrink-0 items-center gap-2">
          <div className="flex flex-1 items-center gap-2 px-3">
            <SidebarTrigger />
            <Separator orientation="vertical" className="mr-2 h-4" />
            <Breadcrumb>
              <BreadcrumbList>
                <BreadcrumbItem>
                  <BreadcrumbPage>Home</BreadcrumbPage>
                </BreadcrumbItem>
              </BreadcrumbList>
            </Breadcrumb>
          </div>
        </header>
        <div className="flex flex-1 flex-col gap-6 p-6">
          <div className="max-w-7xl mx-auto w-full space-y-8">
            <WelcomeSection />
            <QuickStats />
            <div className="grid gap-8 lg:grid-cols-3">
              <div className="lg:col-span-2">
                <RecentCourses />
              </div>
              <div className="space-y-6">
                <QuickActions />
                <RecentActivity />
              </div>
            </div>
          </div>
        </div>
      </SidebarInset>
    </SidebarProvider>
  )
}
