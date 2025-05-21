import SearchIcon from "../icons/SearchIcon";

function SearchBar({ value, onChange, onSearch }) {
  const handleKey = (e) => {
    if (e.key === 'Enter') onSearch();
  };

  return (
    <div style={{ position:'relative', width:300, marginTop:10 }}>
      <input
        type="text"
        value={value}
        onChange={onChange}
        onKeyDown={handleKey}
        placeholder="검색어를 입력하세요"
        style={{
          width:'100%', height:45, padding:'8px 36px 8px 12px',
          fontSize:16, border:'1px solid #ccc', borderRadius:8,
          boxSizing:'border-box'
        }}
      />
      <button
        onClick={onSearch}
        style={{
          position:'absolute', top:'50%', right: 2, transform:'translateY(-50%)',
          background:'transparent', border:'none', cursor:'pointer'
        }}
        aria-label="검색"
      >
        <SearchIcon width={18} height={18} />
      </button>
    </div>
  );
}

export default SearchBar;