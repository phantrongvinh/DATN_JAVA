<script setup lang="ts">
import ulti from '@/ulti/ulti'
import { computed } from 'vue'
import { RouterLink } from 'vue-router'

const props = defineProps<{
  product: {
    id: number
    name: string
    price: number
    image: string
    discountPrice: number
    categoryName: string
    brandName: string
    promotion: {
      discountType: string
      discountValue: number
    }
  }
}>()
const url = 'http://localhost:8080/uploads/images'
</script>

<template>
  <RouterLink :to="'/productDetail/' + props.product.id" class="group block">
    <div class="relative aspect-[4/5] overflow-hidden bg-secondary">
      <img
        :src="url + '/' + props.product.image"
        loading="lazy"
        class="h-full w-full object-contain transition-transform duration-700 ease-out group-hover:scale-105"
      />

      <span
        v-if="props.product.promotion"
        class="absolute left-3 top-3 bg-ink px-2 py-1 text-[10px] font-medium uppercase tracking-widest text-ivory"
      >
        {{
          props.product.promotion.discountType === 'percent'
            ? `-${props.product.promotion.discountValue}%`
            : `-${ulti.formatVND(props.product.promotion.discountValue)}`
        }}
      </span>

      <span
        class="absolute right-3 top-3 text-[10px] uppercase tracking-widest text-muted-foreground"
      >
        {{ props.product.brandName }}
      </span>
    </div>

    <div class="mt-3 space-y-1">
      <p class="text-[11px] uppercase tracking-widest text-muted-foreground">
        {{ props.product.categoryName }}
      </p>

      <h3 class="text-sm font-medium leading-snug transition-colors group-hover:text-gold">
        {{ props.product.name }}
      </h3>

      <div class="flex items-baseline gap-2 pt-1" v-if="props.product.promotion">
        <span class="text-sm font-semibold">
          {{ ulti.formatVND(props.product.discountPrice) }}
        </span>

        <span class="text-xs text-muted-foreground line-through">
          {{ ulti.formatVND(props.product.price) }}
        </span>
      </div>
      <div class="flex items-baseline gap-2 pt-1" v-else>
        <span class="text-sm font-semibold">
          {{ ulti.formatVND(props.product.price) }}
        </span>
      </div>
    </div>
  </RouterLink>
</template>
