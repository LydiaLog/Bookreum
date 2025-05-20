import SearchIcon from "../icons/SearchIcon";

/**
 * 공통 검색 바
 * @param {string}   value       - 입력 값
 * @param {Function} onChange    - 인풋 onChange 핸들러
 * @param {Function} onSearch    - 검색 실행 함수 (아이콘 클릭·Enter)
 * @param {string}   placeholder - placeholder (선택)
 */
function SearchBar_ver2({
  value = "",
  onChange,
  onSearch,
  placeholder = "키워드를 입력하세요.",
}) {
  /** Enter 키 → 검색 */
  const handleKeyDown = (e) => {
    if (e.key === "Enter" && onSearch) onSearch();
  };

  return (
    <div
      style={{
        backgroundColor: "#fff",
        borderRadius: "999px",
        border: "1px solid #ccc",
        display: "flex",
        alignItems: "center",
        padding: "4px 16px",
        width: "600px",
        height: "40px",
      }}
    >
      <input
        type="text"
        value={value}
        onChange={onChange}
        onKeyDown={handleKeyDown}
        placeholder={placeholder}
        style={{
          flex: 1,
          border: "none",
          outline: "none",
          fontSize: "14px",
          background: "transparent",
        }}
      />

      {/* 🔍 아이콘을 버튼으로 만들어 검색 실행 */}
      <button
        onClick={onSearch}
        aria-label="검색"
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          background: "transparent",
          border: "none",
          cursor: "pointer",
          padding: 0,
        }}
      >
        <SearchIcon width={18} height={18} />
      </button>
    </div>
  );
}

export default SearchBar_ver2;