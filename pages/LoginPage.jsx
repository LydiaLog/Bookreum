// src/pages/LoginPage.jsx
/* import React from "react";
import { useNavigate } from "react-router-dom";
import KakaoLogin from "react-kakao-login";
import '../styles/LoginPage.css';

function LoginPage() {
  const navigate = useNavigate();

  const handleSuccess = (response) => {
    console.log("Kakao Login Success:", response);
    // 로그인 성공시 서버로 사용자 정보 전송
    const { profile } = response;
    const user = {
      id: profile.id,
      nickname: profile.kakao_account.profile.nickname,
      email: profile.kakao_account.email,
      profileImage: profile.kakao_account.profile.profile_image_url,
    };

    // 로컬 저장소에 사용자 정보 저장
    localStorage.setItem("user", JSON.stringify(user));
    navigate("/"); // 로그인 후 메인 페이지로 이동
  };

  const handleFailure = (error) => {
    console.error("Kakao Login Failed:", error);
    alert("로그인에 실패했습니다.");
  };

  return (
    <div className="login-container">
      <h2>로그인 페이지</h2>
      <KakaoLogin
        token="YOUR_KAKAO_JAVASCRIPT_KEY"
        onSuccess={handleSuccess}
        onFail={handleFailure}
        onLogout={() => console.log("로그아웃")}
        style={{
          width: "300px",
          height: "45px",
          background: "#FEE500",
          border: "none",
          borderRadius: "6px",
          fontSize: "16px",
          color: "#3C1E1E",
          fontWeight: "bold",
          cursor: "pointer",
        }}
      >
        카카오로 로그인
      </KakaoLogin>
    </div>
  );
}

export default LoginPage; */