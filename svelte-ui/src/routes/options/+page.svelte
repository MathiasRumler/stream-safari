<script>
  import { goto } from '$app/navigation';

  function goBack() {
    goto('/');
  }
</script>

<main class="wrapper">
  <div class="card">
    <h1 class="title">Options</h1>

    <div class="section">
      <label>
        <input type="checkbox" /> Enable Hard Mode
      </label>
    </div>

    <div class="section">
      <label>
        <input type="checkbox" /> Show Hints
      </label>
    </div>

    <button class="back-button" on:click={goBack}>
      Back
    </button>
  </div>
</main>

<style>
  .wrapper {
    height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .card {
    background: rgba(17, 24, 39, 0.9);
    padding: 2.5rem;
    border-radius: 20px;
    width: 400px;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4);
    color: #f9fafb;
  }

  .title {
    margin-bottom: 1.5rem;
    font-size: 1.8rem;
    font-weight: 700;
  }

  .section {
    margin-bottom: 1rem;
    font-size: 1rem;
  }

  .back-button {
    margin-top: 1.5rem;
    width: 100%;
    padding: 0.75rem;
    border-radius: 12px;
    border: none;
    background: #2563eb;
    color: white;
    font-weight: 600;
    cursor: pointer;
    transition: transform 0.2s ease;
  }

  .back-button:hover {
    transform: translateY(-2px);
  }
</style>
