package com.onedoorway.project.services;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;
import com.onedoorway.project.Context;
import com.onedoorway.project.dto.*;
import com.onedoorway.project.exception.ClientServiceException;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.ClientContactRepository;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.FolderRepository;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@Transactional
public class ClientService {
    private final ClientRepository clientRepository;
    private final ClientContactRepository clientContactRepository;
    private final FolderRepository folderRepository;
    private final BlobServiceClientBuilder blobServiceClientBuilder;
    private final String connectionString;
    private final String containerName;
    private final Context context;

    @Autowired
    public ClientService(
            ClientRepository clientRepository,
            ClientContactRepository clientContactRepository,
            FolderRepository folderRepository,
            BlobServiceClientBuilder blobServiceClientBuilder,
            @Value("${azure.blob.connection-string}") String connectionString,
            @Value("${azure.blob.container.name}") String containerName,
            Context context) {
        this.clientRepository = clientRepository;
        this.clientContactRepository = clientContactRepository;
        this.folderRepository = folderRepository;
        this.blobServiceClientBuilder = blobServiceClientBuilder;
        this.connectionString = connectionString;
        this.containerName = containerName;
        this.context = context;
    }

    @SneakyThrows
    @PreAuthorize("@context.isAdmin()")
    public void createClient(ClientRequest request) {

        Client client =
                Client.builder()
                        .name(request.getName())
                        .gender(request.getGender())
                        .dob(
                                LocalDate.parse(
                                        request.getDob(),
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .email(request.getEmail())
                        .phone(request.getPhone())
                        .addrLine1(request.getAddrLine1())
                        .addrLine2(request.getAddrLine2())
                        .city(request.getCity())
                        .state(request.getState())
                        .postCode(request.getPostCode())
                        .deleted(false)
                        .expiryDate(null)
                        .healthFund(null)
                        .centerLinkNo("")
                        .individualReferenceNumber("")
                        .medicareCardName("")
                        .medicareNo(0)
                        .ndisNumber("")
                        .photo("")
                        .identity("")
                        .culture("")
                        .language("")
                        .diagnosis("")
                        .mobility("")
                        .communication("")
                        .medicationSupport("")
                        .transportation("")
                        .justiceOrders("")
                        .supportRatio("")
                        .shiftTimes("")
                        .supportWorkerSpecs("")
                        .build();
        clientRepository.save(client);
        log.info("Created the client for the given email {} ", request.getEmail());
    }

    @PreAuthorize("@context.isAdmin()")
    public void updateClient(Long id, ClientRequest request) throws ClientServiceException {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isPresent()) {
            Client existingEntity = client.get();
            existingEntity.setEmail(request.getEmail());
            existingEntity.setName(request.getName());
            existingEntity.setCity(request.getCity());
            existingEntity.setDob(
                    LocalDate.parse(request.getDob(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            existingEntity.setGender(request.getGender());
            existingEntity.setPhone(request.getPhone());
            existingEntity.setAddrLine1(request.getAddrLine1());
            existingEntity.setAddrLine2(request.getAddrLine2());
            existingEntity.setPostCode(request.getPostCode());
            existingEntity.setState(request.getState());
            existingEntity.setDeleted(request.getDeleted());
            existingEntity.setNdisNumber(request.getNdisNumber());
            clientRepository.save(existingEntity);

            log.info("Updated the client for the given id {}", id);
        } else {
            throw new ClientServiceException("Client not found");
        }
    }

    public List<ClientListDTO> listAllClients(ListClientRequest request)
            throws ClientServiceException {
        List<Client> clients;
        Pageable page =
                PageRequest.of(
                        request.getPageNumber(), request.getPageSize(), Sort.by("id").ascending());
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        if (request.getNameOrHouse() != null) {
            log.info("Fetched the clients");
            clients =
                    clientRepository
                            .findByNameContainingIgnoreCaseAndDeletedOrHouse_HouseCodeContainingIgnoreCaseAndDeleted(
                                    request.getNameOrHouse(),
                                    false,
                                    request.getNameOrHouse(),
                                    false,
                                    page);
        } else {
            clients = clientRepository.findAllByDeleted(false, page);
        }
        log.info("Fetched {} clients", clients.size());
        modelMapper
                .typeMap(Client.class, ClientListDTO.class)
                .addMappings(
                        mapper -> {
                            mapper.map(
                                    src -> src.getHouse().getHouseCode(), ClientListDTO::setHouse);
                        });
        return clients.stream()
                .map(item -> modelMapper.map(item, ClientListDTO.class))
                .collect(Collectors.toList());
    }

    public Long clientsCount(ListClientRequest request) {
        Long clientCount;
        if (request.getNameOrHouse() != null) {
            log.info("Fetching the clients count ");
            clientCount =
                    clientRepository
                            .countByNameContainingIgnoreCaseAndDeletedOrHouse_HouseCodeContainingIgnoreCaseAndDeleted(
                                    request.getNameOrHouse(),
                                    false,
                                    request.getNameOrHouse(),
                                    false);
        } else {
            clientCount = clientRepository.countByDeleted(false);
        }
        log.info("Fetched all clients - count {}", clientCount);
        return clientCount;
    }

    public void createClientContact(ClientContactRequest request) throws ClientServiceException {
        log.info("Creating client contact report for client id {}", request.getClientId());
        Optional<Client> client = clientRepository.findById(request.getClientId());
        if (client.isEmpty()) {
            log.error("Client not found for requested clientId {}", request.getClientId());
            throw new ClientServiceException("Client not found");
        }
        ClientContact clientContact =
                ClientContact.builder()
                        .client(client.get())
                        .relation(request.getRelation())
                        .designation(request.getDesignation())
                        .email(request.getEmail())
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .address1(request.getAddress1())
                        .address2(request.getAddress2())
                        .phone1(request.getPhone1())
                        .phone2(request.getPhone2())
                        .city(request.getCity())
                        .state(request.getState())
                        .notes(request.getNotes())
                        .status(NoticeStatus.ACTIVE)
                        .postCode(request.getPostCode())
                        .lastUpdatedBy(request.getLastUpdatedBy())
                        .lastUpdatedAt(context.now())
                        .build();
        clientContactRepository.save(clientContact);

        log.info("Created client contact with id {}", clientContact.getId());
    }

    public ClientContactDTO getClientContactById(long id) throws ClientServiceException {
        ModelMapper modelMapper = new ModelMapper();
        Optional<ClientContact> clientContact = clientContactRepository.findById(id);
        if (clientContact.isEmpty()) {
            log.info("client contact not found {}", id);
            throw new ClientServiceException("Client contact not found " + id);
        }
        log.info("Fetched client contacts based on id {}", id);
        return modelMapper.map(clientContact.get(), ClientContactDTO.class);
    }

    public void updateClientContact(Long id, UpdateClientContactRequest request)
            throws ClientServiceException {
        Optional<ClientContact> clientContact = clientContactRepository.findById(id);
        if (clientContact.isPresent()) {
            ClientContact existingEntity = clientContact.get();
            ModelMapper mapper = new ModelMapper();
            mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
            mapper.addMappings(
                    new PropertyMap<UpdateClientContactRequest, ClientContact>() {
                        @Override
                        protected void configure() {
                            skip(destination.getClient());
                        }
                    });
            mapper.map(request, existingEntity);
            if (StringUtils.hasText(request.getStatus())) {
                existingEntity.setStatus(NoticeStatus.valueOf(request.getStatus()));
            }
            existingEntity.setLastUpdatedAt(context.now());
            clientContactRepository.save(existingEntity);
            log.info("Updated the contact for the given id {}", id);
        } else {
            String errorMessage = String.format("Cannot get a contact for the id %d", id);
            log.error(errorMessage);
            throw new ClientServiceException(errorMessage);
        }
    }

    public List<ClientContactDTO> listClientContacts(Long clientId) throws ClientServiceException {
        Optional<Client> client = clientRepository.findById(clientId);
        if (client.isEmpty()) {
            log.error("Client not found for requested clientId {}", clientId);
            throw new ClientServiceException("Client not found");
        }
        List<ClientContactDTO> contactDTOS =
                clientContactRepository.findAllByClient_Id(clientId).stream()
                        .map(item -> new ModelMapper().map(item, ClientContactDTO.class))
                        .collect(Collectors.toList());
        log.info("Fetched client contacts with client id {}", clientId);
        return contactDTOS;
    }

    public void createFolder(FolderRequest request) throws ClientServiceException {
        log.info("Creating folder for client id {}", request.getClientId());
        Optional<Client> client = clientRepository.findById(request.getClientId());
        if (client.isEmpty()) {
            log.error("Client not found for requested clientId {}", request.getClientId());
            throw new ClientServiceException("Client not found");
        }
        List<Folder> Folders =
                folderRepository.getByStatusAndClientId(NoticeStatus.ACTIVE, request.getClientId());
        if (Folders.size() >= 5) {
            log.error("Total number of records for client{} exceeds 5 ", request.getClientId());
            throw new ClientServiceException("Five folders already exist");
        }
        Folder folder =
                Folder.builder()
                        .client(client.get())
                        .folderName(request.getFolderName())
                        .status(NoticeStatus.ACTIVE)
                        .lastUpdatedBy(request.getLastUpdatedBy())
                        .lastUpdatedAt(context.now())
                        .build();
        folderRepository.save(folder);
        log.info("Created folder with id {}", folder.getId());
    }

    public FolderDTO getFolderById(long id) throws ClientServiceException {
        Optional<Folder> folder = folderRepository.findById(id);
        if (folder.isEmpty()) {
            throw new ClientServiceException("No folder found with id " + id);
        }
        log.info("Fetched the folder based on id {}", id);
        return new ModelMapper().map(folder.get(), FolderDTO.class);
    }

    public List<FolderDTO> listFoldersById(Long clientId) throws ClientServiceException {
        Optional<Client> client = clientRepository.findById(clientId);
        if (client.isEmpty()) {
            log.error("Client not found for requested clientId {}", clientId);
            throw new ClientServiceException("Client not found");
        }
        List<FolderDTO> folderDTOS =
                folderRepository.findAllByClient_Id(clientId).stream()
                        .map(item -> new ModelMapper().map(item, FolderDTO.class))
                        .collect(Collectors.toList());
        log.info("Fetched folders with client id {}", clientId);
        return folderDTOS;
    }

    public void updateFolder(UpdateFolderRequest request) throws ClientServiceException {
        Optional<Folder> folder = folderRepository.findById(request.getId());
        if (folder.isEmpty()) {
            log.error("cannot find folder with id {}", request.getId());
            throw new ClientServiceException("folder does not exist");
        }
        Folder existingFolder = folder.get();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        mapper.addMappings(
                new PropertyMap<UpdateFolderRequest, Folder>() {
                    @Override
                    protected void configure() {
                        skip(destination.getClient());
                    }
                });
        mapper.map(request, existingFolder);
        if (StringUtils.hasText(request.getStatus())) {
            existingFolder.setStatus(NoticeStatus.valueOf(request.getStatus()));
        }
        existingFolder.setLastUpdatedAt(context.now());
        folderRepository.save(existingFolder);
        log.info("Updated the folder for the given id {}", request.getId());
    }

    public ClientDetailDTO getClientById(Long id)
            throws ClientServiceException, URISyntaxException, InvalidKeyException,
                    StorageException {
        ModelMapper modelMapper = new ModelMapper();
        Optional<Client> clients = clientRepository.findById(id);
        if (clients.isEmpty()) {
            log.info("client  not found with id {}", id);
            throw new ClientServiceException("Client  not found " + id);
        }
        log.info("found clients based on id {}", clients);
        log.info("client {}", clients.get().getPhoto());
        if (clients.get().getPhoto() == null) {
            log.info("photo is null");
            clients.get().setPhoto("");
            modelMapper.addMappings(
                    new PropertyMap<Client, ClientDetailDTO>() {
                        @Override
                        protected void configure() {
                            log.info("Before IRN");
                            map().setIndividualReferenceNumber(
                                            source.getIndividualReferenceNumber());
                            log.info("Before MCN");
                            map().setMedicareCardName(source.getMedicareCardName());
                            log.info("Before photo");
                        }
                    });
            return modelMapper.map(clients.get(), ClientDetailDTO.class);
        }
        if ((clients.get().getPhoto().isEmpty())) {
            log.info("get photo field empty");
            String sasUrl = "";
            log.info("sas url is {} ", sasUrl);
            clients.get().setPhoto("");
            modelMapper.addMappings(
                    new PropertyMap<Client, ClientDetailDTO>() {
                        @Override
                        protected void configure() {
                            log.info("Before IRN");
                            map().setIndividualReferenceNumber(
                                            source.getIndividualReferenceNumber());
                            log.info("Before MCN");
                            map().setMedicareCardName(source.getMedicareCardName());
                            log.info("Before photo");
                        }
                    });
            return modelMapper.map(clients.get(), ClientDetailDTO.class);
        } else {
            log.info("entering the else part");
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);
            CloudBlobClient cloudBlobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);
            CloudBlockBlob blob = container.getBlockBlobReference(clients.get().getPhoto());

            SharedAccessBlobPolicy accessBlobPolicy = new SharedAccessBlobPolicy();
            GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

            calendar.add(Calendar.MINUTE, 10);
            accessBlobPolicy.setSharedAccessExpiryTime(calendar.getTime());
            accessBlobPolicy.setPermissions(
                    EnumSet.of(
                            SharedAccessBlobPermissions.READ,
                            SharedAccessBlobPermissions.WRITE,
                            SharedAccessBlobPermissions.LIST));
            String sas = blob.generateSharedAccessSignature(accessBlobPolicy, null);

            String sasUrl = blob.getUri() + "?" + sas;

            log.info("SAS url {}", sasUrl);
            log.info("Fetched client based on id {}", id);
            modelMapper.addMappings(
                    new PropertyMap<Client, ClientDetailDTO>() {
                        @Override
                        protected void configure() {
                            map().setIndividualReferenceNumber(
                                            source.getIndividualReferenceNumber());
                            map().setMedicareCardName(source.getMedicareCardName());
                            map().setPhoto(sasUrl);
                        }
                    });
        }

        log.info("Before mapping client to DTO {}", clients.get().getPhoto());
        modelMapper.map(clients, ClientDTO.class);
        log.info("Fetched client  based on id {}", id);
        return modelMapper.map(clients.get(), ClientDetailDTO.class);
    }

    public void updateClientById(Long id, UpdateClientRequest request)
            throws ClientServiceException {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isPresent()) {
            Client existingEntity = client.get();
            ModelMapper mapper = new ModelMapper();
            if (request.getExpiryDate() == null) {
                mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
                mapper.map(request, existingEntity);
                log.info("Updated the client for the given id {}", id);
            } else {
                mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
                mapper.map(request, existingEntity);
                existingEntity.setExpiryDate(LocalDate.parse(request.getExpiryDate()));
                log.info("Updated the client for the given id {}", id);
            }
            clientRepository.save(existingEntity);
        } else {
            throw new ClientServiceException("Client not found");
        }
    }

    public void updateClientAdditional(Long id, UpdateClientRequest request)
            throws ClientServiceException {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isPresent()) {
            Client existingEntity = client.get();
            existingEntity.setIdentity(request.getIdentity());
            existingEntity.setCulture(request.getCulture());
            existingEntity.setLanguage(request.getLanguage());
            existingEntity.setDiagnosis(request.getDiagnosis());
            existingEntity.setMobility(request.getMobility());
            existingEntity.setCommunication(request.getCommunication());
            existingEntity.setMedicationSupport(request.getMedicationSupport());
            existingEntity.setTransportation(request.getTransportation());
            existingEntity.setJusticeOrders(request.getJusticeOrders());
            existingEntity.setSupportRatio(request.getSupportRatio());
            existingEntity.setShiftTimes(request.getShiftTimes());
            existingEntity.setSupportWorkerSpecs(request.getSupportWorkerSpecs());
            clientRepository.save(existingEntity);

            log.info("Updated the client for the given id {}", id);
        } else {
            throw new ClientServiceException("Client not found");
        }
    }

