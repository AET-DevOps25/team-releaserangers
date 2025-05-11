import { GalleryVerticalEnd } from "lucide-react"
import Link from "next/link"

export function FooterSection() {
  return (
    <footer className="border-t bg-background py-12">
      <div className="px-4 md:px-6 max-w-3/4 mx-auto">
        <div className="grid grid-cols-1 gap-8 md:grid-cols-2 lg:grid-cols-4">
          <div className="flex flex-col gap-2">
            <Link href="/" className="flex items-center gap-2 font-medium">
              <div className="flex h-6 w-6 items-center justify-center rounded-md bg-primary text-primary-foreground">
                <GalleryVerticalEnd className="size-4" />
              </div>
              Acme Inc.
            </Link>
            <p className="text-sm text-muted-foreground">Simplifying workflows and boosting productivity for teams around the world.</p>
          </div>
          <div className="flex flex-col gap-2">
            <h3 className="text-lg font-medium">Product</h3>
            <Link href="#features" className="text-sm text-muted-foreground hover:text-foreground">
              Features
            </Link>
            <Link href="#pricing" className="text-sm text-muted-foreground hover:text-foreground">
              Pricing
            </Link>
            <Link href="#" className="text-sm text-muted-foreground hover:text-foreground">
              Integrations
            </Link>
            <Link href="#" className="text-sm text-muted-foreground hover:text-foreground">
              Changelog
            </Link>
          </div>
          <div className="flex flex-col gap-2">
            <h3 className="text-lg font-medium">Resources</h3>
            <Link href="#" className="text-sm text-muted-foreground hover:text-foreground">
              Documentation
            </Link>
            <Link href="#" className="text-sm text-muted-foreground hover:text-foreground">
              Guides
            </Link>
            <Link href="#" className="text-sm text-muted-foreground hover:text-foreground">
              Support
            </Link>
            <Link href="#" className="text-sm text-muted-foreground hover:text-foreground">
              API
            </Link>
          </div>
          <div className="flex flex-col gap-2">
            <h3 className="text-lg font-medium">Company</h3>
            <Link href="#" className="text-sm text-muted-foreground hover:text-foreground">
              About
            </Link>
            <Link href="#" className="text-sm text-muted-foreground hover:text-foreground">
              Blog
            </Link>
            <Link href="#" className="text-sm text-muted-foreground hover:text-foreground">
              Careers
            </Link>
            <Link href="#" className="text-sm text-muted-foreground hover:text-foreground">
              Contact
            </Link>
          </div>
        </div>
        <div className="mt-8 border-t pt-8">
          <div className="flex flex-col items-center justify-between gap-4 md:flex-row">
            <p className="text-sm text-muted-foreground">© {new Date().getFullYear()} Acme Inc. All rights reserved.</p>
            <div className="flex gap-4">
              <Link href="#" className="text-sm text-muted-foreground hover:text-foreground">
                Terms
              </Link>
              <Link href="#" className="text-sm text-muted-foreground hover:text-foreground">
                Privacy
              </Link>
              <Link href="#" className="text-sm text-muted-foreground hover:text-foreground">
                Cookies
              </Link>
            </div>
          </div>
        </div>
      </div>
    </footer>
  )
}
