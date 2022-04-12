package com.onedoorway.project.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.*;
import com.onedoorway.project.dto.*;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.*;
import com.onedoorway.project.services.ODWUserDetailsService;
import com.onedoorway.project.util.JwtUtil;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ClientControllerTest {
    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private UserRepository userRepository;

    @Autowired private RoleRepository roleRepository;

    @Autowired private ClientRepository clientRepository;

    @Autowired private HouseRepository houseRepository;

    @Autowired private ClientContactRepository contactRepository;

    @Autowired private FolderRepository folderRepository;

    private Client testClient;

    @BeforeEach
    public void setUp() {
        User user =
                User.builder()
                        .id(1)
                        .email("test@test.com")
                        .password("password")
                        .roles(Set.of((Role.builder().id(1).name("ADMIN").build())))
                        .build();
        userRepository.save(user);

        ODWUserDetails basicUser = new ODWUserDetails(user);
        when(mockJwtUtil.extractUsername(anyString())).thenReturn("test@test.com");
        when(mockJwtUtil.validateToken(anyString(), any(UserDetails.class))).thenReturn(true);
        when(mockUserDetailsService.loadUserByUsername(anyString())).thenReturn(basicUser);
        this.mockMvc =
                webAppContextSetup(this.wac)
                        .addFilters(new JwtRequestFilter(mockUserDetailsService, mockJwtUtil))
                        .build();
    }

    @AfterEach
    public void tearDown() {
        contactRepository.deleteAll();
        folderRepository.deleteAll();
        clientRepository.deleteAll();
        userRepository.deleteAll();
        houseRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create client")
    void testCreateClient_Success() {
        // Given
        ClientRequest request =
                ClientRequest.builder()
                        .name("client")
                        .gender("male")
                        .dob("1966-01-01")
                        .email("client@1client.com")
                        .phone("0987654321")
                        .addrLine1("address 1")
                        .addrLine2("address 2")
                        .city("city")
                        .state("state")
                        .postCode("9089")
                        .deleted(false)
                        .build();
        // When
        mockMvc.perform(
                        post("/client/create")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create client when fields are blank")
    void testCreateClient_WithoutFields() {
        // Given
        String name = "Client";
        String gender = "";
        String dob = "1980-09-08";
        String email = "client@test.com";
        String phone = "";
        String address1 = "Address 1";
        String address2 = "";
        String city = "test-city";
        String state = "test-state";
        String postCode = "0000";
        Boolean deleted = false;

        ClientRequest clientRequest =
                ClientRequest.builder()
                        .name(name)
                        .dob(dob)
                        .email(email)
                        .addrLine1(address1)
                        .city(city)
                        .state(state)
                        .postCode(postCode)
                        .deleted(deleted)
                        .build();
        // When
        mockMvc.perform(
                        post("/client/create")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(clientRequest)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("POST / get all clients")
    void testListClients() {
        // Given
        LocalDate dob = LocalDate.of(1900, 8, 11);
        House house = House.builder().houseCode("700").build();
        testClient =
                Client.builder()
                        .id(2)
                        .name("client")
                        .gender("male")
                        .dob(dob)
                        .email("client@1client.com")
                        .phone("0987654321")
                        .addrLine1("address 1")
                        .addrLine2("address 2")
                        .city("city")
                        .state("state")
                        .postCode("9099")
                        .house(house)
                        .deleted(false)
                        .build();
        testClient = clientRepository.save(testClient);

        ListClientRequest request =
                ListClientRequest.builder()
                        .pageNumber(0)
                        .pageSize(1)
                        .nameOrHouse(testClient.getName())
                        .build();
        // When

        mockMvc.perform(
                        post("/client/list")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clients[0].id").value(testClient.getId()))
                .andExpect(jsonPath("$.clients[0].name").value(testClient.getName()))
                .andExpect(jsonPath("$.clients[0].gender").value(testClient.getGender()))
                .andExpect(
                        jsonPath("$.clients[0].dob")
                                .value(dob.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .andExpect(jsonPath("$.clients[0].email").value(testClient.getEmail()))
                .andExpect(jsonPath("$.clients[0].phone").value(testClient.getPhone()))
                .andExpect(jsonPath("$.clients[0].addrLine1").value(testClient.getAddrLine1()))
                .andExpect(jsonPath("$.clients[0].addrLine2").value(testClient.getAddrLine2()))
                .andExpect(jsonPath("$.clients[0].city").value(testClient.getCity()))
                .andExpect(jsonPath("$.clients[0].state").value(testClient.getState()))
                .andExpect(jsonPath("$.clients[0].postCode").value(testClient.getPostCode()))
                .andExpect(jsonPath("$.clients[0].deleted").value(false))
                .andExpect(jsonPath("$.totalClients", is(1)));
    }

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT/ update client")
    void testUpdateClientSuccess() {

        String name = "test-client";
        String gender = "female";
        String dob = "1966-01-01";
        String email = "test-client@client.com";
        String phone = "4512684598";
        String addrLine1 = "Queen street";
        String addrLine2 = "abc road";
        String city = "Melbourne";
        String state = "Australia";
        String postCode = "3000";
        Boolean deleted = true;
        String ndisNumber = "nd123";

        testClient =
                Client.builder()
                        .name("client")
                        .gender("male")
                        .dob(LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .email("test-client@email.com")
                        .phone(phone)
                        .addrLine1("Bay street")
                        .addrLine2("xyz road")
                        .city("city")
                        .state("state")
                        .postCode("2000")
                        .deleted(false)
                        .ndisNumber("")
                        .build();
        testClient = clientRepository.save(testClient);

        ClientRequest request =
                ClientRequest.builder()
                        .name(name)
                        .gender(gender)
                        .dob(dob)
                        .email(email)
                        .phone(phone)
                        .addrLine1(addrLine1)
                        .addrLine2(addrLine2)
                        .city(city)
                        .state(state)
                        .postCode(postCode)
                        .deleted(deleted)
                        .ndisNumber(ndisNumber)
                        .build();

        // When
        mockMvc.perform(
                        put("/client/update/{id}", testClient.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                //                //Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT/ update client failure")
    void testUpdateClient_Failure() {

        String name = "test-client";
        String gender = "female";
        String dob = "1957-01-01";
        String email = "test-client@client.com";
        String phone = "9567234509";
        String addrLine1 = "abc Line";
        String addrLine2 = "xyz road";
        String city = "Sydney";
        String state = "Australia";
        String postCode = "2000";
        Boolean deleted = false;
        String ndisNumber = "nd123";

        ClientRequest request =
                ClientRequest.builder()
                        .name(name)
                        .gender(gender)
                        .dob(dob)
                        .email(email)
                        .phone(phone)
                        .addrLine1(addrLine1)
                        .addrLine2(addrLine2)
                        .city(city)
                        .state(state)
                        .postCode(postCode)
                        .deleted(deleted)
                        .ndisNumber(ndisNumber)
                        .build();
        // When
        mockMvc.perform(
                        put("/client/update/{id}", 1000)
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                // Then
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("GET/ get client contact")
    void testGetClientContactSuccess() {
        Instant lastUpdatedAt = Instant.now();
        Client client = Client.builder().name("client").build();
        clientRepository.save(client);

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
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();
        contactRepository.save(clientContact);

        // When
        mockMvc.perform(
                        get("/client/get/contact/{id}", clientContact.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientContact.getId()))
                .andExpect(jsonPath("$.clientName").value(clientContact.getClient().getName()))
                .andExpect(jsonPath("$.relation").value(clientContact.getRelation()))
                .andExpect(jsonPath("$.designation").value(clientContact.getDesignation()))
                .andExpect(jsonPath("$.firstName").value(clientContact.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(clientContact.getLastName()))
                .andExpect(jsonPath("$.email").value(clientContact.getEmail()))
                .andExpect(jsonPath("$.address1").value(clientContact.getAddress1()))
                .andExpect(jsonPath("$.address2").value(clientContact.getAddress2()))
                .andExpect(jsonPath("$.phone1").value(clientContact.getPhone1()))
                .andExpect(jsonPath("$.phone2").value(clientContact.getPhone2()))
                .andExpect(jsonPath("$.city").value(clientContact.getCity()))
                .andExpect(jsonPath("$.state").value(clientContact.getState()))
                .andExpect(jsonPath("$.status").value(clientContact.getStatus().name()))
                .andExpect(jsonPath("$.postCode").value(clientContact.getPostCode()))
                .andExpect(jsonPath("$.lastUpdatedBy").value(clientContact.getLastUpdatedBy()))
                .andExpect(
                        jsonPath("$.lastUpdatedAt")
                                .value(
                                        lastUpdatedAt
                                                .atOffset(ZoneOffset.UTC)
                                                .format(
                                                        DateTimeFormatter.ofPattern(
                                                                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))))
                .andExpect(jsonPath("$.notes").value(clientContact.getNotes()));
    }

    @SneakyThrows
    @Test
    @DisplayName("GET/ get client contact failure")
    void testGetClientContact_Failure() {
        Instant lastUpdatedAt = Instant.now();
        Client client = Client.builder().name("client").build();
        clientRepository.save(client);

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
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();
        contactRepository.save(clientContact);

        // When
        mockMvc.perform(
                        get("/client/get/contact/{id}", 500)
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create client contact")
    void testCreateClientContact_Success() {
        // Given
        testClient = Client.builder().name("client").build();

        testClient = clientRepository.save(testClient);

        ClientContactRequest request =
                ClientContactRequest.builder()
                        .clientId(testClient.getId())
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

        // When
        mockMvc.perform(
                        post("/client/create-contact")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create client contact when fields are blank")
    void testCreateClientContact_WithoutFields() {
        // Given
        testClient = Client.builder().name("client").build();
        testClient = clientRepository.save(testClient);
        ClientContactRequest request =
                ClientContactRequest.builder()
                        .clientId(testClient.getId())
                        .relation("relation")
                        .designation("designation")
                        .email("test@gmail.com")
                        .firstName("firstName")
                        .build();
        // When
        mockMvc.perform(
                        post("/client/create-contact")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT/ update client contact")
    void testUpdateClientContactSuccess() {
        // Given
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
        client = clientRepository.save(client);

        ClientContact clientContact =
                ClientContact.builder()
                        .client(client)
                        .relation("daughter")
                        .designation("teacher")
                        .firstName("jane")
                        .email("jane@gmail.com")
                        .lastName("austen")
                        .phone1("5544332211")
                        .phone2("7788990066")
                        .address1("myaddress")
                        .address2("address22")
                        .city("updCity")
                        .state("updState")
                        .postCode("0000")
                        .notes("updNotes")
                        .status(NoticeStatus.ACTIVE)
                        .lastUpdatedBy("jeena")
                        .lastUpdatedAt(Instant.now())
                        .build();
        clientContact = contactRepository.save(clientContact);

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
        // When
        mockMvc.perform(
                        put("/client/update/contact/{id}", clientContact.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT/ update client contact")
    void testUpdateClientContact_Failure() {
        // Given
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
        client = clientRepository.save(client);

        ClientContact clientContact =
                ClientContact.builder()
                        .client(client)
                        .relation("daughter")
                        .designation("teacher")
                        .firstName("jane")
                        .email("jane@gmail.com")
                        .lastName("austen")
                        .phone1("5544332211")
                        .phone2("7788990066")
                        .address1("myaddress")
                        .address2("address22")
                        .city("updCity")
                        .state("updState")
                        .postCode("0000")
                        .notes("updNotes")
                        .status(NoticeStatus.ACTIVE)
                        .lastUpdatedBy("jeena")
                        .lastUpdatedAt(Instant.now())
                        .build();
        contactRepository.save(clientContact);

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
        // When
        mockMvc.perform(
                        put("/client/update/contact/{id}", 600)
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)));
    }

    @SneakyThrows
    @Test
    @DisplayName("GET/ list client contacts")
    void testListClientContactsSuccess() {
        // Given
        long id = 1;
        Client client = Client.builder().id(id).name("client1").build();
        client = clientRepository.save(client);

        ClientContact clientContact =
                ClientContact.builder()
                        .id(2)
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
                        .notes("notes1")
                        .status(NoticeStatus.ACTIVE)
                        .lastUpdatedBy("paul")
                        .lastUpdatedAt(Instant.now())
                        .email("june@gmail.com")
                        .build();
        clientContact = contactRepository.save(clientContact);

        // When
        mockMvc.perform(
                        get("/client/list/contact/{clientId}", client.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].firstName").value(clientContact.getFirstName()))
                .andExpect(jsonPath("$.[0].lastName").value(clientContact.getLastName()))
                .andExpect(jsonPath("$.[0].address1").value(clientContact.getAddress1()))
                .andExpect(jsonPath("$.[0].address2").value(clientContact.getAddress2()))
                .andExpect(jsonPath("$.[0].email").value(clientContact.getEmail()))
                .andExpect(jsonPath("$.[0].phone1").value(clientContact.getPhone1()))
                .andExpect(jsonPath("$.[0].phone2").value(clientContact.getPhone2()))
                .andExpect(jsonPath("$.[0].city").value(clientContact.getCity()))
                .andExpect(jsonPath("$.[0].designation").value(clientContact.getDesignation()))
                .andExpect(jsonPath("$.[0].relation").value(clientContact.getRelation()))
                .andExpect(jsonPath("$.[0].state").value(clientContact.getState()))
                .andExpect(jsonPath("$.[0].notes").value(clientContact.getNotes()))
                .andExpect(jsonPath("$.[0].status").value(clientContact.getStatus().name()))
                .andExpect(jsonPath("$.[0].postCode").value(clientContact.getPostCode()))
                .andExpect(jsonPath("$.[0].lastUpdatedBy").value(clientContact.getLastUpdatedBy()))
                .andExpect(jsonPath("$.[0].id").value(clientContact.getId()))
                .andExpect(
                        jsonPath("$.[0].lastUpdatedAt")
                                .value(
                                        clientContact
                                                .getLastUpdatedAt()
                                                .atOffset(ZoneOffset.UTC)
                                                .format(
                                                        DateTimeFormatter.ofPattern(
                                                                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))));
    }

    @SneakyThrows
    @Test
    @DisplayName("GET/ list client contacts failure")
    void testListClientContacts_Failure() {
        // Given
        long id = 1;
        Client client = Client.builder().id(id).name("client1").build();
        client = clientRepository.save(client);

        ClientContact clientContact =
                ClientContact.builder()
                        .id(2)
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
                        .notes("notes1")
                        .status(NoticeStatus.ACTIVE)
                        .lastUpdatedBy("paul")
                        .lastUpdatedAt(Instant.now())
                        .email("june@gmail.com")
                        .build();
        contactRepository.save(clientContact);

        // When
        mockMvc.perform(
                        get("/client/list/contact/{clientId}", 290)
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)));
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create folder")
    void testCreateFolder_Success() {
        // Given
        testClient = Client.builder().name("client").build();
        testClient = clientRepository.save(testClient);

        FolderRequest request =
                FolderRequest.builder()
                        .clientId(testClient.getId())
                        .folderName("Note")
                        .lastUpdatedBy("Tom")
                        .build();
        // When
        mockMvc.perform(
                        post("/client/create-folder")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create folder when fields are blank")
    void testCreateFolder_WithoutFields() {
        // Given
        testClient = Client.builder().name("client").build();
        testClient = clientRepository.save(testClient);

        FolderRequest request =
                FolderRequest.builder().clientId(testClient.getId()).lastUpdatedBy("Tom").build();
        // When
        mockMvc.perform(
                        post("/client/create-folder")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("GET / get folder")
    void testGetFolder_Success() {
        // Given
        Client client = Client.builder().id(1).name("test").build();
        client = clientRepository.save(client);
        Instant lastUpdatedAt = Instant.now();

        Folder folder =
                Folder.builder()
                        .id(1L)
                        .client(client)
                        .status(NoticeStatus.ACTIVE)
                        .folderName("Check")
                        .lastUpdatedBy("Tom")
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();
        folder = folderRepository.save(folder);

        // When
        mockMvc.perform(
                        get("/client/get/folder/{id}", folder.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(folder.getId()))
                .andExpect(jsonPath("$.status").value(folder.getStatus().name()))
                .andExpect(jsonPath("$.folderName").value(folder.getFolderName()))
                .andExpect(jsonPath("$.lastUpdatedBy").value(folder.getLastUpdatedBy()))
                .andExpect(
                        jsonPath("$.lastUpdatedAt")
                                .value(
                                        lastUpdatedAt
                                                .atOffset(ZoneOffset.UTC)
                                                .format(
                                                        DateTimeFormatter.ofPattern(
                                                                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))));
    }

    @SneakyThrows
    @Test
    @DisplayName("GET /list Folders")
    void testListFoldersSuccess() {
        // Given
        Client client = Client.builder().id(1).name("test").build();
        client = clientRepository.save(client);
        Instant lastUpdatedAt = Instant.now();

        Folder folder =
                Folder.builder()
                        .id(1L)
                        .client(client)
                        .status(NoticeStatus.ACTIVE)
                        .folderName("Check")
                        .lastUpdatedBy("Tom")
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();
        folder = folderRepository.save(folder);

        // When
        mockMvc.perform(
                        get("/client/list/folders/{clientId}", client.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(folder.getId()))
                .andExpect(jsonPath("$.[0].status").value(folder.getStatus().name()))
                .andExpect(jsonPath("$.[0].folderName").value(folder.getFolderName()))
                .andExpect(jsonPath("$.[0].lastUpdatedBy").value(folder.getLastUpdatedBy()))
                .andExpect(
                        jsonPath("$.[0].lastUpdatedAt")
                                .value(
                                        lastUpdatedAt
                                                .atOffset(ZoneOffset.UTC)
                                                .format(
                                                        DateTimeFormatter.ofPattern(
                                                                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))));
    }

    @SneakyThrows
    @Test
    @DisplayName("GET /list Folders failure")
    void testListFoldersFailure() {
        // Given
        Client client = Client.builder().id(1).name("test").build();
        client = clientRepository.save(client);

        Folder folder =
                Folder.builder()
                        .id(1L)
                        .client(client)
                        .status(NoticeStatus.ACTIVE)
                        .folderName("Check")
                        .lastUpdatedBy("Tom")
                        .lastUpdatedAt(Instant.now())
                        .build();
        folder = folderRepository.save(folder);

        // When
        mockMvc.perform(
                        get("/client/list/folders/{clientId}", 500)
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))

                // Then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT/ update folder")
    void testUpdateFolder() {
        // Given
        String folderName = "upd-folder";
        String status = "INACTIVE";
        String lastUpdatedBy = "last";
        Instant lastUpdatedAt = Instant.now();

        Client client = Client.builder().id(1L).build();
        client = clientRepository.save(client);

        Folder folder =
                Folder.builder()
                        .id(1L)
                        .client(client)
                        .folderName("folder")
                        .lastUpdatedBy("sam")
                        .status(NoticeStatus.ACTIVE)
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();
        folder = folderRepository.save(folder);

        UpdateFolderRequest request =
                UpdateFolderRequest.builder()
                        .id(folder.getId())
                        .folderName(folderName)
                        .lastUpdatedBy(status)
                        .lastUpdatedBy(lastUpdatedBy)
                        .build();

        // When
        mockMvc.perform(
                        put("/client/update-folder")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT/ update folder failure")
    void testUpdateFolder_Failure() {
        UpdateFolderRequest request =
                UpdateFolderRequest.builder()
                        .id(20)
                        .folderName("folderName")
                        .lastUpdatedBy("status")
                        .lastUpdatedBy("lastUpdatedBy")
                        .build();

        // When
        mockMvc.perform(
                        put("/client/update-folder")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)));
    }

    @SneakyThrows
    @Test
    @DisplayName("GET /ClientById")
    void testListClientByIdSuccess() {
        // Given
        Client client =
                Client.builder()
                        .id(1)
                        .name("test")
                        .expiryDate(null)
                        .deleted(null)
                        .healthFund(false)
                        .medicareNo(0)
                        .ndisNumber("ND123")
                        .photo("URL")
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
                        .photo("blob")
                        .build();
        client = clientRepository.save(client);
        CloudStorageAccount storageAccount = CloudStorageAccount.getDevelopmentStorageAccount();
        try (MockedStatic<CloudStorageAccount> mocked = mockStatic(CloudStorageAccount.class)) {
            mocked.when(() -> CloudStorageAccount.parse("test-connection-string"))
                    .thenReturn(storageAccount);
            CloudBlobClient cloudBlobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = cloudBlobClient.getContainerReference("test-container");

            CloudBlockBlob blob = container.getBlockBlobReference(client.getPhoto());
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

            String url = blob.getUri() + "?" + sas;

            // When
            mockMvc.perform(
                            get("/client/get/{clientId}", client.getId())
                                    .header("Authorization", "Bearer dummy")
                                    .accept(MediaType.APPLICATION_JSON)
                                    .contentType(MediaType.APPLICATION_JSON))
                    // Then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(client.getId()))
                    .andExpect(jsonPath("$.email").value(client.getEmail()))
                    .andExpect(jsonPath("$.name").value(client.getName()))
                    .andExpect(jsonPath("$.gender").value(client.getGender()))
                    .andExpect(jsonPath("$.dob").value(client.getDob()))
                    .andExpect(jsonPath("$.phone").value(client.getPhone()))
                    .andExpect(jsonPath("$.addrLine1").value(client.getAddrLine1()))
                    .andExpect(jsonPath("$.addrLine2").value(client.getAddrLine2()))
                    .andExpect(jsonPath("$.city").value(client.getCity()))
                    .andExpect(jsonPath("$.state").value(client.getState()))
                    .andExpect(jsonPath("$.postCode").value(client.getPostCode()))
                    .andExpect(jsonPath("$.house").value(client.getHouse()))
                    .andExpect(jsonPath("$.deleted").value(client.getDeleted()))
                    .andExpect(jsonPath("$.medicareNo").value(client.getMedicareNo()))
                    .andExpect(jsonPath("$.centerLinkNo").value(client.getCenterLinkNo()))
                    .andExpect(jsonPath("$.healthFund").value(client.getHealthFund()))
                    .andExpect(
                            jsonPath("$.individualReferenceNumber")
                                    .value(client.getIndividualReferenceNumber()))
                    .andExpect(jsonPath("$.medicareCardName").value(client.getMedicareCardName()))
                    .andExpect(jsonPath("$.expiryDate").value(client.getExpiryDate()))
                    .andExpect(jsonPath("$.ndisNumber").value(client.getNdisNumber()))
                    .andExpect(jsonPath("$.photo").value(url))
                    .andExpect(jsonPath("$.identity").value(client.getIdentity()))
                    .andExpect(jsonPath("$.culture").value(client.getCulture()))
                    .andExpect(jsonPath("$.language").value(client.getLanguage()))
                    .andExpect(jsonPath("$.diagnosis").value(client.getDiagnosis()))
                    .andExpect(jsonPath("$.mobility").value(client.getMobility()))
                    .andExpect(jsonPath("$.communication").value(client.getCommunication()))
                    .andExpect(jsonPath("$.medicationSupport").value(client.getMedicationSupport()))
                    .andExpect(jsonPath("$.transportation").value(client.getTransportation()))
                    .andExpect(jsonPath("$.justiceOrders").value(client.getJusticeOrders()))
                    .andExpect(jsonPath("$.supportRatio").value(client.getSupportRatio()))
                    .andExpect(jsonPath("$.shiftTimes").value(client.getShiftTimes()))
                    .andExpect(
                            jsonPath("$.supportWorkerSpecs").value(client.getSupportWorkerSpecs()));
        }
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT/ update client")
    void testUpdateClientById_Success() {

        String dob = "1966-01-01";
        String phone = "4512684598";
        String centerLinkNo = "1A";
        long medicareNo = 1;
        Boolean healthFund = false;
        String expiryDate = "2021-03-16";
        String individualReferenceNo = "";
        String medicalCardName = "jessica";

        testClient =
                Client.builder()
                        .name("client")
                        .gender("male")
                        .dob(LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .email("test-client@email.com")
                        .phone(phone)
                        .addrLine1("Bay street")
                        .addrLine2("xyz road")
                        .city("city")
                        .state("state")
                        .postCode("2000")
                        .deleted(false)
                        .build();
        testClient = clientRepository.save(testClient);

        UpdateClientRequest request =
                UpdateClientRequest.builder()
                        .centerLinkNo(centerLinkNo)
                        .expiryDate(expiryDate)
                        .healthFund(healthFund)
                        .medicareNo(medicareNo)
                        .medicareCardName(medicalCardName)
                        .individualReferenceNumber(individualReferenceNo)
                        .build();

        // When
        mockMvc.perform(
                        put("/client/update/medicare/{id}", testClient.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                //                //Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT/ update client failure")
    void testUpdateClientById_Failure() {

        String centerLinkNo = "1A";
        String expiryDate = "2021-03-16";

        UpdateClientRequest request =
                UpdateClientRequest.builder()
                        .centerLinkNo(centerLinkNo)
                        .expiryDate(expiryDate)
                        .build();
        // When
        mockMvc.perform(
                        put("/client/update/medicare/{id}", 100)
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                // Then
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT/ update client additional field")
    void testUpdateClient_AdditionalField_Success() {

        String dob = "1966-01-01";
        String phone = "4512684598";

        testClient =
                Client.builder()
                        .name("client")
                        .gender("male")
                        .dob(LocalDate.parse(dob, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .email("test-client@email.com")
                        .phone(phone)
                        .addrLine1("Bay street")
                        .addrLine2("xyz road")
                        .city("city")
                        .state("state")
                        .postCode("2000")
                        .deleted(false)
                        .build();
        testClient = clientRepository.save(testClient);

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

        // When
        mockMvc.perform(
                        put("/client/update-additional/{id}", testClient.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                //                //Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT/ update client Additional field failure")
    void testUpdateClientById_Additionalfield_Failure() {

        UpdateClientRequest request = UpdateClientRequest.builder().identity("123").build();
        // When
        mockMvc.perform(
                        put("/client/update-additional/{id}", 100)
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                // Then
                .andExpect(status().isBadRequest());
    }
}
