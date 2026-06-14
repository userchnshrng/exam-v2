<template>
  <el-card class="placeholder-card" shadow="never">
    <div class="placeholder-card__header">
      <div class="placeholder-card__info">
        <div class="placeholder-card__title">{{ pageTitle }}</div>
        <div class="placeholder-card__desc">{{ pageDesc }}</div>
      </div>
      <el-tag type="primary" effect="light" round>重构阶段占位</el-tag>
    </div>

    <el-divider />

    <el-empty description="此版块已成功挂载基础布局框架与权限守卫。后续阶段将在当前位置直接切入真实接口业务。">
      <template #extra>
        <el-button type="primary" plain>开发进度指南</el-button>
      </template>
    </el-empty>
  </el-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

// 智能动态读取：用户点击任何菜单，都会根据路由 meta 信息展示对应名称，不需要建几十个空文件！
const pageTitle = computed(() => String(route.meta.title || '功能开发中'))
const pageDesc = computed(() => String(route.meta.description || '当前阶段骨架已就绪，正在准备接入真实接口业务。'))
</script>

<style scoped>
.placeholder-card {
  border-radius: 12px;
  border: 1px solid #e6e8eb;
  padding: 8px;
  background-color: #ffffff;
}

.placeholder-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.placeholder-card__title {
  font-size: 20px;
  font-weight: 600;
  color: #1d2129;
  margin-bottom: 6px;
}

.placeholder-card__desc {
  font-size: 13px;
  color: #86909c;
}

:deep(.el-empty) {
  padding: 80px 0;
}
</style>