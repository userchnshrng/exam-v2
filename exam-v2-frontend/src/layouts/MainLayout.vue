<template>
  <el-container class="layout">
    <el-header class="layout__header">
      <div>在线考试系统</div>
      <el-button size="small" @click="logout">退出登录</el-button>
    </el-header>

    <el-container>
      <el-aside class="layout__aside" width="220px">
        <el-menu :default-active="activePath" router>
          <el-menu-item index="/admin/home">管理员首页</el-menu-item>
          <el-menu-item index="/teacher/home">教师首页</el-menu-item>
          <el-menu-item index="/student/home">学生首页</el-menu-item>
        </el-menu>
      </el-aside>

      <el-main class="layout__main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activePath = computed(() => route.path)

function logout() {
  userStore.clearUser()
  router.push('/login')
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
}
.layout__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #ebeef5;
  background: #fff;
}
.layout__aside {
  border-right: 1px solid #ebeef5;
  background: #fff;
}
.layout__main {
  background: #f5f7fa;
}
</style>