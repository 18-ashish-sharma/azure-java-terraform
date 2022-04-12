package com.onedoorway.project.services;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.*;
import com.onedoorway.project.Context;
import com.onedoorway.project.dto.ClientDocumentDTO;
import com.onedoorway.project.exception.ClientDocumentServiceException;
import com.onedoorway.project.model.Client;
import com.onedoorway.project.model.ClientDocument;
import com.onedoorway.project.model.Folder;
import com.onedoorway.project.model.NoticeStatus;
import com.onedoorway.project.repository.ClientDocumentRepository;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.FolderRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class ClientDocumentService {
    private final ClientDocumentRepository clientDocumentRepository;
    private final ClientRepository clientRepository;
    private final FolderRepository folderRepository;
    private final BlobServiceClientBuilder blobServiceClientBuilder;
    private final String connectionString;
    private final String containerName;
    private final Context context;

    @Autowired
    public ClientDocumentService(
            ClientDocumentRepository clientDocumentRepository,
            ClientRepository clientRepository,
            FolderRepository folderRepository,
            BlobServiceClientBuilder blobServiceClientBuilder,
            @Value("${azure.blob.connection-string}") String connectionString,
            @Value("${azure.blob.container.name}") String containerName,
            Context context) {
        this.clientDocumentRepository = clientDocumentRepository;
        this.clientRepository = clientRepository;
        this.folderRepository = folderRepository;
        this.blobServiceClientBuilder = blobServiceClientBuilder;
        this.connectionString = connectionString;
        this.containerName = containerName;
        this.context = context;
    }

    public void storeDocument(
            InputStream data,
            String docName,
            long size,
            long clientId,
            long folderId,
            String contentType,
            String lastUploadedBy)
            throws IOException {
        BlobServiceClient blobServiceClient =
                blobServiceClientBuilder.connectionString(connectionString).buildClient();
        BlobContainerClient blobContainerClient =
                blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient;
        if (contentType.equals("image/jpg")) {
            blobClient =
                    blobContainerClient.getBlobClient(
                            String.format("doc/document_%s_%s.jpg", clientId, UUID.randomUUID()));
        } else if (contentType.equals("image/jpeg")) {
            blobClient =
                    blobContainerClient.getBlobClient(
                            String.format("doc/document_%s_%s.jpeg", clientId, UUID.randomUUID()));
        } else if (contentType.equals("image/png")) {
            blobClient =
                    blobContainerClient.getBlobClient(
                            String.format("doc/document_%s_%s.png", clientId, UUID.randomUUID()));
        } else if (contentType.equals("application/pdf")) {
            blobClient =
                    blobContainerClient.getBlobClient(
                            String.format("doc/document_%s_%s.pdf", clientId, UUID.randomUUID()));
        } else {
            log.error("unsupported format");
            throw new IOException("jpg/png/pdf/jpeg file type are only supported");
        }
        blobClient.upload(data, size);
        String name = blobClient.getBlobName();
        log.info("Uploaded the blob to container - name {}", name);
        Optional<ClientDocument> clientDocument =
                clientDocumentRepository.findByClient_IdAndFolder_IdAndDocName(
                        clientId, folderId, docName);
        Optional<Client> client = clientRepository.findById(clientId);
        if (client.isEmpty()) {
            throw new IOException("client not found");
        }
        Optional<Folder> folder = folderRepository.findById(folderId);
        if (folder.isEmpty()) {
            throw new IOException("folder not found");
        }
        ClientDocument document =
                clientDocument.orElse(ClientDocument.builder().client(client.get()).build());
        document.setLastUploadedBy(lastUploadedBy);
        document.setLastUpdatedAt(context.now());
        document.setBlobName(name);
        document.setFolder(folder.get());
        document.setStatus(NoticeStatus.ACTIVE);
        document.setDocName(docName);
        clientDocumentRepository.save(document);
        log.info(
                "Upserted the document for clientId {} and folderID {} in container by lastUploadedBy {}",
                clientId,
                folderId,
                lastUploadedBy);
    }

    @SneakyThrows
    public List<ClientDocumentDTO> listDocuments(long clientId, long folderId)
            throws ClientDocumentServiceException {
        CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);
        CloudBlobClient cloudBlobClient = storageAccount.createCloudBlobClient();
        CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);
        CloudBlockBlob blob;

        SharedAccessBlobPolicy accessBlobPolicy = new SharedAccessBlobPolicy();
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

        calendar.add(Calendar.MINUTE, 10);
        accessBlobPolicy.setSharedAccessExpiryTime(calendar.getTime());
        accessBlobPolicy.setPermissions(
                EnumSet.of(
                        SharedAccessBlobPermissions.READ,
                        SharedAccessBlobPermissions.WRITE,
                        SharedAccessBlobPermissions.LIST));
        String sas;
        String sasUrl;
        Optional<Client> client = clientRepository.findById(clientId);
        if (client.isEmpty()) {
            log.error("Client not found for requested clientId {}", clientId);
            throw new ClientDocumentServiceException("Client not found");
        }
        Optional<Folder> folder = folderRepository.findById(folderId);
        if (folder.isEmpty()) {
            log.error("Folder not found for requested folderId {}", folderId);
            throw new ClientDocumentServiceException("Folder not found");
        }

        List<ClientDocumentDTO> clientDocumentDTOS = new ArrayList<>();
        List<ClientDocument> clientDocument =
                clientDocumentRepository.findAllByClient_IdAndFolder_Id(clientId, folderId).stream()
                        .map(item -> new ModelMapper().map(item, ClientDocument.class))
                        .collect(Collectors.toList());
        for (int i = 0; i < clientDocument.size(); i++) {
            ClientDocumentDTO clientDocumentDTO = new ClientDocumentDTO();
            log.info("doc {}", clientDocument.get(i).getBlobName());
            blob = container.getBlockBlobReference(clientDocument.get(i).getBlobName());
            sas = blob.generateSharedAccessSignature(accessBlobPolicy, null);
            sasUrl = blob.getUri() + "?" + sas;
            log.info("sasurl {}", sasUrl);
            clientDocumentDTO.setId(clientDocument.get(i).getId());
            clientDocumentDTO.setClientName(clientDocument.get(i).getClient().getName());
            clientDocumentDTO.setDocName(clientDocument.get(i).getDocName());
            clientDocumentDTO.setFolderName(clientDocument.get(i).getFolder().getFolderName());
            clientDocumentDTO.setBlobName(clientDocument.get(i).getBlobName());
            clientDocumentDTO.setBlobUrl(sasUrl);
            clientDocumentDTO.setStatus(clientDocument.get(i).getStatus().name());
            clientDocumentDTO.setLastUploadedBy(clientDocument.get(i).getLastUploadedBy());
            clientDocumentDTO.setLastUpdatedAt(clientDocument.get(i).getLastUpdatedAt());
            clientDocumentDTOS.add(clientDocumentDTO);
        }
        log.info("clientDocumentDTOS {}", clientDocumentDTOS);
        if (clientDocumentDTOS == null) {
            String errorMessage =
                    String.format(
                            "Cannot get an document for the client {} and folder {}",
                            clientId,
                            folderId);
            log.error(errorMessage);
            throw new ClientDocumentServiceException("documents not found");
        }
        log.info("Fetched all documents - count {}", clientDocumentDTOS.size());
        return clientDocumentDTOS;
    }

    public void deleteDocumentById(Long id) throws ClientDocumentServiceException {
        Optional<ClientDocument> clientDocument = clientDocumentRepository.findById(id);
        if (clientDocument.isPresent()) {
            ClientDocument existingEntity = clientDocument.get();
            existingEntity.setStatus(NoticeStatus.INACTIVE);
            clientDocumentRepository.save(existingEntity);
            log.info("deleted the document for the given id {}", id);
        } else {
            String errorMessage = String.format("Cannot find a document for the id %d", id);
            log.error(errorMessage);
            throw new ClientDocumentServiceException(errorMessage);
        }
    }
}
