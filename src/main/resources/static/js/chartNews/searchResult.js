document.getElementById("searchBtn").addEventListener("click", function() {
    var keyword = document.getElementById("searchInput").value.trim();
    var option = document.getElementById("searchOptions").value;

    if (!option) {
        alert("옵션을 선택해주세요.");
        return;
    }
    if (!keyword) {
        alert("검색어를 입력해주세요.");
        return;
    }

    if (option === "news") {
        // 뉴스 선택 시: /news?keyword=검색어
        window.location.href = '/news?keyword=' + encodeURIComponent(keyword);
    } else if (option === "chart") {
        // 차트 선택 시: /candle?keyword=검색어
        window.location.href = '/candle?keyword=' + encodeURIComponent(keyword);
    }
});