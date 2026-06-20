<template>
  <div class="message-center">
    <el-card shadow="never" class="table-card">
      <div class="table-toolbar">
        <el-button type="primary" @click="openCreate">发布留言</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border style="width:100%">
        <el-table-column prop="title" label="标题" width="200" show-overflow-tooltip />
        <el-table-column prop="content" label="内容" min-width="250" show-overflow-tooltip />
        <el-table-column prop="time" label="时间" width="120" align="center" />
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openReplies(row)">查看回复</el-button>
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

    <!-- ===== 发布留言弹窗 ===== -->
    <el-dialog v-model="dialogVisible" title="发布留言" width="500px" :close-on-press-escape="false">
      <el-form :model="form" label-width="60px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">发布</el-button>
      </template>
    </el-dialog>

    <!-- ===== 查看回复弹窗 ===== -->
    <el-dialog v-model="replyVisible" title="留言详情与回复" width="560px" :close-on-press-escape="false">
      <div class="msg-original">
        <strong>{{ currentMsg?.title }}</strong>
        <p>{{ currentMsg?.content }}</p>
        <span class="msg-time">{{ currentMsg?.time }}</span>
      </div>
      <el-divider />
      <div v-if="replies.length" class="reply-list">
        <div v-for="r in replies" :key="r.replayId" class="reply-item">
          <span class="reply-text">{{ r.replay }}</span>
          <span class="reply-time">{{ r.replayTime }}</span>
        </div>
      </div>
      <el-empty v-else description="暂无回复" :image-size="60" />
      <el-divider />
      <div class="reply-form">
        <el-input v-model="replyText" placeholder="写下回复..." @keyup.enter="handleReply" />
        <el-button type="primary" size="small" style="margin-top:8px" @click="handleReply">回复</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listMessages, createMessage, getReplies, createReply, type MessageItem, type ReplyItem } from '@/api/message'

const loading = ref(false)
const tableData = ref<MessageItem[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

async function fetchList() {
  loading.value = true
  try {
    const res = await listMessages(page.value, size.value)
    if (res.data.code === 0 && res.data.data) {
      tableData.value = res.data.data.records
      total.value = res.data.data.total
    }
  } finally {
    loading.value = false
  }
}

// ---- 发布留言 ----
const dialogVisible = ref(false)
const form = reactive({ title: '', content: '' })

function openCreate() {
  form.title = ''; form.content = ''
  dialogVisible.value = true
}

async function handleCreate() {
  if (!form.title || !form.content) { ElMessage.warning('请填写标题和内容'); return }
  const today = new Date().toISOString().slice(0, 10)
  try {
    await createMessage({ id: null, title: form.title, content: form.content, time: today })
    ElMessage.success('发布成功')
    dialogVisible.value = false
    fetchList()
  } catch { ElMessage.error('发布失败') }
}

// ---- 查看回复 ----
const replyVisible = ref(false)
const currentMsg = ref<MessageItem | null>(null)
const replies = ref<ReplyItem[]>([])
const replyText = ref('')

async function openReplies(row: MessageItem) {
  currentMsg.value = row
  replyText.value = ''
  replyVisible.value = true
  try {
    const res = await getReplies(row.id!)
    if (res.data.code === 0) replies.value = res.data.data || []
  } catch { replies.value = [] }
}

async function handleReply() {
  if (!replyText.value.trim() || !currentMsg.value) return
  const today = new Date().toISOString().slice(0, 10)
  try {
    await createReply(currentMsg.value.id!, {
      messageId: currentMsg.value.id!, replayId: 0, replay: replyText.value, replayTime: today
    })
    ElMessage.success('回复成功')
    replyText.value = ''
    openReplies(currentMsg.value)
  } catch { ElMessage.error('回复失败') }
}

onMounted(() => fetchList())
</script>

<style scoped>
.message-center { display: flex; flex-direction: column; gap: 16px; }
.table-card { border-radius: 8px; }
.table-toolbar { margin-bottom: 16px; }
.table-pagination { display: flex; justify-content: flex-end; margin-top: 16px; }

.msg-original { background: #f7f8fa; padding: 12px; border-radius: 8px; }
.msg-original strong { display: block; color: #1d2129; }
.msg-original p { margin: 8px 0; color: #4e5969; }
.msg-time { font-size: 12px; color: #86909c; }

.reply-list { display: flex; flex-direction: column; gap: 8px; }
.reply-item { padding: 8px 12px; background: #f2f7ff; border-radius: 6px; display: flex; justify-content: space-between; align-items: center; }
.reply-text { color: #1d2129; }
.reply-time { font-size: 12px; color: #86909c; }

.reply-form { display: flex; flex-direction: column; }
</style>
