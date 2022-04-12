package com.onedoorway.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiscellaneousNoteDTO {
    private long id;
    private LookupDTO category;
    private String subject;
    private String content;
    private Boolean deleted;
    private String lastUploadedBy;
    private String user;
    private String client;
    private String house;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate noteDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private Instant lastUpdatedAt;
}
