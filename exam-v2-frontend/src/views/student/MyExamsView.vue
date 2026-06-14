<template>
  <div class="my-exams">
    <el-card class="search-card" shadow="never">
      <el-form :inline="true">
        <el-form-item label="关键词">
          <el-input v-model="keyword" placeholder="搜索课程名" clearable @keyup.enter="fetchList"
                    style="width: 240px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchList">搜索</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="list-card" shadow="never">
      <el-table :data="filteredExams" v-loading="loading" stripe style="width:100%">
        <el-table-column prop="examCode" label="编号" width="90" align="center" />
        <el-table-column prop="source" label="科目" width="130" />
        <el-table-column prop="description" label="考试名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="examDate" label="考试日期" width="110" align="center" />
        <el-table-column prop="totalTime" label="时长(分)" width="80" align="center" />
        <el-table-column prop="totalScore" label="总分" width="70" align="center" />
        <el-table-column prop="type" label="类型" width="90" align="center" />
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small"
                       @click="goDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-pagination">
        <el-pagination
            v-model:current-page="page" v-model:page-size="size"
            :total="total" :page-sizes="[5, 10, 20]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="fetchList" @current-change="fetchList"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { listExams, type ExamManage } from '@/api/exam'

const router = useRouter()
const keyword = ref('')
const loading = ref(false)
const allExams = ref<ExamManage[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

const filteredExams = computed(() => {
  if (!keyword.value) return allExams.value
  const kw = keyword.value.toLowerCase()
  return allExams.value.filter(e =>
    e.source?.toLowerCase().includes(kw) ||
    e.description?.toLowerCase().includes(kw))
})

async function fetchList() {
  loading.value = true
  try {
    const res = await listExams('', page.value, size.value)
    if (res.data.code === 0 && res.data.data) {
      allExams.value = res.data.data.records
      total.value = res.data.data.total
    }
  } finally {
    loading.value = false
  }
}

function goDetail(row: ExamManage) {
  router.push(`/student/exam-detail/${row.examCode}`)
}

onMounted(() => fetchList())
</script>

<style scoped>
.my-exams { display: flex; flex-direction: column; gap: 16px; }
.search-card, .list-card { border-radius: 8px; }
.table-pagination { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
