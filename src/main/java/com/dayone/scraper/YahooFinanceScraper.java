package com.dayone.scraper;

import com.dayone.dto.CompanyDto;
import com.dayone.dto.DividendDto;
import com.dayone.dto.ScrapedResultDto;
import com.dayone.dto.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper
    implements Scraper{

    private static final String URL =
           "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";

    private static final String SUMMARY_URL
            ="https://finance.yahoo.com/quote/%s?p=%s";
    private static final long START_TIME = 86400;

    @Override
    public ScrapedResultDto scrap(CompanyDto companyDto){

        ScrapedResultDto scrapedResultDto = new ScrapedResultDto();
        scrapedResultDto.setCompanyDto(companyDto);

        try {
            long now = System.currentTimeMillis() / 1000;
            String url = String.format(URL, companyDto.getTicker(), START_TIME, now);
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element tableElem = parsingDivs.get(0);

            Element tBody = tableElem.children().get(1);
            List<DividendDto> dividendDtoList = new ArrayList<>();
            for(Element e : tBody.children()){
                String text = e.text();
                if(!text.endsWith("Dividend")){
                    continue;
                }

                String[] splits = text.split(" ");
                int month = Month.convertToNumber(splits[0]);
                int day = Integer.valueOf(splits[1].replace(",", ""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                if(-1 == month){
                    throw new RuntimeException();
                }

                dividendDtoList.add(DividendDto.builder()
                        .date(LocalDateTime.of(year, month, day, 0, 0))
                        .dividend(dividend)
                        .build());
                /*dividendDtoList.add(new DividendDto(LocalDateTime.of(year, month, day, 0, 0),
                        dividend));*/
            }
            scrapedResultDto.setDividendDtoList(dividendDtoList);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return scrapedResultDto;
    }

    @Override
    public CompanyDto scrapCompanyByTicker(String ticker){
       String url = String.format(SUMMARY_URL, ticker, ticker);

        try {
            Document document = Jsoup.connect(url).get();
            Element titleEle = document.getElementsByTag("h1").get(0);
            String title = titleEle.text().split("-")[1].trim();

            return CompanyDto.builder()
                    .ticker(ticker)
                    .name(title)
                    .build();
            /*return new CompanyDto(ticker, title);*/
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
