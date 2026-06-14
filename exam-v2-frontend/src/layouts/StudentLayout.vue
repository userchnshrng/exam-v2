<template>
  <DashboardLayout
      role="STUDENT"
      title="学生学业工作台"
      subtitle="在线实时考场、历次考试成绩单及消息中心入口"
  >
    <router-view />
  </DashboardLayout>
</template>

<script setup lang="ts">
import DashboardLayout from './DashboardLayout.vue'
</script>