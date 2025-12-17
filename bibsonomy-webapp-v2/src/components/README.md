# Components

Vue components organized by type and feature.

## Structure

- `ui/` - Reusable UI primitives (wrappers around Headless UI)
- Feature-specific components at root level (e.g., `PostCard.vue`, `PostList.vue`)

## Guidelines

- Use Composition API with `<script setup>` syntax
- Props should be typed with TypeScript interfaces
- Use semantic HTML and ARIA attributes for accessibility
- Component names should be PascalCase
- Emit events using camelCase names
