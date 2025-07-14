"use client"

import type React from "react"

import { useState } from "react"
import { Loader2 } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { EmojiPickerComponent } from "./emoji-picker"
import { useUpdateChapter } from "@/hooks/chapterAPI"

interface CustomizeChapterDialogProps {
  chapter: Chapter
  courseId: string
  children: React.ReactNode
}

export function CustomizeChapterDialog({ chapter, courseId, children }: CustomizeChapterDialogProps) {
  const [open, setOpen] = useState(false)
  const [isSaving, setIsSaving] = useState(false)
  const [formData, setFormData] = useState({
    title: chapter.title,
    emoji: chapter.emoji,
  })
  const { updateChapter } = useUpdateChapter()

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleEmojiSelect = (emoji: string) => {
    setFormData((prev) => ({ ...prev, emoji }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsSaving(true)

    await updateChapter(chapter.id, courseId, {
      title: formData.title,
      emoji: formData.emoji,
    })

    setIsSaving(false)
    setOpen(false)
  }

  const handleCancel = () => {
    setFormData({
      title: chapter.title,
      emoji: chapter.emoji,
    })
    setOpen(false)
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>{children}</DialogTrigger>
      <DialogContent className="sm:max-w-[500px]">
        <form onSubmit={handleSubmit}>
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">Customize Chapter</DialogTitle>
            <DialogDescription>Update your chapter details and appearance.</DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="emoji">Chapter Icon</Label>
              <div className="flex items-center gap-2">
                <EmojiPickerComponent emoji={formData.emoji} onEmojiSelect={handleEmojiSelect} />
                <Input id="title" name="title" placeholder="e.g., Introduction to Machine Learning" value={formData.title} onChange={handleChange} required className="hidden" />
              </div>
            </div>
            <div className="grid gap-2">
              <Label htmlFor="title">Chapter Title</Label>
              <Input id="title" name="title" placeholder="e.g., Introduction to Machine Learning" value={formData.title} onChange={handleChange} required />
            </div>
          </div>
          <DialogFooter>
            <Button type="button" variant="outline" onClick={handleCancel}>
              Cancel
            </Button>
            <Button type="submit" disabled={isSaving}>
              {isSaving ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Saving...
                </>
              ) : (
                "Save Changes"
              )}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
