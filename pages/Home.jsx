import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import BookclubCard_Home from '../components/BookclubCard_Home';
import BooklogCard_Home from '../components/BooklogCard_Home';
import BookRecCard from '../components/BookRecCard';
import BookclubJoinModal from '../components/BookclubJoinModal';
import "../styles/Home.css";

import dummyBookclubs from '../data/dummyBookclubs';
import dummyBooklogs from '../data/dummyBooklogs';
import summer from '../assets/summer.jpg';
import jakbyeol from '../assets/jakbyeol.jpg';

// ✅ 더미 북클럽 데이터 (2개 고정)
const dummyClubs = dummyBookclubs;

// ✅ 더미 추천 책 데이터
const dummyBooks = [
  {
    id: '1',
    title: '여름을 한 입 베어 물었더니',
    author: '이꽃님',
    genre: '장편소설',
    cover: summer,
  },
  {
    id: '2',
    title: '작별하지 않는다',
    author: '한강',
    genre: '장편소설',
    cover: jakbyeol,
  },
];

// 더미 북로그 데이터
const dummyLogs = dummyBooklogs;
  
function Home() {
  const [books, setBooks] = useState(null);
  const [showJoinModal, setShowJoinModal] = useState(false);
  const [selectedClub, setSelectedClub] = useState(null);
  const [isJoinDisabled, setIsJoinDisabled] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    setBooks(dummyBooks);
  }, []);

  const openJoinModal = (club) => {
    const today = new Date().toISOString().split('T')[0];
    const isClosed = new Date(today) > new Date(club.date);

    setSelectedClub(club);
    setIsJoinDisabled(isClosed);
    setShowJoinModal(true);
  };

  const handleJoin = () => {
    if (selectedClub) {
      navigate(`/bookclub/${selectedClub.id}`);
    }
    setShowJoinModal(false);
  };

  if (books === null) return <p>로딩 중…</p>;
  if (books.length === 0) return <p>추천을 불러오지 못했어요 😥</p>;

  return (
    <div className="home-contents">
      <div className="home-left">
        {/* 📚 북클럽 모임 섹션 */}
        <section className="club-section">
          <div className="club-section__title-line">
            <hr />
            <h2>⋆｡°✩ 지금 북그러움에서 열린 모임들 ⋆｡°✩</h2>
            <hr />
          </div>
          <p className="club-section__subtitle">조용히 함께 읽는 공간, 여기에 있어요</p>

          <div className="club-section__cards">
            {dummyClubs.slice(0, 2).map((club) => (
              <BookclubCard_Home 
                key={club.id} 
                club={club} 
                onClick={() => openJoinModal(club)}
              />
            ))}
          </div>

          <div className="club-section__more">
            <button onClick={() => navigate('/bookclub')}>
              더보기 <span aria-hidden="true">▶</span>
            </button>
          </div>
        </section>

        {/* 📖 북로그 미리보기 섹션 */}
        <section className="booklog-section">
          <div className="booklog-section__title-line">
            <hr />
            <h2>⋆｡°✩ 북그러움 유저들이 머물다 간 이야기 ⋆｡°✩</h2>
            <hr />
          </div>
          <p className="booklog-section__subtitle">책에서 잠깐 멈춰, 마음을 적어보았어요</p>

          <div className="booklog-section__cards">
            {dummyLogs.slice(0, 2).map((log) => (
              <BooklogCard_Home key={log.id} log={log} />
            ))}
          </div>

          <div className="booklog-section__more">
            <button onClick={() => navigate('/bookloglist')}>
              더보기 <span aria-hidden="true">▶</span>
            </button>
          </div>
        </section>
      </div>

      {/* 🤖 AI 책 추천 */}
      <div className="rec-container">
        <BookRecCard books={books} onMoreClick={() => navigate('/recommend')} />
      </div>

      {/* 📌 북클럽 참여 모달 */}
      {showJoinModal && selectedClub && (
        <BookclubJoinModal
          club={selectedClub}
          onClose={() => setShowJoinModal(false)}
          onJoin={handleJoin}
          isJoinDisabled={isJoinDisabled}
        />
      )}
    </div>
  );
}

export default Home;