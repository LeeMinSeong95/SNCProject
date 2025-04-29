package com.SNC.common.domain;

import com.SNC.itemdetail.vo.ChartDto;
import com.SNC.itemdetail.vo.DetailResponse;

import java.util.List;

public interface DetailInterface {
    List<DetailResponse> fetch(int offset, String mbrId);
    List<DetailResponse> searchByKeyword(String keyword, String mbrId);
    List<ChartDto> getCandlestickData(String stockName, String from, String to, String period);

}
