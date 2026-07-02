<script setup>
import { useRoute } from 'vue-router'

import { computed, onMounted, ref, watch } from 'vue'
import { useProductStore } from '@/stores/useProductStore'
import { storeToRefs } from 'pinia'
import ulti from '@/ulti/ulti'
import { useCart } from '@/composables/useCart'
import { useNotificationStore } from '@/stores/useNotificationStore'
import ProductCard from '@/components/site/ProductCard.vue'
import { Heart, Minus, Plus, RotateCcw, ShieldCheck, Truck } from 'lucide-vue-next'

const route = useRoute()
const productStore = useProductStore()
const { product, filterProducts } = storeToRefs(productStore)
const productRelate = ref([])

watch(
  () => route.params.productId,
  async (newId) => {
    if (newId) {
      const param = {
        bradnIds: null,
        categoryIds: null,
        audienceIds: null,
        search: null,
        onSale: null,
        minPrice: null,
        maxPrice: null,
        sortBy: null,
      }
      await Promise.all([
        productStore.fetchProductByID(newId),
        productStore.fetchFilterProducts(param),
      ])
      productRelate.value = filterProducts.value
        .filter(
          (p) =>
            p.id !== product.value.id &&
            (p.brandName === product.value.brandName ||
              p.categoryName === product.value.categoryName),
        )
        .slice(0, 4)
    }
  },
  { immediate: true },
)

// tabs
const activeTab = ref('desc')

// colors
const colors = computed(() => {
  if (!product.value) return []
  return [...new Set(product.value.variants.map((v) => v.color))]
})

const selectedColor = ref(null)
const selectedSize = ref(null)

watch(product, (val) => {
  if (val?.variants?.length) {
    selectedColor.value = val.variants[0].color
    selectedSize.value = val.variants[0].sizeName
  }
})

const selectColor = (color) => {
  selectedColor.value = color
  selectedSize.value = sizes.value[0]?.sizeName ?? null
}

// sizes theo color đang chọn
const sizes = computed(() => {
  if (!product.value) return []
  return product.value.variants.filter((v) => v.color === selectedColor.value)
})

// variant đang chọn
const selectedVariant = computed(() => {
  if (!product.value) return null
  return product.value.variants.find(
    (v) => v.color === selectedColor.value && v.sizeName === selectedSize.value,
  )
})

// giá hiển thị
const displayPrice = computed(() => {
  return (
    selectedVariant.value?.discountedPrice ??
    selectedVariant.value?.price ??
    product.value?.basePrice
  )
})

// discount %
const discountPercent = computed(() => product.value?.promotion?.discountValue ?? 0)

// gallery
const activeImg = ref(0)
const gallery = computed(() => product.value?.images ?? [])

// reset activeImg khi đổi product
watch(product, () => {
  activeImg.value = 0
})

//
onMounted(async () => {
  await productStore.fetchProductOnSale()
})
//
const url = 'http://localhost:8080/uploads/images'

// quantity
const qty = ref(1)

// hanlde add to cart
const quantityMessage = ref('')

const { addItem } = useCart()

const notification = useNotificationStore()

watch(qty, (newQuantity) => {
  if (newQuantity) quantityMessage.value = ''
})

const handleAddToCart = async () => {
  if (qty.value <= 0) {
    quantityMessage.value = 'Vui lòng chọn số lượng'
    return
  }

  if (!selectedVariant.value) {
    quantityMessage.value = 'Vui lòng chọn phân loại'
    return
  }

  const image = product.value.images.find((i) => i.isPrimary) ?? product.value.images[0]

  const data = {
    name: product.value.name,
    productVariantId: selectedVariant.value.id,
    sku: selectedVariant.value.sku,
    image: image?.imageUrl,
    sizeName: selectedVariant.value.sizeName,
    color: selectedVariant.value.color,
    quantity: qty.value,
    originalPrice: selectedVariant.value.price,
    price: selectedVariant.value.discountedPrice
      ? selectedVariant.value.discountedPrice
      : selectedVariant.value.price,
  }

  await addItem(data)
  notification.notify(
    `Thêm ${data.name} size ${data.sizeName} số lượng ${data.quantity} thành công`,
    'success',
  )
}
</script>

