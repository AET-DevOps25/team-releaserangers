import Link from "next/link"
import { Button } from "@/components/ui/button"

export function HeroSection() {
  return (
    <section className="bg-muted py-24 md:py-32">
      <div className="px-4 md:px-6 max-w-3/4 mx-auto">
        <div className="grid gap-10 lg:grid-cols-2 lg:gap-16">
          <div className="flex flex-col justify-center space-y-6">
            <div className="space-y-2">
              <h1 className="text-3xl font-bold tracking-tighter sm:text-4xl md:text-5xl">Study Smarter, Not Harder with Smart Summaries</h1>
              <p className="max-w-[600px] text-muted-foreground md:text-xl">
                Effortlessly organize, summarize, and master your lecture materials. Our AI-powered platform creates smart summaries, organizes your content into learning chapters,
                and helps you prepare for exams efficiently—all in one place.
              </p>
            </div>
            <div className="flex flex-col gap-2 sm:flex-row">
              <Link href="/signup">
                <Button size="lg" className="w-full sm:w-auto">
                  Get Started Free
                </Button>
              </Link>
              <Link href="#features">
                <Button variant="outline" size="lg" className="w-full sm:w-auto">
                  See How It Works
                </Button>
              </Link>
            </div>
          </div>
          <div className="flex items-center justify-center">
            <div className="relative h-[350px] w-full max-w-[500px] rounded-lg bg-gradient-to-br from-primary/20 to-primary/5 p-1 shadow-xl">
              <div className="absolute inset-0 flex items-center justify-center">
                <div className="rounded bg-background p-4 shadow-lg">
                  <div className="flex h-6 w-full items-center gap-2">
                    <div className="h-2 w-2 rounded-full bg-red-500"></div>
                    <div className="h-2 w-2 rounded-full bg-yellow-500"></div>
                    <div className="h-2 w-2 rounded-full bg-green-500"></div>
                    <div className="ml-2 h-4 w-40 rounded bg-muted"></div>
                  </div>
                  <div className="mt-4 grid grid-cols-3 gap-2">
                    <div className="h-20 rounded bg-muted"></div>
                    <div className="h-20 rounded bg-muted"></div>
                    <div className="h-20 rounded bg-muted"></div>
                    <div className="h-20 rounded bg-muted"></div>
                    <div className="h-20 rounded bg-muted"></div>
                    <div className="h-20 rounded bg-muted"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}
