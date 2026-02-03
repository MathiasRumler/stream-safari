// src/lib/types.ts
export interface SafariAnimal {
    name: string;
  species: AnimalSpecies; // Updated from string
   animalClass:string;
  age:number;
    weight: number;
    predator: boolean;
}

export interface Riddle {
    id: string;
    description: string;
    dataType: string;
    input:  SafariAnimal[];
    expectedOutput: any;
}
export type AnimalSpecies = 'LION' | 'ELEPHANT' | 'GIRAFFE' | 'ZEBRA' | 'HYENA' | 'CROCODILE' | 'SNAKE' | 'LIZARD';


// src/lib/types.ts
export type ResultType = 'NUMBER' | 'LIST' | 'MAP' | 'SAFARIANIMAL';

export interface RiddleResult {
  success: boolean;
  result: {
    type: ResultType;
    value: any; // This will hold the actual data
  };
  message: string;
  exception?: string;
}