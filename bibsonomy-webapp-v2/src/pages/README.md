# Pages

Top-level page components corresponding to routes.

## Structure

Each page component represents a distinct view/route in the application.

## Guidelines

- One page per route
- Use Composition API with `<script setup>`
- Pages orchestrate components and handle route-level logic
- Use composables for data fetching (e.g., `usePosts()`)
- Handle loading, error, and empty states

## Naming Convention

- `HomePage.vue` - Home/dashboard page
- `PostDetailPage.vue` - Post detail view
- `UserProfilePage.vue` - User profile view
- `SearchPage.vue` - Search results page
