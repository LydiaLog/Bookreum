// 북로그 더미 데이터 모음
import image1 from "../assets/1.jpg";
import image2 from "../assets/2.jpg";

const dummyBooklogs = [
  {
    id: "log1",
    title: "낯설지만 익숙한 세계",
    book: "빛의 자격을 얻어",
    author: "이혜미",
    date: "2025. 05. 24",
    nickname: "테스트유저",
    imageUrl: image1,

    content:
      "평범한 한 여성의 삶을 따라가며 사회를 들여다본다. 읽는 동안 불편함과 공감이 교차하는 경험을 했다. 시선이 달라지니 세성도 달라보이기 시작했다. 사적인 이야기하면서 보편적인 이야기다. 생각할 거리를 많이 던져주는 책이었다.",
  },
  {
    id: "log2",
    title: "조용한 울림",
    book: "참을 수 없는 존재의 가벼움",
    author: "밀란 쿤데라",
    date: "2025. 05. 24",
    nickname: "테스트유저",
    imageUrl: image2,

    content:
      "우울과 일상, 감정의 흐름을 솔직하게 그려낸 에세이. 무겁지만 무겁지 않게, 아프지만 따뜻하게 다가온다. 누구나 한 번쯤 느껴본 감정들을 글로 정리한 느낌. 글쓴이의 담담한 문제가 오히려 큰 위로가 된다. 마음이 지친 날 가볍게 읽기 좋은 책.",
  },
  {
    id: "1",
    title: "나의 첫 북로그",
    content:
      "정말 인상 깊은 책이었어요. 마음이 따뜻해졌습니다. 연금술사의 여행을 따라가며 많은 것을 배웠습니다.",
    nickname: "나",
    date: "2025.05.05",
    book: "연금술사",
    author: "파울로 코엘료",
  },
  {
    id: "2",
    title: "다른 사람의 북로그",
    content:
      "이 책은 제 스타일은 아니었어요. 너무 암울하고 디스토피아적인 내용이라서 읽기 힘들었습니다.",
    nickname: "someone_else",
    date: "2025.05.01",
    book: "1984",
    author: "조지 오웰",
  },
];

export default dummyBooklogs;
