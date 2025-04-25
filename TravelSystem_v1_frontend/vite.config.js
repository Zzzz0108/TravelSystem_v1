import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  optimizeDeps: {
    include: ['date-fns/format'] // 显式包含依赖
  },
  server: {
    port: 3001,
    strictPort: true, // 如果端口被占用，则直接退出
    proxy: {
      '/api': {
        target: 'http://localhost:9090',
        changeOrigin: true,
        secure: false,
        ws: true
      }
    }
  },
  assetsInclude: ['**/*.jpg', '**/*.jpeg', '**/*.png', '**/*.gif', '**/*.svg'],
  publicDir: 'public',
  build: {
    assetsDir: 'assets',
    outDir: 'dist',
    assetsInlineLimit: 4096
  }
})