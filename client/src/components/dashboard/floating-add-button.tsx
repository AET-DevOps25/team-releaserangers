"use client"

import { useState } from "react"
import { Plus } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { UploadDropzone } from "./upload-dropzone"

export function FloatingAddButton() {
  const [open, setOpen] = useState(false)

  return (
    <>
      <Button onClick={() => setOpen(true)} size="icon" className="fixed bottom-6 right-6 h-14 w-14 rounded-full shadow-lg">
        <Plus className="h-6 w-6" />
        <span className="sr-only">Add Content</span>
      </Button>

      <Dialog open={open} onOpenChange={setOpen}>
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
    </>
  )
}
