package com.dayone.service;

import com.dayone.dto.CompanyDto;
import com.dayone.dto.DividendDto;
import com.dayone.dto.ScrapedResultDto;
import com.dayone.dto.constants.CacheKey;
import com.dayone.exception.impl.NoCompanyException;
import com.dayone.persist.CompanyRepository;
import com.dayone.persist.DividendRepository;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class FinanceService {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResultDto getDividendByCompanyName(String companyName) {
        CompanyEntity companyEntity = companyRepository.findByName(companyName)
                .orElseThrow(NoCompanyException::new);

        List<DividendEntity> dividendEntities = dividendRepository.findAllByCompanyId(companyEntity.getId());

        List<DividendDto> dividendDtos = dividendEntities.stream()
                .map(DividendDto::fromEntity)
                .collect(Collectors.toList());


        return ScrapedResultDto.builder()
                .companyDto(CompanyDto.fromEntity(companyEntity))
                .dividendDtoList(dividendDtos)
                .build();

        /*return new ScrapedResultDto(CompanyDto.fromEntity(companyEntity), dividendDtos);*/
    }
}
