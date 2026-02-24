<script lang="ts">
  import type { Riddle, RiddleResult } from '$lib/types';
  import { onMount } from 'svelte';
  import { getAutocompleteOptions, applyAutocomplete } from '$lib/utils/autocomplete';
  import RiddleResultDisplay from './RiddleResultDisplay.svelte';

  // Props
  let {
    riddle = $bindable(),
    isStory = false,
    onSuccess
  }: {
    riddle: Riddle | null,
    isStory?: boolean,
    onSuccess?: () => void
  } = $props();

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
  let objectMethods = $state<string[]>([]);
  let parameterMethods = $state<string[]>([]);

  // Load autocomplete suggestions from file on mount
  onMount(async () => {
    try {
      const response = await fetch('/auto_complete.json');
      const data = await response.json();
      streamMethods = data.streamMethods;
      objectMethods = data.objectMethods;
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

  // Result state
  let result = $state<RiddleResult | null>(null);

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
      // Use relative path to avoid CORS issues if proxy is set up, or configure base URL
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
        result: {
          type: data.result?.type,
          value: data.result?.value
        },
        message: data.message,
        exception: data.exception
      };

      if (result.success && onSuccess) {
        onSuccess();
      }
    } catch (err) {
      result = {
        success: false,
        result: { type: 'SAFARIANIMAL', value: null }, // Default empty result
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

    const result = getAutocompleteOptions(value, cursorPos, {
      streamMethods,
      objectMethods,
      parameterMethods
    });

    autocompleteOptions = result.options;
    showAutocomplete = result.show;
    selectedIndex = result.selectedIndex;
  }

  function selectAutocomplete(method: string) {
    const cursorPos = inputElement.selectionStart || 0;
    const { value, newCursorPos, listType } = applyAutocomplete(pipeline, cursorPos, method);

    pipeline = value;

    setTimeout(() => {
      inputElement.setSelectionRange(newCursorPos, newCursorPos);
      inputElement.focus();

      // Show follow-up suggestions if applicable
      if (listType === 'parameter') {
        autocompleteOptions = parameterMethods;
        showAutocomplete = true;
        selectedIndex = 0;
      } else if (listType === 'object') {
        autocompleteOptions = objectMethods;
        showAutocomplete = true;
        selectedIndex = 0;
      } else {
        showAutocomplete = false;
      }
    }, 0);
  }

  function handleKeyDown(event: KeyboardEvent) {
    if (!showAutocomplete) {
      if (event.key === 'Enter' && !submitting) {
        event.preventDefault(); // Prevent newline in textarea
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
      <RiddleResultDisplay result={result} />
    {/if}
  </div>
{/if}