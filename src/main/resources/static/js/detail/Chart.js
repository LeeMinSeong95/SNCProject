let chart, areaSeries, volumeSeries, datafeed;
let isLoading = false;


//테이블 무한 스크롤
$(document).ready(function() {
    var offset = 10;
    var loading = false; // 중복 호출 방지 플래그
    var allDataLoaded = false; // 더 이상 불러올 데이터 없음을 표시
    const chartType = $("table[data-chart-type]").data("chart-type");
    const targetTable = $(`table[data-chart-type="${chartType}"] tbody`);
    let unit = "원";
    if (chartType === "us") unit = "$";
    function loadMoreData() {
        if (loading || allDataLoaded) return;
        loading = true;
        $("#loading").show();

        setTimeout(function() {
                $.ajax({
                    url: '/itemdetail/detail',
                    type: 'GET',
                    data: {
                        offset: offset,
                        chartType: chartType
                    },
                    dataType: "json",
                    success: function(data) {
                        if (data.length === 0) {
                            allDataLoaded = true;
                        } else {
                            offset += data.length;
                            // 받은 데이터를 테이블에 추가
                            $.each(data, function(index, item) {
                                var bookmarked = item.checkBkMrk === true;
                            var starIcon = bookmarked ? '⭐' : '☆';
                            // 받은 데이터를 테이블에 추가
                            var rowHtml = "<tr>" +
                                    "<td class='stock-item' data-item-id='" + item.itemId + "'>" + item.name + "</td>" +
                                    "<td>" + item.nowprice + unit + "</td>" +
                                    "<td>" + item.subprice + unit + "</td>" +
                                    "<td>" + item.subratio + "%" + "</td>" +
                                    "<td style='text-align: center; width: 60px;'>" +
                                        "<button class='bookmark-btn' " +
                                            "style='background: transparent; border: none; color: #FFD700; font-size: 18px; cursor: pointer; padding: 0;' " +
                                            "data-bookmarked='" + bookmarked + "'>" +
                                            "<span data-favorite='" + bookmarked + "'>" + starIcon + "</span>" +
                                        "</button>" +
                                    "</td>" +
                                   "</tr>";
                                targetTable.append(rowHtml);
                            });
                            bindRowClickEvent();
                        }
                    },
                    error: function(xhr, status, error) {
                        console.error("데이터 로드 에러:", error);
                    },
                    complete: function() {
                        loading = false;
                        $("#loading").hide();
                    }
                });
            }, 500);
    }

    // 한무 스크롤
    $(".table-container").on("scroll", function() {
        var $this = $(this);
        var scrollTop = $this.scrollTop();
        var containerHeight = $this.innerHeight();  // innerHeight() 사용
        var scrollHeight = $this.prop("scrollHeight");
        if (scrollTop + containerHeight >= scrollHeight - 10) {
            loadMoreData();
        }
    });

    // 테이블 검색바
    $("#tableFilterBtn").on("click", filterTable);
        $("#tableFilterInput").on("keypress", function(e) {
            if(e.which === 13) {
                e.preventDefault();
                filterTable();
            }
        });

        function filterTable() {
          const searchTerm = $("#tableFilterInput").val().trim();

          if (!searchTerm) return;

          // 기존 테이블 숨기기
          targetTable.find("tr").hide();

          //서버에 검색어 전달
          $.ajax({
            url: '/itemdetail/search',
            type: 'GET',
            data: {
                keyword: searchTerm ,
                chartType: chartType
            },
            dataType: 'json',
            success: function (data) {
              // 기존 tbody 내용 삭제
              targetTable.empty();

              if (data.length === 0) {
                targetTable.append(
                  `<tr><td colspan="4" style="text-align:center;">검색 결과가 없습니다.</td></tr>`
                );
                return;
              }

              // 검색 결과를 테이블에 삽입
              data.forEach(function (item) {
                  const bookmarked = item.checkBkMrk === true;
                      const starIcon = bookmarked ? '⭐' : '☆';

                      const rowHtml =
                          "<tr>" +
                          "<td class='stock-item' data-item-id='" + item.itemId + "' data-item-type='" + item.itemType + "'>" + item.name + "</td>" +
                          "<td>" + item.nowprice + unit + "</td>" +
                          "<td>" + item.subprice + unit + "</td>" +
                          "<td>" + item.subratio + "%" + "</td>" +
                          "<td style='text-align: center; width: 60px;'>" +
                              "<button class='bookmark-btn' " +
                                  "style='background: transparent; border: none; color: #FFD700; font-size: 18px; cursor: pointer; padding: 0;' " +
                                  "data-bookmarked='" + bookmarked + "'>" +
                                  "<span data-favorite='" + bookmarked + "'>" + starIcon + "</span>" +
                              "</button>" +
                          "</td>" +
                          "</tr>";
                  targetTable.append(rowHtml);
              });

              bindRowClickEvent();

            },
            error: function (xhr, status, error) {
              console.error("검색 실패:", error);
            }
          });
        }

});

