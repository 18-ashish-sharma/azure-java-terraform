package com.onedoorway.project.services;

import com.onedoorway.project.Context;
import com.onedoorway.project.dto.BowelNoteDTO;
import com.onedoorway.project.dto.BowelNoteRequest;
import com.onedoorway.project.dto.ListBowelNoteRequest;
import com.onedoorway.project.dto.UpdateBowelNoteRequest;
import com.onedoorway.project.exception.BowelNoteServiceException;
import com.onedoorway.project.model.BowelNote;
import com.onedoorway.project.model.Client;
import com.onedoorway.project.model.Size;
import com.onedoorway.project.repository.BowelNoteRepository;
import com.onedoorway.project.repository.ClientRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class BowelNoteService {
    private final BowelNoteRepository bowelNoteRepository;
    private final ClientRepository clientRepository;
    private final Context context;

    @Autowired
    public BowelNoteService(
            BowelNoteRepository bowelNoteRepository,
            ClientRepository clientRepository,
            Context context) {
        this.bowelNoteRepository = bowelNoteRepository;
        this.clientRepository = clientRepository;
        this.context = context;
    }

    public void createBowelNote(BowelNoteRequest request) throws BowelNoteServiceException {
        log.info("Creating bowel note for client id {}", request.getClientId());

        Optional<Client> client = clientRepository.findById(request.getClientId());
        if (client.isEmpty()) {
            log.error("Client not found for requested clientId {}", request.getClientId());
            throw new BowelNoteServiceException("Client not found with client id");
        }

        Size size = Size.valueOf(request.getSize());
        log.info("The size is {}", size);
        if (!(LocalDateTime.parse(
                        request.getRecordTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .toLocalDate()
                .isBefore(LocalDate.parse(request.getStartDate())))) {
            BowelNote bowelNote =
                    BowelNote.builder()
                            .client(client.get())
                            .startDate(
                                    LocalDate.parse(
                                            request.getStartDate(),
                                            DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                            .recordTime(
                                    LocalDateTime.parse(
                                            request.getRecordTime(),
                                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                            .size(size)
                            .type1(request.getType1())
                            .type2(request.getType2())
                            .type3(request.getType3())
                            .type4(request.getType4())
                            .type5(request.getType5())
                            .type6(request.getType6())
                            .type7(request.getType7())
                            .lastUploadedBy(request.getLastUploadedBy())
                            .lastUpdatedAt(Instant.now())
                            .deleted(false)
                            .build();

            bowelNoteRepository.save(bowelNote);

            log.info("Created bowel note  with id {}", bowelNote.getId());
        } else {
            log.info("RecordDate Should Be Greater Than StartDate");
            throw new BowelNoteServiceException("RecordTime Should Be Greater Than StartDate");
        }
    }

    public List<BowelNoteDTO> listBowelNotes(ListBowelNoteRequest request)
            throws BowelNoteServiceException {
        List<BowelNote> res;
        Pageable page =
                PageRequest.of(
                        request.getPageNumber(),
                        request.getPageSize(),
                        Sort.by("startDate").descending());
        ModelMapper modelMapper = new ModelMapper();
        log.info("Fetching bowel notes based on request {}", request);
        Optional<Client> client = clientRepository.findById(request.getClientId());
        if (client.isEmpty()) {
            log.info("client not found for clientId {}", request.getClientId());
            throw new BowelNoteServiceException(
                    "Client not found for clientId {}" + request.getClientId());
        }
        res =
                bowelNoteRepository.findAllByClient_IdAndStartDateBetween(
                        request.getClientId(),
                        LocalDate.parse(request.getStart()),
                        LocalDate.parse(request.getEnd()),
                        page);
        log.info("Fetched the bowel notes based on the request");
        return res.stream()
                .map(item -> modelMapper.map(item, BowelNoteDTO.class))
                .collect(Collectors.toList());
    }

    public void updateBowelNote(Long id, UpdateBowelNoteRequest request)
            throws BowelNoteServiceException {
        log.info("Updating the bowel note with id {}", id);
        Optional<BowelNote> bowelNote = bowelNoteRepository.findById(id);
        if (bowelNote.isEmpty()) {
            String errorMessage = String.format("Cannot get a bowel note for the id %d", id);
            log.error(errorMessage);
            throw new BowelNoteServiceException(errorMessage);
        }
        if (!(request.getCurrentLastUpdatedAt())
                .isBefore((bowelNote.get().getLastUpdatedAt().truncatedTo(ChronoUnit.MILLIS)))) {

            BowelNote existingEntity = bowelNote.get();
            ModelMapper mapper = new ModelMapper();
            mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
            mapper.addMappings(
                    new PropertyMap<UpdateBowelNoteRequest, BowelNote>() {
                        @Override
                        protected void configure() {
                            skip(destination.getClient());
                        }
                    });
            mapper.map(request, existingEntity);
            existingEntity.setRecordTime(
                    LocalDateTime.parse(
                            request.getRecordTime(),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            existingEntity.setSize(Size.valueOf(request.getSize()));
            existingEntity.setLastUpdatedAt(context.now());
            bowelNoteRepository.save(existingEntity);
            log.info("Updated the bowel note for the given id {}", id);
        } else {
            String errorMessage =
                    String.format(
                            "Cannot update bowel note note with lastUpdated %s",
                            request.getCurrentLastUpdatedAt());
            log.error(errorMessage);
            throw new BowelNoteServiceException(errorMessage);
        }
    }
}
