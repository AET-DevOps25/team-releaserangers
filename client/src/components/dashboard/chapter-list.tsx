"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Calendar, CalendarSync, ChevronRight } from "lucide-react"
import { formatDistanceToNow } from "date-fns"

import { Button } from "@/components/ui/button"
import { Card, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"

interface ChapterListProps {
  chapters: Chapter[]
  courseId: string
}

export function ChapterList({ chapters, courseId }: ChapterListProps) {
  const router = useRouter()
  const [selectedChapterId, setSelectedChapterId] = useState<string | null>(null)

  const handleChapterSelect = (chapterId: string) => {
    setSelectedChapterId(chapterId)
    router.push(`/course/${courseId}/chapter/${chapterId}`)
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-semibold">Chapters ({chapters.length})</h2>
        <Button disabled variant="outline" size="sm">
          Reorder
        </Button>
      </div>

      <div className="grid gap-4 md:grid-cols-2">
        {chapters.map((chapter) => (
          <Card
            key={chapter.id}
            className={`cursor-pointer transition-all hover:border-primary ${selectedChapterId === chapter.id ? "border-primary" : ""}`}
            onClick={() => handleChapterSelect(chapter.id)}
          >
            <CardHeader className="pb-2">
              <CardTitle className="flex items-center justify-between">
                <span className="line-clamp-1">{chapter.name}</span>
                <ChevronRight className="h-5 w-5 text-muted-foreground" />
              </CardTitle>
              <CardDescription className="line-clamp-2">{chapter.description}</CardDescription>
            </CardHeader>
            {/* <CardContent className="pb-2">
              <div className="flex items-center text-sm text-muted-foreground">
                <FileText className="mr-1 h-4 w-4" />
                <span>Chapter {chapter.id}</span>
                <span className="line-clamp-2">Lorem ipsum dolor sit amet, consectetur adipiscing elit.</span>
              </div>
            </CardContent> */}
            <CardFooter className="pt-0 text-xs text-muted-foreground">
              <div className="flex flex-col gap-1">
                <div className="flex items-center">
                  <CalendarSync className="mr-1 h-3 w-3" />
                  <span className="mr-1">Last updated</span>
                  <span>{formatDistanceToNow(new Date(chapter.createdAt), { addSuffix: true })}</span>
                </div>
                <div className="flex items-center">
                  <Calendar className="mr-1 h-3 w-3" />
                  <span>Created {formatDistanceToNow(new Date(chapter.createdAt), { addSuffix: true })}</span>
                </div>
              </div>
            </CardFooter>
          </Card>
        ))}
      </div>
    </div>
  )
}
