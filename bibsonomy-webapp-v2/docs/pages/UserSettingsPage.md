# UserSettingsPage

## Route

`/settings`

## Access Control

- [ ] Public
- [x] Requires Authentication
- [ ] Requires Admin

## Page Purpose

Manage user account settings, preferences, and API keys. Allows users to update profile information, change language, configure display preferences, manage API keys, and delete account.

**Context**: Comprehensive settings management. Must support:

- Profile customization (bio, avatar, contact info)
- Application preferences (language, items per page, theme)
- Privacy controls (profile visibility, default post visibility)
- Developer tools (API keys for integrations)
- Account management (password, deletion)

**Design References**:

- INFORMATION_ARCHITECTURE.md: Section 2.3.3 (User Settings Routes)
- DESIGN_SYSTEM.md: Section 7.5 (Patterns - Forms), Section 4 (Form Controls)
- COMPONENT_LIBRARY.md: Form components, SettingsNav

## Layout Structure

- **Header**: Global navigation
- **Settings Navigation** (left sidebar or top tabs on mobile):
  - Profile
  - Preferences
  - Privacy
  - API Keys
  - Account
- **Main Settings Area**:
  - Content changes based on active section
  - Each section has its own form
  - Save buttons per section

## Components Used

- `AppHeader`
- `SettingsNav`
- `ProfileSettingsForm`
- `PreferencesSettingsForm`
- `PrivacySettingsForm`
- `ApiKeysList`
- `ApiKeyCreateModal`
- `AccountSettingsForm`
- `DeleteAccountModal`
- `ConfirmationModal`

## API Calls

```typescript
// Load user profile
GET /api/v2/users/{username}

// Load user settings
GET /api/v2/users/{username}/settings

// Update profile
PUT /api/v2/users/{username}
{
  "realName": "...",
  "homepage": "...",
  "biography": "...",
  "institution": "..."
}

// Update settings
PUT /api/v2/users/{username}/settings
{
  "language": "en",
  "itemsPerPage": 20,
  "tagCloud": {
    "minFreq": 1,
    "maxCount": 50
  },
  "defaultGroups": ["public"]
}

// API Keys
GET /api/v2/users/{username}/api-keys

POST /api/v2/users/{username}/api-keys
{
  "name": "My API Key"
}

DELETE /api/v2/users/{username}/api-keys/{keyId}

// Delete account
DELETE /api/v2/users/{username}
```

## State Management

- **vue-query**:
  - `useQuery(['user', username], fetchUser)`
  - `useQuery(['user-settings', username], fetchUserSettings)`
  - `useQuery(['api-keys', username], fetchApiKeys)`
  - `useMutation(updateProfile, { onSuccess: invalidate user })`
  - `useMutation(updateSettings, { onSuccess: show toast })`
  - `useMutation(createApiKey)`
  - `useMutation(deleteApiKey)`
  - `useMutation(deleteAccount, { onSuccess: logout & redirect })`
- **Pinia**:
  - `authStore` (current user, logout on account deletion)
  - `uiStore` (update locale when language changes)
- **Component State**:
  - `activeSection: SettingSection`
  - `formData: SettingsFormData` (per section)
  - `hasUnsavedChanges: boolean`
  - `showApiKeyModal: boolean`
  - `showDeleteAccountModal: boolean`
  - `newApiKey: string | null` (show once after creation)

## User Interactions

### Profile Section

1. User updates real name → Types in field → Clicks "Save" → Calls PUT → Shows success toast
2. User updates homepage → Types URL → Real-time validation → Save → Update
3. User updates biography → Textarea with character count → Save → Update

### Preferences Section

1. User changes language → Selects from dropdown → Saves → Updates i18n locale + localStorage
2. User changes items per page → Slider or number input → Save → Updates preference
3. User configures tag cloud → Min frequency & max count sliders → Save → Updates

### Privacy Section

1. User toggles profile visibility → Checkbox → Save → Updates visibility
2. User sets default post visibility → Radio buttons → Save → Updates default

### API Keys Section

1. User clicks "Create API Key" → Opens modal → Enters name → Creates → Shows key ONCE with copy button
2. User copies API key → Click copy → Copies to clipboard → Toast "Copied"
3. User deletes API key → Click delete → Confirms → Calls DELETE → Removes from list

### Account Section

1. User changes password → Current + new password → Validates strength → Save
2. User clicks "Delete Account" → Opens modal → Enters password to confirm → Deletes → Logs out → Redirects to home

## URL Parameters & Query Strings

- `section`: Active settings section (`profile` | `preferences` | `privacy` | `api-keys` | `account`)

Example: `/settings?section=api-keys`

## Page States

- **Loading state**: Skeleton UI for forms while loading data
- **Empty state** (API Keys): "No API keys created yet"
- **Error state - Load failed**: "Failed to load settings"
- **Error state - Save failed**: Toast notification with error message
- **Success state**: Forms populated with current settings
- **Saving state**: Save button shows spinner, form disabled
- **Unsaved changes warning**: When navigating away with unsaved changes

