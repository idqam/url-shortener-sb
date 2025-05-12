package com.url.shortener.controllers;

import com.url.shortener.dtos.ClickEventDTO;
import com.url.shortener.dtos.UrlMappingDTO;
import com.url.shortener.models.User;
import com.url.shortener.service.UrlMappingService;
import com.url.shortener.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/urls")
@AllArgsConstructor
public class UrlMappingController {
    private final UrlMappingService urlMappingService;
    private final UserService userService;

    @PostMapping("/shorten")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UrlMappingDTO> createShortUrl(
            @RequestBody Map<String, String> request,
            Principal principal
    ) {
        UrlMappingDTO dto = null;
        String url = request.get("originalUrl");
        Optional<User> user = userService.findByUserName(principal.getName());
        if (user.isPresent()){
            dto = urlMappingService.createShortUrl(url, user.get());


        }

        return ResponseEntity.ok(dto);



    }

    @GetMapping("/myurls")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<UrlMappingDTO>> getUserUrls(Principal principal){


        Optional<User> user = userService.findByUserName(principal.getName());


        List<UrlMappingDTO> urls = urlMappingService.getUrlsByUser(user.get());

        return ResponseEntity.ok(urls);






    }

    @GetMapping("/analytics/{shortUrl}")
    @PreAuthorize("hasRole('User')")
    public ResponseEntity<List<ClickEventDTO>> getUrlAnalytics(@PathVariable String shortUrl,
                                                               @RequestParam("startDate") LocalDateTime startDate,
                                                               @RequestParam("endDate") LocalDateTime endDate){

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime start = LocalDateTime.parse(startDate.toString(), formatter);
        LocalDateTime end = LocalDateTime.parse(endDate.toString(), formatter);
        List<ClickEventDTO> clickEventDTOS =  urlMappingService.getClickEventsByDate(shortUrl, start, end);

        return ResponseEntity.ok(clickEventDTOS);

    }

    @GetMapping("/totalClicks}")
    @PreAuthorize("hasRole('User')")
    public ResponseEntity<Map<LocalDate, Long>> getTotalClicksByDate(Principal principal, @RequestParam("startDate") LocalDateTime startDate,
                                                                @RequestParam("endDate") LocalDateTime endDate){
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        Optional<User> user = userService.findByUserName(principal.getName());
        User user2 = null;
        if (user.isPresent()) {
            user2 = user.get();
        }
        LocalDateTime start = LocalDateTime.parse(startDate.toString(), formatter);
        LocalDateTime end = LocalDateTime.parse(endDate.toString(), formatter);
        Map<LocalDate, Long> totalClicks =  urlMappingService.getTotalClicksByUserAndDate(user2, start, end);
        return ResponseEntity.ok(totalClicks);



    }

}
