import { useState, useEffect } from "react";
import defaultAvatar from "../assets/profile.jpg";
import api from "../axiosConfig";
import { useNavigate } from "react-router-dom";

function BookclubCard({ bookclub, onClick }) {
  const [filledCircles, setFilledCircles] = useState(1);
  const [applied, setApplied] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    // 모집 인원 실시간 상태 (모임장 포함)
    const members = (bookclub.currentParticipants ?? 0) + 1;
    setFilledCircles(Math.max(1, members));
  }, [bookclub]);

  useEffect(() => {
    const userId = localStorage.getItem("userId") || "1";
    console.log("Checking application status for club:", bookclub.id);
    api
      .get(`/api/clubs/${bookclub.id}/applications/status`, {
        params: { userId },
      })
      .then((res) => {
        console.log("Application status response:", res.data);
        setApplied(res.data);
      })
      .catch((err) => {
        console.error("Error checking application status:", err);
        setApplied(false);
      });
  }, [bookclub.id]);

  const isMatched = bookclub.status === "MATCHED";
  const isClosed = bookclub.status === "CLOSED";

  const handleClick = () => {
    console.log("Card clicked:", {
      id: bookclub.id,
      applied,
      isMatched,
      status: bookclub.status,
    });
    onClick({ ...bookclub, applied, isMatched });
  };

  return (
    <div onClick={handleClick} className="bookclub-card">
      {/* ─── 대표 이미지 영역 ─── */}
      <div className="bookclub-card__image">
        {bookclub.book?.coverImageUrl || bookclub.coverImageUrl ? (
          <img
            src={bookclub.book?.coverImageUrl || bookclub.coverImageUrl}
            alt={`${bookclub.title} 대표 이미지`}
          />
        ) : (
          <div className="bookclub-card__image-placeholder">이미지</div>
        )}
        <div
          className={`bookclub-card__status ${
            isClosed ? "closed" : isMatched || applied ? "matched" : "open"
          }`}
        >
          {isClosed
            ? "모집 마감"
            : isMatched || applied
            ? "매칭 중"
            : "모집 중"}
        </div>
      </div>

      <div className="bookclub-card__content">
        <h3 className="bookclub-card__title">{bookclub.title}</h3>
        <p className="bookclub-card__book">
          {bookclub.book?.title} | {bookclub.book?.author}
        </p>

        <div className="bookclub-card__info">
          <div className="bookclub-card__creator">
            {bookclub.createdByProfileImageUrl ? (
              <img
                src={bookclub.createdByProfileImageUrl}
                alt="모임장 프로필"
                className="bookclub-card__creator-image"
              />
            ) : (
              <img
                src={defaultAvatar}
                alt="기본 프로필"
                className="bookclub-card__creator-image"
              />
            )}
            <p className="bookclub-card__creator-name">
              {bookclub.createdByNickname}
            </p>
          </div>

          {applied ? (
            <span className="bookclub-card__applied-text">
              이미 신청한 클럽입니다
            </span>
          ) : isClosed ? null : isMatched ? null : (
            <div className="bookclub-card__details">
              <p className="bookclub-card__date">
                ~ {new Date(bookclub.applicationDeadline).toLocaleDateString()}
              </p>
              <div className="bookclub-card__participants">
                {[...Array(bookclub.maxParticipants)].map((_, index) => (
                  <div
                    key={index}
                    className={`bookclub-card__participant-circle ${
                      index < filledCircles ? "filled" : ""
                    }`}
                  />
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default BookclubCard;
