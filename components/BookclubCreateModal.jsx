import { useState } from 'react';
import BookSearchModal from '../components/BookSearchModal';
import '../styles/BookclubCreateModal.css';

function BookclubCreateModal({ onClose, onCreate }) {
  /* 입력 state */
  const [clubName, setClubName] = useState('');
  const [capacity, setCapacity] = useState(2);
  const [deadline, setDeadline] = useState('');
  const [bookTitle, setBookTitle] = useState('');
  const [bookAuthor, setBookAuthor] = useState('');
  const [coverFile, setCoverFile] = useState(null);

  /* 책 검색 모달 */
  const [showBookModal, setShowBookModal] = useState(false);

  /* 대표 이미지 */
  const handleImage = (e) => {
    const f = e.target.files[0];
    if (f) setCoverFile(f);
  };

  /* 만들기 */
  const handleCreate = () => {
    if (!clubName || !bookTitle || !bookAuthor || !deadline)
      return alert('모든 항목을 입력하세요!');
    if (clubName.length > 50)
      return alert('클럽 이름 50자 제한입니다.');
    if (capacity < 2 || capacity > 5)
      return alert('모집 인원은 2~5명!');

    onCreate({
      id: Date.now(),
      title: clubName,
      book: bookTitle,
      author: bookAuthor,
      nickname: '나',
      date: deadline,
      status: 'open',
      capacity,
      coverUrl: coverFile ? URL.createObjectURL(coverFile) : '',
    });
    onClose();
  };

  return (
    <div className="create-backdrop">
      <div className="create-modal">
        <h3>북클럽 만들기</h3>

        {/* 클럽 이름 */}
        <label>
          북클럽 이름&nbsp;({clubName.length}/50)
          <input
            maxLength={50}
            value={clubName}
            onChange={(e) => setClubName(e.target.value)}
          />
        </label>

        {/* 인원 + 마감 날짜 */}
        <div className="row-half">
          <label className="half">
            모집 인원&nbsp;(본인 포함)
            <select
              value={capacity}
              onChange={(e) => setCapacity(+e.target.value)}
            >
              {[2, 3, 4, 5].map((n) => (
                <option key={n}>{n}</option>
              ))}
            </select>
          </label>
          <label className="half">
            마감 날짜
            <input
              type="date"
              value={deadline}
              onChange={(e) => setDeadline(e.target.value)}
            />
          </label>
        </div>

        {/* 책 제목 + 작가 이름 한 줄 */}
        <div className="row-half">
          <label
            className="half search-field"
            onClick={() => setShowBookModal(true)}
          >
            책 제목
            <input readOnly value={bookTitle} />
          </label>

          <label
            className="half search-field"
            onClick={() => setShowBookModal(true)}
          >
            작가 이름
            <input readOnly value={bookAuthor} />
          </label>
        </div>

        {/* 대표 이미지 */}
        <label>
          대표 이미지
          <input type="file" accept="image/*" onChange={handleImage} />
        </label>

        {/* 버튼 */}
        <div className="create-btns">
          <button className="cancel" onClick={onClose}>
            닫기
          </button>
          <button className="confirm" onClick={handleCreate}>
            만들기
          </button>
        </div>
      </div>

      {/* 책 검색 모달 */}
      {showBookModal && (
        <BookSearchModal
          onClose={() => setShowBookModal(false)}
          onSelect={(b) => {
            setBookTitle(b.title);
            setBookAuthor(b.author);
            setShowBookModal(false);
          }}
        />
      )}
    </div>
  );
}

export default BookclubCreateModal;