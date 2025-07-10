"use client"

import { useState } from "react"

import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { UploadDropzone } from "./upload-dropzone"

interface AddContentButtonProps {
  children: React.ReactNode
  courseId: string
}

export function AddContentButton({ children, courseId }: AddContentButtonProps) {
  const [isOpen, setIsOpen] = useState(false)

  const handleUploadComplete = () => {
    setIsOpen(false)
  }

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogTrigger asChild>{children}</DialogTrigger>
      <DialogContent className="sm:max-w-2xl">
        <DialogHeader>
          <DialogTitle>Add new content</DialogTitle>
          <DialogDescription>Upload new course materials. You can upload multiple files at once.</DialogDescription>
        </DialogHeader>
        <div className="py-4">
          <UploadDropzone isInDialog onUploadComplete={handleUploadComplete} courseId={courseId} />
        </div>
      </DialogContent>
    </Dialog>
  )
}
