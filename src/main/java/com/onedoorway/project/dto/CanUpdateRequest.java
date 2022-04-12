package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.Instant;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@JsonDeserialize(builder = CanUpdateRequest.CanUpdateRequestBuilder.class)
public class CanUpdateRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class CanUpdateRequestBuilder {}

    @NotNull private final long id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final Instant lastUpdatedAt;
}
