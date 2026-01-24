// src/routes/riddle/[id]/+page.ts
export function load({ params }) {
    return {
        id: params.id
    };
}