package com.onedoorway.project.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Every.everyItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onedoorway.project.FrozenContext;
import com.onedoorway.project.dto.*;
import com.onedoorway.project.exception.HouseServiceException;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.HouseContactRepository;
import com.onedoorway.project.repository.HouseRepository;
import com.onedoorway.project.repository.UserRepository;
import java.time.Instant;
import java.util.*;
import lombok.SneakyThrows;
import org.hamcrest.beans.HasPropertyWithValue;
import org.hamcrest.core.AnyOf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.test.context.support.WithMockUser;

@ExtendWith(MockitoExtension.class)
class HouseServiceTest {
    @Mock HouseRepository mockHouseRepository;

    @Mock ClientRepository mockClientRepository;

    @Mock UserRepository mockUserRepository;

    @Mock HouseContactRepository mockHouseContactRepository;

    private final FrozenContext context = new FrozenContext();

    private HouseService houseService;

    @BeforeEach
    void init() {
        houseService =
                new HouseService(
                        mockHouseRepository,
                        mockClientRepository,
                        mockUserRepository,
                        mockHouseContactRepository,
                        context);
    }

    @SneakyThrows
    @Test
    void testCreateHouse_Success() {
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
        houseService.createHouse(addHouseRequest);
        // Then
        House expected =
                House.builder()
                        .houseCode(houseCode)
                        .phone(phone)
                        .addrLine1(address1)
                        .addrLine2(address2)
                        .city(city)
                        .state(state)
                        .deleted(false)
                        .postCode(postCode)
                        .build();
        // capture what was about to be persisted and make sure that is as expected
        ArgumentCaptor<House> houseArgumentCaptor = ArgumentCaptor.forClass(House.class);
        verify(mockHouseRepository).save(houseArgumentCaptor.capture());

        assertThat(
                expected,
                (allOf(
                        HasPropertyWithValue.hasProperty("houseCode", equalTo(houseCode)),
                        HasPropertyWithValue.hasProperty("phone", equalTo(phone)),
                        HasPropertyWithValue.hasProperty("addrLine1", equalTo(address1)),
                        HasPropertyWithValue.hasProperty("addrLine2", equalTo(address2)),
                        HasPropertyWithValue.hasProperty("city", equalTo(city)),
                        HasPropertyWithValue.hasProperty("state", equalTo(state)),
                        HasPropertyWithValue.hasProperty("deleted", equalTo(false)),
                        HasPropertyWithValue.hasProperty("postCode", equalTo(postCode)))));
    }

    @SneakyThrows
    @Test
    void testCreateHouse_WithoutField() {
        // Given
        String houseCode = "500";
        String address1 = "Address 1";
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
        houseService.createHouse(addHouseRequest);
        // Then
        House expected =
                House.builder()
                        .houseCode(houseCode)
                        .addrLine1(address1)
                        .city(city)
                        .state(state)
                        .deleted(false)
                        .postCode(postCode)
                        .build();
        // capture what was about to be persisted and make sure that is as expected
        ArgumentCaptor<House> houseArgumentCaptor = ArgumentCaptor.forClass(House.class);
        verify(mockHouseRepository).save(houseArgumentCaptor.capture());

        assertThat(
                expected,
                (allOf(
                        HasPropertyWithValue.hasProperty("houseCode", equalTo(houseCode)),
                        HasPropertyWithValue.hasProperty("addrLine1", equalTo(address1)),
                        HasPropertyWithValue.hasProperty("city", equalTo(city)),
                        HasPropertyWithValue.hasProperty("state", equalTo(state)),
                        HasPropertyWithValue.hasProperty("deleted", equalTo(false)),
                        HasPropertyWithValue.hasProperty("postCode", equalTo(postCode)))));
    }

