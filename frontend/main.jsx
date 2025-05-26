import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.jsx";
import "./styles/Global.css";

// 카카오 SDK 초기화
window.Kakao.init("9e638e4b8d4f411adbdd9ee18bc1098e");

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <App />
  </StrictMode>
);
