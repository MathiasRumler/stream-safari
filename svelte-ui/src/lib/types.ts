// src/lib/types.ts
export interface SafariAnimal {
    name: string;
    height: number;
    weight: number;
}

export interface Riddle {
    id: string;
    description: string;
    dataType: string;
    input: string[] | SafariAnimal[];
    expectedOutput: string[] | SafariAnimal[];
}
