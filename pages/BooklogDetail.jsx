import { useParams, useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import api from '../axiosConfig';
import ConfirmModal from '../components/ConfirmModal';
import '../styles/BooklogDetail.css';

function BooklogDetail() {
  const { id } = useParams();          // 게시글 ID
  const navigate = useNavigate();

  /* ───────── state ───────── */
  const [log,  setLog]     = useState(null);
  const [loading, setLoading] = useState(true);
  const [error,   setError]   = useState('');

  /* 좋아요(글) */
  const [postLikes, setPostLikes] = useState(0);
  const [hasLiked,  setHasLiked]  = useState(false);
  const [isMyPost,  setIsMyPost]  = useState(false);

  /* 댓글 */
  const [comments,  setComments]  = useState([]);
  const [input,     setInput]     = useState('');
  const [editingId, setEditingId] = useState(null);
  const [editText,  setEditText]  = useState('');

  /* 모달 */
  const [showModal,          setShowModal]          = useState(false);
  const [deleteTargetId,     setDeleteTargetId]     = useState(null);
  const [showDeletePostModal,setShowDeletePostModal]= useState(false);

  /* 임시 로그인 유저 */
  const currentUser = '테스트유저';

  /* ─── 1. 글 + 좋아요 + 댓글 로드 ─── */
  useEffect(() => {
    (async () => {
      try {
        /* ① 게시글 */
        const { data: post } = await api.get(`/api/posts/${id}`);
        setLog(post);

        /* ② 좋아요 상태 */
        try {
          const { data: liked } = await api.get(`/api/posts/${id}/heart`);
          setHasLiked(Boolean(liked));
        } catch (e) {
          console.warn('heart status load fail', e);
        }

        const mine = post.nickname === currentUser;
        setIsMyPost(mine);

        /* ③ 내 글이면 좋아요 개수 */
        if (mine) {
          try {
            const { data: count } = await api.get(`/api/posts/${id}/heart-count`);
            setPostLikes(count ?? 0);
          } catch (e) {
            console.error('Like count load error:', e);
          }
        }

        /* ④ 댓글 리스트 + 공감 정보 */
        try {
          const { data: list } =
            await api.get(`/api/comments/post/${id}?isOwner=${mine}`);

          const augmented = await Promise.all(
            list.map(async (c) => {
              try {
                /* 댓글 공감 여부·개수 동시 조회 */
                const [likedRes, countRes] = await Promise.all([
                  api.get(`/api/comments/${c.id}/hearts/exists`),
                  api.get(`/api/comments/${c.id}/hearts/count`),
                ]);
                return {
                  ...c,
                  liked: Boolean(likedRes.data),
                  likes: countRes.data ?? 0,
                };
              } catch (e) {
                console.error('comment heart info load error:', e); // e 사용
                /* 공감 정보 못 불러와도 기본값으로 진행 */
                return { ...c, liked: false, likes: 0 };
              }
            })
          );

          setComments(augmented);
        } catch (e) {
          console.error('comment list load error:', e); // e 사용
        }

      } catch (err) {
        console.error('Post load error:', err);
        setError('북로그를 불러오지 못했습니다.');
      } finally {
        setLoading(false);
      }
    })();
  }, [id]);

  /* ─── 2. 글 좋아요 토글 ─── */
  const togglePostLike = async () => {
  const likedNow = !hasLiked;
  if (isMyPost) setPostLikes(prev => likedNow ? prev + 1 : prev - 1);
  setHasLiked(likedNow);

  try {
    await api.post(`/api/posts/${id}/heart`);
    alert(likedNow ? '좋아요를 눌렀습니다!' : '좋아요를 취소했습니다.');
  } catch (e) {
    console.error('togglePostLike error:', e);   // ← e 사용!

    /* 롤백 */
    if (isMyPost) setPostLikes(prev => likedNow ? prev - 1 : prev + 1);
    setHasLiked(!likedNow);
    alert('좋아요 처리 실패, 다시 시도해 주세요.');
    }
  };

  /* ─── 3. 댓글 좋아요 토글 (백엔드 연동) ─── */
  const toggleCommentLike = async cid => {
    // optimistic update
    setComments(cs => cs.map(c =>
      c.id === cid ? { ...c, likes: c.liked ? c.likes - 1 : c.likes + 1, liked: !c.liked } : c
    ));

    try {
      await api.post(`/api/comments/${cid}/hearts`);
    } catch (e) {
      console.error('comment heart error', e);      // ← e 사용
      // rollback
      setComments(cs => cs.map(c =>
        c.id === cid ? { ...c, likes: c.liked ? c.likes - 1 : c.likes + 1, liked: !c.liked } : c
      ));
      alert('댓글 공감 처리에 실패했습니다.');
    }
  };

  /* ─── 4. 댓글 CRUD (백엔드 연동) ─── */
  const handleAddComment = async () => {
    if (!input.trim()) return;
    try {
      const { data: newC } = await api.post('/api/comments', {
        postId: id,
        content: input.trim(),
      });
      setComments([...comments, { ...newC, likes: 0, liked: false }]);
      setInput('');
    } catch (e) {
      alert('댓글 등록 실패');
      console.error(e);
    }
  };

  const requestDelete = cid => { setDeleteTargetId(cid); setShowModal(true); };

  const handleConfirmDelete = async () => {
    try {
      await api.delete(`/api/comments/${deleteTargetId}`);
      setComments(comments.filter(c => c.id !== deleteTargetId));
    } catch (e) {
      alert('댓글 삭제 실패');
      console.error(e);
    } finally {
      setShowModal(false);
      setDeleteTargetId(null);
    }
  };

  const handleEdit = (cid, content) => { setEditingId(cid); setEditText(content); };

  const handleEditSubmit = async cid => {
    if (!editText.trim()) return;
    try {
      await api.put(`/api/comments/${cid}`, { content: editText.trim() });
      setComments(comments.map(c =>
        c.id === cid ? { ...c, content: editText.trim() } : c
      ));
      setEditingId(null);
      setEditText('');
    } catch (e) {
      alert('댓글 수정 실패');
      console.error(e);
    }
  };

  /* ─── 5. 글 수정/삭제 ─── */
  const startEdit = () => navigate('/booklogwrite', { state: { log } });
  const confirmPostDelete = async () => {
    try {
      await api.delete(`/api/posts/${id}`);
      navigate('/bookloglist');
    } catch (e) {
      alert('삭제에 실패했습니다.');
      console.error(e);
    }
  };

  /* ─── 6. 렌더 ─── */
  if (loading) return <p style={{ textAlign: 'center', marginTop: 40 }}>로딩 중…</p>;
  if (error)   return <p style={{ textAlign: 'center', marginTop: 40, color: 'red' }}>{error}</p>;
  if (!log)    return <p>존재하지 않는 북로그입니다 😢</p>;

  return (
    <div className="booklog-detail">
      <h1 className="booklog-detail__title">{log.title}</h1>

      <div className="post-like-row">
        <span className="heart" onClick={togglePostLike}>
          {hasLiked ? '❤️' : '🤍'}
          {isMyPost && ` ${postLikes}`}
        </span>
      </div>

      <div className="booklog-detail__meta">
        <span>{log.nickname}</span>&nbsp;<span>{log.date}</span>
      </div>

      <figure className="booklog-detail__figure">
        <img src={log.coverUrl} alt="책 표지" className="booklog-detail__image" />
        <figcaption className="booklog-detail__caption">
          <strong>{log.bookTitle}</strong><span className="separator"> | </span><strong>{log.bookAuthor}</strong>
        </figcaption>
      </figure>

      <p className="booklog-detail__content">{log.content}</p>

      {log.nickname === currentUser && (
        <div className="post__actions">
          <button className="log-edit-button" onClick={startEdit}>수정</button>
          <button className="log-del-button" onClick={() => setShowDeletePostModal(true)}>삭제</button>
        </div>
      )}

      {/* 댓글 */}
      <section className="comments">
        <h3>댓글 {comments.length}</h3>
        <ul className="comments__list">
          {comments.map(c => {
            const isMine = c.nickname === currentUser;
            return (
              <li key={c.id}>
                <strong>{c.nickname}</strong>

                {/* 좋아요 아이콘 + (내 댓글이면) 숫자 */}
                <span className="comment-like" onClick={() => toggleCommentLike(c.id)}>
                  {c.liked ? '❤️' : '🤍'} 
                  {isMine && `${c.likes}`}
                </span>

                {editingId === c.id ? (
                  <>
                    <textarea
                      className="comment__edit-textarea"
                      value={editText}
                      onChange={e => setEditText(e.target.value)}
                      rows={3}
                    />
                    <button onClick={() => handleEditSubmit(c.id)}>완료</button>
                  </>
                ) : (
                  <p>{c.content}</p>
                )}

                {isMine && editingId !== c.id && (
                  <div className="comment__actions">
                    <button onClick={() => handleEdit(c.id, c.content)}>수정</button>
                    <button onClick={() => requestDelete(c.id)}>삭제</button>
                  </div>
                )}
              </li>
            );
          })}
        </ul>

        {/* 댓글 작성 */}
        <div className="comments__form">
          <textarea
            className="comments__textarea"
            value={input}
            onChange={e => setInput(e.target.value)}
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