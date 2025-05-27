// src/components/ConfirmModal.jsx
import React from 'react';
import '../styles/ConfirmModal.css';

function ConfirmModal({ message, onConfirm, onCancel }) {
  return (
    <div className="modal-backdrop">
      <div className="modal-box">
        <p className="modal-message">{message}</p>
        <div className="modal-actions">
          <button className="btn-confirm" onClick={onConfirm}>삭제</button>
          <button className="btn-cancel" onClick={onCancel}>취소</button>
        </div>
      </div>
    </div>
  );
}

export default ConfirmModal;