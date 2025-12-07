package com.planitsquare.holiday_service.service;

import com.planitsquare.holiday_service.dto.HolidayDto;
import com.planitsquare.holiday_service.entity.Holiday;
import com.planitsquare.holiday_service.infra.ApiClient;
import com.planitsquare.holiday_service.repository.HolidayQueryRepository;
import com.planitsquare.holiday_service.repository.HolidayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class HolidayServiceTest {

    @Mock
    private ApiClient apiClient;

    @Mock
    private HolidayRepository holidayRepository;

    @Mock
    private HolidayQueryRepository holidayQueryRepository;

    @InjectMocks
    private HolidayService holidayService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("saveHolidays() : API 응답 DTO를 Holiday 엔티티로 변환해 저장")
    void saveHolidays_success() {

        HolidayDto[] dto = {
                new HolidayDto(
                        LocalDate.of(2025, 12, 25),
                        "크리스마스",
                        "Christmas",
                        "KR",
                        false,
                        true,
                        null,
                        null,
                        List.of("Public")
                )
        };

        holidayService.saveHolidays(dto);

        verify(holidayRepository, times(1)).save(any(Holiday.class));
    }

    @Test
    @DisplayName("refresh() : 기존 데이터 삭제 후 새 Holiday 데이터 저장")
    void refresh_success() {

        int year = 2025;
        String country = "KR";

        HolidayDto[] apiResponse = {
                new HolidayDto(
                        LocalDate.of(2025, 1, 1),
                        "새해",
                        "New Year",
                        "KR",
                        false,
                        true,
                        null,
                        null,
                        List.of("Public")
                )
        };

        when(apiClient.fetchHolidays(year, country)).thenReturn(apiResponse);

        holidayService.refresh(year, country);

        verify(holidayRepository, times(1))
                .deleteByCountryCodeAndDateBetween(eq(country), any(), any());

        verify(holidayRepository, times(1))
                .saveAll(anyList());
    }

    @Test
    @DisplayName("delete() → 해당 연도의 특정 국가 공휴일 데이터를 삭제")
    void delete_success() {

        int year = 2025;
        String country = "US";

        holidayService.delete(year, country);

        verify(holidayRepository, times(1))
                .deleteByCountryCodeAndDateBetween(eq(country), any(), any());
    }

    @Test
    @DisplayName("search() → 검색 결과를 HolidaySearchResponse DTO로 반환")
    void search_success() {
        Pageable pageable = PageRequest.of(0, 10);

        Holiday holiday = Holiday.builder()
                .name("Christmas Day")
                .localName("크리스마스")
                .countryCode("KR")
                .date(LocalDate.of(2025, 12, 25))
                .types(List.of("Public"))
                .build();

        Page<Holiday> mockPage = new PageImpl<>(List.of(holiday), pageable, 1);

        when(holidayQueryRepository.search(
                any(), any(), any(), any(), any(), eq(pageable)
        )).thenReturn(mockPage);

        var result = holidayService.search(null, null, null, null, null, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("Christmas Day");
        verify(holidayQueryRepository, times(1))
                .search(any(), any(), any(), any(), any(), eq(pageable));
    }
}