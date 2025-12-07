package com.planitsquare.holiday_service.dto;

import com.planitsquare.holiday_service.entity.Holiday;

import java.time.LocalDate;
import java.util.List;

public record HolidaySearchDto(
        Long id,
        String name,
        String localName,
        LocalDate date,
        String countryCode,
        boolean fixed,
        boolean global,
        Integer launchYear,
        List<String> types,
        List<String> counties
) {

    public static HolidaySearchDto from(Holiday holiday) {
        return new HolidaySearchDto(
                holiday.getId(),
                holiday.getName(),
                holiday.getLocalName(),
                holiday.getDate(),
                holiday.getCountryCode(),
                holiday.isFixed(),
                holiday.isGlobal(),
                holiday.getLaunchYear(),
                holiday.getTypes(),
                holiday.getCounties()
        );
    }
}

