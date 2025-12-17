# RegisterPage

## Route

`/register`

## Access Control

- [x] Public
- [ ] Requires Authentication
- [ ] Requires Admin

Redirect to home if already authenticated.

## Page Purpose

Create a new user account. Collects required information (username, email, password) and optional profile details.

## Layout Structure

- **Header**: Minimal navigation (logo, language selector)
- **Main Content** (centered):
  - Registration form card
  - Steps indicator (if multi-step)
  - "Already have an account?" link
- **Footer**: Links, about

## Components Used

- `MinimalHeader`
- `RegistrationForm`
- `PasswordStrengthIndicator`
- `UsernameAvailabilityCheck`
- `EmailAvailabilityCheck`
- `TermsCheckbox`
- `AppFooter`

## API Calls

```typescript
// Check username availability (debounced)
GET /api/v2/users?search={username}
// If results.length > 0, username taken

// Register new user
POST /api/v2/users
{
  "username": "jsmith",
  "email": "john.smith@example.com",
  "password": "SecurePass123!",
  "realName": "John Smith"
}
Response: {
  "username": "jsmith",
  "realName": "John Smith",
  "email": "john.smith@example.com"
}
```

## State Management

- **Pinia**:
  - `authStore`:
    - `register(userData)` action
    - Optionally auto-login after registration
- **Component State**:
  - `formData: RegistrationFormData`
    - `username: string`
    - `email: string`
    - `password: string`
    - `confirmPassword: string`
    - `realName: string` (optional)
    - `agreeToTerms: boolean`
  - `errors: ValidationErrors`
  - `usernameAvailable: boolean | null`
  - `isRegistering: boolean`

## User Interactions

1. User types username → Real-time validation → Checks availability (debounced 500ms) → Shows "✓ Available" or "✗ Taken"
2. User types email → Real-time validation (format) → Checks availability
3. User types password → Real-time validation → Shows strength indicator (weak/medium/strong)
4. User types confirm password → Validates match with password
5. User types real name (optional) → No validation
6. User checks "I agree to Terms" → Required to submit
7. User clicks "Register" → Validates form → Calls POST → On success:
   - Shows success message
   - Optionally auto-login
   - Redirects to home or onboarding
8. User clicks "Show password" → Toggles password visibility
9. User clicks "Already have an account?" → Navigates to `/login`

## URL Parameters & Query Strings

- `returnUrl`: URL to redirect to after successful registration (default: `/`)

Example: `/register?returnUrl=/posts/new`

## Page States

- **Loading state**: N/A (initial render is fast)
- **Empty state**: N/A (form always rendered)
- **Error state - Username taken**: "This username is already taken"
- **Error state - Email taken**: "This email is already registered"
- **Error state - Validation failed**: Inline error messages per field
- **Error state - Registration failed**: "Registration failed. Please try again."
- **Registering state**: Register button shows spinner, form disabled
- **Success state**: "Account created successfully! Redirecting..."

## Responsive Behavior

- **Desktop (>1024px)**: Centered card (500px width)
- **Tablet (768-1024px)**: Centered card
- **Mobile (<768px)**: Full-width card with padding

## Accessibility Requirements

- Page title: "Register - BibSonomy"
- Form fields with associated `<label>` elements
- Required fields marked with asterisk and `aria-required="true"`
- Error messages linked to fields via `aria-describedby`
- Password strength indicator with text description (not just color)
- Username availability announced to screen readers
- Register button disabled when form is invalid or submitting
- Focus management (username field on load, error message on error)
- Terms checkbox with linked terms page

## i18n Keys

```
page.register.title
page.register.username.label
page.register.username.placeholder
page.register.username.available
page.register.username.taken
page.register.username.checking
page.register.email.label
page.register.email.placeholder
page.register.email.taken
page.register.password.label
page.register.password.placeholder
page.register.password.show
page.register.password.hide
page.register.password.strength.weak
page.register.password.strength.medium
page.register.password.strength.strong
page.register.confirmPassword.label
page.register.confirmPassword.placeholder
page.register.confirmPassword.mismatch
page.register.realName.label
page.register.realName.placeholder
page.register.realName.optional
page.register.agreeToTerms
page.register.termsLink
page.register.button
page.register.registering
page.register.alreadyHaveAccount
page.register.loginLink
page.register.error.usernameTaken
page.register.error.emailTaken
page.register.error.invalidEmail
page.register.error.weakPassword
page.register.error.required
page.register.error.registrationFailed
page.register.success
```

## Mockup Notes

- Clean, minimal design focused on registration form
- Registration form card with subtle shadow
- Form fields with icons (user, email, lock icons)
- Username field with availability indicator (checkmark or X icon)
- Password field with show/hide toggle
- Password strength indicator:
  - Progress bar (red → yellow → green)
  - Text label (Weak / Medium / Strong)
- Confirm password field with match indicator
- Real name field marked as "(optional)"
- Terms checkbox with linked "Terms of Service" text
- Register button as primary CTA (full width)
- "Already have an account? Login" link centered below form
- Loading spinner on register button when submitting
- Inline error messages in red below fields
- Real-time validation feedback (debounced)
- Success message with redirect countdown (optional)
- Smooth transitions for error messages and indicators
