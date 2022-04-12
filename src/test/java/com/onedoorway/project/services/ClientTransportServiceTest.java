package com.onedoorway.project.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onedoorway.project.dto.ClientTransportDTO;
import com.onedoorway.project.dto.ClientTransportRequest;
import com.onedoorway.project.dto.UpdateClientTransportRequest;
import com.onedoorway.project.exception.ClientTransportServiceException;
import com.onedoorway.project.model.Client;
import com.onedoorway.project.model.ClientTransport;
import com.onedoorway.project.model.YesNo;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.ClientTransportRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.hamcrest.beans.HasPropertyWithValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.test.context.support.WithMockUser;

@ExtendWith(MockitoExtension.class)
class ClientTransportServiceTest {
    @Mock ClientTransportRepository mockClientTransportRepository;

    @Mock ClientRepository mockClientRepository;

    private ClientTransportService clientTransportService;

    @BeforeEach
    void init() {
        clientTransportService =
                new ClientTransportService(mockClientTransportRepository, mockClientRepository);
    }

    @SneakyThrows
    @Test
    void testCreateClientTransport_Success() {
        // Given
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
        String cappedKMs = "100";

        ClientTransportRequest request =
                ClientTransportRequest.builder()
                        .clientId(1L)
                        .odCar(YesNo.Yes.name())
                        .carRegistration(carRegistration)
                        .carRegExpiry(carRegExpiry)
                        .carModel(carModel)
                        .carMakeYear(carMakeYear)
                        .lastUploadedBy(lastUploadedBy)
                        .travelProtocol(travelProtocol)
                        .isTravelProtocol(YesNo.Yes.name())
                        .comprehensiveInsurance(YesNo.Yes.name())
                        .authorisedPerson(authorisedPerson)
                        .insurancePolicyNumber(insurancePolicyNumber)
                        .insuranceAgency(insuranceAgency)
                        .authorisedPersonContactNumber(authorisedPersonContactNumber)
                        .roadSideAssistanceCovered(YesNo.Yes.name())
                        .insuranceContactNumber(insuranceContactNumber)
                        .cappedKMs(cappedKMs)
                        .build();

        Client client = Client.builder().id(1L).build();
        when(mockClientRepository.findById(client.getId())).thenReturn(Optional.of(client));

        // When
        clientTransportService.createClientTransport(request);
        // Then
        ClientTransport expected =
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
                        .cappedKMs(cappedKMs)
                        .build();

        // capture what was about to be persisted and make sure that is as expected
        ArgumentCaptor<ClientTransport> clientTransportArgumentCaptor =
                ArgumentCaptor.forClass(ClientTransport.class);
        verify(mockClientTransportRepository).save(clientTransportArgumentCaptor.capture());

        assertThat(
                expected,
                (allOf(
                        HasPropertyWithValue.hasProperty("odCar", equalTo(YesNo.Yes)),
                        HasPropertyWithValue.hasProperty("deleted", equalTo(false)),
                        HasPropertyWithValue.hasProperty("client", equalTo(client)),
                        HasPropertyWithValue.hasProperty(
                                "carRegistration", equalTo(carRegistration)),
                        HasPropertyWithValue.hasProperty("carRegExpiry", equalTo(carRegExpiry)),
                        HasPropertyWithValue.hasProperty("carModel", equalTo(carModel)),
                        HasPropertyWithValue.hasProperty("carMakeYear", equalTo(carMakeYear)),
                        HasPropertyWithValue.hasProperty("lastUploadedBy", equalTo(lastUploadedBy)),
                        HasPropertyWithValue.hasProperty("isTravelProtocol", equalTo(YesNo.Yes)),
                        HasPropertyWithValue.hasProperty("travelProtocol", equalTo(travelProtocol)),
                        HasPropertyWithValue.hasProperty(
                                "authorisedPerson", equalTo(authorisedPerson)),
                        HasPropertyWithValue.hasProperty(
                                "comprehensiveInsurance", equalTo(YesNo.Yes)),
                        HasPropertyWithValue.hasProperty(
                                "insuranceAgency", equalTo(insuranceAgency)),
                        HasPropertyWithValue.hasProperty(
                                "insurancePolicyNumber", equalTo(insurancePolicyNumber)),
                        HasPropertyWithValue.hasProperty(
                                "authorisedPersonContactNumber",
                                equalTo(authorisedPersonContactNumber)),
                        HasPropertyWithValue.hasProperty(
                                "roadSideAssistanceCovered", equalTo(YesNo.Yes)),
                        HasPropertyWithValue.hasProperty("lastUploadedBy", equalTo("sam")),
                        HasPropertyWithValue.hasProperty("cappedKMs", equalTo(cappedKMs)),
                        HasPropertyWithValue.hasProperty(
                                "insuranceContactNumber", equalTo(insuranceContactNumber)))));
    }

