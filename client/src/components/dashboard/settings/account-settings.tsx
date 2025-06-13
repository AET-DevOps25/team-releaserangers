"use client"

import type React from "react"

import { useState } from "react"
import { Loader2 } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog"
import useUserStore from "@/hooks/user-store"
import { useRouter } from "next/navigation"
import z from "zod"

export function AccountSettings() {
  const { user, deleteUser, updateUser } = useUserStore()
  const [isLoadingUpdateProfile, setIsLoadingUpdateProfile] = useState(false)
  const [isLoadingUpdatePassword, setIsLoadingUpdatePassword] = useState(false)
  const [isLoadingDeleteAccount, setIsLoadingDeleteAccount] = useState(false)
  const [formData, setFormData] = useState({
    name: user?.name || "username unavailable",
    email: user?.email || "email unavailable",
    newPassword: "",
    confirmPassword: "",
  })
  const [passwordErrors, setPasswordErrors] = useState<{ newPassword?: string; confirmPassword?: string }>({})
  const router = useRouter()

  const formSchema = z
    .object({
      newPassword: z.string().min(8, "Password must be at least 8 characters long"),
      confirmPassword: z.string().min(8, "Confirm Password must be at least 8 characters long"),
    })
    .refine((data) => data.newPassword === data.confirmPassword, {
      message: "Passwords do not match",
      path: ["confirmPassword"],
    })

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleProfileUpdate = async (e: React.FormEvent) => {
    setIsLoadingUpdateProfile(true)
    e.preventDefault()
    await updateUser({
      name: formData.name,
      email: formData.email,
    })
    setIsLoadingUpdateProfile(false)
  }

  const handlePasswordChange = async (e: React.FormEvent) => {
    e.preventDefault()
    setPasswordErrors({})
    try {
      await formSchema.parseAsync({
        newPassword: formData.newPassword,
        confirmPassword: formData.confirmPassword,
      })
      setIsLoadingUpdatePassword(true)
      await updateUser({
        password: formData.newPassword,
      })
      setIsLoadingUpdatePassword(false)
      setFormData((prev) => ({
        ...prev,
        newPassword: "",
        confirmPassword: "",
      }))
    } catch (error) {
      if (error instanceof z.ZodError) {
        const fieldErrors: { newPassword?: string; confirmPassword?: string } = {}
        error.errors.forEach((err) => {
          if (err.path[0] === "newPassword") fieldErrors.newPassword = err.message
          if (err.path[0] === "confirmPassword") fieldErrors.confirmPassword = err.message
        })
        setPasswordErrors(fieldErrors)
      }
    }
  }

  const handleDeleteAccount = async () => {
    setIsLoadingDeleteAccount(true)
    await deleteUser()
    setIsLoadingDeleteAccount(false)
    router.push("/")
  }

  return (
    <div className="space-y-6">
      <Card>
        <form onSubmit={handleProfileUpdate}>
          <CardHeader>
            <CardTitle>Profile Information</CardTitle>
            <CardDescription>Update your account details and email address.</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4 mt-4">
            <div className="space-y-2">
              <Label htmlFor="name">Name</Label>
              <Input id="name" name="name" value={formData.name} onChange={handleChange} />
            </div>
            <div className="space-y-2">
              <Label htmlFor="email">Email</Label>
              <Input id="email" name="email" type="email" value={formData.email} onChange={handleChange} />
            </div>
          </CardContent>
          <CardFooter>
            <Button type="submit" disabled={isLoadingUpdateProfile} className="mt-4">
              {isLoadingUpdateProfile ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Updating...
                </>
              ) : (
                "Update Profile"
              )}
            </Button>
          </CardFooter>
        </form>
      </Card>

      <Card>
        <form onSubmit={handlePasswordChange}>
          <CardHeader>
            <CardTitle>Change Password</CardTitle>
            <CardDescription>Update your password to keep your account secure.</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4 mt-4">
            <div className="space-y-2">
              <Label htmlFor="newPassword">New Password</Label>
              <Input id="newPassword" name="newPassword" type="password" value={formData.newPassword} onChange={handleChange} />
              {passwordErrors.newPassword && <span className="text-xs text-red-500">{passwordErrors.newPassword}</span>}
            </div>
            <div className="space-y-2">
              <Label htmlFor="confirmPassword">Confirm New Password</Label>
              <Input id="confirmPassword" name="confirmPassword" type="password" value={formData.confirmPassword} onChange={handleChange} />
              {passwordErrors.confirmPassword && <span className="text-xs text-red-500">{passwordErrors.confirmPassword}</span>}
            </div>
          </CardContent>
          <CardFooter>
            <Button type="submit" disabled={isLoadingUpdatePassword} className="mt-4">
              {isLoadingUpdatePassword ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Updating...
                </>
              ) : (
                "Change Password"
              )}
            </Button>
          </CardFooter>
        </form>
      </Card>

      <Card className="border-destructive/20">
        <CardHeader>
          <CardTitle className="text-destructive">Danger Zone</CardTitle>
          <CardDescription>Permanently delete your account and all of your content from the platform.</CardDescription>
        </CardHeader>
        <CardContent>
          <p className="text-sm text-muted-foreground">Once you delete your account, there is no going back. This action cannot be undone.</p>
        </CardContent>
        <CardFooter>
          <AlertDialog>
            <AlertDialogTrigger asChild>
              <Button variant="destructive">Delete Account</Button>
            </AlertDialogTrigger>
            <AlertDialogContent>
              <AlertDialogHeader>
                <AlertDialogTitle>Are you absolutely sure?</AlertDialogTitle>
                <AlertDialogDescription>This action cannot be undone. This will permanently delete your account and remove your data from our servers.</AlertDialogDescription>
              </AlertDialogHeader>
              <AlertDialogFooter>
                <AlertDialogCancel>Cancel</AlertDialogCancel>
                <AlertDialogAction onClick={handleDeleteAccount} className="bg-destructive hover:bg-destructive/90">
                  {isLoadingDeleteAccount ? (
                    <>
                      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                      Deleting...
                    </>
                  ) : (
                    "Delete Account"
                  )}
                </AlertDialogAction>
              </AlertDialogFooter>
            </AlertDialogContent>
          </AlertDialog>
        </CardFooter>
      </Card>
    </div>
  )
}