const chartOptions = {
    layout: {
            background: { type: 'solid', color: '#121212' },
            textColor: '#e0e0e0',
            width: 820,
            innerHeight: 690,
        },
        grid: {
            vertLines: { color: '#2a2a2a' },
            horzLines: { color: '#2a2a2a' },
        },
    rightPriceScale: {
        borderVisible: false,
    },
};

function loadChartFor(stockName, chartType) {
    // 🧼 기존 차트 제거 후 새로 생성
    const chartContainer = document.getElementById('chart');
    chartContainer.style.position = 'relative';
    chartContainer.innerHTML = '';

    chart = LightweightCharts.createChart(chartContainer, chartOptions);

    areaSeries = chart.addCandlestickSeries({
        topColor: '#2962FF',
        bottomColor: 'rgba(41, 98, 255, 0.28)',
        lineColor: '#2962FF',
        lineWidth: 2,
    });
    areaSeries.priceScale().applyOptions({
        scaleMargins: { top: 0.1, bottom: 0.4 }
    });

    volumeSeries = chart.addHistogramSeries({
        color: '#26a69a',
        priceFormat: { type: 'volume' },
        priceScaleId: '',
        scaleMargins: { top: 0.7, bottom: 0 },
    });
    volumeSeries.priceScale().applyOptions({
        scaleMargins: { top: 0.7, bottom: 0 }
    });

    chart.applyOptions({
        crosshair: {
            mode: LightweightCharts.CrosshairMode.Normal,
            vertLine: {
                width: 8,
                color: "#C3BCDB44",
                style: LightweightCharts.LineStyle.Solid,
                labelBackgroundColor: "#9B7DFF",
            },
            horzLine: {
                color: "#9B7DFF",
                labelBackgroundColor: "#9B7DFF",
            },
        },
    });

    // 🔄 Datafeed 생성
    datafeed = new Datafeed(stockName, chartType);

    // 🔰 초기 데이터 로딩
    (async () => {
        const { candles, volumes } = await datafeed.getBars(90);
        areaSeries.setData(candles);
        volumeSeries.setData(volumes);
        chart.timeScale().fitContent();
    })();

    // 🔁 무한스크롤 로딩
    chart.timeScale().subscribeVisibleLogicalRangeChange(async (logicalRange) => {
        if (!logicalRange || !logicalRange.from || isLoading) return;
        if (logicalRange.from < 10) {
            isLoading = true;
            const { candles, volumes } = await datafeed.getBars(90);
            setTimeout(() => {
                areaSeries.setData(candles);
                volumeSeries.setData(volumes);
                isLoading = false;
            }, 250);
        }
    });
    //toolTip 추가
    const toolTipWidth = 50;
    const toolTipHeight = 50;
    const toolTipMargin = 15;

    // Create and style the tooltip html element
    const toolTip = document.createElement('div');
    toolTip.style = `
        width: 140px;
        position: absolute;
        display: none;
        padding: 8px;
        box-sizing: border-box;
        font-size: 12px;
        text-align: left;
        z-index: 1000;
        pointer-events: none;
        border: none;
        border-radius: 0;
        background: transparent;
        color: #ffffff;
        font-family: -apple-system, BlinkMacSystemFont, 'Trebuchet MS', Roboto, Ubuntu, sans-serif;
        top: 12px;
        left: 12px;
    `;
    chartContainer.appendChild(toolTip);

    // update tooltip
    chart.subscribeCrosshairMove(param => {
        if (
            param.point === undefined ||
            !param.time ||
            param.point.x < 0 ||
            param.point.x > chartContainer.clientWidth ||
            param.point.y < 0 ||
            param.point.y > chartContainer.clientHeight
        ) {
            toolTip.style.display = 'none';
        } else {
            const dateStr = param.time;
            toolTip.style.display = 'block';
            const data = param.seriesData.get(areaSeries);
            const price = data.value !== undefined ? data.value : data.close;
            toolTip.innerHTML = `
                    <div style="color: #2962FF">📅 ${dateStr}</div>
                    <div style="color: orange;">시가: ${data.open}</div>
                    <div style="color: red;">고가: ${data.high}</div>
                    <div style="color: #2962FF;">저가: ${data.low}</div>
                    <div style="color: #ccc;">종가: ${data.close}</div>
                `;

            const coordinate = areaSeries.priceToCoordinate(price);
            let shiftedCoordinate = param.point.x - 50;
            if (coordinate === null) {
                return;
            }
            shiftedCoordinate = Math.max(
                0,
                Math.min(chartContainer.clientWidth - toolTipWidth, shiftedCoordinate)
            );
            const coordinateY =
                coordinate - toolTipHeight - toolTipMargin > 0
                    ? coordinate - toolTipHeight - toolTipMargin
                    : Math.max(
                        0,
                        Math.min(
                            container.clientHeight - toolTipHeight - toolTipMargin,
                            coordinate + toolTipMargin
                        )
                    );
            toolTip.style.left = '12px';
            toolTip.style.right = 'auto';
            toolTip.style.top = '12px';
        }
    });
}

