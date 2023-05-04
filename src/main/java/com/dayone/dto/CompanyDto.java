package com.dayone.dto;

import com.dayone.persist.entity.CompanyEntity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CompanyDto {
    private String ticker;
    private String name;

    public static CompanyDto fromEntity(CompanyEntity entity){
        return CompanyDto.builder()
                .ticker(entity.getTicker())
                .name(entity.getName())
                .build();
        //return new CompanyDto(entity.getTicker(), entity.getName());
    }
}
