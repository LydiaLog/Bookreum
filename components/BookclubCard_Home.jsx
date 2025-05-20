import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/BookclubCard_Home.css';

function BookclubCard_Home({ club, onClick, navigateClick = true }) {
  const navigate = useNavigate();

  const internalClick = () => {
    if (onClick) return onClick();
    if (navigateClick) navigate(`/bookclub/${club.id}`);
  };

  const [filledCircles, setFilledCircles] = useState(0);
  const [isClosed, setIsClosed] = useState(false);

  useEffect(() => {
    // 모집 인원 실시간 상태
    setFilledCircles(club.currentMembers || 0);

    // 모집 마감 여부 확인 (오늘 날짜 기준)
    const today = new Date().toISOString().split('T')[0];
    setIsClosed(new Date(today) > new Date(club.date));
  }, [club]);

  return (
    <div className="bookclub-card" onClick={internalClick}>
      <div className="img-container">
        <img
          src={club.imageUrl || '/images/default-club.jpg'} // 기본 이미지
          alt="북클럽 대표 이미지"
          className="bookclub-card__image"
        />
      </div>

      <div className="bookclub-card__divider" />

      <div className="bookclub-card__info">
      <div className="bookclub-card__status-area">
        {/* 모집 마감 상태 */}
        {isClosed ? (
          <p style={{ fontSize: '0.9rem', fontWeight: 'bold', color: '#ff4d4f', textAlign: 'right', marginRight: '10px' }}>
            📕 모집 마감
          </p>
        ) : (
          // 모집 중 상태 - 실시간 인원 현황 표시
          <div style={{ textAlign: 'right' }}>
            <p style={{ fontSize: '0.9rem', fontWeight: 'bold', color: '#888', marginRight: '10px' }}>📢 모집중</p>
            <div style={{ display: 'flex', gap: '4px', justifyContent: 'center', marginTop: '4px' }}>
              {[...Array(club.capacity)].map((_, index) => (
                <div
                  key={index}
                  style={{
                    width: '11px',
                    height: '11px',
                    borderRadius: '50%',
                    marginTop: '-10px',
                    background: index < filledCircles ? '#849974' : '#D9D9D9',
                  }}
                />
              ))}
            </div>
          </div>
        )}
      </div>

        <div className="bookclub-card__middle">
          <div className="bookclub-card__title">{club.title}</div>
          <div className="bookclub-card__book">📖 {club.book}</div>
        </div>

        <div className="bookclub-card__footer">
          <div className="bookclub-card__date">~ {club.date}</div>
        </div>
      </div>
    </div>
  );
}

export default BookclubCard_Home;