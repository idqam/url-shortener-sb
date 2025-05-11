package com.url.shortener.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ClickEventDTO {

   private LocalDate clickDate;
   private Long count;

}
