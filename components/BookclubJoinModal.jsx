// src/components/BookclubJoinModal.jsx
import React from 'react';
import '../styles/BookclubJoinModal.css';

function BookclubJoinModal({ club, onClose, onJoin }) {
  if (!club) return null;

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <h3>북클럽 참여하기</h3>
        <p><strong>북클럽 이름:</strong> {club.title}</p>
        <p><strong>책 제목:</strong> {club.book}</p>
        <p><strong>작가 이름:</strong> {club.author}</p>
        <p><strong>방장:</strong> {club.nickname}</p>
        <p><strong>모집 마감:</strong> {club.date}</p>
        <p><strong>소개:</strong> {club.description || '소개글이 없습니다.'}</p>
        
        <div className="modal-buttons">
          <button onClick={onJoin} className="join-button">참여하기</button>
          <button onClick={onClose} className="cancel-button">취소</button>
        </div>
      </div>
    </div>
  );
}

export default BookclubJoinModal;