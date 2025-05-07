function Pagination({ currentPage, totalPages, onPageChange, pageButtonCount = 5 }) {
    const pageGroup = Math.floor((currentPage - 1) / pageButtonCount);
    const startPage = pageGroup * pageButtonCount + 1;
    const endPage = Math.min(startPage + pageButtonCount - 1, totalPages);
  
    const handlePrevGroup = () => {
      if (startPage > 1) onPageChange(startPage - 1);
    };
  
    const handleNextGroup = () => {
      if (endPage < totalPages) onPageChange(endPage + 1);
    };
  
    return (
      <div 
        style={{ 
            marginTop: "24px",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            gap: "4px",
            flexWrap: "wrap",
      }}>
        {/* ◀ 이전 */}
        <button
          onClick={handlePrevGroup}
          disabled={startPage === 1}
          style={buttonStyle(startPage === 1)}
        >
          ◀
        </button>
  
        {/* 페이지 번호 */}
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
  
        {/* ▶ 다음 */}
        <button
          onClick={handleNextGroup}
          disabled={endPage === totalPages}
          style={buttonStyle(endPage === totalPages)}
        >
          ▶
        </button>
      </div>
    );
}
  
const buttonStyle = (disabled) => ({
    margin: "0 4px",
    padding: "6px 10px",
    cursor: disabled ? "not-allowed" : "pointer",
    background: "white",
    border: "1px solid #ccc",
    borderRadius: "6px",
});
  
const numberButtonStyle = (active) => ({
    margin: "0 4px",
    padding: "6px 12px",
    background: active ? "#849974" : "white",
    color: active ? "white" : "black",
    border: active ? "2px solid #849974" : "1px solid #ccc",
    borderRadius: "6px",
    cursor: "pointer",
});
  
export default Pagination;