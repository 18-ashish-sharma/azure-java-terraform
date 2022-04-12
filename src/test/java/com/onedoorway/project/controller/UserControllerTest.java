package com.onedoorway.project.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onedoorway.project.FrozenContext;
import com.onedoorway.project.dto.*;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.HouseRepository;
import com.onedoorway.project.repository.PasswordTokenRepository;
import com.onedoorway.project.repository.RoleRepository;
import com.onedoorway.project.repository.UserRepository;
import com.onedoorway.project.services.ODWUserDetailsService;
import com.onedoorway.project.util.JwtUtil;
import java.time.LocalDateTime;
import java.util.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {

    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private UserRepository userRepository;

    @Autowired private HouseRepository houseRepository;

    @Autowired private RoleRepository roleRepository;

    @Autowired private PasswordTokenRepository passwordTokenRepository;

    private User testUser;

    private FrozenContext context = new FrozenContext();

    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeAll
    public void setup() {
        testUser =
                User.builder()
                        .email("test910@test.com")
                        .password(passwordEncoder.encode("password"))
                        .firstName("First")
                        .lastName("Last")
                        .phone("61 1111 2222")
                        .mobile("5678 432 109")
                        .roles(
                                new HashSet<>(
                                        Collections.singletonList(
                                                Role.builder().name("ADMIN").build())))
                        .deleted(false)
                        .build();

        Role userRole = Role.builder().name("USER").build();
        roleRepository.save(userRole);

        testUser = userRepository.save(testUser);
        ODWUserDetails basicUser = new ODWUserDetails(testUser);
        when(mockJwtUtil.extractUsername(anyString())).thenReturn("test910@test.com");
        when(mockJwtUtil.validateToken(anyString(), any(UserDetails.class))).thenReturn(true);
        when(mockUserDetailsService.loadUserByUsername(anyString())).thenReturn(basicUser);
        this.mockMvc =
                webAppContextSetup(this.wac)
                        .addFilters(new JwtRequestFilter(mockUserDetailsService, mockJwtUtil))
                        .build();
    }

    @AfterAll
    public void teardown() {
        passwordTokenRepository.deleteAll();
        userRepository.deleteAll();
        houseRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("GET / get all users")
    void testListUsers() {
        // Given
        String houseCode = "800";
        House house = House.builder().houseCode(houseCode).build();
        house = houseRepository.save(house);
        testUser.setHouses(Set.of(house));
        userRepository.save(testUser);

        // When
        mockMvc.perform(
                        get("/user/list")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.[0].firstName").value(testUser.getFirstName()))
                .andExpect(jsonPath("$.[0].lastName").value(testUser.getLastName()))
                .andExpect(jsonPath("$.[0].id").value(testUser.getId()));
    }

    @SneakyThrows
    @Test
    @DisplayName("POST / list all users by page")
    void testListAllUsersByPage() {
        // Given
        String houseCode = "890";
        House house = House.builder().houseCode(houseCode).build();
        house = houseRepository.save(house);
        testUser.setHouses(Set.of(house));
        userRepository.save(testUser);

        ListUsersByPageRequest request =
                ListUsersByPageRequest.builder()
                        .pageNumber(0)
                        .pageSize(1)
                        .nameOrEmail(testUser.getFirstName())
                        .houseCode(house.getHouseCode())
                        .build();

        // When
        mockMvc.perform(
                        post("/user/list-page")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.users[0].email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.users[0].firstName").value(testUser.getFirstName()))
                .andExpect(jsonPath("$.users[0].lastName").value(testUser.getLastName()))
                .andExpect(jsonPath("$.users[0].houses.[0]").value(house.getHouseCode()))
                .andExpect(jsonPath("$.users[0].phone").value(testUser.getPhone()))
                .andExpect(jsonPath("$.users[0].mobile").value(testUser.getMobile()))
                .andExpect(jsonPath("$.users[0].deleted").value(testUser.getDeleted()))
                .andExpect(jsonPath("$.users[0].id").value(testUser.getId()))
                .andExpect(jsonPath("$.totalUsers", is(1)));
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /list of users based on HouseCode")
    void testUsersByHouseCodeSuccess() {
        // Given
        String houseCode = "801";
        House house = House.builder().houseCode(houseCode).build();
        house = houseRepository.save(house);
        testUser.setHouses(Set.of(house));
        userRepository.save(testUser);
        ListUserRequest request = ListUserRequest.builder().houseCode(house.getHouseCode()).build();
        // When
        mockMvc.perform(
                        post("/user/list-by-house")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.[0].firstName").value(testUser.getFirstName()))
                .andExpect(jsonPath("$.[0].lastName").value(testUser.getLastName()))
                .andExpect(jsonPath("$.[0].id").value(testUser.getId()));
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /add role to user")
    void testAddRoleToUser() {
        // Given
        Role role = Role.builder().name("USER").build();
        role = roleRepository.save(role);
        AddRoleToUserRequest addRoleToUserRequest =
                AddRoleToUserRequest.builder()
                        .userId(testUser.getId())
                        .roleId(role.getId())
                        .build();
        // When
        mockMvc.perform(
                        post("/user/add-role")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(addRoleToUserRequest)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("DELETE /remove role from user")
    void testRemoveRole() {
        // Given
        Role testRole = testUser.getRoles().stream().findFirst().get();
        RemoveRoleRequest removeRoleRequest =
                RemoveRoleRequest.builder()
                        .roleId(testRole.getId())
                        .userId(testUser.getId())
                        .build();

        // When
        mockMvc.perform(
                        delete("/user/remove-role")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(removeRoleRequest)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));
    }

    @SneakyThrows
    @Test
    @DisplayName("DELETE /remove role from user failure")
    void testRemoveRole_NonExistentUser() {
        // Given
        Role role = Role.builder().name("TEST_USER").build();
        roleRepository.save(role);

        RemoveRoleRequest removeRoleRequest =
                RemoveRoleRequest.builder().roleId(role.getId()).userId(75).build();
        // When
        mockMvc.perform(
                        delete("/user/remove-role")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(removeRoleRequest)))

                // Then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)));
    }

    @SneakyThrows
    @Test
    @DisplayName("DELETE /remove house from user")
    void testRemoveHouse() {
        // Given
        String houseCode = "109";
        House house = House.builder().houseCode(houseCode).build();
        testUser.setHouses(Set.of(house));
        houseRepository.save(house);
        testUser = userRepository.save(testUser);

        UserToHouseRequest userToHouseRequest =
                UserToHouseRequest.builder().userId(testUser.getId()).houseCode(houseCode).build();
        // When
        mockMvc.perform(
                        delete("/user/remove-house")
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
    @DisplayName("DELETE /remove House from user failure")
    void testRemoveHouse_NonExistentUser() {
        // Given
        String houseCode = "600";
        House house =
                House.builder()
                        .houseCode(houseCode)
                        .phone("0412345678")
                        .addrLine1("abc Line")
                        .addrLine2("martin road")
                        .city("Sydney")
                        .state("Australia")
                        .postCode("2000")
                        .build();
        houseRepository.save(house);

        UserToHouseRequest userToHouseRequest =
                UserToHouseRequest.builder().userId(1000).houseCode(houseCode).build();

        // When
        mockMvc.perform(
                        delete("/user/remove-house")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(userToHouseRequest)))
                // Then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)));
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /change password")
    void testChangePassword() {
        // Given
        ChangePasswordRequest request =
                ChangePasswordRequest.builder()
                        .newPassword("welcome")
                        .oldPassword("password")
                        .build();

        // When
        mockMvc.perform(
                        post("/change-password")
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
    @DisplayName("POST / change Password failure")
    void testChangePassword_Failure() {

        ChangePasswordRequest request =
                ChangePasswordRequest.builder()
                        .newPassword("password")
                        .oldPassword("password")
                        .build();

        // When
        mockMvc.perform(
                        post("/change-password")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                // Then
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /forgot password")
    void testForgotPassword() {
        // Given
        User user = User.builder().id(1).email("test25@test.com").build();
        user = userRepository.save(user);
        ForgotPasswordRequest request =
                ForgotPasswordRequest.builder().email(user.getEmail()).build();

        // When
        mockMvc.perform(
                        post("/user/forgot-password")
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
    @DisplayName("POST / forgot Password failure")
    void testForgotPassword_Failure() {
        // Given
        ForgotPasswordRequest request =
                ForgotPasswordRequest.builder().email("wrongEmail@test.com").build();

        // When
        mockMvc.perform(
                        post("/user/forgot-password")
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))

                // Then
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    @DisplayName("POST /reset password")
    void testResetPassword() {
        // Given
        String token = "token";
        LocalDateTime dateTime = LocalDateTime.now().plusDays(1L);

        User user =
                User.builder()
                        .id(9)
                        .email("test12@test.com")
                        .firstName("test1")
                        .lastName("test2")
                        .password(passwordEncoder.encode("credential"))
                        .build();
        user = userRepository.save(user);
        PasswordResetToken passwordResetToken =
                PasswordResetToken.builder()
                        .id(1L)
                        .user(user)
                        .token(token)
                        .expiryDate(dateTime)
                        .build();
        passwordResetToken = passwordTokenRepository.save(passwordResetToken);

        ResetPasswordRequest request =
                ResetPasswordRequest.builder()
                        .newPassword("credential")
                        .confirmPassword("credential")
                        .token(passwordResetToken.getToken())
                        .build();

        // When
        mockMvc.perform(
                        post("/user/reset-password")
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
    @DisplayName("POST /reset password failure")
    void testResetPassword_Failure() {
        // Given
        String token = "token";
        ResetPasswordRequest request =
                ResetPasswordRequest.builder()
                        .newPassword("credential")
                        .confirmPassword("simple")
                        .token(token)
                        .build();

        // When
        mockMvc.perform(
                        post("/user/reset-password")
                                .header("Authorization", "Bearer dummy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(request)))
                // Then

                // Then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)));
    }

    @SneakyThrows
    @Test
    @DisplayName("PUT /update user")
    void testUpdateUser_Success() {
        // Given
        String firstName = "Tom";
        String lastName = "jacob";
        String email = "tomjacob@email.com";
        String phone = "61 7010 1111";
        String mobile = "0412 555 666";

        User user =
                User.builder()
                        .email("tom@test.com")
                        .firstName("One")
                        .lastName("Two")
                        .phone("00 1111 2222")
                        .mobile("1122 333 444")
                        .deleted(true)
                        .build();
        userRepository.save(user);
        UpdateUserRequest request =
                UpdateUserRequest.builder()
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .phone(phone)
                        .mobile(mobile)
                        .deleted(false)
                        .build();
        // When
        mockMvc.perform(
                        put("/user/update/{id}", user.getId())
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
    @DisplayName("PUT /update user failure")
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
        // When
        mockMvc.perform(
                        put("/user/update/{id}", 300)
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
    @DisplayName("GET / get user")
    void testGetUser_Success() {
        // Given
        User user =
                User.builder()
                        .email("roy@test.com")
                        .firstName("three")
                        .lastName("four")
                        .phone("61 7010 1111")
                        .mobile("0412 555 666")
                        .deleted(false)
                        .build();
        userRepository.save(user);

        // When
        mockMvc.perform(
                        get("/user/get/{userId}", user.getId())
                                .header("Authorization", "Bearer dummy")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.phone").value(user.getPhone()))
                .andExpect(jsonPath("$.mobile").value(user.getMobile()))
                .andExpect(jsonPath("$.deleted").value(false));
    }

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
