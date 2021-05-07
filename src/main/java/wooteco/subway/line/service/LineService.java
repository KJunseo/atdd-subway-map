package wooteco.subway.line.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import wooteco.subway.line.Line;
import wooteco.subway.line.dto.request.LineCreateRequest;
import wooteco.subway.line.dto.request.LineUpdateRequest;
import wooteco.subway.line.dto.response.LineResponse;
import wooteco.subway.line.repository.LineRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private static final Logger log = LoggerFactory.getLogger(LineService.class);

    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse save(LineCreateRequest lineCreateRequest) {
        validateLineName(lineCreateRequest);
        Line line = new Line(lineCreateRequest.getName(), lineCreateRequest.getColor());
        Line newLine = lineRepository.save(line);
        log.info(newLine.getName() + " 노선 생성 성공");
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    private void validateLineName(LineCreateRequest lineCreateRequest) {
        if (checkNameDuplicate(lineCreateRequest)) {
            throw new IllegalArgumentException("중복된 이름의 노선이 존재합니다.");
        }
    }

    private boolean checkNameDuplicate(LineCreateRequest lineCreateRequest) {
        return lineRepository.findAll().stream()
                .anyMatch(line -> line.isSameName(lineCreateRequest.getName()));
    }

    public LineResponse findLine(Long id) {
        Line newLine = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));
        log.info(newLine.getName() + "노선 조회 성공");
        return new LineResponse(newLine.getId(), newLine.getName(), newLine.getColor());
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        log.info("지하철 모든 노선 조회 성공");
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());
    }

    public void updateLine(Long id, LineUpdateRequest lineUpdateRequest) {
        validatesRequest(id, lineUpdateRequest);

        Line updatedLine = new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor());
        lineRepository.updateById(id, updatedLine);
        log.info("노선 정보 수정 완료");
    }

    private void validatesRequest(Long id, LineUpdateRequest lineUpdateRequest) {
        Line currentLine = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));

        String oldName = currentLine.getName();
        String newName = lineUpdateRequest.getName();

        if (validatesName(oldName, newName)) {
            throw new IllegalArgumentException("변경할 수 없는 이름입니다.");
        }
    }

    private boolean validatesName(String oldName, String newName) {
        return lineRepository.findAll().stream()
                .filter(line -> !line.isSameName(oldName))
                .anyMatch(line -> line.isSameName(newName));
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
        log.info("노선 삭제 성공");
    }
}
