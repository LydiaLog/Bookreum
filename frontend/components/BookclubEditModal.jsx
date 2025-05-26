import { useState, useEffect } from "react";
import "../styles/BookclubCreateModal.css";

function BookclubEditModal({ club, onClose, onEdit }) {
  /* ---------- 상태 ---------- */
  const [clubName, setClubName] = useState(club?.title || "");
  const [clubDescription, setClubDescription] = useState(
    club?.description || ""
  );
  const [capacity, setCapacity] = useState(club?.maxParticipants || 2);
  const [deadline, setDeadline] = useState("");
  const [loading, setLoading] = useState(false);

  // 마감일 초기값 설정
  useEffect(() => {
    if (club?.applicationDeadline) {
      const date = new Date(club.applicationDeadline);
      setDeadline(date.toISOString().split("T")[0]);
    }
  }, [club]);

  /* ---------- 수정 ---------- */
  const handleEdit = async () => {
    if (!clubName || !clubDescription || !deadline)
      return alert("모든 항목을 입력하세요!");
    if (clubName.length > 50) return alert("클럽 이름 50자 제한입니다.");
    if (clubDescription.length > 150)
      return alert("클럽 소개 150자 제한입니다.");
    if (capacity < 2 || capacity > 5) return alert("모집 인원은 2~5명!");

    try {
      setLoading(true);

      const editedData = {
        title: clubName,
        description: clubDescription,
        minParticipants: 2,
        maxParticipants: capacity,
        applicationDeadline: new Date(deadline).toISOString().slice(0, 19),
        activityDurationDays: club?.activityDurationDays || 30,
        status: club?.status || "OPEN",
      };

      onEdit(editedData);
    } catch (err) {
      console.error("Failed to edit club:", err);
      alert("북클럽 수정에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  /* ---------- UI ---------- */
  return (
    <div className="create-backdrop">
      <div className="create-modal">
        <h3>북클럽 수정하기</h3>

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

        <div className="create-btns">
          <button className="cancel" onClick={onClose} disabled={loading}>
            취소
          </button>
          <button className="confirm" onClick={handleEdit} disabled={loading}>
            {loading ? "수정 중..." : "수정하기"}
          </button>
        </div>
      </div>
    </div>
  );
}

export default BookclubEditModal;
