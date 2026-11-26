import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    // 백엔드와 같은 출처로 보이게 해서 세션 쿠키가 그대로 동작하게 한다.
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
