// ----------------------------------<게시글 관련>--------------------------------------------------------

// 게시글 수정
$(function() {
    $("#updateRegisterBtn").on("click", function() {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        let title = $("#hiddenUpTitle").val();
        let content = $("#hiddenUpCon").val();
        let brdId = $("#hiddenBrdId").val();

        // 유효성
        // 기존값을 가져와서 빈 문자열인 경우 기존 값으로 처리
        if (title === "") {
            title = $("#titleBN").text(); // 기존 제목으로 설정
        }
        if (content === "") {
            content = $("#contentBN").text(); // 기존 내용으로 설정
        }

        // 수정된 값이 없으면 수정하지 않도록 처리
        if (title === $("#titleBN").text() && content === $("#contentBN").text()) {
            alert("수정된 값이 없습니다.");
            return;
        }

        if(title == null && content == null) {
            alert("수정된 값이 없습니다.");
            return;
        }

        if(confirm("수정하시겠습니까?")) {
            $.ajax({
                type : 'post',
                url : '/board/update',
                contentType: 'application/x-www-form-urlencoded; charset=UTF-8', /* form이나 일반 button으로 해결시 (jsonX) */
                dataType: 'json',  // 응답을 map 형식으로 처리
                data : {
                    title : title,
                    content : content,
                    brdId : brdId
                },
                beforeSend : function(xhr) {
                   /*데이터를 전송하기 전에 헤더에 csrf값을 설정한다*/
                    xhr.setRequestHeader(header, token);
                },
                success : function(map) {
                    if(map.success === "OK") {
                        let Info = map.UpdateInfo;
                        console.log("제목:", Info.brdTitle);
                        console.log("내용:", Info.brdContent);
                        // input 초기화
                        $("#hiddenUpCon").val('');
                        $("#updateRCBtnWrap").val('');

                        // 필드 숨기기
                        $("#PopUpWrap").hide();
                        $("#hiddenUpTitle").hide();
                        $("#hiddenUpCon").hide();
                        $("#updateRCBtnWrap").hide();
                        $("#boardUpdateBtn").show();

                        $("#titleBN").show().text(Info.brdTitle);
                        $("#contentBN").show().text(Info.brdContent);  // 내용 업데이트

                        alert("수정되었습니다.");
                    }
                },
                error : function(xhr, status, error) {
                    const response = JSON.parse(xhr.responseText);

                    alert("서버 오류 발생 : " + response.message || response.error || JSON.stringify(response));
                    return;
                }
            });

        } else {
            return;
        }

    });

});


// 게시글 삭제
$(function() {
    $("#boardDeleteBtn").on('click', function() {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        let id = $("#hiddenBrdId").val();

        if(confirm("정말 삭제하시겠습니까?")) {
            $.ajax({
                type : 'post',
                url : '/board/delete',
                contentType: 'application/x-www-form-urlencoded; charset=UTF-8', /* form이나 일반 button으로 해결시 (jsonX) */
                dataType: 'text',  // 응답을 텍스트로 처리
                data : {
                    id : id
                },
                beforeSend : function(xhr) {
                   /*데이터를 전송하기 전에 헤더에 csrf값을 설정한다*/
                    xhr.setRequestHeader(header, token);
                },
                success : function(response) {
                    console.log(response);  // 응답 값 확인
                    if (response === "삭제 성공") {
                        if (confirm("삭제되었습니다. 게시판으로 가시겠습니까?")) {
                            window.location.href = "/board/home"; // 게시판 목록으로 이동
                        } else {
                            alert("메인 페이지로 이동합니다.");
                            window.location.href = "/main/home"; // 메인 페이지로 이동
                        }
                    } else {
                        alert("삭제에 실패했습니다: " + response);  // 실패 메시지 출력
                    }
                },
                error : function(xhr, status, error) {
                    console.log("삭제 실패:", error);
                    alert("삭제 중 오류가 발생했습니다. 다시 시도해 주세요.");
                    return;
                }
            });
        } else {
            return;
        }
    });
});

