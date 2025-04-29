$(document).ready(function () {
    var errorMessage = $("#errorMessageContent").text().trim();
    if (errorMessage) {
        alert(errorMessage);
    }
});
$(function(){
    $("#pass2").on("input", function() {
                    let pass1 = $("#pass").val();
                    let pass2 = $(this).val();
                    let errorText = $("#passError");

                    if (pass1 !== pass2) {
                        errorText.show();
                    } else {
                        errorText.hide();
                    }
                });

    $("#insertBtn").on("click", function(event) {
        var realname = $("#realname").val(); // 사용자 명
        var name = $("#name").val(); // 사용자 아이디
        var nickname = $("#nickname").val(); // 사용자 닉네임
        var pass = $("#pass").val(); // 비밀번호
        var pass2 = $("#pass2").val(); // 비밀번호 확인
        var email = $("#email").val(); // 이메일
        var emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        var passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,20}$/;
        // 유효성 검사
        if(realname == "") {
            alert("이름을 입력하지 않았습니다.");
            return;
        }
        if(name == "") {
            alert("아이디를 입력하지 않았습니다.");
            return;
        }
        if(nickname == "") {
            lert("닉네임을 입력하지 않았습니다.");
            return;
        }
        if(pass == "") {
            alert("비밀번호를 입력하지 않았습니다.");
            return;
        }
        if(pass2 == "") {
            alert("비밀번호 확인란을 입력하지 않았습니다.");
            return;
        }
        if(email == "") {
            alert("이메일을 입력하지 않았습니다.");
            return;
        }
        if (!emailPattern.test(email)) {
                    alert("잘못된 이메일 형식입니다.");
                    event.preventDefault();
                    return;
                }
        if (!passwordRegex.test(password)) {
                            alert("비밀번호는 영문과 숫자를 포함한 8~20자로 입력해야 합니다.");
                            event.preventDefault();
                            return;
                        }

    });

});