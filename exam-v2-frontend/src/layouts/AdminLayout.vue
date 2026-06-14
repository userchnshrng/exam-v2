<template>
  <DashboardLayout
    role="ADMIN"
    title="管理员后台"
    subtitle="教师、公告和系统基础配置统一在这里管理"
  >
    <router-view />
  </DashboardLayout>
</template>

<script setup lang="ts">
import DashboardLayout from './DashboardLayout.vue'
</script>
