// src/components/BookModal.jsx
import { createPortal } from "react-dom";
import "../styles/BookModal.css";

function BookModal({ book, onClose }) {
  const { title, author, summary, thumbnail } = book;

  return createPortal(
    <div className="modal-backdrop" onClick={onClose}>
      <div
        className="modal-content"
        onClick={(e) => e.stopPropagation()}  // 백드롭 클릭만 닫힘
      >
        <button className="close-btn" onClick={onClose}>
          ✕
        </button>

        <img src={thumbnail} alt={`${title} 표지`} />

        <div className="text">
          <h3>{title}</h3>
          <p className="author">{author}</p>
          {/* <p className="genre">장르: {genre}</p>
          <p className="rating">⭐ {rating}</p> */}
          <p className="summary">{summary}</p>
        </div>
      </div>
    </div>,
    document.body            // 포털: body로 렌더
  );
}

export default BookModal;