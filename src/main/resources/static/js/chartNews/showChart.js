// app.js
window.addEventListener('DOMContentLoaded', () => {
    const ctx = document.getElementById('myChart').getContext('2d');

    // 예시 데이터 (실제로는 서버에서 받아온 JSON 변환)
    const exampleData = [
        { x: new Date("2025-03-01"), o: 1450, h: 1500, l: 1400, c: 1480 },
        { x: new Date("2025-04-01"), o: 1480, h: 1550, l: 1470, c: 1500 },
        { x: new Date("2025-05-01"), o: 1500, h: 1600, l: 1490, c: 1580 },
    ];

    new Chart(ctx, {
        type: 'candlestick',
        data: {
            datasets: [
                {
                    label: '월간 캔들차트',
                    data: exampleData,
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
                    type: 'time',
                    time: {
                        unit: 'month'
                    }
                },
                y: {
                    title: {
                        display: true,
                        text: '가격'
                    }
                }
            }
        }
    });
});
