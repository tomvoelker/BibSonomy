<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { Edit } from 'lucide-vue-next'
import { useBranding } from '@/composables/useBranding'
import Button from '@/components/ui/Button.vue'

const { t } = useI18n()
const { branding } = useBranding()
</script>

<template>
  <div class="bg-gray-100 rounded-lg p-4 md:p-8 mb-5">
    <!-- Headline -->
    <h2 class="text-2xl md:text-3xl font-medium text-gray-800 mb-3 leading-tight md:leading-[38px]">
      {{ t('home.headline') }}
    </h2>

    <!-- Lead text -->
    <p class="text-base md:text-xl font-light leading-relaxed mb-5 text-gray-800">
      {{ t('home.lead', { projectName: branding.projectName }) }}
    </p>

    <!-- Divider -->
    <hr class="border-0 border-t border-gray-300 my-3 md:my-5" />

    <!-- CTA Buttons -->
    <div class="flex flex-col sm:flex-row gap-2 mt-3 md:mt-5">
      <Button
        variant="success"
        :icon="Edit"
        href="/error/not-implemented?feature=Register"
      >
        {{ t('home.register') }}
      </Button>

      <Button
        variant="info"
        href="/error/not-implemented?feature=Getting+Started"
      >
        {{ t('home.learnmore') }}
      </Button>
    </div>
  </div>
</template>
