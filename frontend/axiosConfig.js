// src/axiosConfig.js
import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:9090",
  //baseURL: "http://10.50.234.11:9091",
  headers: {
    "Content-Type": "application/json",
  },
});

// 토큰 저장 함수
/*
const setAuthToken = (accessToken, refreshToken) => {
  if (accessToken) {
    localStorage.setItem("accessToken", accessToken);
    api.defaults.headers.common["Authorization"] = `Bearer ${accessToken}`;
  } else {
    localStorage.removeItem("accessToken");
    delete api.defaults.headers.common["Authorization"];
  }

  if (refreshToken) {
    localStorage.setItem("refreshToken", refreshToken);
  } else {
    localStorage.removeItem("refreshToken");
  }
};
*/

// 요청 인터셉터
api.interceptors.request.use(
  (config) => {
    /*
    const token = localStorage.getItem("accessToken");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    */
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터
api.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    /*
    if (error.response?.status === 401) {
      // 토큰이 만료된 경우 리프레시 토큰으로 갱신 시도
      if (error.response.data?.message === "Token expired") {
        const refreshToken = localStorage.getItem("refreshToken");
        if (refreshToken) {
          try {
            const response = await api.post("/api/auth/refresh", {
              refreshToken: refreshToken,
            });
            const { accessToken, refreshToken: newRefreshToken } =
              response.data;
            setAuthToken(accessToken, newRefreshToken);
            // 실패했던 요청 재시도
            error.config.headers.Authorization = `Bearer ${accessToken}`;
            return api(error.config);
          } catch (refreshError) {
            console.log("리프레시 토큰 갱신 실패");
            setAuthToken(null, null);
            window.location.href = "/login";
          }
        } else {
          console.log("리프레시 토큰이 없습니다.");
          setAuthToken(null, null);
          window.location.href = "/login";
        }
      }
    }
    */
    return Promise.reject(error);
  }
);

// export { setAuthToken };
export default api;
