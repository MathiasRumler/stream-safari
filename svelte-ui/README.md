# Streamy - Interactive Java Streams Learning Platform

Streamy is an interactive web application designed to help developers master Java Streams through hands-on riddles and guided story-based learning.

## 🚀 Key Features

### 📖 Story Mode
A guided learning path that introduces Java Stream concepts progressively.
- **Interactive Slides**: Learn concepts like filtering, mapping, and reducing through bite-sized lessons.
- **Challenge Integration**: Apply what you've learned immediately by solving a relevant riddle.
- **Progress Saving**: Your progress is automatically saved to your browser's local storage, so you can pick up where you left off.

### 🧩 Practice Mode
Access a library of standalone riddles to test your skills on specific topics.
- **Diverse Challenges**: From basic sorting to complex grouping and collecting.
- **Instant Feedback**: Get immediate validation of your stream pipeline.

### 💡 Intelligent Code Editor
The core `RiddleChallenge` component provides a rich editing experience:
- **Autocomplete**: Context-aware suggestions for Stream methods (`.filter()`, `.map()`), static methods (`Comparator.comparing()`), and method references (`SafariAnimal::getWeight`).
- **Visual Feedback**: See the input data (Safari Animals) and your output visualized with emojis and structured data.
- **Class Reference**: Built-in documentation for the `SafariAnimal` class, accessible via an info icon.

## 📂 Project Structure

```
src/
├── routes/
│   ├── +layout.svelte          # Main app wrapper (styles, navigation)
│   ├── +page.svelte            # Homepage
│   ├── story/
│   │   └── +page.svelte        # Story Mode container (manages levels & progress)
│   ├── practice/
│   │   ├── +page.svelte        # List of all practice riddles
│   │   └── [id]/
│   │       └── +page.svelte    # Individual practice riddle page
│   └── options/
│       └── +page.svelte        # Settings or options page
├── components/
│   ├── Riddlechallenge.svelte  # Core component for solving riddles
│   ├── RiddleResultDisplay.svelte # Component to render riddle results
│   └── StoryLevel.svelte       # Component for story slides and transitions
├── lib/
│   ├── storyData.ts            # Content for story levels (slides & riddle IDs)
│   ├── types.ts                # TypeScript interfaces (Riddle, SafariAnimal, etc.)
│   └── utils/
│       └── autocomplete.ts     # Logic for the code editor's autocomplete
└── static/
    ├── auto_complete.json      # Data for autocomplete suggestions
    └── class_definitions.json  # Java class definitions for reference
```

## 🛠️ Tech Stack

- **Frontend**: SvelteKit (Svelte 5 Runes), TypeScript
- **Styling**: Tailwind CSS
- **Backend**: Java Spring Boot (provides the riddle evaluation API)

## 🚦 Getting Started

1.  Ensure the Java backend is running on `http://localhost:8080`.
2.  Install dependencies:
    ```bash
    npm install
    ```
3.  Start the development server:
    ```bash
    npm run dev
    ```
4.  Open your browser and navigate to `http://localhost:5173`.

## 📝 How to Contribute

- **Add New Riddles**: Add new entries to the Java backend's `RiddleRepository`.
- **Add Story Levels**: Update `src/lib/storyData.ts` to create new chapters.
- **Improve Autocomplete**: Update `static/auto_complete.json` with new methods.
