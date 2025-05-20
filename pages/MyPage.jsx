import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  FaUser,
  FaUserEdit,
  FaPen,
  FaStar,
  FaBookOpen,
  FaLock,
  FaArrowLeft,
} from "react-icons/fa";
import "../styles/MyPage.css";

const MyPage = () => {
  const navigate = useNavigate();
  const [userInfo, setUserInfo] = useState({
    userId: "",
    nickname: "",
    profileImageUrl: "",
  });

  useEffect(() => {
    const userId = localStorage.getItem("userId");
    const nickname = localStorage.getItem("nickname");
    const profileImageUrl = localStorage.getItem("profileImageUrl");

    if (!userId) {
      navigate("/login");
      return;
    }

    setUserInfo({ userId, nickname, profileImageUrl });
  }, [navigate]);

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
  };

  const menuList = [
    {
      label: "내가 작성한 글 ",
      icon: <FaUserEdit />,
      onClick: () => navigate("/profile"),
    },
    {
      label: "마음 누른 글",
      icon: <FaPen />,
      onClick: () => navigate("/my-posts"),
    },
    {
      label: "북클럽 관리",
      icon: <FaStar />,
      onClick: () => navigate("/liked-posts"),
    },
    {
      label: "참여중인 채팅",
      icon: <FaBookOpen />,
      onClick: () => navigate("/applied-clubs"),
    },
  ];

  return (
    <div className="mypage-container">
      <div className="mypage-card">
        {/* 뒤로가기 */}
        <button className="back-btn" onClick={() => navigate(-1)}>
          <FaArrowLeft />
        </button>

        {/* 프로필 */}
        <div className="profile-area">
          {userInfo.profileImageUrl ? (
            <img
              src={userInfo.profileImageUrl}
              alt="프로필"
              className="profile-img"
            />
          ) : (
            <div className="profile-placeholder">
              <FaUser />
            </div>
          )}
          <h2 className="profile-nickname">{userInfo.nickname}</h2>
        </div>

        {/* 세로 메뉴 리스트 */}
        <div className="menu-list">
          {menuList.map((item) => (
            <button
              key={item.label}
              className="menu-item"
              onClick={item.onClick}
            >
              <span className="menu-label">{item.label}</span>
              <span className="menu-icon">{item.icon}</span>
            </button>
          ))}
        </div>

        {/* 로그아웃 */}
        <button className="logout-btn" onClick={handleLogout}>
          로그아웃
        </button>
      </div>
    </div>
  );
};

export default MyPage;
