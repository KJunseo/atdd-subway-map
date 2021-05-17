package wooteco.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @Autowired
    private StationDao stationDao;

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        StationRequest 강남역 = new StationRequest("강남역");

        // when
        ExtractableResponse<Response> response = createRequest("/stations", 강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        StationRequest 강남역 = new StationRequest("강남역");

        // when
        createRequest("/stations", 강남역);
        ExtractableResponse<Response> response = createRequest("/stations", 강남역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        StationRequest 강남역 = new StationRequest("강남역");
        StationRequest 역삼역 = new StationRequest("역삼역");

        // when
        ExtractableResponse<Response> 강남역생성 = createRequest("/stations", 강남역);
        ExtractableResponse<Response> 역삼역생성 = createRequest("/stations", 역삼역);
        ExtractableResponse<Response> response = findAllRequest("/stations");
        List<Long> expectedStationIds = expectedStationIdsList(Arrays.asList(강남역생성, 역삼역생성));
        List<Long> resultStationIds = resultStationIdsList(response);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultStationIds).containsAll(expectedStationIds);
    }

    private List<Long> expectedStationIdsList(List<ExtractableResponse<Response>> stations) {
        return Stream.of(stations.get(0), stations.get(1))
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
    }

    private List<Long> resultStationIdsList(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        StationRequest 강남역 = new StationRequest("강남역");
        int originalSize;

        // when
        ExtractableResponse<Response> 강남역생성 = createRequest("/stations", 강남역);
        String uri = 강남역생성.header("Location");
        originalSize = stationDao.findAll().size();
        ExtractableResponse<Response> response = deleteRequest(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(stationDao.findAll()).hasSize(originalSize - 1);
    }
}
