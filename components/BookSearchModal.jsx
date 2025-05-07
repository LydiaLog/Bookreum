import { useState } from 'react';
import '../styles/BookSearchModal.css';

// 책 더미 데이터 // 추후에 책 정보 데이터 연결 예정
const dummyBooks = [
  { id: 1, title: '모순', author: '양귀자' },
  { id: 2, title: '채식주의자', author: '한강' },
  { id: 3, title: '급류', author: '정대건' },
];

function BookSearchModal({ onClose, onSelect }) {
  const [searchTerm, setSearchTerm] = useState('');
  const [results, setResults] = useState([]);

  const handleSearch = () => {
    const filtered = dummyBooks.filter((book) =>
      book.title.includes(searchTerm)
    );
    setResults(filtered);
  };

  return (
    <div className="book-modal-backdrop" onClick={onClose}>
      <div
        className="book-modal"
        onClick={(e) => e.stopPropagation()}
      >
        <h4>책 검색</h4>

        <div className="search-input-row">
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === 'Enter') handleSearch();
            }}
            placeholder="책 제목 입력"
            className="search-input"
          />
          <button onClick={handleSearch} className="search-button">검색</button>
        </div>

        <ul>
          {results.map((book) => (
            <li
              key={book.id}
              onClick={() => {
                onSelect(book);
                onClose();
              }}
              className="search-result-item"
            >
              {book.title} | {book.author}
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}

export default BookSearchModal;