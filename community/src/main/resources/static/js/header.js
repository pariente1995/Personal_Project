$(function() {
    // 네이버 로그인의 경우, 로그인 시 아이디가 너무 길기에 최대 20자리까지만 출력한다.
    let loginUserName = $("#user-nav-link").text();

    if(loginUserName.length > 20) {
        $("#user-nav-link").text(loginUserName.substring(0, 20) + "...");
    }

    // '저출산 뉴스' 메뉴 클릭 시, 로딩 화면 출력
    $("#news-link").on("click", function() {
        // 메뉴 선택 효과
        fnDrawUnderline(this);
        // 로딩 화면 보이기
        $(".loading-wrapper").css("display", "flex");
        // 로딩되는 동안 스크롤 방지
        $("body").css("overflow", "hidden");
    });
});

// 메뉴 항목 클릭 시, 선택 효과
function fnDrawUnderline(clickMenu) {
    // 이전에 선택된 메뉴 효과 초기화
    $(".nav-item").css("border-bottom-color", "white");

    // 밑줄 표현
    $(clickMenu).parent().css("border-bottom-color", "#3F63BF");
}

