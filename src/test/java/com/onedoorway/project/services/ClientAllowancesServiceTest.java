package com.onedoorway.project.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onedoorway.project.dto.ClientAllowancesDTO;
import com.onedoorway.project.dto.ClientAllowancesRequest;
import com.onedoorway.project.dto.UpdateClientAllowancesRequest;
import com.onedoorway.project.exception.ClientAllowancesServiceException;
import com.onedoorway.project.exception.ClientServiceException;
import com.onedoorway.project.model.Client;
import com.onedoorway.project.model.ClientAllowances;
import com.onedoorway.project.repository.ClientAllowancesRepository;
import com.onedoorway.project.repository.ClientRepository;
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
class ClientAllowancesServiceTest {
    @Mock ClientAllowancesRepository mockClientAllowancesRepository;
    @Mock ClientRepository mockClientRepository;

    private ClientAllowancesService clientAllowancesService;

    @BeforeEach
    void init() {
        clientAllowancesService =
                new ClientAllowancesService(mockClientAllowancesRepository, mockClientRepository);
    }

    @SneakyThrows
    @Test
    void testCreateClientAllowances_Success() {
        // Given
        Client client = Client.builder().id(1).build();
        String cappedKMs = "CappedKMs";
        String concessionCard = "ConcessionCard";
        String kms = "Kms";
        String grocerySpend = "GrocerySpend";
        String budgetlyCardNo = "BudgetlyCardNo";
        String lastUploadedBy = "sam";

        ClientAllowancesRequest request =
                ClientAllowancesRequest.builder()
                        .clientId(client.getId())
                        .cappedKMs(cappedKMs)
                        .concessionCard(concessionCard)
                        .kms(kms)
                        .grocerySpend(grocerySpend)
                        .budgetlyCardNo(budgetlyCardNo)
                        .lastUploadedBy(lastUploadedBy)
                        .build();
        when(mockClientRepository.findById(client.getId())).thenReturn(Optional.of(client));

        // When
        clientAllowancesService.createClientAllowances(request);
        // Then
        ClientAllowances expected =
                ClientAllowances.builder()
                        .client(client)
                        .cappedKMs(cappedKMs)
                        .concessionCard(concessionCard)
                        .kms(kms)
                        .grocerySpend(grocerySpend)
                        .budgetlyCardNo(budgetlyCardNo)
                        .deleted(false)
                        .lastUploadedBy(lastUploadedBy)
                        .build();

        // capture what was about to be persisted and make sure that is as expected
        ArgumentCaptor<ClientAllowances> clientAllowancesArgumentCaptor =
                ArgumentCaptor.forClass(ClientAllowances.class);
        verify(mockClientAllowancesRepository).save(clientAllowancesArgumentCaptor.capture());

        assertThat(
                expected,
                (allOf(
                        HasPropertyWithValue.hasProperty("client", equalTo(client)),
                        HasPropertyWithValue.hasProperty("cappedKMs", equalTo(cappedKMs)),
                        HasPropertyWithValue.hasProperty("concessionCard", equalTo(concessionCard)),
                        HasPropertyWithValue.hasProperty("kms", equalTo(kms)),
                        HasPropertyWithValue.hasProperty("grocerySpend", equalTo(grocerySpend)),
                        HasPropertyWithValue.hasProperty("lastUploadedBy", equalTo(lastUploadedBy)),
                        HasPropertyWithValue.hasProperty("deleted", equalTo(false)),
                        HasPropertyWithValue.hasProperty(
                                "budgetlyCardNo", equalTo(budgetlyCardNo)))));
    }

    @SneakyThrows
    @Test
    void testCreateClientsAllowance_WithoutFields() {
        // Given
        Client client = Client.builder().id(1).build();
        String cappedKMs = "CappedKMs";
        String concessionCard = "ConcessionCard";
        String kms = "Kms";
        String lastUploadedBy = "sam";
        ClientAllowancesRequest request =
                ClientAllowancesRequest.builder()
                        .clientId(client.getId())
                        .cappedKMs(cappedKMs)
                        .concessionCard(concessionCard)
                        .kms(kms)
                        .lastUploadedBy(lastUploadedBy)
                        .build();
        when(mockClientRepository.findById(client.getId())).thenReturn(Optional.of(client));

        // When
        clientAllowancesService.createClientAllowances(request);
        // Then
        ClientAllowances expected =
                ClientAllowances.builder()
                        .cappedKMs(cappedKMs)
                        .concessionCard(concessionCard)
                        .kms(kms)
                        .lastUploadedBy(lastUploadedBy)
                        .build();
        // capture what was about to be persisted and make sure that is as expected
        ArgumentCaptor<ClientAllowances> clientAllowancesArgumentCaptor =
                ArgumentCaptor.forClass(ClientAllowances.class);
        verify(mockClientAllowancesRepository).save(clientAllowancesArgumentCaptor.capture());

        assertThat(
                expected,
                (allOf(
                        HasPropertyWithValue.hasProperty("cappedKMs", equalTo(cappedKMs)),
                        HasPropertyWithValue.hasProperty("concessionCard", equalTo(concessionCard)),
                        HasPropertyWithValue.hasProperty("lastUploadedBy", equalTo(lastUploadedBy)),
                        HasPropertyWithValue.hasProperty("kms", equalTo(kms)))));
    }

