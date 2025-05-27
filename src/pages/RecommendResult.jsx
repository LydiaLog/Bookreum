import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import api from "../axiosConfig";          // axios 인스턴스 (baseURL 지정돼 있어야 함)
import BookGrid from "../components/BookGrid";
import SkeletonGrid from "../components/SkeletonGrid";

function RecommendResult() {
  const [params] = useSearchParams();
  const query = params.get("query") || "";

  const [state, setState] = useState({
    loading: true,
    error: null,
    data: [],
  });

  useEffect(() => {
    let ignore = false;

    async function fetchRecommend() {
      try {
        setState({ loading: true, error: null, data: [] });

        // 🔗 GET /api/recommend/content?title={query}
        const { data } = await api.get("/api/recommend/content", {
          params: { title: query },
        });
        if (ignore) return;

        console.log("📌 API 응답 데이터:", data);  // API 응답 확인

        // 👉 BookGrid 가 요구하는 필드명으로 매핑
        const mapped = data.map((b, idx) => ({
          id: b.isbn13 ?? idx,           // ID로 ISBN 사용
          title: b.title,
          author: b.author,
          genre: b.genre ?? "Unknown",   // 장르가 없을 경우 기본값
          summary: b.description,        // 설명 필드 매핑
          thumbnail: b.coverImageUrl,    // 표지 이미지
          rating: b.rating ?? 0,         // 평점 (없으면 0)
        }));

        setState({ loading: false, error: null, data: mapped });
      } catch (err) {
        if (!ignore) setState({ loading: false, error: err, data: [] });
      }
    }

    fetchRecommend();
    return () => { ignore = true; };
  }, [query]);

  return (
    <section style={{ padding: "120px 40px" }}>
      <h3 style={{ fontSize: "24px", marginBottom: "24px" }}>
        “{query}” 검색 결과
      </h3>

      {state.loading && <SkeletonGrid cards={8} />}
      {state.error && (
        <p style={{ color: "red" }}>추천을 불러오지 못했습니다 🥲</p>
      )}
      {!state.loading && !state.error && <BookGrid books={state.data} />}
    </section>
  );
}

export default RecommendResult;