package com.onedoorway.project.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onedoorway.project.dto.*;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.*;
import com.onedoorway.project.services.ODWUserDetailsService;
import com.onedoorway.project.util.JwtUtil;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class HouseControllerTest {

    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private UserRepository userRepository;

    @Autowired private RoleRepository roleRepository;

    @Autowired private ClientRepository clientRepository;

    @Autowired private HouseRepository houseRepository;

    @Autowired private HouseContactRepository houseContactRepository;

    private House testHouse;

    @BeforeEach
    public void setUp() {
        testHouse =
                House.builder()
                        .id(1)
                        .houseCode("100")
                        .phone("0412345678")
                        .addrLine1("abc Line")
                        .addrLine2("martin road")
                        .city("Sydney")
                        .state("Australia")
                        .deleted(false)
                        .postCode("2000")
                        .build();
        testHouse = houseRepository.save(testHouse);

        User user =
                User.builder()
                        .id(1)
                        .email("test@test.com")
                        .password("password")
                        .roles(Set.of((Role.builder().id(1).name("ADMIN").build())))
                        .houses(Set.of(testHouse))
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
        houseContactRepository.deleteAll();
        clientRepository.deleteAll();
        userRepository.deleteAll();
        houseRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create house")
    void testCreateHouseSuccess() {
        // Given
        String houseCode = "500";
        String phone = "01 1114 6789";
        String address1 = "Address 1";
        String address2 = "Address 2";
        String city = "test-city";
        String state = "test-state";
        String postCode = "0000";

        AddHouseRequest addHouseRequest =
                AddHouseRequest.builder()
                        .houseCode(houseCode)
                        .phone(phone)
                        .addrLine1(address1)
                        .addrLine2(address2)
                        .city(city)
                        .state(state)
                        .deleted(false)
                        .postCode(postCode)
                        .build();
        // When
        mockMvc.perform(
                        post("/house/create")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(addHouseRequest)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create house")
    void testCreateHouseFailure_DuplicateHouseCode() {
        // Given
        String houseCode = "510";
        String phone = "01 1114 6789";
        String address1 = "Address 1";
        String address2 = "Address 2";
        String city = "test-city";
        String state = "test-state";
        String postCode = "0000";

        AddHouseRequest addHouseRequest =
                AddHouseRequest.builder()
                        .houseCode(houseCode)
                        .phone(phone)
                        .addrLine1(address1)
                        .addrLine2(address2)
                        .city(city)
                        .state(state)
                        .deleted(false)
                        .postCode(postCode)
                        .build();

        House house = House.builder().houseCode(houseCode).build();
        houseRepository.save(house);

        // When
        mockMvc.perform(
                        post("/house/create")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(addHouseRequest)))
                // Then
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create house when phone and address is blank")
    void testCreateHouse_WithoutFields() {
        // Given
        String houseCode = "501";
        String phone = "";
        String address1 = "Address 1";
        String address2 = "";
        String city = "test-city";
        String state = "test-state";
        String postCode = "0000";

        AddHouseRequest addHouseRequest =
                AddHouseRequest.builder()
                        .houseCode(houseCode)
                        .addrLine1(address1)
                        .city(city)
                        .state(state)
                        .deleted(false)
                        .postCode(postCode)
                        .build();
        // When
        mockMvc.perform(
                        post("/house/create")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(addHouseRequest)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /add clients to house")
    void testAddClientsSuccess() {
        String clientName = "Alice";
        Client client = Client.builder().name(clientName).build();
        clientRepository.save(client);
        String houseCode = "102";
        testHouse = House.builder().id(20).houseCode(houseCode).build();
        houseRepository.save(testHouse);

        AddClientRequest addClientRequest =
                AddClientRequest.builder().clientId(client.getId()).houseCode(houseCode).build();

        // When
        mockMvc.perform(
                        post("/house/add-client")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(addClientRequest)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /add clients to house failure")
    void testAddClients_Failure() {

        String clientName = "Alice";
        Client client = Client.builder().name(clientName).build();
        clientRepository.save(client);

        AddClientRequest addClientRequest =
                AddClientRequest.builder().clientId(client.getId()).houseCode("240").build();

        // When
        mockMvc.perform(
                        post("/house/add-client")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(addClientRequest)))
                // Then
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /fetch clients")
    void testFetchClientsSuccess() {
        // Given
        String houseCode = "102";

        Client client =
                Client.builder()
                        .id(3)
                        .name("test")
                        .house(House.builder().id(3).houseCode(houseCode).build())
                        .build();
        client = clientRepository.save(client);
        GetClientsByHouseRequest request =
                GetClientsByHouseRequest.builder().houseCode(houseCode).build();

        // When
        mockMvc.perform(
                        post("/house/fetch-clients")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name").value(client.getName()))
                .andExpect(jsonPath("$.[0].id").value(client.getId()));
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /fetch house")
    void testFetchHousesSuccess() {
        String houseCode = "104";
        House house = House.builder().id(4).houseCode(houseCode).build();
        houseRepository.save(house);
        testHouse.setHouseCode(houseCode);
        GetHouseByCodeRequest request =
                GetHouseByCodeRequest.builder().houseCode(testHouse.getHouseCode()).build();

        // When
        mockMvc.perform(
                        post("/house/get")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.houseCode", is(houseCode)));
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /add user to house")
    void testAddUserToHouse() {
        // Given
        String houseCode = "300";
        testHouse = House.builder().id(1).houseCode(houseCode).build();
        testHouse = houseRepository.save(testHouse);

        User user = User.builder().id(1).email("addhtest@test.com").password("password").build();
        user = userRepository.save(user);
        UserToHouseRequest userToHouseRequest =
                UserToHouseRequest.builder().userId(user.getId()).houseCode(houseCode).build();
        // When
        mockMvc.perform(
                        post("/house/add-user")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(userToHouseRequest)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("DELETE /remove client from house")
    void testRemoveClient() {
        // Given
        String houseCode = "107";

        Client client = Client.builder().name("test").build();

        testHouse = House.builder().houseCode(houseCode).clients(List.of(client)).build();
        houseRepository.save(testHouse);
        client.setHouse(testHouse);
        clientRepository.save(client);

        RemoveClientRequest removeClientRequest =
                RemoveClientRequest.builder().clientId(client.getId()).houseCode(houseCode).build();

        // When
        mockMvc.perform(
                        delete("/house/remove-client")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(removeClientRequest)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("DELETE /remove client from house failure")
    void testRemoveClient_NonExistentHouse() {
        // Given
        String houseCode = "108";

        Client client = Client.builder().name("test").build();
        clientRepository.save(client);

        RemoveClientRequest removeClientRequest =
                RemoveClientRequest.builder().clientId(client.getId()).houseCode(houseCode).build();

        // When
        mockMvc.perform(
                        delete("/house/remove-client")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(removeClientRequest)))
                // Then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)));
    }

    @SneakyThrows
    @Test
    @DisplayName("GET / get all houses")
    void testListHouses() {
        // When

        mockMvc.perform(
                        get("/house/list")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(testHouse.getId()))
                .andExpect(jsonPath("$.[0].houseCode").value(testHouse.getHouseCode()))
                .andExpect(jsonPath("$.[0].phone").value(testHouse.getPhone()))
                .andExpect(jsonPath("$.[0].addrLine1").value(testHouse.getAddrLine1()))
                .andExpect(jsonPath("$.[0].addrLine2").value(testHouse.getAddrLine2()))
                .andExpect(jsonPath("$.[0].city").value(testHouse.getCity()))
                .andExpect(jsonPath("$.[0].state").value(testHouse.getState()))
                .andExpect(jsonPath("$.[0].deleted").value(testHouse.getDeleted()))
                .andExpect(jsonPath("$.[0].totalUsers").value(0))
                .andExpect(jsonPath("$.[0].totalClients").value(0))
                .andExpect(jsonPath("$.[0].postCode").value(testHouse.getPostCode()));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT/ update house")
    void testUpdateHouseSuccess() {
        String phone = "12 3456 7890";
        String address1 = "Updated Address 1";
        String address2 = "Updated Address 2";
        String city = "Updated city";
        String state = "Updated state";
        String postCode = "0000";
        testHouse =
                House.builder()
                        .addrLine1("Address 1")
                        .addrLine2("Address 2")
                        .city("city")
                        .houseCode("103")
                        .phone("09 7865 4321")
                        .state("state")
                        .deleted(false)
                        .postCode("1111")
                        .build();
        testHouse = houseRepository.save(testHouse);

        AddHouseRequest request =
                AddHouseRequest.builder()
                        .addrLine1(address1)
                        .addrLine2(address2)
                        .city(city)
                        .houseCode(testHouse.getHouseCode())
                        .phone(phone)
                        .state(state)
                        .deleted(false)
                        .postCode(postCode)
                        .build();
        // When
        mockMvc.perform(
                        put("/house/update/{houseId}", testHouse.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT/ update house failure")
    void testUpdateHouse_Failure() {
        String phone = "12 3456 7890";
        String address1 = "Updated Address 1";
        String address2 = "Updated Address 2";
        String city = "Updated city";
        String state = "Updated state";
        String postCode = "0000";

        AddHouseRequest request =
                AddHouseRequest.builder()
                        .addrLine1(address1)
                        .addrLine2(address2)
                        .city(city)
                        .houseCode("NON_EXIST")
                        .phone(phone)
                        .state(state)
                        .deleted(false)
                        .postCode(postCode)
                        .build();
        // When
        mockMvc.perform(
                        put("/house/update/{houseId}", 100)
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                // Then
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create HouseContact")
    void testCreateHouseContactSuccess() {
        // Given
        String houseCode = "101";
        House house = House.builder().houseCode(houseCode).build();
        HouseContactRequest request =
                HouseContactRequest.builder()
                        .houseCode(List.of(house.getHouseCode()))
                        .caption("main")
                        .firstName("Mary")
                        .lastName("Antony")
                        .email("maryantony@gmail.com")
                        .phone1("03 5461 1231")
                        .phone2("02 3456 8769")
                        .address1("abc road")
                        .address2("xyz street")
                        .city("victoria")
                        .state("sydney")
                        .zip("2000")
                        .notes("checked")
                        .lastUpdatedBy("Mark")
                        .build();
        // When
        mockMvc.perform(
                        post("/house/create-contact")
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
    @DisplayName("POST /create houseContact when fields are blank")
    void testCreateHouseContact_WithoutFields() {
        // Given
        String houseCode = "101";
        House house = House.builder().houseCode(houseCode).id(1).build();
        HouseContactRequest request =
                HouseContactRequest.builder()
                        .houseCode(List.of(house.getHouseCode()))
                        .caption("main")
                        .firstName("Mark")
                        .lastName("Antony")
                        .email("markantony@gmail.com")
                        .phone1("03 5461 1231")
                        .phone2("02 3456 8769")
                        .address1("abc road")
                        .zip("2000")
                        .notes("checked")
                        .lastUpdatedBy("Mark")
                        .build();
        // When
        mockMvc.perform(
                        post("/house/create-contact")
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
    @DisplayName("GET / get HouseContact")
    void testGetHouseContact_Success() {
        // Given
        String houseCode = "200";
        House house = House.builder().houseCode(houseCode).build();
        house = houseRepository.save(house);
        Instant lastUpdatedAt = Instant.now();

        HouseContact houseContact =
                HouseContact.builder()
                        .id(2)
                        .houses(Set.of(house))
                        .status(NoticeStatus.ACTIVE)
                        .caption("Room1")
                        .firstName("Mathew")
                        .lastName("Joseph")
                        .email("mathewjoseph@gmail.com")
                        .phone1("03 5461 1231")
                        .phone2("03 5461 7890")
                        .address1("abc road")
                        .address2("xyz street")
                        .city("city")
                        .state("state")
                        .zip("2000")
                        .notes("check")
                        .lastUpdatedBy("mathew")
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();
        houseContact = houseContactRepository.save(houseContact);

        // When
        mockMvc.perform(
                        get("/house/get/contact/{id}", houseContact.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(houseContact.getId()))
                .andExpect(jsonPath("$.caption").value(houseContact.getCaption()))
                .andExpect(jsonPath("$.status").value(houseContact.getStatus().name()))
                .andExpect(jsonPath("$.firstName").value(houseContact.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(houseContact.getLastName()))
                .andExpect(jsonPath("$.email").value(houseContact.getEmail()))
                .andExpect(jsonPath("$.phone1").value(houseContact.getPhone1()))
                .andExpect(jsonPath("$.phone2").value(houseContact.getPhone2()))
                .andExpect(jsonPath("$.address1").value(houseContact.getAddress1()))
                .andExpect(jsonPath("$.address2").value(houseContact.getAddress2()))
                .andExpect(jsonPath("$.city").value(houseContact.getCity()))
                .andExpect(jsonPath("$.state").value(houseContact.getState()))
                .andExpect(jsonPath("$.zip").value(houseContact.getZip()))
                .andExpect(jsonPath("$.notes").value(houseContact.getNotes()))
                .andExpect(jsonPath("$.lastUpdatedBy").value(houseContact.getLastUpdatedBy()))
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
    @DisplayName("PUT /update houseContact")
    void testUpdateHouseContactSuccess() {
        // Given
        String houseCode = "101";
        House house = House.builder().houseCode(houseCode).build();
        houseRepository.save(house);
        Instant lastUpdatedAt = Instant.now();
        String caption = "Room1";
        String firstName = "Sam";
        String lastName = "Mathew";
        String email = "sammathew@gmail.com";
        String phone1 = "03 5461 1231";
        String phone2 = "02 3487 4598";
        String address1 = "Abc road";
        String address2 = "xyz street";
        String city = "abcd";
        String state = "efgh";
        String zip = "2000";
        String notes = "checked";
        String lastUpdatedBy = "Mark";

        HouseContact houseContact =
                HouseContact.builder()
                        .id(2)
                        .houses(Set.of(house))
                        .caption("Room2")
                        .status(NoticeStatus.ACTIVE)
                        .firstName("Sara")
                        .lastName("Joseph")
                        .email("sarajoseph@gmail.com")
                        .phone1("02 2456 8796")
                        .phone2("01 3409 5678")
                        .address1("abc road")
                        .address2("pqr street")
                        .city("city")
                        .state("state")
                        .zip("3000")
                        .notes("note")
                        .lastUpdatedBy("mark")
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();
        houseContact = houseContactRepository.save(houseContact);

        UpdateHouseContactRequest request =
                UpdateHouseContactRequest.builder()
                        .caption(caption)
                        .status(NoticeStatus.INACTIVE.name())
                        .houseCode(List.of("101"))
                        .firstName(firstName)
                        .lastName(lastName)
                        .email(email)
                        .phone1(phone1)
                        .phone2(phone2)
                        .address1(address1)
                        .address2(address2)
                        .city(city)
                        .state(state)
                        .zip(zip)
                        .notes(notes)
                        .lastUpdatedBy(lastUpdatedBy)
                        .build();
        // When
        mockMvc.perform(
                        put("/house/update/contact/{id}", houseContact.getId())
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
    @DisplayName("GET /list HouseContacts")
    void testListHouseContactsSuccess() {
        // Given
        String houseCode = "250";
        House house = House.builder().houseCode(houseCode).build();
        house = houseRepository.save(house);
        Instant lastUpdatedAt = Instant.now();

        HouseContact houseContact =
                HouseContact.builder()
                        .id(2)
                        .houses(Set.of(house))
                        .caption("Room1")
                        .status(NoticeStatus.ACTIVE)
                        .firstName("Mathew")
                        .lastName("Joseph")
                        .email("mathewjoseph@gmail.com")
                        .phone1("03 5461 1231")
                        .phone2("03 5461 7890")
                        .address1("abc road")
                        .address2("xyz street")
                        .city("city")
                        .state("state")
                        .zip("2000")
                        .notes("check")
                        .lastUpdatedBy("mathew")
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();
        houseContact = houseContactRepository.save(houseContact);
        ListHouseContactRequest request =
                ListHouseContactRequest.builder()
                        .houseCode(house.getHouseCode())
                        .pageNumber(0)
                        .pageSize(1)
                        .build();
        ;

        // When
        mockMvc.perform(
                        post("/house/list/contact")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(houseContact.getId()))
                .andExpect(jsonPath("$.[0].caption").value(houseContact.getCaption()))
                .andExpect(jsonPath("$.[0].status").value(houseContact.getStatus().name()))
                .andExpect(jsonPath("$.[0].firstName").value(houseContact.getFirstName()))
                .andExpect(jsonPath("$.[0].lastName").value(houseContact.getLastName()))
                .andExpect(jsonPath("$.[0].email").value(houseContact.getEmail()))
                .andExpect(jsonPath("$.[0].phone1").value(houseContact.getPhone1()))
                .andExpect(jsonPath("$.[0].phone2").value(houseContact.getPhone2()))
                .andExpect(jsonPath("$.[0].address1").value(houseContact.getAddress1()))
                .andExpect(jsonPath("$.[0].address2").value(houseContact.getAddress2()))
                .andExpect(jsonPath("$.[0].city").value(houseContact.getCity()))
                .andExpect(jsonPath("$.[0].state").value(houseContact.getState()))
                .andExpect(jsonPath("$.[0].zip").value(houseContact.getZip()))
                .andExpect(jsonPath("$.[0].notes").value(houseContact.getNotes()))
                .andExpect(jsonPath("$.[0].lastUpdatedBy").value(houseContact.getLastUpdatedBy()))
                .andExpect(
                        jsonPath("$.[0].lastUpdatedAt")
                                .value(
                                        lastUpdatedAt
                                                .atOffset(ZoneOffset.UTC)
                                                .format(
                                                        DateTimeFormatter.ofPattern(
                                                                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))));
    }

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
