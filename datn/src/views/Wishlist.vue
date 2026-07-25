<script setup>
import { onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { Heart, ArrowRight } from 'lucide-vue-next'
import { useWishlistStore } from '@/stores/useWishlistStore'
import ulti from '@/ulti/ulti'
import { useNotificationStore } from '@/stores/useNotificationStore'

const wishlistStore = useWishlistStore()
const { wishlists, loading } = storeToRefs(wishlistStore)
const notificationStore = useNotificationStore()

onMounted(async () => {
  await wishlistStore.fetchWishlist()
  console.log(wishlists.value)
})

const handleRemove = async (productId) => {
  await wishlistStore.toggle(productId)
  notificationStore.notify('Đã bỏ khỏi yêu thích', 'success')
}
</script>

<template>
  <div class="border border-border">
    <!-- Header -->
    <div class="border-b border-border bg-secondary/20 px-6 py-7">
      <h2 class="font-display text-2xl">Yêu thích</h2>

      <p class="mt-2 text-sm text-muted-foreground">{{ wishlists.length }} sản phẩm bạn đã lưu</p>
    </div>

    <!-- Loading skeleton -->
    <div v-if="loading" class="grid grid-cols-2 gap-6 p-6 md:grid-cols-4">
      <div v-for="i in 8" :key="i" class="overflow-hidden rounded-xl border border-border">
        <div class="aspect-[3/4] animate-pulse bg-secondary"></div>

        <div class="space-y-3 p-4">
          <div class="h-4 w-3/4 animate-pulse rounded bg-secondary"></div>
          <div class="h-4 w-1/3 animate-pulse rounded bg-secondary"></div>
        </div>
      </div>
    </div>

    <!-- Empty state -->
    <div
      v-else-if="!wishlists.length"
      class="flex flex-col items-center justify-center px-6 py-28 text-center"
    >
      <div class="mb-8 flex h-24 w-24 items-center justify-center rounded-full bg-secondary">
        <Heart class="h-10 w-10 text-[#B8860B]" />
      </div>

      <h2 class="font-display text-lg">Chưa có sản phẩm yêu thích</h2>

      <p class="mt-3 max-w-md text-sm text-muted-foreground">
        Hãy khám phá những chiếc bánh thơm ngon và nhấn vào biểu tượng trái tim để lưu lại những sản
        phẩm bạn yêu thích.
      </p>

      <div class="mt-4">
        <RouterLink to="/products" class="border-b border-foreground pb-0.5 text-foreground">
          Khám phá sản phẩm
        </RouterLink>
      </div>
    </div>

    <!-- Danh sách -->
    <div v-else class="grid grid-cols-2 gap-8 p-8 lg:grid-cols-3 xl:grid-cols-4">
      <div
        v-for="p in wishlists"
        :key="p.id"
        class="group rounded-2xl border border-[#ECE3D4] bg-white transition-all duration-500 hover:-translate-y-2 hover:shadow-2xl"
      >
        <RouterLink :to="`/productDetail/${p.id}`" class="flex h-full flex-col overflow-hidden">
          <!-- Image -->
          <div class="relative overflow-hidden">
            <img
              :src="p.image"
              :alt="p.name"
              class="aspect-[4/5] w-full object-cover transition duration-700 group-hover:scale-110"
            />

            <!-- Promotion -->

            <div
              v-if="p.promotion"
              class="absolute left-4 top-4 rounded-full bg-[#D4A373] px-3 py-1 text-xs font-semibold text-white"
            >
              -{{ p.promotion.discountValue }}%
            </div>

            <!-- Wishlist -->

            <button
              @click.prevent="handleRemove(p.id)"
              class="absolute right-4 top-4 flex h-11 w-11 items-center justify-center rounded-full bg-white shadow-lg transition hover:scale-110"
            >
              <Heart class="h-5 w-5 fill-red-500 text-red-500" />
            </button>
          </div>

          <!-- Content -->
          <div class="flex flex-1 flex-col p-5">
            <h3 class="line-clamp-2 min-h-[3rem] font-display text-sm leading-6 text-[#3B2E2A]">
              {{ p.name }}
            </h3>

            <div class="mt-auto pt-3">
              <template v-if="p.discountPrice < p.price">
                <p class="text-sm text-gray-400 line-through">
                  {{ ulti.formatVND(p.price) }}
                </p>

                <p class="text-base font-bold text-[#8B5E3C]">
                  {{ ulti.formatVND(p.discountPrice) }}
                </p>
              </template>

              <template v-else>
                <p class="text-base font-bold text-[#8B5E3C]">
                  {{ ulti.formatVND(p.price) }}
                </p>
              </template>
            </div>
          </div>
        </RouterLink>
      </div>
    </div>
  </div>
</template>
