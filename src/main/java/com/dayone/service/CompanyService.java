package com.dayone.service;

import com.dayone.dto.CompanyDto;
import com.dayone.dto.ScrapedResultDto;
import com.dayone.exception.impl.AlreadyExistsCompanyException;
import com.dayone.exception.impl.NoCompanyException;
import com.dayone.persist.CompanyRepository;
import com.dayone.persist.DividendRepository;
import com.dayone.persist.entity.CompanyEntity;
import com.dayone.persist.entity.DividendEntity;
import com.dayone.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    private final Trie trie;
    private final Scraper scraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public CompanyDto save(String ticker){
        boolean exists = companyRepository.existsByTicker(ticker);
        if(exists){
            throw new AlreadyExistsCompanyException();
        }
        return storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable){
        return companyRepository.findAll(pageable);
    }

    private CompanyDto storeCompanyAndDividend(String ticker){
        CompanyDto companyDto = scraper.scrapCompanyByTicker(ticker);
        if(ObjectUtils.isEmpty(companyDto)){
            throw new RuntimeException("failed to scrap ticker -> " + ticker);
        }

        ScrapedResultDto scrapedResultDto = scraper.scrap(companyDto);

        CompanyEntity savedCompanyEntity = companyRepository.save(CompanyEntity.from(companyDto));

        List<DividendEntity> dividendEntityList = scrapedResultDto.getDividendDtoList().stream()
                .map(e -> new DividendEntity(savedCompanyEntity.getId(), e)).toList();

        dividendRepository.saveAll(dividendEntityList);

        return companyDto;
    }

    public void addAutoCompleteKeyword(String keyword){
        trie.put(keyword, null);
    }

    public List<String> autoComplete(String keyword){
        return (List<String>)trie.prefixMap(keyword).keySet()
                .stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    public void deleteAutoCompleteKeyword(String keyword){
        trie.remove(keyword);
    }

    public String deleteCompany(String ticker) {
        CompanyEntity company = companyRepository.findByTicker(ticker)
                .orElseThrow(NoCompanyException::new);

        dividendRepository.deleteAllByCompanyId(company.getId());
        companyRepository.delete(company);

        deleteAutoCompleteKeyword(company.getName());
        return company.getName();
    }
}
