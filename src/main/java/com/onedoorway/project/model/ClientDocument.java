package com.onedoorway.project.model;

import java.time.Instant;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "client_documents")
public class ClientDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_document_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "client", referencedColumnName = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "folder", referencedColumnName = "folder_id", nullable = false)
    private Folder folder;

    @Column(name = "doc_name")
    private String docName;

    @Column(name = "blob_name")
    private String blobName;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private NoticeStatus status;

    @Column(name = "last_uploaded_by")
    private String lastUploadedBy;

    @Column(name = " last_updated_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant lastUpdatedAt;
}
