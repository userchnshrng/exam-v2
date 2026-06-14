<template>
  <div class="statistics">
    <el-row :gutter="16">
      <!-- ====== 成绩分布饼图 ====== -->
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header><span>成绩分布</span></template>
          <v-chart :option="pieOption" style="height:350px" v-if="pieOption" />
          <el-empty v-else description="暂无数据" :image-size="80" />
          <div class="chart-summary">
            <el-tag>总人次：{{ distribution?.totalCount || 0 }}</el-tag>
            <el-tag type="success">平均分：{{ distribution?.average || 0 }}</el-tag>
          </div>
        </el-card>
      </el-col>

      <!-- ====== 各考试对比柱状图 ====== -->
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header><span>各考试平均分对比</span></template>
          <v-chart :option="barOption" style="height:350px" v-if="barOption" />
          <el-empty v-else description="暂无数据" :image-size="80" />
        </el-card>
      </el-col>
    </el-row>

    <!-- ====== 按考试查学生成绩 ====== -->
    <el-card shadow="never" class="chart-card" style="margin-top:16px">
      <template #header>
        <span>学生成绩明细</span>
        <el-select v-model="selectedExam" placeholder="选择考试" @change="loadStudentScores"
                   style="width:280px;margin-left:16px" clearable>
          <el-option v-for="e in examOptions" :key="e.examCode"
                     :label="e.examName" :value="e.examCode" />
        </el-select>
      </template>
      <el-table :data="studentScores" stripe border v-loading="studentLoading" max-height="400">
        <el-table-column prop="studentId" label="学号" width="100" align="center" />
        <el-table-column prop="subject" label="科目" width="150" />
        <el-table-column prop="score" label="得分" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.score >= 60 ? 'success' : 'danger'">{{ row.score }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="date" label="日期" width="120" align="center" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { PieChart, BarChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import {
  getScoreDistribution, getExamComparison, getExamStudentScores, getExamOptions,
  type ScoreDistribution, type ExamComparisonItem, type StudentScoreItem, type ExamOption
} from '@/api/statistics'

use([PieChart, BarChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent, CanvasRenderer])

const distribution = ref<ScoreDistribution | null>(null)
const examComparison = ref<ExamComparisonItem[]>([])
const studentScores = ref<StudentScoreItem[]>([])
const examOptions = ref<ExamOption[]>([])
const selectedExam = ref<number | null>(null)
const studentLoading = ref(false)

const pieOption = ref<any>(null)
const barOption = ref<any>(null)

async function loadDistribution() {
  try {
    const res = await getScoreDistribution()
    if (res.data.code === 0) {
      distribution.value = res.data.data
      const d = res.data.data.distribution
      pieOption.value = {
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [{
          type: 'pie', radius: ['30%', '60%'], center: ['50%', '45%'],
          data: d, label: { formatter: '{b}\n{c}人' },
          itemStyle: { borderRadius: 4 }
        }]
      }
    }
  } catch { /* ignore */ }
}

async function loadExamComparison() {
  try {
    const res = await getExamComparison()
    if (res.data.code === 0) {
      examComparison.value = res.data.data || []
      if (examComparison.value.length > 0) {
        barOption.value = {
          tooltip: { trigger: 'axis' },
          legend: { data: ['平均分', '最高分', '最低分'] },
          grid: { left: 100, right: 20, bottom: 60 },
          xAxis: { type: 'category', data: examComparison.value.map(e => e.examName),
                   axisLabel: { rotate: 30, fontSize: 10 } },
          yAxis: { type: 'value', max: 100 },
          series: [
            { name: '平均分', type: 'bar', data: examComparison.value.map(e => e.avgScore), itemStyle: { color: '#3370ff' } },
            { name: '最高分', type: 'bar', data: examComparison.value.map(e => e.maxScore), itemStyle: { color: '#00b42a' } },
            { name: '最低分', type: 'bar', data: examComparison.value.map(e => e.minScore), itemStyle: { color: '#f53f3f' } }
          ]
        }
      }
    }
  } catch { /* ignore */ }
}

async function loadExamOptions() {
  try {
    const res = await getExamOptions()
    if (res.data.code === 0) examOptions.value = res.data.data || []
  } catch { /* ignore */ }
}

async function loadStudentScores() {
  if (!selectedExam.value) { studentScores.value = []; return }
  studentLoading.value = true
  try {
    const res = await getExamStudentScores(selectedExam.value)
    if (res.data.code === 0) studentScores.value = res.data.data || []
  } finally { studentLoading.value = false }
}

onMounted(() => {
  loadDistribution()
  loadExamComparison()
  loadExamOptions()
})
</script>

<style scoped>
.statistics { display: flex; flex-direction: column; }
.chart-card { border-radius: 8px; }
.chart-summary { display: flex; gap: 8px; justify-content: center; margin-top: 8px; }
</style>
