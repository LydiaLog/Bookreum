import { useNavigate, useLocation } from "react-router-dom";
import SearchIcon from "../icons/SearchIcon"; // 네가 쓰는 아이콘 컴포넌트
import logo from "../assets/logo.svg"; // 네 이미지에 있는 로고 svg 경로 예시

function Header() {
  const navigate = useNavigate();
  const location = useLocation();

  const menus = [
    { name: "홈", path: "/" },
    { name: "북클럽", path: "/bookclub" },
    { name: "북로그", path: "/bookloglist" },
    { name: "AI 책추천", path: "/recommend" },
    { name: "마이페이지", path: "/mypage" },
    { name: "도움말", path: "/help" },
  ];

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
            top: 0,
            left: 0,
            right: 0,
            width: "100%",
            zIndex: 10,
            boxSizing: "border-box"
        }}
    >
        {/* ✅ 왼쪽: 로고 + 검색창 */}
        <div style={{ display: "flex", alignItems: "center", gap: "24px" }}>
            <img 
                src={logo} 
                alt="북그러움 로고" 
                style={{ height: "70px", marginBottom: "8px" }}
                onClick={() => navigate('/')}
            />
    
            {/* 검색창 */}
            <div
                style={{
                    backgroundColor: "#fff",
                    borderRadius: "999px",
                    display: "flex",
                    alignItems: "center",
                    padding: "4px 16px",
                    width: "300px",
                    height: "40px",
                }}
            >
                <span style={{ fontSize: "20px", marginRight: "12px", cursor: "pointer" }}>☰</span>
                <input
                    type="text"
                    placeholder="검색어를 입력하세요."
                    style={{
                        flex: 1,
                        border: "none",
                        outline: "none",
                        fontSize: "14px",
                        background: "transparent",
                    }}
                />
                <SearchIcon width={18} height={18} />
            </div>
        </div>

        {/* 오른쪽: 메뉴 버튼들 */}
        <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
            {menus.map((menu, idx) => {
                const isActive = location.pathname === menu.path ||
                    (menu.name === "북로그" && location.pathname.startsWith("/booklog")) ||
                    (menu.name === "북클럽" && location.pathname.startsWith("/bookclub"));
                return (
                    <button
                        key={idx}
                        onClick={() => navigate(menu.path)}
                        style={{
                            padding: "7px 12px",
                            backgroundColor: isActive ? "#F5F5F5" : "transparent",
                            border: "none",
                            borderRadius: "8px",
                            fontSize: "14px",
                            cursor: "pointer",
                            fontWeight: isActive ? "bold" : "normal",
                            transition: "0.2s",
                            outline: "none",
                        }}
                    >
                        {menu.name}
                    </button>
                );
            })}

            {/* 로그인/로그아웃 버튼 */}
            <button
                style={{
                    marginLeft: "16px",
                    background: "#2E2E2E",
                    color: "#fff",
                    border: "none",
                    padding: "8px 14px",
                    borderRadius: "8px",
                    fontSize: "14px",
                    cursor: "pointer",
                }}
            >
                로그아웃
            </button>
        </div>
        
    </header>
  );
}

export default Header;