package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.Instant;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@JsonDeserialize(builder = ListForGraphRequest.ListForGraphRequestBuilder.class)
public class ListForGraphRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class ListForGraphRequestBuilder {}

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final Instant start;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final Instant end;

    @NotBlank private final String raisedFor;
}
