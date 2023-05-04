package com.dayone.persist.entity;

import com.dayone.dto.CompanyDto;
import lombok.*;

import javax.persistence.*;

@Entity(name = "COMPANY")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String ticker;

    private String name;

    public static CompanyEntity from(CompanyDto companyDto){
        return CompanyEntity.builder()
                .ticker(companyDto.getTicker())
                .name(companyDto.getName())
                .build();
    }
}
