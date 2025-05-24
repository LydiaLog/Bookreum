# import os
# import pandas as pd
# import numpy as np
# import json
# import re
# import sys
# import requests
# import html
# from sqlalchemy import create_engine, text
# from sentence_transformers import SentenceTransformer
# import faiss
# from transformers import pipeline  # ✅ 감정 분석용

# # 설정
# ALADIN_API_URL = 'https://www.aladin.co.kr/ttb/api/ItemSearch.aspx'
# DB_URL = "mysql+pymysql://root:7h4saf0324!@localhost:3306/bookreum?charset=utf8mb4"
# engine = create_engine(DB_URL)
# session = engine.connect()

# # 모델 로딩
# model = SentenceTransformer('all-MiniLM-L6-v2')
# emotion_pipeline = pipeline("sentiment-analysis", model="nlptown/bert-base-multilingual-uncased-sentiment")

# # 책 검색 함수
# def fetch_books_from_aladin(keyword, max_results=50):
#     try:
#         params = {
#             'ttbkey': 'ttbwonj011527001',
#             'Query': keyword,
#             'QueryType': 'Title',
#             'MaxResults': max_results,
#             'Output': 'js',
#             'Cover': 'Big'
#         }
#         response = requests.get(ALADIN_API_URL, params=params)
#         response_text = html.unescape(response.text.strip())
#         response_text = re.sub(r'\\(?![nrt"\\/])', '', response_text)

#         match = re.search(r'\{.*\}', response_text, re.DOTALL)
#         if not match:
#             return pd.DataFrame()

#         data = json.loads(match.group(0))
#         books = pd.DataFrame([{ 
#             'title': item.get('title', ''),
#             'description': item.get('description', ''),
#             'author': item.get('author', ''),
#             'cover_image_url': item.get('cover', '')
#         } for item in data.get('item', [])])
#         return books
#     except Exception as e:
#         print(json.dumps({"error": f"API 오류: {str(e)}"}))
#         return pd.DataFrame()

# # 추천 이유 생성
# def generate_reason(title, keyword):
#     return f"'{title}'는 '{keyword}'와 유사한 주제를 다루는 책입니다."

# # 명령어 인자 받기
# if len(sys.argv) < 2:
#     print(json.dumps({"error": "키워드가 제공되지 않았습니다."}))
#     sys.exit(1)

# keyword = sys.argv[1]
# related_keywords = [keyword]

# # 전체 도서 수집
# books = pd.DataFrame()
# for kw in related_keywords:
#     books = pd.concat([books, fetch_books_from_aladin(kw, max_results=10)])
# books.drop_duplicates(subset=['title'], inplace=True)

# # 유사도 분석용 임베딩
# descriptions = books["description"].fillna("").astype(str).tolist()
# if not descriptions:
#     print(json.dumps({"error": "추천할 책 설명이 없습니다."}))
#     sys.exit(1)

# embeddings = model.encode(descriptions, normalize_embeddings=True)
# d = embeddings.shape[1]
# index = faiss.IndexFlatIP(d)
# faiss.normalize_L2(embeddings)
# index.add(embeddings.astype("float32"))

# query_embedding = model.encode([keyword], normalize_embeddings=True)
# faiss.normalize_L2(query_embedding)
# D, I = index.search(query_embedding.astype("float32"), 8)

# # 추천 결과 생성
# recommended_books = []
# for idx in I[0]:
#     row = books.iloc[idx]
#     if keyword in row['title']:
#         continue  # 제목에 키워드 포함된 책 제외
#     reason = generate_reason(row['title'], keyword)
#     recommended_books.append({
#         "title": row['title'],
#         "author": row['author'],
#         "cover_image_url": row['cover_image_url'],
#         "recommend_reason": reason
#     })

