import { useParams, useNavigate } from "react-router-dom";
import { useState, useRef, useEffect } from "react";
import api from "../axiosConfig";
import "../styles/BookclubDetail.css";

function BookclubDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [club, setClub] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  /* 채팅(댓글) 상태 */
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const currentUser = localStorage.getItem("nickname") || "나";

  /* 자동 스크롤용 ref */
  const bottomRef = useRef(null);
  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  // 북클럽 정보 가져오기
  useEffect(() => {
    const fetchClubDetails = async () => {
      try {
        const response = await api.get(`/api/clubs/${id}`);
        setClub(response.data);
      } catch (err) {
        console.error("Failed to fetch club details:", err);
        if (err.response?.status === 401) {
          // 토큰이 만료된 경우에만 로그아웃 처리
          if (err.response.data?.message === "Token expired") {
            localStorage.removeItem("accessToken");
            localStorage.removeItem("userId");
            localStorage.removeItem("nickname");
            localStorage.removeItem("profileImageUrl");
            alert("세션이 만료되었습니다. 다시 로그인해주세요.");
            navigate("/login");
          }
        }
        setError("북클럽 정보를 불러오는데 실패했습니다.");
      } finally {
        setLoading(false);
      }
    };

    fetchClubDetails();
  }, [id, navigate]);

  const handleSend = async () => {
    if (input.trim() === "") return;

    const token = localStorage.getItem("accessToken");
    if (!token) {
      alert("로그인이 필요합니다.");
      navigate("/login");
      return;
    }

    try {
      const response = await api.post(`/api/clubs/${id}/messages`, {
        content: input.trim(),
        userId: localStorage.getItem("userId"),
      });

      setMessages((prev) => [...prev, response.data]);
      setInput("");
    } catch (err) {
      console.error("Failed to send message:", err);
      if (err.response?.status === 401) {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("userId");
        localStorage.removeItem("nickname");
        localStorage.removeItem("profileImageUrl");
        alert("세션이 만료되었습니다. 다시 로그인해주세요.");
        navigate("/login");
      } else {
        alert("메시지 전송에 실패했습니다.");
      }
    }
  };

  if (loading) return <p>로딩 중...</p>;
  if (error) return <p>{error}</p>;
  if (!club) return <p>존재하지 않는 북클럽입니다 😢</p>;

  return (
    <div className="club-detail">
      {/* ── 클럽 헤더 ───────────────────────────────── */}
      <h2 className="club-title">{club.title}</h2>
      <p className="club-sub">
        {club.bookTitle} | {club.bookAuthor}
      </p>

      {/* ── 채팅 영역 ───────────────────────────────── */}
      <div className="chat-box">
        {messages.map((m) => (
          <div
            key={m.id}
            className={
              m.nickname === currentUser
                ? "message-row my"
                : "message-row other"
            }
          >
            {m.nickname !== currentUser && (
              <span className="msg-nick">{m.nickname}</span>
            )}
            <div className="msg-bubble">{m.content}</div>
          </div>
        ))}
        <div ref={bottomRef} /> {/* 스크롤 앵커 */}
      </div>

      {/* ── 입력창 ──────────────────────────────────── */}
      <div className="chat-input">
        <textarea
          placeholder="메시지를 입력하세요"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          rows={1}
        />
        <button onClick={handleSend}>전송</button>
      </div>
    </div>
  );
}

export default BookclubDetail;
