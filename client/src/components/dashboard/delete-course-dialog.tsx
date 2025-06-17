"use client"

import type React from "react"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Loader2, Trash2 } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import useCourseStore from "@/hooks/course-store"

interface DeleteCourseDialogProps {
  course: Course
  children: React.ReactNode
}

export function DeleteCourseDialog({ course, children }: DeleteCourseDialogProps) {
  const router = useRouter()
  const [open, setOpen] = useState(false)
  const [isDeleting, setIsDeleting] = useState(false)
  const [confirmationText, setConfirmationText] = useState("")
  const { deleteCourse } = useCourseStore()

  const isConfirmationValid = confirmationText === course.name

  const handleDelete = async () => {
    if (!isConfirmationValid) return
    setIsDeleting(true)

    try {
      setOpen(false)
      router.push("/dashboard")
      // TODO: Add proper path inspection logic to avoid unnecessary delay
      await new Promise((resolve) => setTimeout(resolve, 1000))
      deleteCourse(course.id)
    } catch (error) {
      console.error("Failed to delete course:", error)
      setIsDeleting(false)
    }
  }

  const handleCancel = () => {
    setConfirmationText("")
    setOpen(false)
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>{children}</DialogTrigger>
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2 text-destructive">Delete Course</DialogTitle>
          <DialogDescription>This action cannot be undone. Are you sure you want to delete this course?</DialogDescription>
        </DialogHeader>
        <div className="space-y-4 py-4">
          <div className="rounded-lg border border-destructive/20 bg-destructive/5 p-4">
            <div className="flex items-start gap-3">
              <div className="text-2xl">{course.emoji}</div>
              <div className="flex-1">
                <h3 className="font-semibold">{course.name}</h3>
                <p className="text-sm text-muted-foreground mt-1">{course.description}</p>
              </div>
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="confirmation">
              Type <span className="font-mono font-semibold">{course.name}</span> to confirm:
            </Label>
            <Input id="confirmation" value={confirmationText} onChange={(e) => setConfirmationText(e.target.value)} placeholder={course.name} className="font-mono" />
          </div>

          <div className="rounded-lg bg-muted/50 p-3">
            <p className="text-sm text-muted-foreground">
              <strong>What happens next:</strong>
            </p>
            <ul className="text-sm text-muted-foreground mt-1 space-y-1">
              <li>• Course will be permanently deleted</li>
              <li>• All chapters and materials of this course will be removed</li>
              <li>• You will not be able to recover it</li>
            </ul>
          </div>
        </div>
        <DialogFooter>
          <Button type="button" variant="outline" onClick={handleCancel}>
            Cancel
          </Button>
          <Button variant="destructive" onClick={handleDelete} disabled={!isConfirmationValid || isDeleting} className="gap-2">
            {isDeleting ? (
              <>
                <Loader2 className="h-4 w-4 animate-spin" />
                Deleting...
              </>
            ) : (
              <>
                <Trash2 className="h-4 w-4" />
                Delete Course
              </>
            )}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
