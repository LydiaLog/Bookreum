import { useRef, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import searchbookIcon from '../assets/search_book.svg';
import inputimageIcon from '../assets/input_image.svg';
import BookSearchModal from '../components/BookSearchModal';
import dummyBooklogs from '../data/dummyBooklogs';
import '../styles/BooklogWrite.css';

function BooklogWrite() {
  /* ───── 수정 or 새 글 판단 ───── */
  const { state } = useLocation();
  const editingLog = state?.log ?? null;      // undefined → 새 글
  const isEdit = Boolean(editingLog);

  /* ───── 입력값 State ───── */
  const [title, setTitle] = useState(editingLog?.title || '');
  const [content, setContent] = useState(editingLog?.content || '');
  const [selectedBook, setSelectedBook] = useState(
    editingLog ? { title: editingLog.book, author: editingLog.author } : null
  );
  const [selectedImage, setSelectedImage] = useState(null);
  const [showBookModal, setShowBookModal] = useState(false);

  const fileInputRef = useRef();
  const navigate = useNavigate();
  const currentUser = '나';

  /* ───── 이미지 선택 ───── */
  const handleImageClick = () => fileInputRef.current.click();
  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) setSelectedImage(file);
  };

  /* ───── 제출 ───── */
  const handleSubmit = () => {
    if (isEdit) {
      /* 기존 글 덮어쓰기 */
      const idx = dummyBooklogs.findIndex((b) => b.id === editingLog.id);
      if (idx !== -1) {
        dummyBooklogs[idx] = {
          ...dummyBooklogs[idx],
          title,
          content,
          book: selectedBook?.title || '',
          author: selectedBook?.author || '',
        };
      }
      /* 수정 완료 → 해당 글 상세 */
      navigate(`/booklog/${editingLog.id}`);
    } else {
      /* 새 글 추가 */
      const newId = Date.now().toString();
      dummyBooklogs.unshift({
        id: newId,
        title,
        content,
        nickname: currentUser,
        date: new Date().toISOString().slice(0, 10),
        coverUrl: selectedImage ? URL.createObjectURL(selectedImage) : '',
        book: selectedBook?.title || '',
        author: selectedBook?.author || '',
      });
      navigate(`/booklog/${newId}`);            // 작성 후 방금 글 상세
    }
  };

  /* ───── 취소: 바로 이전 화면(상세)로 ───── */
  const handleCancel = () => navigate(-1);

  return (
    <div>
      <div className="page-wrapper">
        <h2 className="page-title">{isEdit ? '글 수정' : '글쓰기'}</h2>
        <p className="page-subtitle">
          {isEdit ? '내용을 수정하세요 :)' : '당신만의 책 이야기를 기록하세요 :)'}
        </p>
      </div>

      <div className="booklog-write-container">
        <input
          className="write-input-title"
          type="text"
          placeholder="제목"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />

        {/* 책 선택 */}
        <div
          className="write-book-select"
          onClick={() => setShowBookModal(true)}
          style={{ cursor: 'pointer' }}
        >
          <img
            src={searchbookIcon}
            alt="책 검색"
            style={{ width: 20, height: 20, marginRight: 8, verticalAlign: 'middle' }}
          />
          {selectedBook ? selectedBook.title : '무슨 책을 읽었나요?'}
        </div>

        {/* 이미지 첨부 */}
        <div
          className="write-image-upload"
          onClick={handleImageClick}
          style={{ cursor: 'pointer' }}
        >
          <img
            src={inputimageIcon}
            alt="이미지 첨부"
            style={{
              width: 25,
              height: 25,
              marginRight: 8,
              verticalAlign: 'middle',
              opacity: 0.5,
            }}
          />
          {selectedImage
            ? selectedImage.name
            : isEdit && editingLog.coverUrl
            ? '기존 이미지 유지'
            : '이미지 첨부'}
          <input
            type="file"
            accept="image/*"
            ref={fileInputRef}
            onChange={handleImageChange}
            style={{ display: 'none' }}
          />
        </div>

        {/* 본문 */}
        <textarea
          className="write-textarea"
          placeholder="내용을 입력하세요."
          value={content}
          onChange={(e) => setContent(e.target.value)}
        />

        {/* 버튼 */}
        {isEdit ? (
          <div className="write-buttons">
            <button className="resubmit-button" onClick={handleSubmit}>
              수정 완료
            </button>
            <button className="cancel-button" onClick={handleCancel}>
              취소
            </button>
          </div>
        ) : (
          <button className="submit-button" onClick={handleSubmit}>
            올리기
          </button>
        )}
      </div>

      {/* 책 검색 모달 */}
      {showBookModal && (
        <BookSearchModal
          onClose={() => setShowBookModal(false)}
          onSelect={(book) => setSelectedBook(book)}
        />
      )}
    </div>
  );
}

export default BooklogWrite;