    @SneakyThrows
    @Test
    void testCreateClientTransport_WithoutFields() {
        // Given
        String carRegistration = "KL 07 AN 0000";
        LocalDate carRegExpiry = LocalDate.of(2021, 10, 14);
        String carModel = "Range Rover Evoque";
        String cappedKMs = "100";

        Client client = Client.builder().id(1L).build();

        ClientTransportRequest request =
                ClientTransportRequest.builder()
                        .odCar(YesNo.Yes.name())
                        .comprehensiveInsurance(YesNo.Yes.name())
                        .isTravelProtocol(YesNo.Yes.name())
                        .roadSideAssistanceCovered(YesNo.Yes.name())
                        .clientId(1L)
                        .carRegistration(carRegistration)
                        .carRegExpiry(carRegExpiry)
                        .carModel(carModel)
                        .cappedKMs(cappedKMs)
                        .build();

        when(mockClientRepository.findById(client.getId())).thenReturn(Optional.of(client));

        // When
        clientTransportService.createClientTransport(request);
        // Then
        ClientTransport expected =
                ClientTransport.builder()
                        .odCar(YesNo.Yes)
                        .roadSideAssistanceCovered(YesNo.Yes)
                        .isTravelProtocol(YesNo.Yes)
                        .comprehensiveInsurance(YesNo.Yes)
                        .client(client)
                        .carRegistration(carRegistration)
                        .carRegExpiry(carRegExpiry)
                        .carModel(carModel)
                        .cappedKMs(cappedKMs)
                        .build();
        // capture what was about to be persisted and make sure that is as expected
        ArgumentCaptor<ClientTransport> clientTransportArgumentCaptor =
                ArgumentCaptor.forClass(ClientTransport.class);
        verify(mockClientTransportRepository).save(clientTransportArgumentCaptor.capture());

        assertThat(
                expected,
                (allOf(
                        HasPropertyWithValue.hasProperty("odCar", equalTo(YesNo.Yes)),
                        HasPropertyWithValue.hasProperty(
                                "comprehensiveInsurance", equalTo(YesNo.Yes)),
                        HasPropertyWithValue.hasProperty("isTravelProtocol", equalTo(YesNo.Yes)),
                        HasPropertyWithValue.hasProperty(
                                "roadSideAssistanceCovered", equalTo(YesNo.Yes)),
                        HasPropertyWithValue.hasProperty("client", equalTo(client)),
                        HasPropertyWithValue.hasProperty(
                                "carRegistration", equalTo(carRegistration)),
                        HasPropertyWithValue.hasProperty("cappedKMs", equalTo(cappedKMs)),
                        HasPropertyWithValue.hasProperty("carRegExpiry", equalTo(carRegExpiry)),
                        HasPropertyWithValue.hasProperty("carModel", equalTo(carModel)))));
    }

    @Test
    void testCreateClientTransport_Failure_ClientNotFound() {
        // Given
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
        String cappedKMs = "100";

        ClientTransportRequest request =
                ClientTransportRequest.builder()
                        .clientId(1L)
                        .odCar(YesNo.Yes.name())
                        .carRegistration(carRegistration)
                        .carRegExpiry(carRegExpiry)
                        .carModel(carModel)
                        .carMakeYear(carMakeYear)
                        .lastUploadedBy(lastUploadedBy)
                        .travelProtocol(travelProtocol)
                        .isTravelProtocol(YesNo.Yes.name())
                        .comprehensiveInsurance(YesNo.Yes.name())
                        .authorisedPerson(authorisedPerson)
                        .insurancePolicyNumber(insurancePolicyNumber)
                        .insuranceAgency(insuranceAgency)
                        .authorisedPersonContactNumber(authorisedPersonContactNumber)
                        .roadSideAssistanceCovered(YesNo.Yes.name())
                        .insuranceContactNumber(insuranceContactNumber)
                        .cappedKMs(cappedKMs)
                        .build();

        assertThrows(
                ClientTransportServiceException.class,
                () -> {
                    // When
                    clientTransportService.createClientTransport(request);
                });
    }

