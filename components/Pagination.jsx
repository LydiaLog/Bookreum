function Pagination({ currentPage, totalPages, onPageChange, pageButtonCount = 5 }) {
  /* 현재 그룹 계산(페이지 버튼 묶음) */
  const pageGroup  = Math.floor((currentPage - 1) / pageButtonCount);
  const startPage  = pageGroup * pageButtonCount + 1;
  const endPage    = Math.min(startPage + pageButtonCount - 1, totalPages);

  /* ◀, ▶ 한 페이지씩 이동 */
  const handlePrev = () => {
    if (currentPage > 1) onPageChange(currentPage - 1);
  };
  const handleNext = () => {
    if (currentPage < totalPages) onPageChange(currentPage + 1);
  };

  return (
    <div
      style={{
        marginTop: 24,
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        gap: 4,
        flexWrap: "wrap",
      }}
    >
      {/* ◀ 이전 페이지 */}
      <button
        onClick={handlePrev}
        disabled={currentPage === 1}
        style={buttonStyle(currentPage === 1)}
      >
        ◀
      </button>

      {/* 페이지 번호 그룹 */}
      {[...Array(endPage - startPage + 1)].map((_, i) => {
        const pageNum = startPage + i;
        return (
          <button
            key={pageNum}
            onClick={() => onPageChange(pageNum)}
            style={numberButtonStyle(pageNum === currentPage)}
          >
            {pageNum}
          </button>
        );
      })}

      {/* ▶ 다음 페이지 */}
      <button
        onClick={handleNext}
        disabled={currentPage === totalPages}
        style={buttonStyle(currentPage === totalPages)}
      >
        ▶
      </button>
    </div>
  );
}

/* 공통 버튼 스타일 */
const buttonStyle = (disabled) => ({
  margin: "0 4px",
  padding: "6px 10px",
  cursor: disabled ? "not-allowed" : "pointer",
  background: "#fff",
  border: "1px solid #ccc",
  borderRadius: 6,
});

/* 페이지 숫자 버튼 */
const numberButtonStyle = (active) => ({
  margin: "0 4px",
  padding: "6px 12px",
  background: active ? "#849974" : "#fff",
  color: active ? "#fff" : "#000",
  border: active ? "2px solid #849974" : "1px solid #ccc",
  borderRadius: 6,
  cursor: "pointer",
});

export default Pagination;