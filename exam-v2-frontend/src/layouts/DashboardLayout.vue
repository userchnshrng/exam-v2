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
          <div class="dashboard-topbar__subtitle">在线考试系统 v2.0 · 基础后台外壳</div>
        </div>

        <div class="dashboard-topbar__user">
          <el-tag effect="light" type="primary">{{ roleLabel }}</el-tag>
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

<style scoped>
/* ==========================================================================
   现代化后台布局核心尺寸约束样式
   ========================================================================== */

/* 1. 锁定最外层容器为屏幕视口大小，彻底切断全局多余滚动条 */
.dashboard-shell {
  height: 100vh;      /* 强制高度撑满屏幕 */
  width: 100vw;       /* 强制宽度撑满屏幕 */
  overflow: hidden;   /* 隐藏全局滚动条 */
  background-color: #f4f6f9; /* 优雅的后台底色 */
  display: flex;
}

/* 2. 左侧侧边栏尺寸与内部 Flex 排流配置 */
.dashboard-shell__aside {
  background-color: #ffffff;
  border-right: 1px solid #e6e8eb;
  display: flex;
  flex-direction: column;    /* 让品牌、菜单、页脚纵向像乐高一样垂直排列 */
  justify-content: space-between;
  height: 100%;
}

.dashboard-brand {
  padding: 24px 20px;
  border-bottom: 1px solid #f0f2f5;
}

.dashboard-brand__title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2f3d;
}

.dashboard-brand__subtitle {
  font-size: 11px;
  color: #909399;
  margin-top: 4px;
}

.dashboard-menu {
  border-right: none;
  flex: 1;            /* 关键：菜单自动撑满除去头部和尾部后的剩余纵向空间 */
  overflow-y: auto;   /* 如果菜单项极多，允许侧边栏内部菜单独立滚动 */
}

.dashboard-menu__item {
  height: 48px;
  line-height: 48px;
  margin: 4px 12px;
  border-radius: 8px;
  color: #4e5969;
}

.dashboard-shell__footer {
  padding: 16px;
  border-top: 1px solid #f0f2f5;
  text-align: center;
}

.dashboard-shell__logout {
  width: 100%;
}

/* 3. 右侧主体容器尺寸配置 */
.dashboard-shell__main {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
}

/* 4. 顶部状态栏高度固定 */
.dashboard-topbar {
  background-color: #ffffff;
  height: 64px;       /* 规范化顶部栏高度 */
  padding: 0 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.01);
  border-bottom: 1px solid #e6e8eb;
  flex-shrink: 0;     /* 关键：防止右侧内容过多时把顶部栏的高度挤压变形 */
}

.dashboard-topbar__title {
  font-size: 16px;
  font-weight: 600;
  color: #1d2129;
}

.dashboard-topbar__subtitle {
  font-size: 12px;
  color: #86909c;
  margin-top: 2px;
}

.dashboard-topbar__user {
  display: flex;
  align-items: center;
  gap: 12px;
}

.dashboard-topbar__name {
  font-size: 14px;
  color: #4e5969;
  font-weight: 500;
}

/* 5. 主内容卡片区局部滚动配置 */
.dashboard-content {
  padding: 24px 32px;
  box-sizing: border-box; /* 保证 padding 不会撑大或者搞乱容器设定的真实宽高 */
  flex: 1;                /* 撑满除了顶部栏之外的所有垂直高度 */
  overflow-y: auto;       /* 核心：只有当这里面的业务表格或表单过长时，才在这里出现纵向滚动条 */
}
</style>