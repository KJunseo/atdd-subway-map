package wooteco.subway.section.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.section.Section;

import java.sql.PreparedStatement;
import java.util.Objects;

@Repository
public class JdbcSectionDao implements SectionDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcSectionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Section save(Section section) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) " +
                "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(con -> {
            PreparedStatement pstmt = con.prepareStatement(query, new String[]{"id"});
            pstmt.setLong(1, section.getLineId());
            pstmt.setLong(2, section.getUpStationId());
            pstmt.setLong(3, section.getDownStationId());
            pstmt.setInt(4, section.getDistance());
            return pstmt;
        }, keyHolder);
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Section(id, section);
    }
}