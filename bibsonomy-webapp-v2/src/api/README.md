# API

API client code for communicating with the backend REST API.

## Structure

- `client.ts` - Axios client instance with configuration
- `posts.ts` - Posts API endpoints
- `users.ts` - Users API endpoints
- `tags.ts` - Tags API endpoints
- `auth.ts` - Authentication endpoints

## Guidelines

- Use Axios for HTTP requests
- Validate responses with Zod schemas
- Handle errors consistently
- Export typed functions (not raw Axios calls)
- Support both real API and MSW mocks via environment variable

## Example

```typescript
import { axios } from './client'
import { PostListSchema } from '@/types/schemas'

export async function getPosts(params: GetPostsParams) {
  const response = await axios.get('/api/v2/posts', { params })
  return PostListSchema.parse(response.data)
}
```
