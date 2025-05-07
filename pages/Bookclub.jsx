import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import SearchBar from '../components/SearchBar';
import SortDropdown from '../components/SortDropdown';
import BookclubCard from '../components/BookclubCard';
import Pagination from '../components/Pagination';
import dummyBookclubs from '../data/dummyBookclubs';
import BookclubCreateModal from '../components/BookclubCreateModal';   // ★ 추가
import '../styles/Global.css';
import '../styles/Bookclub.css';

function Bookclub() {
  const navigate = useNavigate();

  /* 카드 데이터 state → 더미 배열 기반 */
  const [clubs, setClubs] = useState(dummyBookclubs);

  /* 모달 표시 여부 */
  const [showModal, setShowModal] = useState(false);

  /* 페이지네이션 */
  const [currentPage, setCurrentPage] = useState(1);
  const clubsPerPage = 6;
  const indexLast = currentPage * clubsPerPage;
  const currentClubs = clubs.slice(indexLast - clubsPerPage, indexLast);
  const totalPages = Math.ceil(clubs.length / clubsPerPage);

  /* 검색·정렬 (UI만, 실제 필터링은 추후 추가 가능) */
  const [searchTerm, setSearchTerm] = useState('');
  const [sortOption, setSortOption] = useState('all');

  const sortOptions = [
    { value: 'all', label: '전체' },
    { value: 'open', label: '모집중' },
    { value: 'closed', label: '모집마감' },
  ];

  /* 새 클럽 생성 */
  const addClub = (newClub) => {
    dummyBookclubs.unshift(newClub);
    setClubs([...dummyBookclubs]);
    setCurrentPage(1);                 // 첫 페이지로
  };

  return (
    <>
      <div
        className="page-wrapper"
        style={{ display: 'flex', justifyContent: 'center' }}
      >
        {/* 제목 */}
        <h2 className="page-title">북클럽</h2>
        <p className="page-subtitle">책으로 이어지는 우리들의 이야기</p>

        <div style={{ marginLeft: '13%' }}>
          {/* 검색/정렬/만들기 */}
          <div
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'flex-end',
              marginTop: '20px',
            }}
          >
            <SearchBar
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />

            <div style={{ display: 'flex', gap: '10px', marginRight: '10px' }}>
              <SortDropdown
                value={sortOption}
                onChange={(e) => setSortOption(e.target.value)}
                options={sortOptions}
              />
              <button
                style={{
                  background: '#B4C9A4',
                  opacity: '0.8',
                  padding: '8px 18px',
                  fontSize: '14px',
                  border: 'none',
                  borderRadius: '6px',
                  cursor: 'pointer',
                }}
                onClick={() => setShowModal(true)}   /* ★ 모달 열기 */
              >
                만들기
              </button>
            </div>
          </div>

          {/* 북클럽 카드 리스트 */}
          <div className="bookclublist">
            {currentClubs.map((club) => (
              <BookclubCard
                key={club.id}
                bookclub={club}
                onClick={() => navigate(`/bookclub/${club.id}`)}
              />
            ))}
          </div>

          {/* 페이지네이션 */}
          <div style={{ textAlign: 'center', marginTop: '20px' }}>
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={setCurrentPage}
            />
          </div>
        </div>
      </div>

      {/* ─── 만들기 모달 ─── */}
      {showModal && (
        <BookclubCreateModal
          onClose={() => setShowModal(false)}
          onCreate={addClub}
        />
      )}
    </>
  );
}

export default Bookclub;