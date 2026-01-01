# Branding System

This document explains the modern branding system for BibSonomy v2, which replaces the original theme system (`theme_bibsonomy.properties`, `theme_puma.properties`).

## Overview

The branding system allows the same codebase to be deployed with different branding configurations. This is useful for:

- **Development mode**: Shows "BibLicious" branding
- **Production**: Shows "BibSonomy" branding
- **White-label deployments**: Can show "PUMA" or custom branding

## How It Works

### 1. Automatic Dev/Prod Switching

The system automatically selects the appropriate branding:

- **Development** (`bun run dev`): Always uses `biblicious` theme → displays "BibLicious"
- **Production** (`bun run build`): Uses `VITE_PROJECT_THEME` environment variable → displays "BibSonomy" (default) or "PUMA"

This matches the original webapp's behavior where `project.name = BibLicious` in development `project.properties`.

### 2. Configuration Files

**`src/config/branding.ts`**
- Contains theme definitions (bibsonomy, biblicious, puma)
- Each theme defines: project name, tagline, colors, favicon, social links, feature flags
- Exports `getBrandingConfig()` function that returns the active theme

**`src/composables/useBranding.ts`**
- Vue composable for accessing branding in components
- Usage: `const { branding } = useBranding()`

### 3. Environment Variables

**.env.example / .env.production**
```bash
# In production, set the theme:
VITE_PROJECT_THEME=bibsonomy  # or 'puma' for PUMA deployments
```

No environment variable needed for development - it's automatic!

## Adding a New Theme

To add a new theme (e.g., for a white-label deployment):

1. **Edit `src/config/branding.ts`**:

```typescript
const themes: Record<string, BrandingConfig> = {
  // ... existing themes ...

  customTheme: {
    projectName: 'CustomName',
    tagline: 'Your custom tagline',
    primaryColor: '#ff0000',  // Custom color
    faviconPath: '/favicon-custom.ico',
    blogUrl: 'https://blog.custom.org',
    social: {
      twitter: 'customhandle',
    },
    features: {
      groupsAndFriends: true,
      homepageExternalSearch: false,
    },
  },
}
```

2. **Add custom favicon** to `public/`:
   - `public/favicon-custom.ico`

3. **Deploy with environment variable**:
```bash
VITE_PROJECT_THEME=customTheme bun run build
```

## Branding in Components

### Using the composable:

```vue
<script setup lang="ts">
import { useBranding } from '@/composables/useBranding'

const { branding } = useBranding()
</script>

<template>
  <h1>{{ branding.projectName }}</h1>
  <p>{{ branding.tagline }}</p>

  <!-- Use in translations -->
  <p>{{ t('footer.about', { projectName: branding.projectName }) }}</p>
</template>
```

### Available branding properties:

```typescript
branding.projectName      // "BibSonomy" | "BibLicious" | "PUMA"
branding.tagline          // Project tagline
branding.primaryColor     // "#006699" (blue) or theme-specific
branding.faviconPath      // Path to favicon
branding.blogUrl          // Blog URL (optional)
branding.social.twitter   // Social media handles
branding.features.*       // Feature flags
```

## Feature Flags

Themes can enable/disable features:

```typescript
// In a component:
const { branding } = useBranding()

if (branding.features.groupsAndFriends) {
  // Show groups and friends UI
}
```

## Comparison with Original System

| Original Webapp | Modern Vue App |
|----------------|----------------|
| `project.properties` → `project.name = BibLicious` | Auto-detect dev mode → "BibLicious" |
| `project.theme = bibsonomy` | `VITE_PROJECT_THEME=bibsonomy` |
| `theme_bibsonomy.properties` | `themes.bibsonomy` in `branding.ts` |
| `theme_puma.properties` | `themes.puma` in `branding.ts` |
| JSP: `${properties['project.name']}` | Vue: `branding.projectName` |

## Testing

### Test dev mode (BibLicious):
```bash
bun run dev
```
Visit http://localhost:5173 - should show "BibLicious"

### Test production mode (BibSonomy):
```bash
VITE_PROJECT_THEME=bibsonomy bun run build
bun run preview
```
Should show "BibSonomy"

### Test PUMA theme:
```bash
VITE_PROJECT_THEME=puma bun run build
bun run preview
```
Should show "PUMA" with red color scheme
