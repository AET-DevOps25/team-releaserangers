"use client"

import type React from "react"

import { useState } from "react"
import { FileUp, Upload } from "lucide-react"

import { Button } from "@/components/ui/button"
import { cn } from "@/lib/utils"

interface UploadDropzoneProps {
  isInDialog?: boolean
  onUploadComplete?: () => void
}

export function UploadDropzone({ isInDialog = false, onUploadComplete }: UploadDropzoneProps) {
  const [isDragging, setIsDragging] = useState(false)
  const [file, setFile] = useState<File | null>(null)

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault()
    setIsDragging(true)
  }

  const handleDragLeave = () => {
    setIsDragging(false)
  }

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault()
    setIsDragging(false)

    if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
      setFile(e.dataTransfer.files[0])
    }
  }

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      setFile(e.target.files[0])
    }
  }

  const handleUploadClick = () => {
    document.getElementById("file-upload")?.click()
  }

  const handleUpload = () => {
    // Simulate upload process
    setTimeout(() => {
      setFile(null)
      if (onUploadComplete) {
        onUploadComplete()
      }
    }, 1500)
  }

  return (
    <div
      className={cn(
        "relative rounded-lg border-2 border-dashed transition-all",
        isDragging ? "border-primary bg-primary/5" : "border-muted-foreground/25",
        isInDialog ? "p-12" : "p-32"
      )}
      onDragOver={handleDragOver}
      onDragLeave={handleDragLeave}
      onDrop={handleDrop}
    >
      <input id="file-upload" type="file" className="hidden" onChange={handleFileChange} />

      <div className="flex flex-col items-center justify-center text-center">
        {file ? (
          <>
            <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-primary/10">
              <FileUp className="h-6 w-6 text-primary" />
            </div>
            <h3 className="text-lg font-medium">{file.name}</h3>
            <p className="mt-1 text-sm text-muted-foreground">{(file.size / 1024 / 1024).toFixed(2)} MB</p>
            <div className="mt-4 flex gap-2">
              <Button onClick={handleUpload}>Upload Content</Button>
              <Button variant="outline" onClick={() => setFile(null)}>
                Cancel
              </Button>
            </div>
          </>
        ) : (
          <>
            <div className={cn("mb-4 flex items-center justify-center rounded-full bg-primary/10", isInDialog ? "h-16 w-16" : "h-10 w-10")}>
              <Upload className={cn("text-primary", isInDialog ? "h-8 w-8" : "h-5 w-5")} />
            </div>
            <h3 className={cn(isInDialog ? "text-base" : "text-xl mb-2 font-semibold")}>{isInDialog ? "Drag and drop your new material" : "This course has no content yet"}</h3>
            <p className={cn("mt-1 text-muted-foreground", isInDialog ? "text-sm" : "text-base")}>
              {isInDialog ? (
                "Upload PDF, DOCX, PPTX, or other course materials"
              ) : (
                <>
                  Upload your first material to get started with this course.
                  <br />
                  You can upload PDF, DOCX, PPTX, or other course materials.
                </>
              )}
            </p>
            <Button variant="outline" onClick={handleUploadClick} className={cn(isInDialog ? "mt-4" : "mt-6", isInDialog ? "text-sm" : "text-xs")}>
              Select File
            </Button>
          </>
        )}
      </div>
    </div>
  )
}
