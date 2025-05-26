import { useNavigate } from "react-router-dom";
import "../styles/BooklogCard_Home.css";

function BooklogCard_Home({ log }) {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/booklog/${log.id}`);
  };

  const truncate = (text, max = 300) =>
    text.length <= max ? text : text.slice(0, max) + "…";

  return (
    <div className="booklog-card" onClick={handleClick}>
      <img
        src={
          log.imageUrl ||
          log.coverUrl ||
          "https://via.placeholder.com/75x95/f0f0f0/888888?text=책"
        }
        alt={"책 표지 이미지"}
        className="booklog-card__image"
      />
      <div className="booklog-card__content">
        <h3 className="booklog-card__title">{log.title}</h3>
        <div className="booklog-card__meta">
          <span>{log.date}</span>
          <span> &nbsp; </span>
          <span>{log.nickname}</span>
        </div>
        <p className="booklog-card__text">{truncate(log.content)}</p>
      </div>
    </div>
  );
}

export default BooklogCard_Home;
