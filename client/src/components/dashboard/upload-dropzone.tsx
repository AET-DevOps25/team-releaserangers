"use client"

import type React from "react"

import { useState } from "react"
import { FileUp, Upload } from "lucide-react"

import { Button } from "@/components/ui/button"
import { cn } from "@/lib/utils"
import { UPLOAD_ENDPOINT } from "../../server/endpoints"

interface UploadDropzoneProps {
  isInDialog?: boolean
  onUploadComplete?: () => void
}

export function UploadDropzone({ isInDialog = false, onUploadComplete }: UploadDropzoneProps) {
  const [isDragging, setIsDragging] = useState(false)
  const [files, setFiles] = useState<File[]>([])
  const [isUploading, setIsUploading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const validateFiles = (fileList: FileList) => {
    const validFiles: File[] = []
    for (let i = 0; i < fileList.length; i++) {
      const file = fileList[i]
      if (file.type === "application/pdf") {
        validFiles.push(file)
      }
    }
    return validFiles
  }

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
    setError(null)

    if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
      const validFiles = validateFiles(e.dataTransfer.files)
      if (validFiles.length === 0) {
        setError("Please upload PDF files only")
        return
      }
      setFiles((prevFiles) => [...prevFiles, ...validFiles])
    }
  }

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setError(null)
    if (e.target.files && e.target.files.length > 0) {
      const validFiles = validateFiles(e.target.files)
      if (validFiles.length === 0) {
        setError("Please upload PDF files only")
        return
      }
      setFiles((prevFiles) => [...prevFiles, ...validFiles])
    }
  }

  const handleUploadClick = () => {
    document.getElementById("file-upload")?.click()
  }

  const handleUpload = async () => {
    setIsUploading(true)
    setError(null)

    try {
      const formData = new FormData()
      files.forEach((file) => {
        formData.append("files", file)
      })

      // Replace with environment variable or config for production
      const response = await fetch(UPLOAD_ENDPOINT, {
        method: "POST",
        body: formData,
      })

      if (!response.ok) {
        throw new Error(`Upload failed: ${response.statusText}`)
      }

      setFiles([])
      if (onUploadComplete) {
        onUploadComplete()
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : "Upload failed")
    } finally {
      setIsUploading(false)
    }
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
      <input id="file-upload" type="file" className="hidden" onChange={handleFileChange} multiple accept=".pdf" />

      <div className="flex flex-col items-center justify-center text-center">
        {files.length > 0 ? (
          <>
            <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-primary/10">
              <FileUp className="h-6 w-6 text-primary" />
            </div>
            <h3 className="text-lg font-medium">
              {files.length} file{files.length > 1 ? "s" : ""} selected
            </h3>
            <div className="mt-2 max-h-32 overflow-y-auto">
              {files.map((file, index) => (
                <p key={index} className="text-sm text-muted-foreground">
                  {file.name} ({(file.size / 1024 / 1024).toFixed(2)} MB)
                </p>
              ))}
            </div>
            {error && <p className="mt-2 text-sm text-red-500">{error}</p>}
            <div className="mt-4 flex gap-2">
              <Button onClick={handleUpload} disabled={isUploading}>
                {isUploading ? "Uploading..." : "Upload Content"}
              </Button>
              <Button variant="outline" onClick={() => setFiles([])} disabled={isUploading}>
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
            {error && <p className="mt-2 text-sm text-red-500">{error}</p>}
            <Button variant="outline" onClick={handleUploadClick} className={cn(isInDialog ? "mt-4" : "mt-6", isInDialog ? "text-sm" : "text-xs")}>
              Select File
            </Button>
          </>
        )}
      </div>
    </div>
  )
}
