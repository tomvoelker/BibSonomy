<script setup lang="ts">
import type { User } from '@/types/models'

interface Props {
  user: User
}

defineProps<Props>()
</script>

<template>
  <a
    :href="`/error/not-implemented?feature=User+Profile`"
    class="text-primary-600 hover:text-primary-700 hover:underline font-medium no-underline"
  >
    {{ user.realName || user.username }}
  </a>
</template>
