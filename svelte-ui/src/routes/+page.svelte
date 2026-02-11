<script>
  import { goto } from '$app/navigation';



  const modes = [
    { id: 'story', label: 'Story Mode', description: 'Follow structured challenges and unlock new riddles.', route: '/story' },
    { id: 'practice', label: 'Practice Arena', description: 'Experiment freely with streams and custom pipelines.', route: '/practice' },
    { id: 'options', label: 'Options', description: 'Adjust difficulty, theme, and preferences.', route: '/options' }
  ];

  function selectMode(route) {
    goto(route);
  }
</script>

<main class="wrapper">
  <div class="card">
    <h1 class="title">Stream Safari</h1>
    <div class="menu">
      {#each modes as mode}
        <button class="menu-button" on:click={() => selectMode(mode.route)}>
          <div class="button-title">{mode.label}</div>
          <div class="button-desc">{mode.description}</div>
        </button>
      {/each}
    </div>
  </div>
</main>

<style>
  .wrapper {
    height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    font-family: system-ui, -apple-system, sans-serif;
  }

  .card {
    background: #111827;
    padding: 3rem;
    border-radius: 20px;
    width: 420px;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4);
    text-align: center;
  }

  .title {
    margin-bottom: 2rem;
    font-size: 2rem;
    font-weight: 700;
    color: #f9fafb;
  }

  .menu {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .menu-button {
    background: #1f2937;
    border: 1px solid #374151;
    border-radius: 14px;
    padding: 1rem;
    text-align: left;
    cursor: pointer;
    transition: all 0.2s ease;
    color: #e5e7eb;
  }

  .menu-button:hover {
    background: #2563eb;
    border-color: #2563eb;
    transform: translateY(-2px);
  }

  .button-title {
    font-weight: 600;
    font-size: 1.1rem;
  }

  .button-desc {
    font-size: 0.85rem;
    opacity: 0.8;
    margin-top: 0.25rem;
  }
  :global(body) {
    background-image: url('../lib/assets/safari-bg.png');
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    background-attachment: fixed;
    margin: 0;
  }

</style>
