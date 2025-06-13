import { Calendar, FileText, Plus, Upload } from "lucide-react"
import { formatDistanceToNow } from "date-fns"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"

const activities = [
  {
    id: "1",
    type: "course_created",
    title: "Created new course",
    description: "UI/UX Design Principles",
    timestamp: "2023-12-10T14:30:00Z",
    icon: Plus,
  },
  {
    id: "2",
    type: "chapter_completed",
    title: "Completed chapter",
    description: "JavaScript Basics - Functions",
    timestamp: "2023-12-10T12:15:00Z",
    icon: FileText,
  },
  {
    id: "3",
    type: "material_uploaded",
    title: "Uploaded materials",
    description: "React Hooks presentation",
    timestamp: "2023-12-09T16:45:00Z",
    icon: Upload,
  },
  {
    id: "4",
    type: "course_started",
    title: "Started new course",
    description: "Data Science Fundamentals",
    timestamp: "2023-12-09T09:20:00Z",
    icon: Calendar,
  },
]

export function RecentActivity() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">Recent Activity</CardTitle>
        <CardDescription>Your latest learning activities</CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        {activities.map((activity) => (
          <div key={activity.id} className="flex items-start gap-3">
            <div className="flex h-8 w-8 items-center justify-center rounded-full bg-primary/10">
              <activity.icon className="h-4 w-4 text-primary" />
            </div>
            <div className="flex-1 space-y-1">
              <p className="text-sm font-medium">{activity.title}</p>
              <p className="text-sm text-muted-foreground">{activity.description}</p>
              <p className="text-xs text-muted-foreground">{formatDistanceToNow(new Date(activity.timestamp))} ago</p>
            </div>
          </div>
        ))}
      </CardContent>
    </Card>
  )
}
