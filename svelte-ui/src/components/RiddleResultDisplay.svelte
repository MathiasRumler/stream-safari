<script lang="ts">
  import type { RiddleResult } from '$lib/types';

  let { result }: { result: RiddleResult } = $props();

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
</script>

<div class="mt-6 p-5 rounded-lg {result.success ? 'bg-green-50 border-green-500' : 'bg-red-50 border-red-500'} border-2">
  <div class="flex items-start gap-3">
    <div class="flex-1">
      <p class="font-bold mb-2 {result.success ? 'text-green-800' : 'text-red-800'}">
        {result.message}
      </p>

      {#if result.exception}
        <p class="text-xs text-red-700 font-mono bg-red-100 p-2 rounded mb-3">Exception: {result.exception}</p>
      {/if}

      {#if result.result?.value !== undefined}
        <div class="bg-white bg-opacity-60 p-3 rounded border border-gray-200">
          <p class="text-[10px] uppercase font-bold text-gray-500 mb-2">Output ({result.result.type}):</p>

          {#if result.result.type === 'NUMBER'}
            <span class="text-2xl font-mono font-bold text-blue-700">{result.result.value}</span>
          {:else if result.result.type === 'LIST' || result.result.type === 'SAFARIANIMAL'}
            <div class="flex flex-wrap gap-4">
              {#each (Array.isArray(result.result.value) ? result.result.value : [result.result.value]) as animal}
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
          {:else if result.result.type === 'MAP'}
            <div class="space-y-3">
              {#each Object.entries(result.result.value) as [key, val]}
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
            <code class="text-sm break-all">{JSON.stringify(result.result.value)}</code>
          {/if}
        </div>
      {/if}
    </div>
  </div>
</div>