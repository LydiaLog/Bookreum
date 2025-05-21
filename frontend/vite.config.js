// vite.config.js
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  define: {
    global: "window",
  },
  server: {
    host: "0.0.0.0",
    port: 5173, // 개발 서버 포트
    proxy: {
      // /api 로 시작하는 모든 요청을 http://localhost:9090 으로 전달
      "/api": {
        target: "http://localhost:9090",
        changeOrigin: true,
        secure: false,
      },
      // (필요하다면) OAuth 콜백도 프록시
      "/oauth": {
        target: "http://localhost:9090",
        changeOrigin: true,
        secure: false,
      },
      "/ws": {
        target: "ws://localhost:9090",
        ws: true,
      },
    },
  },
});
