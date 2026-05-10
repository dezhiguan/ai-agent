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
    <RouterView />
  </ConfigProvider>
</template>
