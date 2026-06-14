<template>
  <div class="ai-chat">
    <!-- 顶部 -->
    <div class="chat-header">
      <span class="chat-title">🤖 AI 智能助手</span>
      <el-tag size="small" type="info">{{ provider === 'mock' ? '演示模式' : 'DeepSeek' }}</el-tag>
    </div>

    <!-- 消息列表 -->
    <div class="chat-messages" ref="msgContainer">
      <div v-if="messages.length === 0" class="chat-empty">
        <div class="empty-icon">🤖</div>
        <p>你好！我是考试系统智能助手</p>
        <p class="empty-hint">你可以问我关于系统功能的任何问题</p>
        <div class="quick-actions">
          <el-button size="small" v-for="q in quickQuestions" :key="q"
                     @click="sendQuick(q)">{{ q }}</el-button>
        </div>
      </div>

      <div v-for="(msg, idx) in messages" :key="idx"
           :class="['chat-msg', msg.role === 'user' ? 'chat-msg--user' : 'chat-msg--assistant']">
        <div class="chat-msg__avatar">
          {{ msg.role === 'user' ? '👤' : '🤖' }}
        </div>
        <div class="chat-msg__bubble">
          <div class="chat-msg__text">{{ msg.content }}</div>
          <div class="chat-msg__time">{{ formatTime(msg.time) }}</div>
        </div>
      </div>

      <div v-if="loading" class="chat-msg chat-msg--assistant">
        <div class="chat-msg__avatar">🤖</div>
        <div class="chat-msg__bubble typing">思考中...</div>
      </div>
    </div>

    <!-- 输入区 -->
    <div class="chat-input">
      <el-input v-model="inputText" placeholder="输入你的问题..." @keyup.enter="sendMessage"
                :disabled="loading" maxlength="500" show-word-limit />
      <el-button type="primary" :disabled="!inputText.trim() || loading"
                 @click="sendMessage">发送</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted } from 'vue'
import { sendChatMessage, type ChatMessage } from '@/api/aiChat'

interface UIMessage {
  role: 'user' | 'assistant'
  content: string
  time: number
}

const messages = ref<UIMessage[]>([])
const inputText = ref('')
const loading = ref(false)
const provider = ref('mock')
const msgContainer = ref<HTMLElement>()

const quickQuestions = ['系统有哪些功能？', '怎么查看成绩？', '怎么管理题库？']

function sendQuick(q: string) {
  inputText.value = q
  sendMessage()
}

async function sendMessage() {
  const text = inputText.value.trim()
  if (!text || loading.value) return

  messages.value.push({ role: 'user', content: text, time: Date.now() })
  inputText.value = ''
  loading.value = true
  await scrollToBottom()

  try {
    const history: ChatMessage[] = messages.value.map(m => ({
      role: m.role, content: m.content
    }))
    const res = await sendChatMessage({ message: text, history })
    if (res.data.code === 0 && res.data.data) {
      messages.value.push({
        role: 'assistant',
        content: res.data.data.reply,
        time: Date.now()
      })
      provider.value = res.data.data.provider
    }
  } catch {
    messages.value.push({
      role: 'assistant',
      content: '抱歉，AI 服务暂时不可用，请稍后再试。',
      time: Date.now()
    })
  } finally {
    loading.value = false
    await scrollToBottom()
  }
}

function formatTime(ts: number) {
  return new Date(ts).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

async function scrollToBottom() {
  await nextTick()
  if (msgContainer.value) {
    msgContainer.value.scrollTop = msgContainer.value.scrollHeight
  }
}

onMounted(() => scrollToBottom())
</script>

<style scoped>
.ai-chat {
  display: flex; flex-direction: column; height: calc(100vh - 160px);
  background: #fff; border-radius: 12px; overflow: hidden;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
}
.chat-header {
  padding: 16px 20px; border-bottom: 1px solid #f0f2f5;
  display: flex; align-items: center; justify-content: space-between;
}
.chat-title { font-size: 16px; font-weight: 600; }

.chat-messages {
  flex: 1; overflow-y: auto; padding: 16px 20px;
  display: flex; flex-direction: column; gap: 12px;
}
.chat-empty { text-align: center; padding: 40px 20px; }
.empty-icon { font-size: 48px; margin-bottom: 12px; }
.empty-hint { color: #86909c; font-size: 13px; }
.quick-actions { display: flex; gap: 8px; justify-content: center; margin-top: 16px; flex-wrap: wrap; }

.chat-msg { display: flex; gap: 10px; max-width: 85%; }
.chat-msg--user { align-self: flex-end; flex-direction: row-reverse; }
.chat-msg--assistant { align-self: flex-start; }
.chat-msg__avatar { font-size: 28px; flex-shrink: 0; width: 36px; text-align: center; }
.chat-msg__bubble {
  background: #f2f7ff; padding: 10px 14px; border-radius: 12px;
  border-top-left-radius: 2px;
}
.chat-msg--user .chat-msg__bubble {
  background: #3370ff; color: #fff; border-radius: 12px; border-top-right-radius: 2px;
}
.chat-msg__text { font-size: 14px; line-height: 1.6; white-space: pre-wrap; }
.chat-msg__time { font-size: 11px; color: #86909c; margin-top: 4px; }
.chat-msg--user .chat-msg__time { color: rgba(255,255,255,0.7); }
.typing { color: #86909c; font-style: italic; }

.chat-input {
  padding: 12px 16px; border-top: 1px solid #f0f2f5;
  display: flex; gap: 8px; align-items: flex-end;
}
</style>
