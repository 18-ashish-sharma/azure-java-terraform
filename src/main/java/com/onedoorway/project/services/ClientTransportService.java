package com.onedoorway.project.services;

import com.onedoorway.project.dto.*;
import com.onedoorway.project.exception.ClientTransportServiceException;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.ClientTransportRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class ClientTransportService {
    private final ClientTransportRepository clientTransportRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public ClientTransportService(
            ClientTransportRepository clientTransportRepository,
            ClientRepository clientRepository) {
        this.clientTransportRepository = clientTransportRepository;
        this.clientRepository = clientRepository;
    }

    public void createClientTransport(ClientTransportRequest request)
            throws ClientTransportServiceException {
        log.info("Creating client transport for client id {}", request.getClientId());

        Optional<Client> client = clientRepository.findById(request.getClientId());
        if (client.isEmpty()) {
            log.error("Client not found for requested clientId {}", request.getClientId());
            throw new ClientTransportServiceException("Client not found with client id");
        }
        if (clientTransportRepository.findByClient_IdAndOdCarAndCarRegistrationAndDeleted(
                        request.getClientId(),
                        YesNo.valueOf(request.getOdCar()),
                        request.getCarRegistration(),
                        false)
                != null) {
            log.error(
                    "Client transport already exists for client id {}, odCar {} ,CarRegistration {} and Deleted {}",
                    request.getClientId(),
                    request.getOdCar(),
                    request.getCarRegistration(),
                    false);
            throw new ClientTransportServiceException(
                    "Client Transport already exists for client id for the given car, registration and deleted");
        }
        ClientTransport clientTransport =
                ClientTransport.builder()
                        .client(client.get())
                        .odCar(YesNo.valueOf(request.getOdCar()))
                        .carRegistration(request.getCarRegistration())
                        .carRegExpiry(request.getCarRegExpiry())
                        .lastUploadedBy(request.getLastUploadedBy())
                        .deleted(false)
                        .carModel(request.getCarModel())
                        .carMakeYear(request.getCarMakeYear())
                        .isTravelProtocol(YesNo.valueOf(request.getIsTravelProtocol()))
                        .travelProtocol(request.getTravelProtocol())
                        .authorisedPerson(request.getAuthorisedPerson())
                        .comprehensiveInsurance(YesNo.valueOf(request.getComprehensiveInsurance()))
                        .insuranceAgency(request.getInsuranceAgency())
                        .insurancePolicyNumber(request.getInsurancePolicyNumber())
                        .authorisedPersonContactNumber(request.getAuthorisedPersonContactNumber())
                        .roadSideAssistanceCovered(
                                YesNo.valueOf(request.getRoadSideAssistanceCovered()))
                        .insuranceContactNumber(request.getInsuranceContactNumber())
                        .cappedKMs(request.getCappedKMs())
                        .build();
        clientTransportRepository.save(clientTransport);
        log.info("Created the client transport {} ", clientTransport.getId());
    }

    @PreAuthorize("@context.isAdmin()")
    public List<ClientTransportDTO> getClientTransport(long clientId)
            throws ClientTransportServiceException {
        ModelMapper modelMapper = new ModelMapper();
        Optional<Client> client = clientRepository.findById(clientId);
        if (client.isEmpty()) {
            log.info("client not found {}", clientId);
            throw new ClientTransportServiceException("Client not found " + clientId);
        }
        List<ClientTransport> clientTransport = clientTransportRepository.findByClient_Id(clientId);
        if (clientTransport == null) {
            String errorMessage =
                    String.format("Cannot get a client transport for the client %d", clientId);
            log.error(errorMessage);
            throw new ClientTransportServiceException(errorMessage);
        }
        return clientTransport.stream()
                .map(item -> modelMapper.map(item, ClientTransportDTO.class))
                .collect(Collectors.toList());
    }

    public void updateClientTransport(Long id, UpdateClientTransportRequest request)
            throws ClientTransportServiceException {
        Optional<ClientTransport> clientTransport = clientTransportRepository.findById(id);
        if (clientTransport.isPresent()) {
            ClientTransport existingEntity = clientTransport.get();
            ModelMapper mapper = new ModelMapper();
            mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
            mapper.addMappings(
                    new PropertyMap<UpdateClientTransportRequest, ClientTransport>() {
                        @Override
                        protected void configure() {
                            skip(destination.getClient());
                        }
                    });
            mapper.map(request, existingEntity);
            existingEntity.setCarRegExpiry(
                    LocalDate.parse(
                            request.getCarRegExpiry(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            existingEntity.setComprehensiveInsurance(
                    YesNo.valueOf(request.getComprehensiveInsurance()));
            existingEntity.setOdCar(YesNo.valueOf(request.getOdCar()));
            existingEntity.setIsTravelProtocol(YesNo.valueOf(request.getIsTravelProtocol()));
            existingEntity.setDeleted(request.getDeleted());
            existingEntity.setCappedKMs(request.getCappedKMs());
            existingEntity.setRoadSideAssistanceCovered(
                    YesNo.valueOf(request.getRoadSideAssistanceCovered()));
            clientTransportRepository.save(existingEntity);
            log.info("Updated the client transport for the given id {}", id);
        } else {
            String errorMessage = String.format("Cannot get a client transport for the id %d", id);
            log.error(errorMessage);
            throw new ClientTransportServiceException(errorMessage);
        }
    }
}
