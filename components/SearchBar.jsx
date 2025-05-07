import SearchIcon from "../icons/SearchIcon";

function SearchBar({ value, onChange, onSearch }) {
  return (
    <div
      style={{
        position: "relative",
        width: "300px",
        marginTop: "10px",
      }}
    >
      <input
        type="text"
        value={value}
        onChange={onChange}
        placeholder="검색어를 입력하세요"
        style={{
          width: "100%",
          height: "45px",
          padding: "8px 36px 8px 12px",
          fontSize: "16px",
          border: "1px solid #ccc",
          borderRadius: "8px",
          boxSizing: "border-box",
        }}
      />

      <button
        onClick={onSearch}
        style={{
          position: "absolute",
          top: "50%",
          right: "10px",
          transform: "translateY(-50%)",
          border: "none",
          background: "transparent",
          padding: "0",
          cursor: "pointer",
          outline: "none",
        }}
      >
        <SearchIcon width={18} height={18} />
      </button>
    </div>
  );
}

export default SearchBar;