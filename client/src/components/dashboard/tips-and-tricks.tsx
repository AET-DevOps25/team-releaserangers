"use client"

import { useState } from "react"
import { BookOpen, Brain, Clock, FileText, Lightbulb, Target, Zap, ChevronLeft, ChevronRight, Sparkles, Star } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"

const tips = [
  {
    id: 1,
    category: "Upload Strategy",
    icon: FileText,
    title: "Upload Files Regularly",
    description:
      "Upload your lecture materials weekly throughout the semester. The AI will automatically integrate new content into existing chapters and create new ones as needed.",
    color: "text-blue-600",
    bgColor: "bg-blue-50",
  },
  {
    id: 2,
    category: "File Management",
    icon: BookOpen,
    title: "Upload PDF Documents",
    description:
      "Currently supporting PDF uploads including lecture slides, handwritten notes, and annotated documents. Our AI extracts and summarizes content from any PDF format.",
    color: "text-green-600",
    bgColor: "bg-green-50",
  },
  {
    id: 3,
    category: "Organization",
    icon: Target,
    title: "Let AI Structure Content",
    description:
      "Don't worry about organizing - our AI automatically categorizes material into learning chapters with clear topics like 'Requirements Elicitation' or 'System Design'.",
    color: "text-purple-600",
    bgColor: "bg-purple-50",
  },
  {
    id: 4,
    category: "Platform",
    icon: Sparkles,
    title: "Use Emojis for Recognition",
    description: "Choose meaningful emojis for your courses like 🖥️ for Computer Science or 📊 for Business. They help with quick visual identification.",
    color: "text-orange-600",
    bgColor: "bg-orange-50",
  },
  {
    id: 5,
    category: "Exam Prep",
    icon: Brain,
    title: "Review AI Summaries",
    description: "Focus on the AI-generated summaries instead of reading through all documents. They contain the most important information in a study-friendly format.",
    color: "text-blue-600",
    bgColor: "bg-blue-50",
  },
  {
    id: 6,
    category: "Favorites",
    icon: Star,
    title: "Mark Important Content",
    description: "Use the star feature to favorite important courses and chapters. Access them quickly from the sidebar for efficient exam preparation.",
    color: "text-yellow-600",
    bgColor: "bg-yellow-50",
  },
  {
    id: 7,
    category: "Catch-up Strategy",
    icon: Clock,
    title: "Upload Missed Lectures",
    description: "If you miss classes, upload shared notes from classmates. The AI summaries help you quickly catch up without reading every document.",
    color: "text-red-600",
    bgColor: "bg-red-50",
  },
  {
    id: 8,
    category: "Efficiency",
    icon: Zap,
    title: "Drag and Drop Upload",
    description: "Use the large drag-and-drop field to quickly upload multiple files at once. No need to select files one by one.",
    color: "text-yellow-600",
    bgColor: "bg-yellow-50",
  },
]

export function TipsAndTricks() {
  const [currentTipIndex, setCurrentTipIndex] = useState(0)
  const currentTip = tips[currentTipIndex]

  const nextTip = () => {
    setCurrentTipIndex((prev) => (prev + 1) % tips.length)
  }

  const prevTip = () => {
    setCurrentTipIndex((prev) => (prev - 1 + tips.length) % tips.length)
  }

  return (
    <Card className="h-fit">
      <CardHeader>
        <CardTitle className="flex items-center gap-2 text-lg">
          <Lightbulb className="h-5 w-5 text-yellow-500" />
          Tips & Tricks
        </CardTitle>
        <CardDescription>Helpful advice to enhance your learning experience</CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        {/* Current Tip Display */}
        <div className="space-y-3">
          <div className={`flex items-center gap-2 text-sm font-medium ${currentTip.color}`}>
            <div className={`p-1.5 rounded-md ${currentTip.bgColor}`}>
              <currentTip.icon className="h-4 w-4" />
            </div>
            <span>{currentTip.category}</span>
          </div>

          <div>
            <h3 className="font-semibold mb-2">{currentTip.title}</h3>
            <p className="text-sm text-muted-foreground leading-relaxed">{currentTip.description}</p>
          </div>
        </div>

        {/* Navigation */}
        <div className="flex items-center justify-between pt-2 border-t">
          <Button variant="ghost" size="sm" onClick={prevTip} className="h-8 w-8 p-0">
            <ChevronLeft className="h-4 w-4" />
          </Button>

          <div className="flex items-center gap-1">
            {tips.map((_, index) => (
              <button
                key={index}
                onClick={() => setCurrentTipIndex(index)}
                className={`h-2 w-2 rounded-full transition-colors ${index === currentTipIndex ? "bg-primary" : "bg-muted"}`}
              />
            ))}
          </div>

          <Button variant="ghost" size="sm" onClick={nextTip} className="h-8 w-8 p-0">
            <ChevronRight className="h-4 w-4" />
          </Button>
        </div>

        {/* Tip Counter */}
        <div className="text-center">
          <span className="text-xs text-muted-foreground">
            Tip {currentTipIndex + 1} of {tips.length}
          </span>
        </div>
      </CardContent>
    </Card>
  )
}
