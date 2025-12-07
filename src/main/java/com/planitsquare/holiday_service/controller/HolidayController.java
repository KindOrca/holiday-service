package com.planitsquare.holiday_service.controller;

import com.planitsquare.holiday_service.dto.HolidaySearchDto;
import com.planitsquare.holiday_service.service.HolidayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class HolidayController {

    private final HolidayService holidayService;

    @Operation(summary = "공휴일 검색")
    @GetMapping("/search")
    public Page<HolidaySearchDto> searchHolidays(
            @Parameter(description = "연도", example = "2024") @RequestParam(required = false) Integer year,
            @Parameter(description = "국가 코드", example = "KR") @RequestParam(required = false) String country,
            @Parameter(description = "검색 시작일", example = "2024-01-01") @RequestParam(required = false) LocalDate from,
            @Parameter(description = "검색 종료일", example = "2024-12-31") @RequestParam(required = false) LocalDate to,
            @Parameter(description = "공휴일 타입", example = "Public") @RequestParam(required = false) String type,
            Pageable pageable
    ) {
        return holidayService.search(year, country, from, to, type, pageable);
    }

    @PutMapping("/refresh")
    @Operation(summary = "공휴일 재동기화")
    public String refresh(@RequestParam int year, @RequestParam String countryCode) {
        holidayService.refresh(year, countryCode);
        return "refresh ok";
    }

    @DeleteMapping("/delete")
    @Operation(summary = "공휴일 삭제")
    public String delete(@RequestParam int year, @RequestParam String countryCode) {
        holidayService.delete(year, countryCode);
        return "delete ok";
    }
}