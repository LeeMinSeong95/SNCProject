package com.SNC.itemdetail.vo;

import com.SNC.alert.dto.AlertDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailResponse {
    private String name;            //주식 이름
    private String itemId;          //아이템 고유번호
    private String nowprice;        //주식 현재가
    private String subprice;        //전일 대비
    private String subratio;        //전일 대비율
    private boolean checkBkMrk;     //즐찾 여부

    //알림 로직을 위한 필드 추가
    private String itemType;
    private String markId;
    private AlertDto alert;
}

