import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import SearchBar from "../components/SearchBar";
import SortDropdown from '../components/SortDropdown';
import BooklogCard from "../components/BooklogCard";
import Pagination from '../components/Pagination';
import '../styles/Global.css';
import '../styles/BooklogList.css';

import dummyBooklogs from '../data/dummyBooklogs';

function BooklogList() {
  const navigate = useNavigate();

  const [currentPage, setCurrentPage] = useState(1);
  const logsPerPage = 6;
  const [sortOption, setSortOption] = useState("newest");
  const [searchTerm, setSearchTerm] = useState("");

  const sortOptions = [
    {value: "newest", label: "최신순"},
    {value: "oldest", label: "오래된순"},
  ]

  const handleSortChange = (e) => {
    setSortOption(e.target.value);
    console.log("선택된 정렬 옵션:", e.target.value);
  };

  const booklogs = dummyBooklogs;

  const totalPages = Math.ceil(booklogs.length / logsPerPage);
  const indexOfLastLog = currentPage * logsPerPage;
  const indexOfFirstLog = indexOfLastLog - logsPerPage;
  const currentLogs = booklogs.slice(indexOfFirstLog, indexOfLastLog);

    return (
        <div 
            className="page-wrapper"
            style={{
                display: "flex",
                justifyContent: "center",
            }}
        >
                {/* 제목 + 부제목 */}
                <h2 className="page-title">북로그</h2>
                <p className="page-subtitle">
                조용히 읽고, 조심스럽게 남긴 당신만의 책 이야기
                </p>

            <div style={{ marginLeft: "18%" }}>
                {/* 검색창 + 정렬 메뉴 + 글쓰기 버튼 */}
                <div
                    style={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "flex-end",
                        marginTop: "20px",
                    }}
                >
                    <SearchBar
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                    <div style={{ display: "flex", gap: "10px", marginRight: "10px" }}>
                        <SortDropdown 
                            value={sortOption} 
                            onChange={handleSortChange} 
                            options={sortOptions} 
                        />
                        <button
                            style={{
                                background: "#B4C9A4",
                                opacity: "0.8",
                                padding: "8px 18px",
                                fontSize: "14px",
                                border: "none",
                                borderRadius: "6px",
                                cursor: "pointer",
                        }}
                        onClick={() => navigate('/booklogwrite')}
                        >
                            글쓰기
                        </button>
                    </div>
                </div>

                {/* 북로그 리스트 */}
                <div className="bookloglist">
                    {currentLogs.map((booklog) => (
                        <BooklogCard
                        key={booklog.id}
                        booklog={booklog}
                        onClick={() => navigate(`/booklog/${booklog.id}`)}
                        />
                    ))}
                </div>

                {/* 페이지네이션 */}
                <div style={{ textAlign: "center", marginTop: "20px" }}>
                    <Pagination
                        currentPage={currentPage}
                        totalPages={totalPages}
                        onPageChange={setCurrentPage}
                    />
                </div>
            </div>
        </div>
    );
}

export default BooklogList;