import React, { useEffect, useState } from 'react';
import '../styles/Toast.css';

function Toast({ message, onClose, duration = 1500 }) {
  const [visible, setVisible] = useState(true);

  useEffect(() => {
    const hideTimer = setTimeout(() => setVisible(false), duration);
    const removeTimer = setTimeout(onClose, duration + 300); // fade-out 후 제거
    return () => {
      clearTimeout(hideTimer);
      clearTimeout(removeTimer);
    };
  }, [duration, onClose]);

  return (
    <div className={`toast ${visible ? 'show' : 'hide'}`}>
      {message}
    </div>
  );
}

export default Toast;