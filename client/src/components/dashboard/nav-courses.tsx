import { ChevronRight, Plus } from "lucide-react"

import { Collapsible, CollapsibleContent, CollapsibleTrigger } from "@/components/ui/collapsible"
import {
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuAction,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarMenuSub,
  SidebarMenuSubButton,
  SidebarMenuSubItem,
} from "@/components/ui/sidebar"
import { ButtonType, CourseCreationDialog } from "./course-creation-dialog"
import useCourseStore from "@/hooks/course-store"
import { Suspense, useEffect } from "react"
import Link from "next/link"

export function NavCourses() {
  const { courses, isLoading, fetchCourses } = useCourseStore()

  useEffect(() => {
    fetchCourses().catch((error) => console.error("Failed to fetch courses:", error))
  }, [fetchCourses])

  return (
    <SidebarGroup>
      <SidebarGroupLabel className="flex items-center justify-between group/workspace-label">
        <span>Courses</span>
        {courses.length !== 0 && (
          <div>
            <CourseCreationDialog buttonType={ButtonType.Plus} />
          </div>
        )}
      </SidebarGroupLabel>
      <SidebarGroupContent>
        <SidebarMenu>
          {isLoading &&
            courses.length === 0 &&
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
          {!isLoading && courses.length === 0 && (
            <SidebarMenuItem>
              <CourseCreationDialog />
            </SidebarMenuItem>
          )}
          <Suspense
            fallback={
              <SidebarMenuItem>
                <SidebarMenuButton className="text-sidebar-foreground/70">Loading...</SidebarMenuButton>
              </SidebarMenuItem>
            }
          >
            {courses.map((course) => (
              <Collapsible key={course.id}>
                <SidebarMenuItem>
                  <SidebarMenuButton asChild>
                    <Link href={`/${course.id}`}>
                      <span>{course.emoji || "📚"}</span>
                      <span>{course.name}</span>
                    </Link>
                  </SidebarMenuButton>
                  <SidebarMenuAction showOnHover>
                    <Plus />
                  </SidebarMenuAction>
                  {course.chapters && course.chapters.length > 0 && (
                    <>
                      <CollapsibleTrigger asChild>
                        <SidebarMenuAction className="bg-sidebar-accent text-sidebar-accent-foreground left-2 data-[state=open]:rotate-90" showOnHover>
                          <ChevronRight />
                        </SidebarMenuAction>
                      </CollapsibleTrigger>
                      <CollapsibleContent>
                        <SidebarMenuSub>
                          {course.chapters.map((chapter) => (
                            <SidebarMenuSubItem key={chapter.id}>
                              <SidebarMenuSubButton asChild>
                                <Link href={`/${course.id}/chapter/${chapter.id}`}>
                                  <span>{chapter.emoji || "📖"}</span>
                                  <span>{chapter.title}</span>
                                </Link>
                              </SidebarMenuSubButton>
                            </SidebarMenuSubItem>
                          ))}
                        </SidebarMenuSub>
                      </CollapsibleContent>
                    </>
                  )}
                </SidebarMenuItem>
              </Collapsible>
            ))}
          </Suspense>
        </SidebarMenu>
      </SidebarGroupContent>
    </SidebarGroup>
  )
}
