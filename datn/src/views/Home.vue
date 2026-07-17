<script setup>
import { computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { RouterLink } from 'vue-router'
import { ArrowRight, Truck, ShieldCheck, RotateCcw, Sparkles, Flame } from 'lucide-vue-next'

import ProductCard from '@/components/site/ProductCard.vue'
import SectionHeader from '@/components/site/SectionHeader.vue'
import HeroCarousel from '@/components/site/HeroCarousel.vue'

import { useProductStore } from '@/stores/useProductStore'
import { useAudienceStore } from '@/stores/useAudienceStore'
import { useSiteSettingsStore } from '@/stores/useSiteSettingsStore'
import { useTimePromotionStore } from '@/stores/useTimePromotionStore'
import { useBrandStore } from '@/stores/useBrandStore'
import { useFlashSaleCountdown } from '@/composables/useCountDown'
import ulti from '@/ulti/ulti'

const productStore = useProductStore()
const audienceStore = useAudienceStore()
const siteSettingsStore = useSiteSettingsStore()
const promotionStore = useTimePromotionStore()
const brandStore = useBrandStore()

onMounted(async () => {
  await Promise.all([
    productStore.fetchSpotlightProducts(),
    productStore.fetchProductOnSale(),
    audienceStore.fetchAudiences(),
    brandStore.fetchBrand(),
    promotionStore.fetchAllTimePromotion({ newPage: 1, newSize: 50 }),
  ])
})

const { spotlightProducts, productOnSale } = storeToRefs(productStore)
const { audiences } = storeToRefs(audienceStore)
const { landing } = storeToRefs(siteSettingsStore)
const { timePromotions } = storeToRefs(promotionStore)
const { brands } = storeToRefs(brandStore)

const marqueeBrands = computed(() => [...brands.value, ...brands.value])

const flashState = useFlashSaleCountdown(timePromotions)

const images = ['bo-su-tap-1.jpg', 'bo-su-tap-2.jpg', 'bo-su-tap-3.jpg']
const audiencesView = computed(() =>
  audiences.value.slice(0, 3).map((el, idx) => ({ ...el, image: images[idx] })),
)

const audienceLabel = (name) => {
  const map = { Men: 'Nam', Women: 'Nữ', Kids: 'Trẻ em' }
  return map[name] ?? 'Trẻ em'
}

const uspItems = [
  { icon: Truck, t: 'Miễn phí giao', s: 'Đơn từ 500K' },
  { icon: ShieldCheck, t: 'Chính hãng 100%', s: 'Bảo hành toàn quốc' },
  { icon: RotateCcw, t: 'Đổi trả 30 ngày', s: 'Không cần lý do' },
  { icon: Sparkles, t: 'Quà tặng riêng', s: 'Cho thành viên VIP' },
]

// Categories - lấy ảnh đại diện từ 3 sản phẩm đầu trong spotlight
const categoriesView = computed(() => {
  const p = spotlightProducts.value
  if (!p || p.length < 11) return []
  return [
    { label: 'Giày đá bóng', img: p[0]?.image, cat: 'Giày' },
    { label: 'Áo đấu', img: p[3]?.image, cat: 'Áo đấu' },
    { label: 'Phụ kiện', img: p[10]?.image, cat: 'Phụ kiện' },
  ]
})

const posts = computed(() => landing.value.posts ?? [])
</script>

<template>
  <!-- HERO CAROUSEL -->
  <HeroCarousel :slides="landing.slides" :autoplay-ms="landing.slideAutoplayMs" />

  <!-- BRAND MARQUEE -->
  <section class="overflow-hidden border-b border-border bg-background py-6">
    <div class="flex w-max animate-marquee gap-16 whitespace-nowrap">
      <span
        v-for="(b, i) in marqueeBrands"
        :key="i"
        class="font-display text-2xl italic tracking-wide text-muted-foreground/70"
      >
        {{ b.name }}
      </span>
    </div>
  </section>

  <!-- USP -->
  <section class="border-b border-border">
    <div class="container-x grid grid-cols-2 gap-8 py-10 md:grid-cols-4">
      <div v-for="(u, i) in uspItems" :key="i" class="flex items-center justify-center gap-3">
        <component :is="u.icon" class="h-5 w-5 text-gold" stroke-width="1.5" />
        <div>
          <p class="text-sm font-medium">{{ u.t }}</p>
          <p class="text-xs text-muted-foreground">{{ u.s }}</p>
        </div>
      </div>
    </div>
  </section>

  <!-- FLASH SALE COUNTDOWN -->
  <section v-if="flashState.promo" class="relative overflow-hidden bg-ink text-ivory">
    <div
      class="absolute inset-0 opacity-20"
      style="
        background-image:
          radial-gradient(circle at 20% 20%, var(--gold), transparent 50%),
          radial-gradient(circle at 80% 60%, var(--gold), transparent 50%);
      "
    />
    <div class="container-x relative grid items-center gap-8 py-14 md:grid-cols-[1fr_auto]">
      <div class="space-y-3">
        <p class="flex items-center gap-2 text-[11px] uppercase tracking-[0.3em] text-gold">
          <Flame class="h-3.5 w-3.5" /> Flash Sale
        </p>
        <h2 class="font-display text-3xl md:text-4xl">
          {{ flashState.promo.name }} -
          <span class="italic text-gold">
            giảm
            {{
              flashState.promo.discountType === 'PERCENT'
                ? flashState.promo.discountValue
                : ulti.formatVND(flashState.promo.discountValue)
            }}{{ flashState.promo.discountType === 'PERCENT' ? '%' : '' }} tổng đơn
          </span>
        </h2>
        <p class="text-sm text-ivory/70">
          Diễn ra hằng ngày từ <b>{{ flashState.promo.startTime }}</b> đến
          <b>{{ flashState.promo.endTime }}</b
          >. Đừng để bỏ lỡ nhé!
        </p>
      </div>

      <div class="flex items-center gap-3">
        <p
          v-if="flashState.status === 'active'"
          class="mr-1 text-[10px] uppercase tracking-widest text-gold"
        >
          Đang diễn ra — kết thúc sau
        </p>
        <p
          v-else-if="flashState.status === 'upcoming'"
          class="mr-1 text-[10px] uppercase tracking-widest text-ivory/50"
        >
          Bắt đầu sau
        </p>
        <p v-else class="mr-1 text-[10px] uppercase tracking-widest text-ivory/50">
          Khung giờ tiếp theo sau
        </p>

        <div
          v-for="(c, i) in [
            { v: flashState.h, l: 'GIỜ' },
            { v: flashState.m, l: 'PHÚT' },
            { v: flashState.s, l: 'GIÂY' },
          ]"
          :key="i"
          class="min-w-[72px] border border-ivory/20 bg-black/40 px-4 py-3 text-center backdrop-blur-sm"
        >
          <p class="font-display text-3xl">{{ String(c.v).padStart(2, '0') }}</p>
          <p class="mt-1 text-[10px] uppercase tracking-widest text-ivory/60">{{ c.l }}</p>
        </div>
      </div>
    </div>
  </section>

  <!-- SALE -->
  <section class="container-x py-20">
    <SectionHeader
      eyebrow="Ưu đãi đặc biệt"
      title="Đang giảm giá"
      :link="{ to: '/products', label: 'Xem tất cả ưu đãi', query: { onSale: 'true' } }"
    />
    <div class="mt-10 grid grid-cols-2 gap-x-6 gap-y-10 md:grid-cols-4">
      <ProductCard v-for="p in productOnSale" :key="p.id" :product="p" />
    </div>
  </section>

  <!-- CATEGORIES -->
  <section class="container-x py-10">
    <div class="grid gap-4 md:grid-cols-3">
      <RouterLink
        v-for="c in categoriesView"
        :key="c.cat"
        :to="{ path: '/products', query: { category: c.cat } }"
        class="group relative block aspect-[4/5] overflow-hidden bg-secondary"
      >
        <img
          :src="c.img"
          :alt="c.label"
          class="h-full w-full object-cover transition-transform duration-700 group-hover:scale-105"
        />
        <div class="absolute inset-0 bg-gradient-to-t from-black/60 via-black/0 to-transparent" />
        <div class="absolute bottom-6 left-6 right-6 text-ivory">
          <p class="text-[10px] uppercase tracking-[0.25em] opacity-80">Bộ sưu tập</p>
          <h3 class="font-display text-2xl">{{ c.label }}</h3>
          <span class="mt-2 inline-flex items-center gap-1 text-sm">
            Khám phá <ArrowRight class="h-4 w-4 transition-transform group-hover:translate-x-1" />
          </span>
        </div>
      </RouterLink>
    </div>

    <!-- Audiences (giữ theo cấu trúc route đang dùng ở Header/Vue) -->
    <div class="mt-4 grid gap-4 md:grid-cols-3">
      <RouterLink
        v-for="item in audiencesView"
        :key="item.id"
        :to="{ name: 'products', query: { audienceIds: item.id } }"
        class="group relative block aspect-[4/5] overflow-hidden bg-secondary"
      >
        <img
          :src="'/images/' + item.image"
          class="h-full w-full object-cover transition-transform duration-700 group-hover:scale-105"
        />
        <div class="absolute inset-0 bg-gradient-to-t from-black/60 via-black/0 to-transparent" />
        <div class="absolute bottom-6 left-6 right-6 text-ivory">
          <p class="text-[10px] uppercase tracking-[0.25em] opacity-80">Bộ sưu tập</p>
          <h3 class="font-display text-2xl">Dành cho {{ audienceLabel(item.name) }}</h3>
          <span class="mt-2 inline-flex items-center gap-1 text-sm">
            Khám phá <ArrowRight class="h-4 w-4 transition-transform group-hover:translate-x-1" />
          </span>
        </div>
      </RouterLink>
    </div>
  </section>

  <!-- NEW ARRIVALS -->
  <section class="container-x py-20">
    <SectionHeader
      eyebrow="Vừa cập bến"
      title="Hàng mới về"
      :link="{ to: '/products', label: 'Xem tất cả sản phẩm' }"
    />
    <div class="mt-10 grid grid-cols-2 gap-x-6 gap-y-10 md:grid-cols-4">
      <ProductCard v-for="p in spotlightProducts" :key="p.id" :product="p" />
    </div>
  </section>

  <!-- EDITORIAL / LOOKBOOK -->
  <section class="bg-ink py-24 text-ivory">
    <div class="container-x grid items-center gap-12 md:grid-cols-2">
      <div class="aspect-[4/3] overflow-hidden">
        <img
          src="https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&w=1200&q=80"
          alt="Lookbook"
          class="h-full w-full object-cover"
        />
      </div>
      <div class="space-y-6">
        <p class="text-xs uppercase tracking-[0.25em] text-ivory/60">Lookbook 2026</p>
        <h2 class="font-display text-4xl leading-tight md:text-5xl">
          Phong cách trên sân,<br /><span class="italic text-gold">cảm hứng ngoài đời.</span>
        </h2>
        <p class="max-w-md text-ivory/70">
          Bộ ảnh độc quyền hợp tác cùng các cầu thủ và nhiếp ảnh gia hàng đầu Việt Nam.
        </p>
        <button class="border-b border-ivory pb-1 text-sm tracking-widest uppercase">
          Xem bộ ảnh
        </button>
      </div>
    </div>
  </section>

  <!-- BLOG -->
  <section v-if="posts.length" class="container-x py-20">
    <SectionHeader
      eyebrow="Tạp chí"
      title="Câu chuyện sân cỏ"
      :link="{ to: '/blog', label: 'Xem tất cả bài viết' }"
    />
    <div class="mt-10 grid gap-8 md:grid-cols-3">
      <RouterLink
        v-for="post in posts.slice(0, 3)"
        :key="post.id"
        :to="{ name: 'blog-detail', params: { slug: post.slug } }"
        class="group block"
      >
        <div class="aspect-[4/3] overflow-hidden bg-secondary">
          <img
            :src="post.cover"
            :alt="post.title"
            class="h-full w-full object-cover transition-transform duration-700 group-hover:scale-105"
          />
        </div>
        <div class="mt-4 space-y-2">
          <div
            class="flex items-center gap-3 text-[11px] uppercase tracking-widest text-muted-foreground"
          >
            <span class="text-gold">{{ post.category }}</span>
            <span>·</span>
            <span>{{ post.readingTime }}</span>
          </div>
          <h3 class="font-display text-xl leading-snug transition-colors group-hover:text-gold">
            {{ post.title }}
          </h3>
          <p class="text-sm text-muted-foreground">{{ post.excerpt }}</p>
        </div>
      </RouterLink>
    </div>
  </section>

  <!-- NEWSLETTER -->
  <section class="border-t border-border">
    <div class="container-x py-20 text-center">
      <p class="text-xs uppercase tracking-[0.25em] text-muted-foreground">Gia nhập câu lạc bộ</p>
      <h2 class="mx-auto mt-3 max-w-2xl font-display text-3xl md:text-5xl">
        Nhận quyền tiếp cận sớm và ưu đãi <span class="italic text-gold">riêng tư</span>.
      </h2>
      <form class="mx-auto mt-8 flex max-w-md items-end border-b border-foreground">
        <input
          type="email"
          placeholder="email@cuaban.com"
          class="flex-1 bg-transparent py-3 text-sm outline-none"
        />
        <button class="px-2 pb-3 text-xs uppercase tracking-widest">Đăng ký</button>
      </form>
    </div>
  </section>
</template>
