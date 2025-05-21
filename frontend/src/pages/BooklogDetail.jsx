import { useParams, useNavigate } from 'react-router-dom';
import { useState } from 'react';
import ConfirmModal from '../components/ConfirmModal';
import dummyBooklogs from '../data/dummyBooklogs';
import '../styles/BooklogDetail.css';

function BooklogDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const log = dummyBooklogs.find((item) => item.id === id);
  const currentUser = '나';

  /* 글 좋아요 상태 */
  const [postLikes, setPostLikes] = useState(log?.likes || 0);
  const [hasLikedPost, setHasLikedPost] = useState(false);

  /* 댓글 */
  const [comments, setComments] = useState([
    { id: 1, nickname: 'booklover', content: '정말 공감돼요.', likes: 2, liked: false },
    { id: 2, nickname: 'gamja',     content: '이 책 너무 좋아요!', likes: 1, liked: false },
  ]);
  const [input, setInput] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [editText, setEditText]   = useState('');

  /* 삭제 모달 */
  const [showModal, setShowModal] = useState(false);
  const [deleteTargetId, setDeleteTargetId] = useState(null);
  const [showDeletePostModal, setShowDeletePostModal] = useState(false);

  if (!log) return <p>존재하지 않는 북로그입니다 😢</p>;

  /* ─── 좋아요 핸들러 ─── */
  const togglePostLike = () => {
    setPostLikes(prev => hasLikedPost ? prev - 1 : prev + 1);
    setHasLikedPost(!hasLikedPost);
  };

  const toggleCommentLike = (cid) => {
    setComments(comments.map(c =>
      c.id === cid
        ? { ...c, likes: c.liked ? c.likes - 1 : c.likes + 1, liked: !c.liked }
        : c
    ));
  };

  /* ─── 댓글 CRUD ─── */
  const handleAddComment = () => {
    if (!input.trim()) return;
    setComments([
      ...comments,
      { id: Date.now(), nickname: currentUser, content: input.trim(), likes: 0, liked: false },
    ]);
    setInput('');
  };

  const requestDelete = (cid) => { setDeleteTargetId(cid); setShowModal(true); };
  const handleConfirmDelete = () => {
    setComments(comments.filter(c => c.id !== deleteTargetId));
    setShowModal(false); setDeleteTargetId(null);
  };

  const handleEdit = (cid, content) => { setEditingId(cid); setEditText(content); };
  const handleEditSubmit = (cid) => {
    setComments(comments.map(c => c.id === cid ? { ...c, content: editText } : c));
    setEditingId(null); setEditText('');
  };

  /* ─── 글 수정/삭제 ─── */
  const startEdit = () => navigate('/booklogwrite', { state: { log } });
  const confirmPostDelete = () => {
    const idx = dummyBooklogs.findIndex(b => b.id === id);
    if (idx !== -1) dummyBooklogs.splice(idx, 1);
    navigate('/bookloglist');
  };

  return (
    <div className="booklog-detail">
      {/* 제목 + 좋아요 */}
      <h1 className="booklog-detail__title">{log.title}</h1>
      <div className="post-like-row">
        <span className="heart" onClick={togglePostLike}>
          {hasLikedPost ? '❤️' : '🤍'} {postLikes}
        </span>
      </div>

      {/* 메타 */}
      <div className="booklog-detail__meta">
        <span>{log.nickname}</span>&nbsp;<span>{log.date}</span>
      </div>

      {/* 이미지 & 책 정보 */}
      <figure className="booklog-detail__figure">
        <img src={log.coverUrl} alt="책 표지" className="booklog-detail__image" />
        <figcaption className="booklog-detail__caption">
          <strong>{log.book}</strong><span className="separator"> | </span><strong>{log.author}</strong>
        </figcaption>
      </figure>

      {/* 본문 */}
      <p className="booklog-detail__content">{log.content}</p>

      {/* 글 수정/삭제 */}
      {log.nickname === currentUser && (
        <div className="post__actions">
          <button className="log-edit-button" onClick={startEdit}>수정</button>
          <button className="log-del-button"  onClick={() => setShowDeletePostModal(true)}>삭제</button>
        </div>
      )}

      {/* 댓글 */}
      <section className="comments">
        <h3>댓글 {comments.length}</h3>
        <ul className="comments__list">
          {comments.map(c => (
            <li key={c.id}>
              <strong>{c.nickname}</strong>
              {/* 좋아요 버튼 */}
              <span className="comment-like" onClick={() => toggleCommentLike(c.id)}>
                {c.liked ? '❤️' : '🤍'} {c.likes}
              </span>

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
      </section>

      {/* 댓글 삭제 모달 */}
      {showModal && (
        <ConfirmModal
          message="정말 이 댓글을 삭제하시겠어요?"
          onConfirm={handleConfirmDelete}
          onCancel={() => setShowModal(false)}
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
    </div>
  );
}

export default BooklogDetail;