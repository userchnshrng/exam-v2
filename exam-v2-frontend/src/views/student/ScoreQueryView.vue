<template>
  <div class="score-query">
    <el-card shadow="never" class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe border style="width:100%">
        <el-table-column prop="subject" label="科目" width="140" />
        <el-table-column prop="etScore" label="得分" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.etScore >= 60 ? 'success' : 'danger'">{{ row.etScore ?? '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="answerDate" label="答题日期" width="120" align="center" />
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
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { listScores, type ScoreRecord } from '@/api/score'

const userStore = useUserStore()
const loading = ref(false)
const tableData = ref<ScoreRecord[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

async function fetchList() {
  loading.value = true
  try {
    const studentId = userStore.userId
    if (!studentId) {
      ElMessage.error('无法获取学生信息，请重新登录')
      loading.value = false
      return
    }
    const res = await listScores(studentId, page.value, size.value)
    if (res.data.code === 0 && res.data.data) {
      tableData.value = res.data.data.records
      total.value = res.data.data.total
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => fetchList())
</script>

<style scoped>
.score-query { display: flex; flex-direction: column; gap: 16px; }
.table-card { border-radius: 8px; }
.table-pagination { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
