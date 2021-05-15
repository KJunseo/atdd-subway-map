package wooteco.subway.section.dto.request;

import javax.validation.constraints.NotNull;

public class SectionCreateRequest {
    @NotNull
    private long upStationId;
    @NotNull
    private long downStationId;
    @NotNull
    private int distance;

    public SectionCreateRequest() {
    }

    public SectionCreateRequest(long upStationId, long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
