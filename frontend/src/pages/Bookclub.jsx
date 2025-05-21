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

  const [clubs, setClubs] = useState(dummyBookclubs);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [showModal, setShowModal] = useState(false);
  const [showJoinModal, setShowJoinModal] = useState(false);
  const [selectedClub, setSelectedClub] = useState(null);
  const [isJoinDisabled, setIsJoinDisabled] = useState(false);

  const [query, setQuery] = useState('');
  const [keyword, setKeyword] = useState('');

  const [sortOption, setSortOption] = useState('all');
  const [page, setPage] = useState(1);
  const pageSize = 6;

  useEffect(() => {
    (async () => {
      try {
        const { data } = await api.get('/api/clubs', {
          params: { includeBook: true },
        });

        const clubsData = Array.isArray(data) ? data : data?.content || [];
        setClubs(clubsData);
      } catch (err) {
        console.error('Failed to load bookclubs:', err);
        setError('북클럽을 불러오지 못했습니다.');
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const { pageData, totalPages } = useMemo(() => {
    let list = clubs.filter((c) => {
      const hay = `${c.title} ${c.book?.title} ${c.book?.author} ${c.createdByNickname}`.toLowerCase();
      return hay.includes(keyword.toLowerCase());
    });

    if (sortOption !== 'all') {
      list = list.filter((c) => c.status === sortOption);
    }

    const start = (page - 1) * pageSize;
    const slice = list.slice(start, start + pageSize);
    return { pageData: slice, totalPages: Math.ceil(list.length / pageSize) };
  }, [clubs, keyword, sortOption, page]);

  const addClub = (club) => {
    setClubs((prev) => [club, ...prev]);
    setKeyword('');
    setQuery('');
    setPage(1);
  };

  const openJoinModal = (club) => {
    const isClosed = club.status === 'closed';
    setSelectedClub(club);
    setIsJoinDisabled(isClosed);
    setShowJoinModal(true);
  };

  const joinClub = () => {
    if (selectedClub) {
      navigate(`/bookclub/${selectedClub.id}`);
      setShowJoinModal(false);
    }
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
          {/* 검색/정렬/버튼 */}
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

          {loading && <p style={{ textAlign: 'center', marginTop: 40 }}>로딩 중…</p>}
          {error && <p style={{ textAlign: 'center', marginTop: 40, color: 'red' }}>{error}</p>}

          {!loading && !error && (
            <>
              <div className={`bookclublist ${pageData.length === 0 ? 'empty' : ''}`}>
                {pageData.map((club) => (
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
                      status: club.status || '',
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

      {/* 참여 모달 */}
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
          isJoinDisabled={isJoinDisabled}
        />
      )}
    </>
  );
}

export default Bookclub;
