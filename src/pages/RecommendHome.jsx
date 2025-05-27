import { useState } from "react";
import { useNavigate } from "react-router-dom";
import SearchBar_ver2 from "../components/SearchBar_ver2";
import "../styles/RecommendHome.css";

function RecommendHome() {
  const [keyword, setKeyword] = useState("");
  const navigate = useNavigate();

  const handleSearch = () => {
    const q = keyword.trim();
    if (!q) return;
    navigate(`/recommend/result?query=${encodeURIComponent(q)}`);
  };

  return (
    <div className="recommend-home-wrapper">
      <h2 className="recommend-home-title">
        북그러움&nbsp;<span className="accent">AI</span>&nbsp;책추천
      </h2>

      <div className="searchbar-center">
        <SearchBar_ver2
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          onSearch={handleSearch}                   // 버튼 클릭
          onKeyDown={(e) => e.key === "Enter" && handleSearch()} // ↵ 키
        />
      </div>
    </div>
  );
}

export default RecommendHome;