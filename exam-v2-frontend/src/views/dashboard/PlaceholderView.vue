<template>
  <div class="placeholder-page">
    <div class="placeholder-card">
      <div class="placeholder-icon">🚀</div>
      <h2 class="placeholder-title">{{ pageTitle }}</h2>
      <p class="placeholder-desc">{{ pageDesc }}</p>
      <div class="placeholder-status">
        <el-tag effect="plain" type="info" size="small">功能开发中</el-tag>
      </div>
      <p class="placeholder-hint">此模块已挂载基础框架，后续可直接切入真实业务。</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
const route = useRoute()
const pageTitle = computed(() => String(route.meta.title || '功能开发中'))
const pageDesc = computed(() => String(route.meta.description || '当前骨架就绪，待接入真实业务。'))
</script>

<style scoped>
.placeholder-page {
  display: flex; align-items: center; justify-content: center;
  min-height: 400px;
}
.placeholder-card {
  text-align: center; padding: 60px 40px;
  background: #fff; border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md); max-width: 500px; width: 100%;
}
.placeholder-icon { font-size: 64px; margin-bottom: 16px; }
.placeholder-title { font-size: 22px; font-weight: 700; color: var(--color-text-title); margin: 0 0 8px; }
.placeholder-desc { font-size: 14px; color: var(--color-text-muted); margin: 0 0 16px; line-height: 1.6; }
.placeholder-status { margin-bottom: 12px; }
.placeholder-hint { font-size: 12px; color: #b0b7c3; margin: 0; }
</style>
