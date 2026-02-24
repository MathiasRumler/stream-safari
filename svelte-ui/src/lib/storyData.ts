import type { StoryLevel } from './types';

export const storyLevels: StoryLevel[] = [
  {
    id: 'level-1',
    title: 'Sorting the Herd',
    description: 'Learn how to order elements in a stream.',
    riddleId: '1',
    slides: [
      {
        title: 'Welcome to the Safari Stream!',
        content: 'In this adventure, you will learn how to manage a wildlife reserve using Java Streams. Your first task is to organize the animals.',
      },
      {
        title: 'Sorting with Streams',
        content: 'The `.sorted()` method allows you to sort elements in a stream. By default, it sorts in natural order, but for objects like Animals, you need a `Comparator`.',
        codeSnippet: 'List<String> names = Arrays.asList("Charlie", "Alice", "Bob");\nnames.stream()\n  .sorted()\n  .collect(Collectors.toList()); // [Alice, Bob, Charlie]'
      },
      {
        title: 'Using Comparators',
        content: 'To sort objects by a specific field, use `Comparator.comparing()`.',
        codeSnippet: 'animals.stream()\n  .sorted(Comparator.comparing(Animal::getAge))\n  .collect(Collectors.toList());'
      }
    ]
  },
  {
    id: 'level-2',
    title: 'Seeing Double',
    description: 'Learn how to remove duplicate elements.',
    riddleId: '2',
    slides: [
      {
        title: 'Duplicate Data',
        content: 'Sometimes our sensors malfunction and record the same animal twice. We need to clean up our data.',
      },
      {
        title: 'The distinct() Operation',
        content: 'The `.distinct()` method returns a stream consisting of the distinct elements (according to Object.equals(Object)) of this stream.',
        codeSnippet: 'List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 3, 3);\nnumbers.stream()\n  .distinct()\n  .collect(Collectors.toList()); // [1, 2, 3]'
      }
    ]
  },
  {
    id: 'level-3',
    title: 'Predator Patrol',
    description: 'Learn how to filter elements based on conditions.',
    riddleId: '6',
    slides: [
      {
        title: 'Safety First',
        content: 'To keep our visitors safe, we need to identify all the dangerous animals in the park.',
      },
      {
        title: 'Filtering Streams',
        content: 'The `.filter()` method takes a `Predicate` (a function that returns true or false) and keeps only the elements that match.',
        codeSnippet: 'animals.stream()\n  .filter(animal -> animal.isPredator())\n  .collect(Collectors.toList());'
      },
      {
        title: 'Method References',
        content: 'You can make your code cleaner using method references.',
        codeSnippet: 'animals.stream()\n  .filter(SafariAnimal::isPredator)\n  .collect(Collectors.toList());'
      }
    ]
  },
  {
    id: 'level-4',
    title: 'Heavyweights',
    description: 'Find the maximum value in a stream.',
    riddleId: '4',
    slides: [
      {
        title: 'Finding Extremes',
        content: 'We need to find the heaviest animal to ensure our transport trucks can handle the load.',
      },
      {
        title: 'Max and Min',
        content: 'The `.max()` and `.min()` methods return an `Optional` describing the maximum or minimum element of this stream according to a `Comparator`.',
        codeSnippet: 'animals.stream()\n  .max(Comparator.comparing(SafariAnimal::getWeight))\n  .orElseThrow();'
      }
    ]
  },
  {
    id: 'level-5',
    title: 'Total Mass',
    description: 'Calculate the sum of a numeric property.',
    riddleId: '5',
    slides: [
      {
        title: 'Aggregating Data',
        content: 'Now we need to know the total weight of all animals combined to calculate food requirements.',
      },
      {
        title: 'Mapping to Primitives',
        content: 'Streams of objects can be converted to primitive streams (IntStream, DoubleStream, LongStream) using methods like `.mapToDouble()`.',
        codeSnippet: 'double totalWeight = animals.stream()\n  .mapToDouble(SafariAnimal::getWeight)\n  .sum();'
      },
      {
        title: 'The sum() Operation',
        content: 'Primitive streams have special utility methods like `.sum()`, `.average()`, and `.summaryStatistics()`.',
      }
    ]
  },
  {
    id: 'level-6',
    title: 'Species Champions',
    description: 'Group data and find the best in each group.',
    riddleId: '7',
    slides: [
      {
        title: 'Grouping Data',
        content: 'We want to find the heaviest animal for *each* species, not just the heaviest overall.',
      },
      {
        title: 'Collectors.groupingBy',
        content: 'The `.collect(Collectors.groupingBy(...))` method allows you to group elements by a classifier function. The result is a Map.',
        codeSnippet: 'Map<Species, List<Animal>> bySpecies = animals.stream()\n  .collect(Collectors.groupingBy(SafariAnimal::getSpecies));'
      },
      {
        title: 'Downstream Collectors',
        content: 'You can pass a second collector to `groupingBy` to process the values in each group. `Collectors.collectingAndThen` or `Collectors.maxBy` are useful here.',
        codeSnippet: 'animals.stream()\n  .collect(Collectors.groupingBy(\n    SafariAnimal::getSpecies,\n    Collectors.collectingAndThen(\n      Collectors.maxBy(Comparator.comparing(SafariAnimal::getWeight)),\n      Optional::get\n    )\n  ));'
      }
    ]
  },
  {
    id: 'level-7',
    title: 'Weight Classes',
    description: 'Advanced grouping by custom ranges.',
    riddleId: '8',
    slides: [
      {
        title: 'Custom Categorization',
        content: 'Finally, we need to categorize animals into weight classes: Light (0-200), Medium (200-500), Heavy (500-1000), and Super Heavy (1000+).',
      },
      {
        title: 'Complex Grouping',
        content: 'The classifier function in `groupingBy` can be any function that returns a key. You can write a lambda that returns a String based on the weight.',
        codeSnippet: 'animals.stream()\n  .collect(Collectors.groupingBy(animal -> {\n    double w = animal.getWeight();\n    if (w < 200) return "0-200";\n    else if (w < 500) return "200-500";\n    // ...\n    else return "1000+";\n  }));'
      }
    ]
  }
];
