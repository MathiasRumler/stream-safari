<script lang="ts">
    import type {Riddle, SafariAnimal} from '$lib/types';

    let riddles = $state<Riddle[]>([]);
    let loading = $state(true);
    let error = $state<string | null>(null);

    async function fetchRiddles() {
        try {
            const response = await fetch('http://localhost:8080/api/stream/riddles');
            if (!response.ok) throw new Error('Failed to fetch riddles');
            riddles = await response.json();
            console.log(riddles)
        } catch (err) {
            error = err instanceof Error ? err.message : 'Unknown error';
        } finally {
            loading = false;
        }
    }

    // Fetch on mount
    $effect(() => {
        fetchRiddles();
    });

    function isAnimal(value: any): value is SafariAnimal {
        return value && typeof value === 'object' && 'name' in value;
    }
</script>

<div class="container mx-auto px-4 py-8">
    <h1 class="text-4xl font-bold text-center mb-8">Riddle Challenge</h1>

    {#if loading}
        <div class="text-center text-gray-600">Loading riddles...</div>
    {:else if error}
        <div class="text-center text-red-500">Error: {error}</div>
    {:else}
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {#each riddles as riddle}
                <a
                        href="/riddle/{riddle.id}"
                        class="block p-6 bg-white border-2 border-gray-200 rounded-lg shadow hover:shadow-lg hover:border-blue-500 transition-all duration-200"
                >

                    <div class="flex items-center justify-bet‚ween mb-3">
                        <span class="text-sm font-semibold text-blue-600">Riddle #{riddle.id}</span>
                        <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
                        </svg>
                    </div>
                    <h2 class="text-xl font-semibold mb-3 text-gray-800">{riddle.description}</h2>
                    {#if riddle.dataType === 'mvp.streamy.models.SafariAnimal'}
                        {#each riddle.input as animal}
                            <p>{animal.name}</p>
                        {/each}

                    {:else}
                        <div class="text-sm text-gray-600">
                            <p class="mb-1">Input: [{riddle.input.join(', ')}]</p>
                            <p>Output: [{riddle.expectedOutput.join(', ')}]</p>
                        </div>
                    {/if}

                </a>
            {/each}
        </div>
    {/if}
</div>