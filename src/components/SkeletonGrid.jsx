function SkeletonGrid({ cards = 8 }) {
    return (
      <div className="book-grid">
        {Array.from({ length: cards }).map((_, i) => (
          <div className="book-card skeleton" key={i} />
        ))}
      </div>
    );
  }

export default SkeletonGrid;