## Responsive Behavior

- **Desktop (>1024px)**: Left sidebar navigation, wide forms
- **Tablet (768-1024px)**: Top tab navigation, medium forms
- **Mobile (<768px)**: Top tab navigation (horizontal scroll), full-width forms, sticky save button

## Accessibility Requirements

- Page title: "Settings - BibSonomy"
- Settings navigation with `role="navigation"` and `aria-label="Settings"`
- Each section has an `<h2>` heading
- All form fields have labels
- Password fields with show/hide toggle
- API key creation modal traps focus
- Delete account requires password confirmation (prevents accidents)
- Success/error messages announced to screen readers

## i18n Keys

```
page.settings.title
page.settings.nav.profile
page.settings.nav.preferences
page.settings.nav.privacy
page.settings.nav.apiKeys
page.settings.nav.account

# Profile Section
page.settings.profile.title
page.settings.profile.realName
page.settings.profile.homepage
page.settings.profile.biography
page.settings.profile.institution
page.settings.profile.save
page.settings.profile.success

# Preferences Section
page.settings.preferences.title
page.settings.preferences.language
page.settings.preferences.language.en
page.settings.preferences.language.de
page.settings.preferences.itemsPerPage
page.settings.preferences.tagCloud.title
page.settings.preferences.tagCloud.minFreq
page.settings.preferences.tagCloud.maxCount
page.settings.preferences.defaultGroups
page.settings.preferences.save
page.settings.preferences.success

# Privacy Section
page.settings.privacy.title
page.settings.privacy.profileVisibility
page.settings.privacy.profileVisibility.public
page.settings.privacy.profileVisibility.private
page.settings.privacy.defaultPostVisibility
page.settings.privacy.save
page.settings.privacy.success

# API Keys Section
page.settings.apiKeys.title
page.settings.apiKeys.create
page.settings.apiKeys.name
page.settings.apiKeys.key
page.settings.apiKeys.created
page.settings.apiKeys.lastUsed
page.settings.apiKeys.delete
page.settings.apiKeys.noKeys
page.settings.apiKeys.modal.title
page.settings.apiKeys.modal.name
page.settings.apiKeys.modal.create
page.settings.apiKeys.modal.cancel
page.settings.apiKeys.modal.success
page.settings.apiKeys.modal.copyKey
page.settings.apiKeys.modal.keyCopied
page.settings.apiKeys.modal.warning
page.settings.apiKeys.delete.confirm
page.settings.apiKeys.delete.success

# Account Section
page.settings.account.title
page.settings.account.changePassword
page.settings.account.currentPassword
page.settings.account.newPassword
page.settings.account.confirmPassword
page.settings.account.passwordStrength
page.settings.account.save
page.settings.account.success
page.settings.account.deleteAccount.title
page.settings.account.deleteAccount.warning
page.settings.account.deleteAccount.button
page.settings.account.deleteAccount.confirm.title
page.settings.account.deleteAccount.confirm.message
page.settings.account.deleteAccount.confirm.password
page.settings.account.deleteAccount.confirm.cancel
page.settings.account.deleteAccount.confirm.delete
page.settings.account.deleteAccount.success

# General
page.settings.unsavedChanges.title
page.settings.unsavedChanges.message
page.settings.error.loadFailed
page.settings.error.saveFailed
```

## Design System References

**Colors**:

- Settings nav background: `gray-50`
- Active nav item: `indigo-50` background, `indigo-700` text, left border `indigo-600`
- Section backgrounds: `white`
- Danger zone (Delete Account): `red-50` background, `red-600` border
- Success states: `emerald-100` background for success messages
- API key display: `gray-100` background, monospace font

**Typography**:

- Page title: text-3xl, font-bold, `gray-900`
- Section titles: text-2xl, font-semibold, `gray-900`
- Field labels: text-sm, font-medium, `gray-700`
- Helper text: text-sm, `gray-600`
- API keys: text-sm, font-mono, `gray-800`

**Spacing**:

- Settings layout: Grid with nav sidebar (w-64) and content area (flex-1)
- Nav items: py-2 px-4, gap-2
- Section padding: p-6
- Form field gaps: gap-6
- Save button: mt-6

## Mockup Notes

- Settings navigation as left sidebar with icons (Heroicons)
  - Profile icon, Preferences icon, Privacy icon, API key icon, Account icon
  - Active state with left border accent and background tint
  - Collapsible on tablet/mobile (hamburger menu)
- Each section clearly separated with headings and horizontal divider
- Forms with clear field groupings using semantic `<fieldset>`
- Save buttons per section (not global save) at bottom-right of each section
- Auto-save indicator when user stops typing (debounced 2 seconds)
- API key creation modal with warning about copying key:
  - Headless UI Dialog component
  - "This key will only be shown once. Copy it now!"
  - API key in monospace font with copy button
  - Success message after copy: "Key copied to clipboard"
