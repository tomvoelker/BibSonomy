# Mocks

Mock Service Worker (MSW) handlers for API mocking during development and testing.

## Purpose

MSW intercepts HTTP requests and returns mock responses, allowing frontend development to proceed independently of backend readiness.

## Structure

- `browser.ts` - MSW browser worker setup
- `server.ts` - MSW server setup for Node/Vitest
- `handlers/` - Request handlers organized by resource
  - `posts.ts` - Post endpoints
  - `users.ts` - User endpoints
  - `tags.ts` - Tag endpoints
- `data/` - Mock data fixtures

## Guidelines

- Match the OpenAPI specification exactly
- Use realistic mock data
- Include error scenarios (404, 401, 500)
- Support pagination and filtering
- Enable/disable via environment variable

## Usage

Mocking is controlled by `VITE_ENABLE_MOCKS` environment variable.
