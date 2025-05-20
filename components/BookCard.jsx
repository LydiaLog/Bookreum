// src/components/BookCard.jsx
function BookCard({ book, onClick }) {
    const { title, author, thumbnail } = book;
    return (
      <article
        className="book-card"
        onClick={() => onClick?.(book)}
        style={{ cursor: "pointer" }}
      >
        <img src={thumbnail} alt={`${title} 표지`} />
        <div className="info">
          <h4>{title}</h4>
          <p className="author">{author}</p>
          { /* <p className="rating">⭐ {rating}</p> */ }
        </div>
      </article>
    );
  }

export default BookCard;