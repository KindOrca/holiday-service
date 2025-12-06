package com.planitsquare.holiday_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String countryCode;

    private boolean fixed;

    private boolean global;

    private Integer launchYear;

    @Column(nullable = false)
    private String localName;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "holiday_counties", joinColumns = @JoinColumn(name = "holiday_id"))
    private List<String> counties;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "holiday_types", joinColumns = @JoinColumn(name = "holiday_id"))
    private List<String> types;
}