<template>
  <div className="container-x py-8">
    <p className="text-xs uppercase tracking-[0.2em] text-muted-foreground">
      <RouterLink to="/">Trang chủ</RouterLink> / <RouterLink to="/products">Sản phẩm</RouterLink> /
      {{ product?.categoryName }}
    </p>
  </div>

  <div class="py-24 text-center text-muted-foreground" v-if="product?.variants?.length === 0">
    Chưa cập nhật sản phẩm
  </div>
  <div v-else-if="product">
    <div className="container-x grid gap-12 pb-16 md:grid-cols-2">
      <!-- Gallery thumbnails -->
      <div class="flex gap-4">
        <div class="hidden flex-col gap-3 md:flex">
          <button
            v-for="(g, i) in gallery"
            :key="g.id"
            @click="activeImg = i"
            class="h-20 w-16 overflow-hidden border"
            :class="activeImg === i ? 'border-foreground' : 'border-transparent'"
          >
            <img :src="g.imageUrl" class="h-full w-full object-contain" />
          </button>
        </div>

        <!-- Main image -->
        <div class="flex-1 overflow-hidden bg-secondary">
          <img
            :src="gallery[activeImg]?.imageUrl"
            :alt="product.name"
            class="aspect-[4/5] w-full object-contain"
          />
        </div>
      </div>
      <div className="space-y-6">
        <!-- INFO -->
        <div>
          <p class="text-xs uppercase tracking-[0.25em] text-muted-foreground">
            {{ product.brandName }} · {{ product.categoryName }}
          </p>
          <h1 class="mt-2 font-display text-4xl">{{ product.name }}</h1>
          <div class="mt-3 flex items-center gap-2 text-sm">
            <!-- <span class="text-muted-foreground">{product.rating} · {REVIEWS.length} đánh giá</span> -->
          </div>
        </div>

        <!-- PRICE -->
        <div class="flex items-center gap-3">
          <span class="font-display text-3xl">
            {{ ulti.formatVND(displayPrice) }}
          </span>

          <span v-if="product.promotion" class="line-through text-muted-foreground">
            {{ ulti.formatVND(selectedVariant?.price) }}
          </span>

          <span v-if="product.promotion" class="bg-red-500 px-2 py-1 text-xs text-white">
            -{{
              product.promotion?.discountType === 'percent'
                ? `${product.promotion?.discountValue}%`
                : `${product.promotion?.discountValue}đ`
            }}
          </span>
        </div>

        <!-- Colors -->
        <div class="flex flex-wrap gap-2">
          <button
            v-for="color in colors"
            :key="color"
            @click="selectColor(color)"
            class="min-w-12 border px-3 py-2.5 text-sm transition-colors"
            :class="
              selectedColor === color
                ? 'border-foreground bg-foreground text-background'
                : 'border-border hover:border-foreground'
            "
          >
            {{ color }}
          </button>
        </div>

        <!-- Sizes -->
        <div class="flex flex-wrap gap-2">
          <button
            v-for="v in sizes"
            :key="v.id"
            @click="selectedSize = v.sizeName"
            class="min-w-12 border px-3 py-2.5 text-sm transition-colors"
            :class="
              selectedSize === v.sizeName
                ? 'border-foreground bg-foreground text-background'
                : 'border-border hover:border-foreground'
            "
          >
            {{ v.sizeName }}
          </button>
        </div>

        <!-- STOCK -->
        <p v-if="selectedVariant" class="text-sm">
          Còn lại:
          <span class="font-semibold">
            {{ selectedVariant.stock }}
          </span>
          sản phẩm
        </p>

        <!-- Quantity -->
        <div className="flex gap-3 pt-2">
          <div class="flex items-center border border-border">
            <button @click="qty = Math.max(1, qty - 1)" class="p-3 cursor-pointer">
              <Minus class="h-3.5 w-3.5" />
            </button>
            <span class="w-10 text-center text-sm">{{ qty }}</span>
            <button @click="qty++" class="p-3 cursor-pointer">
              <Plus class="h-3.5 w-3.5" />
            </button>
          </div>

          <!-- Add to cart -->
          <button
            @click.prevent="handleAddToCart"
            class="flex-1 bg-ink py-3 text-sm font-medium uppercase tracking-widest text-ivory transition-colors hover:bg-ink/85 cursor-pointer"
            :disabled="!selectedVariant || selectedVariant.stock === 0"
          >
            Thêm vào giỏ
          </button>
          <button
            class="border border-border p-3 transition-colors hover:border-foreground"
            aria-label="Yêu thích"
          >
            <Heart class="h-5 w-5" strokeWidth="{1.5}" />
          </button>
        </div>

        <!-- USPs  -->
        <div class="space-y-3 border-t border-border pt-6 text-sm text-muted-foreground">
          <div class="flex items-center gap-3">
            <Truck class="h-4 w-4 text-gold" strokeWidth="{1.5}" /> Giao hàng nhanh trong 24h tại HN
            & HCM
          </div>
          <div class="flex items-center gap-3">
            <RotateCcw class="h-4 w-4 text-gold" strokeWidth="{1.5}" /> Đổi trả miễn phí trong 30
            ngày
          </div>
          <div class="flex items-center gap-3">
            <ShieldCheck class="h-4 w-4 text-gold" strokeWidth="{1.5}" /> Bảo hành chính hãng toàn
            quốc
          </div>
        </div>
      </div>
    </div>
    <!-- Tabs -->
    <section className="container-x pb-20">
      <div class="border-b border-border">
        <button
          @click="activeTab = 'desc'"
          class="mr-8 pb-3 text-sm uppercase tracking-widest"
          :class="activeTab === 'desc' ? 'border-b-2 border-foreground' : ''"
        >
          Mô tả
        </button>

        <button
          @click="activeTab = 'reviews'"
          class="pb-3 text-sm uppercase tracking-widest"
          :class="activeTab === 'reviews' ? 'border-b-2 border-foreground' : ''"
        >
          Đánh giá
        </button>
      </div>
      <div v-if="activeTab === 'desc'" class="mt-8 max-w-3xl">
        <p>{{ product.description }}</p>
      </div>

      <div v-if="activeTab === 'reviews'" class="mt-8 space-y-8">
        <article class="border-b border-border pb-6">Chưa có đánh giá</article>
      </div>
    </section>

    <section class="container-x pb-24">
      <h2 class="font-display text-3xl">Có thể bạn cũng thích</h2>

      <!-- Relate -->
      <div class="mt-8 grid grid-cols-2 gap-x-6 gap-y-10 md:grid-cols-4">
        <ProductCard v-for="p in productRelate" :key="p.id" :product="p" />
      </div>
    </section>
  </div>

  <div v-else class="py-24 text-center text-muted-foreground">Đang tải...</div>
</template>
