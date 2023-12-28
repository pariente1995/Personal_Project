// 메뉴 항목 클릭 시, 선택 효과
function fnDrawUnderline(clickMenu) {
    // 이전에 선택된 메뉴 효과 초기화
    $(".nav-item").css("border-bottom-color", "white");

    // 밑줄 표현
    $(clickMenu).parent().css("border-bottom-color", "#3F63BF");
}