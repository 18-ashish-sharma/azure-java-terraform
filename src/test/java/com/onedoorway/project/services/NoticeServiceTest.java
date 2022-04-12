package com.onedoorway.project.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Every.everyItem;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onedoorway.project.FrozenContext;
import com.onedoorway.project.dto.*;
import com.onedoorway.project.exception.NoticeServiceException;
import com.onedoorway.project.model.House;
import com.onedoorway.project.model.Notice;
import com.onedoorway.project.model.NoticeStatus;
import com.onedoorway.project.model.User;
import com.onedoorway.project.repository.HouseRepository;
import com.onedoorway.project.repository.NoticeRepository;
import com.onedoorway.project.repository.UserRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import lombok.SneakyThrows;
import org.hamcrest.beans.HasPropertyWithValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @Mock NoticeRepository mockNoticeRepository;

    @Mock UserRepository mockUserRepository;

    @Mock HouseRepository mockHouseRepository;

    @Mock EntityManager mockEntityManager;

    @Mock TypedQuery<Notice> query;

    private NoticeService noticeService;

    private final FrozenContext context = new FrozenContext();

    @BeforeEach
    void init() {
        noticeService =
                new NoticeService(
                        mockNoticeRepository, mockUserRepository, mockHouseRepository, context);
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testCreateNotices_Success() {
        // Given
        String notice = "Test notice";
        String houseCode = "101";
        House house = House.builder().houseCode(houseCode).build();
        Instant createdAt = context.now();
        NoticeRequest request =
                NoticeRequest.builder()
                        .notice(notice)
                        .houseCode(List.of(houseCode))
                        .startDate(createdAt.minus(5, ChronoUnit.MINUTES))
                        .endDate(createdAt.plus(5, ChronoUnit.MINUTES))
                        .build();

        User user = User.builder().email(context.currentUser()).id(1).build();

        when(mockUserRepository.getByEmail(context.currentUser())).thenReturn(user);
        noticeService.createNotice(request);

        // Then
        Notice expected =
                Notice.builder()
                        .notice(notice)
                        .createdBy(user)
                        .noticeStatus(NoticeStatus.ACTIVE)
                        .createdAt(context.now())
                        .houses(Set.of(house))
                        .startDate(createdAt.minus(5, ChronoUnit.MINUTES))
                        .endDate(createdAt.plus(5, ChronoUnit.MINUTES))
                        .build();

        // capture what was about to be persisted and make sure that is as expected
        ArgumentCaptor<Notice> noticeArgumentCaptor = ArgumentCaptor.forClass(Notice.class);
        verify(mockNoticeRepository).save(noticeArgumentCaptor.capture());
        Notice actual = noticeArgumentCaptor.getValue();

        assertThat(
                expected,
                (allOf(
                        HasPropertyWithValue.hasProperty("notice", equalTo(notice)),
                        HasPropertyWithValue.hasProperty("createdAt", equalTo(context.now())),
                        HasPropertyWithValue.hasProperty("createdBy", equalTo(user)))));
    }

    @Test
    @WithMockUser
    void testCreateNotices_Failure_NoUserFound() {
        // Given
        Instant createdAt = context.now();
        String houseCode = "101";
        String notice = "test";
        NoticeRequest request =
                NoticeRequest.builder()
                        .notice(notice)
                        .houseCode(List.of(houseCode))
                        .startDate(createdAt.minus(5, ChronoUnit.MINUTES))
                        .endDate(createdAt.plus(5, ChronoUnit.MINUTES))
                        .build();

        assertThrows(NoticeServiceException.class, () -> noticeService.createNotice(request));
    }

    @Test
    void testCreateNotices_Failure_UnAuthenticated() {
        // Given
        String houseCode = "101";
        Instant createdAt = context.now();
        String notice = "test";
        NoticeRequest request =
                NoticeRequest.builder()
                        .notice(notice)
                        .houseCode(List.of(houseCode))
                        .startDate(createdAt.minus(5, ChronoUnit.MINUTES))
                        .endDate(createdAt.plus(5, ChronoUnit.MINUTES))
                        .build();

        assertThrows(NoticeServiceException.class, () -> noticeService.createNotice(request));
    }

    @SneakyThrows
    @Test
    void testGetNotice_Success() {
        // Given
        House house = House.builder().houseCode("101").build();

        User user = User.builder().email("gettest@test.com").password("test").build();
        Instant createdAt = context.now();

        Notice notice =
                Notice.builder()
                        .id(1L)
                        .noticeStatus(NoticeStatus.ACTIVE)
                        .houses(Set.of(house))
                        .createdBy(user)
                        .notice("checkup")
                        .startDate(createdAt.minus(5, ChronoUnit.MINUTES))
                        .endDate(createdAt.plus(5, ChronoUnit.MINUTES))
                        .createdAt(createdAt)
                        .build();

        when(mockNoticeRepository.findById(1L)).thenReturn(Optional.of(notice));
        NoticeDTO expected = new ModelMapper().map(notice, NoticeDTO.class);

        // When
        NoticeDTO actual = noticeService.getNoticeById(1L);

        // Then
        assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    void testGetNotice_Failure() {
        // Given
        long id = 1;
        // When
        assertThrows(
                NoticeServiceException.class,
                () -> {
                    // When
                    noticeService.getNoticeById(id);
                });
    }

    @SneakyThrows
    @Test
    void testListNotice_Success() {
        // Given
        House house = House.builder().houseCode("101").build();

        User user = User.builder().email("gettest@test.com").password("test").build();
        Instant createdAt = context.now();

        Notice notice =
                Notice.builder()
                        .id(1L)
                        .noticeStatus(NoticeStatus.ACTIVE)
                        .houses(Set.of(house))
                        .createdBy(user)
                        .notice("checkup")
                        .startDate(createdAt.minus(5, ChronoUnit.MINUTES))
                        .endDate(createdAt.plus(5, ChronoUnit.MINUTES))
                        .createdAt(createdAt)
                        .build();

        when(mockNoticeRepository.findNotices(any(), any(), any(), any()))
                .thenReturn(List.of(notice));
        when(mockHouseRepository.getByHouseCode(any())).thenReturn(house);
        ListNoticeRequest request =
                ListNoticeRequest.builder()
                        .status("ACTIVE")
                        .houseCode("101")
                        .pageNumber(0)
                        .pageSize(1)
                        .build();

        // When
        List<NoticeDTO> actual = noticeService.getNotices(request);

        // Then
        assertEquals(1, actual.size());
        assertThat(
                actual,
                (allOf(
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "noticeStatus", anyOf(equalTo("ACTIVE")))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "houses",
                                        anyOf(
                                                equalTo(
                                                        List.of(
                                                                HouseDTO.builder()
                                                                        .houseCode("101")
                                                                        .build()))))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "createdBy",
                                        anyOf(
                                                equalTo(
                                                        UserDTO.builder()
                                                                .email("gettest@test.com")
                                                                .build())))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "notice", anyOf(equalTo("checkup")))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "startDate",
                                        anyOf(equalTo(createdAt.minus(5, ChronoUnit.MINUTES))))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "endDate",
                                        anyOf(equalTo(createdAt.plus(5, ChronoUnit.MINUTES))))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "createdAt", anyOf(equalTo(context.now())))))));

        // also more tests required for lists covering the @Query written
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void testQuery() {
        when(mockEntityManager.createQuery(
                        "select n from notices n where n.noticeStatus = :status and :house member n.houses and ((n.startDate < :now and n.endDate >= :now) or (n.startDate is null and n.endDate >= :now)))"))
                .thenReturn(query);
        when(query.setParameter("id", 1)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(new Notice());
        List<Notice> notices = mockNoticeRepository.findNotices(any(), any(), any(), any());
        assertThat(
                notices,
                (allOf(
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "noticeStatus", anyOf(equalTo("ACTIVE")))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "houses",
                                        anyOf(
                                                equalTo(
                                                        List.of(
                                                                HouseDTO.builder()
                                                                        .houseCode("101")
                                                                        .build()))))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "createdBy",
                                        anyOf(
                                                equalTo(
                                                        UserDTO.builder()
                                                                .email("gettest@test.com")
                                                                .build())))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "notice", anyOf(equalTo("checkup")))),
                        everyItem(
                                HasPropertyWithValue.hasProperty(
                                        "createdAt", anyOf(equalTo(context.now())))))));
    }

    @SneakyThrows
    @Test
    void testListAllNotices_Success_House_NotExist() {
        // Given
        ListNoticeRequest request =
                ListNoticeRequest.builder()
                        .status("status")
                        .houseCode("200")
                        .pageNumber(0)
                        .pageSize(1)
                        .build();
        // When
        assertThrows(
                NoticeServiceException.class,
                () -> {
                    // When
                    noticeService.getNotices(request);
                });
    }

    @SneakyThrows
    @Test
    @WithMockUser
    void testUpdateNotice_Success() {
        // Given
        String notice = "Test notice";
        String houseCode = "101";
        House house = House.builder().houseCode(houseCode).build();
        Instant createdAt = context.now();
        User user = User.builder().email(context.currentUser()).id(1).build();
        Notice expected =
                Notice.builder()
                        .notice(notice)
                        .createdBy(user)
                        .noticeStatus(NoticeStatus.ACTIVE)
                        .createdAt(context.now())
                        .houses(Set.of(house))
                        .startDate(createdAt.minus(5, ChronoUnit.MINUTES))
                        .endDate(createdAt.plus(5, ChronoUnit.MINUTES))
                        .build();
        House newHouse = House.builder().houseCode("102").build();
        when(mockHouseRepository.getByHouseCode("102")).thenReturn(newHouse);
        when(mockNoticeRepository.findById(1L)).thenReturn(Optional.of(expected));

        NoticeRequest request =
                NoticeRequest.builder().notice("Updated notice").houseCode(List.of("102")).build();

        // When
        noticeService.updateNotice(1L, request);

        // Then
        // capture what was about to be persisted and make sure that is as expected
        ArgumentCaptor<Notice> noticeArgumentCaptor = ArgumentCaptor.forClass(Notice.class);
        verify(mockNoticeRepository).save(noticeArgumentCaptor.capture());
        Notice actual = noticeArgumentCaptor.getValue();

        assertEquals(actual.getNotice(), "Updated notice");
        assertEquals(actual.getHouses().size(), 1);
        assertTrue(actual.getHouses().contains(newHouse));
    }

    @Test
    void testUpdateNotice_Failure_NoticeNotFound() {
        // Given
        House house = House.builder().houseCode("101").build();
        Instant createdAt = Instant.now();
        NoticeRequest request =
                NoticeRequest.builder()
                        .notice("notice")
                        .houseCode(List.of(house.getHouseCode()))
                        .noticeStatus(NoticeStatus.INACTIVE.name())
                        .startDate(createdAt.minus(5, ChronoUnit.MINUTES))
                        .endDate(createdAt.minus(5, ChronoUnit.MINUTES))
                        .build();
        assertThrows(
                NoticeServiceException.class,
                () -> {
                    // When
                    noticeService.updateNotice(1L, request);
                });
    }

    @Test
    void testUpdateNotice_Failure_HouseNotFound() {
        Instant createdAt = Instant.now();
        NoticeRequest request =
                NoticeRequest.builder()
                        .notice("notice")
                        .houseCode(List.of("NON-EXIST"))
                        .noticeStatus(NoticeStatus.INACTIVE.name())
                        .startDate(createdAt.minus(5, ChronoUnit.MINUTES))
                        .endDate(createdAt.minus(5, ChronoUnit.MINUTES))
                        .build();
        assertThrows(
                NoticeServiceException.class,
                () -> {
                    // When
                    noticeService.updateNotice(1L, request);
                });
    }
}
