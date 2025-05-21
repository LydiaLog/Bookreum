import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import BookclubCard_Home from "../components/BookclubCard_Home";
import BooklogCard_Home from "../components/BooklogCard_Home";
import BookRecCard from "../components/BookRecCard";
import "../styles/Home.css";

import dummyBooklogs from "../data/dummyBooklogs";

// ✅ 더미 북클럽 데이터 (2개 고정)
const dummyClubs = [
  {
    id: "1",
    title: "이야기를 나눠요",
    bookTitle: "홍학의 자리",
    imageUrl: "/images/puppy.jpg",
    dateRange: "4.10 ~ 4.20",
    maxMembers: 5,
    currentMembers: 3,
  },
  {
    id: "2",
    title: "책 읽었나요?",
    bookTitle: "우울한 미래에 우리가 있어서",
    imageUrl: "/images/cat.jpg",
    dateRange: "4.01 ~ 4.30",
    maxMembers: 3,
    currentMembers: 3,
  },
];

// ✅ 더미 추천 책 데이터
const dummyBooks = [
  {
    id: "1",
    title: "조용한 밤, 별빛 독서",
    author: "김북",
    genre: "에세이",
    coverUrl: "/images/dummy1.jpg",
  },
  {
    id: "2",
    title: "내향인의 대화법",
    author: "이토크",
    genre: "자기계발",
    coverUrl: "/images/dummy2.jpg",
  },
];

// 더미 북로그 데이터
const dummyLogs = dummyBooklogs;

function Home() {
  const [books, setBooks] = useState(null);
  const [latestClubs, setLatestClubs] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchLatestClubs = async () => {
      try {
        const token = localStorage.getItem("accessToken");
        const headers = {
          "Content-Type": "application/json",
        };

        if (token) {
          headers["Authorization"] = `Bearer ${token}`;
        }

        const response = await fetch(
          "http://localhost:9090/api/clubs/public?sort=latest&size=2&page=0",
          { headers }
        );

        if (!response.ok) {
          throw new Error("북클럽 데이터를 가져오는데 실패했습니다.");
        }

        const data = await response.json();
        console.log("Latest clubs:", data);
        setLatestClubs(data.content);
      } catch (err) {
        console.error("Error fetching latest clubs:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchLatestClubs();
  }, []);

  useEffect(() => {
    // 백엔드 붙으면 아래 fetch 사용
    /*
    fetch('/api/recommend?limit=2')
      .then((res) => {
        if (!res.ok) throw new Error(res.status);
        return res.json();
      })
      .then(setBooks)
      .catch(() => setBooks([]));
    */

    // 지금은 더미 데이터로 세팅
    setBooks(dummyBooks);
  }, []);

  if (books === null) return <p>로딩 중…</p>;
  if (books.length === 0) return <p>추천을 불러오지 못했어요 😥</p>;

  return (
    <div className="home-contents">
      <div className="home-left">
        {/* 📚 북클럽 모임 섹션 */}
        <section className="club-section">
          <div className="club-section__title-line">
            <hr />
            <h2>⋆｡°✩ 지금 북그러움에서 열린 모임들 ⋆｡°✩</h2>
            <hr />
          </div>
          <p className="club-section__subtitle">
            조용히 함께 읽는 공간, 여기에 있어요
          </p>

          <div className="club-section__cards">
            {loading ? (
              <p>로딩 중...</p>
            ) : latestClubs.length > 0 ? (
              latestClubs.map((club) => (
                <BookclubCard_Home key={club.id} club={club} />
              ))
            ) : (
              <p>현재 진행 중인 모임이 없습니다.</p>
            )}
          </div>

          <div className="club-section__more">
            <button onClick={() => navigate("/bookclub")}>
              더보기 <span aria-hidden="true">▶</span>
            </button>
          </div>
        </section>

        {/* 📖 북로그 미리보기 섹션 */}
        <section className="booklog-section">
          <div className="booklog-section__title-line">
            <hr />
            <h2>⋆｡°✩ 북그러움 유저들이 머물다 간 이야기 ⋆｡°✩</h2>
            <hr />
          </div>
          <p className="booklog-section__subtitle">
            책에서 잠깐 멈춰, 마음을 적어보았어요
          </p>

          <div className="booklog-section__cards">
            {dummyLogs.slice(0, 2).map((log) => (
              <BooklogCard_Home key={log.id} log={log} />
            ))}
          </div>

          <div className="booklog-section__more">
            <button onClick={() => navigate("/bookloglist")}>
              더보기 <span aria-hidden="true">▶</span>
            </button>
          </div>
        </section>
      </div>

      {/* 🤖 AI 책 추천 */}
      <div className="rec-container">
        <BookRecCard books={books} onMoreClick={() => navigate("/recommend")} />
      </div>
    </div>
  );
}

export default Home;
