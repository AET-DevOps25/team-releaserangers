import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Card, CardContent } from "@/components/ui/card"

export function TestimonialSection() {
  return (
    <section id="testimonials" className="bg-muted py-16 md:py-24">
      <div className="px-4 md:px-6">
        <div className="flex flex-col items-center justify-center space-y-4 text-center">
          <div className="space-y-2">
            <div className="inline-block rounded-lg bg-background px-3 py-1 text-sm">Testimonials</div>
            <h2 className="text-3xl font-bold tracking-tighter sm:text-4xl md:text-5xl">What Our Customers Say</h2>
            <p className="max-w-[900px] text-muted-foreground md:text-xl/relaxed lg:text-base/relaxed xl:text-xl/relaxed">
              Don&apos;t just take our word for it. Here&apos;s what our customers have to say about our platform.
            </p>
          </div>
        </div>
        <div className="mx-auto grid max-w-5xl grid-cols-1 gap-6 py-12 md:grid-cols-2 lg:grid-cols-3">
          <Card>
            <CardContent className="p-6">
              <div className="flex flex-col gap-4">
                <div className="flex items-center gap-4">
                  <Avatar>
                    <AvatarImage src="/placeholder.svg?height=40&width=40" alt="Avatar" />
                  </Avatar>
                  <div>
                    <p className="text-sm font-medium">Jane Doe</p>
                    <p className="text-sm text-muted-foreground">CEO, Company A</p>
                  </div>
                </div>
                <p className="text-muted-foreground">
                  &quot;Acme Inc. has transformed how our team works together. The platform is intuitive and has all the features we need.&quot;
                </p>
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-6">
              <div className="flex flex-col gap-4">
                <div className="flex items-center gap-4">
                  <Avatar>
                    {/* <AvatarImage src="/placeholder.svg?height=40&width=40" alt="Avatar" /> */}
                    <AvatarFallback>JS</AvatarFallback>
                  </Avatar>
                  <div>
                    <p className="text-sm font-medium">John Smith</p>
                    <p className="text-sm text-muted-foreground">CTO, Company B</p>
                  </div>
                </div>
                <p className="text-muted-foreground">&quot;The security features are top-notch, and the customer support team is always responsive and helpful.&quot;</p>
              </div>
            </CardContent>
          </Card>
          <Card className="md:col-span-2 lg:col-span-1">
            <CardContent className="p-6">
              <div className="flex flex-col gap-4">
                <div className="flex items-center gap-4">
                  <Avatar>
                    {/* <AvatarImage src="/placeholder.svg?height=40&width=40" alt="Avatar" /> */}
                    <AvatarFallback>EJ</AvatarFallback>
                  </Avatar>
                  <div>
                    <p className="text-sm font-medium">Emily Johnson</p>
                    <p className="text-sm text-muted-foreground">Product Manager, Company C</p>
                  </div>
                </div>
                <p className="text-muted-foreground">
                  &quot;We&apos;ve seen a 30% increase in productivity since implementing Acme Inc. It&apos;s been a game-changer for our team.&quot;
                </p>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </section>
  )
}
