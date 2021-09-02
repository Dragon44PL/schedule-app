package com.github.schedule.workmonth;

import lombok.*;

import javax.persistence.*;
import java.time.YearMonth;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class WorkMonthEntity {

    @Id
    private UUID id;

    private UUID userId;

    @OneToMany(mappedBy = "workMonth", cascade = CascadeType.ALL)
    private Set<WorkDayEntity> workDays;

    private YearMonth date;

    private WorkHourEntity totalHours;
}
