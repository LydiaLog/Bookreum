// src/pages/Bookclub.jsx
import { useState, useMemo, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import api from "../axiosConfig";
import SearchBar from "../components/SearchBar";
import SortDropdown from "../components/SortDropdown";
import BookclubCard from "../components/BookclubCard";
import Pagination from "../components/Pagination";
import BookclubCreateModal from "../components/BookclubCreateModal";
import BookclubJoinModal from "../components/BookclubJoinModal";
import BookclubEditModal from "../components/BookclubEditModal";
import "../styles/Global.css";
import "../styles/Bookclub.css";

const TEST_USER_ID = 1; // 테스트용 유저 ID, 실제론 로그인된 유저 ID 사용

export default function Bookclub() {
  const navigate = useNavigate();

  // 상태 관리
  const [clubs, setClubs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showJoinModal, setShowJoinModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [selectedClub, setSelectedClub] = useState(null);

  const [query, setQuery] = useState("");
  const [keyword, setKeyword] = useState("");

  const [sortOption, setSortOption] = useState("all");
  const [page, setPage] = useState(1);
  const pageSize = 6;
  const [totalPages, setTotalPages] = useState(1);

  const sortOptions = [
    { value: "all", label: "전체" },
    { value: "OPEN", label: "모집중" },
    { value: "CLOSED", label: "모집마감" },
  ];

  // 백엔드에서 클럽 목록 가져오기
  useEffect(() => {
    (async () => {
      setLoading(true);
      try {
        const params = {
          page: page - 1,
          size: pageSize,
          keyword: keyword || undefined,
          status: sortOption !== "all" ? sortOption : undefined,
        };
        const { data } = await api.get("/api/clubs", { params });
        // 테스트를 위해 임시로 applied 상태 추가
        const clubsWithApplied = data.content.map((club) => ({
          ...club,
          applied: club.status === "MATCHED", // 매칭된 클럽만 applied true
        }));
        setClubs(clubsWithApplied || []);
        setTotalPages(data.totalPages || 1);
      } catch (err) {
        console.error(err);
        setError("북클럽을 불러오지 못했습니다.");
      } finally {
        setLoading(false);
      }
    })();
  }, [page, sortOption, keyword]);

  // 클라이언트 측 검색 (추가 필터)
  const { pageData } = useMemo(() => {
    const filtered = clubs.filter((c) => {
      const text =
        `${c.title} ${c.book?.title} ${c.book?.author} ${c.createdByNickname}`.toLowerCase();
      return text.includes(query.toLowerCase());
    });
    return { pageData: filtered };
  }, [clubs, query]);

  // 북클럽 생성 후 리스트 갱신
  const addClub = (newClub) => {
    setClubs((prev) => [newClub, ...prev]);
    setQuery("");
    setKeyword("");
    setPage(1);
  };

  // 참여 모달 열기
  const openJoinModal = async (club) => {
    console.log("Club data:", club);

    // 현재 사용자의 신청 상태 확인
    const userId = localStorage.getItem("userId") || "1";
    try {
      const { data: isApplied } = await api.get(
        `/api/clubs/${club.id}/applications/status`,
        {
          params: { userId },
        }
      );

      if (isApplied) {
        // 매칭된 클럽이면 채팅으로 바로 이동
        navigate(`/bookclub/${club.id}/chat`);
        return;
      }
    } catch (err) {
      console.error("Error checking application status:", err);
    }

    // 그 외의 경우 모달 표시
    setSelectedClub(club);
    setShowJoinModal(true);
  };

  // 클럽 참여 API 호출
  const joinClub = async () => {
    try {
      await api.post(`/api/clubs/${selectedClub.id}/applications`, {
        userId: TEST_USER_ID,
      });
      setShowJoinModal(false);
      navigate(`/bookclub/${selectedClub.id}`);
    } catch (err) {
      console.error(err);
      alert(err.response?.data?.message || "참여에 실패했습니다.");
    }
  };

  // 매칭 취소
  const cancelMatching = async () => {
    try {
      await api.delete(`/api/clubs/${selectedClub.id}/applications`);
      setShowJoinModal(false);
      // 목록 새로고침
      const { data } = await api.get("/api/clubs", {
        params: {
          page: page - 1,
          size: pageSize,
          keyword: keyword || undefined,
          status: sortOption !== "all" ? sortOption : undefined,
        },
      });
      setClubs(data.content || []);
    } catch (err) {
      console.error(err);
      alert(err.response?.data?.message || "매칭 취소에 실패했습니다.");
    }
  };

  // 북클럽 수정
  const handleEdit = () => {
    setShowJoinModal(false);
    setShowEditModal(true);
  };

  // 북클럽 수정 완료
  const handleEditComplete = async (editedData) => {
    try {
      const formData = new FormData();
      formData.append("title", editedData.title);
      formData.append("description", editedData.description);
      formData.append("minParticipants", editedData.minParticipants.toString());
      formData.append("maxParticipants", editedData.maxParticipants.toString());
      formData.append("applicationDeadline", editedData.applicationDeadline);
      formData.append(
        "activityDurationDays",
        editedData.activityDurationDays.toString()
      );
      formData.append("status", editedData.status);
      formData.append("bookId", selectedClub.bookId.toString());
      formData.append("userId", TEST_USER_ID.toString());

      await api.put(`/api/clubs/${selectedClub.id}`, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });

      alert("북클럽이 수정되었습니다.");
      setShowEditModal(false);

      // 목록 새로고침
      const { data } = await api.get("/api/clubs", {
        params: {
          page: page - 1,
          size: pageSize,
          keyword: keyword || undefined,
          status: sortOption !== "all" ? sortOption : undefined,
        },
      });
      setClubs(data.content || []);
    } catch (err) {
      console.error(err);
      alert(err.response?.data?.message || "수정에 실패했습니다.");
    }
  };

  // 북클럽 삭제
  const handleDelete = async () => {
    try {
      await api.delete(`/api/clubs/${selectedClub.id}`);
      alert("북클럽이 삭제되었습니다.");
      // 목록 새로고침
      const { data } = await api.get("/api/clubs", {
        params: {
          page: page - 1,
          size: pageSize,
          keyword: keyword || undefined,
          status: sortOption !== "all" ? sortOption : undefined,
        },
      });
      setClubs(data.content || []);
    } catch (err) {
      console.error(err);
      alert(err.response?.data?.message || "삭제에 실패했습니다.");
    }
  };

  // 검색 실행
  const commitSearch = () => {
    setKeyword(query.trim());
    setPage(1);
  };

  return (
    <>
      <div className="page-wrapper">
        <h2 className="page-title">북클럽</h2>
        <p className="page-subtitle">책으로 이어지는 우리들의 이야기</p>

        <div className="content-area">
          <div className="top-controls">
            <div className="search-left">
              <SearchBar
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                onSearch={commitSearch}
              />
            </div>
            <div className="controls-right">
              <SortDropdown
                value={sortOption}
                onChange={(e) => {
                  setSortOption(e.target.value);
                  setPage(1);
                }}
                options={sortOptions}
              />
              <button onClick={() => setShowCreateModal(true)}>만들기</button>
            </div>
          </div>

          {loading && <p>로딩 중…</p>}
          {error && <p className="error">{error}</p>}

          {!loading && !error && (
            <>
              <div className="bookclublist">
                {pageData.length > 0 ? (
                  pageData.map((club) => (
                    <BookclubCard
                      key={club.id}
                      bookclub={{
                        ...club,
                        book: club.book || {
                          title: club.bookTitle,
                          author: club.bookAuthor,
                          coverImageUrl: club.coverImageUrl,
                        },
                        currentParticipants: club.currentParticipants || 0,
                        maxParticipants: club.maxParticipants || 0,
                        applicationDeadline: club.applicationDeadline,
                        createdByNickname: club.createdByNickname,
                        createdByProfileImageUrl: club.createdByProfileImageUrl,
                        status: club.status || "OPEN",
                        applied: club.applied || false,
                      }}
                      onClick={() => openJoinModal(club)}
                    />
                  ))
                ) : (
                  <div className="no-results">검색 결과가 없습니다.</div>
                )}
              </div>

              <div className="pagination-wrapper">
                {totalPages > 1 && (
                  <Pagination
                    currentPage={page}
                    totalPages={totalPages}
                    onPageChange={setPage}
                  />
                )}
              </div>
            </>
          )}
        </div>
      </div>

      {/* 모임 생성 모달 */}
      {showCreateModal && (
        <BookclubCreateModal
          onClose={() => setShowCreateModal(false)}
          onCreate={addClub}
        />
      )}

      {/* 모임 참여 모달 */}
      {showJoinModal && selectedClub && (
        <BookclubJoinModal
          club={{
            ...selectedClub,
            book: selectedClub.book?.title,
            author: selectedClub.book?.author,
            nickname: selectedClub.createdByNickname,
            date: selectedClub.applicationDeadline?.split("T")[0],
            description: selectedClub.description,
            coverUrl: selectedClub.coverImageUrl,
            leader: {
              nickname: selectedClub.createdByNickname,
              profileImageUrl: selectedClub.createdByProfileImageUrl,
            },
          }}
          onClose={() => setShowJoinModal(false)}
          onJoin={joinClub}
          onCancel={cancelMatching}
          isJoinDisabled={selectedClub.status === "CLOSED"}
          applied={selectedClub.applied}
          isOwner={selectedClub.createdById === TEST_USER_ID}
          onEdit={handleEdit}
          onDelete={handleDelete}
        />
      )}

      {/* 북클럽 수정 모달 */}
      {showEditModal && selectedClub && (
        <BookclubEditModal
          club={selectedClub}
          onClose={() => setShowEditModal(false)}
          onEdit={handleEditComplete}
        />
      )}
    </>
  );
}
