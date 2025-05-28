import { AppSidebar } from "@/components/dashboard/app-sidebar"
import { MarkdownViewer } from "@/components/dashboard/markdown-viewer"
import { Breadcrumb, BreadcrumbItem, BreadcrumbLink, BreadcrumbList, BreadcrumbPage, BreadcrumbSeparator } from "@/components/ui/breadcrumb"
import { Separator } from "@/components/ui/separator"
import { SidebarInset, SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar"
import { NavActionsChapter } from "@/components/dashboard/nav-actions-chapter"

// TODO: Table, Code, links and other components

// Sample markdown content for a chapter
const sampleChapterContent = `
# Markdown Test
## Markdown Test
### Markdown Test
Markdown Test
# Getting Started with Project Management

**bold text**

*italicized text*

> blockquote

1. First item
2. Second item
3. Third item

- First item
- Second item
- Third item

---

Welcome to your _project management_ workspace. Here you can track tasks, set deadlines, and collaborate with your team.

## Key Features

- **Task Tracking**: Create, assign, and monitor tasks
- **Deadline Management**: Set and track important deadlines
- **Team Collaboration**: Work together seamlessly
- **Progress Reporting**: Generate reports on project progress

## Quick Tips

1. Use **tags** to categorize your tasks
2. Set up **recurring tasks** for regular activities
3. Utilize the **calendar view** for timeline visualization
4. Create **templates** for common project types

> "Good planning without good working is nothing." — Dwight D. Eisenhower

## Next Steps

- [ ] Set up your first project
- [ ] Invite team members
- [ ] Set up your first project
- [ ] Invite team members
- [ ] Set up your first project
- [ ] Invite team members
- [ ] Set up your first project
- [ ] Invite team members


Feel free to customize this workspace to fit your specific needs!
`

export default function ChapterPage({ params }: { params: { courseId: string; chapterId: string } }) {
  // In a real application, you would fetch the course and chapter data based on the IDs
  const courseTitle = "Introduction to Web Development"
  const chapterTitle = "Getting Started with HTML"
  const chapter: Chapter = {
    isFavorite: false,
    id: params.chapterId,
    name: chapterTitle,
    description: "Learn the basics of HTML, the standard markup language for creating web pages.",
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    emoji: "🌐",
  } // TODO: Replace with actual chapter data fetching logic

  return (
    <SidebarProvider>
      <AppSidebar />
      <SidebarInset>
        <header className="flex h-14 shrink-0 items-center gap-2 sticky top-0 z-40 bg-background">
          <div className="flex flex-1 items-center gap-2 px-3">
            <SidebarTrigger />
            <Separator orientation="vertical" className="mr-2 h-4" />
            <Breadcrumb>
              <BreadcrumbList>
                <BreadcrumbItem>
                  <BreadcrumbLink href={`/course/${params.courseId}`}>{courseTitle}</BreadcrumbLink>
                </BreadcrumbItem>
                <BreadcrumbSeparator />
                <BreadcrumbItem>
                  <BreadcrumbPage className="line-clamp-1">{chapterTitle}</BreadcrumbPage>
                </BreadcrumbItem>
              </BreadcrumbList>
            </Breadcrumb>
          </div>
          <div className="ml-auto px-3">
            <NavActionsChapter chapter={chapter} />
          </div>
        </header>
        <div className="flex flex-1 flex-col gap-4 px-4 py-10">
          <MarkdownViewer title={chapterTitle} content={sampleChapterContent} />
        </div>
      </SidebarInset>
    </SidebarProvider>
  )
}
