package com.planitsquare.holiday_service.service;

import com.planitsquare.holiday_service.dto.HolidayDto;
import com.planitsquare.holiday_service.dto.HolidaySearchDto;
import com.planitsquare.holiday_service.entity.Holiday;
import com.planitsquare.holiday_service.infra.ApiClient;
import com.planitsquare.holiday_service.repository.HolidayQueryRepository;
import com.planitsquare.holiday_service.repository.HolidayRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public Page<HolidaySearchDto> search(
            Integer year,
            String country,
            LocalDate from,
            LocalDate to,
            String type,
            Pageable pageable
    ) {
        Page<Holiday> result = holidayQueryRepository.search(year, country, from, to, type, pageable);
        return result.map(HolidaySearchDto::from);
    }

    @Transactional
    public void refresh(Integer year, String countryCode) {

        delete(year, countryCode);

        HolidayDto[] response = apiClient.fetchHolidays(year, countryCode);

        List<Holiday> holidays = new ArrayList<>();
        for (HolidayDto holiday : response) {
            holidays.add(Holiday.builder()
                    .name(holiday.name())
                    .date(holiday.date())
                    .name(holiday.name())
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
    public void delete(Integer year, String countryCode) {
        LocalDate from = LocalDate.of(year, 1, 1);
        LocalDate to = LocalDate.of(year, 12, 31);
        holidayRepository.deleteByCountryCodeAndDateBetween(countryCode, from, to);
    }
}