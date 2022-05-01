import { AppVue } from '@/common/primary/app';
import { createRouter, createWebHistory } from 'vue-router';

const routes = [
  {
    path: '/',
    redirect: { name: 'App' },
  },
  {
    path: '/app',
    name: 'App',
    component: AppVue,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
