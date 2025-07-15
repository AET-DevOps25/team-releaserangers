import { Avatar, AvatarFallback } from "@/components/ui/avatar"
import { Card, CardContent } from "@/components/ui/card"

export function TestimonialSection() {
  return (
    <section id="testimonials" className="bg-muted py-16 md:py-24">
      <div className="px-4 md:px-6">
        <div className="flex flex-col items-center justify-center space-y-4 text-center">
          <div className="space-y-2">
            <div className="inline-block rounded-lg bg-background px-3 py-1 text-sm">Testimonials</div>
            <h2 className="text-3xl font-bold tracking-tighter sm:text-4xl md:text-5xl">How Students Use Our Platform</h2>
            <p className="max-w-[900px] text-muted-foreground md:text-xl/relaxed lg:text-base/relaxed xl:text-xl/relaxed">
              Real scenarios from students who improved their exam preparation with our platform.
            </p>
          </div>
        </div>
        <div className="mx-auto grid max-w-5xl grid-cols-1 gap-6 py-12 md:grid-cols-2 lg:grid-cols-3">
          <Card>
            <CardContent className="p-6">
              <div className="flex flex-col gap-4">
                <div className="flex items-center gap-4">
                  <Avatar>
                    <AvatarFallback>AN</AvatarFallback>
                  </Avatar>
                  <div>
                    <p className="text-sm font-medium">Anna, Computer Science Student</p>
                  </div>
                </div>
                <p className="text-muted-foreground">
                  &quot;I was stressed before exams, but after uploading my messy notes and slides, this app organized everything into clear chapters and summaries. I finally had
                  an overview of the whole course in seconds!&quot;
                </p>
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-6">
              <div className="flex flex-col gap-4">
                <div className="flex items-center gap-4">
                  <Avatar>
                    <AvatarFallback>MX</AvatarFallback>
                  </Avatar>
                  <div>
                    <p className="text-sm font-medium">Max, Engineering Student</p>
                  </div>
                </div>
                <p className="text-muted-foreground">
                  &quot;I upload my lecture material every week. The app keeps my summaries up to date, so I never feel lost when exam season comes around.&quot;
                </p>
              </div>
            </CardContent>
          </Card>
          <Card className="md:col-span-2 lg:col-span-1">
            <CardContent className="p-6">
              <div className="flex flex-col gap-4">
                <div className="flex items-center gap-4">
                  <Avatar>
                    <AvatarFallback>TM</AvatarFallback>
                  </Avatar>
                  <div>
                    <p className="text-sm font-medium">Tom, Business Student</p>
                  </div>
                </div>
                <p className="text-muted-foreground">
                  &quot;After missing two weeks of lectures, I uploaded my friend&apos;s notes and quickly caught up using the AI-generated summaries. I didn&apos;t have to read
                  every document to know what I missed.&quot;
                </p>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </section>
  )
}