    @SneakyThrows
    @Test
    void testAddClient_Success() {
        // Given
        String houseCode = "101";
        String clientName = "test";
        House house = House.builder().houseCode(houseCode).id(1).build();
        Client client = Client.builder().name(clientName).id(1).house(house).build();
        when(mockClientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(mockHouseRepository.getByHouseCode(houseCode)).thenReturn(house);

        // When
        houseService.addClient(1L, houseCode);

        // Then
        Client expected = Client.builder().name(clientName).id(1).house(house).build();
        verify(mockClientRepository).save(eq(expected));
    }

    @Test
    void testAddClient_Failure_ClientNotFound() {
        // Given
        String houseCode = "101";
        House house = House.builder().houseCode(houseCode).id(1).build();
        when(mockHouseRepository.getByHouseCode(houseCode)).thenReturn(house);

        assertThrows(
                HouseServiceException.class,
                () -> {
                    // When
                    houseService.addClient(1L, houseCode);
                });
    }

    @Test
    void testAddClient_Failure_HouseNotFound() {
        // Given
        String houseCode = "101";
        assertThrows(
                HouseServiceException.class,
                () -> {
                    // When
                    houseService.addClient(1L, houseCode);
                });
    }

    @SneakyThrows
    @Test
    void testGetHouses_Success() {
        // Given
        String houseCode = "100";
        House house = House.builder().id(1).houseCode(houseCode).build();
        HouseDTO expected = new ModelMapper().map(house, HouseDTO.class);
        when(mockHouseRepository.getByHouseCode(ArgumentMatchers.anyString())).thenReturn(house);
        GetHouseByCodeRequest request =
                GetHouseByCodeRequest.builder().houseCode(house.getHouseCode()).build();

        // When
        HouseDTO actual = houseService.getHouseByCode(request);

        // Then
        assertEquals(actual, expected);
    }

    @Test
    void testGetHouses_Failure_HouseNotFound() {
        // Given
        String houseCode = "100";
        GetHouseByCodeRequest request =
                GetHouseByCodeRequest.builder().houseCode(houseCode).build();

        assertThrows(
                HouseServiceException.class,
                () -> {
                    // When
                    houseService.getHouseByCode(request);
                });
    }

    @Test
    void testGetClients_Success() {
        // Given
        String houseCode = "100";
        House house = House.builder().id(1).houseCode(houseCode).build();
        String clientOne = "Client One";
        String clientTwo = "Client Two";

        when(mockClientRepository.findByHouse_HouseCode(houseCode))
                .thenReturn(
                        List.of(
                                Client.builder().id(1).name(clientOne).house(house).build(),
                                Client.builder().id(2).name(clientTwo).house(house).build()));
        GetClientsByHouseRequest request =
                GetClientsByHouseRequest.builder().houseCode(house.getHouseCode()).build();

        // When
        List<ClientDTO> clients = houseService.getClientsByHouse(request);

        // Then
        assertEquals(clients.size(), 2);
        assertThat(
                clients,
                (allOf(
                        everyItem(hasProperty("id", AnyOf.anyOf(equalTo(1L), equalTo(2L)))),
                        everyItem(
                                hasProperty(
                                        "name",
                                        AnyOf.anyOf(equalTo(clientOne), equalTo(clientTwo)))))));
    }

    @Test
    void testGetClients_Success_Empty() {
        // Given
        String houseCode = "100";
        GetClientsByHouseRequest request =
                GetClientsByHouseRequest.builder().houseCode(houseCode).build();

        // When
        List<ClientDTO> clients = houseService.getClientsByHouse(request);

        // Then
        assertEquals(clients.size(), 0);
    }

    @SneakyThrows
    @Test
    void testAddUser_Success() {
        // Given
        String houseCode = "101";
        long userId = 1;
        House house = House.builder().houseCode(houseCode).id(1).build();
        User user = User.builder().email("test@test.com").id(1).houses(new HashSet<>()).build();
        when(mockUserRepository.getById(userId)).thenReturn(user);
        when(mockHouseRepository.getByHouseCode(houseCode)).thenReturn(house);

        // When
        houseService.mapHouseToUser(userId, houseCode);

        // Then
        User expected =
                User.builder().id(userId).email("test@test.com").houses(Set.of(house)).build();
        verify(mockUserRepository).save(eq(expected));
    }

    @Test
    void testAddUser_Failure_UserNotFound() {
        // Given
        String houseCode = "101";
        long userId = 1;
        House house = House.builder().houseCode(houseCode).id(1).build();
        when(mockHouseRepository.getByHouseCode(houseCode)).thenReturn(house);

        assertThrows(
                HouseServiceException.class,
                () -> {
                    // When
                    houseService.mapHouseToUser(userId, houseCode);
                });
    }

    @Test
    void testAddUser_Failure_HouseNotFound() {
        // Given
        String houseCode = "101";
        long userId = 1;
        assertThrows(
                HouseServiceException.class,
                () -> {
                    // When
                    houseService.mapHouseToUser(userId, houseCode);
                });
    }

    @SneakyThrows
    @Test
    void testRemoveClient_Success() {
        // Given
        String houseCode = "102";
        long clientId = 1;
        House house = House.builder().houseCode(houseCode).id(1).build();
        Client client = Client.builder().id(clientId).name("test").house(house).build();
        var clients = new ArrayList<Client>();
        clients.add(client);
        house.setClients(clients);
        when(mockClientRepository.getById(clientId)).thenReturn(client);
        when(mockHouseRepository.getByHouseCode(houseCode)).thenReturn(house);

        // When
        houseService.removeClient(houseCode, clientId);

        // Then
        Client clientWithoutHouse = Client.builder().id(clientId).name("test").build();
        House houseWithoutClient =
                House.builder().houseCode(houseCode).clients(List.of()).id(1).build();

        ArgumentCaptor<House> houseArgumentCaptor = ArgumentCaptor.forClass(House.class);
        verify(mockHouseRepository).save(houseArgumentCaptor.capture());
        House actualWithoutClient = houseArgumentCaptor.getValue();

        assertEquals(houseWithoutClient, actualWithoutClient);
        ArgumentCaptor<Client> clientArgumentCaptor = ArgumentCaptor.forClass(Client.class);
        verify(mockClientRepository).save(clientArgumentCaptor.capture());
        Client actualWithoutHouse = clientArgumentCaptor.getValue();
        assertEquals(clientWithoutHouse, actualWithoutHouse);
    }

    @Test
    void testRemoveClient_Failure_ClientNotFound() {
        // Given
        String houseCode = "101";
        long clientId = 1;

        assertThrows(
                HouseServiceException.class,
                () -> {
                    // When
                    houseService.removeClient(houseCode, clientId);
                });
    }

    @Test
    void testListAllHouses_Success() {
        // Given
        House house1 =
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

        House house2 =
                House.builder()
                        .id(2)
                        .houseCode("200")
                        .phone("0412345687")
                        .addrLine1("xyz Line")
                        .addrLine2("abc Road")
                        .city("perth")
                        .state("Australia")
                        .deleted(false)
                        .postCode("3000")
                        .build();
        when(mockHouseRepository.findAllByDeleted(false)).thenReturn(List.of(house1, house2));
        List<HouseDTO> houses = houseService.listAllHouses();

        // Then

        assertEquals(2, houses.size());
        assertThat(
                houses,
                (allOf(
                        everyItem(hasProperty("id", anyOf(equalTo(1L), equalTo(2L)))),
                        everyItem(
                                hasProperty(
                                        "houseCode",
                                        anyOf(
                                                equalTo(house1.getHouseCode()),
                                                equalTo(house2.getHouseCode())))),
                        everyItem(
                                hasProperty(
                                        "phone",
                                        anyOf(
                                                equalTo(house1.getPhone()),
                                                equalTo(house2.getPhone())))),
                        everyItem(
                                hasProperty(
                                        "addrLine1",
                                        anyOf(
                                                equalTo(house1.getAddrLine1()),
                                                equalTo(house2.getAddrLine1())))),
                        everyItem(
                                hasProperty(
                                        "addrLine2",
                                        anyOf(
                                                equalTo(house1.getAddrLine2()),
                                                equalTo(house2.getAddrLine2())))),
                        everyItem(
                                hasProperty(
                                        "city",
                                        anyOf(
                                                equalTo(house1.getCity()),
                                                equalTo(house2.getCity())))),
                        everyItem(
                                hasProperty(
                                        "state",
                                        anyOf(
                                                equalTo(house1.getState()),
                                                equalTo(house2.getState())))),
                        everyItem(
                                hasProperty(
                                        "deleted",
                                        anyOf(
                                                equalTo(house1.getDeleted()),
                                                equalTo(house2.getDeleted())))),
                        everyItem(
                                hasProperty(
                                        "postCode",
                                        anyOf(
                                                equalTo(house1.getPostCode()),
                                                equalTo(house2.getPostCode())))))));
    }

    @Test
    void testListAllHouses_Success_Empty() {

        // When
        List<HouseDTO> houseDTOS = houseService.listAllHouses();

        // Then
        assertEquals(houseDTOS.size(), 0);
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateHouse_Success() {
        long id = 1;
        String houseCode = "50";
        String phone = "12 3456 7890";
        String address1 = "Updated Address 1";
        String address2 = "Updated Address 2";
        String city = "Updated city";
        String state = "Updated state";
        String postCode = "0000";
        House house =
                House.builder()
                        .houseCode(houseCode)
                        .phone("09 7865 4321")
                        .addrLine1("Address 1")
                        .addrLine2("Address 2")
                        .city("city")
                        .state("state")
                        .deleted(false)
                        .postCode("1111")
                        .build();

        AddHouseRequest request =
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
        when(mockHouseRepository.findById(1L)).thenReturn(Optional.of(house));

        // When
        houseService.updateHouse(request, id);
        House expected =
                House.builder()
                        .houseCode(houseCode)
                        .phone(phone)
                        .addrLine1(address1)
                        .addrLine2(address2)
                        .city(city)
                        .state(state)
                        .deleted(false)
                        .postCode(postCode)
                        .build();
        // Then
        verify(mockHouseRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateHouse_Failure_HouseNotFound() {
        AddHouseRequest addHouseRequest =
                AddHouseRequest.builder()
                        .houseCode("100")
                        .phone("09 7865 4321")
                        .addrLine1("Address 1")
                        .addrLine2("Address 2")
                        .city("city")
                        .state("state")
                        .deleted(false)
                        .postCode("1111")
                        .build();
        assertThrows(
                HouseServiceException.class,
                () -> {
                    // When
                    houseService.updateHouse(addHouseRequest, 1L);
                });
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testCreateContact_Success() {
        // Given
        String houseCode = "101";
        House house = House.builder().houseCode(houseCode).build();
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
        HouseContactRequest request =
                HouseContactRequest.builder()
                        .houseCode(List.of(houseCode))
                        .caption(caption)
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
        User user = User.builder().email(context.currentUser()).id(1).build();

        when(mockUserRepository.getByEmail(context.currentUser())).thenReturn(user);
        houseService.createHouseContact(request);

        // Then
        HouseContact expected =
                HouseContact.builder()
                        .houses(Set.of(house))
                        .caption(request.getCaption())
                        .status(NoticeStatus.ACTIVE)
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .email(request.getEmail())
                        .phone1(request.getPhone1())
                        .phone2(request.getPhone2())
                        .address1(request.getAddress1())
                        .address2(request.getAddress2())
                        .city(request.getCity())
                        .state(request.getState())
                        .zip(request.getZip())
                        .notes(request.getNotes())
                        .lastUpdatedBy(lastUpdatedBy)
                        .lastUpdatedAt(context.now())
                        .build();

        ArgumentCaptor<HouseContact> houseContactArgumentCaptor =
                ArgumentCaptor.forClass(HouseContact.class);
        verify(mockHouseContactRepository).save(houseContactArgumentCaptor.capture());
        HouseContact actual = houseContactArgumentCaptor.getValue();

        assertThat(
                expected,
                (allOf(
                        HasPropertyWithValue.hasProperty("caption", equalTo(caption)),
                        HasPropertyWithValue.hasProperty("firstName", equalTo(firstName)),
                        HasPropertyWithValue.hasProperty("lastName", equalTo(lastName)),
                        HasPropertyWithValue.hasProperty("email", equalTo(email)),
                        HasPropertyWithValue.hasProperty("phone1", equalTo(phone1)),
                        HasPropertyWithValue.hasProperty("phone2", equalTo(phone2)),
                        HasPropertyWithValue.hasProperty("address1", equalTo(address1)),
                        HasPropertyWithValue.hasProperty("address2", equalTo(address2)),
                        HasPropertyWithValue.hasProperty("city", equalTo(city)),
                        HasPropertyWithValue.hasProperty("state", equalTo(state)),
                        HasPropertyWithValue.hasProperty("zip", equalTo(zip)),
                        HasPropertyWithValue.hasProperty("notes", equalTo(notes)),
                        HasPropertyWithValue.hasProperty("lastUpdatedBy", equalTo(lastUpdatedBy)),
                        HasPropertyWithValue.hasProperty(
                                "lastUpdatedAt", equalTo(context.now())))));
    }

    @SneakyThrows
    @Test
    void testGetHouseContact_Success() {
        // Given
        House house = House.builder().houseCode("101").build();
        Instant lastUpdatedAt = context.now();
        HouseContact houseContact =
                HouseContact.builder()
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
                        .notes("notes")
                        .lastUpdatedBy("lastUpdatedBy")
                        .lastUpdatedAt(lastUpdatedAt)
                        .build();

        when(mockHouseContactRepository.findById(1L)).thenReturn(Optional.of(houseContact));
        HouseContactDTO expected = new ModelMapper().map(houseContact, HouseContactDTO.class);

        // When
        HouseContactDTO actual = houseService.getHouseContactById(1L);

        // Then
        assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    void testGetHouseContact_Failure() {
        // Given
        long id = 1;
        // When
        assertThrows(
                HouseServiceException.class,
                () -> {
                    // When
                    houseService.getHouseContactById(id);
                });
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateHouseContact_Success() {
        // Given
        String houseCode = "101";
        House house = House.builder().houseCode(houseCode).build();
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
                        .status(NoticeStatus.ACTIVE)
                        .caption("Room2")
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
                        .lastUpdatedAt(context.now())
                        .build();

        UpdateHouseContactRequest request =
                UpdateHouseContactRequest.builder()
                        .caption(caption)
                        .status(NoticeStatus.INACTIVE.name())
                        .houseCode(List.of(houseCode))
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
        when(mockHouseRepository.getByHouseCode(houseCode)).thenReturn(house);
        when(mockHouseContactRepository.findAllByHouses_HouseCodeIn(List.of(house.getHouseCode())))
                .thenReturn(List.of(houseContact));
        when(mockHouseContactRepository.findById(2L)).thenReturn(Optional.of(houseContact));

        // when
        houseService.updateHouseContact(houseContact.getId(), request);

        HouseContact expected =
                HouseContact.builder()
                        .id(houseContact.getId())
                        .houses(Set.of(house))
                        .caption(caption)
                        .status(NoticeStatus.INACTIVE)
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
                        .lastUpdatedAt(context.now())
                        .build();

        // then
        verify(mockHouseContactRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateHouseContact_Failure() {
        // Given
        UpdateHouseContactRequest request =
                UpdateHouseContactRequest.builder()
                        .caption("Room2")
                        .status(NoticeStatus.ACTIVE.name())
                        .houseCode(List.of("102"))
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
                        .build();
        assertThrows(
                HouseServiceException.class,
                () -> {

                    // When
                    houseService.updateHouseContact(1L, request);
                });
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testListHouseContacts_Success() {
        // Given
        String houseCode = "100";
        House house = House.builder().id(1).houseCode(houseCode).build();

        HouseContact houseContact1 =
                HouseContact.builder()
                        .id(1L)
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
                        .notes("notes")
                        .lastUpdatedBy("lastUpdatedBy")
                        .lastUpdatedAt(context.now())
                        .build();
        when(mockHouseContactRepository.findAllByHouses_HouseCode(any(), any()))
                .thenReturn(List.of(houseContact1));
        ListHouseContactRequest request =
                ListHouseContactRequest.builder()
                        .houseCode(houseCode)
                        .pageNumber(1)
                        .pageSize(2)
                        .build();
        // When
        List<HouseContactDTO> houseContacts = houseService.listHouseContacts(request);

        // Then
        assertEquals(houseContacts.size(), 1);
        assertThat(
                houseContacts,
                (allOf(
                        everyItem(hasProperty("id", AnyOf.anyOf(equalTo(1L)))),
                        everyItem(
                                hasProperty(
                                        "caption",
                                        AnyOf.anyOf(equalTo(houseContact1.getCaption())))),
                        everyItem(
                                hasProperty("status", anyOf(equalTo(NoticeStatus.ACTIVE.name())))),
                        everyItem(
                                hasProperty(
                                        "firstName",
                                        AnyOf.anyOf(equalTo(houseContact1.getFirstName())))),
                        everyItem(
                                hasProperty(
                                        "lastName",
                                        AnyOf.anyOf(equalTo(houseContact1.getLastName())))),
                        everyItem(
                                hasProperty(
                                        "email", AnyOf.anyOf(equalTo(houseContact1.getEmail())))),
                        everyItem(
                                hasProperty(
                                        "phone1", AnyOf.anyOf(equalTo(houseContact1.getPhone1())))),
                        everyItem(
                                hasProperty(
                                        "phone2", AnyOf.anyOf(equalTo(houseContact1.getPhone2())))),
                        everyItem(
                                hasProperty(
                                        "address1",
                                        AnyOf.anyOf(equalTo(houseContact1.getAddress1())))),
                        everyItem(
                                hasProperty(
                                        "address2",
                                        AnyOf.anyOf(equalTo(houseContact1.getAddress2())))),
                        everyItem(
                                hasProperty("city", AnyOf.anyOf(equalTo(houseContact1.getCity())))),
                        everyItem(hasProperty("zip", AnyOf.anyOf(equalTo(houseContact1.getZip())))),
                        everyItem(
                                hasProperty(
                                        "notes", AnyOf.anyOf(equalTo(houseContact1.getNotes())))),
                        everyItem(
                                hasProperty(
                                        "lastUpdatedBy",
                                        AnyOf.anyOf(equalTo(houseContact1.getLastUpdatedBy())))),
                        everyItem(
                                hasProperty(
                                        "lastUpdatedAt", AnyOf.anyOf(equalTo(context.now())))))));
    }

    @SneakyThrows
    @Test
    void testListHouseContacts_Success_Empty() {
        // Given
        String houseCode = "100";
        ListHouseContactRequest request =
                ListHouseContactRequest.builder()
                        .houseCode(houseCode)
                        .pageNumber(1)
                        .pageSize(2)
                        .build();

        // When
        List<HouseContactDTO> houseContactDTOS = houseService.listHouseContacts(request);

        // Then
        assertEquals(houseContactDTOS.size(), 0);
    }
}
