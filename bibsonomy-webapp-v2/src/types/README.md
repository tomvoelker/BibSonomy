# Types

TypeScript type definitions and Zod schemas for runtime validation.

## Structure

- `models.ts` - Core domain types (Post, User, Tag, etc.)
- `schemas.ts` - Zod schemas for API response validation
- `api.ts` - API request/response types
- `*.d.ts` - Ambient type declarations

## Guidelines

- Use interfaces for object shapes
- Use type aliases for unions, intersections, and utility types
- Create Zod schemas for all API responses
- Export both TypeScript types AND Zod schemas
- Use `z.infer<typeof Schema>` to derive types from schemas

## Example

```typescript
import { z } from 'zod'

// Zod schema for runtime validation
export const PostSchema = z.object({
  id: z.string(),
  title: z.string(),
  url: z.string().url().optional(),
  // ...
})

// Derive TypeScript type from schema
export type Post = z.infer<typeof PostSchema>
```
