import React from "react";
import { Link } from "react-router-dom";
import "../styles/BookclubCard_Home.css";

const BookclubCard_Home = ({ club }) => {
  return (
    <Link to={`/clubs/${club.id}`} className="bookclub-card">
      <div className="bookclub-card__image">
        <img src={club.coverImageUrl} alt={club.title} />
      </div>
      <div className="bookclub-card__content">
        <h3 className="bookclub-card__title">{club.title}</h3>
        <p className="bookclub-card__book">{club.bookTitle}</p>
        <p className="bookclub-card__author">{club.bookAuthor}</p>
        <div className="bookclub-card__info">
          <span className="bookclub-card__members">
            {club.minParticipants} / {club.maxParticipants}명
          </span>
          <span className="bookclub-card__date">
            {new Date(club.createdAt).toLocaleDateString()}
          </span>
        </div>
      </div>
    </Link>
  );
};

export default BookclubCard_Home;