// 게시글 ⋮(Setting) 클릭
$(function(){
    // 점 3개 누르면 팝업 나오고 다시 누르면 닫힘
    $("#brdUpAndDelWrap").on('click', function() {
        if ($("#PopUpWrap").is(":visible")) {
            // 팝업 닫기 (1)
            $("#PopUpWrap").hide();

            // hidden 도 같이 사라짐
            $("#hiddenUpTitle").hide();
            $("#hiddenUpCon").hide();
            $("#hiddenUpTitle").val(''); //input 값 초기화
            $("#hiddenUpTitle").val('');

            // 수정하기
            $("#boardUpdateBtn").show();

            // 수정 완료, 수정 취소 감싸는 영역
            $("#updateRCBtnWrap").hide();

            // 원래 값 다시 나타남
            $("#titleBN").show();
            $("#contentBN").show();
        } else {
            // 팝업 열기 (1)
            $("#PopUpWrap").show();
            $("#PopUpWrap").css("display", "inline-block");
        }

       // 팝업 외부 클릭 시 팝업 닫기
        $(document).on('click', function(event) {
            // 팝업 외부를 클릭했을 때 팝업을 닫음
            if (!$(event.target).closest("#PopUpWrap").length && !$(event.target).closest("#brdUpAndDelWrap").length) {
                $("#PopUpWrap").hide();
            }
        });
    });

    // 수정하기 버튼
    $("#boardUpdateBtn").on("click", function() {
        // 수정하기 버튼 hide
        $("#boardUpdateBtn").hide();

        // 수정완료 수정취소 감싸는 영역
        $("#updateRCBtnWrap").show();

        // hidden input show
        $("#hiddenUpTitle").css("display", "grid"); // css 깨져서 얘만 grid 처리
        $("#hiddenUpCon").show();

        // 기존 title content hide
        $("#titleBN").hide();
        $("#contentBN").hide();
    });

    // 게시글 수정 취소
    $("#updateCancelBtn").on("click", function() {
        // 수정 완료 , 수정 취소 감싸는 영역
        $("#updateRCBtnWrap").hide();

        // 수정 하기
        $("#boardUpdateBtn").show();

        // 기존 title, content
        $("#titleBN").show();
        $("#contentBN").show();

        // 수정 input
        $("#hiddenUpTitle").hide();
        $("#hiddenUpCon").hide();
        $("#hiddenUpTitle").val(''); // input 초기화
        $("#hiddenUpCon").val(''); // input 초기화
    });

});

// ---------------------------------------------------------------------------------------------------


// ----------------------------------<댓글 관련>--------------------------------------------------------
// 댓글 입력
$(function() {
    // #comment 텍스트박스에서 엔터 키를 눌렀을 때
    $("#comment").on('keydown', function(e) {
        // 엔터 키 코드 (Enter는 13)
        if (e.keyCode === 13) {
            e.preventDefault();  // 기본 엔터 키 동작(줄 바꿈)을 방지
            $("#commentFormBtn").click();  // 클릭 이벤트 강제 호출
        }
    });

    $("#commentFormBtn").on('click', function() {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        /* 값 가져오기 */
        let comment = $("#comment").val();
        let boardId = $("#hiddenBrdId").val();

        /* 유효성 검사 */
        if(comment == "") {
            alert("댓글을 작성하지 않았습니다.");
            return;
        }

        if(confirm("작성 하시겠습니까?")) {
            $.ajax({
                url : '/comments/insert',
                type : 'post',
                contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
                dataType: 'json',
                data : {
                    comment : comment,
                    boardId : boardId
                },
                beforeSend : function(xhr) {
                   /*데이터를 전송하기 전에 헤더에 csrf값을 설정한다*/
                    xhr.setRequestHeader(header, token);
                },
                success : function(map) {
                    console.log("서버 응답:", map);
                    if(map.success === "OK") {
                        $("#comment").val('');
                        // 서버에서 반환한 댓글 데이터를 map에서 꺼내기
                        let comment = map.cmt; // commentDTO

                        let commentItem = `
                            <li class="commentItem">
                                <div id="commentInfo">
                                    <div class="infoLeft">
                                        <span>${comment.mbrNickname}</span>
                                        <span class="bar">|</span>
                                        <span class="comment_date">${comment.cmtDt}</span>
                                        <span class="commentDeleteBtn">삭제</span>
                                    </div>
                                </div>
                                <div id="commentContent">
                                    <p>${comment.cmtContent}</p>
                                </div>
                                <hr style="width: 54.5%; margin: 20px 0px 20px 0px;">
                            </li>
                        `;

                        // 댓글 리스트에 새 댓글 추가
                        $(".ajax_th").prepend(commentItem);

                        let commentCount = parseInt($(".content-comment-cnt").text());

                        $(".content-comment-cnt").text(commentCount + 1);
                    }
                },
                error : function(xhr, status, error) {
                    const response = JSON.parse(xhr.responseText);

                    alert("서버 오류 발생 : " + response.message || response.error || JSON.stringify(response));
                    return;
                }
            });
        }else {
            return;
        }

    });
});

