package com.planitsquare.holiday_service.infra;

import com.planitsquare.holiday_service.dto.CountryDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class CountryCache {

    private final Map<String, String> countries = new ConcurrentHashMap<>();
    private final ApiClient apiClient;

    @PostConstruct
    public void init() {
        for (CountryDto c : apiClient.fetchCountries()) {
            countries.put(c.name(), c.countryCode());
        }
    }

    public String getCountryCode(String name) {
        return countries.get(name);
    }

    public List<String> getAllCountryCodes() {
        return countries.values().stream().toList();
    }
}
