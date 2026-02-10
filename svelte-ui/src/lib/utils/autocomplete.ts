export interface AutocompleteResult {
  options: string[]
  show: boolean
  selectedIndex: number
}

export function getAutocompleteOptions(
    value: string,
    cursorPos: number,
    streamMethods: string[]
): AutocompleteResult {
  const textBeforeCursor = value.substring(0, cursorPos)

  const lastDotIndex = textBeforeCursor.lastIndexOf('.')
  const lastSpaceIndex = textBeforeCursor.lastIndexOf(' ')
  const lastSeparatorIndex = Math.max(lastDotIndex, lastSpaceIndex)

  const searchTerm =
      lastDotIndex >= lastSpaceIndex
          ? textBeforeCursor.substring(lastDotIndex)
          : textBeforeCursor.substring(lastSpaceIndex + 1)

  if (!searchTerm) {
    return { options: [], show: false, selectedIndex: 0 }
  }

  const options = streamMethods.filter(method =>
      method.toLowerCase().startsWith(searchTerm.toLowerCase())
  )

  return {
    options,
    show: options.length > 0,
    selectedIndex: 0
  }
}

export function applyAutocomplete(
    pipeline: string,
    cursorPos: number,
    method: string
): { value: string; newCursorPos: number } {
  const textBeforeCursor = pipeline.substring(0, cursorPos)

  const lastDotIndex = textBeforeCursor.lastIndexOf('.')
  const lastSpaceIndex = textBeforeCursor.lastIndexOf(' ')

  let replaceFromIndex: number

  if (method.startsWith('.')) {
    replaceFromIndex = lastDotIndex !== -1 ? lastDotIndex : cursorPos
  } else {
    replaceFromIndex = Math.max(lastDotIndex, lastSpaceIndex + 1)
    if (replaceFromIndex < 0) replaceFromIndex = 0
  }

  const before = pipeline.substring(0, replaceFromIndex)
  const after = pipeline.substring(cursorPos)

  const value = before + method + after
  const newCursorPos = before.length + method.length

  return { value, newCursorPos }
}