# # 중복 제거 및 최대 8개
# unique_books = []
# seen_titles = set()
# for book in recommended_books:
#     if book['title'] not in seen_titles:
#         unique_books.append(book)
#         seen_titles.add(book['title'])
#         if len(unique_books) >= 8:
#             break

# # 🔁 부족할 경우 감정 기반 추천 추가
# if len(unique_books) < 8:
#     try:
#         sentiment_result = emotion_pipeline(keyword)[0]
#         emotion = sentiment_result['label']
#         additional_books_df = fetch_books_from_aladin(emotion, max_results=10)

#         for _, row in additional_books_df.iterrows():
#             if row['title'] in seen_titles or keyword in row['title']:
#                 continue
#             unique_books.append({
#                 "title": row['title'],
#                 "author": row['author'],
#                 "cover_image_url": row['cover_image_url'],
#                 "recommend_reason": f"'{row['title']}'는 '{keyword}'와 비슷한 감정인 '{emotion}' 분위기를 담고 있습니다."
#             })
#             seen_titles.add(row['title'])
#             if len(unique_books) >= 8:
#                 break
#     except Exception as e:
#         print(json.dumps({"error": f"감정 기반 추천 오류: {str(e)}"}))

# # DB 저장
# try:
#     for book in unique_books:
#         existing = session.execute(
#             text("SELECT id FROM RecommendedBook WHERE title = :title AND author = :author"),
#             {"title": book['title'], "author": book['author']}
#         ).fetchone()
#         if not existing:
#             session.execute(text("""
#                 INSERT INTO RecommendedBook (title, author, cover_image_url, recommend_reason, aladin_rating)
#                 VALUES (:title, :author, :cover_image_url, :recommend_reason, NULL)
#             """), book)
#     session.commit()
# except Exception as e:
#     session.rollback()
#     print(json.dumps({"error": f"DB 저장 오류: {str(e)}"}))
# finally:
#     session.close()

# # 출력
# recommended_titles = [book['title'] for book in unique_books]
# print(json.dumps({"recommended_titles": recommended_titles}, ensure_ascii=False))
import os
import pandas as pd
from sentence_transformers import SentenceTransformer
import numpy as np
import json
import re
import sys
import requests
from sqlalchemy import create_engine, text
from transformers import pipeline

# ✅ 알라딘 API URL 설정
ALADIN_API_URL = 'https://www.aladin.co.kr/ttb/api/ItemSearch.aspx'

# ✅ MySQL 연결 설정 (본인 환경에 맞게 수정)
DB_URL = "mysql+pymysql://root:7h4saf0324!@localhost:3306/bookreum?charset=utf8mb4"
engine = create_engine(DB_URL)
session = engine.connect()

# ✅ 임베딩 모델 로딩
model = SentenceTransformer('all-MiniLM-L6-v2')

# ✅ API 요청 함수 (책 검색)
def fetch_books_from_aladin(keyword, max_results=50):
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
    # print("🔍 API 응답 원본:", response_text[:500], "...")

    try:
        cleaned_text = re.search(r'(\{.*\})', response_text, re.DOTALL)
        if cleaned_text:
            json_text = cleaned_text.group(1).replace('\\', '\\\\')
            data = json.loads(json_text)
            books = pd.DataFrame([{ 
                'title': item.get('title', ''),
                'description': item.get('description', ''),
                'author': item.get('author', ''),
                'cover_image_url': item.get('cover', '')
            } for item in data.get('item', [])])
            # print(f"✅ API에서 검색된 책 수: {len(books)}")
            return books
        else:
            # print("❌ API 응답이 JSON 형식이 아님")
            return pd.DataFrame()
    except json.JSONDecodeError as e:
        # print(f"❌ JSON 파싱 오류: {str(e)}")
        # print("🔍 API 응답 전체:", response_text)
        return pd.DataFrame()

# ✅ 사용자 입력
if len(sys.argv) < 2:
    print(json.dumps({"error": "기준 책 제목이 제공되지 않았습니다."}))
    sys.exit(1)

