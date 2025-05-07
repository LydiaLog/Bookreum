import { useNavigate } from 'react-router-dom';
import '../styles/BookclubCard_Home.css';

function BookclubCard_Home({ club }) {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/bookclub/${club.id}`);
  };

  const isFull = club.currentMembers >= club.maxMembers;
  const statusText = isFull ? '📕 모집마감' : '📢 모집중';
  const statusClass = isFull ? 'closed' : 'open';

  return (
    <div className="bookclub-card" onClick={handleClick}>
      <div className="img-container">
        <img
          src={club.imageUrl}
          alt="북클럽 대표 이미지"
          className="bookclub-card__image"
        />
      </div>
      <div className="bookclub-card__divider" />
      <div className="bookclub-card__info">
        <div className={`bookclub-card__status ${statusClass}`}>{statusText}</div>
        <div className="bookclub-card__middle">
          <div className="bookclub-card__title">{club.title}</div>
          <div className="bookclub-card__book">📖 {club.bookTitle}</div>
        </div>
        <div className="bookclub-card__date">{club.dateRange}</div>
      </div>
    </div>
  );
}

export default BookclubCard_Home;