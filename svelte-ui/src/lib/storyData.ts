import type { StoryLevel } from './types';

export const storyLevels: StoryLevel[] = [
  {
    id: 'level-1',
    title: 'Introduction to Streams',
    description: 'Learn the basics of Java Streams and how to filter data.',
    riddleId: '1', // Assuming riddle 1 is about filtering or basic stream usage
    slides: [
      {
        title: 'What is a Stream?',
        content: 'A Stream in Java is a sequence of elements supporting sequential and parallel aggregate operations. It allows you to process collections of objects in a functional style.',
        codeSnippet: 'List<String> names = Arrays.asList("Alice", "Bob", "Charlie");\nnames.stream().forEach(System.out::println);'
      },
      {
        title: 'Filtering Data',
        content: 'The .filter() method allows you to exclude elements from the stream based on a condition (Predicate).',
        codeSnippet: 'names.stream()\n  .filter(name -> name.startsWith("A"))\n  .collect(Collectors.toList());'
      }
    ]
  },
  {
    id: 'level-2',
    title: 'Mapping Data',
    description: 'Transform data using the map operation.',
    riddleId: '2',
    slides: [
      {
        title: 'Transforming Elements',
        content: 'The .map() method transforms each element in the stream into another object using a Function.',
        codeSnippet: 'names.stream()\n  .map(String::toUpperCase)\n  .collect(Collectors.toList());'
      }
    ]
  }
];
