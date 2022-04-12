package com.onedoorway.project.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Every.everyItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.*;
import com.onedoorway.project.FrozenContext;
import com.onedoorway.project.dto.*;
import com.onedoorway.project.exception.ClientServiceException;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.ClientContactRepository;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.FolderRepository;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import lombok.SneakyThrows;
import org.hamcrest.beans.HasPropertyWithValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock ClientRepository mockClientRepository;

    @Mock ClientContactRepository mockContactRepository;

    @Mock FolderRepository mockFolderRepository;

    @Mock BlobServiceClientBuilder mockBlobServiceClientBuilder;

    private final FrozenContext context = new FrozenContext();

    private ClientService clientService;

    @BeforeEach
    void init() {
        clientService =
                new ClientService(
                        mockClientRepository,
                        mockContactRepository,
                        mockFolderRepository,
                        mockBlobServiceClientBuilder,
                        "a-mock-connection-string",
                        "test-container",
                        context);
    }

    @SneakyThrows
    @Test
    void testCreateClients_Success() {
        // Given
        String name = "client";
        String gender = "female";
        String email = "client@1client.com";
        String phone = "01 1114 6789";
        String dob = "1957-01-01";
        String address1 = "Address 1";
        String address2 = "Address 2";
        String city = "test-city";
        String state = "test-state";
        String postCode = "0000";
        long medicareNo = 0;
        LocalDate expiryDate = null;
        Boolean healthFund = null;
        String centerLinkNo = "0";
        String medicareCardName = "vens";
        String individualReferenceNo = "F1";
        String ndisNumber = "";
        String photo = "";
        String identity = "";
        String culture = "";
        String language = "";
        String diagnosis = "";
        String mobility = "";
        String communication = "";
        String transportation = "";
        String justiceOrders = "";
        String supportRatio = "";
        String shiftTimes = "";
        String supportWorkSpecs = "";
        String medicationSupport = "";
        Boolean deleted = false;

        ClientRequest request =
                ClientRequest.builder()
                        .name(name)
                        .gender(gender)
                        .email(email)
                        .phone(phone)
                        .dob(dob)
                        .addrLine1(address1)
                        .addrLine2(address2)
                        .city(city)
                        .state(state)
                        .postCode(postCode)
                        .build();
        // When
        clientService.createClient(request);
        //  Then
        Client expected =
                Client.builder()
                        .name(name)
                        .gender(gender)
                        .dob(LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .email(email)
                        .phone(phone)
                        .addrLine1(address1)
                        .addrLine2(address2)
                        .city(city)
                        .state(state)
                        .postCode(postCode)
                        .deleted(deleted)
                        .medicareNo(0)
                        .centerLinkNo("0")
                        .healthFund(null)
                        .expiryDate(null)
                        .medicareCardName(medicareCardName)
                        .individualReferenceNumber(individualReferenceNo)
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

        // capture what was about to be persisted and make sure that is as expected
        ArgumentCaptor<Client> clientArgumentCaptor = ArgumentCaptor.forClass(Client.class);
        verify(mockClientRepository).save(clientArgumentCaptor.capture());

        assertThat(
                expected,
                (allOf(
                        HasPropertyWithValue.hasProperty("name", equalTo(name)),
                        HasPropertyWithValue.hasProperty("gender", equalTo(gender)),
                        HasPropertyWithValue.hasProperty(
                                "dob",
                                equalTo(
                                        LocalDate.parse(
                                                dob, DateTimeFormatter.ofPattern("yyyy-MM-dd")))),
                        HasPropertyWithValue.hasProperty("email", equalTo(email)),
                        HasPropertyWithValue.hasProperty("phone", equalTo(phone)),
                        HasPropertyWithValue.hasProperty("addrLine1", equalTo(address1)),
                        HasPropertyWithValue.hasProperty("addrLine2", equalTo(address2)),
                        HasPropertyWithValue.hasProperty("city", equalTo(city)),
                        HasPropertyWithValue.hasProperty("state", equalTo(state)),
                        HasPropertyWithValue.hasProperty("postCode", equalTo(postCode)),
                        HasPropertyWithValue.hasProperty("medicareNo", equalTo(medicareNo)),
                        HasPropertyWithValue.hasProperty("centerLinkNo", equalTo(centerLinkNo)),
                        HasPropertyWithValue.hasProperty("expiryDate", equalTo(expiryDate)),
                        HasPropertyWithValue.hasProperty("healthFund", equalTo(healthFund)),
                        HasPropertyWithValue.hasProperty(
                                "medicareCardName", equalTo(medicareCardName)),
                        HasPropertyWithValue.hasProperty(
                                "individualReferenceNumber", equalTo(individualReferenceNo)),
                        HasPropertyWithValue.hasProperty("ndisNumber", equalTo(ndisNumber)),
                        HasPropertyWithValue.hasProperty("photo", equalTo(photo)),
                        HasPropertyWithValue.hasProperty("identity", equalTo(identity)),
                        HasPropertyWithValue.hasProperty("culture", equalTo(culture)),
                        HasPropertyWithValue.hasProperty("language", equalTo(language)),
                        HasPropertyWithValue.hasProperty("diagnosis", equalTo(diagnosis)),
                        HasPropertyWithValue.hasProperty("mobility", equalTo(mobility)),
                        HasPropertyWithValue.hasProperty("communication", equalTo(communication)),
                        HasPropertyWithValue.hasProperty("transportation", equalTo(transportation)),
                        HasPropertyWithValue.hasProperty("supportRatio", equalTo(supportRatio)),
                        HasPropertyWithValue.hasProperty("shiftTimes", equalTo(shiftTimes)),
                        HasPropertyWithValue.hasProperty(
                                "supportWorkerSpecs", equalTo(supportWorkSpecs)),
                        HasPropertyWithValue.hasProperty(
                                "medicationSupport", equalTo(medicationSupport)),
                        HasPropertyWithValue.hasProperty("justiceOrders", equalTo(justiceOrders)),
                        HasPropertyWithValue.hasProperty("deleted", equalTo(deleted)))));
    }

    @SneakyThrows
    @Test
    void testCreateClients_WithoutFields() {

        String dob = "1957-01-01";
        ClientRequest request =
                ClientRequest.builder()
                        .name("Client 1")
                        .dob(dob)
                        .email("client1@client.com")
                        .build();
        // When
        clientService.createClient(request);
        // Then
        Client expected =
                Client.builder()
                        .name("Client 1")
                        .dob(LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .email("client1@client.com")
                        .build();
        // capture what was about to be persisted and make sure that is as expected
        ArgumentCaptor<Client> clientArgumentCaptor = ArgumentCaptor.forClass(Client.class);
        verify(mockClientRepository).save(clientArgumentCaptor.capture());

        assertThat(
                expected,
                (allOf(
                        HasPropertyWithValue.hasProperty("name", equalTo("Client 1")),
                        HasPropertyWithValue.hasProperty(
                                "dob",
                                equalTo(
                                        LocalDate.parse(
                                                dob, DateTimeFormatter.ofPattern("yyyy-MM-dd")))),
                        HasPropertyWithValue.hasProperty("email", equalTo("client1@client.com")))));
    }

    @Test
    void testListAllClients_Success() throws ClientServiceException {
        // Given
        House house1 = House.builder().id(1).build();
        House house2 = House.builder().id(2).build();
        Client client1 =
                Client.builder()
                        .id(1)
                        .name("Client 1")
                        .gender("male")
                        .dob(LocalDate.of(1976, 4, 5))
                        .email("client1@client.com")
                        .phone("0412345678")
                        .addrLine1("abc Line")
                        .addrLine2("martin road")
                        .city("Sydney")
                        .state("Australia")
                        .postCode("2000")
                        .house(house1)
                        .deleted(false)
                        .build();

        Client client2 =
                Client.builder()
                        .id(2)
                        .name("Client 2")
                        .gender("female")
                        .dob(LocalDate.of(1987, 9, 5))
                        .email("client2@client.com")
                        .phone("0412345687")
                        .addrLine1("xyz Line")
                        .addrLine2("abc Road")
                        .city("perth")
                        .state("Australia")
                        .postCode("3000")
                        .house(house2)
                        .deleted(false)
                        .build();

        when(mockClientRepository.findAllByDeleted(eq(false), any(Pageable.class)))
                .thenReturn(List.of(client1, client2));
        ListClientRequest request = ListClientRequest.builder().pageNumber(0).pageSize(1).build();
        // when
        List<ClientListDTO> clients = clientService.listAllClients(request);

        // Then
        assertEquals(2, clients.size());
        assertThat(
                clients,
                (allOf(
                        everyItem(hasProperty("id", anyOf(equalTo(1L), equalTo(2L)))),
                        everyItem(
                                hasProperty(
                                        "name",
                                        anyOf(
                                                equalTo(client1.getName()),
                                                equalTo(client2.getName())))),
                        everyItem(
                                hasProperty(
                                        "gender",
                                        anyOf(
                                                equalTo(client1.getGender()),
                                                equalTo(client2.getGender())))),
                        everyItem(
                                hasProperty(
                                        "dob",
                                        anyOf(
                                                equalTo(client1.getDob()),
                                                equalTo(client2.getDob())))),
                        everyItem(
                                hasProperty(
                                        "email",
                                        anyOf(
                                                equalTo(client1.getEmail()),
                                                equalTo(client2.getEmail())))),
                        everyItem(
                                hasProperty(
                                        "phone",
                                        anyOf(
                                                equalTo(client1.getPhone()),
                                                equalTo(client2.getPhone())))),
                        everyItem(
                                hasProperty(
                                        "addrLine1",
                                        anyOf(
                                                equalTo(client1.getAddrLine1()),
                                                equalTo(client2.getAddrLine1())))),
                        everyItem(
                                hasProperty(
                                        "addrLine2",
                                        anyOf(
                                                equalTo(client1.getAddrLine2()),
                                                equalTo(client2.getAddrLine2())))),
                        everyItem(
                                hasProperty(
                                        "city",
                                        anyOf(
                                                equalTo(client1.getCity()),
                                                equalTo(client2.getCity())))),
                        everyItem(
                                hasProperty(
                                        "state",
                                        anyOf(
                                                equalTo(client1.getState()),
                                                equalTo(client2.getState())))),
                        everyItem(
                                hasProperty(
                                        "deleted",
                                        anyOf(
                                                equalTo(client1.getDeleted()),
                                                equalTo(client2.getDeleted())))),
                        everyItem(
                                hasProperty(
                                        "postCode",
                                        anyOf(
                                                equalTo(client1.getPostCode()),
                                                equalTo(client2.getPostCode())))))));
    }

    @Test
    void testListAllClientsByName_Success() throws ClientServiceException {
        // Given
        House house1 = House.builder().id(1).build();
        House house2 = House.builder().id(2).build();
        Client client1 =
                Client.builder()
                        .id(1)
                        .name("Client 1")
                        .gender("male")
                        .dob(LocalDate.of(1976, 3, 5))
                        .email("client1@client.com")
                        .phone("0412345678")
                        .addrLine1("abcd Line")
                        .addrLine2("martin road")
                        .city("Sydney")
                        .state("Australia")
                        .postCode("2000")
                        .house(house1)
                        .deleted(false)
                        .build();

        Client client2 =
                Client.builder()
                        .id(2)
                        .name("Client 2")
                        .gender("female")
                        .dob(LocalDate.of(1987, 9, 5))
                        .email("client2@client.com")
                        .phone("0412345687")
                        .addrLine1("xyz Line")
                        .addrLine2("abcd Road")
                        .city("perth")
                        .state("Australia")
                        .postCode("3000")
                        .house(house2)
                        .deleted(false)
                        .build();

        when(mockClientRepository
                        .findByNameContainingIgnoreCaseAndDeletedOrHouse_HouseCodeContainingIgnoreCaseAndDeleted(
                                any(), eq(false), any(), eq(false), any(Pageable.class)))
                .thenReturn(List.of(client1));

        ListClientRequest request =
                ListClientRequest.builder()
                        .pageNumber(0)
                        .pageSize(1)
                        .nameOrHouse(client1.getName())
                        .build();

        // When
        List<ClientListDTO> clients = clientService.listAllClients(request);

        // Then
        assertEquals(1, clients.size());
        assertThat(
                clients,
                (allOf(
                        everyItem(hasProperty("id", anyOf(equalTo(1L), equalTo(2L)))),
                        everyItem(
                                hasProperty(
                                        "name",
                                        anyOf(
                                                equalTo(client1.getName()),
                                                equalTo(client2.getName())))),
                        everyItem(
                                hasProperty(
                                        "gender",
                                        anyOf(
                                                equalTo(client1.getGender()),
                                                equalTo(client2.getGender())))),
                        everyItem(
                                hasProperty(
                                        "dob",
                                        anyOf(
                                                equalTo(client1.getDob()),
                                                equalTo(client2.getDob())))),
                        everyItem(
                                hasProperty(
                                        "email",
                                        anyOf(
                                                equalTo(client1.getEmail()),
                                                equalTo(client2.getEmail())))),
                        everyItem(
                                hasProperty(
                                        "phone",
                                        anyOf(
                                                equalTo(client1.getPhone()),
                                                equalTo(client2.getPhone())))),
                        everyItem(
                                hasProperty(
                                        "addrLine1",
                                        anyOf(
                                                equalTo(client1.getAddrLine1()),
                                                equalTo(client2.getAddrLine1())))),
                        everyItem(
                                hasProperty(
                                        "addrLine2",
                                        anyOf(
                                                equalTo(client1.getAddrLine2()),
                                                equalTo(client2.getAddrLine2())))),
                        everyItem(
                                hasProperty(
                                        "city",
                                        anyOf(
                                                equalTo(client1.getCity()),
                                                equalTo(client2.getCity())))),
                        everyItem(
                                hasProperty(
                                        "state",
                                        anyOf(
                                                equalTo(client1.getState()),
                                                equalTo(client2.getState())))),
                        everyItem(
                                hasProperty(
                                        "deleted",
                                        anyOf(
                                                equalTo(client1.getDeleted()),
                                                equalTo(client2.getDeleted())))),
                        everyItem(
                                hasProperty(
                                        "postCode",
                                        anyOf(
                                                equalTo(client1.getPostCode()),
                                                equalTo(client2.getPostCode())))))));
    }

    @SneakyThrows
    @Test
    void testListAllClients_Success_Empty() {
        // Given
        ListClientRequest request = ListClientRequest.builder().pageSize(1).pageNumber(0).build();

        // When
        List<ClientListDTO> clientDTOS = clientService.listAllClients(request);

        // Then
        assertEquals(clientDTOS.size(), 0);
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateClient_Success() {
        long id = 1;
        String name = "test-client";
        String gender = "female";
        String dob = "1957-01-01";
        String email = "test-client@client.com";
        String phone = "9567234509";
        String address1 = "abc Line";
        String address2 = "xyz road";
        String city = "Sydney";
        String state = "Australia";
        String postCode = "2000";
        Boolean deleted = false;
        String ndisNumber = "nd123";

        Client client =
                Client.builder()
                        .id(id)
                        .name("james")
                        .gender("male")
                        .dob(LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .email("captainamerica@marvel.com")
                        .phone("7777777777")
                        .addrLine1("abc Line")
                        .addrLine2("xyz road")
                        .city("Sydney")
                        .state("Australia")
                        .postCode("2000")
                        .deleted(deleted)
                        .ndisNumber("")
                        .build();

        ClientRequest request =
                ClientRequest.builder()
                        .name(name)
                        .gender(gender)
                        .dob(dob)
                        .email(email)
                        .phone(phone)
                        .addrLine1(address1)
                        .addrLine2(address2)
                        .city(city)
                        .state(state)
                        .postCode(postCode)
                        .deleted(true)
                        .ndisNumber(ndisNumber)
                        .build();
        when(mockClientRepository.findById(id)).thenReturn(Optional.of(client));

        // when
        clientService.updateClient(id, request);
        Client expected =
                Client.builder()
                        .id(id)
                        .name(name)
                        .gender(gender)
                        .dob(LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .email(email)
                        .phone(phone)
                        .addrLine1(address1)
                        .addrLine2(address2)
                        .city(city)
                        .state(state)
                        .postCode(postCode)
                        .deleted(true)
                        .ndisNumber(ndisNumber)
                        .build();
        // Then
        verify(mockClientRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateClient_Failure_ClientNotFound() {
        ClientRequest clientRequest =
                ClientRequest.builder()
                        .name("Client 1")
                        .gender("male")
                        .dob("1987-09-12")
                        .email("client1@client.com")
                        .phone("0412345678")
                        .addrLine1("abc Line")
                        .addrLine2("martin road")
                        .city("Sydney")
                        .state("Australia")
                        .postCode("2000")
                        .deleted(false)
                        .ndisNumber("0000")
                        .build();
        assertThrows(
                ClientServiceException.class,
                () -> {
                    // When
                    clientService.updateClient(1L, clientRequest);
                });
    }

    @SneakyThrows
    @Test
    void testCreateClientContact_Success() {
        // Given
        String relation = "relation";
        String designation = "designation";
        String firstName = "firstName";
        String email = "test@gmail.com";
        String lastName = "lastName";
        String address1 = "address1";
        String address2 = "address2";
        String phone1 = "0987654321";
        String phone2 = "1234567890";
        String city = "myCity";
        String state = "myState";
        String postCode = "9087";
        String notes = "contact added";
        String lastUpdatedBy = "joe";
        Instant lastUpdatedAt = context.now();

        ClientContactRequest contactRequest =
                ClientContactRequest.builder()
                        .clientId(1L)
                        .relation(relation)
                        .designation(designation)
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .address1(address1)
                        .address2(address2)
                        .phone1(phone1)
                        .phone2(phone2)
                        .city(city)
                        .state(state)
                        .postCode(postCode)
                        .notes(notes)
                        .lastUpdatedBy(lastUpdatedBy)
                        .build();
        Client client = Client.builder().id(1L).build();
        when(mockClientRepository.findById(1L)).thenReturn(Optional.of(client));

        // When
        clientService.createClientContact(contactRequest);

        // Then
        ClientContact expected =
                ClientContact.builder()
                        .id(1L)
                        .client(client)
                        .relation(relation)
                        .designation(designation)
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .address1(address1)
                        .address2(address2)
                        .phone1(phone1)
                        .phone2(phone2)
                        .city(city)
                        .state(state)
                        .postCode(postCode)
                        .notes(notes)
                        .status(NoticeStatus.ACTIVE)
                        .lastUpdatedBy(lastUpdatedBy)
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();

        // capture what was about to be persisted and make sure that is as expected
        ArgumentCaptor<ClientContact> contactArgumentCaptor =
                ArgumentCaptor.forClass(ClientContact.class);
        verify(mockContactRepository).save(contactArgumentCaptor.capture());
        assertThat(
                expected,
                (allOf(
                        HasPropertyWithValue.hasProperty("client", equalTo(client)),
                        HasPropertyWithValue.hasProperty("relation", equalTo(relation)),
                        HasPropertyWithValue.hasProperty("designation", equalTo(designation)),
                        HasPropertyWithValue.hasProperty("firstName", equalTo(firstName)),
                        HasPropertyWithValue.hasProperty("lastName", equalTo(lastName)),
                        HasPropertyWithValue.hasProperty("email", equalTo(email)),
                        HasPropertyWithValue.hasProperty("address1", equalTo(address1)),
                        HasPropertyWithValue.hasProperty("address2", equalTo(address2)),
                        HasPropertyWithValue.hasProperty("phone1", equalTo(phone1)),
                        HasPropertyWithValue.hasProperty("phone2", equalTo(phone2)),
                        HasPropertyWithValue.hasProperty("city", equalTo(city)),
                        HasPropertyWithValue.hasProperty("state", equalTo(state)),
                        HasPropertyWithValue.hasProperty("postCode", equalTo(postCode)),
                        HasPropertyWithValue.hasProperty("notes", equalTo(notes)),
                        HasPropertyWithValue.hasProperty("lastUpdatedBy", equalTo(lastUpdatedBy)),
                        HasPropertyWithValue.hasProperty(
                                "lastUpdatedAt", equalTo(lastUpdatedAt)))));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testCreateClientContact_Failure_ClientNotFound() {
        // Given
        ClientContactRequest contactRequest =
                ClientContactRequest.builder()
                        .clientId(1L)
                        .relation("relation")
                        .designation("designation")
                        .email("test@gmail.com")
                        .firstName("firstName")
                        .lastName("lastName")
                        .address1("address1")
                        .address2("address2")
                        .phone1("phone1")
                        .phone2("phone2")
                        .city("city")
                        .state("state")
                        .postCode("2345")
                        .notes("notes")
                        .lastUpdatedBy("lastUpdatedBy")
                        .build();

        assertThrows(
                ClientServiceException.class,
                () -> {
                    // When
                    clientService.createClientContact(contactRequest);
                });
    }

    @SneakyThrows
    @Test
    void testGetClientContactById_Success() {
        // Given
        Client client = Client.builder().id(1L).name("client").build();
        ClientContact clientContact =
                ClientContact.builder()
                        .client(client)
                        .relation("father")
                        .designation("business")
                        .email("henry@email.com")
                        .firstName("Henry")
                        .lastName("William")
                        .address1("lane 1")
                        .address2("lane 2")
                        .phone1("90 8745 1134")
                        .phone2("67 5438 9032")
                        .city("myCity")
                        .state("myState")
                        .notes("myNotes")
                        .status(NoticeStatus.ACTIVE)
                        .postCode("9087")
                        .lastUpdatedBy("john")
                        .lastUpdatedAt(context.now())
                        .build();

        when(mockContactRepository.findById(1L)).thenReturn(Optional.of(clientContact));
        ClientContactDTO expected = new ModelMapper().map(clientContact, ClientContactDTO.class);
        // When
        ClientContactDTO actual = clientService.getClientContactById(1L);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void testGetClientContactById_Failure_ContactNotFound() {
        // Given
        long id = 1;

        assertThrows(
                ClientServiceException.class,
                () -> {
                    // When
                    clientService.getClientContactById(id);
                });
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateClientContact_Success() {
        // Given
        long id = 1;
        String relation = "son";
        String designation = "doctor";
        String firstName = "Harry";
        String lastName = "potter";
        String email = "harry@gmail.com";
        String phone1 = "0099887766";
        String phone2 = "1122334455";
        String address1 = "addr1";
        String address2 = "addr2";
        String city = "city1";
        String state = "victoria";
        String postCode = "9887";
        String notes = "notes1";
        String lastUpdatedBy = "John";

        Client client = Client.builder().id(1L).build();
        client = mockClientRepository.save(client);

        ClientContact clientContact =
                ClientContact.builder()
                        .id(1L)
                        .client(client)
                        .relation("relation")
                        .designation("designation")
                        .firstName("firstName")
                        .lastName("lastName")
                        .email("email")
                        .phone1("phone1")
                        .phone2("phone2")
                        .address1("address1")
                        .address2("address2")
                        .city("city")
                        .state("state")
                        .postCode("postCode")
                        .notes("notes")
                        .status(NoticeStatus.ACTIVE)
                        .lastUpdatedBy("lastUpdatedBy")
                        .lastUpdatedAt(context.now())
                        .build();

        UpdateClientContactRequest request =
                UpdateClientContactRequest.builder()
                        .relation(relation)
                        .designation(designation)
                        .firstName(firstName)
                        .lastName(lastName)
                        .email(email)
                        .phone1(phone1)
                        .phone2(phone2)
                        .address1(address1)
                        .address2(address2)
                        .city(city)
                        .state(state)
                        .postCode(postCode)
                        .notes(notes)
                        .status(NoticeStatus.INACTIVE.name())
                        .lastUpdatedBy(lastUpdatedBy)
                        .build();

        when(mockContactRepository.findById(1L)).thenReturn(Optional.of(clientContact));

        // When
        clientService.updateClientContact(1L, request);
        ClientContact expected =
                ClientContact.builder()
                        .id(id)
                        .client(clientContact.getClient())
                        .relation(relation)
                        .designation(designation)
                        .firstName(firstName)
                        .lastName(lastName)
                        .email(email)
                        .phone1(phone1)
                        .phone2(phone2)
                        .address1(address1)
                        .address2(address2)
                        .city(city)
                        .state(state)
                        .postCode(postCode)
                        .notes(notes)
                        .status(NoticeStatus.INACTIVE)
                        .lastUpdatedBy(lastUpdatedBy)
                        .lastUpdatedAt(clientContact.getLastUpdatedAt())
                        .build();
        // then
        verify(mockContactRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateClientContact_Failure_ContactNotFound() {
        UpdateClientContactRequest request =
                UpdateClientContactRequest.builder()
                        .relation("relation")
                        .designation("designation")
                        .firstName("firstName")
                        .lastName("lastName")
                        .email("email")
                        .phone1("phone1")
                        .phone2("phone2")
                        .address1("address1")
                        .address2("address2")
                        .city("city")
                        .state("state")
                        .postCode("postCode")
                        .notes("notes")
                        .status(NoticeStatus.ACTIVE.name())
                        .lastUpdatedBy("lastUpdatedBy")
                        .build();
        assertThrows(
                ClientServiceException.class,
                () -> {

                    // When
                    clientService.updateClientContact(1L, request);
                });
    }

    @SneakyThrows
    @Test
    void testListClientContacts_Success() {
        // Given
        Client client = Client.builder().id(1L).build();
        ClientContact clientContact =
                ClientContact.builder()
                        .id(1L)
                        .client(client)
                        .relation("mother")
                        .designation("doctor")
                        .firstName("june")
                        .lastName("mary")
                        .phone1("5643543210")
                        .phone2("0987890786")
                        .address1("address11")
                        .address2("address22")
                        .city("city00")
                        .state("state00")
                        .postCode("9870")
                        .notes("note")
                        .status(NoticeStatus.ACTIVE)
                        .lastUpdatedBy("paul")
                        .lastUpdatedAt(context.now())
                        .email("june@gmail.com")
                        .build();
        when(mockClientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(mockContactRepository.findAllByClient_Id(client.getId()))
                .thenReturn(List.of(clientContact));

        // When
        List<ClientContactDTO> contactDTOS = clientService.listClientContacts(client.getId());
        // Then
        assertEquals(contactDTOS.size(), 1);
        assertThat(
                contactDTOS,
                (allOf(
                        everyItem(hasProperty("id", anyOf(equalTo(1L)))),
                        everyItem(hasProperty("relation", anyOf(equalTo("mother")))),
                        everyItem(hasProperty("designation", anyOf(equalTo("doctor")))),
                        everyItem(hasProperty("firstName", anyOf(equalTo("june")))),
                        everyItem(hasProperty("lastName", anyOf(equalTo("mary")))),
                        everyItem(hasProperty("phone1", anyOf(equalTo("5643543210")))),
                        everyItem(hasProperty("phone2", anyOf(equalTo("0987890786")))),
                        everyItem(hasProperty("address1", anyOf(equalTo("address11")))),
                        everyItem(hasProperty("address2", anyOf(equalTo("address22")))),
                        everyItem(hasProperty("city", anyOf(equalTo("city00")))),
                        everyItem(hasProperty("state", anyOf(equalTo("state00")))),
                        everyItem(hasProperty("postCode", anyOf(equalTo("9870")))),
                        everyItem(hasProperty("email", anyOf(equalTo("june@gmail.com")))),
                        everyItem(hasProperty("notes", anyOf(equalTo("note")))),
                        everyItem(
                                hasProperty("status", anyOf(equalTo(NoticeStatus.ACTIVE.name())))),
                        everyItem(hasProperty("lastUpdatedBy", anyOf(equalTo("paul")))),
                        everyItem(hasProperty("lastUpdatedAt", anyOf(equalTo(context.now())))))));
    }

    @SneakyThrows
    @Test
    void testListClientContacts_Failure_ClientNotFound() {
        // Given
        long id = 1;
        assertThrows(
                ClientServiceException.class,
                () -> {
                    // When
                    clientService.listClientContacts(id);
                });
    }

    @SneakyThrows
    @Test
    void testCreateFolder_Success() {
        // Given
        String folderName = "Health check-up Record";
        String lastUpdatedBy = "Roy";
        Client client = Client.builder().id(1L).name("mary").build();
        FolderRequest request =
                FolderRequest.builder()
                        .clientId(1L)
                        .folderName("Note")
                        .lastUpdatedBy("Tom")
                        .build();

        Folder folders = Folder.builder().id(1L).client(client).build();
        when(mockClientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(mockFolderRepository.getByStatusAndClientId(
                        NoticeStatus.ACTIVE, request.getClientId()))
                .thenReturn(List.of(folders));

        // when
        clientService.createFolder(request);

        // Then
        Folder expected =
                Folder.builder()
                        .id(1L)
                        .client(client)
                        .folderName(folderName)
                        .lastUpdatedBy(lastUpdatedBy)
                        .build();

        // capture what was about to be persisted and make sure that is as expected
        ArgumentCaptor<Folder> FolderArgumentCaptor = ArgumentCaptor.forClass(Folder.class);
        verify(mockFolderRepository).save(FolderArgumentCaptor.capture());
        assertThat(
                expected,
                (allOf(
                        HasPropertyWithValue.hasProperty("client", equalTo(client)),
                        HasPropertyWithValue.hasProperty(
                                "folderName", equalTo("Health check-up Record")),
                        HasPropertyWithValue.hasProperty("lastUpdatedBy", equalTo("Roy")))));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testCreateFolder_Failure() {
        // Given
        FolderRequest request =
                FolderRequest.builder()
                        .clientId(1L)
                        .folderName("medical records")
                        .lastUpdatedBy("Tom")
                        .build();
        assertThrows(
                ClientServiceException.class,
                () -> {
                    // When
                    clientService.createFolder(request);
                });
    }

    @SneakyThrows
    @Test
    void testGetNotice_Success() {
        // Given
        Client client = Client.builder().id(1).name("test").build();

        Folder folder =
                Folder.builder()
                        .id(1L)
                        .client(client)
                        .status(NoticeStatus.ACTIVE)
                        .folderName("Check")
                        .lastUpdatedBy("Tom")
                        .lastUpdatedAt(context.now())
                        .build();

        when(mockFolderRepository.findById(1L)).thenReturn(Optional.of(folder));
        FolderDTO expected = new ModelMapper().map(folder, FolderDTO.class);

        // When
        FolderDTO actual = clientService.getFolderById(1L);

        // Then
        assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    void testGetFolder_Failure() {
        // Given
        long id = 1;
        // When
        assertThrows(
                ClientServiceException.class,
                () -> {
                    // When
                    clientService.getFolderById(id);
                });
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testListFolders_Success() {
        // Given
        Client client = Client.builder().id(1).name("test").build();

        Folder folder =
                Folder.builder()
                        .id(1L)
                        .client(client)
                        .status(NoticeStatus.ACTIVE)
                        .folderName("Check")
                        .lastUpdatedBy("Tom")
                        .lastUpdatedAt(context.now())
                        .build();

        when(mockClientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(mockFolderRepository.findAllByClient_Id(client.getId())).thenReturn(List.of(folder));

        // When
        List<FolderDTO> folderDTOS = clientService.listFoldersById(client.getId());
        // Then
        assertEquals(folderDTOS.size(), 1);
        assertThat(
                folderDTOS,
                (allOf(
                        everyItem(hasProperty("id", anyOf(equalTo(1L)))),
                        everyItem(hasProperty("folderName", anyOf(equalTo("Check")))),
                        everyItem(
                                hasProperty("status", anyOf(equalTo(NoticeStatus.ACTIVE.name())))),
                        everyItem(hasProperty("lastUpdatedBy", anyOf(equalTo("Tom")))),
                        everyItem(hasProperty("lastUpdatedAt", anyOf(equalTo(context.now())))))));
    }

    @Test
    void testListFolder_Success_Empty() {
        // Given
        long id = 1;

        // When
        assertThrows(
                ClientServiceException.class,
                () -> {
                    // Then
                    clientService.listFoldersById(id);
                });
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateFolder_Success() {
        // Given
        long id = 1;
        String folderName = "upd-folder";
        String lastUpdatedBy = "last";
        Instant lastUpdatedAt = context.now();

        Client client = Client.builder().id(1L).build();
        client = mockClientRepository.save(client);

        Folder folder =
                Folder.builder()
                        .id(id)
                        .client(client)
                        .folderName("folder")
                        .lastUpdatedBy("sam")
                        .status(NoticeStatus.ACTIVE)
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();

        UpdateFolderRequest request =
                UpdateFolderRequest.builder()
                        .id(folder.getId())
                        .folderName(folderName)
                        .status(NoticeStatus.INACTIVE.name())
                        .lastUpdatedBy(lastUpdatedBy)
                        .build();
        when(mockFolderRepository.findById(1L)).thenReturn(Optional.of(folder));

        // When
        clientService.updateFolder(request);
        Folder expected =
                Folder.builder()
                        .id(id)
                        .client(folder.getClient())
                        .folderName(folderName)
                        .status(NoticeStatus.INACTIVE)
                        .lastUpdatedBy(lastUpdatedBy)
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();

        // Then
        verify(mockFolderRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateFolder_Failure_FolderNotFound() {
        // Given
        UpdateFolderRequest request =
                UpdateFolderRequest.builder()
                        .id(2)
                        .folderName("folderName")
                        .status(NoticeStatus.INACTIVE.name())
                        .lastUpdatedBy("lastUpdatedBy")
                        .build();
        assertThrows(
                ClientServiceException.class,
                () -> {

                    // When
                    clientService.updateFolder(request);
                });
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testGetClientById_Success() {
        // Given
        House house2 = House.builder().id(2).build();
        Client client =
                Client.builder()
                        .id(2)
                        .name("Client 2")
                        .gender("female")
                        .dob(LocalDate.of(1987, 9, 5))
                        .email("client2@client.com")
                        .phone("0412345687")
                        .addrLine1("xyz Line")
                        .addrLine2("abcd Road")
                        .city("perth")
                        .state("Australia")
                        .postCode("3000")
                        .house(house2)
                        .deleted(false)
                        .ndisNumber("ND123")
                        .photo("blob")
                        .identity("patient")
                        .culture("indian")
                        .language("Arabic")
                        .diagnosis("Lungs")
                        .mobility("yes")
                        .communication("good response")
                        .medicationSupport("required")
                        .transportation("Car")
                        .justiceOrders("NA")
                        .supportRatio("1:3")
                        .shiftTimes("9-5")
                        .supportWorkerSpecs("NA")
                        .build();

        when(mockClientRepository.findById(1L)).thenReturn(Optional.of(client));
        CloudStorageAccount mockCloudStorageAccount =
                CloudStorageAccount.getDevelopmentStorageAccount();
        try (MockedStatic<CloudStorageAccount> mocked = mockStatic(CloudStorageAccount.class)) {

            mocked.when(() -> CloudStorageAccount.parse(anyString()))
                    .thenReturn(mockCloudStorageAccount);
            CloudStorageAccount storageAccount =
                    CloudStorageAccount.parse("a-mock-connection-string");
            CloudBlobClient cloudBlobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = cloudBlobClient.getContainerReference("test-container");
            SharedAccessBlobPolicy accessBlobPolicy = new SharedAccessBlobPolicy();
            GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

            calendar.add(Calendar.MINUTE, 10);
            accessBlobPolicy.setSharedAccessExpiryTime(calendar.getTime());
            accessBlobPolicy.setPermissions(
                    EnumSet.of(
                            SharedAccessBlobPermissions.READ,
                            SharedAccessBlobPermissions.WRITE,
                            SharedAccessBlobPermissions.LIST));
            CloudBlockBlob blob = container.getBlockBlobReference(client.getPhoto());
            String url =
                    blob.getUri()
                            + "?"
                            + blob.generateSharedAccessSignature(accessBlobPolicy, null);
            ClientDetailDTO expected = new ModelMapper().map(client, ClientDetailDTO.class);
            expected.setPhoto(url);

            // When
            ClientDetailDTO actual = clientService.getClientById(1L);

            // Then
            assertEquals(expected, actual);
        }
    }

    @SneakyThrows
    @Test
    void testGetClientById_Failure() {
        // Given
        long id = 1;
        CloudStorageAccount mockCloudStorageAccount =
                CloudStorageAccount.getDevelopmentStorageAccount();
        try (MockedStatic<CloudStorageAccount> mocked = mockStatic(CloudStorageAccount.class)) {
            mocked.when(() -> CloudStorageAccount.parse(anyString()))
                    .thenReturn(mockCloudStorageAccount);

            assertThrows(
                    ClientServiceException.class,
                    () -> {
                        // When
                        clientService.getClientById(id);
                    });
        }
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateClientById_Success() {
        // Given
        String centerLinkNo = "1A";
        long medicareNo = 1;
        String expiryDate = "2021-03-16";
        House house2 = House.builder().id(2).build();
        String individualReferenceNumber = "J1";
        String medicareCardName = "jessica";
        Client client =
                Client.builder()
                        .id(2)
                        .name("Client 2")
                        .gender("female")
                        .dob(LocalDate.of(1987, 9, 5))
                        .email("client2@client.com")
                        .phone("0412345687")
                        .addrLine1("xyz Line")
                        .addrLine2("abcd Road")
                        .city("perth")
                        .state("Australia")
                        .postCode("3000")
                        .house(house2)
                        .expiryDate(LocalDate.parse("2000-09-09"))
                        .centerLinkNo("15A")
                        .healthFund(true)
                        .medicareNo(18)
                        .deleted(false)
                        .individualReferenceNumber("")
                        .medicareCardName(medicareCardName)
                        .build();

        UpdateClientRequest request =
                UpdateClientRequest.builder()
                        .centerLinkNo(centerLinkNo)
                        .expiryDate(expiryDate)
                        .healthFund(false)
                        .medicareNo(medicareNo)
                        .individualReferenceNumber(individualReferenceNumber)
                        .build();
        when(mockClientRepository.findById(client.getId())).thenReturn(Optional.of(client));

        // when
        clientService.updateClientById(client.getId(), request);

        Client expected =
                Client.builder()
                        .id(2)
                        .name("Client 2")
                        .gender("female")
                        .dob(LocalDate.of(1987, 9, 5))
                        .email("client2@client.com")
                        .phone("0412345687")
                        .addrLine1("xyz Line")
                        .addrLine2("abcd Road")
                        .city("perth")
                        .state("Australia")
                        .postCode("3000")
                        .house(house2)
                        .expiryDate(LocalDate.parse(expiryDate))
                        .centerLinkNo(centerLinkNo)
                        .healthFund(false)
                        .medicareNo(medicareNo)
                        .medicareCardName(medicareCardName)
                        .individualReferenceNumber(individualReferenceNumber)
                        .deleted(false)
                        .build();

        // then
        verify(mockClientRepository).save(eq(expected));
    }
    // need to check if
    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateClientById_Failure_ClientNotFound() {
        String centerLinkNo = "1A";
        long medicareNo = 1;
        String expiryDate = "2021-03-16";
        String individualReferenceNumber = "J1";
        UpdateClientRequest request =
                UpdateClientRequest.builder()
                        .centerLinkNo(centerLinkNo)
                        .expiryDate(expiryDate)
                        .healthFund(false)
                        .medicareNo(medicareNo)
                        .individualReferenceNumber(individualReferenceNumber)
                        .build();
        assertThrows(
                ClientServiceException.class,
                () -> {
                    // When
                    clientService.updateClientById(1L, request);
                });
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateClients_AdditionalField_Success() {
        // Given
        String centerLinkNo = "1A";
        long medicareNo = 1;
        String expiryDate = "2021-03-16";
        House house2 = House.builder().id(2).build();
        String individualReferenceNumber = "J1";
        String medicareCardName = "jessica";
        Client client =
                Client.builder()
                        .id(2)
                        .name("Client 2")
                        .gender("female")
                        .dob(LocalDate.of(1987, 9, 5))
                        .email("client2@client.com")
                        .phone("0412345687")
                        .addrLine1("xyz Line")
                        .addrLine2("abcd Road")
                        .city("perth")
                        .state("Australia")
                        .postCode("3000")
                        .house(house2)
                        .expiryDate(LocalDate.parse(expiryDate))
                        .centerLinkNo(centerLinkNo)
                        .healthFund(true)
                        .medicareNo(medicareNo)
                        .deleted(false)
                        .individualReferenceNumber(individualReferenceNumber)
                        .medicareCardName(medicareCardName)
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

        UpdateClientRequest request =
                UpdateClientRequest.builder()
                        .identity("identity")
                        .culture("culture")
                        .language("language")
                        .diagnosis("diogonosis")
                        .mobility("mobility")
                        .communication("communication")
                        .medicationSupport("medicalSupport")
                        .transportation("transportation")
                        .justiceOrders("justice orders")
                        .supportRatio("support ratio")
                        .shiftTimes("shift times")
                        .supportWorkerSpecs("support work specs")
                        .build();
        when(mockClientRepository.findById(client.getId())).thenReturn(Optional.of(client));

        // when
        clientService.updateClientAdditional(client.getId(), request);

        Client expected =
                Client.builder()
                        .id(2)
                        .name("Client 2")
                        .gender("female")
                        .dob(LocalDate.of(1987, 9, 5))
                        .email("client2@client.com")
                        .phone("0412345687")
                        .addrLine1("xyz Line")
                        .addrLine2("abcd Road")
                        .city("perth")
                        .state("Australia")
                        .postCode("3000")
                        .house(house2)
                        .expiryDate(LocalDate.parse(expiryDate))
                        .centerLinkNo(centerLinkNo)
                        .healthFund(true)
                        .medicareNo(medicareNo)
                        .deleted(false)
                        .individualReferenceNumber(individualReferenceNumber)
                        .medicareCardName(medicareCardName)
                        .ndisNumber("")
                        .photo("")
                        .identity("identity")
                        .culture("culture")
                        .language("language")
                        .diagnosis("diogonosis")
                        .mobility("mobility")
                        .communication("communication")
                        .medicationSupport("medicalSupport")
                        .transportation("transportation")
                        .justiceOrders("justice orders")
                        .supportRatio("support ratio")
                        .shiftTimes("shift times")
                        .supportWorkerSpecs("support work specs")
                        .build();

        // then
        verify(mockClientRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateClient_AdditionalField_Failure_ClientNotFound() {
        UpdateClientRequest request =
                UpdateClientRequest.builder()
                        .identity("identity")
                        .culture("culture")
                        .language("language")
                        .diagnosis("diogonosis")
                        .mobility("mobility")
                        .communication("communication")
                        .medicationSupport("medicalSupport")
                        .transportation("transportation")
                        .justiceOrders("justice orders")
                        .supportRatio("support ratio")
                        .shiftTimes("shift times")
                        .supportWorkerSpecs("support work specs")
                        .build();
        assertThrows(
                ClientServiceException.class,
                () -> {
                    // When
                    clientService.updateClientAdditional(1L, request);
                });
    }

    @SneakyThrows
    @Test
    void testStorePhoto() {
        // Given
        Client client = Client.builder().id(1L).name("client").build();
        InputStream uploadStream = new ByteArrayInputStream("test data".getBytes());
        long fileSize = 100;
        String name = "photo1";
        long clientId = 1;
        String contentType = "image/jpg";
        BlobServiceClient serviceClient = mock(BlobServiceClient.class);
        when(mockBlobServiceClientBuilder.connectionString(any()))
                .thenReturn(mockBlobServiceClientBuilder);
        when(mockBlobServiceClientBuilder.buildClient()).thenReturn(serviceClient);
        BlobContainerClient containerClient = mock(BlobContainerClient.class);
        when(serviceClient.getBlobContainerClient(anyString())).thenReturn(containerClient);
        BlobClient blobClient = mock(BlobClient.class);
        when(containerClient.getBlobClient(anyString())).thenReturn(blobClient);
        when(blobClient.getBlobName()).thenReturn(name);
        when(mockClientRepository.findById(clientId))
                .thenReturn(Optional.of(Client.builder().build()));

        // When
        clientService.storePhoto(uploadStream, fileSize, clientId, client.getName(), contentType);

        // Then
        Client expected = Client.builder().photo("photo1").build();
        ArgumentCaptor<Client> clientArgumentCaptor = ArgumentCaptor.forClass(Client.class);
        verify(mockClientRepository).save(clientArgumentCaptor.capture());
        Client actual = clientArgumentCaptor.getValue();
        assertEquals(expected, actual);
    }
}
