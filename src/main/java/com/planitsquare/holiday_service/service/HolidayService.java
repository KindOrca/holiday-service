package com.planitsquare.holiday_service.service;

import com.planitsquare.holiday_service.dto.HolidayDto;
import com.planitsquare.holiday_service.entity.Holiday;
import com.planitsquare.holiday_service.infra.ApiClient;
import com.planitsquare.holiday_service.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HolidayService {

    private final ApiClient apiClient;
    private final HolidayRepository holidayRepository;

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
}
