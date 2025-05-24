import { useState, useMemo, useEffect } from 'react';
import api from '../axiosConfig';
import { useNavigate } from 'react-router-dom';
import SearchBar from '../components/SearchBar';
import SortDropdown from '../components/SortDropdown';
import BooklogCard from '../components/BooklogCard';
import Pagination from '../components/Pagination';
import '../styles/Global.css';
import '../styles/BooklogList.css';

// import dummyBooklogs from '../data/dummyBooklogs';

function BooklogList() {
  const navigate = useNavigate();

  /* 글 데이터 */
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  /* 검색 입력 / 확정 값 */
  const [query, setQuery]     = useState('');
  const [keyword, setKeyword] = useState('');

  /* 정렬 · 페이지 */
  const [sortOption, setSortOption] = useState('newest');
  const [page, setPage]             = useState(1);
  const pageSize = 6;

  /* 정렬 옵션 목록 */
  const sortOptions = [
    { value: 'newest', label: '최신순' },
    { value: 'oldest', label: '오래된순' },
  ];

  /* ===== 1. DB 로부터 글 목록 불러오기 ===== */
  useEffect(() => {
     (async () => {
       try {
         const { data } = await api.get('/api/home'); // [{id, title, content, date, bookTitle, bookAuthor, coverUrl, nickname}]
         setLogs(data);
       } catch (err) {
         console.error('Failed to load booklogs:', err);
         setError('북로그를 불러오지 못했습니다.');
       } finally {
         setLoading(false);
       }
     })();
   }, []);

  /* useEffect(() => {
    try {
      // 더미 데이터 불러오기
      setLogs(dummyBooklogs); 
    } catch (err) {
      console.error('Failed to load dummy booklogs:', err);
      setError('북로그를 불러오지 못했습니다.');
    } finally {
      setLoading(false);
    }
  }, []); */

  /* ===== 2. 검색 + 정렬 + 페이지네이션 처리 ===== */
  const { pageData, totalPages } = useMemo(() => {
    if (loading || error) return { pageData: [], totalPages: 0 };

    // 1) 검색
    let list = logs.filter(l => {
      const hay = `${l.title} ${l.bookTitle} ${l.bookAuthor} ${l.nickname}`.toLowerCase();
      return hay.includes(keyword.toLowerCase());
    });

    // 2) 정렬 (date는 ISO 문자열 혹은 epoch)
    list.sort((a, b) => {
      const da = new Date(a.date), db = new Date(b.date);
      return sortOption === 'newest' ? db - da : da - db;
    });

    // 3) 페이지
    const start = (page - 1) * pageSize;
    const slice = list.slice(start, start + pageSize);
    return { pageData: slice, totalPages: Math.ceil(list.length / pageSize) };
  }, [logs, keyword, sortOption, page, loading, error]);

  /* 검색 확정 */
  const commitSearch = () => {
    setKeyword(query.trim());
    setPage(1);
  };

  /* ===== Render ===== */
  return (
    <div className="page-wrapper" style={{ display:'flex', justifyContent: "center", }}>
      {/* 제목 */}
      <h2 className="page-title">북로그</h2>
      <p  className="page-subtitle">조용히 읽고, 조심스럽게 남긴 당신만의 책 이야기</p>

      <div style={{ marginLeft: "18%" }}>
        {/* 검색 / 정렬 / 글쓰기 */}
        <div className="booklog-toolbar">
          <SearchBar
            value={query}
            onChange={e => setQuery(e.target.value)}
            onSearch={commitSearch}
          />

          <div className="toolbar-right">
            <SortDropdown
              value={sortOption}
              onChange={e => { setSortOption(e.target.value); setPage(1); }}
              options={sortOptions}
            />
            <button
              style={{
                background: '#B4C9A4',
                opacity: 0.8,
                padding: '8px 18px',
                fontSize: '14px',
                border: 'none',
                borderRadius: '6px',
                cursor: 'pointer',
              }}
              onClick={() => navigate('/booklogwrite')}
            >
              글쓰기
            </button>
          </div>
        </div>

        {/* 상태 표시 */}
        {loading && <p style={{ textAlign:'center', marginTop:40 }}>로딩 중…</p>}
        {error && <p style={{ textAlign:'center', marginTop:40, color:'red' }}>{error}</p>}

        {/* 북로그 카드 리스트 */}
        {!loading && !error && (
          <>
            <div className="bookloglist">
              {pageData.map(data => (
                <BooklogCard key={data.id} booklog={data} onClick={() => navigate(`/booklog/${data.id}`)} />
              ))}
              {pageData.length === 0 && <p>검색 결과가 없습니다.</p>}
            </div>

            {/* 페이지네이션 */}
            {totalPages > 1 && (
              <div style={{ textAlign:'center', marginTop:"20px" }}>
                <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}

export default BooklogList;