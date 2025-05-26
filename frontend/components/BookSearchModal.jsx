// src/components/BookSearchModal.jsx
import { useState } from "react";
import api from "../axiosConfig";
import "../styles/BookSearchModal.css";

/**
 * 책 검색 모달 – 백엔드의 /api/aladin/search 연동 버전
 * - keyword 파라미터로 최대 5권 반환
 * - 선택 시 상위 컴포넌트에 AladinItem 그대로 전달
 */
export default function BookSearchModal({ onClose, onSelect }) {
  const [searchTerm, setSearchTerm] = useState("");
  const [results, setResults] = useState([]); // AladinItem[]
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  /**
   * 엔터 또는 검색 버튼 → 알라딘 검색
   */
  const handleSearch = async () => {
    const term = searchTerm.trim();
    if (!term) {
      setResults([]);
      return;
    }

    try {
      setLoading(true);
      setError("");

      // 1) 새로운 엔드포인트로 변경
      const { data } = await api.get("/api/aladin/search/items", {
        params: { keyword: term },
      });

      // 2) 바로 배열로 세팅
      setResults(data ?? []);
    } catch (err) {
      console.error("검색 에러:", err);
      if (err.code === "ERR_NETWORK" || !err.response) {
        setError("서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.");
      } else {
        setError("검색 중 오류가 발생했습니다.");
      }
    } finally {
      setLoading(false);
    }
  };

  /**
   * 책 선택 시 DB에 저장하고 ID 반환
   */
  const handleBookSelect = async (book) => {
    try {
      // 1. DB에 책 저장
      const { data: savedBook } = await api.post("/api/clubs/saveBook", book);

      // 2. 저장된 책 정보를 부모 컴포넌트에 전달
      onSelect({
        ...book,
        id: savedBook.id, // DB에 저장된 ID 사용
      });

      onClose();
    } catch (err) {
      console.error("책 저장 실패:", err);
      setError("책 저장 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="book-modal-backdrop" onClick={onClose}>
      <div className="book-modal" onClick={(e) => e.stopPropagation()}>
        <h4>책 검색</h4>

        <div className="search-input-row">
          <input
            className="search-input"
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleSearch()}
            placeholder="책 제목 또는 작가 이름"
          />
          <button
            className="search-button"
            onClick={handleSearch}
            disabled={loading}
          >
            {loading ? "검색 중…" : "검색"}
          </button>
        </div>

        {error && <p className="search-error">{error}</p>}

        <ul className="search-result-list">
          {results.length === 0 && searchTerm && !loading && !error ? (
            <li className="no-results">
              <p>검색 결과가 없습니다.</p>
              <p>다른 키워드로 검색해보세요.</p>
            </li>
          ) : (
            results.map((b) => (
              <li
                key={b.isbn13 || b.itemId}
                className="search-result-item"
                onClick={() => handleBookSelect(b)}
              >
                {b.cover && (
                  <img src={b.cover} alt={b.title} className="result-cover" />
                )}
                <div className="result-text">
                  <strong>{b.title}</strong>
                  <span className="result-author">{b.author}</span>
                </div>
              </li>
            ))
          )}
        </ul>
      </div>
    </div>
  );
}
