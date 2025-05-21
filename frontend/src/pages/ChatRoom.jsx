import React, { useEffect, useState } from "react";
import api from "../services/api";
import WebSocketService from "../services/WebSocketService";

const ChatRoom = () => {
  const [clubId, setClubId] = useState("");
  const [messages, setMessages] = useState([]);
  const [error, setError] = useState("");
  const [stompClient, setStompClient] = useState(null);

  const loadMessages = async () => {
    try {
      const response = await api.get(`/api/clubs/${clubId}/messages`);
      setMessages(response.data);
    } catch (error) {
      console.error("메시지 로드 실패:", error);
      // 401 에러가 아닌 경우에만 에러 메시지 표시
      if (error.response?.status !== 401) {
        setError("메시지를 불러오는데 실패했습니다.");
      }
    }
  };

  const connectWebSocket = () => {
    const newStompClient = new WebSocketService();
    setStompClient(newStompClient);
  };

  useEffect(() => {
    if (clubId) {
      loadMessages();
      connectWebSocket();
    }
    return () => {
      if (stompClient) {
        stompClient.disconnect();
      }
    };
  }, [clubId]);

  return <div>{/* Render your component content here */}</div>;
};

export default ChatRoom;
