package com.planitsquare.holiday_service.infra;

import com.planitsquare.holiday_service.dto.CountryDto;
import com.planitsquare.holiday_service.dto.HolidayDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ApiClient {

    private final RestTemplate restTemplate;

    public CountryDto[] fetchCountries() {

        return restTemplate.getForObject(
                "https://date.nager.at/api/v3/AvailableCountries",
                CountryDto[].class
        );
    }

    public HolidayDto[] fetchHolidays(Integer year, String countryCode) {

        return restTemplate.getForObject(
                "https://date.nager.at/api/v3/PublicHolidays/" + year + "/" + countryCode,
                HolidayDto[].class
        );
    }
}
