import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { useState, useEffect } from "react";
import Header from "./layouts/Header";
import Footer from "./layouts/Footer";
import Home from "./pages/Home";
import Bookclub from "./pages/Bookclub";
import BookclubDetail from "./pages/BookclubDetail";
import BooklogList from "./pages/BooklogList";
import BooklogDetail from "./pages/BooklogDetail";
import BooklogWrite from "./pages/BooklogWrite";
import Login from "./pages/Login";
import KakaoCallback from "./pages/KakaoCallback";
import MyPage from "./pages/MyPage";
import "./App.css";

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [isKakaoInit, setIsKakaoInit] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    setIsLoggedIn(!!token);

    // 카카오 SDK 불러오기 & 초기화
    const scriptId = "kakao-sdk";
    if (!document.getElementById(scriptId)) {
      const script = document.createElement("script");
      script.id = scriptId;
      script.src = "https://developers.kakao.com/sdk/js/kakao.js";
      script.async = true;
      script.onload = () => {
        if (!window.Kakao.isInitialized()) {
          window.Kakao.init(import.meta.env.VITE_KAKAO_CLIENT_ID);
        }
        setIsKakaoInit(true);
      };
      document.head.appendChild(script);
    } else {
      if (!window.Kakao.isInitialized()) {
        window.Kakao.init(import.meta.env.VITE_KAKAO_CLIENT_ID);
      }
      setIsKakaoInit(true);
    }
  }, []);

  return (
    <BrowserRouter>
      <Header isLoggedIn={isLoggedIn} setIsLoggedIn={setIsLoggedIn} />
      <main className="main-content">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/" element={<Home />} />
          <Route path="/bookclub" element={<Bookclub />} />
          <Route path="/bookclub/:id" element={<BookclubDetail />} />
          <Route path="/bookloglist" element={<BooklogList />} />
          <Route path="/booklog/:id" element={<BooklogDetail />} />
          <Route
            path="/booklogwrite"
            element={isLoggedIn ? <BooklogWrite /> : <Navigate to="/login" />}
          />
          <Route
            path="/oauth/kakao/callback"
            element={
              isKakaoInit ? (
                <KakaoCallback />
              ) : (
                <div>카카오 SDK 초기화 중...</div>
              )
            }
          />
          <Route path="/mypage" element={<MyPage />} />
          <Route path="/recommend" element={<Home />} />
        </Routes>
      </main>
      <Footer />
    </BrowserRouter>
  );
}

export default App;
