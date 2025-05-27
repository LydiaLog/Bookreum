import { useState, useMemo, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../axiosConfig';
import SearchBar from '../components/SearchBar';
import SortDropdown from '../components/SortDropdown';
import BookclubCard from '../components/BookclubCard';
import Pagination from '../components/Pagination';
import BookclubCreateModal from '../components/BookclubCreateModal';
import BookclubJoinModal from '../components/BookclubJoinModal';
import dummyBookclubs from '../data/dummyBookclubs';
import '../styles/Global.css';
import '../styles/Bookclub.css';

function Bookclub() {
  const navigate = useNavigate();

  const today = new Date().setHours(0, 0, 0, 0);
  const getStatus = (dateStr) =>
    new Date(dateStr).setHours(0, 0, 0, 0) < today ? 'closed' : 'open';

  const [clubs, setClubs] = useState(
    dummyBookclubs.map((c) => ({ ...c, status: getStatus(c.date) }))
  );
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [showModal, setShowModal] = useState(false);
  const [showJoinModal, setShowJoinModal] = useState(false);
  const [selectedClub, setSelectedClub] = useState(null);
  const [isJoinDisabled, setIsJoinDisabled] = useState(false); // ✅ 복원됨

  const [query, setQuery] = useState('');
  const [keyword, setKeyword] = useState('');

  const [sortOption, setSortOption] = useState('all');
  const [page, setPage] = useState(1);
  const pageSize = 6;

  const { pageData, totalPages } = useMemo(() => {
    let list = clubs.filter((c) => {
      const hay = `${c.title} ${c.book} ${c.author} ${c.nickname}`.toLowerCase();
      return hay.includes(keyword.toLowerCase());
    });

    if (sortOption === 'open') list = list.filter((c) => c.status === 'open');
    if (sortOption === 'closed') list = list.filter((c) => c.status === 'closed');
    if (sortOption !== 'all') list = list.filter((c) => c.status === sortOption);

    const start = (page - 1) * pageSize;
    const slice = list.slice(start, start + pageSize);
    return { pageData: slice, totalPages: Math.ceil(list.length / pageSize) };
  }, [clubs, keyword, sortOption, page]);

  useEffect(() => {
    (async () => {
      try {
        const { data } = await api.get('/api/clubs', {
          params: {
            includeBook: true,
          },
        });
        console.log('북클럽 목록 응답:', data);

        let clubsData = [];
        if (data && Array.isArray(data)) {
          clubsData = data;
        } else if (data && data.content && Array.isArray(data.content)) {
          clubsData = data.content;
        } else {
          console.error('예상치 못한 데이터 구조:', data);
          setError('데이터 형식이 올바르지 않습니다.');
          return;
        }

        setClubs(clubsData);
      } catch (err) {
        console.error('Failed to load bookclubs:', err);
        setError('북클럽을 불러오지 못했습니다.');
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const addClub = (club) => {
    const withStatus = { ...club, status: getStatus(club.date) };
    setClubs((prev) => [withStatus, ...prev]);
    setKeyword('');
    setQuery('');
    setPage(1);
  };

  const openJoinModal = (club) => {
    const isClosed = club.status === 'closed'; // ✅ 훨씬 안전하고 명확
  
    setSelectedClub(club);
    setIsJoinDisabled(isClosed);
    setShowJoinModal(true);
  };
  
  const joinClub = () => {
    if (!selectedClub) return;
    navigate(`/bookclub/${selectedClub.id}`);
    setShowJoinModal(false);
  };

  const sortOptions = [
    { value: 'all', label: '전체' },
    { value: 'open', label: '모집중' },
    { value: 'closed', label: '모집마감' },
  ];

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
          <div
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'flex-end',
              marginTop: 20,
            }}
          >
            <SearchBar
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              onSearch={commitSearch}
            />

            <div style={{ display: 'flex', gap: 10, marginRight: 10 }}>
              <SortDropdown
                value={sortOption}
                onChange={(e) => {
                  setSortOption(e.target.value);
                  setPage(1);
                }}
                options={sortOptions}
              />
              <button
                style={{
                  background: '#B4C9A4',
                  opacity: 0.8,
                  padding: '8px 18px',
                  fontSize: 14,
                  border: 'none',
                  borderRadius: 6,
                  cursor: 'pointer',
                }}
                onClick={() => setShowModal(true)}
              >
                만들기
              </button>
            </div>
          </div>

          {/* 상태 표시 */}
          {loading && <p style={{ textAlign: 'center', marginTop: 40 }}>로딩 중…</p>}
          {error && (
            <p style={{ textAlign: 'center', marginTop: 40, color: 'red' }}>{error}</p>
          )}

          {/* 카드 리스트 */}
          {!loading && !error && (
            <>
              <div className={`bookclublist ${pageData.length === 0 ? 'empty' : ''}`}>
                {pageData.length > 0 &&
                  pageData.map((club) => (
                    <BookclubCard
                      key={club.id}
                      bookclub={{
                        ...club,
                        book: club.book?.title || '',
                        author: club.book?.author || '',
                        nickname: club.createdByNickname || '',
                        date: club.applicationDeadline
                          ? new Date(club.applicationDeadline).toISOString().split('T')[0]
                          : '',
                        capacity: club.maxParticipants || 0,
                        currentMembers: club.currentParticipants || 0,
                        coverUrl: club.coverImageUrl || '',
                        description: club.description || '',
                        leader: {
                          nickname: club.createdByNickname || '',
                          profileImageUrl: club.createdByProfileImageUrl || '',
                        },
                      }}
                      onClick={() => openJoinModal(club)}
                    />
                  ))}

                {pageData.length === 0 && (
                  <div className="no-results-message">검색 결과가 없습니다.</div>
                )}
              </div>

              {/* 페이지네이션 */}
              {totalPages > 1 && (
                <div style={{ textAlign: 'center', marginTop: 20 }}>
                  <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
                </div>
              )}
            </>
          )}
        </div>
      </div>

      {/* 만들기 모달 */}
      {showModal && (
        <BookclubCreateModal onClose={() => setShowModal(false)} onCreate={addClub} />
      )}

      {/* 참여하기 모달 */}
      {showJoinModal && selectedClub && (
        <BookclubJoinModal
          club={{
            ...selectedClub,
            book: selectedClub.bookTitle || '',
            author: selectedClub.bookAuthor || '',
            nickname: selectedClub.createdByNickname || '',
            date: selectedClub.applicationDeadline
              ? new Date(selectedClub.applicationDeadline).toISOString().split('T')[0]
              : '',
            description: selectedClub.description || '',
            coverUrl: selectedClub.coverImageUrl || '',
            leader: {
              nickname: selectedClub.createdByNickname || '',
              profileImageUrl: selectedClub.createdByProfileImageUrl || '',
            },
          }}
          onClose={() => setShowJoinModal(false)}
          onJoin={joinClub}
          isJoinDisabled={isJoinDisabled} // ✅ 이거까지 완벽하게 복구
        />
      )}
    </>
  );
}

export default Bookclub;