    @Test
    void testCreateClientAllowances_Failure_ClientNotFound() {
        // Given

        String cappedKMs = "CappedKMs";
        String concessionCard = "ConcessionCard";
        String kms = "Kms";
        String grocerySpend = "GrocerySpend";
        String budgetlyCardNo = "BudgetlyCardNo";
        String lastUploadedBy = "sam";
        ClientAllowancesRequest request =
                ClientAllowancesRequest.builder()
                        .cappedKMs(cappedKMs)
                        .concessionCard(concessionCard)
                        .kms(kms)
                        .grocerySpend(grocerySpend)
                        .budgetlyCardNo(budgetlyCardNo)
                        .lastUploadedBy(lastUploadedBy)
                        .build();

        assertThrows(
                ClientAllowancesServiceException.class,
                () -> {
                    // When
                    clientAllowancesService.createClientAllowances(request);
                });
    }

    @SneakyThrows
    @Test
    void testGetClientAllowances_Success() {
        // Given
        Client client = Client.builder().id(1L).name("client").build();
        ClientAllowances clientAllowances =
                ClientAllowances.builder()
                        .client(client)
                        .cappedKMs("CappedKMs")
                        .concessionCard("ConcessionCard")
                        .kms("Kms")
                        .grocerySpend("GrocerySpend")
                        .budgetlyCardNo("BudgetlyCardNo")
                        .deleted(false)
                        .lastUploadedBy("lastUploaded")
                        .build();
        when(mockClientAllowancesRepository.findByClient_IdAndDeleted(1L, false))
                .thenReturn(List.of(clientAllowances));
        when(mockClientRepository.findById((1L))).thenReturn(Optional.of(client));
        List<ClientAllowancesDTO> expected =
                List.of(new ModelMapper().map(clientAllowances, ClientAllowancesDTO.class));
        // When
        List<ClientAllowancesDTO> actual = clientAllowancesService.getClientAllowancesById(1L);
        // Then
        assertEquals(expected, actual);
    }

    @Test
    void testGetClientAllowances_Failure_ReportNotFound() {
        // Given
        long id = 1;

        assertThrows(
                ClientServiceException.class,
                () -> {
                    // When
                    clientAllowancesService.getClientAllowancesById(id);
                });
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateClientAllowances_Success() {
        long id = 1L;
        Client client = Client.builder().id(1).name("client").build();
        mockClientRepository.save(client);

        ClientAllowances clientAllowances =
                ClientAllowances.builder()
                        .client(client)
                        .id(id)
                        .cappedKMs("CappedKMs")
                        .concessionCard("ConcessionCard")
                        .kms("Kms")
                        .grocerySpend("GrocerySpend")
                        .budgetlyCardNo("BudgetlyCardNo")
                        .deleted(false)
                        .lastUploadedBy("lastUploadedBy")
                        .build();

        UpdateClientAllowancesRequest request =
                UpdateClientAllowancesRequest.builder()
                        .cappedKMs("CappedKMs")
                        .concessionCard("ConcessionCard")
                        .kms("Kms")
                        .grocerySpend("GrocerySpend")
                        .budgetlyCardNo("BudgetlyCardNo")
                        .deleted(false)
                        .lastUploadedBy("lastUploadedBy")
                        .build();

        when(mockClientAllowancesRepository.findById(id)).thenReturn(Optional.of(clientAllowances));

        // when
        clientAllowancesService.updateClientAllowances(id, request);

        ClientAllowances expected =
                ClientAllowances.builder()
                        .id(id)
                        .client(client)
                        .cappedKMs("CappedKMs")
                        .concessionCard("ConcessionCard")
                        .kms("Kms")
                        .grocerySpend("GrocerySpend")
                        .budgetlyCardNo("BudgetlyCardNo")
                        .deleted(false)
                        .lastUploadedBy("lastUploadedBy")
                        .build();
        // then
        verify(mockClientAllowancesRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateClientAllowances_Failure() {
        // Given
        UpdateClientAllowancesRequest request =
                UpdateClientAllowancesRequest.builder()
                        .cappedKMs("CappedKMs")
                        .concessionCard("ConcessionCard")
                        .kms("Kms")
                        .lastUploadedBy("lastUploadedBy")
                        .build();
        assertThrows(
                ClientAllowancesServiceException.class,
                () -> {

                    // When
                    clientAllowancesService.updateClientAllowances(1L, request);
                });
    }
}