class Datafeed {
    constructor(stockName, chartType) {
        this.stockName = stockName;
        this.chartType = chartType;
        const today = new Date();
        this._earliestDate = today;
        this._candledata = [];
        this._volumedata = [];
    }

    async getBars(numberOfDays) {
        const to = this._earliestDate;
        const from = new Date(to);
        from.setDate(from.getDate() - numberOfDays);

        const fromStr = formatDateToYYYYMMDD(from);
        const toStr = formatDateToYYYYMMDD(to);
        console.log(this.chartType);
       //타입별 API url 변경
         let apiUrl = '';
         switch (this.chartType) {
           case 'kr':
             apiUrl = `/itemdetail/candle?name=${this.stockName}&from=${fromStr}&to=${toStr}&chartType=kr`;
             break;
           case 'us':
             apiUrl = `/itemdetail/candle?name=${this.stockName}&from=${fromStr}&to=${toStr}&chartType=us`;
             break;
           case 'coin':
             apiUrl = `/itemdetail/candle?name=${this.stockName}&from=${fromStr}&to=${toStr}&chartType=coin`;
             break;
           default:
             console.warn('잘못된 chartType:', this.chartType);
             return { candles: [], volumes: [] };
         }

        const response = await fetch(apiUrl);
        const data = await response.json();
        const newCandle = data.map(item => ({
            time: item.time,
            open: Number(item.open),
            high: Number(item.high),
            low: Number(item.low),
            close: Number(item.close),
        }));

        const newVolume = data.map(item => ({
            time: item.time,
            value: parseFloat(item.volume),
            color: Number(item.close) >= Number(item.open) ? '#4fd1c5' : '#f26d6d',
        }));

        this._earliestDate = new Date(from);
        this._earliestDate.setDate(this._earliestDate.getDate() - 1);
        this._candledata = [...newCandle, ...this._candledata];
        this._volumedata = [...newVolume, ...this._volumedata];

        return {
            candles: this._candledata,
            volumes: this._volumedata
        };
    }
}

function formatDateToYYYYMMDD(date) {
    if (!(date instanceof Date) || isNaN(date)) {
        console.warn("잘못된 date:", date);
        return "19700101";
    }
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    return `${yyyy}${mm}${dd}`;
}


function subtractDaysFromYYYYMMDD(yyyymmdd, days) {
    const yyyy = parseInt(yyyymmdd.slice(0, 4));
    const mm = parseInt(yyyymmdd.slice(4, 6)) - 1;
    const dd = parseInt(yyyymmdd.slice(6, 8));
    const date = new Date(yyyy, mm, dd);
    date.setDate(date.getDate() - days);
    return formatDateToYYYYMMDD(date);
}

function bindRowClickEvent() {
  document.querySelectorAll('table[data-chart-type]').forEach(table => {
    const chartType = table.getAttribute('data-chart-type');

    table.querySelectorAll('tbody tr').forEach(row => {
      row.addEventListener('click', () => {
        const stockCell = row.querySelector('.stock-item');
        if (stockCell) {
          const stockName = stockCell.textContent.trim();
          loadChartFor(stockName, chartType); // ✅ 동적으로 chartType 전달
        }
      });
    });
  });
}

//페이지 마다 chartType 설정
function setupStockTableInteraction({ tableId, chartType }) {
  const table = document.querySelector(`#${tableId}`);
  if (!table) return;

  table.querySelectorAll('tbody tr').forEach(row => {
    row.addEventListener('click', () => {
      const stockCell = row.querySelector('.stock-item');
      if (stockCell) {
        const stockName = stockCell.textContent.trim();
        loadChartFor(stockName, chartType);
      }
    });
  });

  const firstItem = table.querySelector('.stock-item');
  if (firstItem) {
    const stockName = firstItem.textContent.trim();
    setTimeout(() => {
          loadChartFor(stockName, chartType);
        }, 1500);
  }
}

window.addEventListener('DOMContentLoaded', () => {
  setupStockTableInteraction({ tableId: 'krStockTable', chartType: 'kr' });
  setupStockTableInteraction({ tableId: 'usStockTable', chartType: 'us' });
  setupStockTableInteraction({ tableId: 'coinTable', chartType: 'coin' });
});