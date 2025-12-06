package com.planitsquare.holiday_service.batch;

import com.planitsquare.holiday_service.dto.HolidayDto;
import com.planitsquare.holiday_service.infra.ApiClient;
import com.planitsquare.holiday_service.infra.CountryCache;
import com.planitsquare.holiday_service.service.HolidayService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class HolidayInitializer {

    private final ApiClient apiClient;
    private final CountryCache countryCache;
    private final HolidayService holidayService;

    @PostConstruct
    public void init() {

        int currentYear = LocalDate.now().getYear();
        for (String countryCode : countryCache.getAllCountryCodes()) {
            for (int year = currentYear; year <= currentYear; ++year) {
                HolidayDto[] holidays = apiClient.fetchHolidays(year, countryCode);

                if (holidays != null) holidayService.saveHolidays(holidays);
            }
        }
    }
}
