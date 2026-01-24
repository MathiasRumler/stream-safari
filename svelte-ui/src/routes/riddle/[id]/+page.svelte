<script lang="ts">
    import type { Riddle } from '$lib/types';

    // Get the riddle ID from the URL
    let { data } = $props();

    let riddle = $state<Riddle | null>(null);
    let loading = $state(true);
    let error = $state<string | null>(null);
    let pipeline = $state('');
    let submitting = $state(false);
    let result = $state<{ success: boolean; output?: number[]; message: string; exception?: string } | null>(null);

    async function fetchRiddle(id: string) {
        try {
            // Fetch all riddles and find the one we need
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

    // Fetch on mount
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
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    riddleId: riddle.id,
                    pipeline: pipeline
                })
            });

            const data = await response.json();

            if (response.ok) {
                // Response is OK, but check if solution was correct
                result = {
                    success: data.success,
                    output: data.output,
                    message: data.message
                };
            } else {
                // HTTP error (syntax error, etc.)
                result = {
                    success: false,
                    message: data.message || 'Submission failed',
                    exception: data.exception
                };
            }
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

            <div class="space-y-4">
                <div class="bg-gray-50 p-4 rounded-lg">
                    <h2 class="text-lg font-semibold mb-2 text-gray-700">Input:</h2>
                    <code class="text-gray-800">[{riddle.input.join(', ')}]</code>
                </div>

                <div class="bg-green-50 p-4 rounded-lg">
                    <h2 class="text-lg font-semibold mb-2 text-gray-700">Expected Output:</h2>
                    <code class="text-gray-800">[{riddle.expectedOutput.join(', ')}]</code>
                </div>
            </div>

            <div class="mt-8">
                <h2 class="text-lg font-semibold mb-3 text-gray-700">Your Solution:</h2>
                <div class="flex gap-2">
                    <input
                            type="text"
                            bind:value={pipeline}
                            onkeypress={handleKeyPress}
                            placeholder=".stream().map(a->a*2).toList()"
                            class="flex-1 px-4 py-2 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-blue-500"
                            disabled={submitting}
                    />
                    <button
                            onclick={submitSolution}
                            disabled={submitting || !pipeline.trim()}
                            class="px-6 py-2 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
                    >
                        {submitting ? 'Submitting...' : 'Submit'}
                    </button>
                </div>
            </div>

            {#if result}
                <div class="mt-4 p-4 rounded-lg {result.success ? 'bg-green-100 border-2 border-green-500' : 'bg-red-100 border-2 border-red-500'}">
                    <div class="flex items-start gap-2">
                        {#if result.success}
                            <svg class="w-6 h-6 text-green-600 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
                            </svg>
                        {:else}
                            <svg class="w-6 h-6 text-red-600 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                            </svg>
                        {/if}
                        <div class="flex-1">
                            <p class="font-semibold {result.success ? 'text-green-800' : 'text-red-800'}">{result.message}</p>
                            {#if result.exception}
                                <p class="text-sm text-red-700 mt-1">Exception: {result.exception}</p>
                            {/if}
                            {#if result.output}
                                <p class="text-sm {result.success ? 'text-green-700' : 'text-red-700'} mt-1">
                                    Your output: [{result.output.join(', ')}]
                                </p>
                            {/if}
                            {#if !result.success && result.output && riddle}
                                <p class="text-sm text-red-700 mt-1">
                                    Expected: [{riddle.expectedOutput.join(', ')}]
                                </p>
                            {/if}
                        </div>
                    </div>
                </div>
            {/if}
        </div>
    {/if}
</div>