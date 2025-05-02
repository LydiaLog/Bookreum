import pandas as pd
from sqlalchemy import create_engine
from datetime import datetime

books = pd.read_csv("data/books.csv", dtype={"book_id": str})

engine = create_engine("mysql+pymysql://root:7h4saf0324!@localhost:3306/bookreum?charset=utf8mb4")

# 필요한 필드 정리
df = pd.DataFrame({
    "isbn13": books["book_id"],
    "title": books["title"],
    "author": books.get("author", ""),
    "description": books["description"],
    "created_at": [datetime.now()] * len(books)
})

# 중복 제거
df = df.drop_duplicates("isbn13")

# 이미 존재하는 책 제외
existing_isbns = pd.read_sql("SELECT isbn13 FROM Book", con=engine)["isbn13"].tolist()
df = df[~df["isbn13"].isin(existing_isbns)]

# 저장
df.to_sql("Book", con=engine, if_exists="append", index=False)
print(f"✅ {len(df)}권 Book 테이블에 새로 저장됨")
