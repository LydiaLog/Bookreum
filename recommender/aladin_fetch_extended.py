import requests
import pandas as pd
import time
from datetime import datetime

TTB_KEY = "ttbwonj011527001" # 알라딘 API 키

BASE_URL = "https://www.aladin.co.kr/ttb/api/ItemSearch.aspx"

# 📚 다양한 분야의 키워드 30개 이상
keywords = [
    "소설", "추리", "로맨스", "에세이", "심리", "자기계발", "마케팅", "경영", "투자",
    "과학", "AI", "빅데이터", "프로그래밍", "인문학", "철학", "역사", "정치", "여행",
    "요리", "환경", "건강", "예술", "건축", "디자인", "영성", "육아", "자연", "에코", "성장", "생존"
]

def fetch_books(query, max_results=30):
    params = {
        "ttbkey": "ttbwonj011527001",
        "Query": query,
        "QueryType": "Keyword",
        "MaxResults": max_results,
        "SearchTarget": "Book",
        "output": "js",
        "Version": "20131101"
    }
    try:
        res = requests.get(BASE_URL, params=params, timeout=5)
        res.raise_for_status()
        items = res.json().get("item", [])
    except Exception as e:
        print(f"❌ {query} 오류: {e}")
        return []

    results = []
    for i in items:
        results.append({
            "book_id": i.get("isbn13"),
            "title": i.get("title"),
            "author": i.get("author"),
            "pub_date": i.get("pubDate"),
            "description": i.get("description", "").replace("\n", " ").strip(),
            "category": i.get("categoryName"),
            "cover": i.get("cover")
        })
    return results

# 📊 데이터 수집
all_books = []
for kw in keywords:
    print(f"🔍 '{kw}' 수집 중...")
    books = fetch_books(kw)
    all_books.extend(books)
    time.sleep(1)  # API 호출 제한 방지용 딜레이

# 🧹 중복 제거
df = pd.DataFrame(all_books).drop_duplicates("book_id")

# 🗃 저장
timestamp = datetime.now().strftime("%Y%m%d_%H%M")
output_path = f"data/books_{timestamp}.csv"
df.to_csv(output_path, index=False, encoding="utf-8-sig")

print(f"\n✅ 수집 완료: {len(df)}권 저장 → {output_path}")
