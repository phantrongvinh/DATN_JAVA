<template>
  <footer class="mt-24 border-t border-border bg-background">
    <div class="container-x grid gap-12 py-16 md:grid-cols-4">
      <!-- Logo -->
      <div class="md:col-span-1">
        <div class="flex items-baseline gap-1">
          <span class="font-display text-2xl font-semibold">Maison</span>
          <span class="font-display text-2xl italic text-gold">Calcio</span>
        </div>

        <p class="mt-4 max-w-xs text-sm text-muted-foreground">
          Cửa hàng thiết bị bóng đá cao cấp. Tinh tuyển từ những thương hiệu hàng đầu thế giới.
        </p>
      </div>

      <!-- Mua sắm -->
      <div>
        <h4 class="font-display text-sm font-semibold uppercase tracking-widest">Mua sắm</h4>

        <ul class="mt-4 space-y-2 text-sm text-muted-foreground">
          <li>
            <RouterLink to="/products" class="hover:text-foreground"> Tất cả sản phẩm </RouterLink>
          </li>

          <li>
            <RouterLink
              :to="{ path: '/products', query: { category: 'Giày' } }"
              class="hover:text-foreground"
            >
              Giày đá bóng
            </RouterLink>
          </li>

          <li>
            <RouterLink
              :to="{ path: '/products', query: { category: 'Áo đấu' } }"
              class="hover:text-foreground"
            >
              Áo đấu
            </RouterLink>
          </li>

          <li>
            <RouterLink
              :to="{ path: '/products', query: { onSale: 'true' } }"
              class="hover:text-foreground"
            >
              Giảm giá
            </RouterLink>
          </li>
        </ul>
      </div>

      <!-- Hỗ trợ -->
      <div>
        <h4 class="font-display text-sm font-semibold uppercase tracking-widest">Hỗ trợ</h4>

        <ul class="mt-4 space-y-2 text-sm text-muted-foreground">
          <li><a href="#" class="hover:text-foreground">Chính sách đổi trả</a></li>
          <li><a href="#" class="hover:text-foreground">Vận chuyển</a></li>
          <li><a href="#" class="hover:text-foreground">Hướng dẫn chọn size</a></li>
          <li><a href="#" class="hover:text-foreground">Liên hệ</a></li>
        </ul>
      </div>

      <!-- Bản tin -->
      <div>
        <h4 class="font-display text-sm font-semibold uppercase tracking-widest">Bản tin</h4>

        <p class="mt-4 text-sm text-muted-foreground">
          Nhận tin mới và ưu đãi riêng cho thành viên.
        </p>

        <form class="mt-4 flex border-b border-foreground/30 focus-within:border-foreground">
          <input
            type="email"
            placeholder="Email của bạn"
            class="flex-1 bg-transparent py-2 text-sm outline-none placeholder:text-muted-foreground"
          />

          <button class="text-sm font-medium uppercase tracking-widest">Gửi</button>
        </form>

        <div class="mt-6 flex gap-3 text-muted-foreground">
          <a href="#" aria-label="Instagram" class="hover:text-foreground">
            <Instagram class="h-4 w-4" />
          </a>

          <a href="#" aria-label="Facebook" class="hover:text-foreground">
            <Facebook class="h-4 w-4" />
          </a>

          <a href="#" aria-label="Youtube" class="hover:text-foreground">
            <Youtube class="h-4 w-4" />
          </a>
        </div>
      </div>
    </div>

    <div class="border-t border-border">
      <div
        class="container-x flex flex-col items-center justify-between gap-2 py-6 text-xs text-muted-foreground md:flex-row"
      >
        <p>© 2026 Maison Calcio. Mọi quyền được bảo lưu.</p>

        <p>Thiết kế tại Việt Nam · Vận chuyển toàn quốc</p>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'
import { Instagram, Facebook, Youtube } from 'lucide-vue-next'
</script>
