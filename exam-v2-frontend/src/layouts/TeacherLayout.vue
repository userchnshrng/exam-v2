<template>
  <DashboardLayout
      role="TEACHER"
      title="教师教学后台"
      subtitle="在线考务发布、核心题库维护及智能班级成绩册"
  >
    <router-view />
  </DashboardLayout>
</template>

<script setup lang="ts">
import DashboardLayout from './DashboardLayout.vue'
</script>