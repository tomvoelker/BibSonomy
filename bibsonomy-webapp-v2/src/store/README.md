# Store (Pinia)

Global client-side state management using Pinia.

## Purpose

Stores manage **client/UI state** that needs to be shared across components:

- Authentication tokens and user session
- User preferences (language, theme)
- Global UI state (sidebar open/closed, notifications)

**Note**: Do NOT use stores for server/API state - use @tanstack/vue-query composables instead.

## Structure

- `auth.ts` - Authentication state and actions
- `preferences.ts` - User preferences (language, theme, etc.)
- `ui.ts` - Global UI state

## Guidelines

- Use setup stores (composition API style)
- Keep stores focused and small
- Persist important state to localStorage when needed
- Use TypeScript for full type safety
