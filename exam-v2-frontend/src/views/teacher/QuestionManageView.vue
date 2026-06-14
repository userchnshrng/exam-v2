<template>
  <div class="question-manage">
    <!-- ======== Tab 切换 ======== -->
    <el-card class="tab-card" shadow="never">
      <el-tabs v-model="questionType" @tab-change="handleTabChange">
        <el-tab-pane label="选择题" name="multi" />
        <el-tab-pane label="填空题" name="fill" />
        <el-tab-pane label="判断题" name="judge" />
      </el-tabs>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-bar">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="搜索题目或科目" clearable
                    @keyup.enter="handleSearch" style="width: 260px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作栏 -->
      <div class="table-toolbar">
        <el-button type="primary" @click="openCreate">{{ createBtnText }}</el-button>
      </div>

      <!-- ===== 选择题表格 ===== -->
      <el-table v-show="questionType === 'multi'" :data="tableData" v-loading="loading" stripe border style="width:100%">
        <el-table-column prop="questionId" label="ID" width="80" align="center" />
        <el-table-column prop="subject" label="科目" width="120" />
        <el-table-column prop="question" label="题目" min-width="180" show-overflow-tooltip />
        <el-table-column prop="rightAnswer" label="正确答案" width="90" align="center" />
        <el-table-column prop="score" label="分值" width="60" align="center" />
        <el-table-column prop="section" label="章节" width="100" show-overflow-tooltip />
        <el-table-column prop="level" label="难度" width="60" align="center" />
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除?" @confirm="handleDelete(row.questionId)">
              <template #reference><el-button link type="danger" size="small">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- ===== 填空题表格 ===== -->
      <el-table v-show="questionType === 'fill'" :data="tableData" v-loading="loading" stripe border style="width:100%">
        <el-table-column prop="questionId" label="ID" width="80" align="center" />
        <el-table-column prop="subject" label="科目" width="120" />
        <el-table-column prop="question" label="题目" min-width="200" show-overflow-tooltip />
        <el-table-column prop="answer" label="答案" min-width="120" show-overflow-tooltip />
        <el-table-column prop="score" label="分值" width="60" align="center" />
        <el-table-column prop="section" label="章节" width="100" show-overflow-tooltip />
        <el-table-column prop="level" label="难度" width="60" align="center" />
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除?" @confirm="handleDelete(row.questionId)">
              <template #reference><el-button link type="danger" size="small">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- ===== 判断题表格 ===== -->
      <el-table v-show="questionType === 'judge'" :data="tableData" v-loading="loading" stripe border style="width:100%">
        <el-table-column prop="questionId" label="ID" width="80" align="center" />
        <el-table-column prop="subject" label="科目" width="120" />
        <el-table-column prop="question" label="题目" min-width="240" show-overflow-tooltip />
        <el-table-column prop="answer" label="答案" width="80" align="center" />
        <el-table-column prop="score" label="分值" width="60" align="center" />
        <el-table-column prop="section" label="章节" width="100" show-overflow-tooltip />
        <el-table-column prop="level" label="难度" width="60" align="center" />
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除?" @confirm="handleDelete(row.questionId)">
              <template #reference><el-button link type="danger" size="small">删除</el-button></template>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="660px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <!-- 公共字段 -->
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="科目" prop="subject">
              <el-input v-model="form.subject" placeholder="如：计算机网络" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="分值" prop="score">
              <el-input-number v-model="form.score" :min="1" :max="99" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="难度" prop="level">
              <el-input v-model="form.level" placeholder="1-5" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="题目" prop="question">
          <el-input v-model="form.question" type="textarea" :rows="2" placeholder="请输入题目内容" maxlength="255" />
        </el-form-item>

        <!-- 选择题特有 -->
        <template v-if="questionType === 'multi'">
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="选项A" prop="answerA">
                <el-input v-model="form.answerA" placeholder="选项A" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="选项B" prop="answerB">
                <el-input v-model="form.answerB" placeholder="选项B" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="选项C" prop="answerC">
                <el-input v-model="form.answerC" placeholder="选项C" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="选项D" prop="answerD">
                <el-input v-model="form.answerD" placeholder="选项D" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="正确答案" prop="rightAnswer">
            <el-select v-model="form.rightAnswer" placeholder="请选择正确答案" style="width:200px">
              <el-option label="A" value="A" />
              <el-option label="B" value="B" />
              <el-option label="C" value="C" />
              <el-option label="D" value="D" />
            </el-select>
          </el-form-item>
        </template>

        <!-- 填空/判断共用 -->
        <template v-else>
          <el-form-item v-if="questionType === 'fill'" label="答案" prop="answer">
            <el-input v-model="form.answer" placeholder="请输入正确答案" />
          </el-form-item>
          <el-form-item v-if="questionType === 'judge'" label="答案" prop="answer">
            <el-select v-model="form.answer" placeholder="请选择" style="width:150px">
              <el-option label="正确 (T)" value="T" />
              <el-option label="错误 (F)" value="F" />
            </el-select>
          </el-form-item>
        </template>

        <!-- 公共底部字段 -->
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="章节">
              <el-input v-model="form.section" placeholder="如：应用层" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="解析">
          <el-input v-model="form.analysis" type="textarea" :rows="2" placeholder="题目解析（可选）" maxlength="255" />
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
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  listMulti, createMulti, updateMulti, deleteMulti,
  listFill, createFill, updateFill, deleteFill,
  listJudge, createJudge, updateJudge, deleteJudge,
  type MultiQuestion, type FillQuestion, type JudgeQuestion
} from '@/api/question'

