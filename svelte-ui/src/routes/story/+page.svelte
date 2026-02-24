<script lang="ts">
  import { storyLevels } from '$lib/storyData';
  import StoryLevel from '../../components/StoryLevel.svelte';

  let currentLevelIndex = $state(0);
  let currentLevel = $derived(storyLevels[currentLevelIndex]);
  let completed = $state(false);

  function handleLevelComplete() {
    if (currentLevelIndex < storyLevels.length - 1) {
      currentLevelIndex++;
    } else {
      completed = true;
    }
  }
</script>

<div class="container mx-auto px-4 py-8">
  <div class="mb-8 flex items-center justify-between">
    <button onclick={() => window.history.back()} class="inline-flex items-center text-blue-600 hover:text-blue-800">
      <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
      </svg>
      Back to Home
    </button>

    {#if !completed}
      <div class="text-sm font-medium text-gray-500">
        Level {currentLevelIndex + 1} of {storyLevels.length}
      </div>
    {/if}
  </div>

  {#if completed}
    <div class="max-w-2xl mx-auto text-center py-20">
      <div class="text-6xl mb-6">🎉</div>
      <h1 class="text-4xl font-bold text-gray-800 mb-4">Congratulations!</h1>
      <p class="text-xl text-gray-600 mb-8">You have completed all the story levels and mastered the basics of Java Streams.</p>
      <div class="flex justify-center gap-4">
        <a href="/practice" class="px-6 py-3 bg-blue-600 text-white font-bold rounded-lg hover:bg-blue-700 transition-colors">
          Practice More Riddles
        </a>
        <button onclick={() => { currentLevelIndex = 0; completed = false; }} class="px-6 py-3 bg-gray-200 text-gray-700 font-bold rounded-lg hover:bg-gray-300 transition-colors">
          Restart Story
        </button>
      </div>
    </div>
  {:else}
    <StoryLevel level={currentLevel} onLevelComplete={handleLevelComplete} />
  {/if}
</div>
