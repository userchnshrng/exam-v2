<template>
  <DashboardLayout
    role="TEACHER"
    title="教师后台"
    subtitle="考试、题库、学生和成绩的日常管理入口"
  >
    <router-view />
  </DashboardLayout>
</template>

<script setup lang="ts">
import DashboardLayout from './DashboardLayout.vue'
</script>
