// src/routes/riddle/[id]/+page.ts
export const prerender = false;

export function load({ params }) {
    return {
        id: params.id
    };
}