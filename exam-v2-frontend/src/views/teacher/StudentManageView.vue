<template>
  <div class="student-manage">
    <!-- ======== 搜索栏 ======== -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="搜索姓名/专业/学院" clearable
                    @keyup.enter="handleSearch" style="width: 260px" />
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
        <el-button type="primary" @click="openCreate">新增学生</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%">
        <el-table-column prop="studentId" label="学号" width="100" align="center" />
        <el-table-column prop="studentName" label="姓名" width="90" />
        <el-table-column prop="sex" label="性别" width="60" align="center" />
        <el-table-column prop="grade" label="年级" width="70" align="center" />
        <el-table-column prop="major" label="专业" min-width="150" show-overflow-tooltip />
        <el-table-column prop="clazz" label="班级" width="70" align="center" />
        <el-table-column prop="institute" label="学院" width="140" show-overflow-tooltip />
        <el-table-column prop="tel" label="电话" width="130" />
        <el-table-column v-if="!isTeacherRole" label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除该学生？" @confirm="handleDelete(row.studentId)">
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="640px" :close-on-click-modal="false" :close-on-press-escape="false">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="姓名" prop="studentName">
              <el-input v-model="form.studentName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="sex">
              <el-select v-model="form.sex" placeholder="请选择" style="width: 100%">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="年级" prop="grade">
              <el-input v-model="form.grade" placeholder="如：2015" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="专业" prop="major">
              <el-input v-model="form.major" placeholder="请输入专业" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="班级" prop="clazz">
              <el-input v-model="form.clazz" placeholder="请输入班级" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="学院" prop="institute">
              <el-input v-model="form.institute" placeholder="请输入学院" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电话" prop="tel">
              <el-input v-model="form.tel" placeholder="请输入电话" maxlength="11" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="cardId">
              <el-input v-model="form.cardId" placeholder="请输入身份证号" maxlength="18" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="密码" :prop="isEdit ? '' : 'pwd'">
              <el-input v-model="form.pwd" type="password" show-password
                        :placeholder="isEdit ? '留空则不修改密码' : '请设置密码'" />
            </el-form-item>
          </el-col>
        </el-row>
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
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { listStudents, createStudent, updateStudent, deleteStudent, type Student } from '@/api/student'

const route = useRoute()
const isTeacherRole = computed(() => route.path.startsWith('/teacher'))

const searchForm = reactive({ keyword: '', page: 1, size: 10 })

const loading = ref(false)
const tableData = ref<Student[]>([])
const total = ref(0)

async function fetchList() {
  loading.value = true
  try {
    const res = await listStudents(searchForm.keyword, searchForm.page, searchForm.size)
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

const emptyForm = (): Student => ({
  studentId: null, studentName: '', grade: '', major: '', clazz: '',
  institute: '', tel: '', email: '', pwd: '', cardId: '', sex: '', role: '2'
})
const form = reactive<Student>(emptyForm())

const formRules: FormRules = {
  studentName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  sex: [{ required: true, message: '请选择性别', trigger: 'change' }],
  pwd: [{ required: true, message: '请设置密码', trigger: 'blur' }]
}

const dialogTitle = computed(() => isEdit.value ? '编辑学生' : '新增学生')

function openCreate() {
  isEdit.value = false; editId.value = null
  Object.assign(form, emptyForm())
  dialogVisible.value = true
}

function openEdit(row: Student) {
  isEdit.value = true; editId.value = row.studentId
  Object.assign(form, { ...row, pwd: '' })
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value && editId.value != null) {
      await updateStudent(editId.value, { ...form })
      ElMessage.success('更新成功')
    } else {
      await createStudent({ ...form })
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
    await deleteStudent(id)
    ElMessage.success('删除成功')
    fetchList()
  } catch {
    ElMessage.error('删除失败')
  }
}

onMounted(() => fetchList())
</script>

<style scoped>
.student-manage {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-card, .table-card {
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
