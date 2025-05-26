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
  const [coverUrl, setCoverUrl] = useState("");
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

      // 1) 책 정보 저장
      const { data: savedBook } = await api.post("/api/clubs/saveBook", {
        title: bookTitle,
        author: bookAuthor,
        coverImageUrl: coverUrl,
      });

      // 2) FormData 조립 (모든 필드를 flat하게)
      const formData = new FormData();
      formData.append("title", clubName);
      formData.append("description", clubDescription);
      formData.append("minParticipants", "2");
      formData.append("maxParticipants", capacity.toString());
      formData.append(
        "applicationDeadline",
        new Date(deadline).toISOString().slice(0, 19)
      );
      formData.append("activityDurationDays", "30");
      formData.append("status", "OPEN");
      formData.append("bookId", savedBook.id.toString());
      formData.append(
        "clubCoverImageUrl",
        coverUrl || "https://picsum.photos/200/300"
      );
      formData.append("userId", "1"); // 테스트 유저 ID 사용

      // 3) 클럽 생성 요청 (Content-Type: multipart/form-data)
      const resp = await api.post("/api/clubs", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
          Accept: "application/json",
        },
      });

      if (resp.status === 201) {
        onCreate(resp.data);
        onClose();
      } else {
        throw new Error("클럽 생성에 실패했습니다.");
      }
    } catch (err) {
      console.error("Failed to create club:", err);
      if (err.response) {
        // 서버에서 응답이 왔지만 에러인 경우
        alert(err.response.data.message || "북클럽 생성에 실패했습니다.");
      } else if (err.request) {
        // 요청은 보냈지만 응답이 없는 경우
        alert("서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.");
      } else {
        // 요청 설정 중 에러가 발생한 경우
        alert("요청을 보내는 중 오류가 발생했습니다.");
      }
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
              min={new Date().toISOString().split("T")[0]}
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
            setCoverUrl(b.cover);
            setShowBookModal(false);
          }}
        />
      )}
    </div>
  );
}

export default BookclubCreateModal;
