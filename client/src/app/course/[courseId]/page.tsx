import { AppSidebar } from "@/components/dashboard/app-sidebar"
import { NavActions } from "@/components/dashboard/nav-actions"
import { Breadcrumb, BreadcrumbItem, BreadcrumbList, BreadcrumbPage } from "@/components/ui/breadcrumb"
import { Separator } from "@/components/ui/separator"
import { SidebarInset, SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar"
import { ChapterList } from "@/components/dashboard/chapter-list"
import { UploadDropzone } from "@/components/dashboard/upload-dropzone"
import { AddContentButton } from "@/components/dashboard/add-content-button"

// Sample course data
const sampleCourse = {
  id: "1",
  title: "Introduction to Web Development",
  description: "Learn the fundamentals of web development including HTML, CSS, and JavaScript.",
  chapters: [
    {
      id: "1",
      title: "Getting Started with HTML",
      description: "Learn the basics of HTML structure and elements",
      createdAt: "2023-05-15T10:00:00Z",
    },
    {
      id: "2",
      title: "CSS Fundamentals",
      description: "Style your web pages with CSS",
      createdAt: "2023-05-22T10:00:00Z",
    },
    {
      id: "3",
      title: "JavaScript Basics",
      description: "Add interactivity with JavaScript",
      createdAt: "2023-05-29T10:00:00Z",
    },
    {
      id: "4",
      title: "Responsive Design",
      description: "Make your websites work on all devices",
      createdAt: "2023-06-05T10:00:00Z",
    },
  ],
}

// Empty course for demonstration
const emptyCourse = {
  id: "2",
  title: "Advanced React Patterns",
  description: "Master advanced React patterns and techniques",
  chapters: [],
}

export default function CoursePage({ params }: { params: { courseId: string } }) {
  // For demo purposes, show empty course if courseId is "2"
  const course = params.courseId === "2" ? emptyCourse : sampleCourse
  const hasChapters = course.chapters.length > 0

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
                  <BreadcrumbPage className="line-clamp-1">{course.title}</BreadcrumbPage>
                </BreadcrumbItem>
              </BreadcrumbList>
            </Breadcrumb>
          </div>
          <div className="ml-auto flex items-center gap-4 px-3">
            {hasChapters && <AddContentButton />}
            <NavActions />
          </div>
        </header>
        <div className="flex flex-1 flex-col gap-6 p-6">
          <div className="max-w-4xl mx-auto w-full">
            <div className="mb-8">
              <h1 className="text-3xl font-bold mb-2">{course.title}</h1>
              <p className="text-muted-foreground">{course.description}</p>
            </div>

            {hasChapters ? (
              <div className="space-y-8">
                <ChapterList chapters={course.chapters} courseId={course.id} />
                {/* <div className="mt-8">
                  <h2 className="text-xl font-semibold mb-4">Add New Chapter</h2>
                  <UploadDropzone compact />
                </div> */}
              </div>
            ) : (
              <div className="space-y-6">
                <UploadDropzone isInDialog={false} />
              </div>
            )}
          </div>
        </div>
      </SidebarInset>
    </SidebarProvider>
  )
}
