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
import com.onedoorway.project.exception.UserServiceException;
import com.onedoorway.project.model.House;
import com.onedoorway.project.model.PasswordResetToken;
import com.onedoorway.project.model.Role;
import com.onedoorway.project.model.User;
import com.onedoorway.project.repository.HouseRepository;
import com.onedoorway.project.repository.PasswordTokenRepository;
import com.onedoorway.project.repository.RoleRepository;
import com.onedoorway.project.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository mockUserRepository;
    @Mock RoleRepository mockRoleRepository;
    @Mock PasswordEncoder mockPasswordEncoder;
    @Mock HouseRepository mockHouseRepository;
    @Mock PasswordTokenRepository mockPasswordTokenRepository;
    @Mock EmailSenderService mockEmailSenderService;

    FrozenContext context = new FrozenContext();

    private UserService userService;

    @BeforeEach
    void init() {
        userService =
                new UserService(
                        mockUserRepository,
                        mockRoleRepository,
                        mockPasswordEncoder,
                        mockHouseRepository,
                        mockPasswordTokenRepository,
                        mockEmailSenderService,
                        "a-mock-url",
                        context);
    }

    @Test
    @SneakyThrows
    void testRegisterWithEmailAndPassword() {
        // Given
        String email = "test@test.com";
        String password = "test";
        String firstName = "First";
        String lastName = "Last";
        String phone = "61 7010 1111";
        String mobile = "0412 555 666";
        Role role = Role.builder().id(1).name("ROLE_USER").build();
        when(mockRoleRepository.getByName("USER")).thenReturn(role);
        when(mockPasswordEncoder.encode(password)).thenReturn(password);

        // When
        userService.register(email, password, firstName, lastName, phone, mobile);

        // Then
        User expected =
                User.builder()
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .phone(phone)
                        .mobile(mobile)
                        .deleted(false)
                        .password(password)
                        .roles(new HashSet<>(Arrays.asList(role)))
                        .build();

        verify(mockUserRepository).save(ArgumentMatchers.eq(expected));
    }

    @Test
    void testRegisterWithEmailAndPassword_with_no_role() {
        // Given
        String email = "test@test.com";
        String password = "test";
        String firstName = "First";
        String lastName = "Last";
        String phone = "61 7010 1111";
        String mobile = "0412 555 666";

        assertThrows(
                UserServiceException.class,
                () -> {
                    // When
                    userService.register(email, password, firstName, lastName, phone, mobile);
                });
    }

    @Test
    void testRegisterWithEmail_not_unique() {
        // Given
        String email = "test@test.com";
        String password = "test";
        String firstName = "First";
        String lastName = "Last";
        String phone = "61 7010 1112";
        String mobile = "0412 555 666";
        when(mockUserRepository.findByEmailIgnoreCase(email)).thenReturn(Mockito.mock(User.class));

        assertThrows(
                UserServiceException.class,
                () -> {
                    // When
                    userService.register(email, password, firstName, lastName, phone, mobile);
                });
    }

    @Test
    void testListAllUsers_Success() {
        // Given
        String email1 = "UserOne@gmail.com";
        String email2 = "UserTwo@gmail.com";
        Role role = Role.builder().name("USER").build();
        House house1 = House.builder().houseCode("111").id(1).build();
        House house2 = House.builder().houseCode("112").id(2).build();
        User user1 =
                User.builder()
                        .id(1)
                        .email(email1)
                        .firstName("User")
                        .lastName("One")
                        .phone("61 7060 1161")
                        .mobile("0412 505 066")
                        .roles(Set.of(role))
                        .deleted(false)
                        .houses(Set.of(house1))
                        .build();
        User user2 =
                User.builder()
                        .id(2)
                        .email(email2)
                        .firstName("User")
                        .lastName("Two")
                        .phone("61 7710 1711")
                        .mobile("0412 553 662")
                        .roles(Set.of(role))
                        .deleted(false)
                        .houses(Set.of(house2))
                        .build();

        when(mockUserRepository.findAllByDeleted(false, Pageable.unpaged()))
                .thenReturn(List.of(user1, user2));
        // When
        List<UserShortDTO> users = userService.listAllUsers();

        // Then
        assertEquals(2, users.size());
        assertThat(
                users,
                (allOf(
                        everyItem(hasProperty("id", anyOf(equalTo(1L), equalTo(2L)))),
                        everyItem(
                                hasProperty(
                                        "email",
                                        anyOf(
                                                equalTo(user1.getEmail()),
                                                equalTo(user2.getEmail())))),
                        everyItem(
                                hasProperty(
                                        "firstName",
                                        anyOf(
                                                equalTo(user1.getFirstName()),
                                                equalTo(user2.getFirstName())))),
                        everyItem(
                                hasProperty(
                                        "lastName",
                                        anyOf(
                                                equalTo(user1.getLastName()),
                                                equalTo(user2.getLastName())))))));
    }

    @Test
    void testListAllUsers_Success_Empty() {

        // When
        List<UserShortDTO> userDTOS = userService.listAllUsers();

        // Then
        assertEquals(userDTOS.size(), 0);
    }

    @Test
    void testListAllUsersByPageName_Success() {
        // Given
        String email1 = "UserOne@gmail.com";
        String email2 = "UserTwo@gmail.com";
        String email3 = "UserThree@gmail.com";
        Role role = Role.builder().name("USER").build();
        House house1 = House.builder().houseCode("101").id(1).build();
        House house2 = House.builder().houseCode("102").id(2).build();
        House house3 = House.builder().houseCode("103").id(3).build();
        User user1 =
                User.builder()
                        .id(1)
                        .email(email1)
                        .firstName("User1")
                        .lastName("One")
                        .phone("61 7060 1161")
                        .mobile("0412 505 066")
                        .roles(Set.of(role))
                        .deleted(false)
                        .houses(Set.of(house1))
                        .build();
        User user2 =
                User.builder()
                        .id(2)
                        .email(email2)
                        .firstName("User2")
                        .lastName("Two")
                        .phone("61 7710 1711")
                        .mobile("0412 553 662")
                        .roles(Set.of(role))
                        .deleted(false)
                        .houses(Set.of(house2))
                        .build();
        User user3 =
                User.builder()
                        .id(3)
                        .email(email3)
                        .firstName("User3")
                        .lastName("Three")
                        .phone("61 7640 1321")
                        .mobile("0412 567 362")
                        .roles(Set.of(role))
                        .deleted(false)
                        .houses(Set.of(house3))
                        .build();
        when(mockUserRepository
                        .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndDeleted(
                                any(), any(), any(), eq(false), any(Pageable.class)))
                .thenReturn(List.of(user1));

        ListUsersByPageRequest request =
                ListUsersByPageRequest.builder()
                        .pageNumber(0)
                        .pageSize(1)
                        .nameOrEmail(user1.getFirstName())
                        .build();

        // When
        List<UserDTO> users = userService.listAllUsersByPage(request);

        // Then
        assertEquals(1, users.size());
        assertThat(
                users,
                (allOf(
                        everyItem(hasProperty("id", anyOf(equalTo(1L), equalTo(2L), equalTo(3L)))),
                        everyItem(
                                hasProperty(
                                        "email",
                                        anyOf(
                                                equalTo(user1.getEmail()),
                                                equalTo(user2.getEmail()),
                                                equalTo(user3.getEmail())))),
                        everyItem(
                                hasProperty(
                                        "firstName",
                                        anyOf(
                                                equalTo(user1.getFirstName()),
                                                equalTo(user2.getFirstName()),
                                                equalTo(user3.getFirstName())))),
                        everyItem(
                                hasProperty(
                                        "lastName",
                                        anyOf(
                                                equalTo(user1.getLastName()),
                                                equalTo(user2.getLastName()),
                                                equalTo(user3.getLastName())))),
                        everyItem(
                                hasProperty(
                                        "phone",
                                        anyOf(
                                                equalTo(user1.getPhone()),
                                                equalTo(user2.getPhone()),
                                                equalTo(user3.getPhone())))),
                        everyItem(
                                hasProperty(
                                        "mobile",
                                        anyOf(
                                                equalTo(user1.getMobile()),
                                                equalTo(user2.getMobile()),
                                                equalTo(user3.getMobile())))),
                        everyItem(
                                hasProperty(
                                        "houses",
                                        anyOf(
                                                equalTo(List.of(house1.getHouseCode())),
                                                equalTo(List.of(house2.getHouseCode())),
                                                equalTo(List.of(house3.getHouseCode()))))))));
    }

    @Test
    void testListAllUsersByPageHouse_Success() {
        // Given
        String email1 = "UserOne@gmail.com";
        String email2 = "UserTwo@gmail.com";
        String email3 = "UserThree@gmail.com";
        Role role = Role.builder().name("USER").build();
        House house1 = House.builder().houseCode("101").id(1).build();
        House house2 = House.builder().houseCode("102").id(2).build();
        House house3 = House.builder().houseCode("103").id(3).build();
        User user1 =
                User.builder()
                        .id(1)
                        .email(email1)
                        .firstName("User1")
                        .lastName("One")
                        .phone("61 7060 1161")
                        .mobile("0412 505 066")
                        .roles(Set.of(role))
                        .deleted(false)
                        .houses(Set.of(house1))
                        .build();
        User user2 =
                User.builder()
                        .id(2)
                        .email(email2)
                        .firstName("User2")
                        .lastName("Two")
                        .phone("61 7710 1711")
                        .mobile("0412 553 662")
                        .roles(Set.of(role))
                        .deleted(false)
                        .houses(Set.of(house2))
                        .build();
        User user3 =
                User.builder()
                        .id(3)
                        .email(email3)
                        .firstName("User3")
                        .lastName("Three")
                        .phone("61 7640 1321")
                        .mobile("0412 567 362")
                        .roles(Set.of(role))
                        .deleted(false)
                        .houses(Set.of(house3))
                        .build();
        when(mockUserRepository.findByHouses_HouseCodeContainingIgnoreCaseAndDeleted(
                        any(), eq(false), any(Pageable.class)))
                .thenReturn(List.of(user2));

        ListUsersByPageRequest request =
                ListUsersByPageRequest.builder()
                        .pageNumber(0)
                        .pageSize(1)
                        .houseCode(house2.getHouseCode())
                        .build();
        // When
        List<UserDTO> users = userService.listAllUsersByPage(request);

        // Then
        assertEquals(1, users.size());
        assertThat(
                users,
                (allOf(
                        everyItem(hasProperty("id", anyOf(equalTo(1L), equalTo(2L), equalTo(3L)))),
                        everyItem(
                                hasProperty(
                                        "email",
                                        anyOf(
                                                equalTo(user1.getEmail()),
                                                equalTo(user2.getEmail()),
                                                equalTo(user3.getEmail())))),
                        everyItem(
                                hasProperty(
                                        "firstName",
                                        anyOf(
                                                equalTo(user1.getFirstName()),
                                                equalTo(user2.getFirstName()),
                                                equalTo(user3.getFirstName())))),
                        everyItem(
                                hasProperty(
                                        "lastName",
                                        anyOf(
                                                equalTo(user1.getLastName()),
                                                equalTo(user2.getLastName()),
                                                equalTo(user3.getLastName())))),
                        everyItem(
                                hasProperty(
                                        "phone",
                                        anyOf(
                                                equalTo(user1.getPhone()),
                                                equalTo(user2.getPhone()),
                                                equalTo(user3.getPhone())))),
                        everyItem(
                                hasProperty(
                                        "mobile",
                                        anyOf(
                                                equalTo(user1.getMobile()),
                                                equalTo(user2.getMobile()),
                                                equalTo(user3.getMobile())))),
                        everyItem(
                                hasProperty(
                                        "houses",
                                        anyOf(
                                                equalTo(List.of(house1.getHouseCode())),
                                                equalTo(List.of(house2.getHouseCode())),
                                                equalTo(List.of(house3.getHouseCode()))))))));
    }

    @Test
    void testListAllUsersByPageNameHouse_Success() {
        // Given
        String email1 = "UserOne@gmail.com";
        String email2 = "UserTwo@gmail.com";
        String email3 = "UserThree@gmail.com";
        Role role = Role.builder().name("USER").build();
        House house1 = House.builder().houseCode("101").id(1).build();
        House house2 = House.builder().houseCode("102").id(2).build();
        House house3 = House.builder().houseCode("103").id(3).build();
        User user1 =
                User.builder()
                        .id(1)
                        .email(email1)
                        .firstName("User1")
                        .lastName("One")
                        .phone("61 7060 1161")
                        .mobile("0412 505 066")
                        .roles(Set.of(role))
                        .deleted(false)
                        .houses(Set.of(house1))
                        .build();
        User user2 =
                User.builder()
                        .id(2)
                        .email(email2)
                        .firstName("User2")
                        .lastName("Two")
                        .phone("61 7710 1711")
                        .mobile("0412 553 662")
                        .roles(Set.of(role))
                        .deleted(false)
                        .houses(Set.of(house2))
                        .build();
        User user3 =
                User.builder()
                        .id(3)
                        .email(email3)
                        .firstName("User3")
                        .lastName("Three")
                        .phone("61 7640 1321")
                        .mobile("0412 567 362")
                        .roles(Set.of(role))
                        .deleted(false)
                        .houses(Set.of(house3))
                        .build();
        when(mockUserRepository
                        .findByFirstNameContainingIgnoreCaseAndHouses_HouseCodeContainingIgnoreCaseAndDeletedOrLastNameContainingIgnoreCaseAndHouses_HouseCodeContainingIgnoreCaseAndDeletedOrEmailContainingIgnoreCaseAndHouses_HouseCodeContainingIgnoreCaseAndDeleted(
                                any(),
                                any(),
                                eq(false),
                                any(),
                                any(),
                                eq(false),
                                any(),
                                any(),
                                eq(false),
                                any(Pageable.class)))
                .thenReturn(List.of(user3));

        ListUsersByPageRequest request =
                ListUsersByPageRequest.builder()
                        .pageNumber(0)
                        .pageSize(1)
                        .nameOrEmail("Three")
                        .houseCode("3")
                        .build();
        // When
        List<UserDTO> users = userService.listAllUsersByPage(request);

        // Then
        assertEquals(1, users.size());
        assertThat(
                users,
                (allOf(
                        everyItem(hasProperty("id", anyOf(equalTo(1L), equalTo(2L), equalTo(3L)))),
                        everyItem(
                                hasProperty(
                                        "email",
                                        anyOf(
                                                equalTo(user1.getEmail()),
                                                equalTo(user2.getEmail()),
                                                equalTo(user3.getEmail())))),
                        everyItem(
                                hasProperty(
                                        "firstName",
                                        anyOf(
                                                equalTo(user1.getFirstName()),
                                                equalTo(user2.getFirstName()),
                                                equalTo(user3.getFirstName())))),
                        everyItem(
                                hasProperty(
                                        "lastName",
                                        anyOf(
                                                equalTo(user1.getLastName()),
                                                equalTo(user2.getLastName()),
                                                equalTo(user3.getLastName())))),
                        everyItem(
                                hasProperty(
                                        "phone",
                                        anyOf(
                                                equalTo(user1.getPhone()),
                                                equalTo(user2.getPhone()),
                                                equalTo(user3.getPhone())))),
                        everyItem(
                                hasProperty(
                                        "mobile",
                                        anyOf(
                                                equalTo(user1.getMobile()),
                                                equalTo(user2.getMobile()),
                                                equalTo(user3.getMobile())))),
                        everyItem(
                                hasProperty(
                                        "houses",
                                        anyOf(
                                                equalTo(List.of(house1.getHouseCode())),
                                                equalTo(List.of(house2.getHouseCode())),
                                                equalTo(List.of(house3.getHouseCode()))))))));
    }

    @Test
    void testListAllUsersByPageSuccess() {
        // Given
        String email1 = "UserOne@gmail.com";
        String email2 = "UserTwo@gmail.com";
        String email3 = "UserThree@gmail.com";
        Role role = Role.builder().name("USER").build();
        House house1 = House.builder().houseCode("101").id(1).build();
        House house2 = House.builder().houseCode("102").id(2).build();
        House house3 = House.builder().houseCode("103").id(3).build();
        User user1 =
                User.builder()
                        .id(1)
                        .email(email1)
                        .firstName("User1")
                        .lastName("One")
                        .phone("61 7060 1161")
                        .mobile("0412 505 066")
                        .roles(Set.of(role))
                        .deleted(false)
                        .houses(Set.of(house1))
                        .build();
        User user2 =
                User.builder()
                        .id(2)
                        .email(email2)
                        .firstName("User2")
                        .lastName("Two")
                        .phone("61 7710 1711")
                        .mobile("0412 553 662")
                        .roles(Set.of(role))
                        .deleted(false)
                        .houses(Set.of(house2))
                        .build();
        User user3 =
                User.builder()
                        .id(3)
                        .email(email3)
                        .firstName("User3")
                        .lastName("Three")
                        .phone("61 7640 1321")
                        .mobile("0412 567 362")
                        .roles(Set.of(role))
                        .deleted(false)
                        .houses(Set.of(house3))
                        .build();
        when(mockUserRepository.findAllByDeleted(eq(false), any(Pageable.class)))
                .thenReturn(List.of(user1, user2, user3));

        ListUsersByPageRequest request =
                ListUsersByPageRequest.builder().pageNumber(0).pageSize(1).build();
        // When
        List<UserDTO> users = userService.listAllUsersByPage(request);

        // Then
        assertEquals(3, users.size());
        assertThat(
                users,
                (allOf(
                        everyItem(hasProperty("id", anyOf(equalTo(1L), equalTo(2L), equalTo(3L)))),
                        everyItem(
                                hasProperty(
                                        "email",
                                        anyOf(
                                                equalTo(user1.getEmail()),
                                                equalTo(user2.getEmail()),
                                                equalTo(user3.getEmail())))),
                        everyItem(
                                hasProperty(
                                        "firstName",
                                        anyOf(
                                                equalTo(user1.getFirstName()),
                                                equalTo(user2.getFirstName()),
                                                equalTo(user3.getFirstName())))),
                        everyItem(
                                hasProperty(
                                        "lastName",
                                        anyOf(
                                                equalTo(user1.getLastName()),
                                                equalTo(user2.getLastName()),
                                                equalTo(user3.getLastName())))),
                        everyItem(
                                hasProperty(
                                        "phone",
                                        anyOf(
                                                equalTo(user1.getPhone()),
                                                equalTo(user2.getPhone()),
                                                equalTo(user3.getPhone())))),
                        everyItem(
                                hasProperty(
                                        "mobile",
                                        anyOf(
                                                equalTo(user1.getMobile()),
                                                equalTo(user2.getMobile()),
                                                equalTo(user3.getMobile())))),
                        everyItem(
                                hasProperty(
                                        "houses",
                                        anyOf(
                                                equalTo(List.of(house1.getHouseCode())),
                                                equalTo(List.of(house2.getHouseCode())),
                                                equalTo(List.of(house3.getHouseCode()))))))));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testListUserByHouseCode_Success() {
        // Given
        String houseCode = "104";
        String email1 = "UserOne@gmail.com";
        String email2 = "UserTwo@gmail.com";
        Role role = Role.builder().name("USER").build();
        mockRoleRepository.save(role);
        House house = House.builder().houseCode(houseCode).id(1).build();
        User user1 =
                User.builder()
                        .id(1)
                        .email(email1)
                        .firstName("User")
                        .lastName("One")
                        .phone("61 7060 1161")
                        .mobile("0412 505 066")
                        .roles(Set.of(role))
                        .deleted(false)
                        .houses(Set.of(house))
                        .build();
        User user2 =
                User.builder()
                        .id(2)
                        .email(email2)
                        .firstName("User")
                        .lastName("Two")
                        .phone("61 7710 1711")
                        .mobile("0412 553 662")
                        .roles(Set.of(role))
                        .deleted(false)
                        .houses(Set.of(house))
                        .build();
        when(mockUserRepository.findByHouses_HouseCodeAndDeleted(
                        houseCode, false, Pageable.unpaged()))
                .thenReturn(List.of(user1, user2));
        ListUserRequest request = ListUserRequest.builder().houseCode(house.getHouseCode()).build();
        // When
        List<UserShortDTO> users = userService.listUsersByHouseCode(request);

        // Then
        assertEquals(2, users.size());
        assertThat(
                users,
                (allOf(
                        everyItem(hasProperty("id", anyOf(equalTo(1L), equalTo(2L)))),
                        everyItem(
                                hasProperty(
                                        "email",
                                        anyOf(
                                                equalTo(user1.getEmail()),
                                                equalTo(user2.getEmail())))),
                        everyItem(
                                hasProperty(
                                        "firstName",
                                        anyOf(
                                                equalTo(user1.getFirstName()),
                                                equalTo(user2.getFirstName())))),
                        everyItem(
                                hasProperty(
                                        "lastName",
                                        anyOf(
                                                equalTo(user1.getLastName()),
                                                equalTo(user2.getLastName())))))));
    }

    @Test
    void testListUserByHouseCode_Success_Empty() {
        // Given
        String houseCode = "104";
        ListUserRequest request = ListUserRequest.builder().houseCode(houseCode).build();

        // When
        List<UserShortDTO> userDTOS = userService.listUsersByHouseCode(request);

        // Then
        assertEquals(userDTOS.size(), 0);
    }

    @SneakyThrows
    @Test
    void testAddRole_Success() {
        // Given
        long roleId = 1;
        long userId = 1;
        Role role = Role.builder().name("USER").id(1).build();
        User user = User.builder().email("test@test.com").id(1).roles(new HashSet<>()).build();
        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mockRoleRepository.findById(roleId)).thenReturn(Optional.of(role));

        // When
        userService.addRoleToUser(userId, roleId);

        User expected =
                User.builder().id(userId).email("test@test.com").roles(Set.of(role)).build();
        verify(mockUserRepository).save(eq(expected));
    }

    @Test
    void testAddRole_Failure_RoleNotFound() {
        // Given
        long roleId = 1;
        long userId = 1;
        User user = User.builder().email("test@test.com").id(userId).build();
        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(
                UserServiceException.class,
                () -> {
                    // When
                    userService.addRoleToUser(userId, roleId);
                });
    }

    @Test
    void testAddRole_Failure_UserNotFound() {
        // Given
        long roleId = 1;
        long userId = 1;
        assertThrows(
                UserServiceException.class,
                () -> {
                    // When
                    userService.addRoleToUser(userId, roleId);
                });
    }

    @SneakyThrows
    @Test
    void testRemoveRole_Success() {
        // Given
        long userId = 1;
        long roleId = 1;
        Role role = Role.builder().name("TEST_USER").build();

        User user = User.builder().id(userId).roles(Set.of(role)).build();
        var roles = new HashSet<Role>();
        roles.add(role);
        user.setRoles(roles);

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mockRoleRepository.findById(roleId)).thenReturn(Optional.of(role));

        // When
        userService.removeRoleFromUser(userId, roleId);

        // Then
        User userWithoutRole = User.builder().roles(Set.of()).id(userId).build();
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserRepository).save(userArgumentCaptor.capture());
        User actualWithoutRole = userArgumentCaptor.getValue();

        assertEquals(userWithoutRole, actualWithoutRole);
    }

    @Test
    void testRemoveRole_Failure_RoleNotFound() {
        // Given
        long roleId = 1;
        long userId = 1;
        User user = User.builder().email("test@test.com").id(userId).build();
        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(
                UserServiceException.class,
                () -> {
                    // When
                    userService.removeRoleFromUser(userId, roleId);
                });
    }

    @SneakyThrows
    @Test
    void testRemoveRole_FailureUserNotFound() {
        // Given
        long userId = 1;
        long roleId = 1;
        assertThrows(
                UserServiceException.class,
                () -> {
                    // When
                    userService.removeRoleFromUser(userId, roleId);
                });
    }

    @SneakyThrows
    @Test
    void testRemoveHouse_Success() {
        // Given
        String houseCode = "102";
        long userId = 1;
        House house = House.builder().id(1).houseCode(houseCode).build();
        User user =
                User.builder().id(userId).email("addhtest@test.com").houses(Set.of(house)).build();
        var houses = new HashSet<House>();
        houses.add(house);
        user.setHouses(houses);

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mockHouseRepository.getByHouseCode(houseCode)).thenReturn(house);

        // When
        userService.removeHouseFromUser(userId, houseCode);

        // Then
        User userWithoutHouse =
                User.builder().id(userId).email("addhtest@test.com").houses(Set.of()).build();

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserRepository).save(userArgumentCaptor.capture());
        User actualWithoutHouse = userArgumentCaptor.getValue();
        assertEquals(userWithoutHouse, actualWithoutHouse);
    }

    @SneakyThrows
    @Test
    void testRemoveHouse_Failure_HouseNotFound() {
        // Given
        String houseCode = "102";
        long userId = 1;
        User user = User.builder().id(userId).email("addhtest@test.com").build();

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));
        assertThrows(
                UserServiceException.class,
                () -> {
                    // When
                    userService.removeHouseFromUser(userId, houseCode);
                });
    }

    @SneakyThrows
    @Test
    void testRemoveHouse_Failure_UserNotFound() {
        // Given
        String houseCode = "102";
        long userId = 1;
        assertThrows(
                UserServiceException.class,
                () -> {
                    // When
                    userService.removeHouseFromUser(userId, houseCode);
                });
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testChangePassword_Success() {
        String password = "welcome";

        User user =
                User.builder()
                        .email(context.currentUser())
                        .password("password")
                        .firstName("First")
                        .lastName("Last")
                        .build();

        ChangePasswordRequest request =
                ChangePasswordRequest.builder()
                        .newPassword(password)
                        .oldPassword(user.getPassword())
                        .build();
        when(mockUserRepository.getByEmail(context.currentUser())).thenReturn(user);
        when(mockPasswordEncoder.encode(password)).thenReturn(password);
        when(mockPasswordEncoder.matches(any(), any())).thenReturn(true);

        // when
        userService.updatePassword(request);
        User expected =
                User.builder()
                        .email(context.currentUser())
                        .password(request.getNewPassword())
                        .firstName("First")
                        .lastName("Last")
                        .build();
        // then
        verify(mockUserRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    void testResetPassword() {
        // Given
        String newPassword = "simple";
        String token = "token";
        LocalDateTime dateTime = LocalDateTime.now().plusDays(1L);

        User user = User.builder().id(1L).email("test@test.com").password("simple").build();

        PasswordResetToken passwordResetToken =
                PasswordResetToken.builder()
                        .id(1L)
                        .user(user)
                        .token(token)
                        .expiryDate(dateTime)
                        .build();
        mockPasswordTokenRepository.save(passwordResetToken);

        ResetPasswordRequest request =
                ResetPasswordRequest.builder()
                        .newPassword(newPassword)
                        .confirmPassword(user.getPassword())
                        .token(passwordResetToken.getToken())
                        .build();

        when(mockPasswordTokenRepository.getByToken(passwordResetToken.getToken()))
                .thenReturn(Optional.of(passwordResetToken));
        when(mockUserRepository.getById(user.getId())).thenReturn(user);
        when(mockPasswordEncoder.encode(newPassword)).thenReturn(newPassword);

        // when
        userService.resetPassword(request);
        PasswordResetToken expected =
                PasswordResetToken.builder()
                        .id(1L)
                        .user(user)
                        .token(passwordResetToken.getToken())
                        .expiryDate(dateTime)
                        .build();
        // then
        verify(mockPasswordTokenRepository).save(eq(expected));
        // TODO: verify the interaction to the email sending service and assert the arguments
        // verify(mockEmailSenderService, times(1)).sendEmail(anyString(), anyString(),
        // anyString());

    }

    @SneakyThrows
    @Test
    void testForgotPassword_Success() {
        // Given
        String token = "token";
        LocalDateTime dateTime = LocalDateTime.now().plusDays(1L);
        User user =
                User.builder()
                        .email("test@test.com")
                        .password("password")
                        .firstName("First")
                        .lastName("Last")
                        .build();
        mockUserRepository.save(user);
        PasswordResetToken passwordResetToken =
                PasswordResetToken.builder()
                        .id(1L)
                        .user(user)
                        .token(token)
                        .expiryDate(dateTime)
                        .build();
        mockPasswordTokenRepository.save(passwordResetToken);

        ForgotPasswordRequest request =
                ForgotPasswordRequest.builder().email(user.getEmail()).build();
        when(mockUserRepository.getByEmail(context.currentUser())).thenReturn(user);

        // when
        userService.forgotPassword(request.getEmail());
        PasswordResetToken expected =
                PasswordResetToken.builder()
                        .id(1L)
                        .user(user)
                        .token(token)
                        .expiryDate(dateTime)
                        .build();
        // then
        verify(mockPasswordTokenRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateUser_Success() {
        // Given
        long id = 1;
        String firstName = "Tom";
        String lastName = "jacob";
        String email = "tomjacob@email.com";
        String phone = "61 7010 1111";
        String mobile = "0412 555 666";

        User user =
                User.builder()
                        .id(1)
                        .email("test910@test.com")
                        .firstName("First")
                        .lastName("Last")
                        .phone("00 1111 2222")
                        .mobile("1122 333 444")
                        .deleted(true)
                        .build();

        UpdateUserRequest request =
                UpdateUserRequest.builder()
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .phone(phone)
                        .mobile(mobile)
                        .deleted(false)
                        .build();
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(user));
        // when
        userService.updateUser(id, request);

        User expected =
                User.builder()
                        .id(1)
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .phone(phone)
                        .mobile(mobile)
                        .deleted(false)
                        .build();
        // then
        verify(mockUserRepository).save(eq(expected));
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateUser_Failure() {
        // Given
        String firstName = "Tom";
        String lastName = "jacob";
        String email = "tomjacob@email.com";
        String phone = "61 7010 1111";
        String mobile = "0412 555 666";
        UpdateUserRequest request =
                UpdateUserRequest.builder()
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .phone(phone)
                        .mobile(mobile)
                        .deleted(false)
                        .build();
        assertThrows(
                UserServiceException.class,
                () -> {

                    // When
                    userService.updateUser(1L, request);
                });
    }

    @SneakyThrows
    @Test
    void testGetUser_Success() {
        // Given
        User user =
                User.builder()
                        .id(1)
                        .email("roy@test.com")
                        .firstName("three")
                        .lastName("four")
                        .phone("61 7010 1111")
                        .mobile("0412 555 666")
                        .deleted(false)
                        .build();
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(user));
        // When

        GetUserDTO expected = new ModelMapper().map(user, GetUserDTO.class);

        // When
        GetUserDTO actual = userService.getUserById(1L);

        // Then
        assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    void testGetUser_Failure() {
        // Given
        long id = 1;
        // When
        assertThrows(
                UserServiceException.class,
                () -> {
                    // When
                    userService.getUserById(id);
                });
    }
}
