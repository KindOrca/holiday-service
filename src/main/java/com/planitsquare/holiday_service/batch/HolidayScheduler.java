package com.planitsquare.holiday_service.batch;

import com.planitsquare.holiday_service.infra.CountryCache;
import com.planitsquare.holiday_service.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class HolidayScheduler {

    private final CountryCache countryCache;
    private final HolidayService holidayService;

    @Scheduled(cron = "0 0 1 2 1 *", zone = "Asia/Seoul")
    public void autoRefresh() {

        int currentYear = LocalDate.now().getYear();

        for (String countryCode : countryCache.getAllCountryCodes()) {
            holidayService.refresh(currentYear - 1, countryCode);
            holidayService.refresh(currentYear, countryCode);
        }
    }
}