    @SneakyThrows
    @Test
    void testGetClientTransport_Success() {
        // Given
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
        String cappedKMs = "100";

        Client client = Client.builder().id(1L).name("client").build();
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
                        .cappedKMs(cappedKMs)
                        .build();

        when(mockClientTransportRepository.findByClient_Id(1L))
                .thenReturn(List.of(clientTransport));
        when(mockClientRepository.findById((1L))).thenReturn(Optional.of(client));
        List<ClientTransportDTO> expected =
                List.of(new ModelMapper().map(clientTransport, ClientTransportDTO.class));
        // When
        List<ClientTransportDTO> actual = clientTransportService.getClientTransport(1L);
        // Then
        assertEquals(expected, actual);
    }

    @Test
    void testGetClientTransport_Failure_ClientNotFound() {
        // Given
        long id = 1;

        assertThrows(
                ClientTransportServiceException.class,
                () -> {
                    // When
                    clientTransportService.getClientTransport(id);
                });
    }

    @SneakyThrows
    @Test
    @WithMockUser
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
        String cappedKMs = "100";

        Client client = Client.builder().id(id).name("client").build();
        mockClientRepository.save(client);

        ClientTransport clientTransport =
                ClientTransport.builder()
                        .id(id)
                        .client(client)
                        .odCar(YesNo.Yes)
                        .carRegistration("KL 08 AN 0001")
                        .carRegExpiry(carRegExpiry)
                        .carModel("Range Rover sport")
                        .carMakeYear("2014")
                        .deleted(false)
                        .isTravelProtocol(YesNo.Yes)
                        .lastUploadedBy("samuel")
                        .travelProtocol("travel")
                        .comprehensiveInsurance(YesNo.Yes)
                        .insurancePolicyNumber("insurancePolicy")
                        .authorisedPerson("roy")
                        .authorisedPersonContactNumber("9896789023")
                        .roadSideAssistanceCovered(YesNo.Yes)
                        .insuranceAgency("insurance")
                        .insuranceContactNumber("insuranceContact")
                        .cappedKMs("10")
                        .build();

        UpdateClientTransportRequest request =
                UpdateClientTransportRequest.builder()
                        .odCar(YesNo.Yes.name())
                        .carRegistration(carRegistration)
                        .carRegExpiry("2021-10-14")
                        .lastUploadedBy(lastUploadedBy)
                        .carModel(carModel)
                        .carMakeYear(carMakeYear)
                        .deleted(false)
                        .isTravelProtocol(YesNo.Yes.name())
                        .travelProtocol(travelProtocol)
                        .authorisedPerson(authorisedPerson)
                        .comprehensiveInsurance(YesNo.Yes.name())
                        .insuranceAgency(insuranceAgency)
                        .insurancePolicyNumber(insurancePolicyNumber)
                        .authorisedPersonContactNumber(authorisedPersonContactNumber)
                        .roadSideAssistanceCovered(YesNo.Yes.name())
                        .insuranceContactNumber(insuranceContactNumber)
                        .cappedKMs(cappedKMs)
                        .build();

        when(mockClientTransportRepository.findById(1L)).thenReturn(Optional.of(clientTransport));

        // When
        clientTransportService.updateClientTransport(id, request);

        ClientTransport expected =
                ClientTransport.builder()
                        .id(id)
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
                        .cappedKMs(cappedKMs)
                        .build();

        // Then
        verify(mockClientTransportRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateClientTransport_Failure_ClientNotFound() {
        // Given
        long id = 1;
        UpdateClientTransportRequest request =
                UpdateClientTransportRequest.builder()
                        .odCar(YesNo.Yes.name())
                        .carRegistration("KL 08 AN 0001")
                        .carRegExpiry("2021-09-12")
                        .carModel("Range Rover sport")
                        .deleted(false)
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
                        .cappedKMs("100kms")
                        .build();

        assertThrows(
                ClientTransportServiceException.class,
                () -> {
                    // When
                    clientTransportService.updateClientTransport(id, request);
                });
    }
}
