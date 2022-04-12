package com.onedoorway.project.services;

import com.onedoorway.project.dto.ClientReportDTO;
import com.onedoorway.project.dto.ToggleReportRequest;
import com.onedoorway.project.exception.ClientReportServiceException;
import com.onedoorway.project.model.Client;
import com.onedoorway.project.model.ClientReport;
import com.onedoorway.project.model.Lookup;
import com.onedoorway.project.repository.ClientReportRepository;
import com.onedoorway.project.repository.ClientRepository;
import com.onedoorway.project.repository.LookupRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class ClientReportService {
    private final ClientRepository clientRepository;
    private final ClientReportRepository clientReportRepository;
    private final LookupRepository lookupRepository;

    @Autowired
    public ClientReportService(
            ClientRepository clientRepository,
            ClientReportRepository clientReportRepository,
            LookupRepository lookupRepository) {
        this.clientRepository = clientRepository;
        this.clientReportRepository = clientReportRepository;
        this.lookupRepository = lookupRepository;
    }

    public void toggleReport(ToggleReportRequest request) throws ClientReportServiceException {
        Optional<Client> client = clientRepository.findById(request.getClientId());
        if (client.isEmpty()) {
            log.error("Invalid client id sent to toggle report, {}", request.getClientId());
            throw new ClientReportServiceException("Invalid client id sent to toggle report");
        }
        Optional<ClientReport> clientReport =
                clientReportRepository.findByLookup_IdAndClient_Id(
                        request.getLookupId(), request.getClientId());
        if (clientReport.isPresent()) {
            ClientReport report = clientReport.get();
            report.setToggle(request.getToggle());
            clientReportRepository.save(report);
            log.info(
                    "Updated the report {} to toggle {} for client {}",
                    report.getId(),
                    report.getToggle(),
                    request.getClientId());
        } else {
            Optional<Lookup> lookup = lookupRepository.findById(request.getLookupId());
            if (lookup.isEmpty()) {
                log.error("Invalid lookup id sent to toggle report, {}", request.getLookupId());
                throw new ClientReportServiceException("Invalid lookup id sent to toggle report");
            }
            ClientReport report =
                    ClientReport.builder()
                            .lookup(lookup.get())
                            .client(client.get())
                            .toggle(request.getToggle())
                            .build();
            clientReportRepository.save(report);

            log.info(
                    "Saved the report {} to toggle {} for client {} with lookup {}",
                    report.getId(),
                    request.getToggle(),
                    request.getClientId(),
                    request.getLookupId());
        }
    }

    public List<ClientReportDTO> getClientReportById(long clientId) {
        log.info("Getting report for clientId {}", clientId);
        List<ClientReportDTO> res =
                clientReportRepository.findAllByClient_Id(clientId).stream()
                        .filter(clientReport -> clientReport.getToggle().equals(true))
                        .map(item -> new ModelMapper().map(item, ClientReportDTO.class))
                        .collect(Collectors.toList());
        log.info("Fetched {} client reports for client {}", res.size(), clientId);
        return res;
    }
}
