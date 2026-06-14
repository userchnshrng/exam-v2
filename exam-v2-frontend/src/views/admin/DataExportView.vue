<template>
  <div class="data-export">
    <el-card shadow="never" class="export-card">
      <template #header><h3>数据导出</h3></template>
      <el-row :gutter="16">
        <el-col v-for="item in exportItems" :key="item.key" :span="8">
          <el-card shadow="hover" class="export-item" @click="handleExport(item)">
            <el-icon :size="32" color="#3370ff"><component :is="item.icon" /></el-icon>
            <div class="export-item__title">{{ item.label }}</div>
            <div class="export-item__desc">{{ item.desc }}</div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { Document, User, DataAnalysis, Tickets } from '@element-plus/icons-vue'
import { downloadExport } from '@/api/importExport'
import { ref } from 'vue'

const exporting = ref(false)

const exportItems = [
  { key: 'students', label: '学生信息', desc: '导出全部学生基本信息', icon: User },
  { key: 'teachers', label: '教师信息', desc: '导出全部教师基本信息', icon: User },
  { key: 'scores', label: '成绩数据', desc: '导出全部考试成绩记录', icon: DataAnalysis },
  { key: 'exams', label: '考试数据', desc: '导出全部考试安排信息', icon: Tickets },
]

async function handleExport(item: typeof exportItems[0]) {
  if (exporting.value) return
  exporting.value = true
  try {
    await downloadExport(item.key)
    ElMessage.success(`${item.label}导出成功`)
  } catch {
    ElMessage.error('导出失败')
  } finally {
    exporting.value = false
  }
}
</script>

<style scoped>
.data-export { max-width: 900px; }
.export-card { border-radius: 8px; }
.export-item {
  cursor: pointer; text-align: center; padding: 24px 16px;
  border-radius: 8px; transition: all .2s;
  margin-bottom: 16px;
}
.export-item:hover { border-color: #3370ff; transform: translateY(-2px); }
.export-item__title { font-size: 16px; font-weight: 600; color: #1d2129; margin: 12px 0 4px; }
.export-item__desc { font-size: 12px; color: #86909c; }
</style>
