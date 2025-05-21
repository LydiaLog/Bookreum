// components/BookRecCard.jsx
// (React 17+·Vite 기본 설정이라면 import React 생략 가능)
import '../styles/BookRecCard.css';

/**
 * @param {{
 *   books: Array<{id:string,title:string,author:string,genre:string,coverUrl:string}>,
 *   onMoreClick: () => void
 * }} props
 */
function BookRecCard({ books, onMoreClick }) {
  return (
    <section className="ai-book-rec">
      <h2 className="ai-book-rec__title">˚₊‧ 이런 책을 좋아하실 것 같아요˚₊‧</h2>

      <ul className="ai-book-rec__list">
        {books.slice(0, 2).map((b, index) => (
          <li 
            key={b.id} 
            className={`ai-book-rec__item${index === 1 ? ' reverse' : ''}`}
          >
            <img className="ai-book-rec__img" src={b.coverUrl} alt={"책 표지 이미지"} />
            <div className="ai-book-rec__text">
              <p>제목&nbsp;: {b.title.length > 20 ? b.title.slice(0, 20) + '...' : b.title}</p>
              <p>작가&nbsp;: {b.author}</p>
              <p>장르&nbsp;: {b.genre}</p>
            </div>
          </li>
        ))}
      </ul>

      <button className="ai-book-rec__more" onClick={onMoreClick}>
        더보기 <span aria-hidden="true">▶</span>
      </button>
    </section>
  );
}

export default BookRecCard;