package wooteco.subway.line.dao;

import org.springframework.util.ReflectionUtils;
import wooteco.subway.line.Line;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryLineDao implements LineDao {
    private static Long seq = 0L;
    private static List<Line> lines = new ArrayList<>();

    @Override
    public Line save(Line line) {
        Line persistLine = createNewObject(line);
        lines.add(persistLine);
        return persistLine;
    }

    private Line createNewObject(Line line) {
        Field field = ReflectionUtils.findField(Line.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, line, ++seq);
        return line;
    }

    @Override
    public List<Line> findAll() {
        return lines;
    }

    @Override
    public Optional<Line> findByName(String name) {
        return lines.stream()
                .filter(line -> line.isSameName(name))
                .findAny();
    }

    @Override
    public Optional<Line> findById(Long id) {
        return lines.stream()
                .filter(line -> line.isSameId(id))
                .findAny();
    }

    @Override
    public void update(Line updatedLine) {
        findById(updatedLine.getId())
                .ifPresent(line -> {
                    int index = lines.indexOf(line);
                    lines.set(index, updatedLine);
                });
    }

    private Line findByIdIfExist(Long id) {
        return findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));
    }

    @Override
    public void delete(Long id) {
        Line line = findByIdIfExist(id);
        lines.remove(line);
    }

    @Override
    public Optional<String> findByNameAndNotInOriginalName(String name, String originalName) {
        return lines.stream()
                .filter(line -> !line.isSameName(originalName))
                .filter(line -> line.isSameName(name))
                .map(Line::getName)
                .findAny();
    }
}
