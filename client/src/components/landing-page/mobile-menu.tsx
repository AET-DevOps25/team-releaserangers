"use client"

import { useState } from "react"
import Link from "next/link"
import { Menu } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Sheet, SheetContent, SheetDescription, SheetHeader, SheetTitle, SheetTrigger } from "@/components/ui/sheet"

export function MobileMenu() {
  const [open, setOpen] = useState(false)

  return (
    <Sheet open={open} onOpenChange={setOpen}>
      <SheetTrigger asChild>
        <Button variant="ghost" size="icon" className="md:hidden">
          <Menu className="h-5 w-5" />
          <span className="sr-only">Toggle menu</span>
        </Button>
      </SheetTrigger>
      <SheetContent side="right" className="flex flex-col">
        <SheetHeader>
          <SheetTitle className="text-lg font-semibold">Menu</SheetTitle>
          <SheetDescription className="text-sm text-muted-foreground">Navigate through the app</SheetDescription>
        </SheetHeader>
        <nav className="flex flex-col gap-4 px-4">
          <Link href="#features" className="text-lg font-medium text-muted-foreground hover:text-foreground" onClick={() => setOpen(false)}>
            Features
          </Link>
          <Link href="#testimonials" className="text-lg font-medium text-muted-foreground hover:text-foreground" onClick={() => setOpen(false)}>
            Testimonials
          </Link>
          <Link href="#pricing" className="text-lg font-medium text-muted-foreground hover:text-foreground" onClick={() => setOpen(false)}>
            Pricing
          </Link>
          <div className="mt-4 flex gap-2 justify-start">
            <Link href="/login" onClick={() => setOpen(false)}>
              <Button variant="outline" className="">
                Login
              </Button>
            </Link>
            <Link href="/signup" onClick={() => setOpen(false)}>
              <Button className="">Sign up</Button>
            </Link>
          </div>
        </nav>
      </SheetContent>
    </Sheet>
  )
}
