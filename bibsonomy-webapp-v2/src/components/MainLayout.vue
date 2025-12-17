<script setup lang="ts">
import Header from './layout/Header.vue'
import Navbar from './layout/Navbar.vue'
import Footer from './layout/Footer.vue'
</script>

<template>
  <div class="bg-gray-100 min-h-screen">
    <!-- Header -->
    <Header />

    <!-- Navigation Bar -->
    <Navbar />

    <!-- Main Content (slot) -->
    <slot />

    <!-- Footer -->
    <Footer />
  </div>
</template>
