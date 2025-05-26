// src/components/BookclubJoinModal.jsx
import React, { useState } from "react";
import "../styles/BookclubJoinModal.css";
import { useNavigate } from "react-router-dom";

function BookclubJoinModal({
  club,
  onClose,
  onJoin,
  onChat,
  onCancel,
  isJoinDisabled,
  applied,
  isOwner = false,
  onEdit,
  onDelete,
}) {
  const navigate = useNavigate();
  const [showManageModal, setShowManageModal] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);

  if (!club) return null;

  const handleEditClick = () => {
    setShowManageModal(false);
    if (onEdit) onEdit();
  };

  const handleDeleteClick = () => {
    setShowManageModal(false);
    setShowDeleteConfirm(true);
  };

  const handleDeleteConfirm = () => {
    setShowDeleteConfirm(false);
    if (onDelete) onDelete();
    onClose();
  };

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        {isOwner && (
          <button
            className="menu-button"
            onClick={() => setShowManageModal(true)}
            style={{
              position: "absolute",
              top: "16px",
              right: "16px",
              background: "none",
              border: "none",
              fontSize: "18px",
              cursor: "pointer",
              padding: "4px",
              borderRadius: "4px",
            }}
          >
            ⋯
          </button>
        )}

        <h3>북클럽 참여하기</h3>
        <p>
          <strong>북클럽 이름:</strong> {club.title}
        </p>
        <p>
          <strong>책 제목:</strong> {club.bookTitle}
        </p>
        <p>
          <strong>작가 이름:</strong> {club.bookAuthor}
        </p>
        <p>
          <strong>방장:</strong> {club.nickname}
        </p>
        <p>
          <strong>모집 마감:</strong> {club.date}
        </p>
        <p>
          <strong>소개:</strong> {club.description || "소개글이 없습니다."}
        </p>

        {/* 참여자 목록 표시 */}
        {club.participants && club.participants.length > 0 && (
          <div style={{ marginTop: "12px" }}>
            <strong>
              참여자 ({club.currentParticipants || club.participants.length}명):
            </strong>
            <div
              style={{
                marginTop: "8px",
                display: "flex",
                flexWrap: "wrap",
                gap: "8px",
              }}
            >
              {club.participants.map((participant, index) => (
                <div
                  key={index}
                  style={{
                    display: "flex",
                    alignItems: "center",
                    gap: "6px",
                    background: "#f5f5f5",
                    padding: "4px 8px",
                    borderRadius: "12px",
                    fontSize: "13px",
                  }}
                >
                  <img
                    src={
                      participant.profileImage || "/uploads/temp/profile.jpg"
                    }
                    alt="프로필"
                    style={{
                      width: "20px",
                      height: "20px",
                      borderRadius: "50%",
                      border: "1px solid #ddd",
                    }}
                  />
                  <span>{participant.nickname || participant.name}</span>
                  {participant.isLeader && (
                    <span style={{ color: "#ff9800", fontSize: "11px" }}>
                      👑
                    </span>
                  )}
                </div>
              ))}
            </div>
          </div>
        )}

        <div className="modal-buttons">
          {applied ? (
            <>
              <button className="chat-button" onClick={onChat}>
                채팅으로 가기
              </button>
              <button className="cancel-button" onClick={onClose}>
                취소
              </button>
            </>
          ) : (
            <>
              <button
                className="join-button"
                onClick={onJoin}
                disabled={isJoinDisabled}
                style={{
                  backgroundColor: isJoinDisabled ? "#ddd" : "#B4C9A4",
                  cursor: isJoinDisabled ? "not-allowed" : "pointer",
                  opacity: isJoinDisabled ? 0.6 : 1,
                }}
              >
                {isJoinDisabled ? "모집 마감" : "참여하기"}
              </button>
              <button className="cancel-button" onClick={onClose}>
                취소
              </button>
            </>
          )}
        </div>
      </div>

      {showManageModal && (
        <div
          className="modal-backdrop"
          onClick={() => setShowManageModal(false)}
        >
          <div
            className="modal small-modal"
            onClick={(e) => e.stopPropagation()}
          >
            <h3>북클럽 관리</h3>
            <p>어떤 작업을 하시겠습니까?</p>
            <div className="modal-buttons">
              <button className="edit-button" onClick={handleEditClick}>
                수정
              </button>
              <button className="delete-button" onClick={handleDeleteClick}>
                삭제
              </button>
              <button
                className="cancel-button"
                onClick={() => setShowManageModal(false)}
              >
                취소
              </button>
            </div>
          </div>
        </div>
      )}

      {showDeleteConfirm && (
        <div
          className="modal-backdrop"
          onClick={() => setShowDeleteConfirm(false)}
        >
          <div
            className="modal small-modal"
            onClick={(e) => e.stopPropagation()}
          >
            <h3>북클럽 삭제</h3>
            <p>정말로 이 북클럽을 삭제하시겠습니까?</p>
            <p style={{ color: "#d32f2f", fontSize: "0.9rem" }}>
              이 작업은 되돌릴 수 없습니다.
            </p>
            <div className="modal-buttons">
              <button
                className="delete-confirm-button"
                onClick={handleDeleteConfirm}
              >
                삭제
              </button>
              <button
                className="cancel-button"
                onClick={() => setShowDeleteConfirm(false)}
              >
                취소
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default BookclubJoinModal;
