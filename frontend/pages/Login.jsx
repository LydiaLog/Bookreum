import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import api from "../axiosConfig";
import "../styles/Login.css";

const Login = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  /*
  useEffect(() => {
    // URL에서 인가코드 추출
    const code = new URL(window.location.href).searchParams.get("code");
    if (code) {
      handleKakaoLogin(code);
    }
  }, []);

  const handleKakaoLogin = async (code) => {
    if (isLoading) return;
    setIsLoading(true);

    try {
      console.log("카카오 로그인 시도 - 인가코드:", code);
      const response = await api.post(
        "/api/auth/kakao",
        {
          code: code,
        },
        {
          headers: {
            "Content-Type": "application/json",
            Accept: "application/json",
          },
        }
      );

      console.log("로그인 응답:", response.data);

      if (response.data.accessToken) {
        localStorage.setItem("accessToken", response.data.accessToken);
        localStorage.setItem("userId", response.data.userId);
        localStorage.setItem("nickname", response.data.nickname);
        localStorage.setItem("profileImageUrl", response.data.profileImageUrl);
        localStorage.setItem("kakaoId", response.data.userId);
        setAuthToken(response.data.accessToken);
        navigate("/");
      } else {
        throw new Error("토큰이 응답에 포함되어 있지 않습니다.");
      }
    } catch (error) {
      console.error("로그인 실패:", error);
      if (error.response?.status === 401) {
        setError("인증에 실패했습니다. 다시 시도해주세요.");
      } else {
        setError("로그인에 실패했습니다. 다시 시도해주세요.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleKakaoLoginClick = () => {
    if (window.Kakao) {
      window.Kakao.Auth.login({
        success: async (authObj) => {
          try {
            const userInfo = await window.Kakao.API.request({
              url: "/v2/user/me",
              data: {
                property_keys: ["kakao_account.profile", "kakao_account.email"],
              },
            });

            const data = {
              kakaoId: userInfo.id.toString(),
              nickname: userInfo.kakao_account.profile.nickname,
              profileImageUrl: userInfo.kakao_account.profile.profile_image_url,
              accessToken: authObj.access_token,
            };

            const response = await api.post("/api/auth/kakao", data, {
              headers: {
                "Content-Type": "application/json",
                Accept: "application/json",
              },
            });

            if (response.data.accessToken) {
              localStorage.setItem("accessToken", response.data.accessToken);
              localStorage.setItem("userId", response.data.userId);
              localStorage.setItem("nickname", response.data.nickname);
              localStorage.setItem(
                "profileImageUrl",
                response.data.profileImageUrl
              );
              localStorage.setItem("kakaoId", response.data.userId);
              setAuthToken(response.data.accessToken);
              navigate("/");
            }
          } catch (err) {
            console.error("로그인 처리 중 오류:", err);
            if (err.response?.status === 401) {
              setError("인증에 실패했습니다. 다시 시도해주세요.");
            } else {
              setError("로그인 처리 중 오류가 발생했습니다.");
            }
          }
        },
        fail: (err) => {
          console.error("카카오 로그인 실패:", err);
          setError("카카오 로그인에 실패했습니다.");
        },
      });
    } else {
      setError("카카오 SDK를 불러오는데 실패했습니다.");
    }
  };
  */

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
          onClick={() => navigate("/")}
          disabled={isLoading}
        >
          {isLoading ? "로그인 중..." : "시작하기"}
        </button>
      </div>
    </div>
  );
};

export default Login;
