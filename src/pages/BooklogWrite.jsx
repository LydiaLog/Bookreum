import { useState } from 'react';
import api from '../axiosConfig';
import { useNavigate, useLocation } from 'react-router-dom';
import searchbookIcon from '../assets/search_book.svg';
import BookSearchModal from '../components/BookSearchModal';
import '../styles/BooklogWrite.css';

function BooklogWrite() {
  /* ─── 라우팅 상태 (수정 모드 여부) ─── */
  const { state } = useLocation();
  const editingLog = state?.log ?? null;
  const isEdit = Boolean(editingLog);

  /* ─── 폼 상태 ─── */
  const [title, setTitle] = useState(editingLog?.title || '');
  const [content, setContent] = useState(editingLog?.content || '');
  const [selectedBook, setSelectedBook] = useState(
    editingLog
      ? {
          title: editingLog.bookTitle,
          author: editingLog.bookAuthor,
          isbn13: editingLog.bookIsbn,
          cover: editingLog.coverUrl,
          itemId: editingLog.bookId,
        }
      : null
  );
  const [showBookModal, setShowBookModal] = useState(false);

  /* ─── 기타 ─── */
  const navigate = useNavigate();
  const currentUser = '나'; // TODO: 실제 로그인 유저로 교체

  /* ──────────────────────────────────────────────────────────── */
  /*                         제출 핸들러                         */
  /* ──────────────────────────────────────────────────────────── */
  const handleSubmit = async () => {
    if (!title.trim())   { alert('제목을 입력하세요.');   return; }
    if (!content.trim()) { alert('내용을 입력하세요.');   return; }
    if (!selectedBook)   { alert('책을 선택하세요.');    return; }

    const formData = new FormData();
    const generatedId = !isEdit
      ? (crypto.randomUUID ? crypto.randomUUID() : `${Date.now()}-${Math.random().toString(16).slice(2)}`)
      : editingLog.id;

    formData.append('id', generatedId);
    formData.append('title', title.trim());
    formData.append('content', content.trim());
    formData.append('nickname', currentUser);
    formData.append('date', new Date().toISOString().slice(0, 10));

    /* 책 메타 */
    formData.append('bookTitle',  selectedBook.title);
    formData.append('bookAuthor', selectedBook.author);
    formData.append('bookIsbn',   selectedBook.isbn13);
    formData.append('coverUrl',   selectedBook.cover);   // ⬅️ 표지 자동 첨부
    if (selectedBook.itemId != null) formData.append('bookId', selectedBook.itemId);

    try {
      const res = isEdit
        ? await api.put(`/api/posts/${editingLog.id}`, formData, { headers:{'Content-Type':'multipart/form-data'} })
        : await api.post('/api/posts', formData,        { headers:{'Content-Type':'multipart/form-data'} });

      const savedId = res.data.id || generatedId;
      navigate(`/booklog/${savedId}`);
    } catch (err) {
      console.error('Failed to save booklog:', err.response?.data || err.message);
      alert('저장에 실패했습니다. 다시 시도해 주세요.');
    }
  };

  /* ──────────────────────────────────────────────────────────── */
  /*                         책 저장 핸들러                       */
  /* ──────────────────────────────────────────────────────────── */
  const handleBookSelect = async (book) => {
    try {
      const res = await api.post('/api/posts/saveBook', book);
      const saved = res.data;
      setSelectedBook({
        title: saved.title,
        author: saved.author,
        isbn13: book.isbn13,
        cover: saved.coverImageUrl,  // 서버가 돌려준 표지 URL
        itemId: saved.id,
      });
      setShowBookModal(false);
    } catch (err) {
      console.error('Failed to save selected book:', err.response?.data || err.message);
      alert('책 저장에 실패했습니다. 다시 시도해 주세요.');
    }
  };

  /* ─── UI ─── */
  return (
    <div>
      <div className="page-wrapper">
        <h2 className="page-title">{isEdit ? '글 수정' : '글쓰기'}</h2>
        <p className="page-subtitle">{isEdit ? '내용을 수정하세요 :)' : '당신만의 책 이야기를 기록하세요 :)'}</p>
      </div>

      <div className="booklog-write-container">
        <input
          className="write-input-title"
          type="text"
          placeholder="제목을 입력하세요."
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />

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
          {selectedBook
            ? `${selectedBook.title} - ${selectedBook.author}`
            : '무슨 책을 읽었나요?'}
        </div>

        {/* 이미지 업로드 영역 제거됨 */}

        <textarea
          className="write-textarea"
          placeholder="내용을 입력하세요."
          value={content}
          onChange={(e) => setContent(e.target.value)}
        />

        <div className="write-buttons">
          <button className="submit-button" onClick={handleSubmit}>
            {isEdit ? '수정 완료' : '올리기'}
          </button>
          {isEdit && (
            <button className="cancel-button" onClick={() => navigate(-1)}>
              취소
            </button>
          )}
        </div>
      </div>

      {showBookModal && (
        <BookSearchModal
          onClose={() => setShowBookModal(false)}
          onSelect={handleBookSelect}
        />
      )}
    </div>
  );
}

export default BooklogWrite;