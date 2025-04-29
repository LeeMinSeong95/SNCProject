// 예시: REST API에서 받아온 데이터를 가정 (실제로는 fetch/axios 등 사용)
const candleDataFromServer = [
    {
        "candle_date_time_kst": "2025-03-10T00:00:00",
        "opening_price": 3162000,
        "high_price": 3197000,
        "low_price": 2979000,
        "trade_price": 3016000
    },
    {
        "candle_date_time_kst": "2025-02-10T00:00:00",
        "opening_price": 2800000,
        "high_price": 2850000,
        "low_price": 2750000,
        "trade_price": 2840000
    },
    // ... 필요에 따라 여러 개
];

window.addEventListener('DOMContentLoaded', () => {
    // 1. Canvas context 가져오기
    const ctx = document.getElementById('myChart').getContext('2d');

    // 2. 데이터 가공
    // chartjs-chart-financial에서 요구하는 구조: { x, o, h, l, c }
    // timeKst => x, opening_price => o, high_price => h, low_price => l, trade_price => c
    const chartData = candleDataFromServer.map(item => ({
        x: new Date(item.candle_date_time_kst),
        o: item.opening_price,
        h: item.high_price,
        l: item.low_price,
        c: item.trade_price
    }));

    // 3. 최소/최대 가격 계산 (y축 범위를 시가 ±500원 정도로 보정하려면 참고)
    //    모든 데이터 중 최솟값/최댓값을 찾아서 ±500을 설정하는 예시
    let minPrice = Math.min(...chartData.map(d => d.l));
    let maxPrice = Math.max(...chartData.map(d => d.h));
    const OFFSET = 500;
    minPrice -= OFFSET;
    maxPrice += OFFSET;

    // 4. 차트 생성
    new Chart(ctx, {
        type: 'candlestick',
        data: {
            datasets: [
                {
                    label: '월간 캔들차트',
                    data: chartData,
                    // 상승/하락 색상
                    color: {
                        up: '#01c38d',
                        down: '#f1416c',
                        unchanged: '#999'
                    }
                }
            ]
        },
        options: {
            scales: {
                x: {
                    type: 'time',        // 시간 축
                    time: {
                        unit: 'month'      // 'day', 'week', 'month' 등
                    }
                },
                y: {
                    min: minPrice,       // 최소값
                    max: maxPrice,       // 최대값
                    title: {
                        display: true,
                        text: '가격 (KRW)'
                    }
                }
            }
        }
    });
});
