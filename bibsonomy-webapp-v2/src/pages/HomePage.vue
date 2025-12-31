<script setup lang="ts">
import { ref, computed } from 'vue'
import { usePosts } from '@/composables/usePosts'
import { Bookmark, FileText } from 'lucide-vue-next'
import MainLayout from '@/components/MainLayout.vue'
import PageContainer from '@/components/layout/PageContainer.vue'
import Sidebar from '@/components/layout/Sidebar.vue'
import Jumbotron from '@/components/home/Jumbotron.vue'
import PostSection from '@/components/home/PostSection.vue'
import BookmarkCard from '@/components/post/BookmarkCard.vue'
import PublicationCard from '@/components/post/PublicationCard.vue'
import SegmentedControl from '@/components/ui/SegmentedControl.vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

// Fetch recent posts
const filters = ref({ limit: 10, offset: 0 })
const { data, isLoading } = usePosts(filters)

// Separate posts by type
const bookmarks = computed(() =>
  data.value?.posts.filter((p) => p.resourceType === 'bookmark') || []
)

const publications = computed(() =>
  data.value?.posts.filter((p) => p.resourceType === 'publication') || []
)

// View filter state
const viewMode = ref<'all' | 'bookmarks' | 'publications'>('all')

const viewOptions = computed(() => [
  { id: 'all', label: t('post.allPosts') },
  { id: 'bookmarks', label: t('post.bookmarks') },
  { id: 'publications', label: t('post.publications') },
])

// Show/hide based on view mode
const showBookmarks = computed(() => viewMode.value === 'all' || viewMode.value === 'bookmarks')
const showPublications = computed(() => viewMode.value === 'all' || viewMode.value === 'publications')
</script>

<template>
  <MainLayout>
    <PageContainer>
      <!-- CTA section for non-logged-in users -->
      <Jumbotron />

      <!-- Responsive layout: 3 columns on desktop, stacked on mobile -->
      <div class="flex flex-col lg:flex-row -mx-4">
        <!-- Main content area (75% on desktop) -->
        <div class="w-full lg:flex-[0_0_75%] lg:max-w-[75%] px-4">
          <!-- View mode filter -->
          <div class="flex justify-center md:justify-start mb-4">
            <SegmentedControl v-model="viewMode" :options="viewOptions" />
          </div>

          <!-- Posts grid -->
          <div
            class="flex flex-col -mx-4"
            :class="{
              'md:flex-row': viewMode === 'all',
              'md:flex-col': viewMode !== 'all'
            }"
          >
            <!-- Bookmarks Section (conditional) -->
            <div
              v-if="showBookmarks"
              class="w-full px-4 mb-6 md:mb-0"
              :class="{
                'md:flex-1': viewMode === 'all',
                'md:max-w-full': viewMode !== 'all'
              }"
            >
            <PostSection
              :title="t('post.bookmarks')"
              :posts="bookmarks"
              :loading="isLoading"
              :icon="Bookmark"
              :card-component="BookmarkCard"
            />
          </div>

            <!-- Publications Section (conditional) -->
            <div
              v-if="showPublications"
              class="w-full px-4 mb-6 md:mb-0"
              :class="{
                'md:flex-1': viewMode === 'all',
                'md:max-w-full': viewMode !== 'all'
              }"
            >
              <PostSection
                :title="t('post.publications')"
                :posts="publications"
                :loading="isLoading"
                :icon="FileText"
                :card-component="PublicationCard"
              />
            </div>
          </div>
        </div>

        <!-- Sidebar (100% on mobile/tablet, 25% on desktop) -->
        <div class="w-full lg:flex-[0_0_25%] lg:max-w-[25%] px-4">
          <Sidebar />
        </div>
      </div>
    </PageContainer>
  </MainLayout>
</template>
