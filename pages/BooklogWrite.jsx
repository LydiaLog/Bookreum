import { useRef, useState } from 'react';
import api from '../axiosConfig';
import { useNavigate, useLocation } from 'react-router-dom';
import searchbookIcon from '../assets/search_book.svg';
import inputimageIcon from '../assets/input_image.svg';
import BookSearchModal from '../components/BookSearchModal';
import '../styles/BooklogWrite.css';

function BooklogWrite() {
  const { state } = useLocation();
  const editingLog = state?.log ?? null;
  const isEdit = Boolean(editingLog);

  const [title, setTitle] = useState(editingLog?.title || '');
  const [content, setContent] = useState(editingLog?.content || '');
  // 선택된 책 객체 통째로 보관
  const [selectedBook, setSelectedBook] = useState(
    editingLog
      ? {            // 글 수정 시
          title:  editingLog.bookTitle,
          author: editingLog.bookAuthor,
          isbn13: editingLog.bookIsbn,
          cover:  editingLog.coverImageUrl
        }
      : null
  );
  const [selectedImage, setSelectedImage] = useState(null);
  const [showBookModal, setShowBookModal] = useState(false);

  const fileInputRef = useRef();
  const navigate = useNavigate();
  const currentUser = '나';

  // ✅ FormData로 서버에 전송
  const handleSubmit = async () => {
    if (!title.trim()) {
      alert("제목을 입력하세요.");
      return;
    }

    if (!content.trim()) {
      alert("내용을 입력하세요.");
      return;
    }

    if (!selectedBook) {
      alert("책을 선택하세요.");
      return;
    }

    const formData = new FormData();
    formData.append("title",    title.trim());
    formData.append("content",  content.trim());
    formData.append("nickname", currentUser);
    formData.append("date",     new Date().toISOString().slice(0, 10));

    if (selectedBook) {
      formData.append("bookTitle",  selectedBook.title);
      formData.append("bookAuthor", selectedBook.author);
      formData.append("bookIsbn",   selectedBook.isbn13);
      formData.append("coverUrl",   selectedBook.cover);   // 백엔드가 URL로 받는다면
      formData.append('bookId',     selectedBook.itemId);
    }

    // 별도 업로드 이미지가 있다면
    if (selectedImage) formData.append("coverImage", selectedImage);


    console.log("FormData Debug:", [...formData.entries()]);

    try {
      const response = await api.post('/api/posts', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      console.log('Create Response:', response.data);
      navigate(`/booklog/${response.data.id}`);
    } catch (error) {
      console.error('Failed to save booklog:', error.response?.data || error.message);
    }
  };

  // 이미지 업로드 핸들러
  const handleImageClick = () => fileInputRef.current.click();
  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) setSelectedImage(file);
  };

  // 책 선택 핸들러
  const handleBookSelect = (book) => {
    setSelectedBook(book);
    setShowBookModal(false);
  };

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
          placeholder="제목을 입력하세요."
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />

        <div className="write-book-select" onClick={() => setShowBookModal(true)} style={{ cursor: 'pointer' }}>
          <img src={searchbookIcon} alt="책 검색" style={{ width: 20, height: 20, marginRight: 8, verticalAlign: 'middle' }} />
          {selectedBook ? `${selectedBook.title} - ${selectedBook.author}` : '무슨 책을 읽었나요?'}
        </div>

        <div className="write-image-upload" onClick={handleImageClick} style={{ cursor: 'pointer' }}>
          <img src={inputimageIcon} alt="이미지 첨부" style={{ width: 25, height: 25, marginRight: 8, verticalAlign: 'middle', opacity: 0.5 }} />
          {selectedImage ? selectedImage.name : '이미지 첨부'}
          <input type="file" accept="image/*" ref={fileInputRef} onChange={handleImageChange} style={{ display: 'none' }} />
        </div>

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
          onSelect={handleBookSelect} // ✅ 선택된 책 설정
        />
      )}
    </div>
  );
}

export default BooklogWrite;