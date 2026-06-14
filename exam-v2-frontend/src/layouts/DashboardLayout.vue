<template>
  <div class="dashboard-shell">
    <!-- ===== 侧边栏 ===== -->
    <aside class="dashboard-sidebar">
      <div class="sidebar-brand" @click="router.push(homePath)">
        <div class="sidebar-logo">📝</div>
        <div class="sidebar-brand-text">
          <div class="sidebar-brand-title">在线考试系统</div>
          <div class="sidebar-brand-sub">OES v2.0</div>
        </div>
      </div>

      <div class="sidebar-user">
        <div class="sidebar-user-avatar" :style="{ background: avatarBg }">
          {{ displayName.charAt(0) }}
        </div>
        <div class="sidebar-user-info">
          <div class="sidebar-user-name">{{ displayName }}</div>
          <div class="sidebar-user-role">{{ roleLabel }}</div>
        </div>
      </div>

      <el-menu
          class="sidebar-menu"
          :default-active="activeMenuKey"
          @select="handleSelect"
      >
        <el-menu-item
            v-for="item in menus"
            :key="item.key"
            :index="item.key"
            class="sidebar-menu__item"
        >
          <span class="menu-label">{{ item.label }}</span>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer">
        <el-button class="sidebar-logout" @click="logout">
          退出登录
        </el-button>
      </div>
    </aside>

    <!-- ===== 右侧主体 ===== -->
    <div class="dashboard-main">
      <header class="dashboard-topbar">
        <div class="topbar-left">
          <span class="topbar-title">{{ title }}</span>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="homePath">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="route.meta.title">{{ route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="topbar-right">
          <el-button class="topbar-ai-btn" circle @click="router.push('/ai-chat')" title="AI 助手">
            🤖
          </el-button>
          <el-tag effect="plain" :type="roleTagType" size="small">{{ roleLabel }}</el-tag>
          <span class="topbar-user-name">{{ displayName }}</span>
          <el-button size="small" text @click="logout">退出</el-button>
        </div>
      </header>

      <main class="dashboard-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { dashboardMenus, type DashboardMenuItem } from '@/config/navigation'
import { getRoleLabel, getHomePath } from '@/constants/auth'
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
const homePath = computed(() => getHomePath(props.role))

const avatarBg = computed(() => {
  if (props.role === 'ADMIN') return '#ef476f'
  if (props.role === 'TEACHER') return '#06d6a0'
  return '#4361ee'
})

const roleTagType = computed(() => {
  if (props.role === 'ADMIN') return 'danger'
  if (props.role === 'TEACHER') return 'success'
  return ''
})

function handleSelect(key: string) {
  const selected = menus.value.find((item) => item.key === key)
  if (!selected) return
  if (selected.action === 'logout') { logout(); return }
  if (selected.path && selected.path !== route.path) {
    router.push(selected.path)
  }
}

function logout() {
  userStore.clearUser()
  ElMessage.success('已退出登录，再见！')
  router.push('/login')
}
</script>

<style scoped>
/* ===== 整体布局 ===== */
.dashboard-shell {
  height: 100vh; width: 100vw; overflow: hidden;
  display: flex; background-color: #f0f2f7;
}

/* ===== 侧边栏 ===== */
.dashboard-sidebar {
  width: 240px; flex-shrink: 0;
  background: linear-gradient(180deg, #1e293b 0%, #1a2332 100%);
  display: flex; flex-direction: column; height: 100%;
}

.sidebar-brand {
  display: flex; align-items: center; gap: 10px;
  padding: 20px 18px; cursor: pointer; border-bottom: 1px solid rgba(255,255,255,0.06);
}
.sidebar-logo { font-size: 28px; }
.sidebar-brand-title { font-size: 15px; font-weight: 700; color: #fff; }
.sidebar-brand-sub { font-size: 10px; color: rgba(255,255,255,0.35); letter-spacing: 1px; }

.sidebar-user {
  display: flex; align-items: center; gap: 10px;
  padding: 16px 18px; border-bottom: 1px solid rgba(255,255,255,0.06);
}
.sidebar-user-avatar {
  width: 36px; height: 36px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  color: #fff; font-weight: 700; font-size: 15px; flex-shrink: 0;
}
.sidebar-user-name { font-size: 13px; font-weight: 600; color: #fff; }
.sidebar-user-role { font-size: 11px; color: rgba(255,255,255,0.45); }

/* ElMenu 覆盖 */
.sidebar-menu {
  flex: 1; overflow-y: auto; background: transparent !important;
  border-right: none !important; padding: 8px 0;
}
.sidebar-menu__item {
  height: 44px; line-height: 44px; margin: 2px 12px;
  border-radius: 8px; color: rgba(255,255,255,0.65) !important;
  font-size: 14px; transition: all 0.15s;
  padding-left: 16px !important;
}
.sidebar-menu__item:hover {
  background: rgba(255,255,255,0.08) !important; color: #fff !important;
}
.sidebar-menu__item.is-active {
  background: rgba(67,97,238,0.25) !important; color: #fff !important;
  font-weight: 600;
}
.sidebar-menu .el-menu-item * { color: inherit !important; }

.sidebar-footer {
  padding: 14px 18px; border-top: 1px solid rgba(255,255,255,0.06);
}
.sidebar-logout {
  width: 100%; color: rgba(255,255,255,0.5); background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.1); border-radius: 8px; height: 38px;
}
.sidebar-logout:hover {
  color: #ef476f; background: rgba(239,71,111,0.1); border-color: rgba(239,71,111,0.3);
}

/* ===== 主体 ===== */
.dashboard-main {
  flex: 1; display: flex; flex-direction: column; height: 100%;
  min-width: 0;
}

/* 顶栏 */
.dashboard-topbar {
  height: 56px; flex-shrink: 0; background: #fff;
  padding: 0 24px; display: flex; align-items: center; justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04); border-bottom: 1px solid #e8eaef;
  z-index: 10;
}
.topbar-left { display: flex; align-items: center; gap: 20px; }
.topbar-title { font-size: 15px; font-weight: 600; color: var(--color-text-title); }
.topbar-right { display: flex; align-items: center; gap: 10px; }
.topbar-ai-btn { font-size: 18px; }
.topbar-user-name { font-size: 13px; color: var(--color-text-body); }

/* 内容区 */
.dashboard-content {
  padding: 20px 24px; flex: 1; overflow-y: auto;
}
</style>
