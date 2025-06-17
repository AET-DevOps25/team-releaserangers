"use client"

import { useState } from "react"

import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { UploadDropzone } from "./upload-dropzone"

export function AddContentButton({ children }: { children: React.ReactNode }) {
  const [open, setOpen] = useState(false)

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>{children}</DialogTrigger>
      <DialogContent className="sm:max-w-[550px]">
        <DialogHeader>
          <DialogTitle>Add New Content</DialogTitle>
          <DialogDescription>Upload new material for this course.</DialogDescription>
        </DialogHeader>
        <div className="py-4">
          <UploadDropzone
            isInDialog={true}
            onUploadComplete={() => {
              setOpen(false)
            }}
          />
        </div>
      </DialogContent>
    </Dialog>
  )
}
