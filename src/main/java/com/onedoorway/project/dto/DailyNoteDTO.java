package com.onedoorway.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class DailyNoteDTO {
    private long id;
    private String note;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Instant createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private long createById;
    private String createByEmail;
    private String createByFirstName;
    private String createByLastName;
    private long clientId;
}
