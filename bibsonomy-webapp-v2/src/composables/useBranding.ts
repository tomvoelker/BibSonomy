/**
 * Vue composable for accessing branding configuration
 *
 * Usage in components:
 * const { branding } = useBranding()
 * <h1>{{ branding.projectName }}</h1>
 */

import { readonly, reactive } from 'vue'
import { getBrandingConfig, type BrandingConfig } from '@/config/branding'

const brandingConfig = reactive<BrandingConfig>(getBrandingConfig())

export function useBranding() {
  return {
    branding: readonly(brandingConfig),
  }
}
