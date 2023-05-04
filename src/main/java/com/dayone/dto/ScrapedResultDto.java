package com.dayone.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ScrapedResultDto {

    private CompanyDto companyDto;
    private List<DividendDto> dividendDtoList;

    public ScrapedResultDto() {this.dividendDtoList = new ArrayList<>();}
}
