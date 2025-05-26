import React, { useState, useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import api from "../axiosConfig";
import { useNavigate } from "react-router-dom";

const ChatRoom = ({ clubId }) => {
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState("");
  const [stompClient, setStompClient] = useState(null);
  const messagesEndRef = useRef(null);
  const navigate = useNavigate();

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    /*
    const token = localStorage.getItem("accessToken");
    const userId = localStorage.getItem("userId");

    if (!token || !userId) {
      alert("로그인이 필요합니다.");
      navigate("/login");
      return;
    }
    */

    // 이전 메시지 로드
    const loadMessages = async () => {
      try {
        const response = await api.get(`/api/clubs/${clubId}/messages`);
        setMessages(response.data);
      } catch (error) {
        console.error("메시지 로드 실패:", error);
        /*
        if (error.response?.status === 401) {
          localStorage.removeItem("accessToken");
          localStorage.removeItem("userId");
          alert("세션이 만료되었습니다. 다시 로그인해주세요.");
          navigate("/login");
        }
        */
      }
    };

    // WebSocket 연결 설정
    const socket = new SockJS("http://localhost:9090/ws-chat");
    const client = new Client({
      webSocketFactory: () => socket,
      /*
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      */
      debug: function (str) {
        console.log(str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        console.log("WebSocket 연결됨");
        client.subscribe(`/topic/chat/${clubId}`, (message) => {
          const newMessage = JSON.parse(message.body);
          setMessages((prev) => [...prev, newMessage]);
        });
      },
      onDisconnect: () => {
        console.log("WebSocket 연결 끊김");
      },
      onStompError: (frame) => {
        console.error("STOMP 에러:", frame);
        /*
        if (frame.headers.message.includes("401")) {
          localStorage.removeItem("accessToken");
          localStorage.removeItem("userId");
          alert("세션이 만료되었습니다. 다시 로그인해주세요.");
          navigate("/login");
        }
        */
      },
    });

    client.activate();
    setStompClient(client);

    loadMessages();

    return () => {
      if (client) {
        client.deactivate();
      }
    };
  }, [clubId, navigate]);

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const sendMessage = async (e) => {
    e.preventDefault();
    if (!newMessage.trim()) return;

    /*
    const token = localStorage.getItem("accessToken");
    const userId = localStorage.getItem("userId");

    if (!token || !userId) {
      alert("로그인이 필요합니다.");
      navigate("/login");
      return;
    }
    */

    try {
      const message = {
        content: newMessage.trim(),
        userId: "1", // 임시 사용자 ID
      };

      stompClient.publish({
        destination: `/app/chat/${clubId}`,
        body: JSON.stringify(message),
      });

      setNewMessage("");
    } catch (error) {
      console.error("메시지 전송 실패:", error);
      alert("메시지 전송에 실패했습니다.");
    }
  };

  return (
    <div className="chat-room">
      <div className="messages">
        {messages.map((msg, index) => (
          <div
            key={index}
            className={`message ${
              msg.userId === "1" ? "my-message" : "other-message"
            }`}
          >
            <div className="message-content">{msg.content}</div>
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>
      <form onSubmit={sendMessage} className="message-input">
        <input
          type="text"
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          placeholder="메시지를 입력하세요..."
        />
        <button type="submit">전송</button>
      </form>
    </div>
  );
};

export default ChatRoom;
