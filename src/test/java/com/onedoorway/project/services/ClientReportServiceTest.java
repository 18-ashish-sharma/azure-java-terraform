package com.onedoorway.project.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Every.everyItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onedoorway.project.dto.ClientReportDTO;
import com.onedoorway.project.dto.ToggleReportRequest;
import com.onedoorway.project.model.Client;
import com.onedoorway.project.model.ClientReport;
import com.onedoorway.project.model.Lookup;
import com.onedoorway.project.model.LookupType;
import com.onedoorway.project.repository.ClientReportRepository;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.LookupRepository;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClientReportServiceTest {

    @Mock ClientRepository mockClientRepository;

    @Mock ClientReportRepository mockClientReportRepository;

    @Mock LookupRepository mockLookupRepository;

    private ClientReportService clientReportService;

    @BeforeEach
    void init() {
        clientReportService =
                new ClientReportService(
                        mockClientRepository, mockClientReportRepository, mockLookupRepository);
    }

    @SneakyThrows
    @Test
    void testGetClientReport_Success() {
        // Given
        long id = 1;
        Lookup lookup = Lookup.builder().id(id).name("tests").build();
        mockLookupRepository.save(lookup);

        Client client = Client.builder().id(id).name("name").build();
        mockClientRepository.save(client);

        ClientReport clientReport =
                ClientReport.builder().id(1).client(client).lookup(lookup).toggle(true).build();

        when(mockClientReportRepository.findAllByClient_Id(client.getId()))
                .thenReturn(List.of(clientReport));

        // When
        List<ClientReportDTO> actual = clientReportService.getClientReportById(client.getId());

        // Then
        assertEquals(actual.size(), 1);
        assertThat(
                actual,
                (allOf(
                        everyItem(hasProperty("clientId", anyOf(equalTo(1L)))),
                        everyItem(hasProperty("lookupId", anyOf(equalTo(1L)))),
                        everyItem(hasProperty("toggle", anyOf(equalTo(true)))))));
    }

    @SneakyThrows
    @Test
    void testWhenToggle_isON_Success() {
        // Given
        Lookup lookup =
                Lookup.builder().id(2L).name("tests").lookupType(LookupType.REPORTS).build();
        Client client = Client.builder().id(1L).name("name").build();
        ToggleReportRequest request =
                ToggleReportRequest.builder().clientId(1L).lookupId(2L).toggle(true).build();
        when(mockClientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(mockLookupRepository.findById(2L)).thenReturn(Optional.of(lookup));

        // When
        clientReportService.toggleReport(request);

        // Then
        ClientReport expected =
                ClientReport.builder().client(client).lookup(lookup).toggle(true).build();
        ArgumentCaptor<ClientReport> clientReportArgumentCaptor =
                ArgumentCaptor.forClass(ClientReport.class);
        verify(mockClientReportRepository).save(clientReportArgumentCaptor.capture());
        ClientReport actual = clientReportArgumentCaptor.getValue();
        assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    void testWhenToggle_isOFF_Success() {
        // Given
        Lookup lookup =
                Lookup.builder().id(4L).name("tests").lookupType(LookupType.REPORTS).build();
        Client client = Client.builder().id(3L).name("name").build();
        ToggleReportRequest request =
                ToggleReportRequest.builder().clientId(3L).lookupId(4L).toggle(false).build();

        when(mockClientRepository.findById(3L)).thenReturn(Optional.of(client));
        when(mockLookupRepository.findById(4L)).thenReturn(Optional.of(lookup));

        // When
        clientReportService.toggleReport(request);

        // Then
        ClientReport expected =
                ClientReport.builder().client(client).lookup(lookup).toggle(false).build();
        ArgumentCaptor<ClientReport> clientReportArgumentCaptor =
                ArgumentCaptor.forClass(ClientReport.class);
        verify(mockClientReportRepository).save(clientReportArgumentCaptor.capture());
        ClientReport actual = clientReportArgumentCaptor.getValue();
        assertEquals(expected, actual);
    }
}
