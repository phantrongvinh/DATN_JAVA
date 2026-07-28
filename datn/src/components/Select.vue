<template>
  <div class="dropdown">
    <button
      class="btn btn-transparent w-100 text-start border-bottom rounded-0"
      data-bs-toggle="dropdown"
    >
      {{ props.selectedName }}
    </button>

    <ul v-if="!props.title" class="dropdown-menu shadow-lg border-0 rounded-4 w-100">
      <li>
        <a class="dropdown-item rounded-3 py-2" @click.prevent="emit('update:selected', undefined)">
          {{ props.descript }}
        </a>
      </li>
      <li v-for="d in props.data" :key="d.id">
        <a class="dropdown-item rounded-3 py-2" @click.prevent="emit('update:selected', d.id)">
          {{ d.name }}
        </a>
      </li>
    </ul>

    <ul v-else class="dropdown-menu shadow-lg border-0 rounded-4 w-100">
      <li v-for="d in props.data" :key="d.id">
        <a
          class="dropdown-item rounded-3 py-2"
          @click.prevent="emit('update:selectedByName', d[`${title}Name`])"
        >
          {{ d[`${title}Name`] }}
        </a>
      </li>
    </ul>
  </div>
</template>

<script setup>
const props = defineProps({
  data: Array,
  selectedName: String,
  selected: String,
  descript: String,
  selectedByName: String,
  title: String,
})

const emit = defineEmits(['update:selected', 'update:selectedByName'])
</script>
