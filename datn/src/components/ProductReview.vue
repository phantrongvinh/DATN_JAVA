<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { Star } from 'lucide-vue-next'
import { useReviewStore } from '@/stores/useReviewStore'
import { useNotificationStore } from '@/stores/useNotificationStore'
import { useDebounce } from '@/composables/useDebounce'
import moderationAPI from '@/api/moderationAPI'
import ulti from '@/ulti/ulti'

const props = defineProps({
  productId: { type: [Number, String], required: true },
})

const reviewStore = useReviewStore()
const notification = useNotificationStore()
const { summary, submitting } = storeToRefs(reviewStore)

onMounted(() => reviewStore.fetchReviews(props.productId))

const form = ref({ rating: 0, comment: '' })
const hoverRating = ref(0)

// AI pre-check khi gõ
const moderationWarning = ref(null)
const checkingModeration = ref(false)
const debouncedComment = useDebounce(
  computed(() => form.value.comment),
  800,
)

watch(debouncedComment, async (text) => {
  moderationWarning.value = null
  if (!text || text.trim().length < 10) return

  checkingModeration.value = true
  try {
    const res = await moderationAPI.check(text)
    if (res.flagged) {
      moderationWarning.value =
        'Nội dung có thể chứa từ ngữ không phù hợp, vui lòng xem lại trước khi gửi'
    }
  } catch (err) {
  } finally {
    checkingModeration.value = false
  }
})

const canSubmit = computed(() => form.value.rating > 0 && !moderationWarning.value)

const handleSubmit = async () => {
  if (form.value.rating === 0) {
    notification.notify('Vui lòng chọn số sao đánh giá', 'error')
    return
  }
  try {
    await reviewStore.createReview(props.productId, form.value)
    notification.notify('Đánh giá của bạn đã được ghi nhận', 'success')
    form.value = { rating: 0, comment: '' }
  } catch (err) {
    notification.notify(err.response?.data?.message ?? 'Có lỗi xảy ra', 'error')
  }
}

const ratingPercent = (count) => {
  if (!summary.value?.totalReviews) return 0
  return Math.round((count / summary.value.totalReviews) * 100)
}
</script>

<template>
  <div v-if="summary" class="space-y-10">
    <!-- Tổng quan rating -->
    <div class="flex flex-col gap-8 md:flex-row md:items-center">
      <div class="text-center">
        <p class="font-display text-5xl">{{ summary.averageRating }}</p>
        <div class="mt-2 flex justify-center gap-0.5">
          <Star
            v-for="i in 5"
            :key="i"
            class="h-4 w-4"
            :class="i <= Math.round(summary.averageRating) ? 'fill-gold text-gold' : 'text-border'"
          />
        </div>
        <p class="mt-1 text-xs text-muted-foreground">{{ summary.totalReviews }} đánh giá</p>
      </div>

      <div class="flex-1 space-y-1.5">
        <div v-for="star in [5, 4, 3, 2, 1]" :key="star" class="flex items-center gap-2 text-xs">
          <span class="w-3 text-muted-foreground">{{ star }}</span>
          <Star class="h-3 w-3 fill-gold text-gold" />
          <div class="h-1.5 flex-1 overflow-hidden rounded-full bg-secondary">
            <div
              class="h-full bg-gold"
              :style="{ width: ratingPercent(summary.ratingBreakdown[star]) + '%' }"
            />
          </div>
          <span class="w-8 text-right text-muted-foreground">{{
            summary.ratingBreakdown[star]
          }}</span>
        </div>
      </div>
    </div>

    <!-- Form đánh giá -->
    <div v-if="summary.canReview" class="border border-border p-6">
      <h3 class="font-display text-lg">Viết đánh giá của bạn</h3>

      <div class="mt-4 flex gap-1">
        <button
          v-for="i in 5"
          :key="i"
          @mouseenter="hoverRating = i"
          @mouseleave="hoverRating = 0"
          @click="form.rating = i"
        >
          <Star
            class="h-6 w-6 transition-colors"
            :class="i <= (hoverRating || form.rating) ? 'fill-gold text-gold' : 'text-border'"
          />
        </button>
      </div>

      <textarea
        v-model="form.comment"
        rows="3"
        placeholder="Chia sẻ trải nghiệm của bạn về sản phẩm..."
        class="mt-4 w-full border bg-background px-3 py-2 text-sm outline-none"
        :class="moderationWarning ? 'border-red-400' : 'border-border focus:border-foreground'"
      />

      <p v-if="checkingModeration" class="mt-1 text-xs text-muted-foreground">
        Đang kiểm tra nội dung...
      </p>
      <p v-else-if="moderationWarning" class="mt-1 text-xs text-red-500">{{ moderationWarning }}</p>

      <button
        @click="handleSubmit"
        :disabled="submitting || !canSubmit"
        class="mt-4 bg-ink px-6 py-2.5 text-sm text-ivory hover:bg-ink/90 disabled:opacity-50 disabled:cursor-not-allowed"
      >
        {{ submitting ? 'Đang gửi...' : 'Gửi đánh giá' }}
      </button>
    </div>

    <p v-else-if="summary.hasReviewed" class="text-sm text-muted-foreground">
      Bạn đã đánh giá sản phẩm này.
    </p>
    <p v-else class="text-sm text-muted-foreground">
      Bạn cần mua và nhận sản phẩm này để có thể đánh giá.
    </p>

    <!-- Danh sách -->
    <div v-if="summary.reviews.length" class="space-y-6">
      <article v-for="r in summary.reviews" :key="r.userId" class="border-b border-border pb-6">
        <div class="flex items-center justify-between">
          <p class="font-medium">{{ r.userName }}</p>
          <p class="text-xs text-muted-foreground">{{ ulti.formatDate(r.createdAt) }}</p>
        </div>
        <div class="mt-1 flex gap-0.5">
          <Star
            v-for="i in 5"
            :key="i"
            class="h-3.5 w-3.5"
            :class="i <= r.rating ? 'fill-gold text-gold' : 'text-border'"
          />
        </div>
        <p class="mt-2 text-sm text-muted-foreground">{{ r.comment }}</p>
      </article>
    </div>
    <p v-else class="text-sm text-muted-foreground">Chưa có đánh giá nào.</p>
  </div>
</template>
