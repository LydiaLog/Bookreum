import { useRef, useState } from 'react';
import api from '../axiosConfig';
import { useNavigate, useLocation } from 'react-router-dom';
import searchbookIcon from '../assets/search_book.svg';
import inputimageIcon from '../assets/input_image.svg';
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
  const [selectedImage, setSelectedImage] = useState(null);
  const [showBookModal, setShowBookModal] = useState(false);

  /* ─── 기타 ─── */
  const fileInputRef = useRef();
  const navigate = useNavigate();
  const currentUser = '나'; // TODO: 실제 로그인 유저로 교체

  /* ──────────────────────────────────────────────────────────── */
  /*                         제출 핸들러                         */
  /* ──────────────────────────────────────────────────────────── */
  const handleSubmit = async () => {
    /* 1. 유효성 검사 */
    if (!title.trim())   { alert('제목을 입력하세요.');   return; }
    if (!content.trim()) { alert('내용을 입력하세요.');   return; }
    if (!selectedBook)   { alert('책을 선택하세요.');    return; }

    /* 2. FormData 구성 */
    const formData = new FormData();
    const generatedId = !isEdit
      ? (crypto.randomUUID ? crypto.randomUUID() : `${Date.now()}-${Math.random().toString(16).slice(2)}`)
      : editingLog.id;

    formData.append('id', generatedId);
    formData.append('title', title.trim());
    formData.append('content', content.trim());
    formData.append('nickname', currentUser);
    formData.append('date', new Date().toISOString().slice(0, 10));

    // ➡︎ 책 관련 메타
    formData.append('bookTitle', selectedBook.title);
    formData.append('bookAuthor', selectedBook.author);
    formData.append('bookIsbn', selectedBook.isbn13);
    formData.append('coverUrl', selectedBook.cover);
    formData.append('bookId', selectedBook.itemId);

    // ➡︎ 사용자 업로드 이미지 (옵션)
    if (selectedImage) formData.append('coverImage', selectedImage);

    /* 3. API 호출 (POST vs PUT) */
    try {
      let response;
      if (isEdit) {
        response = await api.put(`/api/posts/${editingLog.id}`, formData, {
          headers: { 'Content-Type': 'multipart/form-data' },
        });
      } else {
        response = await api.post('/api/posts', formData, {
          headers: { 'Content-Type': 'multipart/form-data' },
        });
      }

      const savedId = response.data.id || generatedId;
      navigate(`/booklog/${savedId}`);
    } catch (error) {
      console.error('Failed to save booklog:', error.response?.data || error.message);
      alert('저장에 실패했습니다. 다시 시도해 주세요.');
    }
  };

  /* ──────────────────────────────────────────────────────────── */
  /*                         책 저장 핸들러                       */
  /* ──────────────────────────────────────────────────────────── */
  const handleBookSelect = async (book) => {
    try {
      const response = await api.post('/api/posts/saveBook', book);
      const savedBook = response.data;

      // 선택된 책 상태 설정
      setSelectedBook({
        title: savedBook.title,
        author: savedBook.author,
        isbn13: book.isbn13,
        cover: savedBook.coverImageUrl,
        itemId: savedBook.id, // 저장된 책 ID
      });

      setShowBookModal(false);
    } catch (error) {
      console.error('Failed to save selected book:', error.response?.data || error.message);
      alert('책 저장에 실패했습니다. 다시 시도해 주세요.');
    }
  };

  /* ─── 이미지 업로드 핸들러 ─── */
  const handleImageClick = () => fileInputRef.current.click();
  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) setSelectedImage(file);
  };

  /* ─── UI ─── */
  return (
    <div>
      <div className="page-wrapper">
        <h2 className="page-title">{isEdit ? '글 수정' : '글쓰기'}</h2>
        <p className="page-subtitle">{isEdit ? '내용을 수정하세요 :)' : '당신만의 책 이야기를 기록하세요 :)'}</p>
      </div>

      <div className="booklog-write-container">
        <input className="write-input-title" type="text" placeholder="제목을 입력하세요." value={title} onChange={(e) => setTitle(e.target.value)} />

        <div className="write-book-select" onClick={() => setShowBookModal(true)} style={{ cursor: 'pointer' }}>
          <img src={searchbookIcon} alt="책 검색" style={{ width: 20, height: 20, marginRight: 8, verticalAlign: 'middle' }} />
          {selectedBook ? `${selectedBook.title} - ${selectedBook.author}` : '무슨 책을 읽었나요?'}
        </div>

        <div className="write-image-upload" onClick={handleImageClick} style={{ cursor: 'pointer' }}>
          <img src={inputimageIcon} alt="이미지 첨부" style={{ width: 25, height: 25, marginRight: 8, verticalAlign: 'middle', opacity: 0.5 }} />
          {selectedImage ? selectedImage.name : '이미지 첨부'}
          <input type="file" accept="image/*" ref={fileInputRef} onChange={handleImageChange} style={{ display: 'none' }} />
        </div>

        <textarea className="write-textarea" placeholder="내용을 입력하세요." value={content} onChange={(e) => setContent(e.target.value)} />

        <div className="write-buttons">
          <button className="submit-button" onClick={handleSubmit}>{isEdit ? '수정 완료' : '올리기'}</button>
          {isEdit && <button className="cancel-button" onClick={() => navigate(-1)}>취소</button>}
        </div>
      </div>

      {showBookModal && (
        <BookSearchModal onClose={() => setShowBookModal(false)} onSelect={handleBookSelect} />
      )}
    </div>
  );
}

export default BooklogWrite;