import { useState, useEffect } from 'react';

function BookclubCard({ bookclub, onClick }) {
  const [filledCircles, setFilledCircles] = useState(0);
  const [isClosed, setIsClosed] = useState(false);

  useEffect(() => {
    // 모집 인원 실시간 상태
    setFilledCircles(bookclub.currentMembers || 0);

    // 모집 마감 여부 확인
    const today = new Date().toISOString().split('T')[0];
    setIsClosed(new Date(today) > new Date(bookclub.date));
  }, [bookclub]);

  return (
    <div
      onClick={onClick}
      style={{
        background: "#fff",
        border: "1px solid #D9D9D9",
        borderRadius: "8px",
        padding: "16px",
        margin: "25px auto",
        width: "290px",
        height: "250px",
        cursor: "pointer",
      }}
    >
      {/* ─── 대표 이미지 영역 ─── */}
      {bookclub.coverUrl ? (
        <img
          src={bookclub.coverUrl}
          alt={`${bookclub.title} 대표 이미지`}
          style={{
            width: "280px",
            height: "165px",
            objectFit: "cover",
            borderRadius: "3px",
            display: "block",
            margin: "2px auto 0",
          }}
        />
      ) : (
        <div 
          style={{ 
            width: "280px", 
            height: "165px", 
            background: "#ddd" ,
            borderRadius: "3px",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            color: "#aaa",
            margin: "2px auto 0",
          }}
        >
          이미지
        </div>
      )}

      <h3 style={{ fontSize: '16px', fontWeight: 'bold', margin: '4px 0 0 0', paddingLeft: '10px' }}>{bookclub.title}</h3>
      <p style={{ fontSize: '11px', color: '#666', margin: '0', paddingLeft: '10px' }}>{bookclub.book} | {bookclub.author}</p>

      <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '10px' }}>
        <div style={{ display: 'flex' }}>
          <div style={{ width: '25px', height: '25px', borderRadius: '50px', background: '#ddd', marginTop: '7px', marginLeft: '5px' }} />
          <p style={{ fontSize: '11px', color: '#888', paddingLeft: '7px' }}>{bookclub.nickname}</p>
        </div>

        {isClosed ? (
          <p style={{ fontSize: '11px', color: '#ff4d4f', marginRight: '10px'}}>모집 마감</p>
        ) : (
          <div style={{ textAlign: 'right' }}>
            <p style={{ fontSize: '11px', color: '#888', marginRight: '10px' }}>~ {bookclub.date}</p>
            <div style={{ display: 'flex', gap: '4px', justifyContent: 'center' }}>
              {[...Array(bookclub.capacity)].map((_, index) => (
                <div
                  key={index}
                  style={{
                    width: '11px',
                    height: '11px',
                    borderRadius: '50%',
                    marginTop: '-10px',
                    background: index < filledCircles ? '#849974' : '#D9D9D9',
                  }}
                />
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default BookclubCard;