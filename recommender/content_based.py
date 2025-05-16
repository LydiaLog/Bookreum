import sys
import os
import pandas as pd
from sentence_transformers import SentenceTransformer
import faiss
import numpy as np

def load_data():
    # 스크립트 폴더 기준으로 data/books.csv 로드
    base = os.path.dirname(os.path.abspath(__file__))
    data_path = os.path.normpath(os.path.join(base, "../data/books.csv"))
    if not os.path.exists(data_path):
        print(f"Error: books.csv not found at {data_path}", file=sys.stderr)
        sys.exit(1)
    return pd.read_csv(data_path, dtype=str)

def build_index(descriptions):
    # 임베딩 모델 로드
    model = SentenceTransformer('all-MiniLM-L6-v2')
    # 코사인 유사도 대응(normalize_embeddings=True)
    embeddings = model.encode(
        descriptions.tolist(),
        normalize_embeddings=True,
        convert_to_numpy=True
    )
    # FAISS 인덱스 생성 (Inner-Product 사용하면 벡터가 정규화된 코사인 유사도와 동일)
    dim = embeddings.shape[1]
    idx = faiss.IndexFlatIP(dim)
    idx.add(embeddings.astype('float32'))
    return idx, embeddings

def recommend(book_id, books, index, embeddings, top_n=5):
    # book_id 컬럼이 문자열로 되어 있다고 가정
    mask = books['book_id'] == str(book_id)
    if not mask.any():
        print(f"❌ book_id {book_id}를 찾을 수 없습니다.", file=sys.stderr)
        return pd.DataFrame()
    target_idx = mask.idxmax()

    # 자기 자신 포함 top_n+1 검색 → 첫 번째(자기 자신) 제외
    D, I = index.search(embeddings[target_idx:target_idx+1], top_n + 1)
    rec_idxs = I[0][1: top_n+1]
    return books.iloc[rec_idxs][['book_id', 'title']]

def main():
    # 0) 인자 검사
    if len(sys.argv) < 2:
        print("Usage: python content_based.py <book_id> [top_n]", file=sys.stderr)
        sys.exit(1)
    book_id = sys.argv[1]
    try:
        top_n = int(sys.argv[2]) if len(sys.argv) > 2 else 5
    except ValueError:
        print("❌ top_n 은 정수여야 합니다.", file=sys.stderr)
        sys.exit(1)

    # 1) 데이터 로드 & 인덱스 생성 (한 번만)
    books = load_data()
    index, embeddings = build_index(books['description'])

    # 2) 추천 실행
    rec_df = recommend(book_id, books, index, embeddings, top_n=top_n)

    # 3) 추천된 book_id 한 줄씩 출력
    for bid in rec_df['book_id'].tolist():
        print(bid)

if __name__ == "__main__":
    main()