keyword = sys.argv[1]

# ✅ 기준 책 설명 확보
base_book_df = fetch_books_from_aladin(keyword, max_results=1)
if base_book_df.empty:
    print(json.dumps({"error": "기준 책을 찾을 수 없습니다."}))
    sys.exit(1)

base_description = base_book_df.iloc[0]['description']

# ✅ 일반 키워드로 책 수집
topics = ["소설", "문학", "인생", "감정", "고전", "철학", "심리", "삶"]
books = pd.DataFrame()
for kw in topics:
    books = pd.concat([books, fetch_books_from_aladin(kw, max_results=30)])

books.drop_duplicates(subset=['title'], inplace=True)
books = books[books['description'].notnull()]

# ✅ 필터링: 제외 키워드
exclude_keywords = [
    '스타일', '유아', '유아도서', '회화', '스티커', '캐릭터', '티니핑', '잡지', '수험서', 
    '학습', '어린이', '어린이도서', '어린이책', '어린이도감', '어린이백과사전', 
    '어린이그림책', '어린이소설', '어린이동화', '어린이만화책', '어린이전집', 
    '어린이문고', '어린이문학', '어린이과학도서', '교사', '임용', '유치원',
    '초등', '중등', '고등', '대학', '대학생', '대학원생', '대학원', '전문서적',
    '전문가', '전문직', '전문서적', '전문가용', '전문직종', '전문직업', '학년', '시리즈', '세트',
    '전집', '문고', '백과사전', '사전', '교과서', '참고서', '문제집', '학습지',
    '상', '하', '별'
]
books = books[~books['title'].str.contains('|'.join(exclude_keywords), case=False, na=False)]

# ✅ 임베딩 기반 유사도 계산
if books.empty or not base_description:
    print(json.dumps({"error": "추천할 책 설명이 없습니다."}))
    sys.exit(1)

from sklearn.metrics.pairwise import cosine_similarity

descriptions = books['description'].tolist()
book_embeddings = model.encode(descriptions, normalize_embeddings=True)
query_embedding = model.encode([base_description], normalize_embeddings=True)

similarities = cosine_similarity(query_embedding, book_embeddings)[0]
top_indices = similarities.argsort()[::-1]

# ✅ 추천 결과 구성
recommended_books = []
seen_titles = set()
for idx in top_indices:
    row = books.iloc[idx]
    if keyword in row['title'] or row['title'] in seen_titles:
        continue
    reason = f"이 책은 '{keyword}'와 비슷한 감정과 경험을 전달합니다."
    recommended_books.append({
        "title": row['title'],
        "author": row['author'],
        "cover_image_url": row['cover_image_url'],
        "recommend_reason": reason
    })
    seen_titles.add(row['title'])
    if len(recommended_books) >= 8:
        break

# ✅ DB 저장 로직
try:
    for book in recommended_books:
        existing_book = session.execute(
            text("SELECT id FROM RecommendedBook WHERE title = :title AND author = :author"),
            {"title": book['title'], "author": book['author']}
        ).fetchone()
        if not existing_book:
            session.execute(
                text("""
                    INSERT INTO RecommendedBook (title, author, cover_image_url, recommend_reason, aladin_rating)
                    VALUES (:title, :author, :cover_image_url, :recommend_reason, NULL)
                """),
                book
            )
            # print(f"✅ DB 저장 성공: {book['title']}\n")
        else:
            # print(f"⚠️ DB에 이미 존재: {book['title']}\n")
            pass
    session.commit()
except Exception as e:
    session.rollback()
    print(json.dumps({"error": f"DB 저장 오류: {str(e)}"}))
finally:
    session.close()

# ✅ 최종 JSON 출력
recommended_titles = [book['title'] for book in recommended_books]

print(json.dumps({
    "recommended_books": recommended_books,
    "recommended_titles": recommended_titles
}, ensure_ascii=False))
