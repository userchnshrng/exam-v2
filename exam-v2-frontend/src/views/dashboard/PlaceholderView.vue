<template>
  <el-card class="placeholder-card" shadow="never">
    <div class="placeholder-card__header">
      <div>
        <div class="placeholder-card__title">{{ pageTitle }}</div>
        <div class="placeholder-card__desc">{{ pageDesc }}</div>
      </div>
      <el-tag type="info" effect="light">占位页面</el-tag>
    </div>

    <el-empty description="这里先放布局和路由，后续再接真实业务" />
  </el-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const pageTitle = computed(() => String(route.meta.title || '页面占位'))
const pageDesc = computed(() => String(route.meta.description || '后续接入真实业务'))
</script>
