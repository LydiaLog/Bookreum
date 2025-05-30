import { useNavigate } from "react-router-dom";

function BooklogCard({ booklog }) {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/booklog/${booklog.id}`);
  };

  const truncate = (text, maxLength = 140) => {
    return text.length > maxLength ? text.slice(0, maxLength) + '…' : text;
  };

  const truncate_02 = (text, maxLength = 30) => {
    return text.length > maxLength ? text.slice(0, maxLength) + '…' : text;
  };


  return (
    <div
      onClick={handleClick}
      style={{
        background: "#f4f7f2",
        borderRadius: "12px",
        padding: "16px",
        margin: "30px auto",
        display: "flex",
        justifyContent: "space-between",
        width: "900px",
        height: "130px",
        cursor: "pointer",
      }}
    >
      <div>
        <h3 style={{ fontSize: "16px", fontWeight: "bold", margin: "0px", paddingLeft: "10px" }}>
          {booklog.title}
        </h3>
        <p style={{ fontSize: "13px", color: "#666", margin: "0px", paddingLeft: "10px" }}>
          {truncate_02(booklog.bookTitle)} | {truncate_02(booklog.bookAuthor)}
        </p>
        <p
          style={{
            fontSize: "13px",
            margin: "13px 0 0 0",
            paddingLeft: "10px",
            width: "700px",
            height: "45px",
          }}
        >
          {truncate(booklog.content)}
        </p>
        <div style={{ display: "flex", justifyContent: "space-between" }}>
          <div style={{ display: "flex", marginTop: "0px" }}>
            <div
              style={{
                width: "25px",
                height: "25px",
                borderRadius: "50px",
                background: "#ddd",
                marginTop: "9px",
                marginLeft: "5px",
              }}
            ></div>
            <p style={{ fontSize: "12px", color: "#888", paddingLeft: "8px" }}>{booklog.nickname}</p>
          </div>
          <p style={{ fontSize: "12px", color: "#888", marginRight: "-60px" }}>{booklog.date}</p>
        </div>
      </div>
      {booklog.coverUrl ? (
        <img
          src={booklog.coverUrl}
          alt="책 표지"
          style={{
            maxWidth: "175px",
            maxHeight: "125px",
            width: "auto",
            height: "auto",
            objectFit: "contain",
            borderRadius: "3px",
            display: "block",
            margin: "2px auto 0",
            marginLeft: "95px",
          }}
        />
      ) : (
        <div
          style={{
            width: "175px",
            height: "125px",
            background: "#ddd",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            color: "#aaa",
            marginTop: "3px",
          }}
        >
          이미지
        </div>
      )}
    </div>
  );
}

export default BooklogCard;