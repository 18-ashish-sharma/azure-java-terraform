package com.onedoorway.project.services;

import com.onedoorway.project.dto.PowerOfAttorneyDTO;
import com.onedoorway.project.dto.PowerOfAttorneyRequest;
import com.onedoorway.project.dto.UpdatePowerOfAttorneyRequest;
import com.onedoorway.project.dto.UpdateServiceProviderRequest;
import com.onedoorway.project.exception.ClientServiceException;
import com.onedoorway.project.exception.PowerOfAttorneyServiceException;
import com.onedoorway.project.model.*;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.PowerOFAttorneyRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class PowerOfAttorneyService {

    private final ClientRepository clientRepository;
    private final PowerOFAttorneyRepository powerOFAttorneyRepository;

    @Autowired
    public PowerOfAttorneyService(
            ClientRepository clientRepository,
            PowerOFAttorneyRepository powerOFAttorneyRepository) {
        this.clientRepository = clientRepository;
        this.powerOFAttorneyRepository = powerOFAttorneyRepository;
    }

    public void createPowerOfAttorney(PowerOfAttorneyRequest request)
            throws PowerOfAttorneyServiceException {

        Optional<Client> client = clientRepository.findById(request.getClientId());
        if (client.isEmpty()) {
            log.error("Client not found for requested clientId {}", request.getClientId());
            throw new PowerOfAttorneyServiceException("Client not found with client id");
        }

        PowerOfAttorney powerOfAttorney =
                PowerOfAttorney.builder()
                        .id(request.getId())
                        .client(client.get())
                        .type(request.getType())
                        .name(request.getName())
                        .phone(request.getPhone())
                        .email(request.getEmail())
                        .address1(request.getAddress1())
                        .address2(request.getAddress2())
                        .city(request.getCity())
                        .state(request.getState())
                        .postCode(request.getPostCode())
                        .deleted(false)
                        .lastUpdatedBy(request.getLastUpdatedBy())
                        .build();

        powerOFAttorneyRepository.save(powerOfAttorney);

        log.info("Created Power Of Attorney  With Id {}", powerOfAttorney.getId());
    }

    public List<PowerOfAttorneyDTO> listPowerOfAttorney(long clientId)
            throws PowerOfAttorneyServiceException, ClientServiceException {
        ModelMapper modelMapper = new ModelMapper();
        Optional<Client> client = clientRepository.findById(clientId);
        if (client.isEmpty()) {
            log.info("client not found {}", clientId);
            throw new ClientServiceException("Client not found " + clientId);
        }
        List<PowerOfAttorney> powerOfAttorney =
                powerOFAttorneyRepository.findByClient_IdAndDeleted(clientId, false);
        if (powerOfAttorney == null) {
            String errorMessage =
                    String.format("Cannot get a power of attorney for the client {}", clientId);
            log.error(errorMessage);
        }
        return powerOfAttorney.stream()
                .map(item -> modelMapper.map(item, PowerOfAttorneyDTO.class))
                .collect(Collectors.toList());
    }

    public void updatePowerOfAttorney(Long id, UpdatePowerOfAttorneyRequest request)
            throws PowerOfAttorneyServiceException {
        log.info("Updating the power of attorney with id {}", id);
        Optional<PowerOfAttorney> powerOfAttorney = powerOFAttorneyRepository.findById(id);
        if (powerOfAttorney.isEmpty()) {
            String errorMessage = String.format("Cannot get a power of attorney for the id %d", id);
            log.error(errorMessage);
            throw new PowerOfAttorneyServiceException(errorMessage);
        }

        PowerOfAttorney existingEntity = powerOfAttorney.get();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        mapper.addMappings(
                new PropertyMap<UpdateServiceProviderRequest, ServiceProvider>() {
                    @Override
                    protected void configure() {
                        skip(destination.getClient());
                    }
                });
        mapper.map(request, existingEntity);
        existingEntity.setDeleted(request.getDeleted());
        powerOFAttorneyRepository.save(existingEntity);
        log.info("Updated the power of attorney for the given id {}", id);
    }
}
