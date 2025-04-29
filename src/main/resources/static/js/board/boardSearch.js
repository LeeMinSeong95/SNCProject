// 익명 검색시 닉네임 검색 삭제
$(document).on("change", "select[name=mainType]", function() {
    if ($(this).val() == '3' || $(this).val() == '4') {
        // subType select 박스에서 value가 0인 option을 숨김
        $("select[name=subType] option[value='0']").hide();
    } else {
        // value가 0인 option을 다시 보이게 함
        $("select[name=subType] option[value='0']").show();
    }
});

// 기간 검색 시
$(document).on("change", "select[name=subType]", function() {
    if ($(this).val() == '4') {
        $("#dateSearchWrap").css("display", "flex");
        $("#searchInput").css("display", "none");
        $("#searchInput").val('');
    } else {
        $("#dateSearchWrap").css("display", "none");
        $("#startDate").val('');
        $("#endDate").val('');
        $("#searchInput").css("display", "block");
    }
});


$(function() {
    // 게시판 검색 결과 비우기
    $(document).on("click", ".search-close-span", function() {
        closeSearchPopup();
    });

    // 게시판 검색 결과 비우기2
    $(document).on("click", ".close-btn-2", function() {
        closeSearchPopup();
    });

    // ESC 키 이벤트 추가
    $(document).on("keydown", function(event) {
        if (event.key === "Escape") {
            closeSearchPopup();
        }
    });

    function closeSearchPopup() {
        // 검색 결과 비우기
        $("#search-tbodys").empty();

        // 검색 팝업 숨기기
        $(".board-search-wrap").css("display", "none");

        // 입력값 초기화
        $("#searchInput").val('');
        $("#startDate").val('');
        $("#endDate").val('');

        // select 값 초기화
        $("select[name='mainType']").val('00');
        $("select[name='subType']").val('00');
    }
});

// 게시판 검색
$(function() {
    // 검색 엔터 눌렀을때
    $("#searchInput").on("keyup", function(event){
        if(event.key === "Enter" || event.keyCode == 13) {
            $("#BoardSearchBtn").click();
        }
    });

    // 검색 ajax
    $("#BoardSearchBtn").on("click", function() {
        // token (csrf)
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        // value
        let mainType = $("select[name=mainType]").val();
        let subType = $("select[name=subType]").val();
        let value = $("#searchInput").val();
        let startDate = $("#startDate").val();
        let endDate = $("#endDate").val();

        // 유효성 감사
        if(subType === '4') {
            if(startDate == "") {
                alert("검색 시작날을 입력해주세요.");
                return;
            }
            if(endDate == "") {
                alert("검색 마감날을 입력해주세요.");
                return;
            }
        } else {
            if(value == "") {
                alert("검색어를 입력해주세요.");
                return;
            }
        }

        if(mainType == '00') {
            alert("주제를 선택해주세요!");
            return;
        }

        if(subType == '00') {
            alert("조건을 선택해주세요!");
            return;
        }

        // 위 조건을 모두 충족 했을 시에 팝업 block
        $(".board-search-wrap").css("display", "block");


        console.log("mainType : " + mainType);
        console.log("subType : " + subType);
        console.log("value : " + value);
        console.log("startDate : " + startDate);
        console.log("endDate :" + endDate);

        var jsonData = {
            mainType: mainType,
            subType: subType,
            value: value,
            startDate: startDate,
            endDate: endDate
        }

        // ajax
        $.ajax({
            type : 'POST',
            url: '/board/search',
            contentType: 'application/json',
            dataType: 'JSON',
            data: JSON.stringify(jsonData),
            beforeSend: function(xhr) {
                xhr.setRequestHeader(header, token); // CSRF 토큰 설정
            },
            success : function(response) {
                if(response.success === "OK") {
                    // 검색 결과 비우기
                    $("#search-tbodys").empty();

                    // 리스트 받기
                    let searchLists = response.searchList;

                    // for문 으로 꺼내기
                    if (searchLists.length === 0) {
                        // 검색 결과가 없을 경우 메시지 추가
                        $("#search-tbodys").append(`
                            <tr>
                                <td colspan="5" style="text-align:center;">검색 결과가 없습니다.</td>
                        `);
                    } else {
                        // 검색 결과가 있을 경우, 리스트 항목 추가
                        searchLists.forEach(function(item) {
                            console.log(item);  // item 객체를 로그로 출력하여 확인

                            let newRow = `
                                <tr class="search-thArea">
                                    <td style="width:90px;padding: 15px;">${item.brdId}</td>
                                    <td style="width:90px; padding-left:5px;">${item.brdType}</td>
                                    <td style="width:125px;padding-left: 35px;">
                                        <a href="/board/detail/${item.brdId}" style="text-decoration: none;">
                                            <span style="color:#30E3CA;">${item.brdTitle}</span>
                                            <span style="color: #FFA500;">[${item.cmtCnt}]</span>
                                        </a>
                                    </td>
                                    <td style="width:120px; padding-left:83px;">${item.brdType == '3' || item.brdType == '4' ? '익명' : item.mbrNickname}</td>
                                    <td style="width:100px; padding-left:145px;">${item.brdUpdt ? item.brdUpdt : item.brdDt}</td>
                                </tr>
                            `;

                            // tbody에 추가
                            $("#search-tbodys").append(newRow);
                        });
                    }

                }
            },
            error : function(xhr, status, error) {
                    console.log("응답 상태 코드:", xhr.status);
                    console.log("응답 본문:", xhr.responseText);  // 실제 응답 본문 확인

                    if (!xhr.responseText) {
                        alert("서버에서 응답이 없습니다.");
                        return;
                    }

                    try {
                        const response = JSON.parse(xhr.responseText);
                        alert("서버 오류 발생 : " + (response.message || response.error || JSON.stringify(response)));
                    } catch (e) {
                        alert("서버 오류 발생: JSON 응답이 아닙니다.");
                        console.error("JSON 파싱 오류:", e);
                    }
            }
        });
    });
});