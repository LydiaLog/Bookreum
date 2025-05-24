// import React, { useEffect, useState } from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Header from "./layouts/Header";
import Home from "./pages/Home";
import Bookclub from "./pages/Bookclub";
import BookclubDetail from "./pages/BookclubDetail";
import BooklogList from "./pages/BooklogList";
import BooklogDetail from "./pages/BooklogDetail";
import BooklogWrite from "./pages/BooklogWrite";
import LoginPage from "./pages/LoginPage";
import RecommendHome from "./pages/RecommendHome";
import RecommendResult from "./pages/RecommendResult";
import MyPage from "./pages/MyPage";
import HelpPage from "./pages/HelpPage";
import Footer from "./layouts/Footer";
import "./App.css";

function App() {
  // const [isLoggedIn, setIsLoggedIn] = useState(false);

  /* useEffect(() => {
    if (!window.Kakao) {
      console.error("Kakao SDK 로드 실패");
      return;
    }
    if (!window.Kakao.isInitialized()) {
      window.Kakao.init("30c2a52458637cbe8d9b14ffe730a37b"); // 여기에 실제 Kakao JavaScript 키를 넣어주세요.
      console.log("✅ Kakao SDK 초기화 완료");
    }

    const user = JSON.parse(localStorage.getItem("user"));
    setIsLoggedIn(!!user);
  }, []); */

  return (
    <BrowserRouter>
      <Header />
      <main className="main-content">
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/" element={<Home />} />
          <Route path="/bookclub" element={<Bookclub />} />
          <Route path="/bookclub/:id" element={<BookclubDetail />} />
          <Route path="/bookloglist" element={<BooklogList />} />
          <Route path="/booklog/:id" element={/* isLoggedIn ? */<BooklogDetail /> /*: <Navigate to="/login" /> */} />
          <Route path="/booklogwrite" element={<BooklogWrite />} />
          <Route path="/recommend" element={<RecommendHome />} />
          <Route path="/recommend/result" element={<RecommendResult />} />
          <Route path="/mypage" element={<MyPage />} />
          <Route path="/help" element={<HelpPage />} />
        </Routes>
      </main>
      <Footer />
    </BrowserRouter>
  );
}

console.log("API URL ⬇️", import.meta.env.VITE_API_URL);


export default App;