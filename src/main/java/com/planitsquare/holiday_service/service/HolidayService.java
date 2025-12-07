package com.planitsquare.holiday_service.service;

import com.planitsquare.holiday_service.dto.HolidayDto;
import com.planitsquare.holiday_service.dto.HolidaySearchDto;
import com.planitsquare.holiday_service.entity.Holiday;
import com.planitsquare.holiday_service.infra.ApiClient;
import com.planitsquare.holiday_service.infra.CountryCache;
import com.planitsquare.holiday_service.repository.HolidayQueryRepository;
import com.planitsquare.holiday_service.repository.HolidayRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final ApiClient apiClient;
    private final CountryCache countryCache;
    private final HolidayRepository holidayRepository;
    private final HolidayQueryRepository holidayQueryRepository;

    public void saveHolidays(HolidayDto[] holidays){
        for (HolidayDto holiday : holidays){
            holidayRepository.save(Holiday.builder()
                    .name(holiday.name())
                    .date(holiday.date())
                    .localName(holiday.localName())
                    .countryCode(holiday.countryCode())
                    .launchYear(holiday.launchYear())
                    .global(holiday.global())
                    .fixed(holiday.fixed())
                    .counties(holiday.counties())
                    .types(holiday.types())
                    .build());
        }
    }

    public Page<HolidaySearchDto> search(Integer year, String country, LocalDate from, LocalDate to, String type, Pageable pageable) {

        // year validation
        if (year != null && (year < 1900 || year > 2100)) {
            throw new IllegalArgumentException("연도는 1900년 ~ 2100년 사이여야 합니다");
        }

        // 국가 변환 → 코드 통일
        country = normalizeCountryCode(country);

        if (from != null && to != null && to.isBefore(from)) {
            throw new IllegalArgumentException("종료일은 시작일보다 이후여야 합니다");
        }

        if (type != null) {
            List<String> validTypes = Arrays.asList("Public", "Bank", "School", "Authorities", "Optional", "Observance");
            if (!validTypes.contains(type)) {
                throw new IllegalArgumentException("타입은 다음 중 하나여야 합니다: " + validTypes);
            }
        }

        Page<Holiday> result = holidayQueryRepository.search(year, country, from, to, type, pageable);
        return result.map(HolidaySearchDto::from);
    }

    @Transactional
    public void refresh(Integer year, String country) {

        country = normalizeCountryCode(country);

        delete(year, country);

        HolidayDto[] response = apiClient.fetchHolidays(year, country);

        List<Holiday> holidays = new ArrayList<>();
        for (HolidayDto holiday : response) {
            holidays.add(Holiday.builder()
                    .name(holiday.name())
                    .date(holiday.date())
                    .localName(holiday.localName())
                    .countryCode(holiday.countryCode())
                    .launchYear(holiday.launchYear())
                    .global(holiday.global())
                    .fixed(holiday.fixed())
                    .counties(holiday.counties())
                    .types(holiday.types())
                    .build());
        }
        holidayRepository.saveAll(holidays);
    }

    @Transactional
    public void delete(Integer year, String country) {

        country = normalizeCountryCode(country);

        LocalDate from = LocalDate.of(year, 1, 1);
        LocalDate to = LocalDate.of(year, 12, 31);
        holidayRepository.deleteByCountryCodeAndDateBetween(country, from, to);
    }

    private String normalizeCountryCode(String input) {

        if (input == null || input.isBlank()) return null;

        if (input.length() == 2) return input.toUpperCase();

        String resolved = countryCache.findCountryCodeByName(input);
        if (resolved == null) throw new IllegalArgumentException("해당 국가를 찾을 수 없습니다");

        return resolved.toUpperCase();
    }
}