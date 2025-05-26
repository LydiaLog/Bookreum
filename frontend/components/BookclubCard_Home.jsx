import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import defaultAvatar from "../assets/profile.jpg";
import BookclubJoinModal from "./BookclubJoinModal";
import api from "../axiosConfig";
import "../styles/BookclubCard.css";

const BookclubCard_Home = ({ club, onCancelMatch }) => {
  const navigate = useNavigate();
  const [filledCircles, setFilledCircles] = useState(1);
  const isMatched = club.status === "MATCHED";
  const isClosed = club.status === "CLOSED";
  const [showJoinModal, setShowJoinModal] = useState(false);

  useEffect(() => {
    // 모집 인원 실시간 상태 (모임장 포함)
    const members = (club.currentParticipants ?? 0) + 1;
    setFilledCircles(Math.max(1, members));
  }, [club]);

  const handleClick = () => {
    setShowJoinModal(true);
  };

  const handleJoin = async () => {
    try {
      const userId = localStorage.getItem("userId") || "1";
      await api.post(`/api/clubs/${club.id}/applications`, {
        userId: parseInt(userId),
      });
      setShowJoinModal(false);
      navigate(`/bookclub/${club.id}`);
    } catch (err) {
      console.error(err);
      alert(err.response?.data?.message || "참여에 실패했습니다.");
    }
  };

  const handleCancelMatch = async () => {
    try {
      const userId = localStorage.getItem("userId") || "1";
      await api.delete(`/api/clubs/${club.id}/applications`, {
        params: { userId },
      });
      setShowJoinModal(false);
      if (onCancelMatch) onCancelMatch(club.id);
    } catch (err) {
      console.error(err);
      alert(err.response?.data?.message || "매칭 취소에 실패했습니다.");
    }
  };

  return (
    <>
      <div onClick={handleClick} className="bookclub-card">
        <div className="bookclub-card__image">
          {club.book?.coverImageUrl || club.coverImageUrl ? (
            <img
              src={club.book?.coverImageUrl || club.coverImageUrl}
              alt={`${club.title} 대표 이미지`}
            />
          ) : (
            <div className="bookclub-card__image-placeholder">이미지</div>
          )}
        </div>

        <div className="bookclub-card__content">
          <div
            className={`bookclub-card__status ${
              isClosed ? "closed" : isMatched ? "matched" : "open"
            }`}
          >
            📢 {isClosed ? "모집 마감" : isMatched ? "매칭 완료" : "모집 중"}
          </div>
          <h3 className="bookclub-card__title">{club.title}</h3>
          <p className="bookclub-card__book">
            📖 {club.book?.title} | {club.book?.author}
          </p>

          <div className="bookclub-card__info">
            <div className="bookclub-card__creator">
              {club.createdByProfileImageUrl ? (
                <img
                  src={club.createdByProfileImageUrl}
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
                {club.createdByNickname}
              </p>
            </div>

            {isClosed ? (
              <p className="bookclub-card__closed-text">모집 마감</p>
            ) : isMatched ? (
              <p className="bookclub-card__matched-text">매칭 완료</p>
            ) : (
              <div className="bookclub-card__details">
                <p className="bookclub-card__date">
                  ~ {new Date(club.applicationDeadline).toLocaleDateString()}
                </p>
                <div className="bookclub-card__participants">
                  {[...Array(club.maxParticipants)].map((_, index) => (
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

      {showJoinModal && (
        <BookclubJoinModal
          club={{
            ...club,
            bookTitle: club.book?.title,
            bookAuthor: club.book?.author,
            nickname: club.createdByNickname,
            date: club.applicationDeadline?.split("T")[0],
            description: club.description,
            coverUrl: club.book?.coverImageUrl || club.coverImageUrl,
          }}
          onClose={() => setShowJoinModal(false)}
          onJoin={handleJoin}
          onCancel={handleCancelMatch}
          isJoinDisabled={isClosed}
          applied={isMatched}
          onChat={() => {
            navigate(`/bookclub/${club.id}/chat`);
            setShowJoinModal(false);
          }}
        />
      )}
    </>
  );
};

export default BookclubCard_Home;
