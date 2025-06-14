"use client"

import type React from "react"

import { useState } from "react"
import { Loader2 } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Switch } from "@/components/ui/switch"
import { Label } from "@/components/ui/label"

export function NotificationSettings() {
  const [isSaving, setIsSaving] = useState(false)
  const [notifications, setNotifications] = useState({
    courseUpdates: true,
    newChapters: true,
    reminders: false,
    marketing: false,
    accountAlerts: true,
  })

  const handleToggle = (key: keyof typeof notifications) => {
    setNotifications((prev) => ({ ...prev, [key]: !prev[key] }))
  }

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault()
    setIsSaving(true)
    // Simulate API call
    setTimeout(() => {
      setIsSaving(false)
    }, 1500)
  }

  return (
    <Card>
      <form onSubmit={handleSave}>
        <CardHeader>
          <CardTitle>Notification Preferences</CardTitle>
          <CardDescription>Choose what notifications you receive from the platform.</CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">
          <div className="space-y-4">
            <div className="flex items-center justify-between space-x-2">
              <Label htmlFor="courseUpdates" className="flex flex-col space-y-1">
                <span>Course Updates</span>
                <span className="font-normal text-sm text-muted-foreground">Receive notifications when courses you&apos;re enrolled in are updated.</span>
              </Label>
              <Switch id="courseUpdates" checked={notifications.courseUpdates} onCheckedChange={() => handleToggle("courseUpdates")} />
            </div>

            <div className="flex items-center justify-between space-x-2">
              <Label htmlFor="newChapters" className="flex flex-col space-y-1">
                <span>New Chapters</span>
                <span className="font-normal text-sm text-muted-foreground">Get notified when new chapters are added to your courses.</span>
              </Label>
              <Switch id="newChapters" checked={notifications.newChapters} onCheckedChange={() => handleToggle("newChapters")} />
            </div>

            <div className="flex items-center justify-between space-x-2">
              <Label htmlFor="reminders" className="flex flex-col space-y-1">
                <span>Study Reminders</span>
                <span className="font-normal text-sm text-muted-foreground">Receive reminders to continue your learning journey.</span>
              </Label>
              <Switch id="reminders" checked={notifications.reminders} onCheckedChange={() => handleToggle("reminders")} />
            </div>

            <div className="flex items-center justify-between space-x-2">
              <Label htmlFor="marketing" className="flex flex-col space-y-1">
                <span>Marketing Communications</span>
                <span className="font-normal text-sm text-muted-foreground">Receive emails about new features, courses, and special offers.</span>
              </Label>
              <Switch id="marketing" checked={notifications.marketing} onCheckedChange={() => handleToggle("marketing")} />
            </div>

            <div className="flex items-center justify-between space-x-2">
              <Label htmlFor="accountAlerts" className="flex flex-col space-y-1">
                <span>Account Alerts</span>
                <span className="font-normal text-sm text-muted-foreground">Get important notifications about your account security and status.</span>
              </Label>
              <Switch id="accountAlerts" checked={notifications.accountAlerts} onCheckedChange={() => handleToggle("accountAlerts")} />
            </div>
          </div>
        </CardContent>
        <CardFooter>
          <Button type="submit" disabled={isSaving}>
            {isSaving ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Saving...
              </>
            ) : (
              "Save Preferences"
            )}
          </Button>
        </CardFooter>
      </form>
    </Card>
  )
}
