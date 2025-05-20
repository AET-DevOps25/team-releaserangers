"use client"

import { useState } from "react"
import ReactMarkdown from "react-markdown"

import { cn } from "@/lib/utils"

interface MarkdownViewerProps {
  title: string
  content: string
  className?: string
}

export function MarkdownViewer({ title, content, className }: MarkdownViewerProps) {
  const [titleValue] = useState(title)
  const [contentValue] = useState(content)

  // TODO: Fix overflow/overscroll issue

  return (
    <div className={cn("max-w-4xl mx-auto w-full", className)}>
      <div className="flex items-center justify-between mb-8">
        <h1 className="text-4xl font-bold">{titleValue || "Untitled"}</h1>
      </div>
      <div className="prose prose-sm sm:prose-base dark:prose-invert max-w-none">
        <ReactMarkdown
          components={{
            h1: ({ ...props }) => <h2 className="text-3xl font-bold mt-8 mb-4" {...props} />, // h1 is already used for the title
            h2: ({ ...props }) => <h3 className="text-2xl font-semibold mt-6 mb-2" {...props} />,
            h3: ({ ...props }) => <h4 className="text-xl font-semibold mt-4 mb-2" {...props} />,
            p: ({ ...props }) => <p className="mb-4" {...props} />,
            a: ({ ...props }) => <a className="text-blue-500 hover:underline" {...props} />,
            ul: ({ ...props }) => <ul className="list-disc list-inside mb-4" {...props} />,
            ol: ({ ...props }) => <ol className="list-decimal list-inside mb-4" {...props} />,
            li: ({ ...props }) => <li className="mb-2" {...props} />,
            blockquote: ({ ...props }) => <blockquote className="border-l-4 border-gray-300 pl-4 italic mb-4" {...props} />,
            hr: ({ ...props }) => <hr className="border-t-2 border-gray-300 my-4" {...props} />,
          }}
        >
          {contentValue}
        </ReactMarkdown>
      </div>
    </div>
  )
}
