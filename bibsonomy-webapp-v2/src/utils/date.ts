import { formatDistanceToNow, format } from 'date-fns'

/**
 * Format date as relative time (e.g., "2 hours ago")
 */
export function formatRelativeTime(date: string | Date): string {
  const dateObj = typeof date === 'string' ? new Date(date) : date
  return formatDistanceToNow(dateObj, { addSuffix: true })
}

/**
 * Format full date and time for tooltips
 */
export function formatFullDateTime(date: string | Date): string {
  const dateObj = typeof date === 'string' ? new Date(date) : date
  return format(dateObj, 'PPpp') // e.g., "Apr 29, 2023, 11:30 AM"
}