    @PreAuthorize("@context.isAdmin()")
    public void storePhoto(
            InputStream data, long size, long clientId, String name, String contentType)
            throws ClientServiceException {
        BlobServiceClient blobServiceClient =
                blobServiceClientBuilder.connectionString(connectionString).buildClient();
        BlobContainerClient blobContainerClient =
                blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient;
        Optional<Client> client = clientRepository.findById(clientId);
        if (contentType.equals("image/jpg")) {
            blobClient =
                    blobContainerClient.getBlobClient(
                            String.format("clientphoto/image_%s_%s.jpg", name, UUID.randomUUID()));
        } else if (contentType.equals("image/jpeg")) {
            blobClient =
                    blobContainerClient.getBlobClient(
                            String.format("clientphoto/image_%s_%s.jpeg", name, UUID.randomUUID()));
        } else if (contentType.equals("image/png")) {
            blobClient =
                    blobContainerClient.getBlobClient(
                            String.format("clientphoto/image_%s_%s.png", name, UUID.randomUUID()));
        } else {
            log.error("unsupported format");
            throw new ClientServiceException("jpg/jpeg/png file type are only supported");
        }
        blobClient.upload(data, size);
        String blobName = blobClient.getBlobName();
        if (client.isPresent()) {
            Client existingEntity = client.get();
            existingEntity.setPhoto(blobName);
        }
        log.info("Uploaded the blob to container - name {}", blobName);
        Client clientPhoto = client.orElse(Client.builder().photo(name).build());
        clientRepository.save(clientPhoto);
        log.info("Upserted the photo for clientId {} and clientName {}", clientId, name);
    }
}
