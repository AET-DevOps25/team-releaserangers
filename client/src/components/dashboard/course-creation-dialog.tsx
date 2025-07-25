"use client"

import type React from "react"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { BookOpen, Loader2, Plus } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { EmojiPickerComponent } from "./emoji-picker"
import { useCreateCourse } from "@/hooks/courseAPI"

export enum ButtonType {
  Default,
  Plus,
  Home,
}

export function CourseCreationDialog({ buttonType = ButtonType.Default }: { buttonType?: ButtonType }) {
  const { createCourse } = useCreateCourse()
  const router = useRouter()
  const [open, setOpen] = useState(false)
  const [formData, setFormData] = useState({
    title: "",
    description: "",
    emoji: "📖",
  })
  const [submitting, setSubmitting] = useState(false)

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setSubmitting(true)

    try {
      const course = await createCourse({
        name: formData.title,
        description: formData.description,
        emoji: formData.emoji,
      })
      setOpen(false)
      setFormData({ title: "", description: "", emoji: "📖" })
      router.push(`/${course.id}`)
    } catch (error) {
      console.error("Failed to create course:", error)
    } finally {
      setSubmitting(false)
    }
  }

  const handleKeyDown = (event: React.KeyboardEvent) => {
    if (event.key === "Enter") {
      event.preventDefault()
      handleSubmit(event)
    }
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        {buttonType === ButtonType.Plus ? (
          <Plus className="h-4 w-4" />
        ) : (
          <Button variant="outline" size={"sm"} className={buttonType === ButtonType.Home ? "gap-2" : "gap-2 m-2"}>
            <BookOpen className="h-4 w-4" />
            New Course
          </Button>
        )}
      </DialogTrigger>
      <DialogContent className="sm:max-w-[500px]">
        <form onSubmit={handleSubmit} onKeyDown={handleKeyDown}>
          <DialogHeader>
            <DialogTitle>Create New Course</DialogTitle>
            <DialogDescription>Create a new course to organize your learning materials and chapters.</DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="title">Course Title</Label>
              <div className="flex items-center gap-2">
                <EmojiPickerComponent
                  emoji={formData.emoji}
                  onEmojiSelect={(emoji: string) => {
                    setFormData((prev) => ({ ...prev, emoji }))
                  }}
                />
                <Input id="title" name="title" placeholder="e.g., Introduction to Machine Learning" value={formData.title} onChange={handleChange} required />
              </div>
            </div>
            <div className="grid gap-2">
              <Label htmlFor="description">Description</Label>
              <Textarea
                id="description"
                name="description"
                placeholder="Provide a brief description of your course..."
                value={formData.description}
                onChange={handleChange}
                required
                className="min-h-[100px]"
              />
            </div>
          </div>
          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => setOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" disabled={submitting}>
              {submitting ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Creating...
                </>
              ) : (
                "Create Course"
              )}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
