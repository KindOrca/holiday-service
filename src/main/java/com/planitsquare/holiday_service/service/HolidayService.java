package com.planitsquare.holiday_service.service;

import com.planitsquare.holiday_service.dto.HolidayDto;
import com.planitsquare.holiday_service.dto.HolidaySearchResponse;
import com.planitsquare.holiday_service.entity.Holiday;
import com.planitsquare.holiday_service.infra.ApiClient;
import com.planitsquare.holiday_service.repository.HolidayQueryRepository;
import com.planitsquare.holiday_service.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final ApiClient apiClient;
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

    public Page<HolidaySearchResponse> search(
            Integer year,
            String country,
            LocalDate from,
            LocalDate to,
            String type,
            Pageable pageable
    ) {
        Page<Holiday> result = holidayQueryRepository.search(year, country, from, to, type, pageable);
        return result.map(HolidaySearchResponse::from);
    }
}