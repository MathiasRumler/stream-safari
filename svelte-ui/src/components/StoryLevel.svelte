<script lang="ts">
  import type { StoryLevel, Riddle } from '$lib/types';
  import Riddlechallenge from './Riddlechallenge.svelte';

  let { level, onLevelComplete }: { level: StoryLevel, onLevelComplete: () => void } = $props();

  let currentSlideIndex = $state(0);
  let showRiddle = $state(false);
  let riddle = $state<Riddle | null>(null);
  let loadingRiddle = $state(false);
  let error = $state<string | null>(null);

  // Reset state when level changes
  $effect(() => {
    currentSlideIndex = 0;
    showRiddle = false;
    riddle = null;
    error = null;
  });

  async function fetchRiddle(id: string) {
    loadingRiddle = true;
    try {
      const response = await fetch('http://localhost:8080/api/stream/riddles');
      if (!response.ok) throw new Error('Failed to fetch riddles');
      const riddles: Riddle[] = await response.json();
      riddle = riddles.find(r => r.id === id) || null;
      if (!riddle) throw new Error('Riddle not found');
    } catch (err) {
      error = err instanceof Error ? err.message : 'Unknown error';
    } finally {
      loadingRiddle = false;
    }
  }

  function nextSlide() {
    if (currentSlideIndex < level.slides.length - 1) {
      currentSlideIndex++;
    } else {
      showRiddle = true;
      fetchRiddle(level.riddleId);
    }
  }

  function prevSlide() {
    if (currentSlideIndex > 0) {
      currentSlideIndex--;
    }
  }

  function handleRiddleSuccess() {
    // You might want to show a success message or animation here before moving on
    setTimeout(() => {
      onLevelComplete();
    }, 1500);
  }
</script>

<div class="max-w-4xl mx-auto">
  {#if !showRiddle}
    <!-- Slides View -->
    <div class="bg-white rounded-xl shadow-lg overflow-hidden min-h-[400px] flex flex-col">
      <!-- Header -->
      <div class="bg-blue-600 p-6 text-white">
        <div class="flex justify-between items-center mb-2">
          <span class="text-sm font-medium opacity-80">Level {level.id}</span>
          <span class="text-sm font-medium opacity-80">Slide {currentSlideIndex + 1} of {level.slides.length}</span>
        </div>
        <h2 class="text-2xl font-bold">{level.slides[currentSlideIndex].title}</h2>
      </div>

      <!-- Content -->
      <div class="p-8 flex-1">
        <p class="text-lg text-gray-700 mb-6 leading-relaxed">
          {level.slides[currentSlideIndex].content}
        </p>

        {#if level.slides[currentSlideIndex].codeSnippet}
          <div class="bg-gray-900 rounded-lg p-4 overflow-x-auto">
            <pre class="text-sm font-mono text-green-400"><code>{level.slides[currentSlideIndex].codeSnippet}</code></pre>
          </div>
        {/if}
      </div>

      <!-- Footer / Navigation -->
      <div class="bg-gray-50 p-6 border-t border-gray-100 flex justify-between items-center">
        <button
            onclick={prevSlide}
            disabled={currentSlideIndex === 0}
            class="px-4 py-2 text-gray-600 hover:text-blue-600 disabled:opacity-30 disabled:cursor-not-allowed font-medium transition-colors"
        >
          ← Previous
        </button>

        <div class="flex gap-2">
          {#each level.slides as _, i}
            <div class="w-2 h-2 rounded-full transition-colors {i === currentSlideIndex ? 'bg-blue-600' : 'bg-gray-300'}"></div>
          {/each}
        </div>

        <button
            onclick={nextSlide}
            class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-medium shadow-sm transition-all hover:shadow-md flex items-center gap-2"
        >
          {currentSlideIndex === level.slides.length - 1 ? 'Start Challenge' : 'Next'}
          {#if currentSlideIndex === level.slides.length - 1}
            🚀
          {:else}
            →
          {/if}
        </button>
      </div>
    </div>

  {:else}
    <!-- Riddle View -->
    <div class="animate-fade-in">
      <div class="mb-6 flex items-center justify-between">
        <button
            onclick={() => showRiddle = false}
            class="text-sm text-gray-500 hover:text-blue-600 flex items-center gap-1"
        >
          ← Back to slides
        </button>
        <span class="px-3 py-1 bg-blue-100 text-blue-800 text-xs font-bold rounded-full uppercase tracking-wide">
          Challenge Mode
        </span>
      </div>

      {#if loadingRiddle}
        <div class="flex flex-col items-center justify-center py-20">
          <div class="w-10 h-10 border-4 border-blue-200 border-t-blue-600 rounded-full animate-spin mb-4"></div>
          <p class="text-gray-500">Loading your challenge...</p>
        </div>
      {:else if error}
        <div class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded relative" role="alert">
          <strong class="font-bold">Error!</strong>
          <span class="block sm:inline"> {error}</span>
        </div>
      {:else if riddle}
        <!-- We need to modify Riddlechallenge to accept an onSuccess callback -->
        <!-- For now, we can assume success if the result is correct, but Riddlechallenge handles its own state -->
        <!-- Ideally, Riddlechallenge should emit an event on success -->
        <Riddlechallenge bind:riddle isStory={true} />

        <!-- Temporary "Next Level" button for simulation until we wire up the success callback properly -->
        <!-- In a real implementation, Riddlechallenge would expose an event or prop for success -->
        <div class="mt-8 flex justify-end">
           <button
              onclick={onLevelComplete}
              class="px-6 py-3 bg-green-600 text-white font-bold rounded-lg shadow-lg hover:bg-green-700 transition-all transform hover:scale-105 flex items-center gap-2"
           >
             Next Level →
           </button>
        </div>
      {/if}
    </div>
  {/if}
</div>
