# Composables

Reusable composition functions for Vue components.

## Purpose

Composables encapsulate and reuse stateful logic across components following Vue 3 Composition API patterns.

## Categories

- **API/Data Fetching** - Vue Query composables (e.g., `usePosts`, `useUser`)
- **UI State** - Local UI state management (e.g., `useModal`, `useToast`)
- **Utilities** - Helper functions (e.g., `useDebounce`, `useLocalStorage`)
- **Business Logic** - Domain-specific logic (e.g., `usePostFilters`)

## Guidelines

- Prefix with `use`
- Return reactive state and methods
- Use TypeScript for parameter and return types
- Use @tanstack/vue-query for server state
- Use VueUse composables when applicable
