// src/lib/types.ts
export interface Riddle {
    id: string;
    description: string;
    input: any[];
    expectedOutput: any[];
}

export interface SafariAnimel {
     name: string;
    height:number;
     weight: number;
}