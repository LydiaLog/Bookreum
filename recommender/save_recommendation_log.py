import pandas as pd
from sentence_transformers import SentenceTransformer
import faiss
import numpy as np
from sqlalchemy import create_engine
from datetime import datetime

# 1. 책 데이터 불러오기 (book_id는 Book 테이블의 id가 아니라 isbn13 기준!)
books = pd.read_csv("data/books.csv", dtype={"book_id": str})  # 알라딘 API로 수집한 책 데이터

# 2. 임베딩 모델 로드
model = SentenceTransformer('all-MiniLM-L6-v2')
embeddings = model.encode(books["description"], normalize_embeddings=True)

# 3. FAISS 인덱스 구축
index = faiss.IndexFlatIP(embeddings.shape[1])
index.add(embeddings.astype("float32"))

# 4. 추천 함수
def recommend(user_id, base_book_isbn13, top_n=5):
    try:
        idx = books[books["book_id"] == str(base_book_isbn13)].index[0]
    except IndexError:
        print(f"❌ 책 ID {base_book_isbn13} 를 찾을 수 없습니다.")
        return []

    D, I = index.search(embeddings[idx:idx+1], top_n + 1)
    results = []

    for dist, i in zip(D[0][1:], I[0][1:]):
        rec_isbn = books.iloc[i]["book_id"]
        results.append({
            "user_id": user_id,
            "recommended_book_isbn13": rec_isbn,
            "base_book_isbn13": base_book_isbn13,
            "score": float(dist)
        })

    return results

# 5. DB 연결 설정 
engine = create_engine("mysql+pymysql://root:7h4saf0324!@localhost:3306/bookreum?charset=utf8mb4")

# 6. DB에서 isbn13 → Book.id 매핑 함수
def get_book_id_map():
    query = "SELECT id, isbn13 FROM Book"
    df = pd.read_sql(query, con=engine)
    return dict(zip(df["isbn13"], df["id"]))

book_id_map = get_book_id_map()

# 7. 추천 생성 및 RecommendationLog에 저장
def save_to_recommendation_log(user_id, base_isbn13, method="content", base_post_id=None):
    recs = recommend(user_id, base_isbn13)

    data = []
    for r in recs:
        recommended_id = book_id_map.get(r["recommended_book_isbn13"])
        base_id = book_id_map.get(r["base_book_isbn13"])

        if recommended_id and base_id:
            data.append({
                "user_id": user_id,
                "recommended_book_id": recommended_id,
                "base_post_id": base_post_id,
                "method": method,
                "reason": f"'{books[books['book_id'] == base_isbn13].iloc[0]['title']}'와 유사한 책입니다.",
                "created_at": datetime.now()
            })

    if data:
        df = pd.DataFrame(data)
        df.to_sql("RecommendationLog", con=engine, if_exists="append", index=False)
        print(f"✅ {len(df)}건 RecommendationLog 저장 완료")
    else:
        print("❗ 저장할 데이터가 없습니다.")

# 8. 테스트 실행
if __name__ == "__main__":
    sample_user_id = 1
    sample_isbn13 = books.iloc[0]["book_id"]  # 책 하나 선택
    save_to_recommendation_log(sample_user_id, sample_isbn13)
