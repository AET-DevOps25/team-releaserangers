import { test, expect } from "@playwright/test"

test("test_password_match", async ({ page }) => {
  await page.goto("/")
  await expect(page.getByRole("button", { name: "Login" })).toBeVisible()
  await page.getByRole("button", { name: "Sign up" }).click()
  await expect(page.getByRole("button", { name: "Create Account" })).toBeVisible()
  await page.getByRole("textbox", { name: "Full Name" }).click()
  await page.getByRole("textbox", { name: "Full Name" }).fill("John Doe")
  await page.getByRole("textbox", { name: "Full Name" }).press("Tab")
  await page.getByRole("textbox", { name: "Email" }).fill("email@email.com")
  await page.getByRole("textbox", { name: "Password", exact: true }).click()
  await page.getByRole("textbox", { name: "Password", exact: true }).fill("password")
  await page.getByRole("textbox", { name: "Confirm Password" }).click()
  await page.getByRole("textbox", { name: "Confirm Password" }).fill("password1")
  await page.getByRole("button", { name: "Create Account" }).click()
  await expect(page.getByText("Passwords do not match")).toBeVisible()
})
