function SortDropdown({ value, onChange, options = [] }) {
  return (
    <select
      value={value}
      onChange={onChange}
      style={{
        padding: "8px 12px",
        border: "1px solid #ccc",
        borderRadius: "6px",
        fontSize: "14px",
        backgroundColor: "#fff",
        cursor: "pointer",
      }}
    >
      {options.map((option) => (
        <option key={option.value} value={option.value}>
          {option.label}
        </option>
      ))}
    </select>
  );
}

export default SortDropdown;
