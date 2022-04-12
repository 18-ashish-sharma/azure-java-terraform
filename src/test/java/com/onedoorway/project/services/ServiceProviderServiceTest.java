package com.onedoorway.project.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onedoorway.project.FrozenContext;
import com.onedoorway.project.dto.ServiceProviderDTO;
import com.onedoorway.project.dto.ServiceProviderRequest;
import com.onedoorway.project.dto.UpdateServiceProviderRequest;
import com.onedoorway.project.exception.ClientServiceException;
import com.onedoorway.project.exception.ServiceProviderException;
import com.onedoorway.project.model.Client;
import com.onedoorway.project.model.ServiceProvider;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.ServiceProviderRepository;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ServiceProviderServiceTest {
    @Mock ServiceProviderRepository mockServiceProviderRepository;
    @Mock ClientRepository mockClientRepository;
    private ServiceProviderService serviceProviderService;
    private final FrozenContext context = new FrozenContext();

    @BeforeEach
    void init() {
        serviceProviderService =
                new ServiceProviderService(
                        mockServiceProviderRepository, mockClientRepository, context);
    }

    @SneakyThrows
    @Test
    void testCreateServiceProvider_Success() {
        // Given
        String name = "name";
        String service = "service";
        String lastUpdatedBy = "sam";
        String email = "jnj@email.com";
        String phone = "1234567890";

        ServiceProviderRequest request =
                ServiceProviderRequest.builder()
                        .clientId(1L)
                        .name(name)
                        .service(service)
                        .email(email)
                        .phone(phone)
                        .lastUpdatedBy(lastUpdatedBy)
                        .build();

        Client client = Client.builder().id(1L).build();
        when(mockClientRepository.findById(client.getId())).thenReturn(Optional.of(client));

        // When
        serviceProviderService.createServiceProvider(request);

        // Then
        ServiceProvider expected =
                ServiceProvider.builder()
                        .id(1L)
                        .client(client)
                        .name(name)
                        .service(service)
                        .deleted(false)
                        .lastUpdatedBy(lastUpdatedBy)
                        .email(email)
                        .phone(phone)
                        .build();

        ArgumentCaptor<ServiceProvider> serviceProviderArgumentCaptor =
                ArgumentCaptor.forClass(ServiceProvider.class);
        verify(mockServiceProviderRepository).save(serviceProviderArgumentCaptor.capture());
        assertThat(
                expected,
                (allOf(
                        HasPropertyWithValue.hasProperty("client", equalTo(client)),
                        HasPropertyWithValue.hasProperty("lastUpdatedBy", equalTo(lastUpdatedBy)),
                        HasPropertyWithValue.hasProperty("name", equalTo(name)),
                        HasPropertyWithValue.hasProperty("deleted", equalTo(false)),
                        HasPropertyWithValue.hasProperty("email", equalTo(email)),
                        HasPropertyWithValue.hasProperty("phone", equalTo(phone)),
                        HasPropertyWithValue.hasProperty("service", equalTo(service)))));
    }

    @Test
    void testCreateServiceProvider_Failure_ClientNotFound() {
        // Given
        String name = "name";
        String service = "service";
        String lastUpdatedBy = "sam";

        ServiceProviderRequest request =
                ServiceProviderRequest.builder()
                        .clientId(1L)
                        .name(name)
                        .service(service)
                        .lastUpdatedBy(lastUpdatedBy)
                        .build();

        assertThrows(
                ServiceProviderException.class,
                () -> {
                    // When
                    serviceProviderService.createServiceProvider(request);
                });
    }

    @SneakyThrows
    @Test
    void testGetServiceProvider_Success() {
        // Given
        Client client = Client.builder().id(1L).name("client").build();
        ServiceProvider serviceProvider =
                ServiceProvider.builder()
                        .client(client)
                        .name("jo")
                        .service("service")
                        .deleted(false)
                        .phone("123452992")
                        .email("say@gmail.com")
                        .lastUpdatedBy("lastUpdatedBy")
                        .build();
        when(mockServiceProviderRepository.findByClient_IdAndDeleted(1L, false))
                .thenReturn(List.of(serviceProvider));
        when(mockClientRepository.findById((1L))).thenReturn(Optional.of(client));
        List<ServiceProviderDTO> expected =
                List.of(new ModelMapper().map(serviceProvider, ServiceProviderDTO.class));
        // When
        List<ServiceProviderDTO> actual = serviceProviderService.getServiceProviderById(1L);
        // Then
        assertEquals(expected, actual);
    }

    @Test
    void testGetServiceProvider_Failure_ReportNotFound() {
        // Given
        long id = 1;

        assertThrows(
                ClientServiceException.class,
                () -> {
                    // When
                    serviceProviderService.getServiceProviderById(id);
                });
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateServiceProvider_Success() {
        long id = 1L;
        String name = "joy";
        String service = "Telephone";
        String lastUpdatedBy = "jimmy";
        String email = "jnj@email.com";
        String phone = "1234567890";

        Client client = Client.builder().id(1).name("name").build();
        mockClientRepository.save(client);

        ServiceProvider serviceProvider =
                ServiceProvider.builder()
                        .client(client)
                        .name("joy")
                        .service("service")
                        .deleted(false)
                        .lastUpdatedBy("lastUpdatedBy")
                        .email("anything@email.com")
                        .phone("3333333333")
                        .build();
        UpdateServiceProviderRequest request =
                UpdateServiceProviderRequest.builder()
                        .name(name)
                        .service(service)
                        .deleted(false)
                        .email(email)
                        .phone(phone)
                        .lastUpdatedBy(lastUpdatedBy)
                        .build();

        when(mockServiceProviderRepository.findById(id)).thenReturn(Optional.of(serviceProvider));

        // when
        serviceProviderService.updateServiceProvider(id, request);

        ServiceProvider expected =
                ServiceProvider.builder()
                        .client(client)
                        .name(name)
                        .service(service)
                        .deleted(false)
                        .lastUpdatedBy(lastUpdatedBy)
                        .email(email)
                        .phone(phone)
                        .build();
        // then
        verify(mockServiceProviderRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateServiceProvider_Failure() {
        // Given
        UpdateServiceProviderRequest request =
                UpdateServiceProviderRequest.builder()
                        .name("name")
                        .service("")
                        .deleted(false)
                        .lastUpdatedBy("lastUpdatedBy")
                        .build();
        assertThrows(
                ServiceProviderException.class,
                () -> {

                    // When
                    serviceProviderService.updateServiceProvider(1L, request);
                });
    }
}
