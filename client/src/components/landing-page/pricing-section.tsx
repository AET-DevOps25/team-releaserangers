import { Check } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"

export function PricingSection() {
  return (
    <section id="pricing" className="py-16 md:py-24">
      <div className="px-4 md:px-6">
        <div className="flex flex-col items-center justify-center space-y-4 text-center">
          <div className="space-y-2">
            <div className="inline-block rounded-lg bg-muted px-3 py-1 text-sm">Coming Soon - Pricing</div>
            <h2 className="text-3xl font-bold tracking-tighter sm:text-4xl md:text-5xl">Simple, Student-Friendly Pricing</h2>
            <p className="max-w-[900px] text-muted-foreground md:text-xl/relaxed lg:text-base/relaxed xl:text-xl/relaxed">
              Choose the plan that fits your study needs. Start for free and upgrade anytime.
            </p>
          </div>
        </div>
        <div className="mx-auto grid max-w-5xl grid-cols-1 gap-6 py-12 md:grid-cols-3">
          <Card>
            <CardHeader className="flex flex-col items-center justify-center space-y-2">
              <CardTitle className="text-xl">Free</CardTitle>
              <CardDescription className="text-center">Get started with the essentials</CardDescription>
              <div className="flex items-baseline text-center">
                <span className="text-3xl font-bold">$0</span>
                <span className="ml-1 text-muted-foreground">/month</span>
              </div>
            </CardHeader>
            <CardContent className="flex flex-col gap-2 p-6">
              <div className="flex items-center gap-2">
                <Check className="h-4 w-4 text-primary" />
                <span className="text-sm">Summarize up to 3 courses</span>
              </div>
              <div className="flex items-center gap-2">
                <Check className="h-4 w-4 text-primary" />
                <span className="text-sm">Basic AI summaries</span>
              </div>
              <div className="flex items-center gap-2">
                <Check className="h-4 w-4 text-primary" />
                <span className="text-sm">Limited flashcards</span>
              </div>
            </CardContent>
            <CardFooter className="flex flex-col p-6 pt-0">
              <Button variant="outline" className="w-full" disabled>
                Get Started
              </Button>
            </CardFooter>
          </Card>
          <Card className="border-primary shadow-md">
            <CardHeader className="flex flex-col items-center justify-center space-y-2">
              <div className="inline-block rounded-full bg-primary px-3 py-1 text-xs text-primary-foreground">Most Popular</div>
              <CardTitle className="text-xl">Pro Student</CardTitle>
              <CardDescription className="text-center">For serious exam preparation</CardDescription>
              <div className="flex items-baseline text-center">
                <span className="text-3xl font-bold">$5</span>
                <span className="ml-1 text-muted-foreground">/month</span>
              </div>
            </CardHeader>
            <CardContent className="flex flex-col gap-2 p-6">
              <div className="flex items-center gap-2">
                <Check className="h-4 w-4 text-primary" />
                <span className="text-sm">Unlimited courses</span>
              </div>
              <div className="flex items-center gap-2">
                <Check className="h-4 w-4 text-primary" />
                <span className="text-sm">Advanced AI summaries</span>
              </div>
              <div className="flex items-center gap-2">
                <Check className="h-4 w-4 text-primary" />
                <span className="text-sm">Unlimited flashcards</span>
              </div>
              <div className="flex items-center gap-2">
                <Check className="h-4 w-4 text-primary" />
                <span className="text-sm">Chat with your material</span>
              </div>
            </CardContent>
            <CardFooter className="flex flex-col p-6 pt-0">
              <Button className="w-full" disabled>
                Upgrade
              </Button>
            </CardFooter>
          </Card>
          <Card>
            <CardHeader className="flex flex-col items-center justify-center space-y-2">
              <CardTitle className="text-xl">Team/Institution</CardTitle>
              <CardDescription className="text-center">For study groups or universities</CardDescription>
              <div className="flex items-baseline text-center">
                <span className="text-3xl font-bold">Custom</span>
              </div>
            </CardHeader>
            <CardContent className="flex flex-col gap-2 p-6">
              <div className="flex items-center gap-2">
                <Check className="h-4 w-4 text-primary" />
                <span className="text-sm">Group management</span>
              </div>
              <div className="flex items-center gap-2">
                <Check className="h-4 w-4 text-primary" />
                <span className="text-sm">Institutional onboarding</span>
              </div>
              <div className="flex items-center gap-2">
                <Check className="h-4 w-4 text-primary" />
                <span className="text-sm">Priority support</span>
              </div>
            </CardContent>
            <CardFooter className="flex flex-col p-6 pt-0">
              <Button variant="outline" className="w-full" disabled>
                Contact Us
              </Button>
            </CardFooter>
          </Card>
        </div>
      </div>
    </section>
  )
}
