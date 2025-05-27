// import React, { useEffect } from "react";
// import { useNavigate } from "react-router-dom";
// import axios from "axios";

// const API_BASE_URL = "http://10.50.253.27:9090";

// const KakaoCallback = () => {
//   const navigate = useNavigate();

//   useEffect(() => {
//     const handleKakaoCallback = async () => {
//       try {
//         // SDK로 카카오 로그인 처리
//         window.Kakao.Auth.login({
//           success: async (authObj) => {
//             try {
//               // 사용자 정보 요청
//               const userResponse = await window.Kakao.API.request({
//                 url: "/v2/user/me",
//                 data: {
//                   property_keys: [
//                     "kakao_account.profile",
//                     "kakao_account.email",
//                   ],
//                 },
//               });

//               if (!userResponse || !userResponse.id) {
//                 throw new Error("사용자 정보를 받지 못했습니다.");
//               }

//               const kakaoUser = userResponse;
//               console.log("카카오 사용자 정보 받음:", kakaoUser);

//               // 백엔드로 카카오 사용자 정보 전송
//               const loginResponse = await axios.post(
//                 `${API_BASE_URL}/api/auth/kakao`,
//                 {
//                   accessToken: authObj.access_token,
//                   kakaoId: kakaoUser.id,
//                   nickname: kakaoUser.kakao_account?.profile?.nickname,
//                   profileImageUrl:
//                     kakaoUser.kakao_account?.profile?.profile_image_url,
//                 },
//                 {
//                   headers: {
//                     "Content-Type": "application/json",
//                     Accept: "application/json",
//                   },
//                   withCredentials: true,
//                 }
//               );

//               console.log("백엔드 응답:", loginResponse.data);

//               if (loginResponse.data.accessToken) {
//                 localStorage.setItem(
//                   "accessToken",
//                   loginResponse.data.accessToken
//                 );
//                 localStorage.setItem(
//                   "refreshToken",
//                   loginResponse.data.refreshToken
//                 );

//                 if (loginResponse.data.userId) {
//                   localStorage.setItem("userId", loginResponse.data.userId);
//                   localStorage.setItem("nickname", loginResponse.data.nickname);
//                   localStorage.setItem(
//                     "profileImageUrl",
//                     loginResponse.data.profileImageUrl
//                   );
//                 }

//                 navigate("/");
//               } else {
//                 throw new Error("JWT 토큰을 받지 못했습니다.");
//               }
//             } catch (error) {
//               console.error("카카오 사용자 정보 처리 중 오류:", error);
//               handleError(error);
//             }
//           },
//           fail: (err) => {
//             console.error("카카오 로그인 실패:", err);
//             navigate("/login", {
//               state: { error: "카카오 로그인에 실패했습니다." },
//             });
//           },
//         });
//       } catch (error) {
//         console.error("카카오 로그인 처리 중 오류:", error);
//         handleError(error);
//       }
//     };

//     const handleError = (error) => {
//       let errorMessage = "로그인 처리 중 오류가 발생했습니다.";

//       if (error.response) {
//         switch (error.response.status) {
//           case 400:
//             errorMessage =
//               "잘못된 요청입니다. 카카오 로그인 설정을 확인해주세요.";
//             break;
//           case 401:
//             errorMessage =
//               "인증에 실패했습니다. 카카오 로그인 설정을 확인해주세요.";
//             break;
//           case 404:
//             errorMessage =
//               "서버 엔드포인트를 찾을 수 없습니다. 백엔드 서버가 실행 중인지 확인해주세요.";
//             break;
//           case 500:
//             errorMessage =
//               "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
//             break;
//           default:
//             errorMessage = error.response.data?.message || errorMessage;
//         }
//       } else if (error.request) {
//         errorMessage = `백엔드 서버(${API_BASE_URL})에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요.`;
//       }

//       navigate("/login", {
//         state: { error: errorMessage },
//       });
//     };

//     handleKakaoCallback();
//   }, [navigate]);

//   return (
//     <div
//       style={{
//         display: "flex",
//         justifyContent: "center",
//         alignItems: "center",
//         height: "100vh",
//         flexDirection: "column",
//         gap: "1rem",
//       }}
//     >
//       <p>카카오 로그인 처리 중...</p>
//       <div className="loading-spinner"></div>
//     </div>
//   );
// };

// export default KakaoCallback;