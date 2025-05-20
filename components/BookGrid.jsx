// src/components/BookGrid.jsx
import { useState } from "react";
import BookCard from "./BookCard";
import BookModal from "./BookModal";
import "../styles/BookGrid.css";

function BookGrid({ books }) {
  const [selected, setSelected] = useState(null);

  return (
    <>
      <div className="book-grid">
        {books.map((b) => (
          <BookCard key={b.id} book={b} onClick={setSelected} />
        ))}
      </div>

      {selected && (
        <BookModal
          book={selected}
          onClose={() => setSelected(null)}
        />
      )}
    </>
  );
}

export default BookGrid;