// 댓글 삭제
$(function() {
    $(".commentDeleteBtn").on("click", function() {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        var commentId = $(this).data('cmt-id'); // data-cmt-id에서 댓글 ID 가져오기

        if(confirm("삭제하시겠습니까?")) {
            $.ajax({
                type : 'post',
                url : '/comments/delete',
                dataType: 'text',
                data : {
                    id : commentId
                },
                beforeSend : function(xhr) {
                   /*데이터를 전송하기 전에 헤더에 csrf값을 설정한다*/
                    xhr.setRequestHeader(header, token);
                },
                success : function(response) {
                    console.log(response);  // '삭제 성공'이라는 응답을 콘솔에 출력
                    alert('댓글이 삭제되었습니다.');
                    location.reload();  // 페이지 리로드 (또는 해당 부분만 새로고침)
                },
                error : function(xhr, status, error) {
                    console.log("삭제 실패:", error);
                    alert("삭제 중 오류가 발생했습니다. 다시 시도해 주세요.");
                    return;
                }
        });
        } else {
            return;
        }
    });
});

// ----------------------------------------------------------------------------------------------------


// ----------------------------------<좋아요 관련>-------------------------------------------------------
// 좋아요 등록 & 해제
$(function() {
    $(".like-button").on("click", function() {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        let brdId = $("#hiddenBrdId").val();

        $.ajax({
            type : 'post',
            url : '/likes/likeOnOff',
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            dataType: 'json',
            data : {
                brdId : brdId
            },
            beforeSend : function(xhr) {
                /*데이터를 전송하기 전에 헤더에 csrf값을 설정한다*/
                xhr.setRequestHeader(header, token);
            },
            success : function(response) {
                if(response.insertSuccess === "insertOK") {
                    // 성공 시 버튼 상태 변경
                    $(".like-span-btn").text('❤️');

                    // 현재 likeCount 가져와서 +1
                    let count = parseInt($("#likeCount").text());
                    let ContentCnt = parseInt($(".content-like-cnt").text());

                    $("#likeCount").text(count + 1);
                    $(".content-like-cnt").text(ContentCnt + 1);

                } else if (response.deleteSuccess === "deleteOK") {
                    // 삭제 시 버튼 상태 변경
                    $(".like-span-btn").text('♡');

                    // 현재 likeCount 가져와서 -1
                    let count = parseInt($("#likeCount").text());
                    let ContentCnt = parseInt($(".content-like-cnt").text());

                    $("#likeCount").text(count - 1);
                    $(".content-like-cnt").text(ContentCnt - 1);

                }
            },
            error : function(xhr, status, error) {
                console.log("실패:", error);
                alert("오류가 발생했습니다. 다시 시도해 주세요.");
                return;
            }
        });
    });
});

// -----------------------------------------------------------------------------------------------------


// ----------------------------------<목록으로 관련>--------------------------------------------------------
// 목록으로 가기 버튼 (그냥 back 함수)
$(function() {
    $(".back-to-list").on("click", function() {
        window.history.back();
    });
});