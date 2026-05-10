import { createRouter, createWebHistory } from 'vue-router'
import QuizHome from '@/views/QuizHome.vue'
import QuizHistory from '@/views/QuizHistory.vue'

export default createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'quiz', component: QuizHome },
    { path: '/history', name: 'history', component: QuizHistory },
  ],
})
