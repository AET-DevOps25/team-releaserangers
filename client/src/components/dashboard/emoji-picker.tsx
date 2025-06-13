import { useState } from "react"
import { Popover, PopoverContent, PopoverTrigger } from "../ui/popover"
import { Button } from "../ui/button"
import { EmojiPicker, EmojiPickerContent, EmojiPickerFooter, EmojiPickerSearch } from "@/components/ui/emoji-picker"

export function EmojiPickerComponent({ emoji, onEmojiSelect }: { emoji: string; onEmojiSelect: (emoji: string) => void }) {
  const [isOpen, setIsOpen] = useState(false)
  return (
    <>
      <Popover onOpenChange={setIsOpen} modal={true} open={isOpen}>
        <PopoverTrigger asChild>
          <Button variant="outline" className="h-10 w-10 p-0">
            {emoji}
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-fit p-0">
          <EmojiPicker
            className="h-[326px] rounded-lg border shadow-md"
            onEmojiSelect={({ emoji }) => {
              setIsOpen(false)
              onEmojiSelect(emoji)
            }}
          >
            <EmojiPickerSearch />
            <EmojiPickerContent />
            <EmojiPickerFooter />
          </EmojiPicker>
        </PopoverContent>
      </Popover>
    </>
  )
}
