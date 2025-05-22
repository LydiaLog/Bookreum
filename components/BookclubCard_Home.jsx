import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/BookclubCard_Home.css';

function BookclubCard_Home({ club, onClick, navigateClick = true }) {
  const navigate = useNavigate();

  const [filledCircles, setFilledCircles] = useState(0);
  const [isClosed, setIsClosed] = useState(false);

  useEffect(() => {
    // 현재 참여 인원
    setFilledCircles(club.currentMembers || 0);

    // 날짜 비교 (시간 0시 기준)
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const deadline = new Date(club.date);
    deadline.setHours(0, 0, 0, 0);

    setIsClosed(today > deadline);
  }, [club]);

  const internalClick = () => {
    if (onClick) {
      onClick();
    } else if (navigateClick) {
      navigate(`/bookclub/${club.id}`);
    }
  };

  return (
    <div
      className="bookclub-card"
      onClick={internalClick}
      role="button"
      tabIndex={0}
      onKeyDown={(e) => e.key === 'Enter' && internalClick()}
    >
      <div className="img-container">
        <img
          src={club.coverUrl || '/images/default-club.jpg'}
          alt="북클럽 대표 이미지"
          className="bookclub-card__image"
        />
      </div>

      <div className="bookclub-card__divider" />

      <div className="bookclub-card__info">
        <div className="bookclub-card__status-area">
          {isClosed ? (
            <p className="bookclub-card__status-closed">📕 모집 마감</p>
          ) : (
            <div className="bookclub-card__status-open">
              <p>📢 모집중</p>
              <div className="bookclub-card__circles">
                {[...Array(club.capacity)].map((_, index) => (
                  <div
                    key={index}
                    className="bookclub-status-dot"
                    style={{
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