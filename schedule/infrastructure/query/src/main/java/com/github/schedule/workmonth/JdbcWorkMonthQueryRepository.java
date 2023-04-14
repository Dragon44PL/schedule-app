package com.github.schedule.workmonth;

import com.github.schedule.workmonth.dto.WorkMonthQueryDto;
import lombok.*;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.*;

@Component
@RequiredArgsConstructor
class JdbcWorkMonthQueryRepository implements WorkMonthQueryRepository {

    private final NamedParameterJdbcTemplate template;

    @Override
    public List<WorkMonthQueryDto> findAll() {
        return executeQuery(
            "SELECT wm.id, wm.hours, wm.minutes, wm.user_id, wm.month, wm.year, wd.date, wd.ending_hour, wd.is_leave, wd.starting_hour FROM WORK_MONTH_ENTITY wm, WORK_DAY_ENTITY wd WHERE wm.id = wd.workmonth_id",
            new MapSqlParameterSource()
        );
    }

    @Override
    public List<WorkMonthQueryDto> findAllByDate(YearMonth yearMonth) {
        return executeQuery(
            "SELECT wm.id, wm.hours, wm.minutes, wm.user_id, wm.month, wm.year, wd.date, wd.ending_hour, wd.is_leave, wd.starting_hour FROM WORK_MONTH_ENTITY wm, WORK_DAY_ENTITY wd WHERE wm.id = wd.workmonth_id AND wm.year = :year AND wm.month = :month",
            new MapSqlParameterSource("year", yearMonth.getYear()).addValue("month", yearMonth.getMonth())
        );
    }

    @Override
    public Optional<WorkMonthQueryDto> findById(UUID id) {
        return executeQuery(
            "SELECT wm.id, wm.hours, wm.minutes, wm.user_id, wm.month, wm.year, wd.date, wd.ending_hour, wd.is_leave, wd.starting_hour FROM WORK_MONTH_ENTITY wm, WORK_DAY_ENTITY wd WHERE wm.id = wd.workmonth_id AND wm.id = :workMonthId",
            new MapSqlParameterSource("workMonthId", id.toString())
        ).stream().findAny();
    }

    @Override
    public List<WorkMonthQueryDto> findAllByUserId(UUID id) {
        return executeQuery(
            "SELECT wm.id, wm.hours, wm.minutes, wm.user_id, wm.month, wm.year, wd.date, wd.ending_hour, wd.is_leave, wd.starting_hour FROM WORK_MONTH_ENTITY wm, WORK_DAY_ENTITY wd WHERE wm.id = wd.workmonth_id AND user_id = :userId",
            new MapSqlParameterSource("userId", id.toString())
        );
    }

    private List<WorkMonthQueryDto> executeQuery(String query, SqlParameterSource source) {
        final List<FlattenedWorkMonthCollection> collection = template.query(query, source, BeanPropertyRowMapper.newInstance(FlattenedWorkMonthCollection.class));
        return FlattenedWorkMonthConverter.convertFlattenedResponse(collection);
    }
}
