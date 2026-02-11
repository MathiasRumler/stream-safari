<script lang="ts">
  import type { Riddle } from '$lib/types';
  import { onMount } from 'svelte';

  // Props
  let { riddle = $bindable() }: { riddle: Riddle | null } = $props();

  // State
  let pipeline = $state('');
  let submitting = $state(false);

  // Autocomplete state
  let showAutocomplete = $state(false);
  let autocompleteOptions = $state<string[]>([]);
  let selectedIndex = $state(0);
  let inputElement: HTMLTextAreaElement;

  // Stream methods loaded from file
  let streamMethods = $state<string[]>([]);
  let objectmethods = $state<string[]>([]);
  let parameterMethods = $state<string[]>([]);

  // Load autocomplete suggestions from file on mount
  onMount(async () => {
    try {
      const response = await fetch('/auto_complete.json');
      const data = await response.json();
      streamMethods = data.streamMethods;
      objectmethods = data.objectMethods;
      parameterMethods = data.parameterMethods;
    } catch (err) {
      console.error('Failed to load autocomplete suggestions:', err);
      // Fallback to default methods
      streamMethods = [
        '.stream()',
        '.filter()',
        '.map()',
        '.collect()',
        '.toList()',
      ];
    }
  });

  // Result state updated to handle RiddleResult fields
  let result = $state<{
    success: boolean;
    output?: any;
    message: string;
    exception?: string;
    resultType?: string;
  } | null>(null);

  // Map Java Enum to Emojis
  const speciesIcons: Record<string, string> = {
    LION: '🦁',
    ELEPHANT: '🐘',
    GIRAFFE: '🦒',
    ZEBRA: '🦓',
    HYENA: '🐕',
    CROCODILE: '🐊',
    SNAKE: '🐍',
    LIZARD: '🦎'
  };

  async function submitSolution() {
    if (!riddle || !pipeline.trim()) return;

    submitting = true;
    result = null;
    showAutocomplete = false;

    try {
      const response = await fetch('http://localhost:8080/api/stream/submit', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          riddleId: riddle.id,
          pipeline: pipeline
        })
      });

      const data = await response.json();

      // Mapping the backend RiddleResult to our UI state
      result = {
        success: data.success,
        output: data.result?.value,
        resultType: data.result?.type,
        message: data.message,
        exception: data.exception
      };
    } catch (err) {
      result = {
        success: false,
        message: err instanceof Error ? err.message : 'Unknown error'
      };
    } finally {
      submitting = false;
    }
  }

  const constantList = ['value', 'map', 'filter', 'reduce', 'toString'];
  const staticMethodSuggestions = ['of', 'empty', 'concat', 'range', 'fromArray', 'generate'];

  function handleInput(event: Event) {
    const target = event.target as HTMLInputElement;
    const value = target.value;
    const cursorPos = target.selectionStart || 0;

    const textBeforeCursor = value.substring(0, cursorPos);
    console.log(textBeforeCursor);

    const lastDotIndex = textBeforeCursor.lastIndexOf('.');
    const lastSpaceIndex = textBeforeCursor.lastIndexOf(' ');
    const lastParenIndex = textBeforeCursor.lastIndexOf('(');
    const lastDoubleColonIndex = textBeforeCursor.lastIndexOf('::');

    // Find which separator is closest to the cursor
    const lastSeparatorIndex = Math.max(lastDotIndex, lastSpaceIndex, lastParenIndex, lastDoubleColonIndex);

    let searchTerm = '';
    let suggestionList: string[] = streamMethods;
    let showAllSuggestions = false;

    // Determine context based on which separator was last
    if (lastDoubleColonIndex === lastSeparatorIndex && lastDoubleColonIndex !== -1) {
      // After "::" - show static method suggestions
      searchTerm = textBeforeCursor.substring(lastDoubleColonIndex + 2);
      suggestionList = objectmethods;
      showAllSuggestions = true;
    } else if (lastParenIndex === lastSeparatorIndex && lastParenIndex !== -1) {
      // After "(" - show parameter suggestions
      searchTerm = textBeforeCursor.substring(lastParenIndex + 1);
      suggestionList = parameterMethods;
      showAllSuggestions = true;
    } else if (lastDotIndex >= lastSpaceIndex) {
      // After "." - show methods (original behavior)
      searchTerm = textBeforeCursor.substring(lastDotIndex);
      suggestionList = streamMethods;
    } else {
      // After space or start - original behavior
      searchTerm = textBeforeCursor.substring(lastSpaceIndex + 1);
      suggestionList = streamMethods;
    }

    // Show all suggestions after "(" or "::" or filter by search term
    if (searchTerm.length > 0 || showAllSuggestions) {
      const filtered = suggestionList.filter(method =>
          method.toLowerCase().startsWith(searchTerm.toLowerCase())
      );

      if (filtered.length > 0) {
        autocompleteOptions = filtered;
        showAutocomplete = true;
        selectedIndex = 0;
      } else {
        showAutocomplete = false;
      }
    } else {
      showAutocomplete = false;
    }
  }

  function selectAutocomplete(method: string) {
    const cursorPos = inputElement.selectionStart || 0;
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

    pipeline = before + method + after;

    // If the completed method ends with "(" or "::", show appropriate suggestions
    const endsWithParen = method.endsWith('(');
    const endsWithDoubleColon = method.endsWith('::');

    setTimeout(() => {
      const newPos = before.length + method.length;
      inputElement.setSelectionRange(newPos, newPos);
      inputElement.focus();

      // Show all parameter suggestions after autocompleting a method with "("
      if (endsWithParen) {
        autocompleteOptions = parameterMethods;
        showAutocomplete = true;
        selectedIndex = 0;
      } else if (endsWithDoubleColon) {
        // Show all static method suggestions after autocompleting with "::"
        autocompleteOptions = objectmethods;
        showAutocomplete = true;
        selectedIndex = 0;
      }
    }, 0);

    if (!endsWithParen && !endsWithDoubleColon) {
      showAutocomplete = false;
    }
  }

  function handleKeyDown(event: KeyboardEvent) {
    if (!showAutocomplete) {
      if (event.key === 'Enter' && !submitting) {
        submitSolution();
      }
      return;
    }

    switch (event.key) {
      case 'ArrowDown':
        event.preventDefault();
        selectedIndex = (selectedIndex + 1) % autocompleteOptions.length;
        break;
      case 'ArrowUp':
        event.preventDefault();
        selectedIndex = selectedIndex === 0 ? autocompleteOptions.length - 1 : selectedIndex - 1;
        break;
      case 'Enter':
      case 'Tab':
        event.preventDefault();
        selectAutocomplete(autocompleteOptions[selectedIndex]);
        break;
      case 'Escape':
        event.preventDefault();
        showAutocomplete = false;
        break;
    }
  }

  function handleBlur() {
    // Delay to allow click on autocomplete item
    setTimeout(() => {
      showAutocomplete = false;
    }, 200);
  }

  function handleInputWithResize(event: Event) {
    const target = event.target as HTMLTextAreaElement;

    // Reset height to auto to get the correct scrollHeight
    target.style.height = 'auto';

    // Set height based on scrollHeight
    target.style.height = target.scrollHeight + 'px';

    // Call your original handleInput function
    handleInput(event);
  }
