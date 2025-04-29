package com.SNC.itemdetail.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChartDto {
    private String time;
    private String open;
    private String high;
    private String low;
    private String close;
    private String volume;
}
