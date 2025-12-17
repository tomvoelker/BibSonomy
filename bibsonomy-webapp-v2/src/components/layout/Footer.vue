<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { useBranding } from '@/composables/useBranding'
import FooterColumn from './FooterColumn.vue'

const { t } = useI18n()
const { branding } = useBranding()

// Column 1: What is BibSonomy
const column1Links = [
  { text: t('footer.gettingStarted'), href: '/error/not-implemented?feature=Getting%20Started' },
  { text: t('footer.buttons'), href: '/error/not-implemented?feature=Buttons' },
  { text: t('footer.help'), href: '/error/not-implemented?feature=Help' },
  { text: t('footer.faqs'), href: '/error/not-implemented?feature=FAQ' },
]

const developerLinks = [
  { text: t('footer.overview'), href: 'https://bibsonomy.bitbucket.io', external: true },
  { text: t('footer.apiDoc'), href: 'https://bitbucket.org/bibsonomy/bibsonomy/wiki/documentation/api/REST%20API', external: true },
]

// Column 2: Privacy & Contact
const column2Links = [
  { text: t('footer.about'), href: '/error/not-implemented?feature=About' },
  { text: t('footer.termsOfUse'), href: '/error/not-implemented?feature=Terms%20of%20Use' },
  { text: t('footer.cookies'), href: '/error/not-implemented?feature=Cookies' },
  { text: t('footer.issues'), href: '/error/not-implemented?feature=Issues' },
]

// Column 3: Integration
const column3Links = [
  { text: t('footer.academicPuma'), href: 'https://academic-puma.de/', external: true },
  { text: t('footer.typoThree'), href: 'https://typo3.org/extensions/repository/view/ext_bibsonomy_csl', external: true },
  { text: t('footer.wordpress'), href: 'https://wordpress.org/plugins/bibsonomy-csl/', external: true },
  { text: t('footer.javaClient'), href: 'https://dev.bibsonomy.org/maven2/org/bibsonomy/bibsonomy-rest-client/', external: true },
  { text: t('footer.scraperInfo'), href: '/error/not-implemented?feature=Scraper%20Info' },
]

// Column 4: About BibSonomy
const column4Links = [
  { text: t('footer.team'), href: '/error/not-implemented?feature=Team' },
  { text: t('footer.blog'), href: 'https://blog.bibsonomy.org', external: true },
]

const socialMediaLinks = [
  { text: t('footer.twitter'), href: 'https://twitter.com/bibsonomy', external: true },
]
</script>

<template>
  <footer class="bg-gray-100 py-8 px-4 mt-5">
    <div class="flex flex-wrap max-w-[1170px] mx-auto">
      <!-- Column 1: What is BibSonomy + Developer -->
      <div class="w-full md:w-1/2 lg:flex-[0_0_25%] lg:max-w-[25%] px-4 mb-6 md:mb-0">
        <h4 class="text-sm font-bold mb-2.5 text-gray-800">
          {{ t('footer.what', { projectName: branding.projectName }) }}
        </h4>
        <ul class="list-none p-0 m-0 text-[13px]">
          <li v-for="link in column1Links" :key="link.href" class="mb-1">
            <a :href="link.href" class="text-primary-600 no-underline hover:underline">
              {{ link.text }}
            </a>
          </li>
        </ul>

        <h4 class="text-sm font-bold my-5 text-gray-800">
          {{ t('footer.developer') }}
        </h4>
        <ul class="list-none p-0 m-0 text-[13px]">
          <li v-for="link in developerLinks" :key="link.href" class="mb-1">
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

      <!-- Column 2: Privacy & Contact -->
      <FooterColumn
        :title="t('footer.privacyContact')"
        :links="column2Links"
      />

      <!-- Column 3: Integration -->
      <FooterColumn
        :title="t('footer.integration')"
        :links="column3Links"
      />

      <!-- Column 4: About BibSonomy + Social Media -->
      <div class="w-full md:w-1/2 lg:flex-[0_0_25%] lg:max-w-[25%] px-4 mb-6 md:mb-0">
        <h4 class="text-sm font-bold mb-2.5 text-gray-800">
          {{ t('footer.aboutBibSonomy', { projectName: branding.projectName }) }}
        </h4>
        <ul class="list-none p-0 m-0 text-[13px]">
          <li v-for="link in column4Links" :key="link.href" class="mb-1">
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

        <h4 class="text-sm font-bold my-5 text-gray-800">
          {{ t('footer.socialMedia') }}
        </h4>
        <ul class="list-none p-0 m-0 text-[13px]">
          <li v-for="link in socialMediaLinks" :key="link.href" class="mb-1">
            <a
              :href="link.href"
              class="text-primary-600 no-underline hover:underline"
              target="_blank"
              rel="noopener noreferrer"
            >
              {{ link.text }}
            </a>
          </li>
        </ul>
      </div>
    </div>

    <!-- Legal Notices -->
    <div class="border-t border-gray-300 mt-5 pt-4 max-w-[1170px] mx-auto">
      <p class="text-xs text-gray-600 m-0 text-center">
        {{ t('footer.offeredBy', { projectName: branding.projectName }) }}
      </p>
    </div>
  </footer>
</template>
