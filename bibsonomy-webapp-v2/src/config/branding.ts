/**
 * Branding configuration for the application
 *
 * This module provides a modern equivalent to the original webapp's theme system
 * (theme_bibsonomy.properties and theme_puma.properties).
 *
 * Supports:
 * - Dev mode: "BibLicious" branding
 * - Production: "BibSonomy" or "PUMA" branding
 * - Theme switching via environment variables
 */

export interface BrandingConfig {
  /** Project name displayed in UI */
  projectName: string
  /** Project tagline/subtitle */
  tagline: string
  /** Primary brand color */
  primaryColor: string
  /** Logo path (relative to public/) */
  logoPath?: string
  /** Favicon path (relative to public/) */
  faviconPath: string
  /** Blog URL */
  blogUrl?: string
  /** Social media links */
  social: {
    twitter?: string
    facebook?: string
    youtube?: string
    instagram?: string
  }
  /** Feature flags */
  features: {
    groupsAndFriends: boolean
    homepageExternalSearch: boolean
  }
}

/**
 * Theme definitions
 * Equivalent to theme_*.properties files in original webapp
 */
const themes: Record<string, BrandingConfig> = {
  bibsonomy: {
    projectName: 'BibSonomy',
    tagline: 'The blue social bookmark and publication sharing system.',
    primaryColor: '#006699',
    faviconPath: '/favicon.ico',
    blogUrl: 'https://blog.bibsonomy.org',
    social: {
      twitter: 'bibsonomyCrew',
    },
    features: {
      groupsAndFriends: false,
      homepageExternalSearch: false,
    },
  },
  biblicious: {
    projectName: 'BibLicious',
    tagline: 'The blue social bookmark and publication sharing system.',
    primaryColor: '#006699',
    faviconPath: '/favicon.ico',
    blogUrl: 'https://blog.bibsonomy.org',
    social: {
      twitter: 'bibsonomyCrew',
    },
    features: {
      groupsAndFriends: false,
      homepageExternalSearch: false,
    },
  },
  puma: {
    projectName: 'PUMA',
    tagline: 'Publications at University of Mannheim - Academic repository',
    primaryColor: '#c50e1f', // Red for PUMA
    faviconPath: '/favicon-puma.ico',
    blogUrl: undefined,
    social: {},
    features: {
      groupsAndFriends: true,
      homepageExternalSearch: true,
    },
  },
}

/**
 * Get the current branding configuration based on environment
 *
 * Logic:
 * - Development mode (import.meta.env.DEV): Use "biblicious" theme
 * - Production mode: Use theme from VITE_PROJECT_THEME env var (default: "bibsonomy")
 */
export function getBrandingConfig(): BrandingConfig {
  // In development, always use BibLicious
  if (import.meta.env.DEV) {
    return themes.biblicious
  }

  // In production, use configured theme
  const theme = import.meta.env.VITE_PROJECT_THEME || 'bibsonomy'
  return themes[theme] || themes.bibsonomy
}

/**
 * Get project name (for use in translations)
 */
export function getProjectName(): string {
  return getBrandingConfig().projectName
}

/**
 * Get primary brand color
 */
export function getPrimaryColor(): string {
  return getBrandingConfig().primaryColor
}
