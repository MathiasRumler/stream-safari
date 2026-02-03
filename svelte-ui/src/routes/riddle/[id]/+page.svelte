<script lang="ts">
  import type { Riddle } from '$lib/types';

  let { data } = $props();
  let riddle = $state<Riddle | null>(null);
  let loading = $state(true);
  let error = $state<string | null>(null);
  let pipeline = $state('');
  let submitting = $state(false);

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

  function handleKeyPress(event: KeyboardEvent) {
    if (event.key === 'Enter' && !submitting) {
      submitSolution();
    }
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
        <div class="flex gap-2">
          <input
              type="text"
              bind:value={pipeline}
              onkeypress={handleKeyPress}
              placeholder=".stream()..."
              class="flex-1 px-4 py-2 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-blue-500 font-mono"
              disabled={submitting}
          />
          <button
              onclick={submitSolution}
              disabled={submitting || !pipeline.trim()}
              class="px-6 py-2 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 disabled:bg-gray-400 transition-colors"
          >
            {submitting ? 'Submitting...' : 'Submit'}
          </button>
        </div>
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
                    <div class="text-sm font-mono divide-y divide-gray-200">
                      {#each Object.entries(result.output) as [key, val]}
                        <div class="py-2 flex justify-between items-start gap-4">
                          <span class="font-bold text-purple-700">{key}:</span>
                          {#if typeof val === 'object' && val !== null}
                            {#if Array.isArray(val)}
                              <div class="flex flex-wrap gap-2">
                                {#each val as item}
                                  {#if item.species}
                                    <div class="group relative w-10 h-10 flex items-center justify-center bg-white rounded-full shadow-sm border border-gray-200 hover:border-purple-500 hover:scale-110 transition-all cursor-help">
                                                                            <span class="text-xl" role="img" aria-label={item.species}>
                                                                                {speciesIcons[item.species] || '🐾'}
                                                                            </span>

                                      <div class="absolute bottom-full mb-2 hidden group-hover:block z-50 w-40 p-2 bg-gray-900 text-white text-xs rounded-lg shadow-xl">
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
                                    <span class="px-2 py-1 bg-gray-100 rounded">{JSON.stringify(item)}</span>
                                  {/if}
                                {/each}
                              </div>
                            {:else}
                              <span class="text-gray-700">{JSON.stringify(val)}</span>
                            {/if}
                          {:else}
                            <span class="text-gray-700">{val}</span>
                          {/if}
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