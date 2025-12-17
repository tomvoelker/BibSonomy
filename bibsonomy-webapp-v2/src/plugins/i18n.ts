/**
 * Vue I18n configuration
 * Internationalization with German and English support
 */

import { createI18n } from 'vue-i18n'
import de from '@/locales/de/translation.json'
import en from '@/locales/en/translation.json'

export type MessageSchema = typeof de

const i18n = createI18n<[MessageSchema], 'de' | 'en'>({
  legacy: false, // Use Composition API mode
  locale: 'de', // Default to German
  fallbackLocale: 'en',
  messages: {
    de,
    en,
  },
  datetimeFormats: {
    de: {
      short: {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
      },
      long: {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'long',
      },
      time: {
        hour: '2-digit',
        minute: '2-digit',
      },
    },
    en: {
      short: {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
      },
      long: {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'long',
      },
      time: {
        hour: '2-digit',
        minute: '2-digit',
        hour12: true,
      },
    },
  },
  numberFormats: {
    de: {
      decimal: {
        style: 'decimal',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      },
      percent: {
        style: 'percent',
        useGrouping: false,
      },
    },
    en: {
      decimal: {
        style: 'decimal',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      },
      percent: {
        style: 'percent',
        useGrouping: false,
      },
    },
  },
})

export default i18n
