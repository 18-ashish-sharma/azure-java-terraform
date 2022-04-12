package com.onedoorway.project.controller;

import static com.onedoorway.project.controller.UserControllerTest.asJsonString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.*;
import com.onedoorway.project.dto.GetHouseImageRequest;
import com.onedoorway.project.filters.JwtRequestFilter;
import com.onedoorway.project.model.HouseImage;
import com.onedoorway.project.model.ODWUserDetails;
import com.onedoorway.project.model.Role;
import com.onedoorway.project.model.User;
import com.onedoorway.project.repository.HouseImageRepository;
import com.onedoorway.project.repository.UserRepository;
import com.onedoorway.project.services.ODWUserDetailsService;
import com.onedoorway.project.util.JwtUtil;
import java.time.Instant;
import java.util.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
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
class HouseImageControllerTest {
    private MockMvc mockMvc;

    @InjectMocks private HouseImageController houseImageController;

    @Autowired private WebApplicationContext wac;

    @Mock private JwtUtil mockJwtUtil;

    @Mock private ODWUserDetailsService mockUserDetailsService;

    @Autowired private UserRepository userRepository;

    @Autowired private HouseImageRepository houseImageRepository;

    private HouseImage testHouseImage;

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
        houseImageRepository.deleteAll();
        userRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("GET/ get image url")
    void testGetImageUrl_Success() {
        // Given
        String houseCode = "100";
        GetHouseImageRequest request = GetHouseImageRequest.builder().houseCode("100").build();
        testHouseImage =
                HouseImage.builder()
                        .id(1L)
                        .houseCode(houseCode)
                        .imageBlobName("blob")
                        .lastUploadedBy("bob")
                        .lastUpdatedAt(Instant.now())
                        .build();
        testHouseImage = houseImageRepository.save(testHouseImage);

        CloudStorageAccount storageAccount = CloudStorageAccount.getDevelopmentStorageAccount();
        try (MockedStatic<CloudStorageAccount> mocked = mockStatic(CloudStorageAccount.class)) {
            mocked.when(() -> CloudStorageAccount.parse("test-connection-string"))
                    .thenReturn(storageAccount);
            CloudBlobClient cloudBlobClient = storageAccount.createCloudBlobClient();
            CloudBlobContainer container = cloudBlobClient.getContainerReference("test-container");

            CloudBlockBlob blob =
                    container.getBlockBlobReference(testHouseImage.getImageBlobName());
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
                            post("/get-image")
                                    .header("Authorization", "Bearer dummy")
                                    .accept(MediaType.APPLICATION_JSON)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(request)))
                    .andDo(MockMvcResultHandlers.print())
                    // Then
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.imageUrl").value(url));
        }
    }

    @SneakyThrows
    @Test
    @DisplayName("GET/ get image url failure")
    void testGetImageUrl_Failure() {
        // Given
        String houseCode = "100";
        GetHouseImageRequest request = GetHouseImageRequest.builder().houseCode("100").build();
        // When
        mockMvc.perform(
                        post("/get-image")
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
}
