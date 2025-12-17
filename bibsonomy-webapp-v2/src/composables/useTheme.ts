import { computed } from 'vue'
import { useBranding } from './useBranding'

/**
 * Composable for theme-related utilities
 * Provides convenient access to theme colors and classes
 */
export function useTheme() {
  const { branding } = useBranding()

  // Computed classes for primary color
  const primaryColorClass = computed(() => 'text-primary-600')
  const primaryBgClass = computed(() => 'bg-primary-600')
  const primaryBorderClass = computed(() => 'border-primary-600')
  const primaryHoverBgClass = computed(() => 'hover:bg-primary-700')
  const primaryHoverTextClass = computed(() => 'hover:text-primary-700')

  // Computed classes for success color
  const successColorClass = computed(() => 'text-success-600')
  const successBgClass = computed(() => 'bg-success-600')

  // Computed classes for info color
  const infoColorClass = computed(() => 'text-info-600')
  const infoBgClass = computed(() => 'bg-info-600')

  // Computed classes for danger color
  const dangerColorClass = computed(() => 'text-danger-600')
  const dangerBgClass = computed(() => 'bg-danger-600')

  return {
    // Branding info
    branding,

    // Primary color utilities
    primaryColorClass,
    primaryBgClass,
    primaryBorderClass,
    primaryHoverBgClass,
    primaryHoverTextClass,

    // Success color utilities
    successColorClass,
    successBgClass,

    // Info color utilities
    infoColorClass,
    infoBgClass,

    // Danger color utilities
    dangerColorClass,
    dangerBgClass,
  }
}
