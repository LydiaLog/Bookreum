// App.jsx
import { BrowserRouter, Routes, Route, Navigate, Outlet, useLocation } from "react-router-dom";
import { useEffect, useState } from "react";
import Header from "./layouts/Header";
import Footer from "./layouts/Footer";
import Home from "./pages/Home";
import Bookclub from "./pages/Bookclub";
import BookclubDetail from "./pages/BookclubDetail";
import BooklogList from "./pages/BooklogList";
import BooklogDetail from "./pages/BooklogDetail";
import BooklogWrite from "./pages/BooklogWrite";
import RecommendHome from "./pages/RecommendHome";
import RecommendResult from "./pages/RecommendResult";
import MyPage from "./pages/MyPage";
import HelpPage from "./pages/HelpPage";
import Login from "./pages/Login";
import KakaoCallback from "./pages/KakaoCallback";
import "./App.css";

/* ---------- 로그인 필요 라우트 래퍼 ---------- */
function RequireAuth({ isLoggedIn }) {
  const location = useLocation();
  if (!isLoggedIn) {
    // 로그인 안 되어 있으면 현재 위치 remember 후 /login 으로
    return <Navigate to="/login" replace state={{ from: location }} />;
  }
  return <Outlet />;           // 통과 ➜ 하위 라우트 렌더
}

function App() {
  const [isLoggedIn, setIsLoggedIn]  = useState(false);
  const [isKakaoInit, setIsKakaoInit] = useState(false);

  /* ----- 토큰 & Kakao SDK 초기화 ----- */
  useEffect(() => {
    setIsLoggedIn(!!localStorage.getItem("accessToken"));

    const KAKAO_KEY = import.meta.env.VITE_KAKAO_CLIENT_ID;
    const onLoad = () => {
      if (window.Kakao && !window.Kakao.isInitialized()) window.Kakao.init(KAKAO_KEY);
      setIsKakaoInit(true);
    };

    const scriptId = "kakao-sdk";
    const exist = document.getElementById(scriptId);
    if (exist) { window.Kakao ? onLoad() : exist.addEventListener("load", onLoad); }
    else {
      const s = document.createElement("script");
      s.id = scriptId; s.src="https://developers.kakao.com/sdk/js/kakao.js"; s.async = true;
      s.addEventListener("load", onLoad); document.head.appendChild(s);
    }
  }, []);

  return (
    <BrowserRouter>
      <Header isLoggedIn={isLoggedIn} setIsLoggedIn={setIsLoggedIn} />

      <main className="main-content">
        <Routes>
          {/* ====== 공개 라우트 ====== */}
          <Route path="/login"       element={<Login />} />
          <Route path="/"            element={<Home />} />
          <Route path="/bookclub"    element={<Bookclub />} />
          <Route path="/bookloglist" element={<BooklogList />} />
          <Route path="/help"        element={<HelpPage />} />
          <Route
            path="/oauth/kakao/callback"
            element={isKakaoInit ? <KakaoCallback /> : <p style={{textAlign:'center',marginTop:40}}>카카오 SDK 초기화 중...</p>}
          />

          {/* ====== 로그인 필요 라우트 ====== */}
          <Route element={<RequireAuth isLoggedIn={isLoggedIn} />}>
            <Route path="/bookclub/:id"     element={<BookclubDetail />} />
            <Route path="/booklog/:id"      element={<BooklogDetail />} />
            <Route path="/booklogwrite"     element={<BooklogWrite />} />
            <Route path="/recommend"        element={<RecommendHome />} />
            <Route path="/recommend/result" element={<RecommendResult />} />
            <Route path="/mypage"           element={<MyPage />} />
          </Route>
        </Routes>
      </main>

      <Footer />
    </BrowserRouter>
  );
}

export default App;