<script lang="ts" setup>
import FooterLinks from './FooterLinks.vue'
import FooterBgLogo from './FooterBgLogo.vue'
import ExactproLogo from './ExactproLogo.vue'
</script>

<template>
  <footer
    class="relative text-white footer min-h-20 sm:footer-horizontal footer-center bg-base-300 p-4 bg-linear-to-r from-grad-bg-1 to-grad-bg-2"
  >
    <figure class="absolute flex justify-end inset-0 z-0 overflow-hidden">
      <FooterBgLogo class="footer__bg-logo" />
    </figure>
    <div class="z-10 flex flex-col sm:flex-row justify-between items-start gap-x-10">
      <div class="flex flex-col sm:flex-row gap-4 text-center sm:text-left items-center">
        <a href="https://exactpro.com" target="_blank" rel="noopener noreferrer">
          <ExactproLogo class="w-36" />
        </a>

        <span class="text-xs text-balance max-w-sm">
          This is a solution prototype developed exclusively for the purposes of the Swift Hackathon 2025 and the
          corresponding demo.
        </span>
      </div>
      <FooterLinks class="py-2" />
    </div>
  </footer>
</template>

<style scoped>
@reference "../../style.css";
.footer__bg-logo {
  @apply w-full mr-[-40%] sm:w-auto sm:h-[150%] sm:-mr-8;
}
</style>
