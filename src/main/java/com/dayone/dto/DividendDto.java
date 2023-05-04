package com.dayone.dto;

import com.dayone.persist.entity.DividendEntity;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DividendDto {
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime date;

    private String dividend;

    public static DividendDto fromEntity(DividendEntity entity){
        return DividendDto.builder()
                .date(entity.getDate())
                .dividend(entity.getDividend())
                .build();
        //return new DividendDto(entity.getDate(), entity.getDividend());
    }
}
