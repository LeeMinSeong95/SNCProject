package com.SNC.chartNews.vo.news;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Value
@AllArgsConstructor
@Builder
public class NewsResponseDto {
    private String title;
    private String link;
    private String description;
    @JsonProperty("originallink")
    private String imageLink;

    /**
     * @return imageLink 존재여부에 따른 validation
     */
    public boolean validateImageLink(){
        return imageLink != null && ! imageLink.isEmpty();
    }
}
