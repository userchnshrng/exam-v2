<template>
  <div class="exam-manage">
    <!-- ======== 搜索栏 ======== -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="搜索课程名/描述/专业" clearable
                    @keyup.enter="handleSearch" style="width: 280px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- ======== 表格 ======== -->
    <el-card class="table-card" shadow="never">
      <div class="table-toolbar">
        <el-button type="primary" @click="openCreate">新增考试</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%">
        <el-table-column prop="examCode" label="编号" width="100" align="center" />
        <el-table-column prop="source" label="课程" width="130" show-overflow-tooltip />
        <el-table-column prop="description" label="考试名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="paperId" label="试卷号" width="80" align="center" />
        <el-table-column prop="examDate" label="考试日期" width="110" align="center" />
        <el-table-column prop="totalTime" label="时长(分)" width="80" align="center" />
        <el-table-column prop="type" label="类型" width="90" align="center" />
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除该考试？" @confirm="handleDelete(row.examCode)">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-pagination">
        <el-pagination
            v-model:current-page="searchForm.page"
            v-model:page-size="searchForm.size"
            :total="total"
            :page-sizes="[5, 10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSearch"
            @current-change="handleSearch"
        />
      </div>
    </el-card>

    <!-- ======== 新增 / 编辑 弹窗 ======== -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="680px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="课程名称" prop="source">
              <el-input v-model="form.source" placeholder="如：计算机网络" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="考试名称" prop="description">
              <el-input v-model="form.description" placeholder="如：期末/期中考试" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="试卷编号" prop="paperId">
              <el-input-number v-model="form.paperId" :min="1" placeholder="如：1001" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="考试日期" prop="examDate">
              <el-date-picker v-model="form.examDate" type="date" placeholder="选择日期"
                              format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="时长(分)" prop="totalTime">
              <el-input-number v-model="form.totalTime" :min="1" :max="300" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="年级" prop="grade">
              <el-input v-model="form.grade" placeholder="如：2018" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="学期" prop="term">
              <el-input v-model="form.term" placeholder="如：1" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="类型" prop="type">
              <el-input v-model="form.type" placeholder="如：期末考试" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="专业" prop="major">
              <el-input v-model="form.major" placeholder="请输入专业" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学院" prop="institute">
              <el-input v-model="form.institute" placeholder="请输入学院" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="总分" prop="totalScore">
              <el-input-number v-model="form.totalScore" :min="1" :max="999" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="考生须知" prop="tips">
          <el-input v-model="form.tips" type="textarea" :rows="3"
                    placeholder="请输入考生须知" maxlength="255" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { listExams, createExam, updateExam, deleteExam, type ExamManage } from '@/api/exam'

const searchForm = reactive({ keyword: '', page: 1, size: 10 })

const loading = ref(false)
const tableData = ref<ExamManage[]>([])
const total = ref(0)

async function fetchList() {
  loading.value = true
  try {
    const res = await listExams(searchForm.keyword, searchForm.page, searchForm.size)
    if (res.data.code === 0 && res.data.data) {
      tableData.value = res.data.data.records
      total.value = res.data.data.total
    }
  } finally {
    loading.value = false
  }
}

function handleSearch() { searchForm.page = 1; fetchList() }
function handleReset() { searchForm.keyword = ''; searchForm.page = 1; fetchList() }

// ---------- 弹窗 ----------
const dialogVisible = ref(false)
const submitting = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const formRef = ref<FormInstance>()

const emptyForm = (): ExamManage => ({
  examCode: null, description: '', source: '', paperId: null,
  examDate: '', totalTime: 120, grade: '', term: '', major: '',
  institute: '', totalScore: 100, type: '', tips: ''
})
const form = reactive<ExamManage>(emptyForm())

const formRules: FormRules = {
  source: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入考试名称', trigger: 'blur' }],
  examDate: [{ required: true, message: '请选择考试日期', trigger: 'change' }],
  totalTime: [{ required: true, message: '请输入时长', trigger: 'blur' }]
}

const dialogTitle = computed(() => isEdit.value ? '编辑考试' : '新增考试')

function openCreate() {
  isEdit.value = false; editId.value = null
  Object.assign(form, emptyForm())
  dialogVisible.value = true
}

function openEdit(row: ExamManage) {
  isEdit.value = true; editId.value = row.examCode
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value && editId.value != null) {
      await updateExam(editId.value, { ...form })
      ElMessage.success('更新成功')
    } else {
      await createExam({ ...form })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchList()
  } catch {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

async function handleDelete(id: number) {
  try {
    await deleteExam(id)
    ElMessage.success('删除成功')
    fetchList()
  } catch {
    ElMessage.error('删除失败')
  }
}

onMounted(() => fetchList())
</script>

<style scoped>
.exam-manage {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-card, .table-card { border-radius: 8px; }
.table-toolbar { margin-bottom: 16px; }
.table-pagination { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
