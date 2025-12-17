<script setup lang="ts">
interface Link {
  text: string
  href: string
  external?: boolean
}

interface Props {
  title: string
  links: Link[]
}

defineProps<Props>()
</script>

<template>
  <div class="w-full md:w-1/2 lg:flex-[0_0_25%] lg:max-w-[25%] px-4 mb-6 md:mb-0">
    <h4 class="text-sm font-bold mb-2.5 text-gray-800">
      {{ title }}
    </h4>
    <ul class="list-none p-0 m-0 text-[13px]">
      <li v-for="link in links" :key="link.href" class="mb-1">
        <a
          :href="link.href"
          class="text-primary-600 no-underline hover:underline"
          :target="link.external ? '_blank' : undefined"
          :rel="link.external ? 'noopener noreferrer' : undefined"
        >
          {{ link.text }}
        </a>
      </li>
    </ul>
  </div>
</template>
