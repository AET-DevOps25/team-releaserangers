import { CheckCircle, Shield, Zap, Clock } from "lucide-react"

export function FeatureSection() {
  return (
    <section id="features" className="py-16 md:py-24">
      <div className="px-4 md:px-6">
        <div className="flex flex-col items-center justify-center space-y-4 text-center">
          <div className="space-y-2">
            <div className="inline-block rounded-lg bg-muted px-3 py-1 text-sm">Features</div>
            <h2 className="text-3xl font-bold tracking-tighter sm:text-4xl md:text-5xl">Everything You Need</h2>
            <p className="max-w-[900px] text-muted-foreground md:text-xl/relaxed lg:text-base/relaxed xl:text-xl/relaxed">
              Our platform provides all the tools you need to streamline your workflow and boost productivity.
            </p>
          </div>
        </div>
        <div className="mx-auto grid max-w-5xl grid-cols-1 gap-6 py-12 md:grid-cols-2 lg:gap-12">
          <div className="flex flex-col gap-2">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-muted">
              <Zap className="h-5 w-5 text-primary" />
            </div>
            <h3 className="text-xl font-bold">Lightning Fast</h3>
            <p className="text-muted-foreground">Our platform is optimized for speed, ensuring that your team can work efficiently without delays.</p>
          </div>
          <div className="flex flex-col gap-2">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-muted">
              <Shield className="h-5 w-5 text-primary" />
            </div>
            <h3 className="text-xl font-bold">Secure by Design</h3>
            <p className="text-muted-foreground">Security is built into every aspect of our platform, protecting your data and privacy at all times.</p>
          </div>
          <div className="flex flex-col gap-2">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-muted">
              <CheckCircle className="h-5 w-5 text-primary" />
            </div>
            <h3 className="text-xl font-bold">Easy to Use</h3>
            <p className="text-muted-foreground">Our intuitive interface makes it simple for anyone on your team to get started and be productive.</p>
          </div>
          <div className="flex flex-col gap-2">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-muted">
              <Clock className="h-5 w-5 text-primary" />
            </div>
            <h3 className="text-xl font-bold">Time-Saving</h3>
            <p className="text-muted-foreground">Automate repetitive tasks and focus on what matters most with our time-saving features.</p>
          </div>
        </div>
      </div>
    </section>
  )
}
