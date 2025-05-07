import { useParams, useNavigate } from 'react-router-dom';
import { useState } from 'react';
import ConfirmModal from '../components/ConfirmModal';
import dummyBooklogs from '../data/dummyBooklogs';
import '../styles/BooklogDetail.css';

function BooklogDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const log = dummyBooklogs.find((item) => item.id === id);
  const currentUser = '나'; // 로그인 사용자

  const [comments, setComments] = useState([
    { id: 1, nickname: 'booklover', content: '정말 공감돼요.' },
    { id: 2, nickname: 'gamja', content: '이 책 너무 좋아요!' },
  ]);
  const [input, setInput] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [editText, setEditText] = useState('');

  const [showModal, setShowModal] = useState(false);
  const [deleteTargetId, setDeleteTargetId] = useState(null);

  // 글 삭제 모달
  const [showDeletePostModal, setShowDeletePostModal] = useState(false);

  if (!log) return <p>존재하지 않는 북로그입니다 😢</p>;

  // 댓글 추가
  const handleAddComment = () => {
    if (input.trim() === '') return;
    const newComment = {
      id: Date.now(),
      nickname: currentUser,
      content: input.trim(),
    };
    setComments([...comments, newComment]);
    setInput('');
  };

  // 댓글 삭제 요청
  const requestDelete = (id) => {
    setDeleteTargetId(id);
    setShowModal(true);
  };

  const handleConfirmDelete = () => {
    setComments(comments.filter((c) => c.id !== deleteTargetId));
    setShowModal(false);
    setDeleteTargetId(null);
  };

  const handleCancelDelete = () => {
    setShowModal(false);
    setDeleteTargetId(null);
  };

  const handleEdit = (id, content) => {
    setEditingId(id);
    setEditText(content);
  };

  const handleEditSubmit = (id) => {
    setComments(comments.map((c) =>
      c.id === id ? { ...c, content: editText } : c
    ));
    setEditingId(null);
    setEditText('');
  };

  // 글 수정
  const startEdit = () => {
    navigate('/booklogwrite', {state: {log}});
  }

  // 글 삭제
  const confirmPostDelete = () => {
    const index = dummyBooklogs.findIndex((b) => b.id === id);
    if (index !== -1) dummyBooklogs.splice(index, 1);
    navigate('/bookloglist');
  };

  return (
    <div className="booklog-detail">
      {/* 제목 */}
      <h1 className="booklog-detail__title">{log.title}</h1>

      <div className="booklog-detail__meta">
        <span>{log.nickname}</span>
        <span> &nbsp; </span>
        <span>{log.date}</span>
      </div>

      {/* 이미지 + 책 정보 */}
      <figure className="booklog-detail__figure">
        <img src={log.coverUrl} alt="책 표지 또는 이미지" className="booklog-detail__image" />
        <figcaption className="booklog-detail__caption">
          <strong>{log.book}</strong>
          <span className="separator"> | </span>
          <strong>{log.author}</strong>
        </figcaption>
      </figure>

      {/* 본문 */}
      <p className="booklog-detail__content">{log.content}</p>

      {/* 글 수정/삭제 버튼 */}
      {log.nickname === currentUser && (
        <div className="post__actions">
            <button className="log-edit-button" onClick={startEdit}>수정</button>
            <button className="log-del-button" onClick={() => setShowDeletePostModal(true)}>삭제</button>
        </div>
      )}

      {/* 댓글 영역 */}
      <section className="comments">
        <h3>{/*💬*/} 댓글 {comments.length}</h3>
        <ul className="comments__list">
          {comments.map((c) => (
            <li key={c.id}>
              <strong>{c.nickname}</strong>
              {editingId === c.id ? (
                <>
                  <textarea
                    className="comment__edit-textarea"
                    value={editText}
                    onChange={(e) => setEditText(e.target.value)}
                    rows={3}
                  />
                  <button onClick={() => handleEditSubmit(c.id)}>완료</button>
                </>
              ) : (
                <p>{c.content}</p>
              )}

              {c.nickname === currentUser && editingId !== c.id && (
                <div className="comment__actions">
                  <button onClick={() => handleEdit(c.id, c.content)}>수정</button>
                  <button onClick={() => requestDelete(c.id)}>삭제</button>
                </div>
              )}
            </li>
          ))}
        </ul>

        <div className="comments__form">
          <textarea
            className="comments__textarea"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            placeholder="댓글을 입력하세요"
            rows={3}
          />
          <button onClick={handleAddComment}>등록</button>
        </div>

        {/* 댓글 삭제 모달 */}
        {showModal && (
          <ConfirmModal
            message="정말 이 댓글을 삭제하시겠어요?"
            onConfirm={handleConfirmDelete}
            onCancel={handleCancelDelete}
          />
        )}

        {/* 글 삭제 모달 */}
        {showDeletePostModal && (
          <ConfirmModal
            message="정말 이 글을 삭제하시겠어요?"
            onConfirm={confirmPostDelete}
            onCancel={() => setShowDeletePostModal(false)}
          />
        )}
      </section>
    </div>
  );
}

export default BooklogDetail;