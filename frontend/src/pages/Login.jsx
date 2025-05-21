import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import "../styles/Login.css";

const Login = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    // 이전 페이지에서 전달된 에러 메시지가 있다면 표시
    if (location.state?.error) {
      setError(location.state.error);
    }
  }, [location]);

  const handleKakaoLogin = async () => {
    if (isLoading) return;
    setIsLoading(true);

    try {
      if (!window.Kakao.isInitialized()) {
        console.error("Kakao SDK is not initialized");
        return;
      }

      const res = await window.Kakao.Auth.login({
        success: async (authObj) => {
          try {
            // 사용자 정보 요청
            const userInfo = await window.Kakao.API.request({
              url: "/v2/user/me",
              data: {
                property_keys: ["kakao_account.profile", "kakao_account.email"],
              },
            });

            console.log("Kakao user info:", userInfo);

            // 백엔드로 전송할 데이터 구성
            const data = {
              kakaoId: userInfo.id.toString(),
              nickname: userInfo.kakao_account.profile.nickname,
              profileImageUrl: userInfo.kakao_account.profile.profile_image_url,
              accessToken: authObj.access_token,
            };

            console.log("Sending data to backend:", data);

            const response = await fetch(
              "http://localhost:9090/api/auth/kakao",
              {
                method: "POST",
                headers: {
                  "Content-Type": "application/json",
                  Authorization: `Bearer ${authObj.access_token}`,
                },
                body: JSON.stringify(data),
              }
            );

            // 응답 상태 코드 로깅
            console.log("Response status:", response.status);

            // 응답 헤더 로깅
            console.log(
              "Response headers:",
              Object.fromEntries(response.headers.entries())
            );

            let responseData;
            const contentType = response.headers.get("content-type");
            if (contentType && contentType.includes("application/json")) {
              responseData = await response.json();
            } else {
              const text = await response.text();
              console.log("Non-JSON response:", text);
              throw new Error("서버 응답이 올바르지 않습니다.");
            }

            if (!response.ok) {
              throw new Error(
                responseData.message || `로그인 실패 (${response.status})`
              );
            }

            console.log("Login response:", responseData);

            if (responseData.accessToken && responseData.refreshToken) {
              localStorage.setItem("accessToken", responseData.accessToken);
              localStorage.setItem("refreshToken", responseData.refreshToken);
              localStorage.setItem("userId", responseData.userId);
              localStorage.setItem("nickname", responseData.nickname);
              localStorage.setItem(
                "profileImageUrl",
                responseData.profileImageUrl
              );
              navigate("/");
            } else {
              throw new Error("토큰이 발급되지 않았습니다.");
            }
          } catch (error) {
            console.error("Login error:", error);
            alert(error.message || "로그인 중 오류가 발생했습니다.");
          } finally {
            setIsLoading(false);
          }
        },
        fail: (err) => {
          console.error("Kakao login failed:", err);
          alert("카카오 로그인에 실패했습니다.");
          setIsLoading(false);
        },
      });
    } catch (error) {
      console.error("Kakao login error:", error);
      alert("카카오 로그인 중 오류가 발생했습니다.");
      setIsLoading(false);
    }
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
