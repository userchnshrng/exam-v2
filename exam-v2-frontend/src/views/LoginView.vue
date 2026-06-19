<template>
  <div class="login-page">
    <!-- 左侧品牌区 -->
    <div class="login-brand">
      <div class="login-brand__inner">
        <div class="brand-logo">
          <span class="brand-icon">📝</span>
        </div>
        <h1 class="brand-title">在线考试系统</h1>
        <p class="brand-subtitle">Online Examination System</p>
        <div class="brand-features">
          <div class="feature-item">📋 智能组卷 · 自动判分</div>
          <div class="feature-item">📊 成绩统计 · 图表分析</div>
          <div class="feature-item">🤖 AI 助手 · 高效办公</div>
        </div>
        <p class="brand-version">v2.0 · 计算机科学与技术 · 毕业设计</p>
      </div>
    </div>

    <!-- 右侧登录区 -->
    <div class="login-form-area">
      <div class="login-form-card">
        <h2 class="form-title">欢迎登录</h2>
        <p class="form-subtitle">请使用您的账号登录系统</p>

        <el-form :model="loginForm" @submit.prevent="handleLogin" class="login-form">
          <el-form-item>
            <el-input v-model="loginForm.username" placeholder="用户名 / 账号"
                      :prefix-icon="User" size="large" clearable />
          </el-form-item>
          <el-form-item>
            <el-input v-model="loginForm.password" type="password" placeholder="密码"
                      :prefix-icon="Lock" size="large" show-password
                      @keyup.enter="handleLogin" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" class="login-btn" :loading="loading"
                       @click="handleLogin" round>
              登 录
            </el-button>
          </el-form-item>
        </el-form>

        <el-divider>快速选择演示账号</el-divider>

        <div class="demo-list">
          <div v-for="acc in demoList" :key="acc.username"
               class="demo-item" @click="fillDemo(acc.username, acc.password)">
            <div class="demo-avatar" :style="{ background: getAvatarBg(acc.role) }">
              {{ getRoleIcon(acc.role) }}
            </div>
            <div class="demo-info">
              <div class="demo-role">
                <el-tag size="small" :type="getTagType(acc.role)" effect="dark">
                  {{ roleLabels[acc.role] }}
                </el-tag>
              </div>
              <div class="demo-name">{{ acc.displayName }}</div>
              <div class="demo-account">账号: {{ acc.username }} / 密码: {{ acc.password }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/auth'
import { getTeacherPublicInfo } from '@/api/teacher'
import { demoAccounts, roleLabels, getHomePath } from '@/constants/auth'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const loginForm = reactive({ username: '', password: '' })

// 响应式演示账号列表（教师卡片 displayName 可被 onMounted 中拉取的真实姓名覆盖）
const demoList = reactive(demoAccounts.map(a => ({ ...a })))

function fillDemo(user: string, pass: string) {
  loginForm.username = user
  loginForm.password = pass
}

function getTagType(role: string) { return role === 'ADMIN' ? 'danger' : role === 'TEACHER' ? 'success' : '' }
function getAvatarBg(role: string) { return role === 'ADMIN' ? '#ef476f' : role === 'TEACHER' ? '#06d6a0' : '#4361ee' }
function getRoleIcon(role: string) { return role === 'ADMIN' ? '👑' : role === 'TEACHER' ? '🎓' : '📚' }

// 页面挂载后拉取教师真实姓名，动态更新演示卡片
onMounted(async () => {
  try {
    const res = await getTeacherPublicInfo('20081001')
    const body = res.data
    if (body.code === 0 && body.data?.teacherName) {
      const teacher = demoList.find(a => a.role === 'TEACHER')
      if (teacher) {
        teacher.displayName = body.data.teacherName + '老师'
      }
    }
  } catch {
    // 接口异常，保持 demoAccounts 默认 displayName
  }
})

async function handleLogin() {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    const res = await login(loginForm)
    const body = res.data

    if (body.code === 0) {
      const { role, tableName, userInfo } = body.data
      const info = userInfo as Record<string, unknown> || {}

      const username = (info.username || info.account || info.sid || info.tid || info.aid || loginForm.username) as string
      const displayName = (info.displayName || info.name || info.sname || info.tname || info.aname || username) as string

      userStore.setUser({
        username,
        displayName,
        role,
        tableName
      })

      ElMessage.success(`登录成功！欢迎，${displayName}`)
      router.push(getHomePath(role))
    } else {
      ElMessage.error(body.message || '账号或密码错误')
    }
  } catch {
    // 网络异常已在 axios 拦截器中提示，此处仅确保 loading 状态复位
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 全屏双栏布局 */
.login-page {
  display: flex; height: 100vh; width: 100vw;
  overflow: hidden;
}

/* ===== 左侧品牌区 ===== */
.login-brand {
  flex: 1; background: linear-gradient(135deg, #1e293b 0%, #334155 50%, #1a2332 100%);
  display: flex; align-items: center; justify-content: center;
  position: relative; overflow: hidden;
}
.login-brand::before {
  content: ''; position: absolute; inset: 0;
  background: radial-gradient(circle at 20% 80%, rgba(67,97,238,0.15) 0%, transparent 50%),
              radial-gradient(circle at 80% 20%, rgba(72,149,239,0.1) 0%, transparent 50%);
}
.login-brand__inner {
  position: relative; z-index: 1; text-align: center; color: #fff;
  padding: 40px;
}
.brand-logo { margin-bottom: 24px; }
.brand-icon { font-size: 64px; }
.brand-title { font-size: 32px; font-weight: 700; margin: 0 0 8px; letter-spacing: 2px; }
.brand-subtitle { font-size: 14px; color: rgba(255,255,255,0.5); margin: 0 0 32px;
  letter-spacing: 4px; text-transform: uppercase; }
.brand-features { display: flex; flex-direction: column; gap: 12px; margin-bottom: 40px; }
.feature-item { font-size: 15px; color: rgba(255,255,255,0.7); }
.brand-version { font-size: 12px; color: rgba(255,255,255,0.3); margin-top: 24px; }

/* ===== 右侧登录区 ===== */
.login-form-area {
  width: 480px; display: flex; align-items: center; justify-content: center;
  background: var(--color-bg); padding: 40px;
}
.login-form-card {
  width: 100%; max-width: 380px;
}
.form-title { font-size: 24px; font-weight: 700; color: var(--color-text-title); margin: 0 0 4px; }
.form-subtitle { font-size: 13px; color: var(--color-text-muted); margin: 0 0 28px; }
.login-form { margin-bottom: 8px; }
.login-btn { width: 100%; height: 44px; font-size: 16px; letter-spacing: 4px; }

/* ===== 演示账号 ===== */
.demo-list { display: flex; flex-direction: column; gap: 8px; }
.demo-item {
  display: flex; align-items: center; gap: 12px;
  padding: 10px; border-radius: var(--radius-md);
  border: 1px solid var(--color-border); cursor: pointer;
  transition: all 0.2s; background: #fff;
}
.demo-item:hover { border-color: var(--color-primary); box-shadow: var(--shadow-sm); transform: translateX(4px); }
.demo-avatar {
  width: 40px; height: 40px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 20px; flex-shrink: 0;
}
.demo-info { flex: 1; min-width: 0; }
.demo-role { margin-bottom: 2px; }
.demo-name { font-size: 14px; font-weight: 600; color: var(--color-text-title); }
.demo-account { font-size: 11px; color: var(--color-text-muted); margin-top: 1px;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

@media (max-width: 768px) {
  .login-brand { display: none; }
  .login-form-area { width: 100%; }
}
</style>
