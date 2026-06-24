<template>
  <div class="data-import">
    <el-card shadow="never" class="import-card">
      <template #header><h3>数据导入</h3></template>
      <el-tabs v-model="importType">
        <el-tab-pane label="导入学生" name="students">
          <div class="import-desc">
            <p>Excel 列顺序：姓名、年级、专业、班级、学院、电话、邮箱、密码、身份证号、性别</p>
          </div>
        </el-tab-pane>
        <el-tab-pane label="导入教师" name="teachers">
          <div class="import-desc">
            <p>Excel 列顺序：姓名、学院、性别、电话、邮箱、密码、身份证号、职称</p>
          </div>
        </el-tab-pane>
        <el-tab-pane label="导入考试" name="exams">
          <div class="import-desc">
            <p>Excel 列顺序：描述、科目、试卷ID、考试日期、时长(分)、年级、学期、专业、学院、总分、类型、须知</p>
          </div>
        </el-tab-pane>
      </el-tabs>

      <el-upload
          :auto-upload="false"
          :limit="1"
          accept=".xlsx,.xls"
          :on-change="handleFileChange"
          :file-list="fileList"
          drag
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">将 Excel 文件拖到此处，或<em>点击上传</em></div>
      </el-upload>

      <el-button type="primary" :loading="uploading" :disabled="!file"
                 @click="handleImport" style="margin-top:16px;width:100%">
        开始导入
      </el-button>

      <!-- 结果 -->
      <div v-if="result" class="import-result">
        <el-alert :title="`导入完成：成功 ${result.success} 条，失败 ${result.fail} 条，共 ${result.total} 条`"
                  :type="result.fail > 0 ? 'warning' : 'success'" :closable="false" show-icon />
        <div v-if="result.errors?.length" class="error-list">
          <p v-for="(e, i) in result.errors" :key="i" class="error-item">{{ e }}</p>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { importStudents, importTeachers, importExams, type ImportResult } from '@/api/importExport'
import type { UploadFile } from 'element-plus'

const importType = ref('students')
const file = ref<File | null>(null)
const fileList = ref<UploadFile[]>([])
const uploading = ref(false)
const result = ref<ImportResult | null>(null)

function handleFileChange(uploadFile: UploadFile) {
  file.value = uploadFile.raw || null
  result.value = null
}

async function handleImport() {
  if (!file.value) { ElMessage.warning('请先选择文件'); return }
  uploading.value = true
  result.value = null
  try {
    let res: any
    if (importType.value === 'students') res = await importStudents(file.value)
    else if (importType.value === 'teachers') res = await importTeachers(file.value)
    else res = await importExams(file.value)
    if (res.data.code === 0) result.value = res.data.data
    else ElMessage.error(res.data.message || '导入失败')
  } catch { ElMessage.error('导入失败') }
  finally { uploading.value = false }
}
</script>

<style scoped>
.data-import { max-width: 700px; }
.import-card { border-radius: 8px; }
.import-desc { background: #f7f8fa; padding: 12px; border-radius: 6px; margin-bottom: 16px; font-size: 13px; color: #4e5969; }
.import-desc p { margin: 0; }
.import-result { margin-top: 16px; }
.error-list { margin-top: 8px; }
.error-item { color: #f53f3f; font-size: 12px; margin: 4px 0; }
</style>
