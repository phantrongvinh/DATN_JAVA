<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

import { ArrowRight, Truck, ShieldCheck, RotateCcw, Sparkles } from 'lucide-vue-next'

import ProductCard from '@/components/ProductCard.vue'

import SectionHeader from '@/components/SectionHeader.vue'
import { useProductStore } from '@/stores/useProductStore'
import { storeToRefs } from 'pinia'

const router = useRouter()

const productStore = useProductStore()

onMounted(async () => {
  await Promise.all([productStore.fetchSpotlightProducts(), productStore.fetchProductOnSale()])
})

const { spotlightProducts, productOnSale } = storeToRefs(productStore)

const url = 'http://localhost:8080/uploads/images'
</script>

<template>
  <!-- HERO -->
  <section class="relative overflow-hidden border-b border-border">
    <div class="container-x grid items-center gap-12 py-16 md:grid-cols-2 md:py-24">
      <div class="animate-fade-up space-y-6">
        <p
          class="flex items-center gap-2 text-xs uppercase tracking-[0.25em] text-muted-foreground"
        >
          <Sparkles class="h-3 w-3 text-gold" />
          Bộ sưu tập Hè 2026
        </p>

        <h1 class="font-display text-5xl leading-[1.05] md:text-7xl">
          Bóng đá <br />
          <span class="italic text-gold">tinh tuyển.</span>
        </h1>

        <p class="max-w-md text-base text-muted-foreground">
          Mỗi sản phẩm là sự kết hợp giữa công nghệ đỉnh cao và thiết kế tinh tế — dành riêng cho
          những ai yêu cái đẹp trên sân cỏ.
        </p>

        <div class="flex flex-wrap items-center gap-3 pt-2">
          <RouterLink to="/products">
            <button
              class="group inline-flex items-center gap-2 bg-ink px-7 py-3.5 text-sm font-medium tracking-wide text-ivory transition-colors hover:bg-ink/85 cursor-pointer"
            >
              Khám phá bộ sưu tập
              <ArrowRight class="h-4 w-4 transition-transform group-hover:translate-x-1" />
            </button>
          </RouterLink>

          <button
            class="inline-flex items-center gap-2 border-b border-foreground px-1 pb-1 text-sm font-medium"
          >
            Xem ưu đãi
          </button>
        </div>
      </div>

      <div class="relative">
        <div class="aspect-[4/5] overflow-hidden bg-secondary rounded-xl">
          <img src="/images/hero.png" class="h-full w-full object-cover" />
        </div>

        <div
          class="absolute -bottom-6 -left-6 hidden border border-border bg-background p-5 shadow-sm md:block"
        >
          <p class="text-[10px] uppercase tracking-widest text-muted-foreground">Best seller</p>
          <p class="font-display text-lg"></p>
          <p class="text-sm text-gold">Giảm đến 28%</p>
        </div>
      </div>
    </div>
  </section>

  <!-- USP -->
  <section class="border-b border-border">
    <div class="container-x grid grid-cols-2 gap-8 py-10 md:grid-cols-4 mx-auto">
      <div
        v-for="(u, i) in [
          { i: Truck, t: 'Miễn phí giao', s: 'Đơn từ 2000K' },
          { i: ShieldCheck, t: 'Chính hãng 100%', s: 'Bảo hành toàn quốc' },
          { i: RotateCcw, t: 'Đổi trả 30 ngày', s: 'Không cần lý do' },
          { i: Sparkles, t: 'Quà tặng riêng', s: 'Cho thành viên VIP' },
        ]"
        :key="i"
        class="flex items-center gap-3"
      >
        <component :is="u.i" class="h-5 w-5 text-gold" stroke-width="1.5" />

        <div>
          <p class="text-sm font-medium">{{ u.t }}</p>
          <p class="text-xs text-muted-foreground">{{ u.s }}</p>
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
      <div class="group relative block aspect-[4/5] overflow-hidden bg-secondary">
        <img
          class="h-full w-full object-cover transition-transform duration-700 group-hover:scale-105"
        />

        <div
          class="absolute inset-0 bg-gradient-to-t from-black/60 via-black/0 to-transparent"
        ></div>

        <div class="absolute bottom-6 left-6 right-6 text-ivory">
          <p class="text-[10px] uppercase tracking-[0.25em] opacity-80">Bộ sưu tập</p>
          <h3 class="font-display text-2xl">Gayf</h3>

          <span class="mt-2 inline-flex items-center gap-1 text-sm">
            Khám phá
            <ArrowRight class="h-4 w-4 transition-transform group-hover:translate-x-1" />
          </span>
        </div>
      </div>
    </div>
  </section>

  <!-- NEW ARRIVALS -->
  <section class="container-x py-20">
    <!-- <SectionHeader eyebrow="Vừa cập bến" title="Hàng mới về" />

    <div class="mt-10 grid grid-cols-2 gap-x-6 gap-y-10 md:grid-cols-4">
      <ProductCard v-for="p in newest" :key="p.id" :product="p" />
    </div> -->
  </section>

  <!-- BLOG -->
  <section class="container-x py-20">
    <SectionHeader eyebrow="Tạp chí" title="Câu chuyện sân cỏ" />

    <!-- <div class="mt-10 grid gap-8 md:grid-cols-3">
      <article v-for="post in BLOG_POSTS" :key="post.id" class="group cursor-pointer">
        <div class="aspect-[4/3] overflow-hidden bg-secondary">
          <img
            :src="post.cover"
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

          <h3 class="font-display text-xl group-hover:text-gold">
            {{ post.title }}
          </h3>

          <p class="text-sm text-muted-foreground">
            {{ post.excerpt }}
          </p>
        </div>
      </article>
    </div> -->
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
