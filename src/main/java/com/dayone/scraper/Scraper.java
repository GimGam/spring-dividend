package com.dayone.scraper;

import com.dayone.dto.CompanyDto;
import com.dayone.dto.ScrapedResultDto;

public interface Scraper {
    CompanyDto scrapCompanyByTicker(String ticker);
    ScrapedResultDto scrap(CompanyDto companyDto);
}
