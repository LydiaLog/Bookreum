import React from "react";
import { useNavigate } from "react-router-dom";
import puppy from "../assets/puppy.jpg";
import "../styles/MyPage.css";

const userData = {
  profileImage: puppy,
  nickname: "으예르인",
  myBooklogs: [
    { id: 1, title: "고요 속을 걷는 마음", bookTitle: "이끼숲", author: "천선란", date: "2025.05.01" },
    { id: 2, title: "빛과 어둠 사이, 나를 만나다", bookTitle: "데미안", author: "헤르만 헤세", date: "2025.04.15" },
  ],
  likedBooklogs: [
    { id: 3, title: "고요한 저항, 식물이 된 여자", bookTitle: "채식주의자", author: "한강", date: "2025.03.22" },
  ],
  joinedBookclubs: [
    { id: 1, name: "월간 판타지 독서회", bookTitle: "해리 포터와 불사조 기사단", author: "J.K. 롤링", date: "2025.06.08"},
    { id: 2, name: "철학 고전 모임", bookTitle: "소크라테스의 변명", author: "플라톤", date: "2025.05.31"},
  ],
};

function Section({ title, items, renderItem }) {
  return (
    <div className="section">
      <h3>{title}</h3>
      <ul>
        {items.map(renderItem)}
      </ul>
    </div>
  );
}

function MyPage() {
  const navigate = useNavigate();

  const {
    profileImage,
    nickname,
    myBooklogs,
    likedBooklogs,
    joinedBookclubs,
  } = userData;

  return (
    <div className="mypage-container">
      <div className="profile">
        <img src={profileImage} alt="프로필" className="profile-img" />
        <h2 className="nickname">{nickname}</h2>
      </div>

      <Section
        title="내가 쓴 북로그"
        items={myBooklogs}
        renderItem={(log) => (
          <li key={log.id}>
            {log.title}
            <span className="bookInfo">{log.bookTitle} | {log.author}</span>
            <span className="date">{log.date}</span>
          </li>
        )}
      />

      <Section
        title="마음을 남긴 북로그"
        items={likedBooklogs}
        renderItem={(log) => (
          <li key={log.id}>
            {log.title}
            <span className="bookInfo">{log.bookTitle} | {log.author}</span>
            <span className="date">{log.date}</span>
          </li>
        )}
      />

      <Section
        title="참여중인 북클럽"
        items={joinedBookclubs}
        renderItem={(club) => (
          <li key={club.id} onClick={() => navigate(`/bookclub/${club.id}`)} style={{ cursor: 'pointer' }}>
            {club.name}
            <span className="bookInfo">{club.bookTitle} | {club.author}</span>
            <span className="date">~ {club.date}</span>
          </li>
        )}
      />
    </div>
  );
}

export default MyPage;

// import React, { useEffect, useState } from "react";
// import { useNavigate } from "react-router-dom";
// import {
//   FaUser,
//   FaUserEdit,
//   FaPen,
//   FaStar,
//   FaBookOpen,
//   FaLock,
//   FaArrowLeft,
// } from "react-icons/fa";
// import "../styles/MyPage.css";

// const MyPage = () => {
//   const navigate = useNavigate();
//   const [userInfo, setUserInfo] = useState({
//     userId: "",
//     nickname: "",
//     profileImageUrl: "",
//   });

//   useEffect(() => {
//     const userId = localStorage.getItem("userId");
//     const nickname = localStorage.getItem("nickname");
//     const profileImageUrl = localStorage.getItem("profileImageUrl");

//     if (!userId) {
//       navigate("/login");
//       return;
//     }

//     setUserInfo({ userId, nickname, profileImageUrl });
//   }, [navigate]);

//   const handleLogout = () => {
//     localStorage.clear();
//     navigate("/login");
//   };

//   const menuList = [
//     {
//       label: "내가 작성한 글 ",
//       icon: <FaUserEdit />,
//       onClick: () => navigate("/profile"),
//     },
//     {
//       label: "마음 누른 글",
//       icon: <FaPen />,
//       onClick: () => navigate("/my-posts"),
//     },
//     {
//       label: "북클럽 관리",
//       icon: <FaStar />,
//       onClick: () => navigate("/liked-posts"),
//     },
//     {
//       label: "참여중인 채팅",
//       icon: <FaBookOpen />,
//       onClick: () => navigate("/applied-clubs"),
//     },
//   ];

//   return (
//     <div className="mypage-container">
//       <div className="mypage-card">
//         {/* 뒤로가기 */}
//         <button className="back-btn" onClick={() => navigate(-1)}>
//           <FaArrowLeft />
//         </button>

//         {/* 프로필 */}
//         <div className="profile-area">
//           {userInfo.profileImageUrl ? (
//             <img
//               src={userInfo.profileImageUrl}
//               alt="프로필"
//               className="profile-img"
//             />
//           ) : (
//             <div className="profile-placeholder">
//               <FaUser />
//             </div>
//           )}
//           <h2 className="profile-nickname">{userInfo.nickname}</h2>
//         </div>

//         {/* 세로 메뉴 리스트 */}
//         <div className="menu-list">
//           {menuList.map((item) => (
//             <button
//               key={item.label}
//               className="menu-item"
//               onClick={item.onClick}
//             >
//               <span className="menu-label">{item.label}</span>
//               <span className="menu-icon">{item.icon}</span>
//             </button>
//           ))}
//         </div>

//         {/* 로그아웃 */}
//         <button className="logout-btn" onClick={handleLogout}>
//           로그아웃
//         </button>
//       </div>
//     </div>
//   );
// };

// export default MyPage;