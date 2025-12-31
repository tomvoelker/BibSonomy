import { useBranding } from './useBranding'

/**
 * Composable for theme-related utilities
 * Provides convenient access to theme colors and classes
 */
export function useTheme() {
  const { branding } = useBranding()

  // Static theme classes (no need for computed since these are constants)
  const primaryColorClass = 'text-primary-600'
  const primaryBgClass = 'bg-primary-600'
  const primaryBorderClass = 'border-primary-600'
  const primaryHoverBgClass = 'hover:bg-primary-700'
  const primaryHoverTextClass = 'hover:text-primary-700'

  const successColorClass = 'text-success-600'
  const successBgClass = 'bg-success-600'

  const infoColorClass = 'text-info-600'
  const infoBgClass = 'bg-info-600'

  const dangerColorClass = 'text-danger-600'
  const dangerBgClass = 'bg-danger-600'

  return {
    branding,
    primaryColorClass,
    primaryBgClass,
    primaryBorderClass,
    primaryHoverBgClass,
    primaryHoverTextClass,
    successColorClass,
    successBgClass,
    infoColorClass,
    infoBgClass,
    dangerColorClass,
    dangerBgClass,
  }
}
