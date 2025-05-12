package com.url.shortener.service;

import com.url.shortener.dtos.ClickEventDTO;
import com.url.shortener.dtos.UrlMappingDTO;
import com.url.shortener.models.ClickEvent;
import com.url.shortener.models.UrlMapping;
import com.url.shortener.models.User;
import com.url.shortener.repository.ClickEventRepository;
import com.url.shortener.repository.UrlMappingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UrlMappingService {

    private UrlMappingRepository urlMappingRepository;
    private ClickEventRepository clickEventRepository;
    // All URL-safe alphanumeric characters
    private final String URL_SAFE_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +  // upper-case
                    "abcdefghijklmnopqrstuvwxyz" +  // lower-case
                    "0123456789";                    // digits

    public UrlMappingDTO createShortUrl(String originalUrl, User user) {
        String shortUrl = generateShortUrl();

        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setUser(user);
        urlMapping.setCreatedDate(LocalDateTime.now());
        UrlMapping savedUrlMapping = urlMappingRepository.save(urlMapping);

        return convertToDto(savedUrlMapping);
    }

    private UrlMappingDTO convertToDto(UrlMapping urlMapping){


        UrlMappingDTO urlMappingDTO = new UrlMappingDTO();
        urlMappingDTO.setId(urlMapping.getId());
        urlMappingDTO.setOriginalUrl(urlMapping.getOriginalUrl());
        urlMappingDTO.setShortUrl(urlMapping.getShortUrl());
        urlMappingDTO.setClickCount(urlMapping.getClickCount());
        urlMappingDTO.setCreatedDate(urlMapping.getCreatedDate());
        urlMappingDTO.setUsername(urlMapping.getUser().getUserName());

        return urlMappingDTO;


    }

    private String generateShortUrl() {

        Random random = new Random();

        StringBuilder shortUrl = new StringBuilder(8);
        for (int i = 0; i < 8; i++){
            shortUrl.append(
                    URL_SAFE_CHARS.charAt(
                            random.nextInt(URL_SAFE_CHARS.length())
                    )
            );

        }

        return shortUrl.toString();

    }


    public List<UrlMappingDTO> getUrlsByUser(User value) {

        List<UrlMapping> mappings = urlMappingRepository.findByUser(value);

        List<UrlMappingDTO> dtos = mappings.stream()
                .map(m -> {
                    UrlMappingDTO dto = new UrlMappingDTO();
                    dto.setId(m.getId());
                    dto.setOriginalUrl(m.getOriginalUrl());
                    dto.setShortUrl(m.getShortUrl());
                    dto.setClickCount(m.getClickCount());
                    dto.setCreatedDate(m.getCreatedDate());
                    dto.setUsername(m.getUser().getUserName());
                    return dto;
                })
                .toList();

        return dtos;





    }

    public List<ClickEventDTO> getClickEventsByDate(
            String shortUrl,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
        if (urlMapping == null) {
            return Collections.emptyList();
        }

        List<ClickEvent> events = clickEventRepository
                .findByUrlMappingAndClickDateBetween(urlMapping, startDate, endDate);

        Map<LocalDate, Long> countsByDate = events.stream()
                .collect(Collectors.groupingBy(
                        click -> click.getClickDate().toLocalDate(),
                        Collectors.counting()
                ));

        return countsByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())  // optional: sort by date
                .map(entry -> {
                    ClickEventDTO dto = new ClickEventDTO();
                    dto.setClickDate(entry.getKey());
                    dto.setCount(entry.getValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Map<LocalDate, Long> getTotalClicksByUserAndDate(
            User user,
            LocalDate start,
            LocalDate end
    ) {
        List<UrlMapping> urlMappings = urlMappingRepository.findByUser(user);
        if (urlMappings == null || urlMappings.isEmpty()) {
            return Collections.emptyMap();
        }

        List<ClickEvent> clickEvents = clickEventRepository
                .findByUrlMappingInAndClickDateBetween(urlMappings, start.atStartOfDay(), end.plusDays(1).atStartOfDay());

        return clickEvents.stream()
                .collect(Collectors.groupingBy(
                        ev -> ev.getClickDate().toLocalDate(),
                        Collectors.counting()
                ));
    }
}
