package com.onedoorway.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.onedoorway.project.model.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BowelNoteDTO {
    private long id;
    private long clientId;
    private String clientName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate clientDOB;

    private Size size;
    private Boolean type1;
    private Boolean type2;
    private Boolean type3;
    private Boolean type4;
    private Boolean type5;
    private Boolean type6;
    private Boolean type7;
    private Boolean deleted;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recordTime;

    private String lastUploadedBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private Instant lastUpdatedAt;
}
