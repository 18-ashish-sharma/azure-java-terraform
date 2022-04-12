package com.onedoorway.project.services;

import com.onedoorway.project.Context;
import com.onedoorway.project.dto.ListNoticeRequest;
import com.onedoorway.project.dto.NoticeDTO;
import com.onedoorway.project.dto.NoticeRequest;
import com.onedoorway.project.dto.UserDTO;
import com.onedoorway.project.exception.NoticeServiceException;
import com.onedoorway.project.model.House;
import com.onedoorway.project.model.Notice;
import com.onedoorway.project.model.NoticeStatus;
import com.onedoorway.project.model.User;
import com.onedoorway.project.repository.HouseRepository;
import com.onedoorway.project.repository.NoticeRepository;
import com.onedoorway.project.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@Transactional
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final HouseRepository houseRepository;
    private final Context context;

    public NoticeService(
            NoticeRepository noticeRepository,
            UserRepository userRepository,
            HouseRepository houseRepository,
            Context context) {
        this.noticeRepository = noticeRepository;
        this.userRepository = userRepository;
        this.houseRepository = houseRepository;
        this.context = context;
    }

    public void createNotice(NoticeRequest request) throws NoticeServiceException {
        String email = context.currentUser();
        User user = userRepository.getByEmail(email);
        if (user == null) {
            handleError("Unauthenticated session or no user found for email %s", email);
        }

        Set<House> houseSet =
                request.getHouseCode().stream()
                        .map(houseRepository::getByHouseCode)
                        .collect(Collectors.toSet());

        Notice notice =
                Notice.builder()
                        .notice(request.getNotice())
                        .createdBy(user)
                        .houses(houseSet)
                        .createdAt(context.now())
                        .startDate(request.getStartDate())
                        .endDate(request.getEndDate())
                        .noticeStatus(NoticeStatus.ACTIVE)
                        .build();
        noticeRepository.save(notice);

        log.info("Created the notice with id {}", notice.getId());
    }

    private void handleError(String message, String... params) throws NoticeServiceException {
        String errorMessage = String.format(message, params);
        log.error(errorMessage);
        throw new NoticeServiceException(errorMessage);
    }

    public NoticeDTO getNoticeById(long id) throws NoticeServiceException {
        Optional<Notice> res = noticeRepository.findById(id);
        if (res.isEmpty()) {
            throw new NoticeServiceException("No notice found with id " + id);
        }
        log.info("Fetched the notice based on id {}", id);
        return new ModelMapper().map(res.get(), NoticeDTO.class);
    }

    public List<NoticeDTO> getNotices(ListNoticeRequest request) throws NoticeServiceException {
        List<Notice> res;
        Pageable page =
                PageRequest.of(
                        request.getPageNumber(), request.getPageSize(), Sort.by("id").descending());
        ModelMapper modelMapper = new ModelMapper();
        TypeMap<Notice, NoticeDTO> typeMap =
                modelMapper.createTypeMap(Notice.class, NoticeDTO.class);
        typeMap.addMappings(
                mapper ->
                        mapper.using(new HouseListConverter())
                                .map(Notice::getHouses, NoticeDTO::setHouses));
        modelMapper.typeMap(User.class, UserDTO.class);
        log.info("Fetching notices based on request");
        if (request.getHouseCode() != null && request.getStatus() != null) {
            log.info("Fetching the notices based on houseCode and NoticeStatus");
            House house = houseRepository.getByHouseCode(request.getHouseCode());
            if (house == null) {
                throw new NoticeServiceException(
                        "No house found with code " + request.getHouseCode());
            }
            res =
                    noticeRepository.findNotices(
                            page, NoticeStatus.valueOf(request.getStatus()), house, context.now());
        } else {
            log.info("Fetching all the notices");
            res = noticeRepository.findAll(page).getContent();
        }
        log.info("Fetched the notices based on the request");
        return res.stream()
                .map(item -> modelMapper.map(item, NoticeDTO.class))
                .collect(Collectors.toList());
    }

    public void updateNotice(Long id, NoticeRequest request) throws NoticeServiceException {
        Optional<Notice> notice = noticeRepository.findById(id);
        if (notice.isPresent()) {
            Notice existingNotice = notice.get();
            existingNotice.setNotice(request.getNotice());
            existingNotice.setStartDate(request.getStartDate());
            existingNotice.setEndDate(request.getEndDate());
            if (StringUtils.hasText(request.getNoticeStatus())) {
                existingNotice.setNoticeStatus(NoticeStatus.valueOf(request.getNoticeStatus()));
            }
            Set<House> houseSet =
                    request.getHouseCode().stream()
                            .map(houseRepository::getByHouseCode)
                            .collect(Collectors.toSet());
            existingNotice.setHouses(houseSet);
            noticeRepository.save(existingNotice);
            log.info("Updated notice for id {}", id);
        } else {
            throw new NoticeServiceException("Notice not found for id " + id);
        }
    }
}
