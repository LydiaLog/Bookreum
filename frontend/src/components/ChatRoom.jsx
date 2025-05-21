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
    const token = localStorage.getItem("accessToken");
    const userId = localStorage.getItem("userId");

    if (!token || !userId) {
      alert("로그인이 필요합니다.");
      navigate("/login");
      return;
    }

    // 이전 메시지 로드
    const loadMessages = async () => {
      try {
        const response = await api.get(`/api/clubs/${clubId}/messages`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setMessages(response.data);
      } catch (error) {
        console.error("메시지 로드 실패:", error);
        if (error.response?.status === 401) {
          localStorage.removeItem("accessToken");
          localStorage.removeItem("userId");
          alert("세션이 만료되었습니다. 다시 로그인해주세요.");
          navigate("/login");
        }
      }
    };

    // WebSocket 연결 설정
    const socket = new SockJS("http://localhost:9090/ws-chat");
    const client = new Client({
      webSocketFactory: () => socket,
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
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
        if (frame.headers.message.includes("401")) {
          localStorage.removeItem("accessToken");
          localStorage.removeItem("userId");
          alert("세션이 만료되었습니다. 다시 로그인해주세요.");
          navigate("/login");
        }
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

    const token = localStorage.getItem("accessToken");
    const userId = localStorage.getItem("userId");

    if (!token || !userId) {
      alert("로그인이 필요합니다.");
      navigate("/login");
      return;
    }

    try {
      if (stompClient && stompClient.connected) {
        stompClient.publish({
          destination: `/app/chat.send/${clubId}`,
          headers: {
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            userId: parseInt(userId),
            content: newMessage,
          }),
        });
        setNewMessage("");
      } else {
        alert("채팅 서버에 연결되어 있지 않습니다.");
      }
    } catch (error) {
      console.error("메시지 전송 실패:", error);
      alert("메시지 전송에 실패했습니다.");
    }
  };

  return (
    <div className="flex flex-col h-[500px] bg-white rounded-lg shadow-lg">
      <div className="flex-1 overflow-y-auto p-4">
        {messages.map((message, index) => (
          <div key={index} className="mb-4">
            <div className="flex items-start">
              <div className="flex-1">
                <div className="text-sm font-semibold text-gray-700">
                  {message.user?.nickname || "알 수 없음"}
                </div>
                <div className="mt-1 text-gray-800">{message.content}</div>
                <div className="text-xs text-gray-500 mt-1">
                  {new Date(message.sentAt).toLocaleString()}
                </div>
              </div>
            </div>
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>
      <form onSubmit={sendMessage} className="border-t p-4">
        <div className="flex gap-2">
          <input
            type="text"
            value={newMessage}
            onChange={(e) => setNewMessage(e.target.value)}
            placeholder="메시지를 입력하세요..."
            className="flex-1 p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <button
            type="submit"
            className="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            전송
          </button>
        </div>
      </form>
    </div>
  );
};

export default ChatRoom;
