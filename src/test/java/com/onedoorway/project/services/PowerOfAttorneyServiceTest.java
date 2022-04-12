package com.onedoorway.project.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onedoorway.project.dto.PowerOfAttorneyDTO;
import com.onedoorway.project.dto.PowerOfAttorneyRequest;
import com.onedoorway.project.dto.UpdatePowerOfAttorneyRequest;
import com.onedoorway.project.exception.ClientServiceException;
import com.onedoorway.project.exception.PowerOfAttorneyServiceException;
import com.onedoorway.project.model.Client;
import com.onedoorway.project.model.PowerOfAttorney;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.PowerOFAttorneyRepository;
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
public class PowerOfAttorneyServiceTest {
    @Mock PowerOFAttorneyRepository mockPowerOFAttorneyRepository;
    @Mock ClientRepository mockClientRepository;
    private PowerOfAttorneyService powerOfAttorneyService;

    @BeforeEach
    void init() {
        powerOfAttorneyService =
                new PowerOfAttorneyService(mockClientRepository, mockPowerOFAttorneyRepository);
    }

    @SneakyThrows
    @Test
    void testPowerOfAttorney_Success() {
        // Given
        long id = 1L;
        String type = "Small";
        String name = "Klaus";
        String phone = "1234567890";
        String email = "klaus@originals.com";
        String address1 = "bingo street,disneyworld";
        String address2 = "jame,austrailia";
        String city = "edmonton";
        String state = "Alberta";

        String postcode = "1234";
        String lastUpdatedBy = "sam";

        PowerOfAttorneyRequest request =
                PowerOfAttorneyRequest.builder()
                        .id(1L)
                        .clientId(1L)
                        .type(type)
                        .name(name)
                        .phone(phone)
                        .email(email)
                        .address1(address1)
                        .address2(address2)
                        .city(city)
                        .state(state)
                        .postCode(postcode)
                        .lastUpdatedBy(lastUpdatedBy)
                        .build();

        Client client = Client.builder().id(1L).build();
        when(mockClientRepository.findById(client.getId())).thenReturn(Optional.of(client));

        // When
        powerOfAttorneyService.createPowerOfAttorney(request);

        // Then
        PowerOfAttorney expected =
                PowerOfAttorney.builder()
                        .id(id)
                        .client(client)
                        .type(type)
                        .name(name)
                        .phone(phone)
                        .email(email)
                        .address1(address1)
                        .address2(address2)
                        .city(city)
                        .state(state)
                        .postCode(postcode)
                        .deleted(false)
                        .lastUpdatedBy(lastUpdatedBy)
                        .build();

        ArgumentCaptor<PowerOfAttorney> powerOfAttorneyArgumentCaptor =
                ArgumentCaptor.forClass(PowerOfAttorney.class);
        verify(mockPowerOFAttorneyRepository).save(powerOfAttorneyArgumentCaptor.capture());
        assertThat(
                expected,
                (allOf(
                        HasPropertyWithValue.hasProperty("id", equalTo(id)),
                        HasPropertyWithValue.hasProperty("client", equalTo(client)),
                        HasPropertyWithValue.hasProperty("type", equalTo(type)),
                        HasPropertyWithValue.hasProperty("name", equalTo(name)),
                        HasPropertyWithValue.hasProperty("phone", equalTo(phone)),
                        HasPropertyWithValue.hasProperty("email", equalTo(email)),
                        HasPropertyWithValue.hasProperty("address1", equalTo(address1)),
                        HasPropertyWithValue.hasProperty("address2", equalTo(address2)),
                        HasPropertyWithValue.hasProperty("city", equalTo(city)),
                        HasPropertyWithValue.hasProperty("state", equalTo(state)),
                        HasPropertyWithValue.hasProperty("deleted", equalTo(false)),
                        HasPropertyWithValue.hasProperty("postCode", equalTo(postcode)),
                        HasPropertyWithValue.hasProperty(
                                "lastUpdatedBy", equalTo(lastUpdatedBy)))));
    }

    @Test
    void testCreatePowerOfAttorney_Failure_NoClientFound() {
        // Given
        String type = "Small";
        String name = "Klaus";
        String phone = "1234567890";
        String email = "klaus@originals.com";
        String address1 = "bingo street,disneyworld";
        String address2 = "jame,austrailia";
        String city = "edmonton";
        String state = "Alberta";

        PowerOfAttorneyRequest request =
                PowerOfAttorneyRequest.builder()
                        .clientId(1L)
                        .type(type)
                        .name(name)
                        .phone(phone)
                        .email(email)
                        .address1(address1)
                        .address2(address2)
                        .city(city)
                        .state(state)
                        .build();

        assertThrows(
                PowerOfAttorneyServiceException.class,
                () -> {

                    // When
                    powerOfAttorneyService.createPowerOfAttorney(request);
                });
    }

