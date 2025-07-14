"use client"

import type React from "react"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Loader2, Trash2 } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { useDeleteChapter } from "@/hooks/chapterAPI"

interface DeleteChapterDialogProps {
  chapter: Chapter
  courseId: string
  children: React.ReactNode
}

export function DeleteChapterDialog({ chapter, courseId, children }: DeleteChapterDialogProps) {
  const router = useRouter()
  const [open, setOpen] = useState(false)
  const [isDeleting, setIsDeleting] = useState(false)
  const [confirmationText, setConfirmationText] = useState("")
  const { deleteChapter } = useDeleteChapter()

  const isConfirmationValid = confirmationText === chapter.title

  const handleDelete = async () => {
    if (!isConfirmationValid) return
    setIsDeleting(true)

    try {
      setOpen(false)
      router.push(`/${courseId}`)
      deleteChapter(chapter.id)
    } catch (error) {
      console.error("Failed to delete chapter:", error)
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
          <DialogTitle className="flex items-center gap-2 text-destructive">Delete Chapter</DialogTitle>
          <DialogDescription>This action cannot be undone. Are you sure you want to delete this chapter?</DialogDescription>
        </DialogHeader>
        <div className="space-y-4 py-4">
          <div className="rounded-lg border border-destructive/20 bg-destructive/5 p-4">
            <div className="flex items-start gap-3">
              <div className="text-2xl">{chapter.emoji}</div>
              <div className="flex-1">
                <h3 className="font-semibold">{chapter.title}</h3>
              </div>
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="confirmation">
              Type <span className="font-mono font-semibold">{chapter.title}</span> to confirm:
            </Label>
            <Input id="confirmation" value={confirmationText} onChange={(e) => setConfirmationText(e.target.value)} placeholder={chapter.title} className="font-mono" />
          </div>

          <div className="rounded-lg bg-muted/50 p-3">
            <p className="text-sm text-muted-foreground">
              <strong>What happens next:</strong>
            </p>
            <ul className="text-sm text-muted-foreground mt-1 space-y-1">
              <li>• Chapter will be permanently deleted</li>
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
                Delete Chapter
              </>
            )}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
