import os
import pandas as pd
from sentence_transformers import SentenceTransformer
import faiss
import numpy as np
import json
import re
import sys
import requests
from sqlalchemy import create_engine, text

# 1. 알라딘 API URL 설정
ALADIN_API_URL = 'https://www.aladin.co.kr/ttb/api/ItemSearch.aspx'

# 2. MySQL 연결 설정 (본인 환경에 맞게 수정)
DB_URL = "mysql+pymysql://root:7h4saf0324!@localhost:3306/bookreum?charset=utf8mb4"
engine = create_engine(DB_URL)

# API 요청 함수 (오류 확인 추가)
def fetch_books_from_aladin(keyword, max_results=50):
    try:
        params = {
            'ttbkey': 'ttbwonj011527001',
            'Query': keyword,
            'QueryType': 'Title',
            'MaxResults': max_results,
            'Output': 'js',
            'Cover': 'Big'
        }
        response = requests.get(ALADIN_API_URL, params=params)
        response_text = response.text.strip()

        # 🔍 JSON 응답이 유효한지 확인
        try:
            # ✅ 잘못된 JSON 형식 문제 해결 (JSON 응답이 여러 개일 수 있음)
            if response_text.startswith('{') and response_text.endswith('}'):
                data = json.loads(response_text)
            else:
                # JSON 파싱 에러 발생 시 수동으로 JSON 클린업
                cleaned_text = re.search(r'(\{.*\})', response_text, re.DOTALL)
                if cleaned_text:
                    data = json.loads(cleaned_text.group(1))
                else:
                    raise ValueError("유효한 JSON 형식을 찾을 수 없습니다.")
        
        except json.JSONDecodeError as e:
            return json.dumps({"error": f"JSON 파싱 오류: {str(e)}"})

        books = pd.DataFrame([{ 
            'title': item.get('title', ''),
            'description': item.get('description', ''),
        } for item in data.get('item', [])])

        if books.empty:
            return json.dumps({"error": "책 데이터를 불러오지 못했습니다."})

        return books
    except Exception as e:
        return json.dumps({"error": f"API 호출 오류: {str(e)}"})

# 사용자 입력으로 키워드 받기 (Command Line Argument)
if len(sys.argv) < 2:
    print(json.dumps({"error": "키워드가 제공되지 않았습니다."}))
    sys.exit(1)

keyword = sys.argv[1]
books = fetch_books_from_aladin(keyword, max_results=10)

# 🔍 API 호출 오류 확인
if isinstance(books, str):
    print(books)
    sys.exit(1)

# 임베딩 생성
model = SentenceTransformer('all-MiniLM-L6-v2')
descriptions = books["description"].fillna("").astype(str).tolist()
embeddings = model.encode(descriptions, normalize_embeddings=True)

# FAISS 인덱스 생성
d = embeddings.shape[1]
index = faiss.IndexFlatIP(d)
faiss.normalize_L2(embeddings)
index.add(embeddings.astype("float32"))

# 유사도 계산
query_embedding = model.encode([keyword], normalize_embeddings=True)
faiss.normalize_L2(query_embedding)
D, I = index.search(query_embedding.astype("float32"), 5)

# 추천 결과 반환
recommended_titles = [books.iloc[idx]["title"] for idx in I[0]]

# 추천 결과를 DB에 저장
try:
    with engine.connect() as connection:
        for title in recommended_titles:
            connection.execute(text("""
                INSERT INTO RecommendedBook (title, author, cover_image_url, aladin_rating)
                VALUES (:title, '', '', NULL)
            """), {"title": title})
except Exception as e:
    print(json.dumps({"error": f"DB 저장 오류: {str(e)}"}))
    sys.exit(1)

# JSON 출력 (Spring에서 읽기 쉽게)
print(json.dumps({"recommended_titles": recommended_titles}, ensure_ascii=False))
