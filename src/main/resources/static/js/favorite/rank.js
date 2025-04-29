const tabs = document.querySelectorAll('.top5-tab');
let currentType = 'volume'; // 기본 시작값

const bodies = {
    volume: document.getElementById('table-volume'),
    price: document.getElementById('table-price'),
    increase: document.getElementById('table-increase')
};

/* ----------------------------
    공통 숫자 포맷 함수
----------------------------- */
function formatNumber(num) {
    return Number(num).toLocaleString('ko-KR');
}

/* ----------------------------
    한국 주식 탭 기능
----------------------------- */
const krTabs = document.querySelectorAll('.kr-tab');
let krCurrentType = 'volume';
const krBodies = {
    volume: document.getElementById('table-volume'),
    price: document.getElementById('table-price'),
    increase: document.getElementById('table-increase')
};

krTabs.forEach(tab => {
    tab.addEventListener('click', () => {
        document.querySelector('.kr-tab.active')?.classList.remove('active');
        tab.classList.add('active');

        krCurrentType = tab.dataset.target;
        Object.keys(krBodies).forEach(key => {
            krBodies[key].style.display = (key === krCurrentType) ? '' : 'none';
        });

        fetchTop5(krCurrentType);
    });
});

function fetchTop5(type) {
    fetch(`/api/top5/${type}`)
        .then(response => response.json())
        .then(data => {
            const tbody = document.getElementById(`table-${type}`);
            tbody.innerHTML = "";

            if (data.length === 0) {
                tbody.innerHTML = `<tr><td colspan="5">📭 아직 데이터가 없습니다.</td></tr>`;
                return;
            }

            data.forEach((stock, index) => {
                const isNegative = stock.vol_inrt.includes('-');
                const rateClass = isNegative ? 'negative' : 'positive';

                const row = `
                    <tr>
                        <td>${index + 1}</td>
                        <td>${stock.hts_kor_isnm}</td>
                        <td>${formatNumber(stock.stck_prpr)}</td>
                        <td>${formatNumber(stock.prdy_vol)}</td>
                        <td><span class="rate-cell ${rateClass}">${stock.vol_inrt}</span></td>
                    </tr>
                `;
                tbody.insertAdjacentHTML('beforeend', row);
            });

            document.getElementById('top5-updated').textContent =
                new Date().toLocaleTimeString('ko-KR', {hour: '2-digit', minute: '2-digit'});
        });
}

fetchTop5(krCurrentType);
setInterval(() => fetchTop5(krCurrentType), 60000);

/* ----------------------------
    미국 주식 탭 기능
----------------------------- */
const usaTabs = document.querySelectorAll('.usa-tab');
let usaCurrentType = 'stckprpr';
const usaBodies = {
    stckprpr: document.getElementById('table-stckprpr'),
    prdyvrss: document.getElementById('table-prdyvrss'),
    prdyctrt: document.getElementById('table-prdyctrt')
};

usaTabs.forEach(tab => {
    tab.addEventListener('click', () => {
        document.querySelector('.usa-tab.active')?.classList.remove('active');
        tab.classList.add('active');

        usaCurrentType = tab.dataset.target;
        Object.keys(usaBodies).forEach(key => {
            usaBodies[key].style.display = (key === usaCurrentType) ? '' : 'none';
        });

        document.getElementById('top5-updated-usa').textContent =
            new Date().toLocaleTimeString('ko-KR', {hour: '2-digit', minute: '2-digit'});
    });
});

/* ----------------------------
    📌 코인 탭 기능
----------------------------- */
const coinTabs = document.querySelectorAll('.coin-tab');
let coinCurrentType = 'coin-stckprpr';

const coinBodies = {
    'coin-stckprpr': document.getElementById('table-coin-stckprpr'),
    'coin-prdyvrss': document.getElementById('table-coin-prdyvrss'),
    'coin-prdyctrt': document.getElementById('table-coin-prdyctrt')
};

coinTabs.forEach(tab => {
    tab.addEventListener('click', () => {
        document.querySelector('.coin-tab.active')?.classList.remove('active');
        tab.classList.add('active');

        coinCurrentType = tab.dataset.target;

        Object.keys(coinBodies).forEach(key => {
            coinBodies[key].style.display = (key === coinCurrentType) ? '' : 'none';
        });

        document.getElementById('top5-updated-coin').textContent =
            new Date().toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });
    });
});

//  초기화
usaBodies[usaCurrentType].style.display = '';
document.getElementById('top5-updated-usa').textContent =
    new Date().toLocaleTimeString('ko-KR', {hour: '2-digit', minute: '2-digit'});

// ✅ 초기화
coinBodies[coinCurrentType].style.display = '';
document.getElementById('top5-updated-coin').textContent =
    new Date().toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });