import { useState } from 'react';
import api from '../axiosConfig';
import '../styles/BookSearchModal.css';

/**
 * 책 검색 모달 – 알라딘 백엔드 API(/api/aladin/search) 연동 버전
 * - keyword 파라미터로 최대 5권 반환
 * - 선택 시 상위 컴포넌트에 AladinItem 그대로 전달
 */
export default function BookSearchModal({ onClose, onSelect }) {
  const [searchTerm, setSearchTerm] = useState('');
  const [results, setResults]       = useState([]); // AladinItem[]
  const [loading, setLoading]       = useState(false);
  const [error, setError]           = useState('');

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
      setError('');

      const { data } = await api.get('/api/aladin/search', {
        params: { keyword: term },
      });

      // 백엔드 DTO: { totalResults, item: AladinItem[] }
      setResults(data?.item ?? []);
    } catch (err) {
      console.error('책 검색 실패:', err);
      setError('검색 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
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
            onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
            placeholder="책 제목 또는 작가 이름"
          />
          <button className="search-button" onClick={handleSearch} disabled={loading}>
            {loading ? '검색 중…' : '검색'}
          </button>
        </div>

        {error && <p className="search-error">{error}</p>}

        <ul className="search-result-list">
          {results.map((b) => (
            <li
              key={b.isbn13 || b.itemId}
              className="search-result-item"
              onClick={() => {
                onSelect(b); // 부모에게 AladinItem 전달
                onClose();
              }}
            >
              {b.cover && <img src={b.cover} alt={b.title} className="result-cover" />}
              <div className="result-text">
                <strong>{b.title}</strong>
                <span className="result-author">{b.author}</span>
              </div>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}