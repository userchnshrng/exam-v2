<template>
  <el-container class="dashboard-shell">
    <el-aside class="dashboard-shell__aside" width="260px">
      <div class="dashboard-brand">
        <div class="dashboard-brand__title">{{ title }}</div>
        <div class="dashboard-brand__subtitle">{{ subtitle }}</div>
      </div>

      <el-menu
        class="dashboard-menu"
        :default-active="activeMenuKey"
        @select="handleSelect"
      >
        <el-menu-item
          v-for="item in menus"
          :key="item.key"
          :index="item.key"
          class="dashboard-menu__item"
        >
          <span>{{ item.label }}</span>
        </el-menu-item>
      </el-menu>

      <div class="dashboard-shell__footer">
        <el-button text type="danger" class="dashboard-shell__logout" @click="logout">
          退出登录
        </el-button>
      </div>
    </el-aside>

    <el-container class="dashboard-shell__main">
      <el-header class="dashboard-topbar">
        <div>
          <div class="dashboard-topbar__title">{{ title }}</div>
          <div class="dashboard-topbar__subtitle">简洁、清晰、可扩展的后台壳子</div>
        </div>

        <div class="dashboard-topbar__user">
          <el-tag effect="light">{{ roleLabel }}</el-tag>
          <span class="dashboard-topbar__name">{{ displayName }}</span>
          <el-button size="small" @click="logout">退出登录</el-button>
        </div>
      </el-header>

      <el-main class="dashboard-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { dashboardMenus, type DashboardMenuItem } from '@/config/navigation'
import { getRoleLabel } from '@/constants/auth'
import { useUserStore } from '@/stores/user'
import type { UserRole } from '@/types/user'

const props = defineProps<{
  role: UserRole
  title: string
  subtitle: string
}>()

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const menus = computed<DashboardMenuItem[]>(() => dashboardMenus[props.role])
const activeMenuKey = computed(() => route.path)
const displayName = computed(() => userStore.displayName || getRoleLabel(props.role))
const roleLabel = computed(() => getRoleLabel(props.role))

function handleSelect(key: string) {
  const selected = menus.value.find((item) => item.key === key)
  if (!selected) return

  if (selected.action === 'logout') {
    logout()
    return
  }

  if (selected.path && selected.path !== route.path) {
    router.push(selected.path)
  }
}

function logout() {
  userStore.clearUser()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>
