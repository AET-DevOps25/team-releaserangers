"use client"

import { useState } from "react"
import { Settings } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { AppearanceSettings } from "@/components/dashboard/settings/appearance-settings"
import { AccountSettings } from "@/components/dashboard/settings/account-settings"

interface SettingsDialogProps {
  triggerVariant?: "icon" | "default"
  className?: string
}

export function SettingsDialog({ triggerVariant = "default", className }: SettingsDialogProps) {
  const [open, setOpen] = useState(false)

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        {triggerVariant === "icon" ? (
          <Button variant="ghost" size="icon" className={className}>
            <Settings className="h-5 w-5" />
            <span className="sr-only">Settings</span>
          </Button>
        ) : (
          <Button variant="ghost" size="sm" className={`gap-2 ${className}`}>
            <Settings className="h-4 w-4" />
            <span className="font-normal">Settings</span>
          </Button>
        )}
      </DialogTrigger>
      <DialogContent className="sm:max-w-[700px] max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="text-2xl">Settings</DialogTitle>
          <DialogDescription>Manage your account settings and preferences.</DialogDescription>
        </DialogHeader>
        <Tabs defaultValue="appearance" className="w-full mt-6">
          <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="appearance">Appearance</TabsTrigger>
            <TabsTrigger value="account">Account</TabsTrigger>
          </TabsList>
          <TabsContent value="appearance" className="mt-6 space-y-4">
            <AppearanceSettings />
          </TabsContent>
          <TabsContent value="account" className="mt-6 space-y-4">
            <AccountSettings />
          </TabsContent>
        </Tabs>
      </DialogContent>
    </Dialog>
  )
}
