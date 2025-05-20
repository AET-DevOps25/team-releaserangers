import { Check } from "lucide-react"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"

export function PricingSection() {
  return (
    <section id="pricing" className="py-16 md:py-24">
      <div className="px-4 md:px-6">
        <div className="flex flex-col items-center justify-center space-y-4 text-center">
          <div className="space-y-2">
            <div className="inline-block rounded-lg bg-muted px-3 py-1 text-sm">Pricing</div>
            <h2 className="text-3xl font-bold tracking-tighter sm:text-4xl md:text-5xl">Simple, Transparent Pricing</h2>
            <p className="max-w-[900px] text-muted-foreground md:text-xl/relaxed lg:text-base/relaxed xl:text-xl/relaxed">
              Choose the plan that&apos;s right for you and your team.
            </p>
          </div>
        </div>
        <div className="mx-auto grid max-w-5xl grid-cols-1 gap-6 py-12 md:grid-cols-3">
          <Card>
            <CardHeader className="flex flex-col items-center justify-center space-y-2">
              <CardTitle className="text-xl">Starter</CardTitle>
              <CardDescription className="text-center">Perfect for individuals and small teams</CardDescription>
              <div className="flex items-baseline text-center">
                <span className="text-3xl font-bold">$9</span>
                <span className="ml-1 text-muted-foreground">/month</span>
              </div>
            </CardHeader>
            <CardContent className="flex flex-col gap-2 p-6">
              <div className="flex items-center gap-2">
                <Check className="h-4 w-4 text-primary" />
                <span className="text-sm">Up to 5 users</span>
              </div>
              <div className="flex items-center gap-2">
                <Check className="h-4 w-4 text-primary" />
                <span className="text-sm">Basic features</span>
              </div>
              <div className="flex items-center gap-2">
                <Check className="h-4 w-4 text-primary" />
                <span className="text-sm">5GB storage</span>
              </div>
              <div className="flex items-center gap-2">
                <Check className="h-4 w-4 text-primary" />
                <span className="text-sm">Email support</span>
              </div>
            </CardContent>
            <CardFooter className="flex flex-col p-6 pt-0">
              <Link href="/signup" className="w-full">
                <Button variant="outline" className="w-full">
                  Get Started
                </Button>
              </Link>
            </CardFooter>
          </Card>
          <Card className="border-primary shadow-md">
            <CardHeader className="flex flex-col items-center justify-center space-y-2">
              <div className="inline-block rounded-full bg-primary px-3 py-1 text-xs text-primary-foreground">Popular</div>
              <CardTitle className="text-xl">Pro</CardTitle>
              <CardDescription className="text-center">Ideal for growing businesses</CardDescription>
              <div className="flex items-baseline text-center">
                <span className="text-3xl font-bold">$29</span>
                <span className="ml-1 text-muted-foreground">/month</span>
              </div>
            </CardHeader>
            <CardContent className="flex flex-col gap-2 p-6">
              <div className="flex items-center gap-2">
                <Check className="h-4 w-4 text-primary" />
                <span className="text-sm">Up to 20 users</span>
              </div>
              <div className="flex items-center gap-2">
                <Check className="h-4 w-4 text-primary" />
                <span className="text-sm">Advanced features</span>
              </div>
              <div className="flex items-center gap-2">
                <Check className="h-4 w-4 text-primary" />
                <span className="text-sm">20GB storage</span>
              </div>
              <div className="flex items-center gap-2">
                <Check className="h-4 w-4 text-primary" />
                <span className="text-sm">Priority support</span>
              </div>
            </CardContent>
            <CardFooter className="flex flex-col p-6 pt-0">
              <Link href="/signup" className="w-full">
                <Button className="w-full">Get Started</Button>
              </Link>
            </CardFooter>
          </Card>
          <Card>
            <CardHeader className="flex flex-col items-center justify-center space-y-2">
              <CardTitle className="text-xl">Enterprise</CardTitle>
              <CardDescription className="text-center">For large organizations</CardDescription>
              <div className="flex items-baseline text-center">
                <span className="text-3xl font-bold">$99</span>
                <span className="ml-1 text-muted-foreground">/month</span>
              </div>
            </CardHeader>
            <CardContent className="flex flex-col gap-2 p-6">
              <div className="flex items-center gap-2">
                <Check className="h-4 w-4 text-primary" />
                <span className="text-sm">Unlimited users</span>
              </div>
              <div className="flex items-center gap-2">
                <Check className="h-4 w-4 text-primary" />
                <span className="text-sm">All features</span>
              </div>
              <div className="flex items-center gap-2">
                <Check className="h-4 w-4 text-primary" />
                <span className="text-sm">100GB storage</span>
              </div>
              <div className="flex items-center gap-2">
                <Check className="h-4 w-4 text-primary" />
                <span className="text-sm">24/7 dedicated support</span>
              </div>
            </CardContent>
            <CardFooter className="flex flex-col p-6 pt-0">
              <Link href="/signup" className="w-full">
                <Button variant="outline" className="w-full">
                  Contact Sales
                </Button>
              </Link>
            </CardFooter>
          </Card>
        </div>
      </div>
    </section>
  )
}
