import { GalleryVerticalEnd } from "lucide-react"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { HeroSection } from "@/components/landing-page/hero-section"
import { FeatureSection } from "@/components/landing-page/feature-section"
import { TestimonialSection } from "@/components/landing-page/testimonial-section"
import { PricingSection } from "@/components/landing-page/pricing-section"
import { FooterSection } from "@/components/landing-page/footer-section"
import { MobileMenu } from "@/components/landing-page/mobile-menu"
import { ModeToggle } from "@/components/landing-page/mode-toggle"

export default function LandingPage() {
  return (
    <div className="flex min-h-screen flex-col">
      {/* Navigation */}
      <header className="sticky top-0 z-40 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="flex h-16 items-center justify-between px-4 md:px-6 max-w-3/4 mx-auto">
          <Link href="/" className="flex items-center gap-2 font-medium">
            <div className="flex h-6 w-6 items-center justify-center rounded-md bg-primary text-primary-foreground">
              <GalleryVerticalEnd className="size-4" />
            </div>
            ReleaseRangers
          </Link>

          {/* Desktop Navigation - hidden on mobile */}
          <nav className="hidden md:flex items-center justify-start flex-1 ml-8">
            <div className="flex items-center gap-6">
              <Link href="#features" className="text-sm font-medium text-muted-foreground hover:text-foreground">
                Features
              </Link>
              <Link href="#testimonials" className="text-sm font-medium text-muted-foreground hover:text-foreground">
                Testimonials
              </Link>
              <Link href="#pricing" className="text-sm font-medium text-muted-foreground hover:text-foreground">
                Pricing
              </Link>
            </div>
          </nav>

          <div className="flex items-center gap-2">
            <div className="hidden md:flex items-center gap-2">
              <ModeToggle />
              <Link href="/login">
                <Button variant="ghost" size="sm">
                  Login
                </Button>
              </Link>
              <Link href="/signup">
                <Button size="sm">Sign up</Button>
              </Link>
            </div>
            <MobileMenu />
          </div>
        </div>
      </header>

      <main className="flex-1">
        <HeroSection />
        <FeatureSection />
        <TestimonialSection />
        <PricingSection />
      </main>

      <FooterSection />
    </div>
  )
}
