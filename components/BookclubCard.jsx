import { useNavigate } from "react-router-dom";

function BookclubCard({ bookclub }) {
  const navigate = useNavigate();

  const handleClick = () => navigate(`/bookclub/${bookclub.id}`);

  return (
    <div
      onClick={handleClick}
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
            background: "#ddd",
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

      {/* ─── 텍스트 영역 ─── */}
      <h3
        style={{
          fontSize: "16px",
          fontWeight: "bold",
          margin: "4px 0 0 0",
          paddingLeft: "10px",
        }}
      >
        {bookclub.title}
      </h3>
      <p
        style={{
          fontSize: "11px",
          color: "#666",
          margin: "0",
          paddingLeft: "10px",
        }}
      >
        {bookclub.book} | {bookclub.author}
      </p>

      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          marginTop: "10px",
        }}
      >
        <div style={{ display: "flex" }}>
          <div
            style={{
              width: "25px",
              height: "25px",
              borderRadius: "50px",
              background: "#ddd",
              marginTop: "7px",
              marginLeft: "5px",
            }}
          />
          <p
            style={{
              fontSize: "11px",
              color: "#888",
              paddingLeft: "7px",
            }}
          >
            {bookclub.nickname}
          </p>
        </div>
        <p style={{ fontSize: "11px", color: "#888", marginRight: "10px" }}>
          ~ {bookclub.date}
        </p>
      </div>
    </div>
  );
}

export default BookclubCard;
