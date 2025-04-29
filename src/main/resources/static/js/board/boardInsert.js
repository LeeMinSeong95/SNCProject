/* 글쓰기 등록 시 유효성 검사 */
$(document).ready(function() {
    $("#registerBtn").click(function() {
        /* 유효성 검사 */
        let title = $("#title").val().trim();
        let content = $("#content").val().trim();

        if (title === "") {
            alert("제목을 입력해주세요.");
            return;
        }
        if (content === "") {
            alert("내용을 입력해주세요.");
            return;
        }

        if (!confirm("게시글을 등록하시겠습니까?")) {
            return; // 사용자가 취소하면 아무것도 하지 않음
        }

    });
});



/* 글쓰기 버튼 누를때 머릿말이랑 select 값 변경 */
$(document).ready(function () {
    let referrer = document.referrer; // 이전 페이지 URL 가져오기
    let cancelBtn = $("#registerCancelBtn"); // 취소 버튼
    let boardTitle = $("#boardTitle"); // 게시판 제목 <h1> 태그
    let typeSelect = $("#type"); // 타입 <select> 태그

    // 옵션들
    const options = {
        "주식": [
            { value: "0", text: "국내" },
            { value: "1", text: "해외" }
        ],
        "코인": [
            { value: "2", text: "코인" }
        ],
        "익명": [
            { value: "3", text: "익명" },
            { value: "4", text: "QnA" }
        ]
    };

    // 기본적으로 옵션 비우기
    typeSelect.empty();

    if (referrer.includes("/board/home")) {
        cancelBtn.attr("href", "/board/home"); // 주식 게시판
        boardTitle.text("주식 게시판"); // 제목 변경
        // 주식 게시판에 맞는 옵션 추가
        options["주식"].forEach(function(option) {
            typeSelect.append(new Option(option.text, option.value));
        });
    } else if (referrer.includes("/board/coin")) {
        cancelBtn.attr("href", "/board/coin"); // 코인 게시판
        boardTitle.text("코인 게시판"); // 제목 변경
        // 코인 게시판에 맞는 옵션 추가
        options["코인"].forEach(function(option) {
            typeSelect.append(new Option(option.text, option.value));
        });
    } else if (referrer.includes("/board/anonym")) {
        cancelBtn.attr("href", "/board/anonym"); // 익명 게시판
        boardTitle.text("익명 게시판"); // 제목 변경
        // 익명 게시판에 맞는 옵션 추가
        options["익명"].forEach(function(option) {
            typeSelect.append(new Option(option.text, option.value));
        });
    } else {
        cancelBtn.attr("href", "/board/home"); // 기본값 (주식 게시판)
        boardTitle.text("주식 게시판"); // 기본 제목
        // 기본 주식 게시판 옵션 추가
        options["주식"].forEach(function(option) {
            typeSelect.append(new Option(option.text, option.value));
        });
    }
});



/* 익명 게시판에서 글쓰기 누르면 닉네임 값 익명 */
$(document).ready(function () {
    let referrer = document.referrer; // 이전 페이지 URL 가져오기
    let nickNameInput = $("#nickName"); // 닉네임 input

    if (referrer.includes("/board/anonym")) {
        nickNameInput.val("익명"); // 익명 게시판이면 '익명' 설정
    }
});


