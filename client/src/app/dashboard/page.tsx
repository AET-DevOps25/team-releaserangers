import { AppSidebar } from "@/components/dashboard/app-sidebar"
import { MarkdownViewer } from "@/components/dashboard/markdown-viewer"
import { NavActions } from "@/components/dashboard/nav-actions"
import { Breadcrumb, BreadcrumbItem, BreadcrumbList, BreadcrumbPage } from "@/components/ui/breadcrumb"
import { Separator } from "@/components/ui/separator"
import { SidebarInset, SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar"

// TODO: Table, Code, links and other components

// Sample markdown content
const sampleMarkdown = `
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

## Quick Tips

1. Use **tags** to categorize your tasks
2. Set up **recurring tasks** for regular activities

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

export default function Page() {
  return (
    <SidebarProvider>
      <AppSidebar />
      <SidebarInset>
        <header className="flex h-14 shrink-0 items-center gap-2 sticky top-0 z-40 bg-background">
          <div className="flex flex-1 items-center gap-2 px-3">
            <SidebarTrigger />
            <Separator orientation="vertical" className="mr-2 data-[orientation=vertical]:h-4" />
            <Breadcrumb>
              <BreadcrumbList>
                <BreadcrumbItem>
                  <BreadcrumbPage className="line-clamp-1">Project Management & Task Tracking</BreadcrumbPage>
                </BreadcrumbItem>
              </BreadcrumbList>
            </Breadcrumb>
          </div>
          <div className="ml-auto px-3">
            <NavActions />
          </div>
        </header>
        <div className="flex flex-1 flex-col gap-4 px-4 py-10">
          <MarkdownViewer title="Project Management & Task Tracking" content={sampleMarkdown} />
        </div>
      </SidebarInset>
    </SidebarProvider>
  )
}
