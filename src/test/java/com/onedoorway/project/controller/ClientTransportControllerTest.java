package com.onedoorway.project.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.onedoorway.project.dto.ClientTransportRequest;
import com.onedoorway.project.dto.UpdateClientTransportRequest;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.ClientTransportRepository;
import com.onedoorway.project.repository.UserRepository;
import com.onedoorway.project.services.ODWUserDetailsService;
import com.onedoorway.project.util.JwtUtil;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
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
class ClientTransportControllerTest {
    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private ClientTransportRepository clientTransportRepository;

    @Autowired private ClientRepository clientRepository;

    @Autowired private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        User user =
                User.builder()
                        .id(1)
                        .email("test@test.com")
                        .password("password")
                        .roles(
                                new HashSet<>(
                                        Collections.singletonList(
                                                Role.builder().id(1).name("ADMIN").build())))
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
        clientTransportRepository.deleteAll();
        userRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /create client transport")
    void testCreateClientTransport_Success() {
        // Given
        Client client = Client.builder().id(1L).build();
        client = clientRepository.save(client);

        ClientTransportRequest request =
                ClientTransportRequest.builder()
                        .odCar(YesNo.Yes.name())
                        .clientId(client.getId())
                        .carRegistration("KL 07 AN 0000")
                        .carRegExpiry(LocalDate.of(2021, 10, 14))
                        .carModel("Range Rover Evoque")
                        .carMakeYear("2013")
                        .isTravelProtocol(YesNo.Yes.name())
                        .lastUploadedBy("sam")
                        .travelProtocol("travel protocol")
                        .comprehensiveInsurance(YesNo.Yes.name())
                        .insurancePolicyNumber("insurancePolicyNumber")
                        .authorisedPerson("authorisedPerson")
                        .authorisedPersonContactNumber("authorisedPersonContactNumber")
                        .roadSideAssistanceCovered(YesNo.Yes.name())
                        .insuranceAgency("insuranceAgency")
                        .insuranceContactNumber("insuranceContactNumber")
                        .build();
        // When
        mockMvc.perform(
                        post("/client-transport/create")
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
    @DisplayName("POST /create client transport when fields are blank")
    void testCreateClientTransport_WithoutFields() {
        // Given
        Client client = Client.builder().id(1L).build();
        client = clientRepository.save(client);

        String carRegistration = "KL 07 AN 0000";
        LocalDate carRegExpiry = LocalDate.of(2021, 10, 14);
        String carModel = "Range Rover Evoque";
        String carMakeYear = "2013";
        String travelProtocol = "";
        String comprehensiveInsurance = "";
        String insurancePolicyNumber = "insurancePolicyNumber";
        String authorisedPerson = "";
        String authorisedPersonContactNumber = "authorisedPersonContactNumber ";
        String insuranceAgency = "";
        String insuranceContactNumber = "insuranceContactNumber";
        String lastUploadedBy = "";

        ClientTransportRequest request =
                ClientTransportRequest.builder()
                        .odCar(YesNo.Yes.name())
                        .clientId(client.getId())
                        .carRegistration(carRegistration)
                        .carRegExpiry(carRegExpiry)
                        .carModel(carModel)
                        .carMakeYear(carMakeYear)
                        .isTravelProtocol(YesNo.Yes.name())
                        .insurancePolicyNumber(insurancePolicyNumber)
                        .authorisedPersonContactNumber(authorisedPersonContactNumber)
                        .roadSideAssistanceCovered(YesNo.Yes.name())
                        .comprehensiveInsurance(YesNo.Yes.name())
                        .insuranceContactNumber(insuranceContactNumber)
                        .build();

        // When
        mockMvc.perform(
                        post("/client-transport/create")
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
    @DisplayName("GET / list client transport")
    void testGetClientTransport_Success() {
        // Given
        long id = 1;
        String carRegistration = "KL 07 AN 0000";
        LocalDate carRegExpiry = LocalDate.of(2021, 10, 14);
        String carModel = "Range Rover Evoque";
        String carMakeYear = "2013";
        String travelProtocol = "travelProtocol";
        String insurancePolicyNumber = "insurancePolicyNumber";
        String authorisedPerson = "authorisedPerson";
        String authorisedPersonContactNumber = "authorisedPersonContactNumber ";
        String insuranceAgency = "insuranceAgency";
        String insuranceContactNumber = "insuranceContactNumber";
        String lastUploadedBy = "sam";

        Client client = Client.builder().id(id).name("client").build();
        client = clientRepository.save(client);

        ClientTransport clientTransport =
                ClientTransport.builder()
                        .client(client)
                        .odCar(YesNo.Yes)
                        .carRegistration(carRegistration)
                        .carRegExpiry(carRegExpiry)
                        .lastUploadedBy(lastUploadedBy)
                        .deleted(false)
                        .carModel(carModel)
                        .carMakeYear(carMakeYear)
                        .isTravelProtocol(YesNo.Yes)
                        .travelProtocol(travelProtocol)
                        .authorisedPerson(authorisedPerson)
                        .comprehensiveInsurance(YesNo.Yes)
                        .insuranceAgency(insuranceAgency)
                        .insurancePolicyNumber(insurancePolicyNumber)
                        .authorisedPersonContactNumber(authorisedPersonContactNumber)
                        .roadSideAssistanceCovered(YesNo.Yes)
                        .insuranceContactNumber(insuranceContactNumber)
                        .build();
        clientTransportRepository.save(clientTransport);

        // When
        mockMvc.perform(
                        get("/client-transport/list/{clientId}", client.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].odCar").value("Yes"))
                .andExpect(jsonPath("$.[0].deleted").value(false))
                .andExpect(jsonPath("$.[0].comprehensiveInsurance").value("Yes"))
                .andExpect(jsonPath("$.[0].isTravelProtocol").value("Yes"))
                .andExpect(jsonPath("$.[0].roadSideAssistanceCovered").value("Yes"))
                .andExpect(
                        jsonPath("$.[0].carRegistration")
                                .value(clientTransport.getCarRegistration()))
                .andExpect(
                        jsonPath("$.[0].carRegExpiry")
                                .value(
                                        clientTransport
                                                .getCarRegExpiry()
                                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .andExpect(jsonPath("$.[0].carModel").value(clientTransport.getCarModel()))
                .andExpect(jsonPath("$.[0].carMakeYear").value(clientTransport.getCarMakeYear()))
                .andExpect(
                        jsonPath("$.[0].travelProtocol").value(clientTransport.getTravelProtocol()))
                .andExpect(
                        jsonPath("$.[0].insurancePolicyNumber")
                                .value(clientTransport.getInsurancePolicyNumber()))
                .andExpect(
                        jsonPath("$.[0].authorisedPerson")
                                .value(clientTransport.getAuthorisedPerson()))
                .andExpect(
                        jsonPath("$.[0].authorisedPersonContactNumber")
                                .value(clientTransport.getAuthorisedPersonContactNumber()))
                .andExpect(
                        jsonPath("$.[0].authorisedPerson")
                                .value(clientTransport.getAuthorisedPerson()))
                .andExpect(
                        jsonPath("$.[0].insuranceAgency")
                                .value(clientTransport.getInsuranceAgency()))
                .andExpect(
                        jsonPath("$.[0].insuranceContactNumber")
                                .value(clientTransport.getInsuranceContactNumber()));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT /update client transport success")
    void testUpdateClientTransport_Success() {
        // Given
        long id = 1;
        String carRegistration = "KL 07 AN 0000";
        LocalDate carRegExpiry = LocalDate.of(2021, 10, 14);
        String carModel = "Range Rover Evoque";
        String carMakeYear = "2013";
        String travelProtocol = "travelProtocol";
        String insurancePolicyNumber = "insurancePolicyNumber";
        String authorisedPerson = "authorisedPerson";
        String authorisedPersonContactNumber = "authorisedPersonContactNumber ";
        String insuranceAgency = "insuranceAgency";
        String insuranceContactNumber = "insuranceContactNumber";
        String lastUploadedBy = "sam";

        Client client = Client.builder().id(id).name("client").build();
        client = clientRepository.save(client);

        ClientTransport clientTransport =
                ClientTransport.builder()
                        .client(client)
                        .odCar(YesNo.Yes)
                        .carRegistration(carRegistration)
                        .carRegExpiry(carRegExpiry)
                        .lastUploadedBy(lastUploadedBy)
                        .deleted(false)
                        .carModel(carModel)
                        .carMakeYear(carMakeYear)
                        .isTravelProtocol(YesNo.Yes)
                        .travelProtocol(travelProtocol)
                        .authorisedPerson(authorisedPerson)
                        .comprehensiveInsurance(YesNo.Yes)
                        .insuranceAgency(insuranceAgency)
                        .insurancePolicyNumber(insurancePolicyNumber)
                        .authorisedPersonContactNumber(authorisedPersonContactNumber)
                        .roadSideAssistanceCovered(YesNo.Yes)
                        .insuranceContactNumber(insuranceContactNumber)
                        .build();
        clientTransport = clientTransportRepository.save(clientTransport);

        UpdateClientTransportRequest request =
                UpdateClientTransportRequest.builder()
                        .odCar(YesNo.Yes.name())
                        .carRegistration("KL 08 AN 0001")
                        .carRegExpiry("2021-09-12")
                        .deleted(false)
                        .carModel("Range Rover sport")
                        .carMakeYear("2014")
                        .isTravelProtocol(YesNo.Yes.name())
                        .lastUploadedBy("samuel")
                        .travelProtocol("travel")
                        .comprehensiveInsurance(YesNo.Yes.name())
                        .insurancePolicyNumber("insurancePolicy")
                        .authorisedPerson("roy")
                        .authorisedPersonContactNumber("9896789023")
                        .roadSideAssistanceCovered(YesNo.Yes.name())
                        .insuranceAgency("insurance")
                        .insuranceContactNumber("insuranceContact")
                        .build();

        // When
        mockMvc.perform(
                        put("/client-transport/update/{id}", clientTransport.getId())
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
    @DisplayName("PUT /update client transport failure")
    void testUpdateClientTransport_Failure() {
        // Given
        Client client = Client.builder().id(1).name("client").build();
        clientRepository.save(client);

        UpdateClientTransportRequest request =
                UpdateClientTransportRequest.builder()
                        .odCar(YesNo.Yes.name())
                        .carRegistration("KL 08 AN 0001")
                        .carRegExpiry("2021-09-12")
                        .carModel("Range Rover sport")
                        .carMakeYear("2014")
                        .deleted(false)
                        .isTravelProtocol(YesNo.Yes.name())
                        .lastUploadedBy("samuel")
                        .travelProtocol("travel")
                        .comprehensiveInsurance(YesNo.Yes.name())
                        .insurancePolicyNumber("insurancePolicy")
                        .authorisedPerson("roy")
                        .authorisedPersonContactNumber("9896789023")
                        .roadSideAssistanceCovered(YesNo.Yes.name())
                        .insuranceAgency("insurance")
                        .insuranceContactNumber("insuranceContact")
                        .build();

        // When
        mockMvc.perform(
                        put("/client-transport/update/{id}", 300)
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

    static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
