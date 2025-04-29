$(document).ready(function () {
    var now = new Date();
    var hour = now.getHours();
    var minute = now.getMinutes();
    var ticker = $(".ticker");
    var speed = 30; // 스크롤 속도 조절
    var tickerWidth = 0;

    // 홈 페이지 접속 시, 서버의 /coin/start 호출하여 CoinWebsocket 연결
    $.get("/coin/start")
        .done(function(response){
            console.log("WebSocket 시작 요청 성공: " );
        })
        .fail(function(){
            console.error("WebSocket 시작 요청 실패");
        });

    // 한국 주식 연결 - 09:00 ~ 15:30 사이
    if (hour >= 9 && (hour < 15 || (hour === 15 && minute <= 30))) {
        $.get("/stock/krwebsocket")
            .done(function(response) {
                console.log("한국 주식 WebSocket 시작 요청 성공");
            })
            .fail(function() {
                console.error("한국 주식 WebSocket 시작 요청 실패");
            });
    } else {
        console.log("한국 주식 연결 요청은 장중(09:00 ~ 15:30) 시간에만 호출됩니다.");
    }

    // 미국 주식 연결 - 22:30 ~ 05:00 사이
    if ((hour === 22 && minute >= 30) || (hour >= 23) || (hour < 5)) {
        $.get("/stock/usawebsocket")
            .done(function(response) {
                console.log("미국 주식 WebSocket 시작 요청 성공: ");
            })
            .fail(function() {
                console.error("미국 주식 WebSocket 시작 요청 실패");
            });
    } else {
        console.log("미국 주식 연결 요청은 장외(22:30 ~ 05:00) 시간에만 호출됩니다.");
    }

    // SockJS와 STOMP를 이용해 WebSocket 연결
    var socket = new SockJS('/ws'); // WebSocketConfig에 정의한 엔드포인트
    var stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log("WebSocket 연결됨: " + frame);

        // '/topic/coinData' 구독
        stompClient.subscribe('/topic/coinData', function (message) {
                // 서버에서 전송한 CoinData 객체를 JSON으로 파싱
                var coinData = JSON.parse(message.body);

                // 한국 주식 테이블의 첫 번째 셀(종목명)이 일치하면 업데이트
                $("#coinTable tbody tr").each(function () {
                    var $row = $(this);
                    var presetName = $row.find("td").eq(0).text().trim();
                    if (presetName === coinData.coinName) {

                        var changeAmount = coinData.chgAmt;
                        var changeRate = parseFloat(coinData.chgRate);
                        var closePrice = coinData.closePrice;

                        var changeValueCell = $row.find('td').eq(1);
                        var changeRateCell = $row.find('td').eq(2);


                        changeValueCell.removeClass("positive negative");
                        changeRateCell.removeClass("positive negative");

                        // 변동금액도 동일한 클래스 적용
                        if (changeRate > 0) {
                            changeRateCell.addClass("positive");
                            changeValueCell.addClass("positive");
                        } else {
                            changeRateCell.addClass("negative");
                            changeValueCell.addClass("negative");
                        }

                        // 값 업데이트
                        changeRateCell.text(changeRate + "%");
                        changeValueCell.text(changeAmount);
                        // 시세 업데이트
                        $row.find('td').eq(3).text(closePrice);
                    }
                });
        });

        // '/topic/KRData' 구독
        stompClient.subscribe('/topic/KRData', function (message) {
                var krStockData = JSON.parse(message.body);

                // 한국 주식 테이블의 첫 번째 셀(종목명)이 일치하면 업데이트
                $("#krStockTable tbody tr").each(function () {
                    var $row = $(this);
                    var presetName = $row.find("td").eq(0).text().trim();
                    if (presetName === krStockData.stockName) {

                        $row.find("td").eq(1).text(krStockData.formattedPrdyvrss);
                        $row.find("td").eq(2).text(krStockData.prdyctrt + "%");
                        $row.find("td").eq(3).text(krStockData.formattedStckprpr);
                    }
                });
            });

        // '/topic/USData' 구독
        stompClient.subscribe('/topic/USAData', function (message) {
            var usaStockData = JSON.parse(message.body);

            $("#usaStockTable tbody tr").each(function () {
                var $row = $(this);
                var presetName = $row.find("td").eq(0).text().trim();
                if (presetName === usaStockData.stockName) {
                    var changeAmount = parseFloat(usaStockData.prdyvrss);
                    var changeRate = parseFloat(usaStockData.prdyctrt);
                    var closePrice = "$" + usaStockData.stckprpr;

                    var changeValueCell = $row.find('td').eq(1);
                    var changeRateCell = $row.find('td').eq(2);


                    changeValueCell.removeClass("positive negative");
                    changeRateCell.removeClass("positive negative");

                   // 변동금액도 동일한 클래스 적용
                    if (changeRate > 0) {
                        changeRateCell.addClass("positive");
                        changeValueCell.addClass("positive");
                        changeValueCell.text("$" + usaStockData.prdyvrss);
                    } else {
                        changeRateCell.addClass("negative");
                        changeValueCell.addClass("negative");
                        changeValueCell.text("-" + "$"+usaStockData.prdyvrss);
                    }

                    // 값 업데이트

                    changeRateCell.text(usaStockData.prdyctrt + "%");

                    // 시세 업데이트
                    $row.find('td').eq(3).text(closePrice);
                }
            });
        });

    });

        // ✅ 티커 아이템 복제해서 자연스럽게 순환
        $(".ticker-item").each(function () {
            tickerWidth += $(this).outerWidth(true);
        });

        ticker.append(ticker.html()); // 기존 내용을 복제

        function moveTicker() {
            ticker.css("transform", "translateX(" + tickerWidth + "px)");
            ticker.animate({ transform: "translateX(-" + tickerWidth + "px)" }, speed * tickerWidth, "linear", function () {
                ticker.css("transform", "translateX(" + tickerWidth + "px)");
                moveTicker();
            });
        }

        moveTicker(); // ✅ 티커 애니메이션 시작

        // ✅ API로 초기 데이터 가져오기 (시장 열리기 전)
        function fetchInitialTickerData() {
                $("#sp500").text('Loading...');
                $("#nasdaq").text('Loading...');
                $("#bitcoin").text('Loading...');
                $("#gold").text('Loading...');

        }

        // ✅ 초기 데이터 로딩
        fetchInitialTickerData();
});