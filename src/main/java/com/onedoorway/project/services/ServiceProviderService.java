package com.onedoorway.project.services;

import com.onedoorway.project.Context;
import com.onedoorway.project.dto.ServiceProviderDTO;
import com.onedoorway.project.dto.ServiceProviderRequest;
import com.onedoorway.project.dto.UpdateServiceProviderRequest;
import com.onedoorway.project.exception.ClientServiceException;
import com.onedoorway.project.exception.ServiceProviderException;
import com.onedoorway.project.model.Client;
import com.onedoorway.project.model.ServiceProvider;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.ServiceProviderRepository;
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
public class ServiceProviderService {
    private final ServiceProviderRepository serviceProviderRepository;
    private final ClientRepository clientRepository;
    private final Context context;

    @Autowired
    public ServiceProviderService(
            ServiceProviderRepository serviceProviderRepository,
            ClientRepository clientRepository,
            Context context) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.clientRepository = clientRepository;
        this.context = context;
    }

    public void createServiceProvider(ServiceProviderRequest request)
            throws ServiceProviderException {
        log.info("Creating service provider for client id {}", request.getClientId());
        Optional<Client> client = clientRepository.findById(request.getClientId());
        if (client.isEmpty()) {
            log.error("Client not found for requested clientId {}", request.getClientId());
            throw new ServiceProviderException("Client not found");
        }
        ServiceProvider serviceProvider =
                ServiceProvider.builder()
                        .client(client.get())
                        .name(request.getName())
                        .service(request.getService())
                        .deleted(false)
                        .phone(request.getPhone())
                        .email(request.getEmail())
                        .lastUpdatedBy(request.getLastUpdatedBy())
                        .build();
        serviceProviderRepository.save(serviceProvider);

        log.info("Created service provider with id {}", serviceProvider.getId());
    }

    public List<ServiceProviderDTO> getServiceProviderById(long clientId)
            throws ServiceProviderException, ClientServiceException {
        ModelMapper modelMapper = new ModelMapper();
        Optional<Client> client = clientRepository.findById(clientId);
        if (client.isEmpty()) {
            log.info("client not found {}", clientId);
            throw new ClientServiceException("Client not found " + clientId);
        }
        List<ServiceProvider> serviceProvider =
                serviceProviderRepository.findByClient_IdAndDeleted(clientId, false);
        if (serviceProvider == null) {
            String errorMessage =
                    String.format("Cannot get an service provider for the client {}", clientId);
            log.error(errorMessage);
        }
        return serviceProvider.stream()
                .map(item -> modelMapper.map(item, ServiceProviderDTO.class))
                .collect(Collectors.toList());
    }

    public void updateServiceProvider(Long id, UpdateServiceProviderRequest request)
            throws ServiceProviderException {
        log.info("Updating the service provider with id {}", id);
        Optional<ServiceProvider> serviceProvider = serviceProviderRepository.findById(id);
        if (serviceProvider.isEmpty()) {
            String errorMessage = String.format("Cannot get a service provider for the id %d", id);
            log.error(errorMessage);
            throw new ServiceProviderException(errorMessage);
        }

        ServiceProvider existingEntity = serviceProvider.get();
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
        serviceProviderRepository.save(existingEntity);
        log.info("Updated the service provider for the given id {}", id);
    }
}
