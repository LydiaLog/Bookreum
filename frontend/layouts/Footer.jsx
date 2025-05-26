function Footer() {
    return (
      <footer
        style={{
          width: "100%",
          maxWidth: "100vw",
          padding: "20px 32px",
          color: "#444",
          fontSize: "14px",
          textAlign: "left",
          marginTop: "40px",
          borderTop: "1px solid #ddd",
          boxSizing: "border-box",
        }}
      >
        <div style={{ fontWeight: "bold", fontSize: "16px", marginBottom: "6px" }}>
          북그러움
        </div>
        <p style={{ margin: "4px 0", fontSize: "12px" }}>
          내향인을 위한 비대면 북클럽
        </p>
        <p style={{ margin: "4px 0", fontSize: "12px" }}>
          <a href="/terms" style={linkStyle}>이용약관</a> ·{" "}
          <a href="/privacy" style={linkStyle}>개인정보처리방침</a> ·{" "}
          <a href="mailto:support@bookreum.com" style={linkStyle}>문의</a>
        </p>
        <p style={{ marginTop: "8px", color: "#999", fontSize: "10px" }}>
          ⓒ 2025 Bookreum. All rights reserved.
        </p>
      </footer>
    );
  }
  
  const linkStyle = {
    textDecoration: "none",
    color: "#444",
    margin: "0 4px",
  };
  
export default Footer;