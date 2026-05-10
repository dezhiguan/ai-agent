<script setup lang="ts">
import { ConfigProvider } from 'ant-design-vue'
import zhCN from 'ant-design-vue/es/locale/zh_CN'

const theme = {
  token: {
    colorPrimary: '#b93a2f',
    borderRadius: 10,
    fontFamily: "'Noto Serif SC', 'Songti SC', serif",
  },
}
</script>

<template>
  <ConfigProvider :locale="zhCN" :theme="theme">
    <div class="app-shell">
      <header class="top-nav" aria-label="主导航">
        <RouterLink class="nav-link" to="/">答题</RouterLink>
        <RouterLink class="nav-link" to="/history">历史成绩</RouterLink>
      </header>
      <RouterView />
    </div>
  </ConfigProvider>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
}

.top-nav {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  padding: 10px 16px;
  background: rgba(26, 18, 14, 0.92);
  border-bottom: 1px solid rgba(185, 58, 47, 0.4);
  position: sticky;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(8px);
}

.nav-link {
  padding: 8px 20px;
  border-radius: 999px;
  color: rgba(240, 230, 216, 0.88);
  text-decoration: none;
  letter-spacing: 0.12em;
  font-size: 0.9rem;
  transition:
    color 0.2s,
    background 0.2s;
}

.nav-link:hover {
  color: #fff;
  background: rgba(185, 58, 47, 0.28);
}

.nav-link.router-link-active {
  color: #fff;
  background: rgba(185, 58, 47, 0.5);
  border: 1px solid rgba(240, 230, 216, 0.22);
}
</style>