    @SneakyThrows
    @Test
    void testListPowerOfAttorney_Success() {
        // Given
        String type = "Small";
        String name = "Klaus";
        String phone = "1234567890";
        String email = "klaus@originals.com";
        String address1 = "bingo street,disneyworld";
        String address2 = "jame,austrailia";
        String city = "edmonton";
        String state = "Alberta";
        String postcode = "1234";
        String lastUpdatedBy = "sam";
        Client client = Client.builder().id(1L).name("client").build();
        PowerOfAttorney powerOfAttorney =
                PowerOfAttorney.builder()
                        .client(client)
                        .type(type)
                        .name(name)
                        .phone(phone)
                        .email(email)
                        .address1(address1)
                        .address2(address2)
                        .city(city)
                        .state(state)
                        .postCode(postcode)
                        .deleted(false)
                        .lastUpdatedBy(lastUpdatedBy)
                        .build();
        when(mockPowerOFAttorneyRepository.findByClient_IdAndDeleted(1L, false))
                .thenReturn(List.of(powerOfAttorney));
        when(mockClientRepository.findById((1L))).thenReturn(Optional.of(client));
        List<PowerOfAttorneyDTO> expected =
                List.of(new ModelMapper().map(powerOfAttorney, PowerOfAttorneyDTO.class));
        // When
        List<PowerOfAttorneyDTO> actual = powerOfAttorneyService.listPowerOfAttorney(1L);
        // Then
        assertEquals(expected, actual);
    }

    @Test
    void testListPowerOfAttorney_Failure() {
        // Given
        long id = 1;

        assertThrows(
                ClientServiceException.class,
                () -> {
                    // When
                    powerOfAttorneyService.listPowerOfAttorney(id);
                });
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdatePowerOfAttorney_Success() {
        long id = 1L;
        String type = "Small";
        String name = "Klaus";
        String phone = "1234567890";
        String email = "klaus@originals.com";
        String address1 = "bingo street,disneyworld";
        String address2 = "jame,austrailia";
        String city = "edmonton";
        String state = "Alberta";
        String postcode = "1234";
        String lastUpdatedBy = "sam";
        Client client = Client.builder().id(1).name("name").build();
        mockClientRepository.save(client);

        PowerOfAttorney powerOfAttorney =
                PowerOfAttorney.builder()
                        .client(client)
                        .type("medium")
                        .name("rai")
                        .phone("123")
                        .email("rai@gmail.com")
                        .address1("116 mesa vista drive")
                        .address2("326 harvest moon")
                        .city("hamilton")
                        .state("ontario")
                        .postCode("4582")
                        .deleted(false)
                        .lastUpdatedBy("cody")
                        .build();
        UpdatePowerOfAttorneyRequest request =
                UpdatePowerOfAttorneyRequest.builder()
                        .type(type)
                        .name(name)
                        .phone(phone)
                        .email(email)
                        .address1(address1)
                        .address2(address2)
                        .city(city)
                        .state(state)
                        .postCode(postcode)
                        .deleted(false)
                        .lastUpdatedBy(lastUpdatedBy)
                        .build();

        when(mockPowerOFAttorneyRepository.findById(id)).thenReturn(Optional.of(powerOfAttorney));

        // when
        powerOfAttorneyService.updatePowerOfAttorney(id, request);

        PowerOfAttorney expected =
                PowerOfAttorney.builder()
                        .client(client)
                        .type(type)
                        .name(name)
                        .phone(phone)
                        .email(email)
                        .address1(address1)
                        .address2(address2)
                        .city(city)
                        .state(state)
                        .postCode(postcode)
                        .deleted(false)
                        .lastUpdatedBy(lastUpdatedBy)
                        .build();
        // then
        verify(mockPowerOFAttorneyRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdatePowerOfAttorney_Failure() {
        // Given
        UpdatePowerOfAttorneyRequest request =
                UpdatePowerOfAttorneyRequest.builder()
                        .type("medium")
                        .name("rai")
                        .phone("123")
                        .email("rai@gmail.com")
                        .address1("116 mesa vista drive")
                        .address2("326 harvest moon")
                        .city("hamilton")
                        .state("ontario")
                        .postCode("4582")
                        .deleted(false)
                        .lastUpdatedBy("cody")
                        .build();
        assertThrows(
                PowerOfAttorneyServiceException.class,
                () -> {

                    // When
                    powerOfAttorneyService.updatePowerOfAttorney(1L, request);
                });
    }
}
