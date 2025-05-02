import requests
import pandas as pd
import time

TTB_KEY = "여기에_너의_TTBKEY_입력"  # 예: ttbwonj0XXXXXXXXX
BASE_URL = "https://www.aladin.co.kr/ttb/api/ItemSearch.aspx"

# 키워드 리스트 (원하는 분야로 확장 가능)
keywords = ["소설", "자기계발", "심리", "인문", "여행", "에세이"]

def fetch_books(query, max_results=20):
    params = {
        "ttbkey": "ttbwonj011527001",
        "Query": query,
        "QueryType": "Keyword",
        "MaxResults": max_results,
        "SearchTarget": "Book",
        "output": "js",
        "Version": "20131101"
    }
    res = requests.get(BASE_URL, params=params)
    res.raise_for_status()
    items = res.json().get("item", [])
    
    results = []
    for i in items:
        results.append({
            "book_id": i.get("isbn13"),
            "title": i.get("title"),
            "description": i.get("description", "").replace("\n", " ").strip()
        })
    return results

# 전체 키워드 순회하면서 책 정보 수집
all_books = []
for kw in keywords:
    print(f"📚 '{kw}' 검색 중...")
    books = fetch_books(kw, max_results=30)
    all_books.extend(books)
    time.sleep(1)  # API 과호출 방지

# 중복 제거 + 저장
df = pd.DataFrame(all_books).drop_duplicates("book_id")
df.to_csv("data/books.csv", index=False)
print(f"✅ 저장 완료: {len(df)}권 → data/books.csv")
