import { formatDistanceToNow, format } from 'date-fns'

/**
 * Check if a date object is valid
 */
function isValidDate(date: Date): boolean {
  return !isNaN(date.getTime())
}

/**
 * Format date as relative time (e.g., "2 hours ago")
 * Returns empty string for invalid dates
 */
export function formatRelativeTime(date: string | Date): string {
  const dateObj = typeof date === 'string' ? new Date(date) : date
  if (!isValidDate(dateObj)) {
    return ''
  }
  return formatDistanceToNow(dateObj, { addSuffix: true })
}

/**
 * Format full date and time for tooltips
 * Returns empty string for invalid dates
 */
export function formatFullDateTime(date: string | Date): string {
  const dateObj = typeof date === 'string' ? new Date(date) : date
  if (!isValidDate(dateObj)) {
    return ''
  }
  return format(dateObj, 'PPpp') // e.g., "Apr 29, 2023, 11:30 AM"
}
