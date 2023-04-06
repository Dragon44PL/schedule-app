package com.github.schedule.workmonth;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

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
    @Type(type="uuid-char")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Type(type="uuid-char")
    private UUID userId;

    @OneToMany(mappedBy = "workMonth", cascade = CascadeType.ALL)
    private Set<WorkDayEntity> workDays;

    private YearMonthEntity yearMonth;

    private WorkHourEntity totalHours;
}
