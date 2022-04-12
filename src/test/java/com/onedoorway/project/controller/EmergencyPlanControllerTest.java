package com.onedoorway.project.controller;

import static com.onedoorway.project.controller.UserControllerTest.asJsonString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.*;
import com.onedoorway.project.dto.GetEmergencyPlanRequest;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.EmergencyPlan;
import com.onedoorway.project.model.ODWUserDetails;
import com.onedoorway.project.model.Role;
import com.onedoorway.project.model.User;
import com.onedoorway.project.repository.EmergencyPlanRepository;
import com.onedoorway.project.repository.UserRepository;
import com.onedoorway.project.services.ODWUserDetailsService;
import com.onedoorway.project.util.JwtUtil;
import java.util.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
class EmergencyPlanControllerTest {
    private MockMvc mockMvc;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private UserRepository userRepository;

    @Autowired private EmergencyPlanRepository emergencyPlanRepository;

    private EmergencyPlan testEmergencyPlan;

    @BeforeEach
    public void setUp() {
        User user =
                User.builder()
                        .id(1)
                        .email("test@test.com")
                        .password("password")
                        .roles(Set.of((Role.builder().id(1).name("ADMIN").build())))
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
        emergencyPlanRepository.deleteAll();
        userRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("GET/ get plan url")
    void testGetPlanUrl_Success() {
        // Given
        String houseCode = "100";
        String emergencyPlanBlobName = "test.pdf";
        String emergencyHandoutBlobName = "test.pdf";

        testEmergencyPlan =
                EmergencyPlan.builder()
                        .houseCode(houseCode)
                        .urlEmergencyPlan(emergencyPlanBlobName)
                        .urlEmergencyHandout(emergencyHandoutBlobName)
                        .build();
        testEmergencyPlan = emergencyPlanRepository.save(testEmergencyPlan);
        GetEmergencyPlanRequest request =
                GetEmergencyPlanRequest.builder().houseCode("100").build();

        CloudBlockBlob blob;
        CloudStorageAccount storageAccount = CloudStorageAccount.getDevelopmentStorageAccount();
        try (MockedStatic<CloudStorageAccount> mocked = mockStatic(CloudStorageAccount.class)) {
            mocked.when(() -> CloudStorageAccount.parse("test-connection-string"))
                    .thenReturn(storageAccount);
            CloudBlobClient cloudBlobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = cloudBlobClient.getContainerReference("test-container");

            if (testEmergencyPlan.getUrlEmergencyPlan() != null) {
                blob = container.getBlockBlobReference(testEmergencyPlan.getUrlEmergencyPlan());
            } else {
                blob = container.getBlockBlobReference(testEmergencyPlan.getUrlEmergencyHandout());
            }
            SharedAccessBlobPolicy accessBlobPolicy = new SharedAccessBlobPolicy();
            GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

            calendar.add(Calendar.MINUTE, 10);
            accessBlobPolicy.setSharedAccessExpiryTime(calendar.getTime());
            accessBlobPolicy.setPermissions(
                    EnumSet.of(
                            SharedAccessBlobPermissions.READ,
                            SharedAccessBlobPermissions.WRITE,
                            SharedAccessBlobPermissions.LIST));
            String sas = blob.generateSharedAccessSignature(accessBlobPolicy, null);

            String url = blob.getUri() + "?" + sas;

            // When
            mockMvc.perform(
                            post("/url/get")
                                    .header("Authorization", "Bearer dummy")
                                    .accept(MediaType.APPLICATION_JSON)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(request)))
                    .andDo(MockMvcResultHandlers.print())

                    // Then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.houseCode").value(testEmergencyPlan.getHouseCode()))
                    .andExpect(jsonPath("$.emergencyPlanUrl").value(url))
                    .andExpect(jsonPath("$.emergencyHandoutUrl").value(url));
        }
    }
}
