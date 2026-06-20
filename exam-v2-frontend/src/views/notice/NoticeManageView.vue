<template>
  <div class="notice-manage">
    <!-- ======== 搜索栏 ======== -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="搜索公告内容" clearable
                    @keyup.enter="handleSearch" style="width: 280px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- ======== 操作栏 ======== -->
    <el-card class="table-card" shadow="never">
      <div class="table-toolbar">
        <el-button type="primary" @click="openCreate">新增公告</el-button>
      </div>

      <!-- ======== 数据表格 ======== -->
      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%">
        <el-table-column prop="noticeId" label="ID" width="80" align="center" />
        <el-table-column prop="content" label="公告内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="createTime" label="发布时间" width="160" align="center" />
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除这条公告？" @confirm="handleDelete(row.noticeId)">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- ======== 分页 ======== -->
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" :close-on-click-modal="false" :close-on-press-escape="false">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="公告内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="5"
                    placeholder="请输入公告内容" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="发布时间" prop="createTime">
          <el-date-picker v-model="form.createTime" type="date" placeholder="选择日期"
                          format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 100%" />
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
import { listNotices, createNotice, updateNotice, deleteNotice, type Notice } from '@/api/notice'

// ---------- 搜索 ----------
const searchForm = reactive({ keyword: '', page: 1, size: 10 })

// ---------- 表格 ----------
const loading = ref(false)
const tableData = ref<Notice[]>([])
const total = ref(0)

async function fetchList() {
  loading.value = true
  try {
    const res = await listNotices(searchForm.keyword, searchForm.page, searchForm.size)
    const body = res.data
    if (body.code === 0 && body.data) {
      tableData.value = body.data.records
      total.value = body.data.total
    }
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  searchForm.page = 1
  fetchList()
}

function handleReset() {
  searchForm.keyword = ''
  searchForm.page = 1
  fetchList()
}

// ---------- 弹窗表单 ----------
const dialogVisible = ref(false)
const submitting = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const formRef = ref<FormInstance>()

const emptyForm = (): Notice => ({ noticeId: null, content: '', createTime: '' })
const form = reactive<Notice>(emptyForm())

const formRules: FormRules = {
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }],
  createTime: [{ required: true, message: '请选择发布时间', trigger: 'change' }]
}

const dialogTitle = computed(() => isEdit.value ? '编辑公告' : '新增公告')

function openCreate() {
  isEdit.value = false
  editId.value = null
  Object.assign(form, emptyForm())
  dialogVisible.value = true
}

function openEdit(row: Notice) {
  isEdit.value = true
  editId.value = row.noticeId
  Object.assign(form, { noticeId: row.noticeId, content: row.content, createTime: row.createTime })
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (isEdit.value && editId.value != null) {
      await updateNotice(editId.value, { ...form })
      ElMessage.success('更新成功')
    } else {
      await createNotice({ ...form })
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

// ---------- 删除 ----------
async function handleDelete(id: number) {
  try {
    await deleteNotice(id)
    ElMessage.success('删除成功')
    fetchList()
  } catch {
    ElMessage.error('删除失败')
  }
}

// ---------- 初始化 ----------
onMounted(() => fetchList())
</script>

<style scoped>
.notice-manage {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-card {
  border-radius: 8px;
}

.table-card {
  border-radius: 8px;
}

.table-toolbar {
  margin-bottom: 16px;
}

.table-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
