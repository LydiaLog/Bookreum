import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/HelpPage.css";

function HelpPage() {
  const navigate = useNavigate();
  const [page, setPage] = useState(0); // 0,1,2

  /* 페이지 전환 */
  const handleNext = () => {
    if (page < 2) setPage((p) => p + 1);
    else navigate("/"); // 마지막 → 홈
  };

  /* 각 페이지별 콘텐츠 */
  const pages = [
    {
      key: "intro",
      content: (
        <div className="help-center">
          <h1>북그러움</h1>
          <p className="subtitle">
            내향적인 사람들을 위한<br />조용한 독서 공간
          </p>

          {/* 첫 페이지 CTA → 다음 페이지 */}
          <button className="cta-btn" onClick={handleNext}>
            책장을 넘겨보세요&nbsp;➔
          </button>
        </div>
      ),
    },
    {
      key: "chapter1",
      content: (
        <div className="help-book-layout">
          {/* 왼쪽 페이지 */}
          <div className="left-page">
            <h2>1장. 북그러움을 소개합니다.</h2>
            <div className="left-page-content">
              <p>
                요즘엔 책을 읽고 무언가를 남기고 싶어질 때,<br />
                누구와 나눌 수 있을까 망설이게 됩니다.<br />
                말로 표현하는 게 익숙하지 않고,<br />
                혼자 있는 시간이 편하지만<br />
                또 너무 혼자이고 싶지는 않은 사람들이 있죠.
              </p>
              <p>
                북그러움은 그런 사람들을 위한 공간입니다.<br />
                우리는 책을 좋아하지만,<br />
                대면 모임이나 실시간 채팅,<br />
                눈치를 봐야 하는 토론에 쉽게 지치거나<br />
                부담을 느끼는 사람들이 있다는 걸 잘 압니다.
              </p>
              <p>
                그래서 북그러움은 책을 중심으로 하되<br />
                ‘느슨하게’, ‘자기 페이스대로’, ‘필요할 때만’<br />
                연결될 수 있는 방식을 고민하며 만들어졌습니다.
              </p>
              <p>
                북그러움에서는<br />
                책을 다 읽지 않아도 괜찮고,<br />
                감상을 길게 써도, 짧게 적어도,<br />
                심지어 아무 말 없이 읽기만 해도 괜찮습니다.
              </p>
            </div>
          </div>

          {/* 중앙 세로선 */}
          <div className="page__divider" />

          {/* 오른쪽 페이지 */}
          <div className="right-page">
            <p>
              독후감을 기록할 수 있는 공간은<br />
              내 블로그처럼 개인적인 공간이고,<br />
              다른 사람들의 생각을 보고 마음 하나 눌러주는 일로도<br />
              충분한 소통이 될 수 있습니다.<br />
              비슷한 책을 읽는 사람들이 소규모로 모일 수 있는<br />
              채팅방 형식의 북클럽도 있습니다.
            </p>
            <p>
              하지만 이 모든 건 어디까지나 선택사항입니다.<br />
              북그러움은 '해야 하는' 플랫폼이 아니라,<br />
              ‘있어줘서 다행인’ 플랫폼이고 싶습니다.
            </p>
            <p>
              북그러움은 말보다는 글이 편하고,<br />
              대화보다는 기록이 익숙한 사람들을 위해 만들어졌습니다.<br />
              조금 느려도 괜찮고,<br />
              지금 당장이 아니어도 괜찮고,<br />
              굳이 드러나지 않아도 괜찮은 공간.
            </p>
            <p>
              책을 좋아하는 마음 하나면,<br />
              천천히 연결될 수 있도록 준비되어 있습니다.
            </p>
          </div>
        </div>
      ),
    },
    {
      key: "chapter2",
      content: (
        <div className="help-book-layout">
          {/* 왼쪽 페이지 */}
          <div className="left-page">
            <h2>2장. 북그러움에서는 이런 걸 할 수 있어요.</h2>
            <div className="left-page-content">
              <p>
                무언가를 하지 않아도 괜찮은 공간이지만,<br />
                그래도 조심스럽게,<br />
                당신과 나눌 수 있는 몇 가지를 소개해볼게요.
              </p>
              <p>
                AI 책 추천은<br />
                당신이 쓴 글 속 책들,<br />
                마음을 눌렀던 글들,<br />
                함께했던 북클럽의 책들을 기억하고<br />
                당신을 위한 다음 책을 조심스럽게 권합니다.
              </p>
              <p>
                북클럽은<br />
                최소 2명, 최대 5명으로 이루어진<br />
                조용한 글 중심의 모임입니다.<br />
                대면도 실시간도 없지만,<br />
                기간 동안 서로의 글을 천천히 읽고,<br />
                자신의 속도대로 생각을 나눕니다.
              </p>
              <p>
                사람이 많지 않아 더 깊어지고,<br />
                실시간이 아니라서 더 편안합니다.
              </p>
           </div>
          </div>

          {/* 중앙 세로선 */}
          <div className="page__divider" />

          {/* 오른쪽 페이지 */}
          <div className="right-page">
            <p>
              북로그는<br />
              읽고 싶은 책을 저장하고,<br />
              읽었던 책을 기록하며,<br />
              다른 이들의 감상도 살펴볼 수 있는<br />
              당신만의 조용한 책장입니다.
            </p>
            <p>
              좋아요는<br />
              '마음'이라는 이름으로 바꿨습니다.<br />
              댓글을 달 여유가 없을 땐,<br />
              그저 조용히 마음만 남겨도 괜찮습니다.
            </p>
            <p>
              누군가에게 보여주기 위한 통계는 없습니다.<br />
              당신이 남긴 글이 얼마나 울림을 주었는지는<br />
              당신만 알고 있으면 충분하니까요.
            </p>
            <p>
              북그러움은<br />
              내향적인 당신의 속도에<br />
              끝까지 함께하는 독서 공간입니다.
            </p>
          </div>
        </div>
      ),
    },
  ];

  return (
    <main className="help-wrapper">
      {pages[page].content}

      {page !== 0 && (
        <button className="next-btn" onClick={handleNext}>
          {page < 2 ? "다음 페이지 ➔" : "북그러움과 함께해요 ➔"}
        </button>
      )}
    </main>
  );
}

export default HelpPage;