package com.planitsquare.holiday_service.repository;

import com.planitsquare.holiday_service.entity.Holiday;
import com.planitsquare.holiday_service.entity.QHoliday;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HolidayQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QHoliday h = QHoliday.holiday;

    public Page<Holiday> search(Integer year, String country, LocalDate from, LocalDate to, String type, Pageable pageable) {

        List<Holiday> content = queryFactory
                .selectFrom(h)
                .where(
                        yearEq(year),
                        countryEq(country),
                        dateGoe(from),
                        dateLoe(to),
                        typeContains(type)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(h.count())
                .from(h)
                .where(
                        yearEq(year),
                        countryEq(country),
                        dateGoe(from),
                        dateLoe(to),
                        typeContains(type)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, count == null ? 0 : count);
    }

    private BooleanExpression yearEq(Integer year) {
        return year == null ? null : h.date.year().eq(year);
    }

    private BooleanExpression countryEq(String countryCode) {
        return countryCode == null ? null : h.countryCode.eq(countryCode);
    }

    private BooleanExpression dateGoe(LocalDate from) {
        return from == null ? null : h.date.goe(from);
    }

    private BooleanExpression dateLoe(LocalDate to) {
        return to == null ? null : h.date.loe(to);
    }

    private BooleanExpression typeContains(String type) {
        return type == null ? null : h.types.contains(type);
    }
}