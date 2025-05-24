-- 초기화
SHOW DATABASES;
DROP DATABASE IF EXISTS bookreum;
CREATE DATABASE bookreum CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE bookreum;

-- 사용자
CREATE TABLE User (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '고유 사용자 ID',
    kakao_id VARCHAR(100) UNIQUE COMMENT '카카오 로그인 식별자',
    nickname VARCHAR(100) COMMENT '사용자 닉네임',
    profile_image VARCHAR(255) COMMENT '프로필 이미지 URL'
);

-- 카테고리 (장르)FK72mt33dhhs48hf9gcqrq4fxte
CREATE TABLE Category (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '카테고리 고유 ID',
    name VARCHAR(100) UNIQUE COMMENT '카테고리 이름 (예: 에세이, 소설 등)'
);

-- 책book
CREATE TABLE Book (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '고유 책 ID',
    aladin_id BIGINT COMMENT '알라딘 API의 책 ID',
    isbn13 VARCHAR(50) UNIQUE COMMENT 'ISBN-13 코드',
    title VARCHAR(255) COMMENT '책 제목',
    original_title VARCHAR(255) COMMENT '원제',
    subtitle VARCHAR(255) COMMENT '부제',
    author VARCHAR(255) COMMENT '저자',
    publisher VARCHAR(255) COMMENT '출판사',
    pubdate DATE COMMENT '출간일',
    description TEXT COMMENT '간단한 설명',
    full_description TEXT COMMENT '상세 설명 1',
    full_description2 TEXT COMMENT '상세 설명 2',
    link VARCHAR(255) COMMENT '책 상세 링크',
    cover_image_url VARCHAR(255) COMMENT '책 표지 이미지 URL',
    category_id INT COMMENT '카테고리 ID (Category 테이블 참조)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '책 정보 등록일',
    FULLTEXT (title, author, description, full_description),
    FOREIGN KEY (category_id) REFERENCES Category(id) ON DELETE SET NULL,
    INDEX idx_book_author (author),
    INDEX idx_book_category (category_id)
);

-- 북로그 글
CREATE TABLE Post (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '고유 글 ID',
    user_id INT NOT NULL COMMENT '작성자 ID (User 테이블 참조)',
    book_id INT NOT NULL COMMENT '연결된 책 ID (Book 테이블 참조)',
    title VARCHAR(255) NOT NULL COMMENT '글 제목',
    content TEXT NOT NULL COMMENT '글 본문 내용',
    image_url VARCHAR(255) COMMENT '글에 표시할 책 이미지 (기본: Book.cover_image_url)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '작성일',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES Book(id) ON DELETE CASCADE,
    FULLTEXT (title, content),
    INDEX idx_post_user_id (user_id),
    INDEX idx_post_book_id (book_id),
    INDEX idx_post_created_at (created_at)
);

