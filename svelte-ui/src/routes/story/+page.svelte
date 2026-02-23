<script lang="ts">
  import type { Riddle } from '$lib/types';
  import Riddlechallenge from "../../components/Riddlechallenge.svelte";

  let { data } = $props();
  let riddle = $state<Riddle | null>(null);
  let loading = $state(true);
  let error = $state<string | null>(null);

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

      fetchRiddle('5');

  });

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
  {:else}
    <Riddlechallenge bind:riddle isStory={false} />
  {/if}
</div>