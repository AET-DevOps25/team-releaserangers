"use client"

import * as React from "react"
import { ChevronDown, GalleryVerticalEnd, LogOut } from "lucide-react"

import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"
import { SidebarMenu, SidebarMenuButton, SidebarMenuItem } from "@/components/ui/sidebar"
import { useRouter } from "next/navigation"
import { useSignOut } from "@/hooks/authAPI"

export function AppActions() {
  const router = useRouter()
  const { signOut } = useSignOut()

  const logout = async () => {
    await signOut()
    router.push("/")
  }

  return (
    <SidebarMenu>
      <SidebarMenuItem>
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <SidebarMenuButton className="w-fit px-1.5">
              <div className="flex items-center gap-2 self-center font-medium">
                <div className="flex h-5 w-5 items-center justify-center rounded-md bg-primary text-primary-foreground">
                  <GalleryVerticalEnd className="size-3" />
                </div>
                ReleaseRangers
              </div>
              <ChevronDown className="opacity-50" />
            </SidebarMenuButton>
          </DropdownMenuTrigger>
          <DropdownMenuContent className="w-64 rounded-lg" align="start" side="bottom" sideOffset={4}>
            <DropdownMenuItem className="gap-2 p-2" onClick={logout}>
              <div className="bg-background flex size-6 items-center justify-center rounded-md border">
                <LogOut className="size-4" />
              </div>
              <div className="text-muted-foreground font-medium">Logout</div>
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </SidebarMenuItem>
    </SidebarMenu>
  )
}
