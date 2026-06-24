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
      <div v-if="!isTeacherRole" class="table-toolbar">
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
        <el-table-column label="操作" :width="isTeacherRole ? 90 : 210" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="success" size="small" @click="openCompose(row)">组卷</el-button>
            <template v-if="!isTeacherRole">
              <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
              <el-popconfirm title="确定删除该考试？" @confirm="handleDelete(row.examCode)">
                <template #reference>
                  <el-button link type="danger" size="small">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="680px" :close-on-click-modal="false" :close-on-press-escape="false">
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

    <!-- ======== 组卷弹窗 ======== -->
    <el-dialog v-model="composeVisible" width="780px" :close-on-click-modal="false" :close-on-press-escape="false"
               @opened="loadComposeData" class="compose-dialog">
      <template #header>
        <div class="compose-header">
          <div class="compose-header__left">
            <span class="compose-header__title">试卷组卷</span>
            <el-tag type="warning" effect="plain" round>{{ composeExam?.source || '未设置课程' }}</el-tag>
            <el-tag v-if="composeExam?.paperId" type="info" effect="plain" round>试卷号 {{ composeExam.paperId }}</el-tag>
          </div>
          <el-button v-if="composeExam?.paperId" type="primary" plain size="small"
                     :loading="autoComposing" @click="handleAutoCompose">
            一键组卷
          </el-button>
        </div>
      </template>

      <div v-if="!composeExam?.paperId" class="compose-no-paper">
        <el-result icon="warning" title="尚未设置试卷编号" sub-title="请在下方填写试卷编号后开始组卷">
          <template #extra>
            <div class="compose-set-paper">
              <el-input-number v-model="newPaperId" :min="1" placeholder="试卷编号" style="width: 180px" />
              <el-button type="primary" :loading="savingPaperId" :disabled="!newPaperId" @click="handleSetPaperId">
                保存并开始组卷
              </el-button>
            </div>
          </template>
        </el-result>
      </div>
      <template v-else>
        <el-tabs v-model="composeTab" class="compose-tabs">
          <el-tab-pane name="1">
            <template #label>
              <span>选择题</span>
              <el-badge :value="composeByType(1).length" :type="composeByType(1).length ? 'primary' : 'info'" class="compose-badge" />
            </template>
          </el-tab-pane>
          <el-tab-pane name="2">
            <template #label>
              <span>填空题</span>
              <el-badge :value="composeByType(2).length" :type="composeByType(2).length ? 'success' : 'info'" class="compose-badge" />
            </template>
          </el-tab-pane>
          <el-tab-pane name="3">
            <template #label>
              <span>判断题</span>
              <el-badge :value="composeByType(3).length" :type="composeByType(3).length ? 'warning' : 'info'" class="compose-badge" />
            </template>
          </el-tab-pane>
        </el-tabs>

        <div class="compose-toolbar">
          <span class="compose-hint">
            已选 <strong>{{ checkedCount(Number(composeTab)) }}</strong> / {{ composeByType(Number(composeTab)).length }} 题
            · 科目匹配「<em>{{ composeExam?.source }}</em>」
          </span>
        </div>

        <div v-if="composeByType(Number(composeTab)).length === 0" class="compose-empty-state">
          <el-empty description="该科目下暂无此类型题目" :image-size="70">
            <template #extra>
              <span class="empty-hint">请在「题库管理」或「题目导入」中先添加「{{ composeExam?.source }}」科目的{{ tabLabel(composeTab) }}</span>
            </template>
          </el-empty>
        </div>
        <div v-else class="compose-list">
          <div v-for="q in composeByType(Number(composeTab))" :key="compKey(q)" class="compose-item"
               :class="{ 'compose-item--checked': isComposeChecked(q) }"
               @click="onToggle(q, !isComposeChecked(q))">
            <div class="compose-item__left">
              <el-checkbox :model-value="isComposeChecked(q)" @change="(checked: boolean) => onToggle(q, checked)" @click.stop />
              <span class="compose-item__question">{{ q.question }}</span>
            </div>
            <div class="compose-item__right">
              <el-tag size="small" effect="plain" round>{{ q.section || '未分类' }}</el-tag>
              <span class="compose-item__score">{{ q.score }}分</span>
              <span class="compose-item__level">{{ q.level || '-' }}</span>
            </div>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { listExams, createExam, updateExam, deleteExam, type ExamManage } from '@/api/exam'
import { getComposeData, addQuestionToPaper, removeQuestionFromPaper, autoCompose, type ComposeQuestion } from '@/api/examPaper'

const route = useRoute()
const isTeacherRole = computed(() => route.path.startsWith('/teacher'))

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

// ---------- 组卷 ----------
const composeVisible = ref(false)
const composeExam = ref<ExamManage | null>(null)
const composeTab = ref('1')
const composeData = ref<ComposeQuestion[]>([])
const composeChecked = ref<string[]>([])
const composeLoading = ref(false)
const autoComposing = ref(false)
const newPaperId = ref<number | null>(null)
const savingPaperId = ref(false)

