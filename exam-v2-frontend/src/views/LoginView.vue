<template>
  <div class="login-container">
    <el-card class="login-card" shadow="always">
      <div class="login-header">
        <h2>在线考试系统 v2.0</h2>
        <p>第三阶段 · 独立角色后台壳子测试</p>
      </div>

      <el-form :model="loginForm" label-position="top" @submit.prevent="handleLogin">
        <el-form-item label="用户名 / 账号">
          <el-input v-model="loginForm.username" placeholder="请输入任意字符或点击下方测试账号" clearable />
        </el-form-item>
        <el-form-item label="安全密码">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" class="login-submit-btn" :loading="loading" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <el-divider>点击快速填充演示账号</el-divider>

      <div class="demo-accounts">
        <div
            v-for="acc in demoAccounts"
            :key="acc.username"
            class="demo-account-item"
            @click="fillDemo(acc.username, acc.password)"
        >
          <el-tag size="small" :type="getTagType(acc.role)">{{ roleLabels[acc.role] }}</el-tag>
          <span class="demo-user">账号: <strong>{{ acc.username }}</strong>密码: {{ acc.password }}</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { demoAccounts, roleLabels, matchDemoAccount, getHomePath } from '@/constants/auth'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const loginForm = reactive({
  username: '',
  password: ''
})

// 点击下方演示框自动填充账号，方便你随时测试三大角色的切换效果
function fillDemo(user: string, pass: string) {
  loginForm.username = user
  loginForm.password = pass
  ElMessage.info(`已成功填充测试账号数据`)
}

function getTagType(role: string) {
  if (role === 'ADMIN') return 'danger'
  if (role === 'TEACHER') return 'success'
  return 'primary'
}

function handleLogin() {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }

  loading.value = true

  // 匹配内置演示名单
  const matched = matchDemoAccount(loginForm.username, loginForm.password)

  setTimeout(() => {
    loading.value = false
    if (matched) {
      // 成功写入 Pinia 状态树及 localStorage
      userStore.setUser({
        username: matched.username,
        displayName: matched.displayName,
        role: matched.role,
        tableName: matched.tableName
      })
      ElMessage.success(`登录成功！欢迎回来，${matched.displayName}`)

      // 执行登录后的智能化跳转逻辑
      const targetPath = getHomePath(matched.role)
      router.push(targetPath)
    } else {
      ElMessage.error('模拟验证失败，请直接点击下方的浅色演示账号填充测试！')
    }
  }, 600)
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%); /* 渐变高端质感背景 */
}

.login-card {
  width: 400px;
  border-radius: 12px;
}

.login-header {
  text-align: center;
  margin-bottom: 24px;
}

.login-header h2 {
  color: #1f2f3d;
  font-size: 22px;
  margin-bottom: 6px;
}

.login-header p {
  color: #909399;
  font-size: 13px;
}

.login-submit-btn {
  width: 100%;
  height: 38px;
  margin-top: 8px;
}

.demo-accounts {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.demo-account-item {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 6px;
  border-radius: 4px;
  transition: background 0.2s;
}

.demo-account-item:hover {
  background-color: #e9ecef;
}

.demo-user {
  font-size: 12px;
  color: #495057;
}
</style>