-- 공감 (좋아요)
CREATE TABLE PostHeart (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '공감 고유 ID',
    user_id INT COMMENT '공감한 사용자 ID',
    post_id INT COMMENT '공감한 게시글 ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '공감한 시간',
    FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES Post(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_post UNIQUE (user_id, post_id), -- ✅ 사용자와 게시글의 중복 공감 방지
    INDEX idx_postheart_post_id (post_id),
    INDEX idx_postheart_user_post (user_id, post_id)
);

DROP TABLE IF EXISTS PostHeart;

-- 댓글
CREATE TABLE Comment (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '댓글 고유 ID',
    post_id INT COMMENT '댓글이 달린 게시글 ID',
    user_id INT COMMENT '댓글 작성자 ID',
    content TEXT COMMENT '댓글 내용',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '댓글 작성일',
    FOREIGN KEY (post_id) REFERENCES Post(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE,
    INDEX idx_comment_post_id (post_id)
);

-- 댓글 공감
CREATE TABLE CommentHeart (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '댓글 공감 고유 ID',
    user_id INT COMMENT '공감한 사용자 ID',
    comment_id INT COMMENT '공감 대상 댓글 ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '공감한 시간',
    FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE,
    FOREIGN KEY (comment_id) REFERENCES Comment(id) ON DELETE CASCADE
);

-- 북클럽 모집글
CREATE TABLE ClubTemplate (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '북클럽 모집글 고유 ID',
    book_id INT NOT NULL COMMENT '모임 도서 ID',
    title VARCHAR(255) COMMENT '모집글 제목',
    description TEXT COMMENT '모집글 설명',
    min_participants INT DEFAULT 2 COMMENT '최소 인원',
    max_participants INT DEFAULT 5 COMMENT '최대 인원',
    application_deadline DATETIME COMMENT '모집 마감일',
    activity_duration_days INT DEFAULT 30 COMMENT '활동 기간 (일 단위)',
    created_by_user_id INT COMMENT '모집글 생성자 ID',
    status ENUM('open', 'matched', 'closed') DEFAULT 'open' COMMENT '모집 상태',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '모집글 생성일',
    FOREIGN KEY (book_id) REFERENCES Book(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by_user_id) REFERENCES User(id) ON DELETE SET NULL,
    INDEX idx_clubtemplate_status (status)
);

-- 북클럽 신청
CREATE TABLE ClubApplication (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '북클럽 신청 고유 ID',
    club_template_id INT COMMENT '모집글 ID',
    user_id INT COMMENT '신청자 ID',
    applied_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '신청 시간',
    FOREIGN KEY (club_template_id) REFERENCES ClubTemplate(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE,
    UNIQUE (club_template_id, user_id)
);

-- 채팅방
CREATE TABLE ChatRoom (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '채팅방 고유 ID',
    name VARCHAR(255) COMMENT '채팅방 이름',
    club_template_id INT COMMENT '연결된 모집글 ID',
    started_at DATETIME COMMENT '채팅 시작일',
    ends_at DATETIME COMMENT '채팅 종료일',
    is_closed BOOLEAN DEFAULT FALSE COMMENT '채팅방 종료 여부',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    FOREIGN KEY (club_template_id) REFERENCES ClubTemplate(id) ON DELETE CASCADE
);

-- 채팅방 멤버
CREATE TABLE ChatRoomMember (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '채팅방 멤버 고유 ID',
    chatroom_id INT COMMENT '채팅방 ID',
    user_id INT COMMENT '참여자 ID',
    joined_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '입장 시간',
    FOREIGN KEY (chatroom_id) REFERENCES ChatRoom(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
);

-- 채팅 메시지
CREATE TABLE ChatMessage (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '채팅 메시지 고유 ID',
    chatroom_id INT COMMENT '채팅방 ID',
    user_id INT COMMENT '보낸 사용자 ID',
    message TEXT COMMENT '메시지 본문',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '전송 시간',
    FOREIGN KEY (chatroom_id) REFERENCES ChatRoom(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE,
    INDEX idx_chat_created_at (chatroom_id, created_at)
);

-- ✅ 외래 키 제약 조건 삭제 (RecommendationLog)
ALTER TABLE RecommendationLog  
DROP FOREIGN KEY recommendationlog_ibfk_1;

ALTER TABLE RecommendationLog 
ADD CONSTRAINT recommendationlog_ibfk_1 
FOREIGN KEY (recommended_id) REFERENCES RecommendedBook(id) ON DELETE CASCADE;

-- ✅ 기존 RecommendationLog 테이블 삭제 (존재할 경우)
DROP TABLE IF EXISTS RecommendationLog;

-- ✅ 기존 RecommendedBook 테이블 삭제 (존재할 경우)
DROP TABLE IF EXISTS RecommendedBook;

-- ✅ 개선된 RecommendedBook 테이블 생성
CREATE TABLE RecommendedBook (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '추천된 책 ID',
    title VARCHAR(255) NOT NULL COMMENT '책 제목',
    author VARCHAR(255) NOT NULL COMMENT '책 저자',
    cover_image_url TEXT COMMENT '책 표지 이미지 URL',
    aladin_rating FLOAT DEFAULT NULL COMMENT '알라딘 평점',
    
    -- 중복 방지: 동일한 제목, 저자 (표지 URL은 TEXT로 키에서 제외)
    UNIQUE KEY unique_book (title, author)
);

-- ✅ 기존 RecommendationLog 테이블 삭제 (존재할 경우)
DROP TABLE IF EXISTS RecommendationLog;

-- ✅ RecommendationLog 테이블 다시 생성
CREATE TABLE RecommendationLog (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '추천 로그 고유 ID',
    user_id INT NOT NULL COMMENT '추천을 받은 사용자 ID',
    recommended_id INT NOT NULL COMMENT '추천된 책 ID (RecommendedBook 테이블의 id 참조)',
    base_book_id INT COMMENT '기준이 된 책 ID (Book 테이블의 id 참조, NULL 가능)',
    base_post_id INT COMMENT '기반이 된 글 ID (NULL 가능)',
    method ENUM('author', 'genre', 'combined', 'popular', 'content', 'AI', 'manual', 'random') DEFAULT 'combined' COMMENT '추천 방식',
    score FLOAT DEFAULT NULL COMMENT '추천 점수 (AI 추천 점수)',
    model_version VARCHAR(50) DEFAULT NULL COMMENT 'AI 모델 버전',
    reason TEXT COMMENT '추천 이유 (예: 같은 저자, 장르 등)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '추천 일시',

    -- 외래 키 설정
    FOREIGN KEY (recommended_id) REFERENCES RecommendedBook(id) ON DELETE CASCADE,
    FOREIGN KEY (base_book_id) REFERENCES Book(id) ON DELETE SET NULL,

    -- 인덱스 설정
    INDEX idx_recommendation_user (user_id),
    INDEX idx_recommendation_method (method)
);


-- 5️⃣ 새로 추가된 필드 확인
DESCRIBE RecommendationLog;

-- 사용자 예제 (테스트 사용자 생성)
INSERT INTO User (id, kakao_id, nickname, profile_image) 
VALUES (1, 'test-kakao', '테스트유저', 'https://example.com/default-profile.jpg');

-- 외래 키 제약 조건 해제 (임시)
SET FOREIGN_KEY_CHECKS = 0;

-- RecommendationLog 테이블 삭제
DROP TABLE IF EXISTS RecommendationLog;
DROP TABLE IF EXISTS Post;

-- Book 테이블에 category 열 추가
ALTER TABLE Book 
ADD COLUMN category VARCHAR(255) DEFAULT 'Uncategorized' AFTER description;

-- 외래 키 제약 조건 다시 활성화
SET FOREIGN_KEY_CHECKS = 1;

-- 북로그 더미 데이터 테이블 생성
CREATE TABLE booklogs (
    id VARCHAR(10) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    book VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    nickname VARCHAR(255) NOT NULL,
    content TEXT
);

-- 더미 데이터 삽입
INSERT INTO booklogs (id, title, book, author, date, nickname, content) VALUES
('1', '그래서 빛의 자격은 얻으셨는지~?', '빛의 자격을 얻어', '이혜미', '2025-04-03', '안녕쌉싸리와요', '이혜미 시인의 『빛의 자격을 얻어』는...'),
('2', '저는 이 책과 함께 갈 수 없습니다.', '참을 수 없는 존재의 가벼움', '밀란 쿤데라', '2025-04-01', '시골쥐서울상경', '참을 수 없는 존재의 가벼움을 읽었다. 사실 읽었다고 보기도 어렵다...'),
('3', '나의 첫 북로그', '연금술사', '파울로 코엘료', '2025-05-05', '나', '정말 인상 깊은 책이었어요. 마음이 따뜻해졌습니다.'),
('4', '다른 사람의 북로그', '1984', '조지 오웰', '2025-05-01', 'someone_else', '이 책은 제 스타일은 아니었어요.');

SELECT * FROM booklogs;
SELECT * FROM booklogs WHERE id = 1;

INSERT INTO book (title, author, publisher, pubdate, description, created_at) VALUES
('빛의 자격을 얻어', '이혜미', '알 수 없음', '2025-04-03', '샘플 설명', NOW()),
('참을 수 없는 존재의 가벼움', '밀란 쿤데라', '알 수 없음', '2025-04-01', '샘플 설명', NOW()),
('연금술사', '파울로 코엘료', '알 수 없음', '2025-05-05', '샘플 설명', NOW()),
('1984', '조지 오웰', '알 수 없음', '2025-05-01', '샘플 설명', NOW());

SELECT * FROM Book;
SELECT * FROM Post;
SELECT * FROM PostHeart;
SELECT * FROM PostHeart WHERE post_id = 1 AND user_id = 1;
SELECT * FROM Comment;
SELECT * FROM RecommendationLog;
SELECT * FROM RecommendedBook;
SELECT id, title 
FROM Book 
WHERE LOWER(title) LIKE '%채식주의자%';
DROP TABLE IF EXISTS Book;

ALTER TABLE RecommendedBook 
ADD COLUMN embedding TEXT COMMENT '책 임베딩 벡터';
ALTER TABLE RecommendedBook 
ADD COLUMN recommend_reason TEXT COMMENT '추천 이유';




INSERT INTO book (id, title, author, cover_image_url) 
VALUES (25843736, '모순', '양귀자', 'https://example.com/mosun_cover.jpg');

SHOW TABLES;
DESCRIBE book;
DESCRIBE RecommendationLog;
DESCRIBE RecommendedBook;

SHOW VARIABLES LIKE 'transaction_isolation';
SET GLOBAL TRANSACTION ISOLATION LEVEL READ COMMITTED;
SET GLOBAL innodb_lock_wait_timeout = 100;

