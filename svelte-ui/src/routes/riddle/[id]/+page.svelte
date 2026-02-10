<script lang="ts">
  import type { Riddle } from '$lib/types';
  import { onMount } from 'svelte';

  let { data } = $props();
  let riddle = $state<Riddle | null>(null);
  let loading = $state(true);
  let error = $state<string | null>(null);
  let pipeline = $state('');
  let submitting = $state(false);

  // Autocomplete state
  let showAutocomplete = $state(false);
  let autocompleteOptions = $state<string[]>([]);
  let selectedIndex = $state(0);
  let inputElement: HTMLInputElement;

  // Stream methods loaded from file
  let streamMethods = $state<string[]>([]);

  // Load autocomplete suggestions from file on mount
  onMount(async () => {
    try {
      const response = await fetch('/auto_complete.json');
      const data = await response.json();
      streamMethods = data.streamMethods;
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

  async function fetchRiddle(id: string) {
    try {
      const response = await fetch('http://localhost:8080/api/stream/riddles');
      if (!response.ok) throw new Error('Failed to fetch riddles');
      const riddles: Riddle[] = await response.json();
      riddle = riddles.find(r => r.id === id) || null;
      if (!riddle) throw new Error('Riddle not found');
    } catch (err) {
      error = err instanceof Error ? err.message : 'Unknown error';
    } finally {
      loading = false;
    }
  }

  $effect(() => {
    if (data?.id) {
      fetchRiddle(data.id);
    }
  });

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

  function handleInput(event: Event) {
    const target = event.target as HTMLInputElement;
    const value = target.value;
    const cursorPos = target.selectionStart || 0;

    // Find the last dot or space before the cursor
    const textBeforeCursor = value.substring(0, cursorPos);
    const lastDotIndex = textBeforeCursor.lastIndexOf('.');
    const lastSpaceIndex = textBeforeCursor.lastIndexOf(' ');

    // The start of our current "word" is the character after the last dot or space
    const lastSeparatorIndex = Math.max(lastDotIndex, lastSpaceIndex);

    // If a dot was the last separator, include it in the search (e.g., ".str")
    // Otherwise, take the word as is (e.g., "Saf")
    const searchTerm = lastDotIndex >= lastSpaceIndex
        ? textBeforeCursor.substring(lastDotIndex)
        : textBeforeCursor.substring(lastSpaceIndex + 1);

    if (searchTerm.length > 0) {
      const filtered = streamMethods.filter(method =>
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

  function selectAutocomplete(method: string) {
    const cursorPos = inputElement.selectionStart || 0;
    const textBeforeCursor = pipeline.substring(0, cursorPos);

    const lastDotIndex = textBeforeCursor.lastIndexOf('.');
    const lastSpaceIndex = textBeforeCursor.lastIndexOf(' ');

    // Determine where the current word started
    let replaceFromIndex: number;

    if (method.startsWith('.')) {
      // If we're inserting a method like ".stream()", find the last dot
      replaceFromIndex = lastDotIndex !== -1 ? lastDotIndex : cursorPos;
    } else {
      // If we're inserting a class like "SafariAnimal", replace from the last space or dot
      replaceFromIndex = Math.max(lastDotIndex, lastSpaceIndex + 1);
      if (replaceFromIndex === 0 && lastDotIndex === -1 && lastSpaceIndex === -1) {
        replaceFromIndex = 0;
      }
    }

    const before = pipeline.substring(0, Math.max(0, replaceFromIndex));
    const after = pipeline.substring(cursorPos);

    pipeline = before + method + after;

    setTimeout(() => {
      const newPos = before.length + method.length;
      inputElement.setSelectionRange(newPos, newPos);
      inputElement.focus();
    }, 0);

    showAutocomplete = false;
  }

  function handleBlur() {
    // Delay to allow click on autocomplete item
    setTimeout(() => {
      showAutocomplete = false;
    }, 200);
  }
</script>

<div class="container mx-auto px-4 py-8 max-w-2xl">
  <button onclick={() => window.history.back()} class="inline-flex items-center text-blue-600 hover:text-blue-800 mb-6">
    <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
    </svg>
    Back to all riddles
  </button>

  {#if loading}
    <div class="text-center text-gray-600">Loading riddle...</div>
  {:else if error}
    <div class="text-center text-red-500">Error: {error}</div>
  {:else if riddle}
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
            <input
                bind:this={inputElement}
                type="text"
                bind:value={pipeline}
                oninput={handleInput}
                onkeydown={handleKeyDown}
                onblur={handleBlur}
                placeholder=".stream()..."
                class="w-full px-4 py-2 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-blue-500 font-mono"
                disabled={submitting}
            />

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
</div>