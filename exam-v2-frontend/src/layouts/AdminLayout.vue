<template>
  <DashboardLayout
      role="ADMIN"
      title="管理员控制台"
      subtitle="全校级别师资配置、全站公告发布与系统参数运维"
  >
    <router-view />
  </DashboardLayout>
</template>

<script setup lang="ts">
import DashboardLayout from './DashboardLayout.vue'
</script>