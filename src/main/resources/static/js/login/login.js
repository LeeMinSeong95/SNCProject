$(document).ready(function () {
        var successMessage = $("#successMessageContent").text().trim();
        if (successMessage == "회원가입이 성공적으로 완료되었습니다!") {
            alert(successMessage);
        }
    });