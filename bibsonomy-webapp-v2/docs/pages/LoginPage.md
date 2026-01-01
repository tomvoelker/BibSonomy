# LoginPage

## Route

`/login`

## Access Control

- [x] Public
- [ ] Requires Authentication
- [ ] Requires Admin

Redirect to home if already authenticated.

## Page Purpose

Authenticate existing users via username/password, OAuth, or OpenID Connect. Gateway to authenticated features.

## Layout Structure

- **Header**: Minimal navigation (logo, language selector)
- **Main Content** (centered):
  - Login form card
  - OAuth provider buttons
  - "Forgot password?" link
  - "Create account" link
- **Footer**: Links, about

## Components Used

- `MinimalHeader`
- `LoginForm`
- `OAuthButtonGroup`
- `OAuthButton`
- `ForgotPasswordLink`
- `CreateAccountLink`
- `AppFooter`

## API Calls

```typescript
// Username/Password login
POST /api/v2/auth/token
Content-Type: application/x-www-form-urlencoded
{
  grant_type: "password",
  username: "jsmith",
  password: "******"
}
Response: {
  access_token: "eyJ...",
  token_type: "Bearer",
  expires_in: 3600,
  refresh_token: "abc...",
  scope: "read write"
}

// OAuth login (redirect flow)
// Client redirects to: /api/v2/oauth/authorize?provider=google&redirect_uri=...
// After user approves, redirects back with code
// Client exchanges code:
POST /api/v2/auth/token
{
  grant_type: "authorization_code",
  code: "xyz123",
  redirect_uri: "..."
}
Response: { access_token, ... }
```

## State Management

- **Pinia**:
  - `authStore`:
    - `login(credentials)` action
    - `loginWithOAuth(provider)` action
    - Sets `user`, `accessToken`, `refreshToken`
    - Persists token to localStorage
- **Component State**:
  - `username: string`
  - `password: string`
  - `rememberMe: boolean`
  - `errors: ValidationErrors`
  - `isLoggingIn: boolean`

## User Interactions

1. User enters username → Types in field
2. User enters password → Types in field (masked)
3. User toggles "Remember me" → Checkbox
4. User clicks "Login" → Validates form → Calls POST → On success:
   - Saves token to authStore
   - Redirects to `returnUrl` (from query param) or home
5. User clicks "Show password" → Toggles password visibility
6. User clicks OAuth button (Google/GitHub/ORCID) → Redirects to OAuth provider → Redirects back → Exchanges code → Logs in
7. User clicks "Forgot password?" → Navigates to `/forgot-password`
8. User clicks "Create account" → Navigates to `/register`
9. User presses Enter in password field → Submits form

## URL Parameters & Query Strings

- `returnUrl`: URL to redirect to after successful login (default: `/`)
- `error`: OAuth error message (if OAuth fails)

Example: `/login?returnUrl=/posts/new`

## Page States

- **Loading state**: N/A (initial render is fast)
- **Empty state**: N/A (form always rendered)
- **Error state - Invalid credentials**: "Invalid username or password"
- **Error state - OAuth failed**: "Authentication failed. Please try again."
- **Error state - Network error**: "Network error. Please check your connection."
- **Logging in state**: Login button shows spinner, form disabled
- **Success state**: Redirect to returnUrl or home

## Responsive Behavior

- **Desktop (>1024px)**: Centered card (400px width), OAuth buttons inline
- **Tablet (768-1024px)**: Centered card, OAuth buttons inline
- **Mobile (<768px)**: Full-width card with padding, OAuth buttons stacked

## Accessibility Requirements

- Page title: "Login - BibSonomy"
- Form fields with associated `<label>` elements
- Error messages linked to fields via `aria-describedby`
- Password field with show/hide button
- Login button disabled when form is invalid or submitting
- Focus management (username field on load, error message on error)
- OAuth buttons clearly labeled with provider name

## i18n Keys

```
page.login.title
page.login.username.label
page.login.username.placeholder
page.login.password.label
page.login.password.placeholder
page.login.password.show
page.login.password.hide
page.login.rememberMe
page.login.button
page.login.loggingIn
page.login.forgotPassword
page.login.createAccount
page.login.orLoginWith
page.login.oauth.google
page.login.oauth.github
page.login.oauth.orcid
page.login.error.invalidCredentials
page.login.error.oauthFailed
page.login.error.networkError
page.login.error.required
page.login.success
```

## Mockup Notes

- Clean, minimal design focused on login form
- Login form card with subtle shadow
- Username and password fields with icons (user icon, lock icon)
- Password field with show/hide toggle (eye icon)
- "Remember me" checkbox
- Login button as primary CTA (full width)
- OAuth provider buttons with brand colors and logos:
  - Google (white button with Google logo)
  - GitHub (dark button with GitHub logo)
  - ORCID (green button with ORCID logo)
- "Or login with" divider between form and OAuth buttons
- "Forgot password?" link aligned right
- "Create account" link centered below form
- Loading spinner on login button when submitting
- Error messages in red below fields
- Success state redirects immediately (no intermediate UI)
- Smooth transitions for error messages
