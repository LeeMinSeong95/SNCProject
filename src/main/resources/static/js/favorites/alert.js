// 각 주식,코인 버튼에 대한 js
$(function() {
    // ⋮ 클릭 시 해당 카드의 alert-setting-wrap만 토글
    $(".setting-span").on("click", function() {
        const card = $(this).closest(".card-content");
        card.find(".alert-setting-wrap").toggle();
    });

    // '취소' 버튼 클릭 시 해당 카드의 alert-setting-wrap만 숨김
    $(".alert-cancel").on("click", function() {
        $(this).closest(".alert-setting-wrap").hide();
    });

    // '알림 설정' 버튼 클릭 시 해당 카드의 alert-popup-wrap 표시
    $(".alert-on-off").on("click", function() {
        const card = $(this).closest(".card-content");
        card.find(".alert-popup-wrap").show();
        card.find(".alert-setting-wrap").hide();
    });

    $(".alert-cancel").on("click", function() {
        const card = $(this).closest(".card-content");
        card.find(".alert-setting-wrap").hide();
    });

    // 팝업 닫기 버튼들
    $(".alert-popup-cancel, .alert-popup-cancel2").on("click", function() {
        $(this).closest(".alert-popup-wrap").hide();
    });

    // ON 버튼 클릭 시: 배경색 바꾸고 설정영역 show
    $(".alert-on").on("click", function() {
        const card = $(this).closest(".card-content");
        $(this).addClass("active");
        card.find(".alert-off").removeClass("active");
        card.find(".alert-update").removeClass("active");
        card.find(".alert-percent-wrap").show();
        card.find(".alert-type-wrap").show();
        card.find(".alert-status").val("1"); // ON 상태로 설정
    });

    // OFF 버튼 클릭 시: 배경색 바꾸고 설정영역 hide
    $(".alert-off").on("click", function() {
        const card = $(this).closest(".card-content");
        $(this).addClass("active");
        card.find(".alert-on").removeClass("active");
        card.find(".alert-update").removeClass("active");
        card.find(".alert-percent-wrap").hide();
        card.find(".alert-type-wrap").hide();
        card.find(".alert-status").val("0"); // OFF 상태로 설정
    });

    // UPDATE 버튼 클릭 시: 상태 값 변경만
    $(".alert-update").on("click", function() {
        const card = $(this).closest(".card-content");
        $(this).addClass("active"); // active css 작동
        card.find(".alert-off").removeClass("active");
        card.find(".alert-status").val("2"); // 값 변경
    });
});

// 공용 값 함수 콜백
$(function() {
    $(".alert-popup-check").on("click", function() {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        // 부모 카드 지정
        var card = $(this).closest(".card-content");

        // INSERT, DELETE, UPDATE 판단 위해 존재
        var alertStatus = card.find(".alert-status").val();

        var tempCurrPrice = card.find('.alert-curr-price').text(); // 현재가 텍스트
        var alertCurrPrice;

        if (tempCurrPrice.includes('$')) {
            // 달러($) 가격 처리
            alertCurrPrice = tempCurrPrice.replace('📈 종가: ', '').replace('$', '').trim();
        } else if (tempCurrPrice.includes('원')) {
            // 원화(원) 가격 처리
            alertCurrPrice = tempCurrPrice.replace('📈 종가: ', '').replace('원', '').trim();
        }

        var alertId = card.find(".alert-id").val();

        var markId = card.find(".alert-mark-id").val(); // 북마크 pk
        var itemId = card.find(".alert-item-id").val(); // item
        var itemType = card.find(".alert-item-type").val(); // itemType

        // 퍼센트 설정 유효성 검사(INSERT 될때 String으로 들어감)
        var alertPercent = card.find(".alert-percent-input").val()?.trim();
        if (!alertPercent) {
            alert("목표치를 입력해주세요.");
            return;
        }

        // 퍼센트 형변환 후 0, 최솟값, 최댓값 판단
        var tempPercent = parseFloat(alertPercent);
        if (isNaN(tempPercent) || tempPercent === 0 || tempPercent < -30 || tempPercent > 30) {
            alert("퍼센트는 -30 ~ 30 사이 (0 제외) 로 입력해주세요.");
            return;
        }

        // 각 카드별(국장,미장,코인) 알림 타입 가져와서 판단할 것.
        var krType = card.find('input[name="alert_kr"]:checked').val();
        var usType = card.find('input[name="alert_us"]:checked').val();
        var coinType = card.find('input[name="alert_coin"]:checked').val();
        var alertType = krType || usType || coinType;
        if (!alertType) {
            alert("알람 방식을 선택하지 않았습니다.");
            return;
        }

        var insertData = {
            markId : markId,
            itemId: itemId,
            itemType: itemType,
            alertPercent: alertPercent,
            alertType: alertType,
            alertCurrPrice : alertCurrPrice
        };

        var deleteData = {
            alertId : alertId
        }

        var updateData = {
            alertId : alertId,
            alertPercent : alertPercent,
            alertType : alertType,
            alertCurrPrice : alertCurrPrice
        }

        // "1"이면 insert & "0"이면 delete 실행 & "2"면 update 실행
        if(alertStatus === "1") {
            // INSERT
            if (confirm("알림을 새로 설정하시겠습니까?")) {
                sendAlertAjax("/alert/insert", insertData, token, header, card);
            } else {
                return;
            }
        } else if(alertStatus === "0") {
            // DELETE
            if(confirm("알림을 해제 하시겠습니까? 해제 하시면 다시 설정 하셔야 합니다.")) {
                sendAlertAjax("/alert/delete", deleteData, token, header, card);
            } else {
                return;
            }
        } else if(alertStatus === "2") {
            // UPDATE
            if(confirm("알림 내용을 수정하시겠습니까?")) {
                sendAlertAjax("/alert/update", updateData, token, header, card);
            } else {
                return;
            }
        }
    });
});

// 알림 insert & delete & update
// 공통 AJAX 전송 함수
function sendAlertAjax(url, data, token, header, card) {
    $.ajax({
        type: 'post',
        url: url,
        contentType: 'application/json',
        dataType: 'text',
        data: JSON.stringify(data),
        beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function(response) {
            if(response === "alert on success") {
                alert("알림이 설정되었습니다!");
                card.find(".alert-popup-wrap").hide();
                return;
            } else if(response === "alert off success") {
                alert("알림이 해제되었습니다.");
                card.find(".alert-popup-wrap").hide();
                return;
            } else if(response === "alert update success") {
                alert("알림이 변경 되었습니다.");
                card.find(".alert-popup-wrap").hide();
                return;
            }
        },
        error: function(xhr, status, error) {
            console.log("alert setting error:", error);
            alert("처리 중 오류 발생");
            return;
        }
    });
}
