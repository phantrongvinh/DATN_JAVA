<script setup>
import { computed } from 'vue'
import { storeToRefs } from 'pinia'
import { RouterLink } from 'vue-router'
import { Instagram, Facebook, Youtube, MapPin } from 'lucide-vue-next'
import { useSiteSettingsStore } from '@/stores/useSiteSettingsStore'

const siteSettingsStore = useSiteSettingsStore()
const { landing } = storeToRefs(siteSettingsStore)

const t = computed(() => landing.value.theme)
</script>

<template>
  <footer
    class="mt-24 border-t"
    :style="{
      backgroundColor: t.footerBg,
      color: t.footerText,
      borderColor: `color-mix(in oklab, ${t.footerText} 20%, transparent)`,
    }"
  >
    <div class="container-x grid gap-12 py-16 md:grid-cols-4">
      <!-- Logo -->
      <div class="md:col-span-1">
        <div class="flex items-baseline gap-1">
          <span class="font-display text-2xl font-semibold">Maison</span>
          <span class="font-display text-2xl italic text-gold">Calcio</span>
        </div>

        <p class="mt-4 max-w-xs text-sm opacity-70">
          Cửa hàng thiết bị bóng đá cao cấp. Tinh tuyển từ những thương hiệu hàng đầu thế giới.
        </p>
      </div>

      <!-- Mua sắm -->
      <div>
        <h4 class="font-display text-sm font-semibold uppercase tracking-widest">Mua sắm</h4>

        <ul class="mt-4 space-y-2 text-sm opacity-70">
          <li><RouterLink to="/products" class="hover:opacity-100">Tất cả sản phẩm</RouterLink></li>
          <li>
            <RouterLink
              :to="{ path: '/products', query: { category: 'Giày' } }"
              class="hover:opacity-100"
            >
              Giày đá bóng
            </RouterLink>
          </li>
          <li>
            <RouterLink
              :to="{ path: '/products', query: { category: 'Áo đấu' } }"
              class="hover:opacity-100"
            >
              Áo đấu
            </RouterLink>
          </li>
          <li>
            <RouterLink
              :to="{ path: '/products', query: { onSale: 'true' } }"
              class="hover:opacity-100"
            >
              Giảm giá
            </RouterLink>
          </li>
        </ul>
      </div>

      <!-- Hỗ trợ -->
      <div>
        <h4 class="font-display text-sm font-semibold uppercase tracking-widest">Hỗ trợ</h4>

        <ul class="mt-4 space-y-2 text-sm opacity-70">
          <li><RouterLink to="/about" class="hover:opacity-100">Về chúng tôi</RouterLink></li>
          <li>
            <RouterLink to="/map" class="inline-flex items-center gap-1 hover:opacity-100">
              <MapPin class="h-3 w-3" /> Cửa hàng & Bản đồ
            </RouterLink>
          </li>
          <li><a href="#" class="hover:opacity-100">Chính sách đổi trả</a></li>
          <li><a href="#" class="hover:opacity-100">Hướng dẫn chọn size</a></li>
        </ul>
      </div>

      <!-- Bản tin -->
      <div>
        <h4 class="font-display text-sm font-semibold uppercase tracking-widest">Bản tin</h4>

        <p class="mt-4 text-sm opacity-70">Nhận tin mới và ưu đãi riêng cho thành viên.</p>

        <form
          class="mt-4 flex border-b focus-within:opacity-100"
          :style="{ borderColor: `color-mix(in oklab, ${t.footerText} 30%, transparent)` }"
        >
          <input
            type="email"
            placeholder="Email của bạn"
            class="flex-1 bg-transparent py-2 text-sm outline-none placeholder:opacity-60"
          />
          <button class="text-sm font-medium uppercase tracking-widest">Gửi</button>
        </form>

        <div class="mt-6 flex gap-3 opacity-70">
          <a href="#" aria-label="Instagram" class="hover:opacity-100"
            ><Instagram class="h-4 w-4"
          /></a>
          <a href="#" aria-label="Facebook" class="hover:opacity-100"
            ><Facebook class="h-4 w-4"
          /></a>
          <a href="#" aria-label="Youtube" class="hover:opacity-100"><Youtube class="h-4 w-4" /></a>
        </div>
      </div>
    </div>

    <div :style="{ borderTop: `1px solid color-mix(in oklab, ${t.footerText} 15%, transparent)` }">
      <div
        class="container-x flex flex-col items-center justify-between gap-2 py-6 text-xs opacity-70 md:flex-row"
      >
        <p>© 2026 Maison Calcio. Mọi quyền được bảo lưu.</p>
        <p>Thiết kế tại Việt Nam · Vận chuyển toàn quốc</p>
      </div>
    </div>
  </footer>
</template>
