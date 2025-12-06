package com.planitsquare.holiday_service.controller;

import com.planitsquare.holiday_service.dto.HolidaySearchResponse;
import com.planitsquare.holiday_service.service.HolidayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class HolidayController {

    private final HolidayService holidayService;

    @Operation(
            summary = "공휴일 검색",
            description = "연도, 국가코드, 기간, 타입 등의 조건으로 공휴일을 검색합니다. 결과는 페이지로 응답됩니다."
    )
    @GetMapping("/search")
    public Page<HolidaySearchResponse> searchHolidays(
            @Parameter(description = "연도", example = "2024") @RequestParam(required = false) Integer year,
            @Parameter(description = "국가 코드", example = "KR") @RequestParam(required = false) String country,
            @Parameter(description = "검색 시작일", example = "2024-01-01") @RequestParam(required = false) LocalDate from,
            @Parameter(description = "검색 종료일", example = "2024-12-31") @RequestParam(required = false) LocalDate to,
            @Parameter(description = "공휴일 타입", example = "Public") @RequestParam(required = false) String type,
            Pageable pageable
    ) {
        return holidayService.search(year, country, from, to, type, pageable);
    }
}
