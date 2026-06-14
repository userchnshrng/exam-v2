<template>
  <DashboardLayout
    role="STUDENT"
    title="学生后台"
    subtitle="考试、成绩和消息的统一入口"
  >
    <router-view />
  </DashboardLayout>
</template>

<script setup lang="ts">
import DashboardLayout from './DashboardLayout.vue'
</script>
