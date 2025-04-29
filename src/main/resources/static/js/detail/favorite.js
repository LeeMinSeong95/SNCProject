$(document).ready(function () {
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    $.ajaxSetup({
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        }
    });
});
$(document).on('click', '.bookmark-btn', function () {
        const $btn = $(this);
        const itemId = $btn.closest('td').siblings('.stock-item').data('item-id');
        const $table = $btn.closest('table');
        const chartType = $table.data('chart-type');
        const isBookmarked = $btn.find('span').data('favorite');

        const newIcon = isBookmarked ? '☆' : '⭐';
        const newBookmarkedState = !isBookmarked;

        const confirmMessage = isBookmarked
                ? "즐겨찾기에서 삭제하시겠습니까?"
                : "즐겨찾기에 추가하시겠습니까?";


        if (!confirm(confirmMessage)) {
            return;
        }

        $.ajax({
            url: '/itemdetail/favoriteChng',
            type: 'POST',
            data: {
                itemId: itemId,
                chartType: chartType,
                favoriteYN: newBookmarkedState
            },
            success: function () {
                $btn.find('span')
                    .text(newIcon)
                    .data('favorite', newBookmarkedState);
            },
            error: function (err) {
                if (err.status === 401) {
                    alert("로그인이 필요합니다.");
                    window.location.href = err.responseText;
                } else {
                    console.error("즐겨찾기 실패:", err);
                }
            }
        });
    });
