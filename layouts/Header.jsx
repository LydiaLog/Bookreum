// src/layouts/Header.jsx
import { useNavigate, useLocation } from "react-router-dom";
import SearchIcon from "../icons/SearchIcon";
import logo from "../assets/logo.svg";

/* 👉 App.jsx 에서 isLoggedIn, setIsLoggedIn 을 props 로 내려받음 */
function Header({ isLoggedIn, setIsLoggedIn }) {
  const navigate  = useNavigate();
  const location  = useLocation();

  /* ----- 메뉴 정의 ----- */
  const menus = [
    { name: "홈",       path: "/" },
    { name: "북클럽",   path: "/bookclub" },
    { name: "북로그",   path: "/bookloglist" },
    { name: "AI 책추천", path: "/recommend" },
    { name: "마이페이지",path: "/mypage" },
    { name: "도움말",   path: "/help" },
  ];

  /* ----- 로그아웃 ----- */
  const handleLogout = () => {
    localStorage.removeItem("accessToken");      // 토큰/유저정보 삭제
    setIsLoggedIn(false);                        // App 의 state 업데이트
    navigate("/login");
  };

  /* ----- 로그인 페이지 이동 ----- */
  const handleLogin = () => navigate("/login");

  return (
    <header
      style={{
        backgroundColor: "#8A9B78",
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        padding: "0 32px",
        height: "85px",
        position: "fixed",
        inset: 0,
        width: "100%",
        zIndex: 10,
        boxSizing: "border-box",
      }}
    >
      {/* ── 왼쪽: 로고 + 검색창 ───────────────────────── */}
      <div style={{ display: "flex", alignItems: "center", gap: 24 }}>
        <img
          src={logo}
          alt="북그러움 로고"
          style={{ height: 70, cursor: "pointer", marginBottom: 8 }}
          onClick={() => navigate("/")}
        />

        <div
          style={{
            background: "#fff",
            borderRadius: 999,
            display: "flex",
            alignItems: "center",
            padding: "4px 16px",
            width: 300,
            height: 40,
          }}
        >
          <span style={{ fontSize: 20, marginRight: 12, cursor: "pointer" }}>☰</span>
          <input
            type="text"
            placeholder="검색어를 입력하세요."
            style={{
              flex: 1,
              border: "none",
              outline: "none",
              fontSize: 14,
              background: "transparent",
            }}
          />
          <SearchIcon width={18} height={18} />
        </div>
      </div>

      {/* ── 오른쪽: 메뉴 & 로그인/로그아웃 ───────────────── */}
      <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
        {menus.map((m) => {
          const active =
            location.pathname === m.path ||
            (m.name === "북로그"   && location.pathname.startsWith("/booklog")) ||
            (m.name === "북클럽"   && location.pathname.startsWith("/bookclub")) ||
            (m.name === "AI 책추천" && location.pathname.startsWith("/recommend"));
          return (
            <button
              key={m.path}
              onClick={() => navigate(m.path)}
              style={{
                padding: "7px 12px",
                background: active ? "#F5F5F5" : "transparent",
                border: "none",
                borderRadius: 8,
                fontSize: 14,
                cursor: "pointer",
                fontWeight: active ? "bold" : "normal",
                outline: "none",
              }}
            >
              {m.name}
            </button>
          );
        })}

        {/* 로그인 / 로그아웃 토글 */}
        {isLoggedIn ? (
          <button
            onClick={handleLogout}
            style={{
              marginLeft: 16,
              background: "#2E2E2E",
              color: "#fff",
              border: "none",
              padding: "8px 14px",
              borderRadius: 8,
              fontSize: 14,
              cursor: "pointer",
            }}
          >
            로그아웃
          </button>
        ) : (
          <button
            onClick={handleLogin}
            style={{
              marginLeft: 16,
              background: "#2E2E2E",
              color: "#fff",
              border: "none",
              padding: "8px 14px",
              borderRadius: 8,
              fontSize: 14,
              cursor: "pointer",
            }}
          >
            로그인
          </button>
        )}
      </div>
    </header>
  );
}

export default Header;