- API key list table:
  - Columns: Name, Key (masked), Created, Last Used, Actions
  - Masked key: `sk_live_••••••••••••••••1234`
  - Copy button to reveal and copy full key
  - Delete button with confirmation
- Delete account section with prominent warning:
  - Red border (`border-red-600 border-2`)
  - Warning icon and bold heading
  - "This action cannot be undone" message
  - Password confirmation required
  - Final confirmation checkbox: "I understand this will permanently delete my account and all data"
- Password change section:
  - Current password field (required for security)
  - New password field with real-time strength indicator
  - Confirm new password field with match validation
  - Password strength indicator: Weak (red), Medium (yellow), Strong (green)
  - Password requirements checklist (min length, uppercase, number, special char)
- Language selector:
  - Headless UI Listbox with flag icons
  - Options: Deutsch (🇩🇪), English (🇬🇧)
  - Live preview: "Sprache geändert" / "Language changed" toast
- Items per page slider:
  - Headless UI controlled slider (10-100)
  - Live preview number next to slider
  - Recommended default: 20
- Tag cloud settings with visual preview:
  - Min frequency slider (1-10)
  - Max count slider (10-200)
  - Live preview of tag cloud in modal or inline
- Smooth transitions when switching sections (fade in/out)
- Success toasts after saving each section:
  - "Profile updated successfully"
  - "Preferences saved"
  - Positioned top-right, auto-dismiss after 3 seconds
- Unsaved changes indicator:
  - Asterisk (\*) on section name in nav if changes pending
  - Unsaved changes warning when navigating away
  - Browser beforeunload warning

## Advanced Features

### Avatar Upload

- Drag-and-drop zone or file picker
- Image cropping tool (Headless UI based cropper)
- Preview before save
- Max file size: 5MB
- Supported formats: JPG, PNG, GIF, WebP
- Gravatar integration as fallback option

### Two-Factor Authentication (2FA)

- Enable/disable toggle
- QR code for authenticator app setup
- Backup codes generation and download
- Recovery options (email, SMS if supported)

### API Key Management

- Create multiple API keys with names (e.g., "Browser Extension", "Python Script")
- Set expiration dates for keys (7 days, 30 days, 90 days, never)
- Scope selection (read-only, read-write, admin)
- Usage statistics (last used, total requests)
- Revoke all keys button (emergency)

### Privacy Controls

- Profile visibility (Public, Private, Registered Users Only)
- Individual field visibility toggles (email, homepage, institution)
- Search engine indexing toggle
- Default post visibility (Public, Private, Groups)
- Allow others to fork my posts toggle
- Show activity on profile toggle

### Data Export & Backup

- Download all data (GDPR compliance)
  - Posts (BibTeX, JSON)
  - Tags and groups
  - Profile information
  - Documents (ZIP archive)
- Export format: JSON or XML
- Scheduled backups (weekly/monthly email)

### Session Management

- Active sessions list (device, location, IP, last active)
- Revoke individual sessions
- "Sign out all other sessions" button
- Session timeout settings

## Performance Considerations

- Settings data cached in Pinia store
- Autosave debounced (2 seconds after last keystroke)
- API key creation uses optimistic UI (show immediately, confirm async)
- Avatar upload with progress indicator (chunked upload for large files)
- Settings sections lazy-loaded (only load when navigated to)
- Form validation client-side (Zod schemas) before API call

## Error Handling

- Network errors: Show error toast, preserve form state, retry button
- Validation errors: Inline field errors
- Password change errors: "Current password incorrect", "Passwords do not match"
- API key creation errors: "Failed to create API key. Please try again."
- Avatar upload errors: "File too large", "Invalid format"
- Delete account errors: "Password incorrect", "Cannot delete account with active API keys"
- Concurrent session update: Warn user if settings changed in another tab/device

## Security Considerations

- Password change requires current password
- Account deletion requires password + confirmation checkbox
- API keys masked by default (click to reveal)
- Session timeout enforced (30 minutes of inactivity)
- HTTPS only for all settings operations
- Rate limiting on password changes and API key creation
- Audit log for sensitive actions (password change, API key creation, account deletion)

## Accessibility

- Keyboard navigation for all sections and controls
- Focus management when switching sections
- Error messages announced to screen readers
- Password strength announced to screen readers
- Toggle switches (Headless UI Switch) with proper ARIA labels
- Required fields marked with asterisk and `aria-required`
- Help text for complex settings (aria-describedby)

## Analytics

- Track which settings sections are most visited
- Track settings changes (which settings are changed most often)
- Track API key creation and usage
- Track account deletion requests (successful and cancelled)
- Track autosave vs manual save usage
