import { BookOpen, MessageCircle, Layers, FileText } from "lucide-react"

export function FeatureSection() {
  return (
    <section id="features" className="py-16 md:py-24">
      <div className="px-4 md:px-6">
        <div className="flex flex-col items-center justify-center space-y-4 text-center">
          <div className="space-y-2">
            <div className="inline-block rounded-lg bg-muted px-3 py-1 text-sm">Features</div>
            <h2 className="text-3xl font-bold tracking-tighter sm:text-4xl md:text-5xl">Your AI-Powered Study Companion</h2>
            <p className="max-w-[900px] text-muted-foreground md:text-xl/relaxed lg:text-base/relaxed xl:text-xl/relaxed">
              We help you organize, summarize, and master your lecture materials for efficient exam preparation.
            </p>
          </div>
        </div>
        <div className="mx-auto grid max-w-5xl grid-cols-1 gap-6 py-12 md:grid-cols-2 lg:gap-12">
          <div className="flex flex-col gap-2">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-muted">
              <FileText className="h-5 w-5 text-primary" />
            </div>
            <h3 className="text-xl font-bold">Automatic Summarization</h3>
            <p className="text-muted-foreground">Upload your lecture notes and slides—our AI generates concise, smart summaries for you instantly.</p>
          </div>
          <div className="flex flex-col gap-2">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-muted">
              <Layers className="h-5 w-5 text-primary" />
            </div>
            <h3 className="text-xl font-bold">Learning Chapters</h3>
            <p className="text-muted-foreground">Your materials are automatically organized into thematic chapters, giving you a clear overview of your course.</p>
          </div>
          <div className="flex flex-col gap-2">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-muted">
              <BookOpen className="h-5 w-5 text-primary" />
            </div>
            <h3 className="text-xl font-bold">Continuous Updates</h3>
            <p className="text-muted-foreground">Add new content anytime—summaries and chapters update automatically, so you’re always prepared.</p>
          </div>
          <div className="flex flex-col gap-2">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-muted">
              <MessageCircle className="h-5 w-5 text-primary" />
            </div>
            <h3 className="text-xl font-bold">Smart Study Tools</h3>
            <p className="text-muted-foreground">In the future, you will be able to chat with your material and get AI-powered quizzes to deepen your understanding.</p>
          </div>
        </div>
      </div>
    </section>
  )
}
