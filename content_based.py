import pandas as pd
from sentence_transformers import SentenceTransformer
import faiss
import numpy as np

# 1. 책 데이터 불러오기
books = pd.read_csv("data/books.csv")

# 2. 문장 임베딩 모델 로드
model = SentenceTransformer('all-MiniLM-L6-v2')  # 필요 시 다른 ko-BERT 계열로 교체 가능

# 3. 책 설명 임베딩 생성 (normalize_embeddings=True → 코사인 유사도 대응)
embeddings = model.encode(books['description'], normalize_embeddings=True)

# 4. FAISS 인덱스 생성 (IndexFlatIP = 코사인 유사도)
index = faiss.IndexFlatIP(embeddings.shape[1])
index.add(embeddings.astype("float32"))

# 5. 추천 함수 정의
def recommend(book_id, top_n=3):
    try:
        idx = books[books['book_id'] == str(book_id)].index[0]
    except IndexError:
        print(f"❌ book_id {book_id} 를 찾을 수 없습니다.")
        return pd.DataFrame()

    # 자기 자신 제외하고 top_n 추천
    D, I = index.search(embeddings[idx:idx+1], top_n + 1)
    recommended = books.iloc[I[0][1:]][['book_id', 'title']]
    return recommended

# 6. 테스트 실행 (book_id = 1 기준 추천 출력)
if __name__ == "__main__":
    test_book_id = books.iloc[0]["book_id"]  # 첫 번째 책 기준
    print(f"📚 '{books.iloc[0]['title']}' 기준 추천 결과:")
    print(recommend(test_book_id, top_n=3))
