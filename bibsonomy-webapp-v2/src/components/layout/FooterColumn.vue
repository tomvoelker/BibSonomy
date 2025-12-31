<script setup lang="ts">
import { RouterLink } from 'vue-router'
import { useI18n } from 'vue-i18n'

/** Footer link definition - exported for reuse in Footer.vue */
export interface FooterLink {
  text: string
  href: string
  external?: boolean
}

interface Props {
  title: string
  links: FooterLink[]
}

defineProps<Props>()

const { t } = useI18n()
</script>

<template>
  <div class="w-full md:w-1/2 lg:flex-[0_0_25%] lg:max-w-[25%] px-4 mb-6 md:mb-0">
    <h4 class="text-sm font-bold mb-2.5 text-gray-800">
      {{ title }}
    </h4>
    <ul class="list-none p-0 m-0 text-[13px]">
      <li v-for="link in links" :key="link.href" class="mb-1">
        <component
          :is="link.external ? 'a' : RouterLink"
          :href="link.external ? link.href : undefined"
          :to="link.external ? undefined : link.href"
          class="text-primary-600 no-underline hover:underline"
          :target="link.external ? '_blank' : undefined"
          :rel="link.external ? 'noopener noreferrer' : undefined"
          :aria-label="link.external ? `${link.text} (${t('common.opensInNewTab')})` : undefined"
        >
          {{ link.text }}
          <span v-if="link.external" class="sr-only"> ({{ t('common.opensInNewTab') }})</span>
        </component>
      </li>
    </ul>
  </div>
</template>
