import { useParams } from 'react-router-dom';
import { useState, useRef, useEffect } from 'react';
import dummyBookclubs from '../data/dummyBookclubs';
import '../styles/BookclubDetail.css';

function BookclubDetail() {
  const { id } = useParams();
  const club = dummyBookclubs.find((c) => c.id.toString() === id);
  const currentUser = '나';

  /* 채팅(댓글) 상태 */
  const [messages, setMessages] = useState([
    { id: 1, nickname: 'alice', text: '이번 달 책 다 읽으셨나요?' },
    { id: 2, nickname: currentUser, text: '저는 막 끝냈어요! 결말 충격 😲' },
    { id: 3, nickname: 'bob', text: '아직 3장인데 스포 금지 🤐' },
  ]);
  const [input, setInput] = useState('');

  /* 자동 스크롤용 ref */
  const bottomRef = useRef(null);
  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSend = () => {
    if (input.trim() === '') return;
    setMessages([
      ...messages,
      { id: Date.now(), nickname: currentUser, text: input.trim() },
    ]);
    setInput('');
  };

  if (!club) return <p>존재하지 않는 북클럽입니다 😢</p>;

  return (
    <div className="club-detail">
      {/* ── 클럽 헤더 ───────────────────────────────── */}
      <h2 className="club-title">{club.title}</h2>
      <p className="club-sub">{club.book} | {club.author}</p>

      {/* ── 채팅 영역 ───────────────────────────────── */}
      <div className="chat-box">
        {messages.map((m) => (
          <div
            key={m.id}
            className={
              m.nickname === currentUser
                ? 'message-row my'
                : 'message-row other'
            }
          >
            {m.nickname !== currentUser && (
              <span className="msg-nick">{m.nickname}</span>
            )}
            <div className="msg-bubble">{m.text}</div>
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