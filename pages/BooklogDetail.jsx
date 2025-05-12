import { useParams, useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import api from '../axiosConfig';
import ConfirmModal from '../components/ConfirmModal';
import '../styles/BooklogDetail.css';

function BooklogDetail() {
  const { id } = useParams();
  const navigate = useNavigate();

  /* 서버에서 받아온 글 데이터 */
  const [log, setLog]       = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState('');

  /* ─── 좋아요 (테스트 단계 : 비활성화) ─── */
  // const [postLikes, setPostLikes] = useState(0);
  // const [hasLikedPost, setHasLikedPost] = useState(false);

  /* 댓글 (임시 로컬 상태) */
  const [comments, setComments] = useState([]);
  const [input, setInput]       = useState('');
  const [editingId, setEditingId] = useState(null);
  const [editText, setEditText]   = useState('');

  /* 삭제 모달 */
  const [showModal, setShowModal]               = useState(false);
  const [deleteTargetId, setDeleteTargetId]     = useState(null);
  const [showDeletePostModal, setShowDeletePostModal] = useState(false);

  const currentUser = '나';

  /* ─── 1. 서버에서 글 데이터 로드 ─── */
  useEffect(() => {
    (async () => {
      try {
        const { data } = await api.get(`/api/posts/${id}`); // [{id, title, content, date, bookTitle, bookAuthor, coverUrl, nickname}]
        setLog(data);
        // setPostLikes(data.likes || 0);
        setComments(data.comments || []);
      } catch (err) {
        console.error('Failed to load booklogs:', err);
        setError('북로그를 불러오지 못했습니다.');
      } finally {
        setLoading(false);
      }
    })();
  }, [id]);

  /* ─── 2. 좋아요 토글 (주석 처리) ─── */
  /*
  const togglePostLike = async () => {
    setPostLikes(prev => hasLikedPost ? prev - 1 : prev + 1);
    setHasLikedPost(!hasLikedPost);
    try {
      await api.post(`/api/posts/${id}/like`);
    } catch (e) {
      console.error('Failed to toggle like', e);
    }
  };
  */

  /* ─── 3. 댓글 좋아요/CRUD (로컬 only) ─── */
  const toggleCommentLike = (cid) => {
    setComments(comments.map(c => c.id === cid ? { ...c, likes: c.liked ? c.likes - 1 : c.likes + 1, liked: !c.liked } : c));
  };

  const handleAddComment = () => {
    if (!input.trim()) return;
    setComments([...comments, { id: Date.now(), nickname: currentUser, content: input.trim(), likes: 0, liked: false }]);
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

  /* ─── 4. 글 수정/삭제 ─── */
  const startEdit = () => navigate('/booklogwrite', { state: { log } });
  const confirmPostDelete = async () => {
    try {
      await api.delete(`/api/posts/${id}`);
      navigate('/bookloglist');
    } catch (e) {
      console.error('Failed to delete post', e);
      alert('삭제에 실패했습니다.');
    }
  };

  /* ─── 5. 화면 렌더 ─── */
  if (loading) return <p style={{ textAlign: 'center', marginTop: 40 }}>로딩 중…</p>;
  if (error)   return <p style={{ textAlign: 'center', marginTop: 40, color: 'red' }}>{error}</p>;
  if (!log)    return <p>존재하지 않는 북로그입니다 😢</p>;

  return (
    <div className="booklog-detail">
      {/* 제목 */}
      <h1 className="booklog-detail__title">{log.title}</h1>

      {/* 좋아요 영역 (주석 처리) */}
      {/**
      <div className="post-like-row">
        <span className="heart" onClick={togglePostLike}>
          {hasLikedPost ? '❤️' : '🤍'} {postLikes}
        </span>
      </div>
      **/}

      {/* 메타 */}
      <div className="booklog-detail__meta">
        <span>{log.nickname}</span>&nbsp;<span>{log.date}</span>
      </div>

      {/* 이미지 & 책 정보 */}
      <figure className="booklog-detail__figure">
        <img src={log.coverUrl} alt="책 표지" className="booklog-detail__image" />
        <figcaption className="booklog-detail__caption">
          <strong>{log.bookTitle}</strong><span className="separator"> | </span><strong>{log.bookAuthor}</strong>
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
              <span className="comment-like" onClick={() => toggleCommentLike(c.id)}>
                {c.liked ? '❤️' : '🤍'} {c.likes}
              </span>

              {editingId === c.id ? (
                <>
                  <textarea className="comment__edit-textarea" value={editText} onChange={(e) => setEditText(e.target.value)} rows={3} />
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

        {/* 댓글 작성 */}
        <div className="comments__form">
          <textarea className="comments__textarea" value={input} onChange={(e) => setInput(e.target.value)} placeholder="댓글을 입력하세요" rows={3} />
          <button onClick={handleAddComment}>등록</button>
        </div>
      </section>

      {/* 댓글 삭제 모달 */}
      {showModal && (
        <ConfirmModal message="정말 이 댓글을 삭제하시겠어요?" onConfirm={handleConfirmDelete} onCancel={() => setShowModal(false)} />
      )}

      {/* 글 삭제 모달 */}
      {showDeletePostModal && (
        <ConfirmModal message="정말 이 글을 삭제하시겠어요?" onConfirm={confirmPostDelete} onCancel={() => setShowDeletePostModal(false)} />
      )}
    </div>
  );
}

export default BooklogDetail;