<template>
  <div class="nearby-facilities">
    <h3>附近设施</h3>
    <ul>
      <li v-for="facility in facilities" :key="facility.id">
        {{ facility.name }} - {{ facility.type }}
      </li>
    </ul>
  </div>
</template>

<script setup>
defineProps({
  facilities: {
    type: Array,
    required: true
  }
})
</script>