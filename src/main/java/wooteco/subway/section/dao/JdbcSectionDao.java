package wooteco.subway.section.dao;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.line.Line;
import wooteco.subway.section.Section;
import wooteco.subway.station.Station;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
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
            pstmt.setLong(1, section.lineId());
            pstmt.setLong(2, section.upStationId());
            pstmt.setLong(3, section.downStationId());
            pstmt.setInt(4, section.getDistance());
            return pstmt;
        }, keyHolder);
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return new Section(id, section);
    }

    @Override
    public List<Section> findAllByLineId(Long id) {
        String query = "SELECT * FROM section WHERE line_id = ?";
        return jdbcTemplate.query(query, sectionRowMapper(), id);
    }

    private RowMapper<Section> sectionRowMapper() {
        return (rs, rowNum) -> new Section(
                rs.getLong("id"),
                new Line(rs.getLong("line_id")),
                new Station(rs.getLong("up_station_id")),
                new Station(rs.getLong("down_station_id")),
                rs.getInt("distance")
        );
    }

    @Override
    public void deleteByLineId(Long id) {
        String query = "DELETE FROM section WHERE line_id = ?";
        jdbcTemplate.update(query, id);
    }

    @Override
    public void batchInsert(List<Section> sections) {
        String query = "INSERT INTO section (line_id, up_station_id, down_station_id, distance) VALUES (?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(query,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement pstmt, int index) throws SQLException {
                        pstmt.setLong(1, sections.get(index).lineId());
                        pstmt.setLong(2, sections.get(index).upStationId());
                        pstmt.setLong(3, sections.get(index).downStationId());
                        pstmt.setInt(4, sections.get(index).getDistance());
                    }

                    @Override
                    public int getBatchSize() {
                        return sections.size();
                    }
                });
    }
}