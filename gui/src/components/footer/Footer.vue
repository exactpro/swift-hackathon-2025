<script lang="ts" setup>
import config from '../../../config'
import FooterLinks from './FooterLinks.vue'
import FooterBgLogo from './FooterBgLogo.vue'
import ExactproLogo from './ExactproLogo.vue'
import ExactproDevise from './ExactproDevise.vue'
</script>

<template>
  <footer
    class="relative text-white footer min-h-40 sm:footer-horizontal footer-center bg-base-300 p-4 bg-linear-to-r from-grad-bg-1 to-grad-bg-2"
  >
    <figure class="absolute flex justify-end inset-0 z-0 overflow-hidden">
      <FooterBgLogo class="footer__bg-logo" />
    </figure>
    <div class="z-10 flex justify-between items-start gap-10">
      <div class="flex flex-col gap-4 text-center sm:text-left">
        <aside class="flex flex-wrap gap-x-8 gap-y-4 items-center justify-center sm:justify-start">
          <a href="https://exactpro.com" target="_blank" rel="noopener noreferrer">
            <ExactproLogo class="w-44" />
          </a>
          <ExactproDevise class="w-36" />
        </aside>
        <FooterLinks class="sm:hidden" />
        <span class="text-sm text-balance" v-if="config.useMock">
          This is a solution prototype developed exclusively for the purposes of the Swift Hackathon 2025 and the
          corresponding demo.
        </span>
      </div>
      <FooterLinks class="hidden sm:flex py-6" />
    </div>
  </footer>
</template>

<style scoped>
@reference "../../style.css";
.footer__bg-logo {
  @apply w-full mr-[-50%] sm:w-auto sm:h-[150%] sm:-mr-20;
}
</style>