// ---------- Tab & Search ----------
const questionType = ref<'multi' | 'fill' | 'judge'>('multi')
const searchForm = reactive({ keyword: '', page: 1, size: 10 })
const createBtnText = computed(() => {
  if (questionType.value === 'multi') return '新增选择题'
  if (questionType.value === 'fill') return '新增填空题'
  return '新增判断题'
})

// ---------- Table ----------
const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)

async function fetchList() {
  loading.value = true
  try {
    let res: any
    const k = searchForm.keyword; const p = searchForm.page; const s = searchForm.size
    if (questionType.value === 'multi') res = await listMulti(k, p, s)
    else if (questionType.value === 'fill') res = await listFill(k, p, s)
    else res = await listJudge(k, p, s)

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
function handleTabChange() { searchForm.page = 1; fetchList() }

// ---------- Dialog ----------
const dialogVisible = ref(false)
const submitting = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const formRef = ref<FormInstance>()

// 为不同题型提供不同空表单
function emptyMulti(): MultiQuestion {
  return { questionId: null, subject: '', question: '', answerA: '', answerB: '', answerC: '', answerD: '',
    rightAnswer: '', analysis: '', score: 2, section: '', level: '' }
}
function emptyFill(): FillQuestion {
  return { questionId: null, subject: '', question: '', answer: '', analysis: '', score: 2, level: '', section: '' }
}
function emptyJudge(): JudgeQuestion {
  return { questionId: null, subject: '', question: '', answer: '', analysis: '', score: 2, level: '', section: '' }
}

const form = reactive<Record<string, any>>({ ...emptyMulti() })

// 不同题型的表单校验规则
const multiRules: FormRules = {
  subject: [{ required: true, message: '请输入科目', trigger: 'blur' }],
  question: [{ required: true, message: '请输入题目', trigger: 'blur' }],
  answerA: [{ required: true, message: '请输入选项A', trigger: 'blur' }],
  answerB: [{ required: true, message: '请输入选项B', trigger: 'blur' }],
  answerC: [{ required: true, message: '请输入选项C', trigger: 'blur' }],
  answerD: [{ required: true, message: '请输入选项D', trigger: 'blur' }],
  rightAnswer: [{ required: true, message: '请选择正确答案', trigger: 'change' }]
}
const fillRules: FormRules = {
  subject: [{ required: true, message: '请输入科目', trigger: 'blur' }],
  question: [{ required: true, message: '请输入题目', trigger: 'blur' }],
  answer: [{ required: true, message: '请输入答案', trigger: 'blur' }]
}
const judgeRules: FormRules = {
  subject: [{ required: true, message: '请输入科目', trigger: 'blur' }],
  question: [{ required: true, message: '请输入题目', trigger: 'blur' }],
  answer: [{ required: true, message: '请选择答案', trigger: 'change' }]
}
const formRules = computed(() => {
  if (questionType.value === 'multi') return multiRules
  if (questionType.value === 'fill') return fillRules
  return judgeRules
})

const dialogTitle = computed(() => {
  const typeName = questionType.value === 'multi' ? '选择题' : questionType.value === 'fill' ? '填空题' : '判断题'
  return (isEdit.value ? '编辑' : '新增') + typeName
})

function openCreate() {
  isEdit.value = false; editId.value = null
  if (questionType.value === 'multi') Object.assign(form, emptyMulti())
  else if (questionType.value === 'fill') Object.assign(form, emptyFill())
  else Object.assign(form, emptyJudge())
  dialogVisible.value = true
}

function openEdit(row: any) {
  isEdit.value = true; editId.value = row.questionId
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    const data = { ...form }
    if (isEdit.value && editId.value != null) {
      if (questionType.value === 'multi') await updateMulti(editId.value, data as MultiQuestion)
      else if (questionType.value === 'fill') await updateFill(editId.value, data as FillQuestion)
      else await updateJudge(editId.value, data as JudgeQuestion)
      ElMessage.success('更新成功')
    } else {
      if (questionType.value === 'multi') await createMulti(data as MultiQuestion)
      else if (questionType.value === 'fill') await createFill(data as FillQuestion)
      else await createJudge(data as JudgeQuestion)
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
    if (questionType.value === 'multi') await deleteMulti(id)
    else if (questionType.value === 'fill') await deleteFill(id)
    else await deleteJudge(id)
    ElMessage.success('删除成功')
    fetchList()
  } catch {
    ElMessage.error('删除失败')
  }
}

// 切换 tab 时重置表单校验
watch(questionType, () => {
  formRef.value?.clearValidate()
  Object.assign(form, emptyMulti())
})

import { onMounted } from 'vue'
onMounted(() => fetchList())
</script>

<style scoped>
.question-manage { display: flex; flex-direction: column; gap: 16px; }
.tab-card { border-radius: 8px; }
.search-bar { margin-top: 16px; }
.table-toolbar { margin-bottom: 16px; }
.table-pagination { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