</script>

{#if riddle}
  <div class="bg-white border-2 border-gray-200 rounded-lg shadow-lg p-8">
    <div class="mb-4">
      <span class="text-sm font-semibold text-blue-600">Riddle #{riddle.id}</span>
    </div>

    <h1 class="text-3xl font-bold mb-6 text-gray-800">{riddle.description}</h1>

    <div class="bg-gray-50 p-6 rounded-xl border-2 border-dashed border-gray-300 mb-8">
      <h2 class="text-xs font-bold text-gray-400 uppercase tracking-widest mb-4">Input Data:</h2>
      <div class="flex flex-wrap gap-4">
        {#each riddle.input as animal}
          <div class="group relative flex items-center justify-center w-14 h-14 bg-white rounded-full shadow-sm border border-gray-200 hover:border-blue-500 hover:scale-110 transition-all cursor-help">
            <span class="text-2xl" role="img" aria-label={animal.species}>
              {speciesIcons[animal.species] || '🐾'}
            </span>

            <div class="absolute bottom-full mb-3 hidden group-hover:block z-50 w-48 p-3 bg-gray-900 text-white text-xs rounded-lg shadow-xl">
              <p class="font-bold border-b border-gray-700 pb-1 mb-1 text-blue-400">{animal.name}</p>
              <ul class="space-y-1">
                <li>Species: {animal.species}</li>
                <li>Age: {animal.age}</li>
                <li>Weight: {animal.weight}kg</li>
                <li>{animal.predator ? '🔴 Predator' : '🟢 Prey'}</li>
              </ul>
              <div class="absolute top-full left-1/2 -translate-x-1/2 border-8 border-transparent border-t-gray-900"></div>
            </div>
          </div>
        {/each}
      </div>
    </div>

    <div class="mt-8">
      <h2 class="text-lg font-semibold mb-3 text-gray-700">Your Solution:</h2>
      <div class="flex gap-2 relative">
        <div class="flex-1 relative">
            <textarea
                bind:this={inputElement}
                bind:value={pipeline}
                oninput={handleInputWithResize}
                onkeydown={handleKeyDown}
                onblur={handleBlur}
                placeholder=".stream()..."
                rows="1"
                class="w-full px-4 py-2 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-blue-500 font-mono resize-none overflow-hidden"
                disabled={submitting}
            ></textarea>

          {#if showAutocomplete && autocompleteOptions.length > 0}
            <div class="absolute top-full left-0 right-0 mt-1 bg-white border-2 border-gray-300 rounded-lg shadow-lg max-h-60 overflow-y-auto z-50">
              {#each autocompleteOptions as option, index}
                <button
                    type="button"
                    onclick={() => selectAutocomplete(option)}
                    class="w-full text-left px-4 py-2 font-mono text-sm hover:bg-blue-50 {index === selectedIndex ? 'bg-blue-100' : ''}"
                >
                  {option}
                </button>
              {/each}
            </div>
          {/if}
        </div>

        <button
            onclick={submitSolution}
            disabled={submitting || !pipeline.trim()}
            class="px-6 py-2 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 disabled:bg-gray-400 transition-colors"
        >
          {submitting ? 'Submitting...' : 'Submit'}
        </button>
      </div>
      <p class="text-xs text-gray-500 mt-2">💡 Type a dot (.) to see autocomplete suggestions</p>
    </div>

    {#if result}
      <div class="mt-6 p-5 rounded-lg {result.success ? 'bg-green-50 border-green-500' : 'bg-red-50 border-red-500'} border-2">
        <div class="flex items-start gap-3">
          <div class="flex-1">
            <p class="font-bold mb-2 {result.success ? 'text-green-800' : 'text-red-800'}">
              {result.message}
            </p>

            {#if result.exception}
              <p class="text-xs text-red-700 font-mono bg-red-100 p-2 rounded mb-3">Exception: {result.exception}</p>
            {/if}

            {#if result.output !== undefined}
              <div class="bg-white bg-opacity-60 p-3 rounded border border-gray-200">
                <p class="text-[10px] uppercase font-bold text-gray-500 mb-2">Output ({result.resultType}):</p>

                {#if result.resultType === 'NUMBER'}
                  <span class="text-2xl font-mono font-bold text-blue-700">{result.output}</span>
                {:else if result.resultType === 'LIST' || result.resultType === 'SAFARIANIMAL'}
                  <div class="flex flex-wrap gap-4">
                    {#each (Array.isArray(result.output) ? result.output : [result.output]) as animal}
                      <div class="group relative flex items-center justify-center w-14 h-14 bg-white rounded-full shadow-sm border border-gray-200 hover:border-green-500 hover:scale-110 transition-all cursor-help">
                          <span class="text-2xl" role="img" aria-label={animal.species}>
                            {speciesIcons[animal.species] || '🐾'}
                          </span>

                        <div class="absolute bottom-full mb-3 hidden group-hover:block z-50 w-48 p-3 bg-gray-900 text-white text-xs rounded-lg shadow-xl">
                          <p class="font-bold border-b border-gray-700 pb-1 mb-1 text-green-400">{animal.name}</p>
                          <ul class="space-y-1">
                            <li>Species: {animal.species}</li>
                            <li>Age: {animal.age}</li>
                            <li>Weight: {animal.weight}kg</li>
                            <li>{animal.predator ? '🔴 Predator' : '🟢 Prey'}</li>
                          </ul>
                          <div class="absolute top-full left-1/2 -translate-x-1/2 border-8 border-transparent border-t-gray-900"></div>
                        </div>
                      </div>
                    {/each}
                  </div>
                {:else if result.resultType === 'MAP'}
                  <div class="space-y-3">
                    {#each Object.entries(result.output) as [key, val]}
                      <div class="flex items-start gap-3">
                        <span class="font-bold text-purple-700 text-xs uppercase tracking-wide min-w-[80px] pt-3">{key}</span>
                        <div class="flex-1">
                          {#if typeof val === 'object' && val !== null}
                            {#if Array.isArray(val)}
                              <div class="flex flex-wrap gap-3">
                                {#each val as item}
                                  {#if item?.species}
                                    <div class="group relative flex items-center justify-center w-12 h-12 bg-white rounded-full shadow-sm border border-gray-200 hover:border-purple-500 hover:scale-110 transition-all cursor-help">
                                        <span class="text-xl" role="img" aria-label={item.species}>
                                          {speciesIcons[item.species] || '🐾'}
                                        </span>

                                      <div class="absolute bottom-full mb-2 hidden group-hover:block z-50 w-44 p-2.5 bg-gray-900 text-white text-xs rounded-lg shadow-xl">
                                        <p class="font-bold border-b border-gray-700 pb-1 mb-1 text-purple-400">{item.name}</p>
                                        <ul class="space-y-0.5">
                                          <li>Species: {item.species}</li>
                                          <li>Age: {item.age}</li>
                                          <li>Weight: {item.weight}kg</li>
                                          <li>{item.predator ? '🔴 Predator' : '🟢 Prey'}</li>
                                        </ul>
                                        <div class="absolute top-full left-1/2 -translate-x-1/2 border-6 border-transparent border-t-gray-900"></div>
                                      </div>
                                    </div>
                                  {:else}
                                    <span class="px-2 py-1 bg-gray-100 rounded text-xs">{JSON.stringify(item)}</span>
                                  {/if}
                                {/each}
                              </div>
                            {:else if val.species}
                              <!-- Single animal object -->
                              <div class="group relative inline-flex items-center justify-center w-12 h-12 bg-white rounded-full shadow-sm border border-gray-200 hover:border-purple-500 hover:scale-110 transition-all cursor-help">
                                  <span class="text-xl" role="img" aria-label={val.species}>
                                    {speciesIcons[val.species] || '🐾'}
                                  </span>

                                <div class="absolute bottom-full mb-2 hidden group-hover:block z-50 w-44 p-2.5 bg-gray-900 text-white text-xs rounded-lg shadow-xl">
                                  <p class="font-bold border-b border-gray-700 pb-1 mb-1 text-purple-400">{val.name}</p>
                                  <ul class="space-y-0.5">
                                    <li>Species: {val.species}</li>
                                    <li>Age: {val.age}</li>
                                    <li>Weight: {val.weight}kg</li>
                                    <li>{val.predator ? '🔴 Predator' : '🟢 Prey'}</li>
                                  </ul>
                                  <div class="absolute top-full left-1/2 -translate-x-1/2 border-6 border-transparent border-t-gray-900"></div>
                                </div>
                              </div>
                            {:else}
                              <!-- Other object types -->
                              <code class="text-xs text-gray-700 bg-gray-50 px-2 py-1 rounded">{JSON.stringify(val)}</code>
                            {/if}
                          {:else}
                            <!-- Primitive values -->
                            <span class="text-sm text-gray-700 font-mono">{val}</span>
                          {/if}
                        </div>
                      </div>
                    {/each}
                  </div>
                {:else}
                  <code class="text-sm break-all">{JSON.stringify(result.output)}</code>
                {/if}
              </div>
            {/if}
          </div>
        </div>
      </div>
    {/if}
  </div>
{/if}