function compKey(q: ComposeQuestion) { return `${q.questionType}_${q.questionId}` }

function isComposeChecked(q: ComposeQuestion) { return composeChecked.value.includes(compKey(q)) }

function composeByType(type: number) { return composeData.value.filter(q => q.questionType === type) }

function checkedCount(type: number) { return composeByType(type).filter(q => isComposeChecked(q)).length }

function typeLabel(type: number) { return type === 1 ? '选择' : type === 2 ? '填空' : '判断' }

function tabLabel(tab: string) { return tab === '1' ? '选择题' : tab === '2' ? '填空题' : '判断题' }

function openCompose(row: ExamManage) {
  composeExam.value = row
  composeTab.value = '1'
  composeChecked.value = []
  composeData.value = []
  newPaperId.value = null
  composeVisible.value = true
}

async function handleSetPaperId() {
  if (!composeExam.value || !newPaperId.value) return
  savingPaperId.value = true
  try {
    await updateExam(composeExam.value.examCode!, { ...composeExam.value, paperId: newPaperId.value })
    composeExam.value.paperId = newPaperId.value
    ElMessage.success('试卷编号已设置')
    await loadComposeData()
  } catch {
    ElMessage.error('设置失败')
  } finally {
    savingPaperId.value = false
  }
}

async function loadComposeData() {
  if (!composeExam.value?.paperId || !composeExam.value?.source) return
  composeLoading.value = true
  try {
    const res = await getComposeData(composeExam.value.paperId, composeExam.value.source)
    if (res.data.code === 0) {
      composeData.value = res.data.data || []
      composeChecked.value = res.data.data?.filter(q => q.inPaper).map(compKey) || []
    } else {
      ElMessage.error(res.data.message || '获取组卷数据失败')
    }
  } catch {
    ElMessage.error('获取组卷数据失败，请检查网络连接')
  } finally {
    composeLoading.value = false
  }
}

async function handleAutoCompose() {
  if (!composeExam.value?.paperId || !composeExam.value?.source) return
  autoComposing.value = true
  try {
    const res = await autoCompose(composeExam.value.paperId, composeExam.value.source)
    if (res.data.code === 0) {
      ElMessage.success(res.data.data?.message || '组卷完成')
    }
    await loadComposeData()
  } catch {
    ElMessage.error('自动组卷失败')
  } finally {
    autoComposing.value = false
  }
}

async function onToggle(q: ComposeQuestion, checked: boolean) {
  if (!composeExam.value?.paperId) return
  try {
    if (checked) {
      await addQuestionToPaper(composeExam.value.paperId, q.questionType, q.questionId)
      composeChecked.value.push(compKey(q))
    } else {
      await removeQuestionFromPaper(composeExam.value.paperId, q.questionType, q.questionId)
      composeChecked.value = composeChecked.value.filter(k => k !== compKey(q))
    }
  } catch {
    ElMessage.error('操作失败')
    loadComposeData()
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

/* ---- 组卷弹窗 ---- */
.compose-header {
  display: flex; align-items: center; justify-content: space-between; width: 100%;
}
.compose-header__left {
  display: flex; align-items: center; gap: 10px;
}
.compose-header__title {
  font-size: 17px; font-weight: 600; color: #1d2129;
}
.compose-no-paper {
  padding: 20px 0;
}
.compose-set-paper {
  display: flex; align-items: center; gap: 12px; margin-top: 8px;
}
.compose-tabs {
  margin-bottom: 0;
}
.compose-badge {
  margin-left: 6px;
}
.compose-toolbar {
  padding: 10px 0 6px; border-bottom: 1px solid #f0f1f3; margin-bottom: 10px;
}
.compose-hint {
  font-size: 13px; color: #86909c;
}
.compose-hint em {
  font-style: normal; color: #3370ff;
}
.compose-empty-state {
  padding: 30px 0;
}
.empty-hint {
  font-size: 13px; color: #86909c; display: block; margin-top: 4px;
}
.compose-list {
  display: flex; flex-direction: column; gap: 6px;
  max-height: 380px; overflow-y: auto; padding-right: 4px;
}
.compose-item {
  display: flex; align-items: center; justify-content: space-between;
  padding: 10px 14px; border: 1px solid #e5e6eb; border-radius: 8px;
  background: #fff; transition: all .15s; cursor: pointer;
}
.compose-item:hover {
  border-color: #b4c8f0; background: #fafbff;
}
.compose-item--checked {
  border-color: #3370ff; background: #f2f7ff;
}
.compose-item__left {
  display: flex; align-items: center; gap: 10px; flex: 1; min-width: 0;
}
.compose-item__question {
  font-size: 14px; color: #1d2129; line-height: 1.5;
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
}
.compose-item__right {
  display: flex; align-items: center; gap: 10px; flex-shrink: 0; margin-left: 16px;
}
.compose-item__score {
  font-size: 13px; color: #4e5969; font-weight: 500; min-width: 32px; text-align: right;
}
.compose-item__level {
  font-size: 12px; color: #c9cdd4; min-width: 28px; text-align: right;
}
</style>
