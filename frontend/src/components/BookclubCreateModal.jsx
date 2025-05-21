// src/components/BookclubCreateModal.jsx
import { useState } from "react";
import api from "../axiosConfig";
import BookSearchModal from "./BookSearchModal";
import "../styles/BookclubCreateModal.css";

function BookclubCreateModal({ onClose, onCreate }) {
  /* ---------- 상태 ---------- */
  const [clubName, setClubName] = useState("");
  const [clubDescription, setClubDescription] = useState("");
  const [capacity, setCapacity] = useState(2);
  const [deadline, setDeadline] = useState("");
  const [bookTitle, setBookTitle] = useState("");
  const [bookAuthor, setBookAuthor] = useState("");
  const [coverUrl, setCoverUrl] = useState(""); // ✔ 책 표지 URL만 보관
  const [loading, setLoading] = useState(false);
  const [showBookModal, setShowBookModal] = useState(false);

  /* ---------- 생성 ---------- */
  const handleCreate = async () => {
    if (!clubName || !clubDescription || !bookTitle || !bookAuthor || !deadline)
      return alert("모든 항목을 입력하세요!");
    if (clubName.length > 50) return alert("클럽 이름 50자 제한입니다.");
    if (clubDescription.length > 150)
      return alert("클럽 소개 150자 제한입니다.");
    if (capacity < 2 || capacity > 5) return alert("모집 인원은 2~5명!");

    try {
      setLoading(true);

      /* 1) 책 정보 저장 */
      const { data: savedBook } = await api.post("/api/clubs/saveBook", {
        title: bookTitle,
        author: bookAuthor,
        cover: coverUrl,
      });

      /* 2) FormData – JSON + 기타 필드 */
      const formData = new FormData();
      const clubDto = {
        title: clubName,
        description: clubDescription,
        minParticipants: 1,
        maxParticipants: capacity,
        applicationDeadline: new Date(deadline).toISOString(),
        activityDurationDays: 30,
        status: "OPEN",
        bookId: savedBook.id,
      };
      formData.append(
        "club",
        new Blob([JSON.stringify(clubDto)], { type: "application/json" })
      );

      /* userId, coverUrl만 전송 */
      formData.append("userId", localStorage.getItem("userId"));
      formData.append("coverUrl", coverUrl);

      /* 3) 클럽 생성 */
      const resp = await api.post("/api/clubs", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      onCreate(resp.data);
      onClose();
    } catch (err) {
      console.error("Failed to create club:", err);
      alert("북클럽 생성에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  /* ---------- UI ---------- */
  return (
    <div className="create-backdrop">
      <div className="create-modal">
        <h3>북클럽 만들기</h3>

        <label>
          북클럽 이름&nbsp;({clubName.length}/50)
          <input
            maxLength={50}
            value={clubName}
            onChange={(e) => setClubName(e.target.value)}
          />
        </label>

        <label>
          클럽 소개&nbsp;({clubDescription.length}/150)
          <input
            maxLength={150}
            value={clubDescription}
            onChange={(e) => setClubDescription(e.target.value)}
          />
        </label>

        <div className="row-half">
          <label className="half">
            모집 인원&nbsp;(본인 포함)
            <select
              value={capacity}
              onChange={(e) => setCapacity(+e.target.value)}
            >
              {[2, 3, 4, 5].map((n) => (
                <option key={n}>{n}</option>
              ))}
            </select>
          </label>
          <label className="half">
            마감 날짜
            <input
              type="date"
              value={deadline}
              onChange={(e) => setDeadline(e.target.value)}
            />
          </label>
        </div>

        <div className="row-half">
          <label
            className="half search-field"
            onClick={() => setShowBookModal(true)}
          >
            책 제목
            <input readOnly value={bookTitle} />
          </label>
          <label
            className="half search-field"
            onClick={() => setShowBookModal(true)}
          >
            작가 이름
            <input readOnly value={bookAuthor} />
          </label>
        </div>

        {/* 👉 이미지 업로드 필드 삭제됨 */}

        <div className="create-btns">
          <button className="cancel" onClick={onClose} disabled={loading}>
            닫기
          </button>
          <button className="confirm" onClick={handleCreate} disabled={loading}>
            {loading ? "생성 중..." : "만들기"}
          </button>
        </div>
      </div>

      {/* 책 검색 모달 */}
      {showBookModal && (
        <BookSearchModal
          onClose={() => setShowBookModal(false)}
          onSelect={(b) => {
            setBookTitle(b.title);
            setBookAuthor(b.author);
            setCoverUrl(b.cover); // 책 표지 URL만 저장
            setShowBookModal(false);
          }}
        />
      )}
    </div>
  );
}

export default BookclubCreateModal;
