package com.onedoorway.project.services;

import com.onedoorway.project.dto.*;
import com.onedoorway.project.dto.ClientAllowancesRequest;
import com.onedoorway.project.exception.ClientAllowancesServiceException;
import com.onedoorway.project.exception.ClientServiceException;
import com.onedoorway.project.model.Client;
import com.onedoorway.project.model.ClientAllowances;
import com.onedoorway.project.repository.ClientAllowancesRepository;
import com.onedoorway.project.repository.ClientRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class ClientAllowancesService {
    private final ClientAllowancesRepository clientAllowancesRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public ClientAllowancesService(
            ClientAllowancesRepository clientAllowancesRepository,
            ClientRepository clientRepository) {
        this.clientAllowancesRepository = clientAllowancesRepository;
        this.clientRepository = clientRepository;
    }

    public void createClientAllowances(ClientAllowancesRequest request)
            throws ClientAllowancesServiceException {
        Optional<Client> client = clientRepository.findById(request.getClientId());
        if (client.isEmpty()) {
            log.info("client not found with clientId{}", request.getClientId());
            throw new ClientAllowancesServiceException("Client not found");
        }

        ClientAllowances clientAllowances =
                ClientAllowances.builder()
                        .client(client.get())
                        .cappedKMs(request.getCappedKMs())
                        .concessionCard(request.getConcessionCard())
                        .kms(request.getKms())
                        .grocerySpend(request.getGrocerySpend())
                        .budgetlyCardNo(request.getBudgetlyCardNo())
                        .deleted(false)
                        .lastUploadedBy(request.getLastUploadedBy())
                        .build();
        clientAllowancesRepository.save(clientAllowances);
        log.info("Created the client with allowances {} ", clientAllowances.getId());
    }

    public List<ClientAllowancesDTO> getClientAllowancesById(long clientId)
            throws ClientAllowancesServiceException, ClientServiceException {
        ModelMapper modelMapper = new ModelMapper();
        Optional<Client> client = clientRepository.findById(clientId);
        if (client.isEmpty()) {
            log.info("client not found {}", clientId);
            throw new ClientServiceException("Client not found " + clientId);
        }
        List<ClientAllowances> clientAllowances =
                clientAllowancesRepository.findByClient_IdAndDeleted(clientId, false);
        if (clientAllowances == null) {
            String errorMessage =
                    String.format("Cannot get an client allowances for the client {}", clientId);
            log.error(errorMessage);
        }
        return clientAllowances.stream()
                .map(item -> modelMapper.map(item, ClientAllowancesDTO.class))
                .collect(Collectors.toList());
    }

    public void updateClientAllowances(Long id, UpdateClientAllowancesRequest request)
            throws ClientAllowancesServiceException {
        log.info("Updating the client allowances with id {}", id);
        Optional<ClientAllowances> clientAllowances = clientAllowancesRepository.findById(id);
        if (clientAllowances.isEmpty()) {
            String errorMessage = String.format("Cannot get a client allowances for the id %d", id);
            log.error(errorMessage);
            throw new ClientAllowancesServiceException(errorMessage);
        }

        ClientAllowances existingEntity = clientAllowances.get();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        mapper.addMappings(
                new PropertyMap<UpdateClientAllowancesRequest, ClientAllowances>() {
                    @Override
                    protected void configure() {
                        skip(destination.getClient());
                    }
                });
        mapper.map(request, existingEntity);
        existingEntity.setDeleted(request.getDeleted());
        clientAllowancesRepository.save(existingEntity);
        log.info("Updated the client allowances for the given id {}", id);
    }
}
