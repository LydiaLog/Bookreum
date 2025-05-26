import { useParams, useNavigate, useLocation } from "react-router-dom";
import { useState, useEffect, useRef } from "react";
import api from "../axiosConfig";
import BookclubJoinModal from "../components/BookclubJoinModal";
import BookclubEditModal from "../components/BookclubEditModal";
import "../styles/BookclubDetail.css";
import profileImg from "../assets/profile.jpg";

function BookclubDetail({ userId }) {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const isChatView = location.pathname.includes("/chat");
  const [club, setClub] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showJoinModal, setShowJoinModal] = useState(false);
  const [showDetailModal, setShowDetailModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [isParticipant, setIsParticipant] = useState(true); // 임시로 true 설정
  const [participants, setParticipants] = useState([]);
  const [currentParticipantCount, setCurrentParticipantCount] = useState(0);

  const TEST_USER_ID = 1; // 테스트용 유저 ID

  /* 댓글 상태 */
  const [comments, setComments] = useState([]);
  const [input, setInput] = useState("");
  const [editingCommentId, setEditingCommentId] = useState(null);
  const [editingContent, setEditingContent] = useState("");
  const [likeCounts, setLikeCounts] = useState({}); // 댓글별 좋아요 개수
  const [likedComments, setLikedComments] = useState(new Set()); // 내가 좋아요한 댓글들
  const currentUser = localStorage.getItem("nickname") || "나";

  /* 자동 스크롤용 ref */
  const bottomRef = useRef(null);
  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [comments]);

  // 참여자 목록 가져오기
  const fetchParticipants = async () => {
    try {
      const response = await api.get(`/api/clubs/${id}/participants`);
      setParticipants(response.data);
      setCurrentParticipantCount(response.data.length);
      return true;
    } catch (error) {
      console.error("참여자 목록 가져오기 실패:", error);
      setParticipants([]);
      setCurrentParticipantCount(0);
      return false;
    }
  };

  // 북클럽 정보 가져오기
  useEffect(() => {
    const fetchClubDetails = async () => {
      try {
        if (!id || isNaN(parseInt(id, 10))) {
          setError("잘못된 북클럽 ID입니다.");
          setLoading(false);
          return;
        }

        const response = await api.get(`/api/clubs/${id}`);
        setClub(response.data);

        // 참여자 목록 가져오기
        await fetchParticipants();
        // 채팅 내역 불러오기
        fetchComments();
      } catch (err) {
        console.error("Failed to fetch club details:", err);
        if (err.response?.status === 404) {
          setError("존재하지 않는 북클럽입니다.");
        } else if (err.response?.status === 400) {
          setError("잘못된 요청입니다.");
        } else {
          setError("북클럽 정보를 불러오는데 실패했습니다.");
        }
      } finally {
        setLoading(false);
      }
    };

    if (id) {
      fetchClubDetails();
    }
  }, [id]);

  // 댓글 가져오기
  const fetchComments = async () => {
    try {
      const response = await api.get(`/api/clubs/${id}/comments`, {
        params: { userId },
      });
      setComments(response.data);

      // 각 댓글의 좋아요 정보 가져오기
      const likeCountsData = {};
      const likedCommentsSet = new Set();

      for (const comment of response.data) {
        try {
          const likeResponse = await api.get(
            `/api/clubs/${id}/comments/${comment.id}/likes`,
            {
              params: { userId },
            }
          );
          likeCountsData[comment.id] = likeResponse.data.likeCount;
          if (likeResponse.data.likedByUser) {
            likedCommentsSet.add(comment.id);
          }
        } catch (err) {
          console.error(
            `Failed to fetch like info for comment ${comment.id}:`,
            err
          );
          likeCountsData[comment.id] = 0;
        }
      }

      setLikeCounts(likeCountsData);
      setLikedComments(likedCommentsSet);
    } catch (err) {
      console.error("Failed to fetch comments:", err);
      setComments([]);
    }
  };

  const handleSend = async () => {
    if (input.trim() === "") return;

    console.log("=== 채팅 전송 시작 ===");
    console.log("clubId:", id);
    console.log("userId:", userId);
    console.log("content:", input.trim());

    try {
      const response = await api.post(`/api/clubs/${id}/comments`, {
        content: input.trim(),
        userId: userId,
      });

      console.log("채팅 전송 성공:", response.data);
      setComments((prev) => [...prev, response.data]);
      setInput("");
    } catch (err) {
      console.error("Failed to send comment:", err);
      console.error("Error response:", err.response?.data);
      console.error("Error status:", err.response?.status);
      alert("댓글 작성에 실패했습니다.");
    }
  };

  // 댓글 수정 시작
  const handleEditStart = (comment) => {
    setEditingCommentId(comment.id);
    setEditingContent(comment.content);
  };

  // 댓글 수정 완료
  const handleEditComplete = async () => {
    if (editingContent.trim() === "") {
      alert("댓글 내용을 입력해주세요.");
      return;
    }

    try {
      const response = await api.put(
        `/api/clubs/${id}/comments/${editingCommentId}`,
        {
          content: editingContent.trim(),
          userId: userId,
        }
      );

      setComments((prev) =>
        prev.map((comment) =>
          comment.id === editingCommentId
            ? { ...comment, content: editingContent.trim() }
            : comment
        )
      );

      setEditingCommentId(null);
      setEditingContent("");
    } catch (err) {
      console.error("Failed to edit comment:", err);
      alert("댓글 수정에 실패했습니다.");
    }
  };

  // 댓글 수정 취소
  const handleEditCancel = () => {
    setEditingCommentId(null);
    setEditingContent("");
  };

  // 댓글 삭제
  const handleDelete = async (commentId) => {
    if (!confirm("정말로 이 댓글을 삭제하시겠습니까?")) {
      return;
    }

    try {
      await api.delete(`/api/clubs/${id}/comments/${commentId}`, {
        params: { userId: userId },
      });

      setComments((prev) => prev.filter((comment) => comment.id !== commentId));
    } catch (err) {
      console.error("Failed to delete comment:", err);
      alert("댓글 삭제에 실패했습니다.");
    }
  };

  // 좋아요 토글
  const handleLikeToggle = async (commentId) => {
    const isLiked = likedComments.has(commentId);

    try {
      if (isLiked) {
        // 좋아요 취소
        await api.delete(`/api/clubs/${id}/comments/${commentId}/likes`, {
          params: { userId },
        });
        setLikedComments((prev) => {
          const newSet = new Set(prev);
          newSet.delete(commentId);
          return newSet;
        });
        setLikeCounts((prev) => ({
          ...prev,
          [commentId]: Math.max(0, (prev[commentId] || 0) - 1),
        }));
      } else {
        // 좋아요 추가
        await api.post(`/api/clubs/${id}/comments/${commentId}/likes`, null, {
          params: { userId },
        });
        setLikedComments((prev) => new Set([...prev, commentId]));
        setLikeCounts((prev) => ({
          ...prev,
          [commentId]: (prev[commentId] || 0) + 1,
        }));
      }
    } catch (err) {
      console.error("Failed to toggle like:", err);
      alert("좋아요 처리에 실패했습니다.");
    }
  };

  // 북클럽 수정
  const handleClubEdit = () => {
    setShowDetailModal(false);
    setShowEditModal(true);
  };

  // 북클럽 수정 완료
  const handleClubEditComplete = async (editedData) => {
    try {
      const formData = new FormData();
      formData.append("title", editedData.title);
      formData.append("description", editedData.description);
      formData.append("minParticipants", editedData.minParticipants.toString());
      formData.append("maxParticipants", editedData.maxParticipants.toString());
      formData.append("applicationDeadline", editedData.applicationDeadline);
      formData.append(
        "activityDurationDays",
        editedData.activityDurationDays.toString()
      );
      formData.append("status", editedData.status);
      formData.append("bookId", club.bookId.toString());
      formData.append("userId", TEST_USER_ID.toString());

      await api.put(`/api/clubs/${id}`, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });

      alert("북클럽이 수정되었습니다.");
      setShowEditModal(false);

      // 북클럽 정보 새로고침
      const response = await api.get(`/api/clubs/${id}`);
      setClub(response.data);
    } catch (err) {
      console.error(err);
      alert(err.response?.data?.message || "수정에 실패했습니다.");
    }
  };

  // 북클럽 삭제
  const handleClubDelete = async () => {
    try {
      await api.delete(`/api/clubs/${id}`);
      alert("북클럽이 삭제되었습니다.");
      navigate("/bookclub");
    } catch (err) {
      console.error(err);
      alert(err.response?.data?.message || "삭제에 실패했습니다.");
    }
  };

  const handleJoin = async () => {
    try {
      console.log("북클럽 참여 요청 시작");
      const response = await api.post(`/api/clubs/${id}/join`);
      console.log("참여 성공:", response.data);
      setShowJoinModal(false);

      // 참여자 목록 새로고침
      await fetchParticipants();
      // 채팅 내역 불러오기
      fetchComments();
    } catch (error) {
      console.error("참여 실패:", error);
      alert("북클럽 참여에 실패했습니다.");
    }
  };

  if (loading) return <div className="loading">로딩 중...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!club)
    return <div className="error">북클럽 정보를 찾을 수 없습니다.</div>;

  return (
    <div className="bookclub-detail">
      {isChatView ? (
        <div className="chat-room-container">
          {/* 채팅 헤더 */}
          <div
            className="chat-header"
            style={{
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
              padding: "12px 16px",
              borderBottom: "1px solid #eee",
              background: "#fff",
              borderRadius: "8px 8px 0 0",
            }}
          >
            <div style={{ display: "flex", alignItems: "center" }}>
              <button
                onClick={() => navigate("/bookclub")}
                style={{
                  background: "none",
                  border: "none",
                  fontSize: "18px",
                  cursor: "pointer",
                  padding: "4px 8px",
                  marginRight: "12px",
                  borderRadius: "4px",
                  display: "flex",
                  alignItems: "center",
                  color: "#666",
                }}
                title="뒤로가기"
              >
                ←
              </button>
              <div>
                <h3
                  style={{
                    margin: 0,
                    fontSize: "16px",
                    fontWeight: "600",
                    color: "#333",
                  }}
                >
                  {club.title}
                </h3>
              </div>
            </div>

            {/* 상세모달 버튼 */}
            <button
              onClick={() => setShowDetailModal(true)}
              style={{
                background: "none",
                border: "1px solid #ddd",
                borderRadius: "6px",
                padding: "6px 12px",
                fontSize: "14px",
                cursor: "pointer",
                color: "#666",
                display: "flex",
                alignItems: "center",
                gap: "4px",
              }}
              title="북클럽 상세정보"
            >
              📋 상세정보
            </button>
          </div>

          <div
            className="chat-messages"
            style={{
              height: "400px",
              overflowY: "auto",
              background: "#f7f7fa",
              padding: 16,
              borderRadius: "0 0 8px 8px",
              border: "1px solid #eee",
              borderTop: "none",
            }}
          >
            {comments.length === 0 && (
              <div
                style={{ textAlign: "center", color: "#aaa", marginTop: 40 }}
              >
                아직 메시지가 없습니다.
              </div>
            )}
            {comments.map((comment, idx) => {
              const isMine = String(comment.authorId) === String(userId);
              const isEditing = editingCommentId === comment.id;

              return (
                <div
                  key={comment.id || idx}
                  style={{
                    display: "flex",
                    flexDirection: isMine ? "row-reverse" : "row",
                    alignItems: "flex-end",
                    marginBottom: 12,
                    position: "relative",
                  }}
                  className="comment-container"
                >
                  <img
                    src={
                      comment.authorProfileImage || "/uploads/temp/profile.jpg"
                    }
                    alt="프로필"
                    style={{
                      width: 32,
                      height: 32,
                      borderRadius: "50%",
                      margin: isMine ? "0 0 0 8px" : "0 8px 0 0",
                      border: "1px solid #ddd",
                    }}
                  />
                  <div style={{ maxWidth: "70%", position: "relative" }}>
                    <div
                      style={{
                        background: isMine ? "#d1e7ff" : "#fff",
                        color: "#222",
                        borderRadius: 16,
                        padding: "8px 14px",
                        marginBottom: 2,
                        boxShadow: "0 1px 2px rgba(0,0,0,0.03)",
                        wordBreak: "break-all",
                        textAlign: "left",
                        position: "relative",
                      }}
                      className="message-bubble"
                    >
                      <span
                        style={{ fontWeight: 500, fontSize: 13, color: "#888" }}
                      >
                        {comment.authorNickname}
                      </span>

                      {isEditing ? (
                        <div style={{ marginTop: 4 }}>
                          <input
                            type="text"
                            value={editingContent}
                            onChange={(e) => setEditingContent(e.target.value)}
                            onKeyPress={(e) => {
                              if (e.key === "Enter") {
                                handleEditComplete();
                              } else if (e.key === "Escape") {
                                handleEditCancel();
                              }
                            }}
                            style={{
                              width: "100%",
                              border: "1px solid #ccc",
                              borderRadius: 4,
                              padding: "4px 8px",
                              fontSize: 15,
                              marginBottom: 4,
                            }}
                            autoFocus
                          />
                          <div style={{ display: "flex", gap: 4 }}>
                            <button
                              onClick={handleEditComplete}
                              style={{
                                background: "#1976d2",
                                color: "white",
                                border: "none",
                                borderRadius: 4,
                                padding: "2px 8px",
                                fontSize: 12,
                                cursor: "pointer",
                              }}
                            >
                              완료
                            </button>
                            <button
                              onClick={handleEditCancel}
                              style={{
                                background: "#666",
                                color: "white",
                                border: "none",
                                borderRadius: 4,
                                padding: "2px 8px",
                                fontSize: 12,
                                cursor: "pointer",
                              }}
                            >
                              취소
                            </button>
                          </div>
                        </div>
                      ) : (
                        <div style={{ fontSize: 15 }}>{comment.content}</div>
                      )}

                      {/* 내가 쓴 댓글에만 수정/삭제 버튼 표시 */}
                      {isMine && !isEditing && (
                        <div
                          className="comment-actions"
                          style={{
                            position: "absolute",
                            top: -8,
                            right: isMine ? "auto" : -60,
                            left: isMine ? -60 : "auto",
                            display: "none",
                            gap: 4,
                            background: "white",
                            borderRadius: 4,
                            padding: "2px 4px",
                            boxShadow: "0 2px 8px rgba(0,0,0,0.15)",
                            zIndex: 10,
                          }}
                        >
                          <button
                            onClick={() => handleEditStart(comment)}
                            style={{
                              background: "none",
                              border: "none",
                              color: "#666",
                              cursor: "pointer",
                              fontSize: 12,
                              padding: "2px 4px",
                            }}
                            title="수정"
                          >
                            ✏️
                          </button>
                          <button
                            onClick={() => handleDelete(comment.id)}
                            style={{
                              background: "none",
                              border: "none",
                              color: "#666",
                              cursor: "pointer",
                              fontSize: 12,
                              padding: "2px 4px",
                            }}
                            title="삭제"
                          >
                            🗑️
                          </button>
                        </div>
                      )}
                    </div>
                    <div
                      style={{
                        fontSize: 11,
                        color: "#aaa",
                        textAlign: isMine ? "right" : "left",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: isMine ? "flex-end" : "flex-start",
                        gap: 8,
                        marginTop: 4,
                      }}
                    >
                      <span>
                        {comment.createdAt
                          ? new Date(comment.createdAt).toLocaleTimeString()
                          : ""}
                      </span>

                      {/* 좋아요 표시 */}
                      {isMine ? (
                        // 내 댓글: 좋아요 개수만 표시 (1개 이상일 때만)
                        (likeCounts[comment.id] || 0) > 0 && (
                          <div
                            style={{
                              display: "flex",
                              alignItems: "center",
                              gap: 4,
                              padding: "2px 6px",
                              borderRadius: 12,
                              background: "#f5f5f5",
                              border: "1px solid #ddd",
                            }}
                          >
                            <span style={{ fontSize: 12, color: "#666" }}>
                              ❤️
                            </span>
                            <span style={{ fontSize: 10, color: "#666" }}>
                              {likeCounts[comment.id]}
                            </span>
                          </div>
                        )
                      ) : (
                        // 다른 사람 댓글: 좋아요 버튼
                        <div
                          style={{
                            display: "flex",
                            alignItems: "center",
                            gap: 4,
                            cursor: "pointer",
                            padding: "2px 6px",
                            borderRadius: 12,
                            background: likedComments.has(comment.id)
                              ? "#ffebee"
                              : "#f5f5f5",
                            border: `1px solid ${
                              likedComments.has(comment.id) ? "#e91e63" : "#ddd"
                            }`,
                            transition: "all 0.2s ease",
                          }}
                          onClick={() => handleLikeToggle(comment.id)}
                          onMouseEnter={(e) => {
                            e.target.style.transform = "scale(1.05)";
                          }}
                          onMouseLeave={(e) => {
                            e.target.style.transform = "scale(1)";
                          }}
                        >
                          <span
                            style={{
                              fontSize: 12,
                              color: likedComments.has(comment.id)
                                ? "#e91e63"
                                : "#666",
                            }}
                          >
                            {likedComments.has(comment.id) ? "❤️" : "🤍"}
                          </span>
                          <span
                            style={{
                              fontSize: 10,
                              color: likedComments.has(comment.id)
                                ? "#e91e63"
                                : "#666",
                              fontWeight: likedComments.has(comment.id)
                                ? "600"
                                : "400",
                            }}
                          >
                            {likeCounts[comment.id] || 0}
                          </span>
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              );
            })}
            <div ref={bottomRef} />
          </div>
          <form
            className="chat-input-bar"
            style={{ display: "flex", marginTop: 12, gap: 8 }}
            onSubmit={(e) => {
              e.preventDefault();
              handleSend();
            }}
          >
            <input
              type="text"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="메시지를 입력하세요..."
              style={{
                flex: 1,
                borderRadius: 16,
                border: "1px solid #ccc",
                padding: "10px 16px",
                fontSize: 15,
              }}
            />
            <button
              type="submit"
              style={{
                background: "#1976d2",
                color: "#fff",
                border: "none",
                borderRadius: 16,
                padding: "0 20px",
                fontWeight: 600,
                fontSize: 15,
                cursor: "pointer",
              }}
            >
              전송
            </button>
          </form>
        </div>
      ) : (
        <>
          <div className="bookclub-detail__header">
            <h1>{club.title}</h1>
            <div className="bookclub-detail__status">
              <span
                className={`status-badge ${
                  club.status === "CLOSED" ? "closed" : "open"
                }`}
              >
                {club.status === "CLOSED" ? "모집 마감" : "모집 중"}
              </span>
            </div>
          </div>

          <div className="bookclub-detail__book-info">
            <div className="book-cover">
              <img src={club.coverImageUrl} alt={club.bookTitle} />
            </div>
            <div className="book-details">
              <h2>{club.bookTitle}</h2>
              <p className="author">{club.bookAuthor}</p>
            </div>
          </div>

          <div className="bookclub-detail__info">
            <div className="info-section">
              <h3>모임 정보</h3>
              <p>
                모집 인원: {currentParticipantCount} / {club.maxParticipants}명
              </p>
              <p>
                마감일:{" "}
                {new Date(club.applicationDeadline).toLocaleDateString()}
              </p>
              <p>활동 기간: {club.activityDurationDays}일</p>
            </div>

            <div className="info-section">
              <h3>모임 설명</h3>
              <p>{club.description}</p>
            </div>

            <div className="info-section">
              <h3>모임장</h3>
              <div className="creator-info">
                <img
                  src="/uploads/temp/profile.jpg"
                  alt="프로필"
                  className="creator-avatar"
                />
                <span>{club.createdByNickname}</span>
              </div>
            </div>
          </div>

          {club.status === "MATCHED" ? (
            <div className="bookclub-detail__actions">
              <button className="join-button" disabled>
                매칭 완료
              </button>
            </div>
          ) : (
            club.status !== "CLOSED" && (
              <div className="bookclub-detail__actions">
                <button
                  className="join-button"
                  onClick={() => setShowJoinModal(true)}
                >
                  참여하기
                </button>
              </div>
            )
          )}

          {showJoinModal && (
            <div className="modal-backdrop">
              <div className="modal">
                <h3>북클럽 참여하기</h3>
                <p>정말로 이 북클럽에 참여하시겠습니까?</p>
                <div className="modal-buttons">
                  <button className="join-button" onClick={handleJoin}>
                    참여하기
                  </button>
                  <button
                    className="cancel-button"
                    onClick={() => setShowJoinModal(false)}
                  >
                    취소
                  </button>
                </div>
              </div>
            </div>
          )}
        </>
      )}

      {/* 상세모달 */}
      {showDetailModal && (
        <BookclubJoinModal
          club={{
            ...club,
            nickname: club.createdByNickname,
            date: club.applicationDeadline?.split("T")[0],
            description: club.description,
            coverUrl: club.coverImageUrl,
            currentParticipants: currentParticipantCount,
            participants: participants,
          }}
          onClose={() => setShowDetailModal(false)}
          onJoin={() => {}} // 채팅방에서는 참여 기능 비활성화
          onCancel={() => {}}
          isJoinDisabled={true}
          applied={true} // 이미 참여중이므로 채팅 버튼 표시
          onChat={() => {}} // 이미 채팅방이므로 비활성화
          isOwner={club.createdById === TEST_USER_ID}
          onEdit={handleClubEdit}
          onDelete={handleClubDelete}
        />
      )}

      {/* 수정모달 */}
      {showEditModal && (
        <BookclubEditModal
          club={club}
          onClose={() => setShowEditModal(false)}
          onEdit={handleClubEditComplete}
        />
      )}
    </div>
  );
}

export default BookclubDetail;
