package com.onedoorway.project.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.time.Instant;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@JsonDeserialize(builder = NoticeRequest.NoticeRequestBuilder.class)
public class NoticeRequest {
    @JsonPOJOBuilder(withPrefix = "")
    public static class NoticeRequestBuilder {}

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final Instant startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final Instant endDate;

    @NotBlank private final String notice;
    @NotEmpty private final List<String> houseCode;

    private final String noticeStatus;
}
