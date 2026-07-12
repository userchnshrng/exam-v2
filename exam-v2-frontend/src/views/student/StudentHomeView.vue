<template>
  <div class="student-home">
    <!-- ==================== 欢迎横幅 ==================== -->
    <div class="welcome-banner">
      <div class="welcome-left">
        <div class="welcome-avatar">{{ avatarChar }}</div>
        <div class="welcome-text">
          <h1 class="welcome-greeting">{{ greeting }}，{{ displayName }}</h1>
          <p class="welcome-sub">{{ todayStr }} · 保持专注，每天进步一点点 ✨</p>
        </div>
      </div>
      <div class="welcome-right">
        <div class="quick-stat">
          <span class="quick-stat__num">{{ examCount }}</span>
          <span class="quick-stat__label">已完成考试</span>
        </div>
        <div class="quick-stat">
          <span class="quick-stat__num">{{ avgScore }}</span>
          <span class="quick-stat__label">平均分</span>
        </div>
      </div>
    </div>

    <!-- ==================== 公告栏 ==================== -->
    <div class="section">
      <div class="section-header">
        <h2 class="section-title">📢 最新公告</h2>
        <el-button text type="primary" size="small" @click="loadNotices">
          刷新
        </el-button>
      </div>

      <div v-loading="noticeLoading" class="notice-grid">
        <template v-if="notices.length > 0">
          <div
              v-for="(notice, idx) in notices"
              :key="notice.noticeId ?? idx"
              class="notice-card"
              :style="{ animationDelay: `${idx * 0.08}s` }"
          >
            <div class="notice-card__header">
              <span class="notice-card__icon">{{ idx === 0 ? '📌' : '📋' }}</span>
              <span class="notice-card__time">{{ formatDate(notice.createTime) }}</span>
            </div>
            <p class="notice-card__content">{{ notice.content }}</p>
          </div>
        </template>

        <el-empty v-else description="暂无公告" :image-size="80" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { listNotices, type Notice } from '@/api/notice'
import { listScores } from '@/api/score'

const userStore = useUserStore()
const displayName = computed(() => userStore.displayName || '同学')

const avatarChar = computed(() => {
  const name = displayName.value
  return name ? name.charAt(0).toUpperCase() : '学'
})

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '夜深了'
  if (h < 9) return '早上好'
  if (h < 12) return '上午好'
  if (h < 14) return '中午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

const todayStr = computed(() => {
  const d = new Date()
  const weekMap = ['日', '一', '二', '三', '四', '五', '六']
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 星期${weekMap[d.getDay()]}`
})

// ——— 统计数据 ———
const examCount = ref(0)
const avgScore = ref('--')

async function loadStats() {
  try {
    const studentId = userStore.userId
    if (!studentId) return
    const res = await listScores(studentId, 1, 100)
    if (res.data.code === 0 && res.data.data) {
      const records = res.data.data.records
      examCount.value = res.data.data.total
      if (records.length > 0) {
        const avg = records.reduce((sum: number, r: any) => sum + (r.etScore || 0), 0) / records.length
        avgScore.value = Math.round(avg).toString()
      }
    }
  } catch {
    // 静默失败
  }
}

// ——— 公告 ———
const noticeLoading = ref(false)
const notices = ref<Notice[]>([])

async function loadNotices() {
  noticeLoading.value = true
  try {
    const res = await listNotices('', 1, 10)
    if (res.data.code === 0 && res.data.data) {
      notices.value = res.data.data.records
    }
  } catch {
    // 静默
  } finally {
    noticeLoading.value = false
  }
}

function formatDate(dateStr: string): string {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

onMounted(() => {
  loadStats()
  loadNotices()
})
</script>

<style scoped>
.student-home {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* ============================================
   欢迎横幅
   ============================================ */
.welcome-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28px 32px;
  border-radius: 16px;
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 50%, #ede9fe 100%);
  box-shadow: 0 2px 20px rgba(59, 130, 246, 0.08);
  flex-wrap: wrap;
  gap: 20px;
}

.welcome-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.welcome-avatar {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  color: #fff;
  font-size: 22px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.welcome-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.welcome-greeting {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
}
.welcome-sub {
  margin: 0;
  font-size: 13px;
  color: #64748b;
}

.welcome-right {
  display: flex;
  gap: 24px;
}
.quick-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 12px;
  backdrop-filter: blur(4px);
}
.quick-stat__num {
  font-size: 28px;
  font-weight: 800;
  color: #3b82f6;
  line-height: 1;
}
.quick-stat__label {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 4px;
}

/* ============================================
   分区标题
   ============================================ */
.section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.section-title {
  margin: 0;
  font-size: 17px;
  font-weight: 700;
  color: #1e293b;
}

/* ============================================
   公告卡片网格
   ============================================ */
.notice-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 16px;
}

.notice-card {
  padding: 20px 24px;
  border-radius: 14px;
  background: #fff;
  border: 1px solid #f1f5f9;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.04);
  transition: transform 0.2s, box-shadow 0.2s;
  animation: fadeInUp 0.4s ease both;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.notice-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}
.notice-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.notice-card__icon {
  font-size: 18px;
}
.notice-card__time {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 500;
}
.notice-card__content {
  margin: 0;
  font-size: 14px;
  color: #475569;
  line-height: 1.8;
  white-space: pre-wrap;
  word-break: break-word;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(12px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ============================================
   响应式
   ============================================ */
@media (max-width: 768px) {
  .welcome-banner {
    padding: 20px;
  }
  .welcome-right {
    width: 100%;
    justify-content: space-around;
  }
  .notice-grid {
    grid-template-columns: 1fr;
  }
}
</style>
