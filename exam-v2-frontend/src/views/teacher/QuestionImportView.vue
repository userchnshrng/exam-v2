<template>
  <div class="question-import">
    <el-card shadow="never" class="import-card">
      <template #header><h3>题目导入</h3></template>
      <el-tabs v-model="importType">
        <el-tab-pane label="导入选择题" name="multi">
          <div class="import-desc">
            <p>Excel 列顺序：科目、题目、选项A、选项B、选项C、选项D、正确答案、解析、分值、章节、难度</p>
          </div>
        </el-tab-pane>
        <el-tab-pane label="导入填空题" name="fill">
          <div class="import-desc">
            <p>Excel 列顺序：科目、题目、答案、解析、分值、章节、难度</p>
          </div>
        </el-tab-pane>
        <el-tab-pane label="导入判断题" name="judge">
          <div class="import-desc">
            <p>Excel 列顺序：科目、题目、答案(T/F)、解析、分值、章节、难度</p>
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
import { importQuestions, importFillQuestions, importJudgeQuestions, type ImportResult } from '@/api/importExport'
import type { UploadFile } from 'element-plus'

const importType = ref('multi')
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
    if (importType.value === 'multi') res = await importQuestions(file.value)
    else if (importType.value === 'fill') res = await importFillQuestions(file.value)
    else res = await importJudgeQuestions(file.value)
    if (res.data.code === 0) result.value = res.data.data
    else ElMessage.error(res.data.message || '导入失败')
  } catch { ElMessage.error('导入失败') }
  finally { uploading.value = false }
}
</script>

<style scoped>
.question-import { max-width: 700px; }
.import-card { border-radius: 8px; }
.import-desc { background: #f7f8fa; padding: 12px; border-radius: 6px; margin-bottom: 16px; font-size: 13px; color: #4e5969; }
.import-desc p { margin: 0; }
.import-result { margin-top: 16px; }
.error-list { margin-top: 8px; }
.error-item { color: #f53f3f; font-size: 12px; margin: 4px 0; }
</style>
