package com.SNC.volumeRank.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class ResponseDTO {

    // 성공 실패 여부
    private String rtCd;

    // 응답코드
    private String msgCd;

    // 응답메세지
    private String msg1;

    // 응답상세
    private List<ResponseOutputDTO> output;
}
