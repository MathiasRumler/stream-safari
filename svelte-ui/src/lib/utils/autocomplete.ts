export interface AutocompleteResult {
  options: string[]
  show: boolean
  selectedIndex: number
}

export interface AutocompleteLists {
  streamMethods: string[];
  objectMethods: string[];
  parameterMethods: string[];
}

export function getAutocompleteOptions(
    value: string,
    cursorPos: number,
    lists: AutocompleteLists
): AutocompleteResult {
  const textBeforeCursor = value.substring(0, cursorPos);

  const lastDotIndex = textBeforeCursor.lastIndexOf('.');
  const lastSpaceIndex = textBeforeCursor.lastIndexOf(' ');
  const lastParenIndex = textBeforeCursor.lastIndexOf('(');
  const lastDoubleColonIndex = textBeforeCursor.lastIndexOf('::');

  // Find which separator is closest to the cursor
  const lastSeparatorIndex = Math.max(lastDotIndex, lastSpaceIndex, lastParenIndex, lastDoubleColonIndex);

  let searchTerm = '';
  let suggestionList: string[] = lists.streamMethods;
  let showAllSuggestions = false;

  // Determine context based on which separator was last
  if (lastDoubleColonIndex === lastSeparatorIndex && lastDoubleColonIndex !== -1) {
    // After "::" - show static method suggestions
    searchTerm = textBeforeCursor.substring(lastDoubleColonIndex + 2);
    suggestionList = lists.objectMethods;
    showAllSuggestions = true;
  } else if (lastParenIndex === lastSeparatorIndex && lastParenIndex !== -1) {
    // After "(" - show parameter suggestions
    searchTerm = textBeforeCursor.substring(lastParenIndex + 1);
    suggestionList = lists.parameterMethods;
    showAllSuggestions = true;
  } else if (lastDotIndex >= lastSpaceIndex) {
    // After "." - show methods
    searchTerm = textBeforeCursor.substring(lastDotIndex);
    suggestionList = lists.streamMethods;
  } else {
    // After space or start
    searchTerm = textBeforeCursor.substring(lastSpaceIndex + 1);
    suggestionList = lists.streamMethods;
  }

  if (!searchTerm && !showAllSuggestions) {
    return { options: [], show: false, selectedIndex: 0 };
  }

  const options = suggestionList.filter(method =>
      method.toLowerCase().startsWith(searchTerm.toLowerCase())
  );

  return {
    options,
    show: options.length > 0,
    selectedIndex: 0
  };
}

export function applyAutocomplete(
    pipeline: string,
    cursorPos: number,
    method: string
): { value: string; newCursorPos: number; listType?: 'stream' | 'object' | 'parameter' } {
  const textBeforeCursor = pipeline.substring(0, cursorPos);

  const lastDotIndex = textBeforeCursor.lastIndexOf('.');
  const lastSpaceIndex = textBeforeCursor.lastIndexOf(' ');
  const lastParenIndex = textBeforeCursor.lastIndexOf('(');
  const lastDoubleColonIndex = textBeforeCursor.lastIndexOf('::');

  const lastSeparatorIndex = Math.max(lastDotIndex, lastSpaceIndex, lastParenIndex, lastDoubleColonIndex);

  let replaceFromIndex: number;

  if (lastDoubleColonIndex === lastSeparatorIndex && lastDoubleColonIndex !== -1) {
    // After "::" - replace from after the double colon
    replaceFromIndex = lastDoubleColonIndex + 2;
  } else if (lastParenIndex === lastSeparatorIndex && lastParenIndex !== -1) {
    // After "(" - replace from after the paren
    replaceFromIndex = lastParenIndex + 1;
  } else if (method.startsWith('.')) {
    // If we're inserting a method like ".stream()", find the last dot
    replaceFromIndex = lastDotIndex !== -1 ? lastDotIndex : cursorPos;
  } else {
    // Original behavior for classes/words
    replaceFromIndex = Math.max(lastDotIndex, lastSpaceIndex + 1);
    if (replaceFromIndex === 0 && lastDotIndex === -1 && lastSpaceIndex === -1) {
      replaceFromIndex = 0;
    }
  }

  const before = pipeline.substring(0, Math.max(0, replaceFromIndex));
  const after = pipeline.substring(cursorPos);

  const value = before + method + after;
  const newCursorPos = before.length + method.length;

  // Determine list type for follow-up suggestions
  let listType: 'stream' | 'object' | 'parameter' | undefined;
  if (method.endsWith('(')) {
    listType = 'parameter';
  } else if (method.endsWith('::')) {
    listType = 'object';
  }

  return { value, newCursorPos, listType };
}
