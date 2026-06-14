<template>
  <div class="exam-detail">
    <el-card v-loading="loading" shadow="never" class="detail-card">
      <template #header>
        <el-button link @click="router.back()">
          <el-icon><ArrowLeft /></el-icon> 返回列表
        </el-button>
      </template>

      <div v-if="exam" class="detail-body">
        <h2>{{ exam.description }}</h2>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="科目">{{ exam.source }}</el-descriptions-item>
          <el-descriptions-item label="考试类型">{{ exam.type }}</el-descriptions-item>
          <el-descriptions-item label="考试日期">{{ exam.examDate }}</el-descriptions-item>
          <el-descriptions-item label="考试时长">{{ exam.totalTime }} 分钟</el-descriptions-item>
          <el-descriptions-item label="总分">{{ exam.totalScore }} 分</el-descriptions-item>
          <el-descriptions-item label="年级">{{ exam.grade }} 级</el-descriptions-item>
          <el-descriptions-item label="专业">{{ exam.major }}</el-descriptions-item>
          <el-descriptions-item label="学院">{{ exam.institute }}</el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <el-alert title="考生须知" type="warning" :closable="false" show-icon>
          <p style="margin: 8px 0; white-space: pre-wrap;">{{ exam.tips || '请认真答题，考试开始后倒计时不会停止。' }}</p>
        </el-alert>

        <div class="action-bar">
          <el-button size="large" type="primary" @click="startExam">
            开始考试
          </el-button>
        </div>
      </div>

      <el-empty v-else description="考试不存在或已失效" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getExamById, type ExamManage } from '@/api/exam'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const exam = ref<ExamManage | null>(null)
const examCode = Number(route.params.examCode)

async function fetchDetail() {
  loading.value = true
  try {
    const res = await getExamById(examCode)
    if (res.data.code === 0) exam.value = res.data.data
  } finally {
    loading.value = false
  }
}

function startExam() {
  router.push(`/exam/taking/${examCode}`)
}

onMounted(() => fetchDetail())
</script>

<style scoped>
.exam-detail { padding: 0; }
.detail-card { border-radius: 8px; max-width: 800px; }
.detail-body h2 { margin: 0 0 20px; color: #1d2129; }
.action-bar { margin-top: 24px; text-align: center; }
</style>
