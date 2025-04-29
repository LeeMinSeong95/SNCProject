package com.SNC.common.util;

import com.SNC.stock.vo.StockInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class HeaderUtil {

    @Autowired
    private StockInfo accessInfo;

    /**
     * @param tr_id
     * @return HttpHeaders
     * @Description symbol 기준 헤더 세팅
     */
    public HttpHeaders createDefaultHeaders(String tr_id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("authorization", "Bearer " + accessInfo.getToken());
        headers.set("appKey", accessInfo.getKey());
        headers.set("appSecret", accessInfo.getSecret());
        headers.set("tr_id", tr_id);
        headers.set("custtype", "P");
        return headers;

    }
}
