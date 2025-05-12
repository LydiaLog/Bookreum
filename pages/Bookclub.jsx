import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import SearchBar from '../components/SearchBar';
import SortDropdown from '../components/SortDropdown';
import BookclubCard from '../components/BookclubCard';
import Pagination from '../components/Pagination';
import BookclubCreateModal from '../components/BookclubCreateModal';
import BookclubJoinModal from '../components/BookclubJoinModal'; // ✅ 경로 확인
import dummyBookclubs from '../data/dummyBookclubs';
import '../styles/Global.css';
import '../styles/Bookclub.css';

function Bookclub() {
  const navigate = useNavigate();

  /* 카드 데이터 */
  const [clubs, setClubs] = useState(dummyBookclubs);

  /* 모달 상태 */
  const [showModal, setShowModal] = useState(false);
  const [showJoinModal, setShowJoinModal] = useState(false);
  const [selectedClub, setSelectedClub] = useState(null);

  /* 검색 입력값과 확정 검색어 분리 */
  const [query, setQuery] = useState('');
  const [keyword, setKeyword] = useState('');

  /* 정렬 · 페이지네이션 */
  const [sortOption, setSortOption] = useState('all');
  const [page, setPage] = useState(1);
  const pageSize = 6;

  /* 필터/정렬/페이지 슬라이스 */
  const { pageData, totalPages } = useMemo(() => {
    let list = clubs.filter(c => {
      const hay = `${c.title} ${c.book} ${c.author} ${c.nickname}`.toLowerCase();
      return hay.includes(keyword.toLowerCase());
    });

    if (sortOption === 'open') list = list.filter(c => c.status === 'open');
    if (sortOption === 'closed') list = list.filter(c => c.status === 'closed');

    const start = (page - 1) * pageSize;
    const slice = list.slice(start, start + pageSize);
    return { pageData: slice, totalPages: Math.ceil(list.length / pageSize) };
  }, [clubs, keyword, sortOption, page]);

  /* 새 클럽 추가 */
  const addClub = (club) => {
    dummyBookclubs.unshift(club);
    setClubs([...dummyBookclubs]);
    setKeyword('');
    setQuery('');
    setPage(1);
  };

  /* 참여 모달 열기 */
  const openJoinModal = (club) => {
    setSelectedClub(club);
    setShowJoinModal(true);
  };

  /* 참여하기 동작 */
  const joinClub = () => {
    if (!selectedClub) return;
    navigate(`/bookclub/${selectedClub.id}`);
    setShowJoinModal(false); // 모달 닫기
  };

  /* 정렬 옵션 */
  const sortOptions = [
    { value: 'all', label: '전체' },
    { value: 'open', label: '모집중' },
    { value: 'closed', label: '모집마감' },
  ];

  /* 검색 확정 핸들러 */
  const commitSearch = () => {
    setKeyword(query.trim());
    setPage(1);
  };

  return (
    <>
      <div className="page-wrapper" style={{ display: 'flex', justifyContent: 'center' }}>
        <h2 className="page-title">북클럽</h2>
        <p className="page-subtitle">책으로 이어지는 우리들의 이야기</p>

        <div style={{ marginLeft: '13%' }}>
          {/* 검색 / 정렬 / 만들기 */}
          <div style={{
            display: 'flex', justifyContent: 'space-between',
            alignItems: 'flex-end', marginTop: 20
          }}>
            <SearchBar
              value={query}
              onChange={e => setQuery(e.target.value)}
              onSearch={commitSearch}
            />

            <div style={{ display: 'flex', gap: 10, marginRight: 10 }}>
              <SortDropdown
                value={sortOption}
                onChange={e => { setSortOption(e.target.value); setPage(1); }}
                options={sortOptions}
              />
              <button
                style={{
                  background: '#B4C9A4', opacity: 0.8, padding: '8px 18px',
                  fontSize: 14, border: 'none', borderRadius: 6, cursor: 'pointer'
                }}
                onClick={() => setShowModal(true)}
              >
                만들기
              </button>
            </div>
          </div>

          {/* 카드 리스트 */}
          <div className="bookclublist">
            {pageData.map(club => (
              <BookclubCard
                key={club.id}
                bookclub={club}
                onClick={() => openJoinModal(club)}
              />
            ))}
            {pageData.length === 0 && <p>검색 결과가 없습니다.</p>}
          </div>

          {/* 페이지네이션 */}
          {totalPages > 1 && (
            <div style={{ textAlign: 'center', marginTop: 20 }}>
              <Pagination
                currentPage={page}
                totalPages={totalPages}
                onPageChange={setPage}
              />
            </div>
          )}
        </div>
      </div>

      {/* 만들기 모달 */}
      {showModal && (
        <BookclubCreateModal
          onClose={() => setShowModal(false)}
          onCreate={addClub}
        />
      )}

      {/* 참여하기 모달 */}
      {showJoinModal && selectedClub && (
        <BookclubJoinModal
          club={selectedClub}
          onClose={() => setShowJoinModal(false)}
          onJoin={joinClub}
        />
      )}
    </>
  );
}

export default Bookclub;