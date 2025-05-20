import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import "../styles/Login.css";

const Login = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [error, setError] = useState(null);

  useEffect(() => {
    // 이전 페이지에서 전달된 에러 메시지가 있다면 표시
    if (location.state?.error) {
      setError(location.state.error);
    }
  }, [location]);

  const handleKakaoLogin = () => {
    if (!window.Kakao.isInitialized()) {
      console.error("카카오 SDK가 초기화되지 않았습니다.");
      setError("카카오 로그인을 초기화하는데 실패했습니다.");
      return;
    }

    window.Kakao.Auth.login({
      success: () => {
        // 1) 콜백 페이지로 이동
        navigate("/oauth/kakao/callback", { replace: true });

        // 2) 콜백에서 토큰을 저장한 뒤, 새로고침 한 번
        setTimeout(() => window.location.reload(), 0);
      },
      fail: (err) => {
        console.error("카카오 로그인 실패:", err);
        setError("카카오 로그인에 실패했습니다.");
      },
    });
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <h1>북그러움</h1>
        <p className="subtitle">나만의 독서 기록을 시작하세요</p>

        {error && (
          <div className="error-message">
            <p>{error}</p>
            <button className="retry-button" onClick={() => setError(null)}>
              다시 시도
            </button>
          </div>
        )}

        <button
          className="kakao-login-btn"
          onClick={handleKakaoLogin}
          disabled={!!error}
        >
          카카오로 시작하기
        </button>
      </div>
    </div>
  );
};

export default Login;