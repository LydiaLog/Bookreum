#!/usr/bin/env python3
# recommender/content_based.py

import sys
import os
import pandas as pd
from sentence_transformers import SentenceTransformer
import faiss
import numpy as np

def load_data():
    base = os.path.dirname(os.path.abspath(__file__))
    data_path = os.path.normpath(os.path.join(base, "../data/books.csv"))
    if not os.path.exists(data_path):
        print(f"Error: books.csv not found at {data_path}", file=sys.stderr)
        sys.exit(1)

    df = pd.read_csv(data_path, dtype=str)
    # book_id 또는 isbn13 컬럼을 내부 book_id로 통일
    if 'book_id' in df.columns:
        pass
    elif 'isbn13' in df.columns:
        df = df.rename(columns={'isbn13': 'book_id'})
    else:
        print("Error: 'book_id' 또는 'isbn13' 컬럼이 없습니다.", file=sys.stderr)
        sys.exit(1)

    if 'description' not in df.columns:
        print("Error: 'description' 컬럼이 없습니다.", file=sys.stderr)
        sys.exit(1)

    return df

def build_index(descriptions):
    model = SentenceTransformer('all-MiniLM-L6-v2')
    embeddings = model.encode(
        descriptions.tolist(),
        normalize_embeddings=True,
        convert_to_numpy=True
    )
    dim = embeddings.shape[1]
    index = faiss.IndexFlatIP(dim)
    index.add(embeddings.astype('float32'))
    return index, embeddings

def recommend(book_id, books, index, embeddings, top_n=5):
    mask = books['book_id'] == str(book_id)
    if not mask.any():
        print(f"❌ book_id {book_id}를 찾을 수 없습니다.", file=sys.stderr)
        # 빈 DataFrame에 컬럼만 지정
        return pd.DataFrame(columns=['book_id', 'title'])

    target_idx = mask.idxmax()
    D, I = index.search(embeddings[target_idx:target_idx+1], top_n + 1)
    rec_idxs = I[0][1: top_n+1]
    return books.iloc[rec_idxs][['book_id', 'title']]

def main():
    if len(sys.argv) < 2:
        print("Usage: python content_based.py <book_id> [top_n]", file=sys.stderr)
        sys.exit(1)
    book_id = sys.argv[1]
    try:
        top_n = int(sys.argv[2]) if len(sys.argv) > 2 else 5
    except ValueError:
        print("❌ top_n 은 정수여야 합니다.", file=sys.stderr)
        sys.exit(1)

    books = load_data()
    index, embeddings = build_index(books['description'])
    rec_df = recommend(book_id, books, index, embeddings, top_n=top_n)

    # 빈 DataFrame이어도 book_id 컬럼이 있으므로 KeyError 없음
    for bid in rec_df['book_id'].tolist():
        print(bid)

if __name__ == "__main__":
    main()
