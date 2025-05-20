// src/axiosConfig.js
import axios from "axios";

const api = axios.create({
  baseURL: "http://10.50.253.27:9090",
  withCredentials: true,
  headers: { "Content-Type": "application/json" },
});

api.interceptors.request.use((config) => {
  const url = config.url || "";

  // 1) 카카오 인증, 2) 알라딘 검색 API에는 토큰을 안 붙인다
  if (url.includes("/api/auth/kakao") || url.startsWith("/api/aladin")) {
    return config;
  }

  // 그 외에는 로컬 스토리지에서 토큰 꺼내서 Authorization 헤더에 붙이기
  const token = localStorage.getItem("accessToken");
  console.log("Current token:", token); // 토큰 로깅
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
    console.log("Request headers:", config.headers); // 헤더 로깅
  } else {
    console.warn("No token found in localStorage"); // 토큰 없음 경고
  }
  return config;
});

export default api;